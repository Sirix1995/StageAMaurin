<xsl:transform
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   version="2.0"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:mtg2msml="http://grogra.de/msml/mtg"
   exclude-result-prefixes="xs mtg2msml">
<!-- Parse a MTG file and create a xml representation of the data. --> 
<xsl:output method="xml" indent="yes" encoding="utf-8" version="1.0"/>

<!-- The $input parameter holds the name of the input MTG file, relative to the stylesheet.
	 If no input-parameter is specified, the file in the select attribute is choosen. -->
<xsl:param name="input-mtg-file" select="'examples/mtg/ei1.mtg'" as="xs:string"/>
<!--  required="yes" -->

<!-- Read the input file into a string variable -->
<xsl:variable name="input-text" as="xs:string" 
                     select="unparsed-text($input-mtg-file, 'iso-8859-1')"/>

<!-- Remove comments
	A comment in a mtg-file starts with a # ,
	is followed by the text of the comment
	and ends with a line break or a tab. -->                     
<xsl:variable name="input-text_withoutComments" as="xs:string" >
	<xsl:value-of select="tokenize($input-text,'#[^(\t|\r?\n)]*')" separator=""/>
</xsl:variable>		
                    
<!-- Split the input file into a sequence of sections, each holding one section of the input -->
<xsl:variable name="sections" as="xs:string*" select="tokenize($input-text_withoutComments,'(CODE)|(CLASSES)|(DESCRIPTION)|(FEATURES)|(MTG)')"/>

<xsl:variable name="mtg_datatypes"
	select="('INT','REAL','STRING',
			'DD/MM','DD/MM/YY','MM/YY',
			'DD/MM-TIME','DD/MM/YY-TIME',
			'GEOMETRY','APPEARANCE','OBJECT')" as="xs:string+"/>

<xsl:variable name="mtg_datatypes_regex_str">
	<xsl:text>(</xsl:text>
	<xsl:value-of select="$mtg_datatypes" separator=")|("/>
	<xsl:text>)</xsl:text>
</xsl:variable>
 	  
<xsl:variable name="mtg_attributes" as="xs:string*">
	<xsl:variable name="section" select="$sections[5]"/>
	<xsl:variable name="header" select="'(NAME)\t(TYPE)'"/>
	<xsl:analyze-string select="$section" regex="{$header}"> 
		<xsl:matching-substring>
			<xsl:variable name="data" as="xs:string*" select="tokenize($section,concat('.*',$header,'\r?\n'))"/>			
			<xsl:variable name="lines" as="xs:string*" select="tokenize($data[2],'\r?\n')"/>
			<xsl:for-each select="$lines">
				<xsl:analyze-string select="." 
					regex="^([A-Za-z0-9]+)\t({$mtg_datatypes_regex_str})(\t(.+))?$"> 
		      		<xsl:matching-substring>
		      			<xsl:value-of select="regex-group(1)"/>
					</xsl:matching-substring>
				</xsl:analyze-string>
			</xsl:for-each>
		</xsl:matching-substring>
	 </xsl:analyze-string>	
</xsl:variable>
  
<xsl:template name="code">
	<xsl:param name="section"/>
	<xsl:element name="code">
		<xsl:analyze-string select="$section" regex=".*\t(FORM-(A|B))(.|(\r?\n))*"> 
			<xsl:matching-substring>
				<xsl:attribute name="form" select="regex-group(2)"/>
			</xsl:matching-substring>
			<xsl:non-matching-substring>
				<xsl:message terminate="yes">Unknown Header: "<xsl:value-of select="."/>"</xsl:message>
			</xsl:non-matching-substring>
		 </xsl:analyze-string>
	</xsl:element>
</xsl:template>

<xsl:template name="classes">
	<xsl:param name="section"/>
	<xsl:variable name="header" select="'(SYMBOL)\t(SCALE)\t(DECOMPOSITION)\t(INDEXATION)\t(DEFINITION)'"/>
	<xsl:analyze-string select="$section" regex="{$header}"> 
		<xsl:matching-substring>
			<xsl:variable name="data" as="xs:string*" select="tokenize($section,concat('.*',$header,'\r?\n'))"/>			
			<xsl:variable name="lines" as="xs:string*" select="tokenize($data[2],'\r?\n')"/>
			<xsl:element name="classes">
			<xsl:for-each select="$lines">
				<xsl:analyze-string select="." 
     regex="^([\$A-Za-z])\t([0-9]+)\t((CONNECTED)|(LINEAR)|(&lt;-LINEAR)|(\+-LINEAR)|(FREE)|(NONE))\t(FREE)\t((IMPLICIT)|(EXPLICIT))$"> 
		      		<xsl:matching-substring>
				        <class 	symbol="{regex-group(1)}"
				        		scale="{regex-group(2)}"
				              	decomposition="{regex-group(3)}"
				              	indexation="{regex-group(10)}"
				              	definition="{regex-group(11)}"/>
					</xsl:matching-substring>
		      		<xsl:non-matching-substring>
		        		<xsl:message terminate="yes">Non-matching line "<xsl:value-of select="."/>"</xsl:message>
		      		</xsl:non-matching-substring>
				</xsl:analyze-string>
			</xsl:for-each>
			</xsl:element>
		</xsl:matching-substring>
	 </xsl:analyze-string>	
