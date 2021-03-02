<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:msml="http://grogra.de/msml"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:mtg2msml="http://grogra.de/msml"
	xmlns:mtg="http://grogra.de/mtg"
	exclude-result-prefixes="#all" version="2.0">
	<xsl:output method="xml" indent="yes" encoding="utf-8" version="1.0"/>

<xsl:variable name="linebreak"><xsl:text>
</xsl:text>
</xsl:variable>

	<!-- this is the MAIN-Template, where the processing of the XML-representation
		 of the mtg-file created by parse-mtg.xslt begins -->
	<xsl:template match="/mtg">
		<xsl:variable name="tree" select="." as="item()*"/>
		<xsl:element name="msml:msml">
			<xsl:namespace name="mtg">http://grogra.de/mtg</xsl:namespace>
			<xsl:attribute name="version">1.0</xsl:attribute>
			<xsl:element name="msml:msobject">
				<xsl:attribute name="id">mso1</xsl:attribute>
				<xsl:attribute name="name">MTG-Object</xsl:attribute>
				<xsl:variable name="nodes" as="item()*">
					<xsl:variable name="tempnodes" as="item()*">
						<xsl:for-each select="./topo/rule">
							<xsl:sort select="@number" data-type="number"/>
							<xsl:variable name="rulecontent" select="@content" as="xs:string"/>
							<xsl:variable name="nodenames" as="xs:string*" select="tokenize($rulecontent,'/|\+|&lt;')"/>	
							<xsl:for-each select="$nodenames">
								<xsl:if test="current()!=''">
									<xsl:variable name="nodename" select="current()"/>
									<xsl:variable name="classname">
										<xsl:analyze-string select="$nodename" regex="^([A-Za-z])[0-9]+$">
											<xsl:matching-substring>
												<xsl:value-of select="regex-group(1)"/>
											</xsl:matching-substring>
										</xsl:analyze-string>
									</xsl:variable>
									<xsl:variable name="regexpr" as="xs:string*">
										<xsl:value-of select="subsequence($nodenames,2,position()-1)" separator="(/|\+|&lt;)"/>
									</xsl:variable>
									<xsl:analyze-string select="$rulecontent" regex="(/{$regexpr})">
										<xsl:matching-substring>
											<xsl:element name="msml:node">
												<xsl:attribute name="id" select="regex-group(1)"/>
												<xsl:attribute name="name" select="$nodename"/>
												<xsl:element name="msml:data">
													<xsl:element name="mtg:class">
														<xsl:attribute name="name" select="$classname"/>
													</xsl:element>
												</xsl:element>
											</xsl:element>
										</xsl:matching-substring>
									</xsl:analyze-string>
								</xsl:if>
							</xsl:for-each>
						</xsl:for-each>
					</xsl:variable>
					<xsl:for-each-group select="$tempnodes" group-by="@id">
						<xsl:element name="msml:node">
							<xsl:copy-of select="./@*"/>
							<xsl:variable name="attributes" select="$tree/topo/rule[@content=current-grouping-key()]/attribute"/>
							<xsl:element name="msml:data">
								<xsl:copy-of select="./msml:data/*"/>
								<xsl:if test="exists($attributes)">
									<!-- At this point can take place a conversion between mtg-attributes and
										 MSML-specific attributes. For example Length, TopDiameter and BottomDiameter attributes
										 can be used to create a x3d:Cylinder-Element. -->
									<xsl:for-each select="$attributes">
										<xsl:element name="mtg:attribute">
											<xsl:copy-of select="current()/@*"/>
											<xsl:attribute name="type" select="$tree/features/attribute[@name=current()/@name]/@type"/>
										</xsl:element>
									</xsl:for-each>
								</xsl:if>
							</xsl:element>
						</xsl:element>
					</xsl:for-each-group>
				</xsl:variable>
				<xsl:variable name="edges" as="item()*">
					<xsl:variable name="temp_edges" as="item()*">
					<xsl:for-each select="/mtg/description/rule">
						<xsl:variable name="currentDescriptionRule" select="current()"/>
						<xsl:for-each select="/mtg/topo/rule">
							<xsl:call-template name="createEdges">
								<xsl:with-param name="rule" select="current()/@content"/>
								<xsl:with-param name="left" select="$currentDescriptionRule/@left"/>
								<xsl:with-param name="right" select="$currentDescriptionRule/@right"/>
								<xsl:with-param name="reltype" select="$currentDescriptionRule/@reltype"/>
								<xsl:with-param name="scale" select="/mtg/classes/class[@symbol=$currentDescriptionRule/@left]/@scale"/>
							</xsl:call-template>
						</xsl:for-each>
					</xsl:for-each>
					</xsl:variable>
					<xsl:variable name="temp_edgesWithoutDuplicates" as="item()*">
						<xsl:for-each-group select="$temp_edges" group-by="@source">
							<xsl:for-each-group select="current-group()" group-by="@target">
								<xsl:copy-of select="."/>
							</xsl:for-each-group>
						</xsl:for-each-group>
					</xsl:variable>
					<xsl:for-each-group select="$temp_edgesWithoutDuplicates" group-by="@temp_scale">
						<xsl:for-each-group select="current-group()" group-by="@source">
							<xsl:for-each select="current-group()">
								<xsl:sort select="$tree/topo/rule[contains(@content,current()/@target)][1]/@number" data-type="number"/>
								<xsl:element name="msml:edge">
									<xsl:copy-of select="./(*|((@*) except(@temp_scale)))"/>
									<xsl:attribute name="order" select="position()"/>
								</xsl:element>
							</xsl:for-each>
						</xsl:for-each-group>
					</xsl:for-each-group>
				</xsl:variable>
				<xsl:variable name="refedges" as="item()*">
					<xsl:variable name="temp_edges" as="item()*">
					<xsl:for-each-group select="/mtg/classes/class" group-by="@scale">
						<xsl:if test="current-grouping-key()>0">
							<xsl:variable name="currentScaleClasses" select="current-group()"/>
							<xsl:for-each select="/mtg/topo/rule">
								<xsl:call-template name="createRefEdges">
									<xsl:with-param name="rule" select="current()/@content"/>
									<xsl:with-param name="coarseScale" select="$currentScaleClasses"/>
									<xsl:with-param name="finerScale" select="/mtg/classes/class[@scale=current-grouping-key()+1]"/>
								</xsl:call-template>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each-group>
					</xsl:variable>
					<xsl:for-each-group select="$temp_edges" group-by="@source">
						<xsl:for-each-group select="current-group()" group-by="@target">
							<xsl:copy-of select="."/>
						</xsl:for-each-group>
					</xsl:for-each-group>
				</xsl:variable>
				<xsl:for-each-group select="/mtg/classes/class[@scale>0]" group-by="./@scale">
					<xsl:element name="msml:scale">	
					<xsl:attribute name="id" select="concat('s',current-grouping-key())"/>
					<xsl:attribute name="name" select="concat('Scale',current-grouping-key())"/>
					<xsl:for-each-group select="current-group()" group-by="@symbol">
						<xsl:for-each select="$nodes[msml:data/mtg:class/@name=current-grouping-key()]">
							<!-- create nodes in the actual msml:scale-element -->
							<xsl:copy-of select="."/>
							<!-- create non-refinement-edges in the actual msml:scale-element -->
							<xsl:copy-of select="$edges[@source=current()/@id]"/>
						</xsl:for-each>
					</xsl:for-each-group>					
					</xsl:element>
				</xsl:for-each-group>
				<!-- create refinement-edges in msml:msobject-element -->
				<xsl:copy-of select="$refedges"/>	
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createRefEdges">
		<xsl:param name="rule"/>
		<xsl:param name="coarseScale" as="item()*"/>
		<xsl:param name="finerScale" as="item()*"/>
		<xsl:for-each select="$coarseScale">
			<xsl:if test="upper-case(current())!='NONE'">
				<xsl:if test="contains($rule,current()/@symbol)">
					<xsl:variable name="scale" select="current()" as="item()"/>
					<xsl:for-each select="$finerScale">
						<xsl:variable name="scalefine" select="current()" as="item()"/>
						<xsl:variable name="regexpr" 
	select="concat('^(((.*',$scale/@symbol,'[0-9]+)','/[^',$scale/@symbol,'.]*)',$scalefine/@symbol,'[0-9]+)[^',$scale/@symbol,'^',$scalefine/@symbol,'.]*$')"/>						
	 					<xsl:analyze-string select="$rule" regex="{$regexpr}">
							<xsl:matching-substring>
								<xsl:variable name="source" select="regex-group(3)"/>
								<xsl:variable name="target" select="regex-group(1)"/>
								<msml:edge source="{$source}" target="{$target}" type="refinement"/>
								<xsl:call-template name="createRefEdges">
									<xsl:with-param name="rule" select="regex-group(2)"/>
									<xsl:with-param name="coarseScale" select="$scale"/>
									<xsl:with-param name="finerScale" select="$scalefine"/>
								</xsl:call-template>
							</xsl:matching-substring>
						</xsl:analyze-string>
					</xsl:for-each>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>


	<xsl:template name="createEdges">
		<xsl:param name="rule"/>
		<xsl:param name="left"/>
		<xsl:param name="right"/>
		<xsl:param name="reltype"/>
		<xsl:param name="scale"/>
		<xsl:if test="contains($rule,$left)">
			<xsl:variable name="regexpr" 
select="concat('^((.*',$left,'[0-9]+)[^',$left,'.]*',if ($reltype='+')then '\'else(),$reltype,'[^',$left,'.]*',$right,'[0-9]+).*$')"/>
			<xsl:analyze-string select="$rule" regex="{$regexpr}">
				<xsl:matching-substring>
					<xsl:variable name="source" select="regex-group(2)"/>
					<xsl:variable name="target" select="regex-group(1)"/>
					<xsl:element name="msml:edge">
						<xsl:attribute name="source" select="$source"/>
						<xsl:attribute name="target" select="$target"/>
						<xsl:attribute name="type" select="if ($reltype='+')then 'branch' else 'successor'"/>
						<xsl:attribute name="temp_scale" select="$scale"/>
					</xsl:element>
					<xsl:call-template name="createEdges">
						<xsl:with-param name="rule" select="regex-group(2)"/>
						<xsl:with-param name="left" select="$left"/>
						<xsl:with-param name="right" select="$right"/>
						<xsl:with-param name="reltype" select="$reltype"/>
						<xsl:with-param name="scale" select="$scale"/>
					</xsl:call-template>
				</xsl:matching-substring>
			</xsl:analyze-string>
		</xsl:if>
	</xsl:template>
</xsl:transform>