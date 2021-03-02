<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:msml="http://grogra.de/msml"
	xmlns:x3d="http://www.web3d.org/specifications"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xdt="http://www.w3.org/2005/xpath-datatypes"
	xmlns:g="http://grogra.de/msml/datatypes/groimp"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="#all" version="2.0">
	<xsl:output method="xml" indent="yes" encoding="utf-8" version="1.0" omit-xml-declaration="yes"/>

	<xsl:template match="/msml:msml">
	<xsl:message><xsl:value-of select="system-property('xsl:vendor')"/></xsl:message>
		<X3D profile="Interactive" version="3.0">
			<Scene>
				<xsl:apply-templates
					select="msml:library|msml:group|msml:msobject" />
			</Scene>
		</X3D>
	</xsl:template>

	<xsl:template match="msml:library">
		<xsl:for-each select="./msml:data/g:Appearance">
			<xsl:apply-templates select="."/>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="msml:group">
		<xsl:variable name="groupcontent">
			<xsl:apply-templates select="msml:msobject|msml:group" />
		</xsl:variable>
		<Group>
			<xsl:choose>
				<xsl:when test="./msml:data/g:Transform">
					<xsl:apply-templates select="./msml:data/g:Transform">
						<xsl:with-param name="innercontent"	select="$groupcontent"/>
					</xsl:apply-templates>
				</xsl:when>	
				<xsl:otherwise>
					<xsl:copy-of select="$groupcontent"/>
				</xsl:otherwise>
			</xsl:choose>
		</Group>
	</xsl:template>

	<xsl:template match="msml:msobject" >
		<Switch>
			<xsl:choose>
				<xsl:when test="@showScale">
					<xsl:variable name="showScale" select="@showScale"/>
					<xsl:for-each select="msml:scale">
						<xsl:if test="@name=$showScale">
							<xsl:attribute name="whichChoice"><xsl:value-of select="position()-1"/></xsl:attribute>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<!-- wähle beliebige (erste) Skale aus -->
					<xsl:attribute name="whichChoice">0</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:for-each select="msml:scale">
				<xsl:variable name="scalecontent">
					<xsl:apply-templates select="." />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="./msml:data/g:Transform">				
						<xsl:apply-templates select="./msml:data/g:Transform">
							<xsl:with-param name="innercontent"	select="$scalecontent"/>
						</xsl:apply-templates>				
					</xsl:when>	
					<xsl:otherwise>
						<Transform>
							<xsl:apply-templates select="$scalecontent"/>
						</Transform>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</Switch>
	</xsl:template>
	
	<xsl:template match="msml:scale">
		<xsl:param name="formerendpos" select="0"/>
	<!-- finde alle Knoten, die nicht Endpunkt einer Kante sind -->
	<!-- für jeden Knoten: prüfe jede Kante, ob ihr target-Attribut die ID des Knoten hat. 
	Wenn ja, dann Abbruch
	Wenn nein, dann ist dieser Knoten eine Wurzel
	folge dieser Wurzel -->
		<xsl:variable name="edgetargets" as="xs:string*">
			<xsl:sequence select="distinct-values(./msml:edge/@target)"/>
		</xsl:variable>
		<xsl:variable name="roots" as="item()*">
			<xsl:sequence select="./(msml:node|msml:msobject)[not(@id=$edgetargets)]"/>
		</xsl:variable>
	<!-- <xsl:message>roots:<xsl:value-of select="$roots/@id" separator=","/>ENDE</xsl:message>  -->	
		<xsl:for-each select="$roots">
			<xsl:call-template name="traverse">
				<xsl:with-param name="node" select="current()"/>
				<xsl:with-param name="formerendpos" select="$formerendpos"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="traverse">
	 	<xsl:param name="node"/>
	 	<xsl:param name="formerendpos" select="0"/>
		<xsl:choose>
			<xsl:when test="string(node-name($node))='node'">
				<xsl:call-template name="node">
					<xsl:with-param name="node" select="$node"/>
					<xsl:with-param name="formerendpos" select="$formerendpos"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="string(node-name($node))='msobject'">
				<xsl:call-template name="msnode">
					<xsl:with-param name="node" select="$node"/>
					<xsl:with-param name="formerendpos" select="$formerendpos"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="msnode">
	 	<xsl:param name="node"/>
	 	<xsl:param name="formerendpos" select="0"/>
	 	<Transform>
		 	<Switch>
				<xsl:choose>
					<xsl:when test="$node/@showScale">
						<xsl:variable name="showScale" select="$node/@showScale"/>
						<xsl:for-each select="$node/msml:scale">
							<xsl:if test="@name=$showScale">
								<xsl:attribute name="whichChoice"><xsl:value-of select="position()-1"/></xsl:attribute>
							</xsl:if>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<!-- wähle beliebige (erste) Skale aus -->
						<xsl:attribute name="whichChoice">0</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:for-each select="$node/msml:scale">
					<xsl:variable name="scalecontent">
						<xsl:apply-templates select=".">
							<xsl:with-param name="formerendpos" select="$formerendpos"/> 
						</xsl:apply-templates>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="./msml:data/g:Transform">				
							<xsl:apply-templates select="./msml:data/g:Transform">
								<xsl:with-param name="innercontent"	select="$scalecontent"/>
							</xsl:apply-templates>				
						</xsl:when>	
						<xsl:otherwise>
							<Transform>
								<xsl:apply-templates select="$scalecontent"/>
							</Transform>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</Switch>
		</Transform>
	</xsl:template>

	<xsl:template name="node">
		<xsl:param name="node"/>
		<xsl:param name="formerendpos" select="0"/>
		<xsl:variable name="datacontent">
			<xsl:if test="$node/msml:data/g:Shape">
				<Shape>
					<xsl:apply-templates
						select="$node/msml:data/g:Appearance" />
					<xsl:apply-templates
						select="$node/msml:data/g:Shape/*" />
				</Shape>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="length">
			<xsl:choose>
				<xsl:when test="$node/msml:data/g:Shape/(x3d:Cone|x3d:Cylinder)">
					<xsl:choose>
						<xsl:when test="$node/msml:data/g:Shape/*/@height">
							<xsl:value-of select="$node/msml:data/g:Shape/*/@height"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="2"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$node/msml:data/g:Shape/x3d:Sphere">
					<xsl:choose>
						<xsl:when test="$node/msml:data/g:Shape/x3d:Sphere/@radius">
							<xsl:value-of select="$node/msml:data/g:Shape/x3d:Sphere/@radius"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="1"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$node/msml:data/g:Shape/x3d:Box">
					<xsl:choose>
						<xsl:when test="$node/msml:data/g:Shape/x3d:Box/@size">
							<xsl:value-of select="(tokenize($node/msml:data/g:Shape/x3d:Box/@size,'\s+'))[2]"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="2"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="startpos">
			<xsl:choose>
				<xsl:when test="$node/msml:data/g:Shape/@startpos">
					<xsl:value-of select="$node/msml:data/g:Shape/@startpos"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="-0.5"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="endpos">
			<xsl:choose>
				<xsl:when test="$node/msml:data/g:Shape/@endpos">
					<xsl:value-of select="$node/msml:data/g:Shape/@endpos"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="transstartpos" select="(0.5+$startpos)*$length"/>
		<xsl:variable name="transendpos" select="-($transstartpos)+($endpos*$length)"/>		
		<xsl:variable name="content">
			<xsl:choose>
				<xsl:when test="$node/msml:data/g:Transform">
					<xsl:apply-templates
						select="$node/msml:data/g:Transform">
						<xsl:with-param name="innercontent"	select="$datacontent" />
						<xsl:with-param name="node"	select="$node" />
						<xsl:with-param name="formerendpos" select="$transendpos"/>
						<xsl:with-param name="centerstartpos" select="-$transstartpos"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<Transform>
						<xsl:copy-of select="$datacontent" />
						<xsl:for-each select="//msml:edge[@source=$node/@id][(@type='branch')or(@type='successor')]">
							<xsl:variable name="edge" select="."/>
							<xsl:call-template name="traverse">
								<xsl:with-param name="node" select="//(msml:node|msml:msobject)[@id=$edge/@target]"/>
								<xsl:with-param name="formerendpos" select="$transendpos"/>
							</xsl:call-template>
						</xsl:for-each>
					</Transform>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="content2">
			<xsl:choose>
				<xsl:when test="$transstartpos=0">
					<xsl:choose>
						<xsl:when test="$content/Transform">
							<xsl:copy-of select="$content"/>
						</xsl:when>
						<xsl:otherwise>
							<Transform>
								<xsl:copy-of select="$content"/>
							</Transform>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<Transform translation="0 {$transstartpos} 0">
						<xsl:copy-of select="$content"/>
					</Transform>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$formerendpos=0">
				<xsl:copy-of select="$content2"/>
			</xsl:when>
			<xsl:otherwise>
				<Transform translation="0 {$formerendpos} 0">
					<xsl:copy-of select="$content2"/>
				</Transform>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="g:Appearance">
		<xsl:choose>
			<xsl:when test="@DEF">
				<Appearance>
					<xsl:copy-of select="@*"/>
					<xsl:apply-templates select="./x3d:Appearance/*"/>
				</Appearance>
			</xsl:when>
			<xsl:when test="@USE">
				<Appearance>
					<xsl:copy-of select="@*"/>
				</Appearance>
			</xsl:when>
			<xsl:otherwise>
				<Appearance>
					<xsl:copy-of select="@*"/>
					<xsl:apply-templates select="./x3d:Appearance/*"/>
				</Appearance>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	
	
	<xsl:template match="g:Transform">
		<xsl:param name="innercontent" />
		<xsl:param name="node"/>
		<xsl:param name="formerendpos" select="0"/>
		<xsl:param name="centerstartpos" select="0"/>
		<xsl:apply-templates select="x3d:Transform">
			<xsl:with-param name="innercontent" select="$innercontent" />
			<xsl:with-param name="node"	select="$node" />
			<xsl:with-param name="formerendpos" select="$formerendpos"/>
			<xsl:with-param name="centerstartpos" select="$centerstartpos"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="x3d:Transform">
		<xsl:param name="innercontent" />
		<xsl:param name="node"/>
		<xsl:param name="formerendpos" select="0"/>
		<xsl:param name="centerstartpos" select="0"/>
		<Transform>
			<xsl:if test="not($centerstartpos=0)">
				<xsl:attribute name="center">0 <xsl:value-of select="$centerstartpos"/> 0</xsl:attribute>
			</xsl:if>
			<xsl:copy-of select="@*" />
			<xsl:choose>
				<xsl:when test="not(exists(./x3d:Transform))"> 
					<xsl:copy-of select="$innercontent" />
					<xsl:if test="$node">
						<xsl:for-each select="//msml:edge[@source=$node/@id][(@type='branch')or(@type='successor')]">
							<xsl:variable name="edge" select="."/>
							<xsl:call-template name="traverse">
								<xsl:with-param name="node" select="//(msml:node|msml:msobject)[@id=$edge/@target]"/>
								<xsl:with-param name="formerendpos" select="$formerendpos"/>
							</xsl:call-template>
						</xsl:for-each>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="x3d:Transform">
						<xsl:with-param name="innercontent"	select="$innercontent" />
						<xsl:with-param name="node"	select="$node" />
						<xsl:with-param name="formerendpos" select="$formerendpos"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</Transform>
	</xsl:template>

	<xsl:template match="*">
		<xsl:element name="{local-name()}">
			<xsl:copy-of select="@*" />
			<xsl:apply-templates select="*" />
		</xsl:element>
	</xsl:template>
</xsl:transform>