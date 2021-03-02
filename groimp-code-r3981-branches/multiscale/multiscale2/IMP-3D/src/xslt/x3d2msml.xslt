<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://grogra.de/msml"
	xmlns:msml="http://grogra.de/msml"
	xmlns:x3d="http://www.web3d.org/specifications"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" version="2.0"
	xmlns:g="http://grogra.de/msml/datatypes/groimp"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xdt fn">
	<xsl:output method="xml" indent="yes" encoding="utf-8" version="1.0" />
	<xsl:template name="resolveDEFUSE">
		<xsl:param name="node"/>
		<!-- rekursiv X3D-Baum durchlaufen und alle Vorkommen von USE durch
		 DEF-Inhalte ersetzen ausser für Appearance-Elemente, da diese gesondert
		 behandelt werden -->
		<xsl:choose>
			<xsl:when test="string(node-name($node))='Appearance'">
				<xsl:copy>
					<xsl:for-each select="$node/@*">
						<xsl:copy/>
					</xsl:for-each>
					<xsl:for-each select="$node/*">
						<xsl:call-template name="resolveDEFUSE">
							<xsl:with-param name="node" select="."/>
						</xsl:call-template>
					</xsl:for-each>
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$node[@DEF]">
						<xsl:copy>
							<xsl:for-each select="$node/((@*)except(@DEF))">
								<xsl:copy/>
							</xsl:for-each>
							<xsl:for-each select="$node/*">
								<xsl:call-template name="resolveDEFUSE">
									<xsl:with-param name="node" select="."/>
								</xsl:call-template>
							</xsl:for-each>
						</xsl:copy>
					</xsl:when>
					<xsl:when test="$node[@USE]">
						<xsl:variable name="usename" select="$node/@USE"/>
						<xsl:for-each select="//*[@DEF]">
							<xsl:if test="(node-name(.)=node-name($node))and(./@DEF=$node/@USE)">
								<xsl:call-template name="resolveDEFUSE">
									<xsl:with-param name="node" select="."/>
								</xsl:call-template>
							</xsl:if>
						</xsl:for-each>
						<xsl:for-each select="$node/*">
							<xsl:call-template name="resolveDEFUSE">
								<xsl:with-param name="node" select="."/>
							</xsl:call-template>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:copy>
							<xsl:for-each select="$node/@*">
								<xsl:copy/>
							</xsl:for-each>
							<xsl:for-each select="$node/*">
								<xsl:call-template name="resolveDEFUSE">
									<xsl:with-param name="node" select="."/>
								</xsl:call-template>
							</xsl:for-each>
						</xsl:copy>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="X3D">
		<xsl:variable name="content">
			<xsl:choose>
				<xsl:when test="//((*)except(Appearance))[@USE]">
					<xsl:call-template name="resolveDEFUSE">
						<xsl:with-param name="node" select="."/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<msml:msml version="1.0">
			<msml:library>
				<msml:data>
				<xsl:for-each select="$content//Appearance[@DEF]">
					<xsl:element name="g:Appearance">
						<xsl:copy-of select="@DEF"/>
						<xsl:element name="x3d:Appearance">
							<xsl:apply-templates select="./*"/>
						</xsl:element>
					</xsl:element>
				</xsl:for-each>
				</msml:data>
			</msml:library>
			<xsl:apply-templates select="$content/X3D/*" />
		</msml:msml>
	</xsl:template>

	<xsl:template match="Scene">
		<msml:msobject>
			<xsl:attribute name="id"><xsl:value-of select="generate-id(.)"/></xsl:attribute>
			<xsl:attribute name="name">X3D-Scene</xsl:attribute>
			<msml:scale>
				<xsl:for-each select="Shape|Group|Transform|Switch">
					<xsl:variable name="nodename">
						<xsl:value-of select="string(node-name(.))"/>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="($nodename='Shape')or($nodename='Switch')">
							<xsl:apply-templates select="."/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="traverse">
								<xsl:with-param name="node" select="."/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</msml:scale>
		</msml:msobject>
	</xsl:template>
	
	<xsl:template name="traverse">
		<xsl:param name="node"/>
		<xsl:for-each select="$node/(Shape|Group|Transform|Switch)">
			<xsl:variable name="nodename">
				<xsl:value-of select="string(node-name(.))"/>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="($nodename='Shape')or($nodename='Switch')">
					<xsl:variable name="predecessornode" select="((ancestor::Transform|ancestor::Group|ancestor::Switch)/(Shape|Switch))[last()-1]"/>
					<xsl:variable name="predecessornodename" select="string(node-name($predecessornode))"/>
					<xsl:variable name="predecessornodeid" select="generate-id($predecessornode)"/>
					<xsl:variable name="nodeid">
						<xsl:value-of select="generate-id(.)"/>
					</xsl:variable>
					<xsl:if test="not($predecessornodename='Switch')and not($predecessornodeid='')and not($predecessornodeid=$nodeid)">
						<xsl:element name="msml:edge">
							<xsl:attribute name="source">
								<xsl:value-of select="$predecessornodeid"/>
							</xsl:attribute>
							<xsl:attribute name="target">
								<xsl:value-of select="$nodeid"/>
							</xsl:attribute>
							<xsl:attribute name="type">successor</xsl:attribute> 
						</xsl:element>
					</xsl:if>
					<xsl:variable name="actualShape" select="."/>
					<xsl:variable name="predecessortransforms" select="(ancestor::Transform|ancestor::Group)"/>
					<xsl:variable name="firstprecedingTransformwithanotherShapeNode" select="($predecessortransforms[(Shape|Switch)except $actualShape][last()])"/>
					<xsl:variable name="precedingTransformswithoutanotherSubShapeNode"
					 select="if (empty($firstprecedingTransformwithanotherShapeNode))
								then $predecessortransforms
								else (($firstprecedingTransformwithanotherShapeNode//(Transform|Group))intersect $predecessortransforms)"/>
				 <!-- <xsl:message><xsl:value-of select="$precedingTransformswithoutanotherSubShapeNode/@*" separator=","/>|ENDE</xsl:message> -->	
				 	<xsl:apply-templates select=".">
						<xsl:with-param name="transformdata" select="$precedingTransformswithoutanotherSubShapeNode"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="traverse">
						<xsl:with-param name="node" select="."/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="Switch">
		<xsl:param name="transformdata"/>
		<msml:msobject>
			<xsl:attribute name="id"><xsl:value-of select="generate-id(.)"/></xsl:attribute>
			<xsl:attribute name="showScale">scale<xsl:value-of select="@whichChoice"/></xsl:attribute>
			<xsl:if test="not(empty($transformdata))">
					<xsl:variable name="tempcontent">
						<xsl:call-template name="createTransformstructure">
							<xsl:with-param name="innerTransforms" select="$transformdata"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:if test="$tempcontent/*/attribute()">
						<msml:data>
							<g:Transform>
								<xsl:copy-of select="$tempcontent"/>
							</g:Transform>
						</msml:data>
					</xsl:if>
				</xsl:if>
			<xsl:for-each select="Shape|Group|Transform|Switch">	
				<msml:scale>
					<xsl:attribute name="id"><xsl:value-of select="generate-id(.)"/></xsl:attribute>
					<xsl:attribute name="name">scale<xsl:value-of select="position()-1"/></xsl:attribute>
					<xsl:variable name="nodename">
						<xsl:value-of select="string(node-name(.))"/>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="($nodename='Shape')or($nodename='Switch')">
							<xsl:variable name="predecessornode" select="((ancestor::Transform|ancestor::Group|ancestor::Switch)/(Shape|Switch))[last()-1]"/>
							<xsl:variable name="predecessornodename" select="string(node-name($predecessornode))"/>
							<xsl:variable name="predecessornodeid" select="generate-id($predecessornode)"/>
							<xsl:variable name="nodeid">
								<xsl:value-of select="generate-id(.)"/>
							</xsl:variable>
							<xsl:if test="not($predecessornodename='Switch')and not($predecessornodeid='')and not($predecessornodeid=$nodeid)">
								<xsl:element name="msml:edge">
									<xsl:attribute name="source">
										<xsl:value-of select="$predecessornodeid"/>
									</xsl:attribute> 
									<xsl:attribute name="target">
										<xsl:value-of select="$nodeid"/>
									</xsl:attribute>
									<xsl:attribute name="type">successor</xsl:attribute> 
								</xsl:element>
							</xsl:if>
							<xsl:apply-templates select="."/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="traverse">
								<xsl:with-param name="node" select="."/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</msml:scale>
			</xsl:for-each>
		</msml:msobject>
	</xsl:template>
	
	<xsl:template match="Shape">
		<xsl:param name="transformdata"/>
		<msml:node>
			<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
			<msml:data>
				<g:Shape>
					<xsl:apply-templates select="./(*)except(Appearance, MetadataDouble,
					 MetadataFloat, MetadataInteger, MetadataSet, MetadataString)"/>
				</g:Shape>
				<xsl:apply-templates select="Appearance" />
				<xsl:if test="not(empty($transformdata))">
					<xsl:variable name="tempcontent">
						<xsl:call-template name="createTransformstructure">
							<xsl:with-param name="innerTransforms" select="$transformdata"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:if test="$tempcontent/*/attribute()">
						<g:Transform>
							<xsl:copy-of select="$tempcontent"/>
						</g:Transform>
					</xsl:if>
				</xsl:if>
			</msml:data>
		</msml:node>
	</xsl:template>
	
	<xsl:template name="createTransformstructure">
		<xsl:param name="innerTransforms"/>
		<xsl:if test="$innerTransforms instance of element()+">
			<xsl:choose>
				<xsl:when test="($innerTransforms[1]/@*) instance of attribute()+">
					<x3d:Transform>
						<xsl:copy-of select="$innerTransforms[1]/@*"/>
						<xsl:call-template name="createTransformstructure">
							<xsl:with-param name="innerTransforms" select="subsequence($innerTransforms,2)"/>
						</xsl:call-template>
					</x3d:Transform>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="createTransformstructure">
						<xsl:with-param name="innerTransforms" select="subsequence($innerTransforms,2)"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="Appearance">
		<xsl:choose>
			<xsl:when test="@USE|@DEF">
				<g:Appearance>
					<xsl:attribute name="USE">
						<xsl:value-of select="@USE|@DEF"/>
					</xsl:attribute>
				</g:Appearance>
			</xsl:when>
			<xsl:otherwise>
				<g:Appearance>
					<x3d:Appearance>
						<xsl:apply-templates select="*"/>
					</x3d:Appearance>
				</g:Appearance>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="PointLight">
	</xsl:template>

	<xsl:template match="*">
		<xsl:element name="x3d:{local-name()}">
			<xsl:copy-of select="@*" />
			<xsl:apply-templates select="*" />
		</xsl:element>
	</xsl:template>
</xsl:transform>