</xsl:template>

<xsl:template name="description">
	<xsl:param name="section"/>
	<xsl:variable name="header" select="'(LEFT)\t(RIGHT)\t(RELTYPE)\t(MAX)'"/>
	<xsl:analyze-string select="$section" regex="{$header}"> 
		<xsl:matching-substring>
			<xsl:variable name="data" as="xs:string*" select="tokenize($section,concat('.*',$header,'\r?\n'))"/>			
			<xsl:variable name="lines" as="xs:string*" select="tokenize($data[2],'\r?\n')"/>
			<xsl:element name="description">
			<xsl:for-each select="$lines">
				<xsl:analyze-string select="." 
					regex="^([A-Za-z])\t([A-Za-z](,[A-Za-z])*)\t(\+|&lt;)\t([0-9]+|\?)$"> 
		      		<xsl:matching-substring>
				        <xsl:choose>
				        	<xsl:when test="contains(regex-group(2),',')">
				        		<xsl:variable name="classes" as="xs:string*" select="tokenize(regex-group(2),',')"/>
				        		<xsl:for-each select="$classes">
				        		<rule   left="{regex-group(1)}"
						        		right="{current()}"
						        		reltype="{regex-group(4)}"
						        		max="{regex-group(5)}"/>
						        </xsl:for-each>
				        	</xsl:when>
				        	<xsl:otherwise>
						        <rule   left="{regex-group(1)}"
						        		right="{regex-group(2)}"
						        		reltype="{regex-group(4)}"
						        		max="{regex-group(5)}"/>
				        	</xsl:otherwise>
				        </xsl:choose>
					</xsl:matching-substring>
		      		<xsl:non-matching-substring>
		        		<xsl:message terminate="yes">Non-matching line "<xsl:value-of select="."/>"</xsl:message>
		      		</xsl:non-matching-substring>
				</xsl:analyze-string>
			</xsl:for-each>
			</xsl:element>
		</xsl:matching-substring>
	 </xsl:analyze-string>	
</xsl:template>

<xsl:template name="features">
	<xsl:param name="section"/>
	<xsl:variable name="header" select="'(NAME)\t(TYPE)'"/>
	<xsl:analyze-string select="$section" regex="{$header}"> 
		<xsl:matching-substring>
			<xsl:variable name="data" as="xs:string*" select="tokenize($section,concat('.*',$header,'\r?\n'))"/>			
			<xsl:variable name="lines" as="xs:string*" select="tokenize($data[2],'\r?\n')"/>
			<xsl:element name="features">
			<xsl:for-each select="$lines">
				<xsl:analyze-string select="." 
					regex="^([A-Za-z0-9]+)\t({$mtg_datatypes_regex_str})(\t(.+))?$"> 
		      		<xsl:matching-substring>
		      			<xsl:element name="attribute">
		      				<xsl:attribute name="name" select="regex-group(1)"/>
		      				<xsl:attribute name="type" select="regex-group(2)"/>
				        	<xsl:if test="regex-group(2+count($mtg_datatypes)+2)!=''">
		        				 <xsl:attribute name="file" select="regex-group(2+count($mtg_datatypes)+2)"/>
					        </xsl:if>
				        </xsl:element>
					</xsl:matching-substring>
		      		<xsl:non-matching-substring>
		        		<xsl:message terminate="yes">Non-matching line "<xsl:value-of select="."/>". Check for using actual MTG-Datatypes, for instance data type ALPHA is replaced by datatype STRING</xsl:message>
		      		</xsl:non-matching-substring>
				</xsl:analyze-string>
			</xsl:for-each>
			</xsl:element>
		</xsl:matching-substring>
	 </xsl:analyze-string>	
</xsl:template>

