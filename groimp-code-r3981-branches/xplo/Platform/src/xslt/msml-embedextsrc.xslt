<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://grogra.de/msml" xmlns:msml="http://grogra.de/msml"
	xmlns:x3d="http://www.web3d.org/specifications"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" version="2.0"
	xmlns:g="http://grogra.de/msml/datatypes/groimp"
	exclude-result-prefixes="msml fn">
	<xsl:output method="xml" indent="yes" encoding="utf-8"
		version="1.0"/>
	<xsl:strip-space elements="*" />
	<xsl:template match="/msml:msml">
	<xsl:message>XSLT-Prozessor:<xsl:value-of select="system-property('xsl:vendor')"/></xsl:message>
		<msml>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates
				select="msml:library|msml:group|msml:msobject" />
		</msml>
	</xsl:template>

	<xsl:template match="msml:library">
		<xsl:variable name="dataitems">
			<xsl:call-template name="fillLibrary">
				<xsl:with-param name="actualDocument" select="/" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="names">
			<xsl:for-each-group select="$dataitems/*" group-by="@DEF">
				<xsl:copy-of select="current-group()[1]"/>
			</xsl:for-each-group>
		</xsl:variable>
		<xsl:if test="count($names/*)>0">
			<library>
				<data>
					<xsl:copy-of select="$names"/>
				</data>
			</library>
		</xsl:if>
	</xsl:template>
	
 	<xsl:template name="fillLibrary">
		<xsl:param name="actualDocument"/>
		<xsl:for-each select="$actualDocument//msml:library/msml:data">
			<xsl:apply-templates />
		</xsl:for-each>
		<xsl:for-each select="$actualDocument//msml:extSrc">
			<xsl:variable name="importurl">
				<xsl:variable name="prechar" select="substring-before(@src, '#')"/>
				<xsl:choose>
					<xsl:when test="(contains(@src,'#'))and($prechar!='')">
						<xsl:value-of select="resolve-uri(substring-before(@src, '#'),base-uri(.))" />
					</xsl:when>
					<xsl:when test="(contains(@src,'#'))and($prechar='')">
						<!-- referred element is inside the same document -->
						<xsl:value-of select="base-uri(.)" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="resolve-uri(@src,base-uri(.))" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<!-- extSrc-Verweisen, die nicht auf Elemente in externen Dateien zeigen
				, sondern auf Elemente im gleichen Dokument (z.B. #scale),
				soll nicht gefolgt werden, sonst Gefahr von Endlosschleifen -->
			<xsl:if test="$importurl!=base-uri(.)">
				<xsl:choose>
					<xsl:when test="doc-available($importurl)">
						<xsl:call-template name="fillLibrary">
							<xsl:with-param name="actualDocument" select="document($importurl)"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="msml:data">
		<data>
			<xsl:apply-templates />
		</data>
	</xsl:template> 
	
	<xsl:template match="x3d:ImageTexture">
		<x3d:ImageTexture>
			<xsl:attribute name="url" select="resolve-uri(@url,base-uri(.))" />
			<xsl:copy-of select="((@*)except(@url))" />
		</x3d:ImageTexture>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:copy-of select="." />
	</xsl:template>
	
	<xsl:template match="msml:group">
		<group>
			<xsl:copy-of select="@*" />
			<xsl:apply-templates select="msml:group|msml:msobject|msml:data" />
		</group>
	</xsl:template>
	
	<xsl:template match="msml:msobject">
		<xsl:param name="SuperID" />
		<msobject>
		<!-- Wenn SuperID leer ist, dann bleibt id, sonst wird eine neue id erstellt
			indem der SuperID-Wert mit dem bisherigen id-Wert verbunden wird. -->
			<xsl:attribute name="id" select="concat($SuperID,@id)" />
			<xsl:choose>
				<xsl:when test="@USE">
					<xsl:copy-of select="((@*)except(@USE,@id))" />
					<xsl:apply-templates select="//msml:library/msml:msobject[@DEF=current()/@USE]/*">
					 	<xsl:with-param name="SuperID" select="concat($SuperID,@id)" />
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="((@*)except(@id))" />
					<xsl:apply-templates
						select="msml:extSrc|msml:scale|msml:edge|msml:data">
						<xsl:with-param name="SuperID" select="$SuperID" />
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</msobject>
	</xsl:template>
	
	<xsl:template match="msml:scale">
		<xsl:param name="SuperID" />
		<scale>
			<xsl:attribute name="id" select="concat($SuperID,@id)" />
			<xsl:copy-of select="((@*)except(@id))" />
			<xsl:apply-templates select="msml:extSrc|msml:msobject|msml:node|msml:edge|msml:data">
				<xsl:with-param name="SuperID" select="$SuperID" />
			</xsl:apply-templates>
		</scale>
	</xsl:template>
	
	<xsl:template match="msml:node">
		<xsl:param name="SuperID" />
		<node>
			<xsl:attribute name="id" select="concat($SuperID,@id)" />
			<xsl:copy-of select="((@*)except(@id))" />
			<xsl:apply-templates select="msml:data"/>
		</node>
	</xsl:template>
	
	<xsl:template match="msml:edge">
		<xsl:param name="SuperID" />
		<edge>
			<xsl:attribute name="source" select="concat($SuperID,@source)"/>
			<xsl:attribute name="target" select="concat($SuperID,@target)"/>
			<xsl:copy-of select="((@*)except(@source,@target))" />
			<xsl:apply-templates select="msml:data"/>
		</edge>
	</xsl:template>

	<xsl:template match="msml:extSrc">
		<xsl:param name="SuperID" />
		<xsl:variable name="importurl">
			<xsl:variable name="prechar" select="substring-before(@src, '#')"/>
			<xsl:choose>
				<xsl:when test="(contains(@src,'#'))and($prechar!='')">
					<xsl:value-of select="resolve-uri(substring-before(@src, '#'),base-uri(.))" />
				</xsl:when>
				<xsl:when test="(contains(@src,'#'))and($prechar='')">
					<!-- referred element is inside the same document -->
					<xsl:value-of select="base-uri(.)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="resolve-uri(@src,base-uri(.))" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="doc-available($importurl)">
				<xsl:variable name="elemtree" select="document($importurl)" />
				<xsl:variable name="importid" select="substring-after(@src, '#')"/>
				<xsl:choose>
					<xsl:when test="(local-name(..)='msobject')and($importid='')">
						<xsl:apply-templates select="$elemtree//msml:msobject/*">
							<xsl:with-param name="SuperID" select="concat($SuperID,../@id)" />
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="(local-name(..)='msobject')and not($importid='')">
						<xsl:apply-templates select="$elemtree//msml:msobject[@id=$importid]/*">
							<xsl:with-param name="SuperID" select="concat($SuperID,../@id)" />
						</xsl:apply-templates>
 					</xsl:when>
 					<xsl:when test="(local-name(..)='scale')and not($importid='')">
						<xsl:apply-templates select="$elemtree//msml:scale[@id=$importid]/*">
							<xsl:with-param name="SuperID" select="concat($SuperID,../@id)" />
						</xsl:apply-templates>
 					</xsl:when>  
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:message>Error: File from external Source not found!</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:transform>