<xsl:template name="mtg">
	<xsl:param name="section"/>
	<xsl:variable name="header" select="'(TOPO)\t*(\t(.+))?'"/>
 	<xsl:analyze-string select="$section" regex="{$header}"> 
		<xsl:matching-substring>
			<xsl:variable name="data" as="xs:string*" select="tokenize($section,concat('.*',$header,'\r?\n'))"/>			
			<!-- count the number of headercolumns -->
			<!-- find headerrow -->
			<xsl:variable name="rows" as="xs:string*" select="tokenize($section,'\r?\n')"/>
			<xsl:variable name="headerrow" as="xs:string">
				<xsl:for-each select="$rows">
					<xsl:if test="contains(current(),'TOPO')">
						<xsl:value-of select="."/>
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<xsl:variable name="headercolumns" as="xs:string*" select="tokenize($headerrow,'\t')"/>
			<xsl:variable name="num_headercolumns" select="count($headercolumns)"/>
			<xsl:variable name="num_attributecolumns" select="count($mtg_attributes)"/>
			<xsl:variable name="lines" as="xs:string*" select="tokenize($data[2],'\r?\n')"/>
			<xsl:variable name="num_topocolumns">
				<xsl:choose>
					<xsl:when test="$num_attributecolumns eq 0">
						<xsl:value-of select="max(for $i in $lines return count(tokenize($i,'\t')))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$num_headercolumns - $num_attributecolumns"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="temptree">
				<xsl:for-each select="$lines">
					<xsl:variable name="lineelems" as="xs:string*" select="tokenize(current(),'\t')"/>
					<xsl:if test="exists($lineelems)">
						<xsl:element name="rule">
							<xsl:attribute name="number" select="position()"/>
							<xsl:for-each select="subsequence($lineelems,1,$num_topocolumns)"> 
								<xsl:if test="current()!=''">
									<xsl:attribute name="level" select="position()"/>
									<xsl:attribute name="content">
										<xsl:call-template name="replaceRelationRangeExpressions">
											<xsl:with-param name="rule" select="current()"/>
										</xsl:call-template>
									</xsl:attribute>	
								</xsl:if>
							</xsl:for-each>
							<xsl:for-each select="subsequence($lineelems,$num_topocolumns + 1)">
								<xsl:if test="current()!=''">
									<xsl:element name="attribute">
										<xsl:attribute name="name" select="subsequence($headercolumns,$num_topocolumns + position(),1)"/>
										<xsl:attribute name="value" select="current()"/>
									</xsl:element>
								</xsl:if>
							</xsl:for-each>
						</xsl:element>
					</xsl:if>
				</xsl:for-each>	
			</xsl:variable>
			<xsl:variable name="temptree_withoutAliasnames">
				<xsl:for-each select="$temptree/rule">
					<xsl:call-template name="resolveAliasnames">
						<xsl:with-param name="allRulesTempTree" select="$temptree"/>
						<xsl:with-param name="rule" select="current()"/>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:variable>
			<xsl:element name="topo">
				<xsl:for-each select="$temptree_withoutAliasnames/rule">
					<xsl:call-template name="transformRelative2AbsoluteRule">
						<xsl:with-param name="allRulesTempTree" select="$temptree_withoutAliasnames"/>
						<xsl:with-param name="rule" select="current()"/>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:element>
		</xsl:matching-substring>
	 </xsl:analyze-string>
</xsl:template>

<!-- resolves relative rules with leading Tabs or ^ 
	 to absolute representations of these rules-->
<xsl:template name="transformRelative2AbsoluteRule">
	<xsl:param name="allRulesTempTree" as="item()+"/>
	<xsl:param name="rule" as="item()"/>
	<xsl:param name="temp_precedingRuleNumber" as="xs:integer" select="$rule/@number"/>
	<xsl:choose>
		<xsl:when test="contains($rule/@content,'^')">
			<xsl:variable name="newRule">
				<xsl:element name="rule">
					<xsl:attribute name="number" select="$rule/@number" />
					<xsl:attribute name="level" select="$rule/@level"/>
					<xsl:variable name="precedingRule" as="item()+">
						<xsl:perform-sort select="$allRulesTempTree/rule[number(@number) lt $temp_precedingRuleNumber][@level=$rule/@level]">
							<xsl:sort select="@number" data-type="number" order="descending"/>
						</xsl:perform-sort>
					</xsl:variable>
					<xsl:attribute name="temp_precedingRuleNumber" select="$precedingRule[1]/@number"/>
					<xsl:attribute name="content" select="concat($precedingRule[1]/@content,substring-after($rule/@content,'^'))"/>
					<xsl:copy-of select="$rule/attribute"/>
				</xsl:element>
			</xsl:variable>
			<xsl:call-template name="transformRelative2AbsoluteRule">
				<xsl:with-param name="allRulesTempTree" select="$allRulesTempTree"/>
				<xsl:with-param name="rule" select="$newRule/rule"/>
				<xsl:with-param name="temp_precedingRuleNumber" select="$newRule/rule/@temp_precedingRuleNumber"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:choose>
				<xsl:when test="number($rule/@level) gt 1">
					<xsl:variable name="newRule">
						<xsl:element name="rule">
							<xsl:variable name="newLevel" select="number($rule/@level) - 1"/>
							<xsl:attribute name="number" select="$rule/@number" />
							<xsl:attribute name="level" select="$newLevel"/>
							<xsl:variable name="precedingRule" as="item()+">
								<xsl:perform-sort select="$allRulesTempTree/rule[number(@number) lt $temp_precedingRuleNumber][@level=$newLevel]">
									<xsl:sort select="@number" data-type="number" order="descending"/>
								</xsl:perform-sort>
							</xsl:variable>
							<xsl:attribute name="temp_precedingRuleNumber" select="$precedingRule[1]/@number"/>
							<xsl:attribute name="content" select="concat($precedingRule[1]/@content,$rule/@content)"/>
							<xsl:copy-of select="$rule/attribute"/>
						</xsl:element>
					</xsl:variable>
					<xsl:call-template name="transformRelative2AbsoluteRule">
						<xsl:with-param name="allRulesTempTree" select="$allRulesTempTree"/>
						<xsl:with-param name="rule" select="$newRule/rule"/>
						<xsl:with-param name="temp_precedingRuleNumber" select="$newRule/rule/@temp_precedingRuleNumber"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<rule>
						<xsl:copy-of select="$rule/(*|((@*) except(@level|@temp_precedingRuleNumber)))"/>
					</rule>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- resolves Aliasnames inside a rule --> 
<xsl:template name="resolveAliasnames">
	<xsl:param name="allRulesTempTree" as="item()+"/>
	<xsl:param name="rule" as="item()"/>
	<xsl:variable name="regexpr" select="'^(.*)\((.+)\)(.*)$'"/>
	<xsl:analyze-string select="$rule/@content" regex="{$regexpr}">
		<xsl:matching-substring>
			<xsl:variable name="newRule">
	 			<xsl:element name="rule">
					<xsl:attribute name="number" select="$rule/@number" />
					<xsl:attribute name="level" select="$rule/@level"/>
					<xsl:variable name="aliasReplacement" select="$allRulesTempTree/rule[attribute/@name='Alias'][attribute/@value=regex-group(2)]/@content"/>
					<xsl:attribute name="content" select="concat(regex-group(1),$aliasReplacement,regex-group(3))"/>
					<xsl:copy-of select="$rule/attribute"/>
				</xsl:element>
			</xsl:variable>
			<xsl:call-template name="resolveAliasnames">
				<xsl:with-param name="allRulesTempTree" select="$allRulesTempTree"/>
				<xsl:with-param name="rule" select="$newRule/rule"/>
			</xsl:call-template>
		</xsl:matching-substring>
		<xsl:non-matching-substring>
			<xsl:copy-of select="$rule"/>
		</xsl:non-matching-substring>
	</xsl:analyze-string>
</xsl:template>

<!-- replaces Relations like U1++U4 with U1+U2+U3+U4 -->
<xsl:template name="replaceRelationRangeExpressions">
	<xsl:param name="rule"/>
	<xsl:variable name="regexpr" select="'^(.*)([a-zA-Z])([0-9]+)(&lt;&lt;|\+\+)([a-zA-Z])([0-9]+)(.*)$'"/>
	<xsl:analyze-string select="$rule" regex="{$regexpr}">
		<xsl:matching-substring>
 			<xsl:variable name="range">
 				<xsl:value-of select="for $i in ((regex-group(3) cast as xs:integer) to (regex-group(6) cast as xs:integer)) return concat(regex-group(2),$i)" separator="{substring(regex-group(4),1,string-length(regex-group(4))idiv 2)}"/>
 			</xsl:variable>
			<xsl:call-template name="replaceRelationRangeExpressions">
				<xsl:with-param name="rule" select="concat(regex-group(1),$range,regex-group(7))"/>
			</xsl:call-template>
		</xsl:matching-substring>
		<xsl:non-matching-substring>
			<xsl:value-of select="$rule"/>
		</xsl:non-matching-substring>
	</xsl:analyze-string>
</xsl:template>

<!-- Main entry point to the stylesheet -->
<xsl:template match="/">
	<xsl:element name="mtg">
		<!-- Header Section -->
		<xsl:call-template name="code">
			<xsl:with-param name="section" select="$sections[2]"/>
		</xsl:call-template> 
		<xsl:call-template name="classes">
			<xsl:with-param name="section" select="$sections[3]"/>
		</xsl:call-template>
		<xsl:call-template name="description">
			<xsl:with-param name="section" select="$sections[4]"/>
		</xsl:call-template> 
		<xsl:call-template name="features">
			<xsl:with-param name="section" select="$sections[5]"/>
		</xsl:call-template>
		<!-- Coding Section -->
		<xsl:call-template name="mtg">
			<xsl:with-param name="section" select="$sections[6]"/>
		</xsl:call-template>
	</xsl:element>
</xsl:template>
</xsl:transform>
