<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:msml="http://grogra.de/msml"
	xmlns:g="http://grogra.de/msml/datatypes/groimp"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:msml2mtg="http://grogra.de/msml"
	xmlns:mtg="http://grogra.de/mtg"
	version="2.0">
	<xsl:output method="text" encoding="utf-8"/>

<!-- This parameter selects the object, that should be exported to MTG.
	 If no parameter is set, the first msml:msobject is selected as default. -->
<xsl:param name="msobject" select="//msml:msobject[1]"/>

<!-- This variable contains all class symbols, that are possible in MTG -->
<xsl:variable name="possibleClassSymbols">
	<xsl:value-of select="'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
</xsl:variable>

<!-- This variable contains all class symbols, that are used inside the selected object -->
<xsl:variable name="classSymbols" as="xs:string*">
	<xsl:variable name="sortedClassnodes" as="item()*">
		<xsl:perform-sort select="$msobject/msml:scale/(msml:msobject|msml:node)">
			<xsl:sort select="parent::msml:scale"/>
			<xsl:sort select="msml:data/mtg:class/@name"/>
		</xsl:perform-sort>
	</xsl:variable>
	<xsl:sequence select="msml2mtg:assignClassSymbols($sortedClassnodes,$possibleClassSymbols)"/>
</xsl:variable>

<!-- This function assigns class symbols to the nodes specified in parameter "nodes".
	 The parameter "symbols" contains the available class symbols.
	 Precondition for this function: No more than 52 different classes exists in the 
	 selected MSML-object -->
<xsl:function name="msml2mtg:assignClassSymbols" as="xs:string*">
	<xsl:param name="nodes"/>
	<xsl:param name="symbols"/>
	<xsl:variable name="actualClassSymbol" select="substring($symbols,1,1)"/>
	<xsl:variable name="actualScale" select="msml2mtg:getScale($nodes[1])"/>
	<xsl:variable name="actualClassnodes" as="item()*">
		<xsl:choose>
			<xsl:when test="$nodes[1]/msml:data/mtg:class/@name">
				<xsl:sequence select="$nodes[parent::msml:scale=$actualScale][msml:data/mtg:class/@name=$nodes[1]/msml:data/mtg:class/@name]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="$nodes[parent::msml:scale=$actualScale][not(msml:data/mtg:class/@name)]"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="node-classSymbol-assignment-info" as="item()*">
		<xsl:for-each select="$actualClassnodes">
			<xsl:sequence select="(current()/@id,$actualClassSymbol)"/>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="remainingNodes" select="$nodes except $actualClassnodes" as="node()*"/>
	<xsl:variable name="remainingClassSymbols" select="substring-after($symbols,$actualClassSymbol)"/>
	<xsl:sequence select="if ((string-length($remainingClassSymbols) gt 0)and(exists($remainingNodes)))
		then ($node-classSymbol-assignment-info,msml2mtg:assignClassSymbols($remainingNodes,$remainingClassSymbols))
		else ($node-classSymbol-assignment-info)"/>
</xsl:function>

<!-- Returns the class symbol for a specified node. -->
<xsl:function name="msml2mtg:getClassSymbol">
	<xsl:param name="node" as="node()"/>
	<xsl:value-of select="$classSymbols[index-of($classSymbols,$node/@id)+1]"/>
</xsl:function>

<!-- Returns all symbols of classes, that are used inside of a specified scale. -->
<xsl:function name="msml2mtg:getClassSymbolsFromScale" as="xs:string*">
	<xsl:param name="scale"/>
	<xsl:variable name="nodes" as="xs:string*">
		<xsl:for-each select="$scale/(msml:node|msml:msobject)">
			<xsl:sequence select="msml2mtg:getClassSymbol(current())"/>
		</xsl:for-each>
	</xsl:variable>
	<xsl:sequence select="distinct-values($nodes)"/>
</xsl:function>

<!-- This variable contains a sequence of 3-tupels, that contains:
	 the ID, the class symbol and the number for every node of the selected object. -->
<xsl:variable name="nodeinfo" as="item()*">
	<xsl:for-each select="$msobject/msml:scale">
		<xsl:for-each-group select="msml:node|msml:msobject" group-by="msml2mtg:getClassSymbol(current())">
			<xsl:variable name="classSymbol" select="current-grouping-key()"/>
			<xsl:for-each select="current-group()">
				<xsl:sequence select="(current()/@id,msml2mtg:getClassSymbol(current()),position())"/>
			</xsl:for-each>
		</xsl:for-each-group>
	</xsl:for-each>
</xsl:variable>

<!-- Returns a sequence of strings, which are the nodenames of the 
	 nodes (msml:node or msml:msobject) passed in a sequence as parameter -->
<xsl:function name="msml2mtg:getNodename" as="xs:string*">
	<xsl:param name="node" as="node()*"/>
	<xsl:for-each select="$node">
		<xsl:variable name="nodepos" select="index-of($nodeinfo,current()/@id)"/>
		<xsl:sequence select="concat($nodeinfo[$nodepos+1],$nodeinfo[$nodepos+2])"/>
	</xsl:for-each>
</xsl:function>

<!-- Returns the ID of the node (msml:node or msml:msobject) that belongs to a mtg-nodename-->
<xsl:function name="msml2mtg:getNodeID" as="xs:string*">
	<xsl:param name="nodename" as="xs:string"/>
	<xsl:analyze-string select="$nodename" regex="^.*([a-zA-Z])([0-9]+).*$">
		<xsl:matching-substring>
			<xsl:variable name="nodeinfoString">
				<xsl:value-of select="$nodeinfo" separator=","/>
			</xsl:variable>
			<xsl:variable name="regexpr" select="concat('^(.+),',regex-group(1),',',regex-group(2),'(,.*$|$)')"/>
			<xsl:analyze-string select="$nodeinfoString" regex="{$regexpr}">
				<xsl:matching-substring>
					<xsl:variable name="regexpr" select="'^(.+,)*(.+)$'"/>
					<xsl:analyze-string select="regex-group(1)" regex="{$regexpr}">
						<xsl:matching-substring>
							<xsl:value-of select="regex-group(2)"/>
						</xsl:matching-substring>
					</xsl:analyze-string>
				</xsl:matching-substring>
			</xsl:analyze-string>
		</xsl:matching-substring>
	</xsl:analyze-string>
</xsl:function>

<!-- Returns the equivalent nodes of the specified higher targetScale for a specified node -->
<xsl:function name="msml2mtg:getHigherScaledNodes" as="node()*">
	<xsl:param name="node" as="node()"/>
	<xsl:param name="targetScale" as="node()"/>
	<xsl:for-each-group select="$msobject//msml:edge[@type='refinement'][@source=$node/@id]" group-by="@target">
		<xsl:choose>
			<xsl:when test="$msobject//msml:scale[*[@id=current-grouping-key()]]/@id=$targetScale/@id">
				<xsl:sequence select="$msobject//*[@id=current-grouping-key()]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="msml2mtg:getHigherScaledNodes($msobject//*[@id=current-grouping-key()],$targetScale)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each-group>
</xsl:function>

<!-- Returns the equivalent node of the specified lower targetScale for a specified node -->
<xsl:function name="msml2mtg:getLowerScaledNode" as="node()*">
	<xsl:param name="node" as="node()"/>
	<xsl:param name="targetScale" as="node()"/>
	<xsl:for-each-group select="$msobject//msml:edge[@type='refinement'][@target=$node/@id]" group-by="@source">
		<xsl:choose>
			<xsl:when test="$msobject//msml:scale[*[@id=current-grouping-key()]]/@id=$targetScale/@id">
				<xsl:sequence select="$msobject//*[@id=current-grouping-key()]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="msml2mtg:getLowerScaledNode($msobject//*[@id=current-grouping-key()],$targetScale)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each-group>
</xsl:function>

<xsl:variable name="linebreak"><xsl:text>
</xsl:text>
</xsl:variable>

<xsl:variable name="tab">
	<xsl:text>	</xsl:text>
</xsl:variable>

<!-- returns a specified number of tab's -->
<xsl:function name="msml2mtg:tabs">
	<xsl:param name="numberOfTabs" as="xs:integer"/>
	<xsl:if test="$numberOfTabs gt 0">
		<xsl:value-of select="msml2mtg:tabs($numberOfTabs - 1)"/>
		<xsl:value-of select="$tab"/>
	</xsl:if>
</xsl:function>

<!-- This variable contains the coarsest scale of the selected object. -->
<xsl:variable name="coarsestScale" as="node()">
	<xsl:variable name="refinementedgetargets" as="node()*">
		<xsl:sequence select="for $target in distinct-values($msobject/msml:edge/@target)
			return $msobject//msml:node[@id=$target]"/>
	</xsl:variable>
	<xsl:variable name="refinementedgesources" as="node()*">
		<xsl:sequence select="for $source in distinct-values($msobject/msml:edge/@source)
			return $msobject//(msml:node|msml:msobject)[@id=$source]"/>
	</xsl:variable>
	<xsl:variable name="coarsestScalenodes" as="node()*">
		<xsl:sequence select="$refinementedgesources except $refinementedgetargets"/>
	</xsl:variable>
	<xsl:choose>
		<xsl:when test="exists($coarsestScalenodes)">
			<xsl:sequence select="for $scalenode in $coarsestScalenodes
				return $msobject//msml:scale[*[@id=$scalenode/@id]]"/>
		</xsl:when>
		<xsl:otherwise>
			<!-- if no refinement-edges are available, the first scale is choosen as coarsest scale -->
			<xsl:sequence select="$msobject//msml:scale[1]"></xsl:sequence>
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>

<!-- Returns a sequence of scales, which directly refine a specified scale. 
	 "Directly", means that only scales are returned, which are connected to
	 the specified scale with edges of the type "refinement".
	 For example, if scale C refines B and B refines A, then only scale B is returned,
	 if scale A is passed to this function. -->
<xsl:function name="msml2mtg:higherScale">
	<xsl:param name="scale" as="node()"/>
	<xsl:variable name="nodeids" as="xs:string*">
		<xsl:sequence select="$scale/(msml:node|msml:msnode)/@id"/>
	</xsl:variable>
	<xsl:variable name="targets" as="xs:string*">
		<xsl:sequence select="distinct-values($msobject//msml:edge[@type='refinement'][@source=$nodeids]/@target)"/>
	</xsl:variable>
	<xsl:sequence select="$msobject//msml:scale[msml:node/@id=$targets][1]"/>
</xsl:function>

<!-- Returns the scale, which is directly refined by a specified scale. 
	 "Directly", means that only the scale is returned, which is connected to
	 the specified scale with an edge of the type "refinement". -->
<xsl:function name="msml2mtg:lowerScale">
	<xsl:param name="scale" as="node()"/>
	<xsl:variable name="nodeids" as="xs:string*">
		<xsl:sequence select="$scale/(msml:node|msml:msnode)/@id"/>
	</xsl:variable>
	<xsl:variable name="sources" as="xs:string*">
		<xsl:sequence select="distinct-values($msobject//msml:edge[@type='refinement'][@target=$nodeids]/@source)"/>
	</xsl:variable>
	<xsl:sequence select="$msobject//msml:scale[msml:node/@id=$sources][1]"/>
</xsl:function>

<!-- Returns a sequence of the root-nodes of a specified scale. -->
<xsl:function name="msml2mtg:getRoot">
	<xsl:param name="scale"/>
	<xsl:variable name="edgetargets" as="xs:string*">
		<xsl:sequence select="distinct-values($scale/msml:edge/@target)"/>
	</xsl:variable>
	<xsl:sequence select="$scale/(msml:node|msml:msobject)[not(@id=$edgetargets)]"/>
</xsl:function>

<!-- Returns the msml:scale-node for a specified msml:node or msml:msobject. -->
<xsl:function name="msml2mtg:getScale" as="node()">
	<xsl:param name="node" as="node()"/>
	<xsl:copy-of select="$msobject//msml:scale[(msml:node|msml:msobject)[@id=$node/@id]]"/>
</xsl:function>

<!-- Returns a sequence of all nodes of the scale of a specified node A,
	 that are children of A, including A itself, ordered in 
	 prefix notation. -->
<xsl:function name="msml2mtg:sortChildrenAndSelf" as="node()*">
	<xsl:param name="node" as="node()"/>
	<xsl:sequence select="$node"/>
	<xsl:for-each select="msml2mtg:getScale($node)/msml:edge[@source=$node/@id]">
		<xsl:sort select="@order" data-type="number"/>
		<xsl:sequence select="msml2mtg:sortChildrenAndSelf(msml2mtg:getNodes(current()/@target))"/>
	</xsl:for-each>
</xsl:function>

<!-- Returns a sequence of all nodes of the same scale,
	 passed in a sequence as parameter, ordered in prefix notation. -->
<xsl:function name="msml2mtg:sortNodes">
	<xsl:param name="nodes" as="node()+"/>
	<xsl:variable name="scale" select="msml2mtg:getScale($nodes[1])"/>
	<xsl:variable name="root" select="msml2mtg:getRoot($scale)"/>
	<xsl:variable name="sortedScalenodes" select="msml2mtg:sortChildrenAndSelf($root)"/>
	<xsl:for-each-group select="$sortedScalenodes" group-by="@id">
		<xsl:if test="some $x in $nodes/@id satisfies $x=current-grouping-key()">
			<xsl:sequence select="msml2mtg:getNodes(current-grouping-key())"/>
		</xsl:if>
	</xsl:for-each-group>
</xsl:function>

<!-- Returns a sequence of nodes (msml:node or msml:msobject),
	 which IDs were specified in a sequence of strings as parameter of the function.
	 The order of the returned nodes is the same as the order of the sequence of IDs -->
<xsl:function name="msml2mtg:getNodes" as="node()*">
	<xsl:param name="nodeids" as="xs:string*"/>
	<xsl:for-each select="$nodeids">
		<xsl:sequence select="$msobject//(msml:node|msml:msobject)[@id=current()]"/>
	</xsl:for-each>
</xsl:function>

	
	<!-- this is the MAIN-Template, where the processing begins -->
	<xsl:template match="/msml:msml">
		<xsl:text>CODE:</xsl:text><xsl:value-of select="$tab"/>
		<xsl:call-template name="code"/>
		<xsl:value-of select="$linebreak"/>
		<xsl:text>CLASSES:</xsl:text>
		<xsl:call-template name="classes"/>
		<xsl:value-of select="$linebreak"/>
		<xsl:text>DESCRIPTION:</xsl:text>
		<xsl:call-template name="description"/>
		<xsl:value-of select="$linebreak"/>
		<xsl:text>FEATURES:</xsl:text>
		<xsl:call-template name="features"/>
		<xsl:value-of select="$linebreak"/>
		<xsl:text>MTG:</xsl:text>
		<xsl:call-template name="mtg"/>
		<xsl:value-of select="$linebreak"/>
	</xsl:template>

	<xsl:template name="code">
		<xsl:text>FORM-A</xsl:text>
		<xsl:value-of select="$linebreak"/>
	</xsl:template>
	
	<xsl:template name="scaleInfo">
		<xsl:param name="scale" as="node()"/>
		<xsl:param name="scaleNumber"/>
		<xsl:variable name="higherScale" as="node()*">
			<xsl:sequence select="msml2mtg:higherScale($scale)"/>
		</xsl:variable>
		<!-- SYMBOL: single character -->
		<xsl:variable name="classSymbols" select="msml2mtg:getClassSymbolsFromScale($scale)"/>
		<xsl:for-each select="$classSymbols">
			<xsl:value-of select="current()"/>
			<xsl:value-of select="$tab"/>
			<!-- SCALE: 0 to n -->
			<xsl:value-of select="$scaleNumber"/>
			<xsl:value-of select="$tab"/>
			<!-- DECOMPOSITION: CONNECTED
								LINEAR
								<-LINEAR
								+-LINEAR
								FREE
								NONE -->
			<xsl:text>FREE</xsl:text>
			<xsl:value-of select="$tab"/>
			<!-- INDEXATION: is not used, that is why it is always FREE -->
			<xsl:text>FREE</xsl:text>
			<xsl:value-of select="$tab"/>
			<!-- DEFINITION:EXPLICIT: class symbol has attributes
							IMPLICIT: class symbol has no attributes -->
			<xsl:text>EXPLICIT</xsl:text>
			<xsl:value-of select="$linebreak"/>
		</xsl:for-each>
		<xsl:if test="exists($higherScale)">
			<xsl:call-template name="scaleInfo">
				<xsl:with-param name="scale" select="$higherScale"/>
				<xsl:with-param name="scaleNumber" select="$scaleNumber+1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="classes">
		<xsl:value-of select="$linebreak"/>
		<xsl:text>SYMBOL	SCALE	DECOMPOSITION	INDEXATION	DEFINITION</xsl:text>
		<xsl:value-of select="$linebreak"/>
		<xsl:text>$	0	FREE	FREE	IMPLICIT</xsl:text>
		<xsl:value-of select="$linebreak"/>
		<xsl:call-template name="scaleInfo">
			<xsl:with-param name="scale" select="$coarsestScale"/>
			<xsl:with-param name="scaleNumber" select="1"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="description">
		<xsl:value-of select="$linebreak"/>
		<xsl:text>LEFT	RIGHT	RELTYPE	MAX</xsl:text>
		<xsl:value-of select="$linebreak"/>
		<xsl:call-template name="relationInfo">
			<xsl:with-param name="scale" select="$coarsestScale"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="relationInfo">
		<xsl:param name="scale" as="node()"/>
		<xsl:variable name="higherScale" as="node()*">
			<xsl:sequence select="msml2mtg:higherScale($scale)"/>
		</xsl:variable>
		<xsl:for-each-group select="$scale/msml:edge" group-by="@type">
			<xsl:variable name="relationtype" select="current-grouping-key()"/>
			<xsl:for-each-group select="current-group()" group-by="msml2mtg:getClassSymbol($msobject//(msml:node|msml:msobject)[@id=current()/@source])">
				<xsl:variable name="left" select="current-grouping-key()"/>
				<xsl:for-each-group select="current-group()" group-by="msml2mtg:getClassSymbol($msobject//(msml:node|msml:msobject)[@id=current()/@target])">
					<xsl:variable name="right" select="current-grouping-key()"/>
					<xsl:variable name="max" select="count(current-group())"/>
					<!-- LEFT -->
					<xsl:value-of select="$left"/>
					<xsl:value-of select="$tab"/>
					<!-- RIGHT -->
					<xsl:value-of select="$right"/>
					<xsl:value-of select="$tab"/>
					<!-- RELTYPE: + or < -->
					<xsl:choose>
						<xsl:when test="$relationtype='branch'">
							<xsl:text>+</xsl:text>
						</xsl:when>
						<xsl:when test="$relationtype='successor'">
							<xsl:text>&lt;</xsl:text>
						</xsl:when>
					</xsl:choose>
					<xsl:value-of select="$tab"/>
					<!-- MAX -->
					<xsl:choose>
						<xsl:when test="$max=1">
							<xsl:text>1</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>?</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:value-of select="$linebreak"/>
				</xsl:for-each-group>
			</xsl:for-each-group>
		</xsl:for-each-group>
		<xsl:if test="exists($higherScale)">
			<xsl:call-template name="relationInfo">
				<xsl:with-param name="scale" select="$higherScale"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
		
	<xsl:template name="features">
		<xsl:value-of select="$linebreak"/>
		<xsl:text>NAME	TYPE</xsl:text>
		<xsl:value-of select="$linebreak"/>
	</xsl:template>
	
	<xsl:template name="mtg">
		<xsl:value-of select="$linebreak"/>
		<xsl:text>TOPO</xsl:text>
		<xsl:value-of select="$linebreak"/>	
	 	<xsl:variable name="content" as="xs:string*">
	 		<xsl:sequence select="'/'"/>
		 	<xsl:call-template name="createMTGDatatree">
				<xsl:with-param name="actualNode" select="msml2mtg:getRoot($coarsestScale)[1]"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="insertTabs">
			<xsl:with-param name="allNodes" select="msml2mtg:createContentStructure($content)"/>
			<xsl:with-param name="precedingNodeIndex" select="0"/>
			<xsl:with-param name="actualNodeIndex" select="1"/>
		</xsl:call-template>
	</xsl:template>

<!-- the contentstructure is a sequence of tupels, where each of them contains
	 a rule (from content) and the number of tab's for that rule -->
	<xsl:function name="msml2mtg:createContentStructure" as="xs:string*">
		<xsl:param name="content" as="xs:string*"/>
		<xsl:if test="exists($content)">
			<xsl:sequence select="(concat($content[1],$content[2]),'0')"/>
			<xsl:sequence select="msml2mtg:createContentStructure(subsequence($content,3))"/>
		</xsl:if>
	</xsl:function>	

<!-- Calculates the necessary amount of tab's for each rule and creates the resulting
	 tree of rules with tab's. -->			
	<xsl:template name="insertTabs">
		<xsl:param name="allNodes" as="xs:string*"/>
		<xsl:param name="precedingNodeIndex" as="xs:integer"/>
		<xsl:param name="actualNodeIndex" as="xs:integer"/>
		<xsl:if test="$actualNodeIndex le (count($allNodes) idiv 2)">
			<xsl:choose>
				<xsl:when test="$precedingNodeIndex gt 0">
					<xsl:variable name="actualNode" select="$msobject//*[@id=msml2mtg:getNodeID($allNodes[2* $actualNodeIndex -1])]"/>
					<xsl:variable name="precedingNode" select="$msobject//*[@id=msml2mtg:getNodeID($allNodes[2* $precedingNodeIndex -1])]"/>
				 	<xsl:variable name="higherScaledNodesForActualNode" select="msml2mtg:getHigherScaledNodes($actualNode,msml2mtg:getScale($precedingNode))"/>
					<xsl:variable name="lowerScaledNodeForPrecedingNode" select="msml2mtg:getLowerScaledNode($precedingNode,msml2mtg:getScale($actualNode))"/>
				 	<xsl:variable name="actualNodeEquivalent" 
						select="if (exists($higherScaledNodesForActualNode) and msml2mtg:isLowerScaleThan(msml2mtg:getScale($actualNode),msml2mtg:getScale($precedingNode)))
							then (msml2mtg:sortNodes($higherScaledNodesForActualNode)[1])
							else ($actualNode)"/>
					<xsl:variable name="precedingNodeEquivalent" 
						select="if (not(exists($higherScaledNodesForActualNode))and exists($lowerScaledNodeForPrecedingNode) and msml2mtg:isLowerScaleThan(msml2mtg:getScale($actualNode),msml2mtg:getScale($precedingNode)))
							then ($lowerScaledNodeForPrecedingNode)
							else ($precedingNode)"/>
					<xsl:choose>
						<xsl:when test="$msobject//msml:edge[@source=$precedingNodeEquivalent/@id][@target=$actualNodeEquivalent/@id]">
							<xsl:variable name="temp_tab" select="xs:integer($allNodes[2* $precedingNodeIndex]) +1" as="xs:integer"/>
							<xsl:value-of select="msml2mtg:tabs($temp_tab)"/>
							<xsl:value-of select="$allNodes[2* $actualNodeIndex -1]"/>
							<xsl:value-of select="$linebreak"/>
							<xsl:call-template name="insertTabs">
								<xsl:with-param name="allNodes" select="insert-before(remove($allNodes,2* $actualNodeIndex),2* $actualNodeIndex,string($temp_tab))"/>
								<xsl:with-param name="precedingNodeIndex" select="$actualNodeIndex"/>
								<xsl:with-param name="actualNodeIndex" select="$actualNodeIndex + 1"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="insertTabs">
								<xsl:with-param name="allNodes" select="$allNodes"/>
								<xsl:with-param name="precedingNodeIndex" select="$precedingNodeIndex - 1"/>
								<xsl:with-param name="actualNodeIndex" select="$actualNodeIndex"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$allNodes[2* $actualNodeIndex -1]"/>
					<xsl:value-of select="$linebreak"/>
					<xsl:call-template name="insertTabs">
						<xsl:with-param name="allNodes" select="$allNodes"/>
						<xsl:with-param name="precedingNodeIndex" select="$actualNodeIndex"/>
						<xsl:with-param name="actualNodeIndex" select="$actualNodeIndex + 1"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	
	<!-- Returns TRUE, if scale1 is lower than scale2, else FALSE -->	
	<xsl:function name="msml2mtg:isLowerScaleThan" as="xs:boolean">
		<xsl:param name="scale1" as="node()"/>
		<xsl:param name="scale2" as="node()"/>
		<xsl:variable name="finerScale" select="msml2mtg:higherScale($scale2)"/>
		<xsl:choose>
			<xsl:when test="$scale2/@id=$scale1/@id">
				<xsl:value-of select="false()"/>
			</xsl:when>
			<xsl:when test="empty($finerScale) and not($scale2/@id=$scale1/@id)">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$finerScale[1]/@id=$scale1/@id">
						<xsl:value-of select="false()"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="msml2mtg:isLowerScaleThan($scale1,$finerScale[1])"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:template name="createMTGDatatree">
		<xsl:param name="actualNode" as="node()"/>
		<xsl:param name="alreadyProcessedNodesIDs" as="xs:string*"/>
		<xsl:sequence select="msml2mtg:getNodename($actualNode)"/>
		<xsl:variable name="actualAlreadyProcessedNodesIDs" select="($actualNode/@id,$alreadyProcessedNodesIDs)"/>
		<xsl:variable name="finerNode" as="node()?">
			<xsl:variable name="higherScale" select="msml2mtg:higherScale(msml2mtg:getScale($actualNode))"/>
			<xsl:if test="exists($higherScale)">
				<xsl:variable name="refinementedges" select="$msobject//msml:edge[@type='refinement'][@source=$actualNode/@id]"/>				
				<xsl:variable name="finerNodes" as="node()*">
					<xsl:for-each select="$refinementedges">
						<xsl:variable name="finerNode" select="$msobject//(msml:node|msml:msobject)[@id=current()/@target]"/>
						<xsl:if test="$msobject//msml:scale[(msml:node|msml:msobject)[@id=$finerNode/@id]]/@id=$higherScale/@id">
							<xsl:copy-of select="$finerNode"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:variable>
				<xsl:if test="exists($finerNodes)">
					<xsl:variable name="sortedFinerNodes" as="node()*" select="msml2mtg:sortNodes($finerNodes)"/>
					<xsl:sequence select="$sortedFinerNodes[1]"/>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
		<xsl:choose><!-- is node refineable? -->
			<xsl:when test="exists($finerNode)"><!-- refineable -->
				<xsl:sequence select="'/'"/>
				<xsl:call-template name="createMTGDatatree">
					<xsl:with-param name="actualNode" select="$finerNode"/>
					<xsl:with-param name="alreadyProcessedNodesIDs" select="$actualAlreadyProcessedNodesIDs"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise><!-- not refineable -->
				<xsl:call-template name="createMTGDatatree_hasDirectChildren">
					<xsl:with-param name="actualNode" select="$actualNode"/>
					<xsl:with-param name="alreadyProcessedNodesIDs" select="$actualAlreadyProcessedNodesIDs"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="createMTGDatatree_hasDirectChildren">
		<xsl:param name="actualNode" as="node()"/>
		<xsl:param name="alreadyProcessedNodesIDs" as="xs:string*"/>
		<xsl:variable name="orderedDirectChildren" as="node()*">
			<xsl:variable name="orderedEdges" as="node()*">
				<xsl:perform-sort select="$msobject//msml:edge[@type!='refinement'][@source=$actualNode/@id]">
					<xsl:sort select="@order" data-type="number"/>
				</xsl:perform-sort>
			</xsl:variable>
			<xsl:for-each select="$orderedEdges">
				<xsl:sequence select="$msobject//*[@id=current()/@target]"/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:choose><!-- has node direct children (successors/branches)? -->
			<xsl:when test="exists($orderedDirectChildren)"><!-- has direct children -->
				<xsl:call-template name="nonameyet2">
					<xsl:with-param name="nodes" select="$orderedDirectChildren"/>
					<xsl:with-param name="actualNode" select="$actualNode"/>
					<xsl:with-param name="alreadyProcessedNodesIDs" select="$alreadyProcessedNodesIDs"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise><!-- has no direct children -->
				<xsl:variable name="lowerScale" select="msml2mtg:lowerScale(msml2mtg:getScale($actualNode))"/>
				<xsl:if test="exists($lowerScale)">
					<xsl:variable name="lowerScaledNode" select="msml2mtg:getLowerScaledNode($actualNode,$lowerScale)"/>
					<!-- exists a lower scaled node for the actual node? -->
					<xsl:if test="exists($lowerScaledNode)">
						<xsl:call-template name="createMTGDatatree_hasDirectChildren">
							<xsl:with-param name="actualNode" select="$lowerScaledNode"/>
							<xsl:with-param name="alreadyProcessedNodesIDs" select="$alreadyProcessedNodesIDs"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>			
	</xsl:template>
	
	<xsl:template name="nonameyet2">
		<xsl:param name="nodes" as="node()*"/>
		<xsl:param name="actualNode" as="node()"/>
		<xsl:param name="alreadyProcessedNodesIDs" as="xs:string*"/>
		<xsl:if test="exists($nodes)">
			<xsl:variable name="node" select="$nodes[1]"/>
			<xsl:variable name="temp" as="xs:string*">
				<xsl:if test="not(msml2mtg:isNodeAlreadyProcessed($node,$alreadyProcessedNodesIDs))"><!-- is the direct children not already written in the MTG-Datatree? -->
					<xsl:call-template name="createMTGDatatree_isRefinementOfSameLowerScaledNodeAsActualNode">
						<xsl:with-param name="node" select="$node"/>
						<xsl:with-param name="actualNode" select="$actualNode"/>
						<xsl:with-param name="alreadyProcessedNodesIDs" select="$alreadyProcessedNodesIDs"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:variable>
			<xsl:if test="exists($temp)">
				<xsl:sequence select="$temp"/>
			</xsl:if>
			<xsl:variable name="actualAlreadyProcessedNodesIDs" as="xs:string*">
				<xsl:sequence select="$alreadyProcessedNodesIDs"/>
				<xsl:if test="exists($temp)">
					<xsl:for-each select="$temp">
						<xsl:if test="(current()!='&lt;')and(current()!='+')and(current()!='/')">
							<xsl:sequence select="msml2mtg:getNodeID(current())"/>
						</xsl:if>
					</xsl:for-each>	
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="nonameyet2">
				<xsl:with-param name="nodes" select="subsequence($nodes,2)"/>
				<xsl:with-param name="actualNode" select="$actualNode"/>
				<xsl:with-param name="alreadyProcessedNodesIDs" select="distinct-values($actualAlreadyProcessedNodesIDs)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="createMTGDatatree_isRefinementOfSameLowerScaledNodeAsActualNode">
		<xsl:param name="node" as="node()"/>
		<xsl:param name="actualNode" as="node()"/>
		<xsl:param name="alreadyProcessedNodesIDs" as="xs:string*"/>
		<xsl:variable name="lowerScale_node" select="msml2mtg:lowerScale(msml2mtg:getScale($node))"/>
		<xsl:choose>
			<xsl:when test="exists($lowerScale_node)">
				<xsl:variable name="lowerScaledNode_node" select="msml2mtg:getLowerScaledNode($node,$lowerScale_node)"/>
				<xsl:variable name="lowerScaledNode_actualNode" select="msml2mtg:getLowerScaledNode($actualNode,$lowerScale_node)"/>
				<xsl:choose><!-- is "node" a refinement of the same node of the lower scale, where "actualNode" is a refinement from? -->
					<xsl:when test="$lowerScaledNode_node/@id=$lowerScaledNode_actualNode/@id">
					<!-- "node" is a refinement of the same node of the lower scale, where "actualNode" is a refinement from -->
						<xsl:variable name="relationtype" select="$msobject//msml:edge[@type!='refinement'][@target=$node/@id]/@type"/>
						<xsl:choose>
							<xsl:when test="$relationtype='branch'">
								<xsl:sequence select="'+'"/>
							</xsl:when>
							<xsl:when test="$relationtype='successor'">
								<xsl:sequence select="'&lt;'"/>
							</xsl:when>
						</xsl:choose>
						<xsl:call-template name="createMTGDatatree">
							<xsl:with-param name="actualNode" select="$node"/>
							<xsl:with-param name="alreadyProcessedNodesIDs" select="$alreadyProcessedNodesIDs"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise><!-- "node" is not a refinement of the same node of the lower scale, where "actualNode" is a refinement from -->
						<xsl:variable name="orderedOrderEdges">
							<xsl:variable name="ordernum_edge_actualNode_node" select="$msobject//msml:edge[@source=$lowerScaledNode_actualNode/@id][@target=$lowerScaledNode_node/@id]/@order"/>
							<xsl:if test="exists($ordernum_edge_actualNode_node)">
								<xsl:perform-sort select="$msobject//msml:edge[@type!='refinement'][@source=$lowerScaledNode_actualNode/@id][@order][@order lt $ordernum_edge_actualNode_node]">
									<xsl:sort select="@order" data-type="number"/>
								</xsl:perform-sort>
							</xsl:if>
						</xsl:variable>
						<xsl:variable name="temp" as="xs:string*">
							<xsl:if test="exists($orderedOrderEdges)">
								<xsl:call-template name="nonameyet">
									<xsl:with-param name="edges" select="$orderedOrderEdges/msml:edge"/>
									<xsl:with-param name="alreadyProcessedNodesIDs" select="$alreadyProcessedNodesIDs"/>
								</xsl:call-template>
							</xsl:if>
						</xsl:variable>
						<xsl:sequence select="$temp"/>
						<xsl:variable name="actualAlreadyProcessedNodesIDs" as="xs:string*">
							<xsl:sequence select="$alreadyProcessedNodesIDs"/>
							<xsl:if test="exists($temp)">
								<xsl:for-each select="$temp">
									<xsl:if test="(current()!='&lt;')and(current()!='+')and(current()!='/')">
										<xsl:sequence select="msml2mtg:getNodeID(current())"/>
									</xsl:if>
								</xsl:for-each>	
							</xsl:if>
						</xsl:variable>
						<xsl:call-template name="createMTGDatatree_isRefinementOfSameLowerScaledNodeAsActualNode">
							<xsl:with-param name="node" select="$lowerScaledNode_node"/>
							<xsl:with-param name="actualNode" select="$actualNode"/>
							<xsl:with-param name="alreadyProcessedNodesIDs" select="distinct-values($actualAlreadyProcessedNodesIDs)"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="relationtype" select="$msobject//msml:edge[@type!='refinement'][@target=$node/@id]/@type"/>
					<xsl:choose>
						<xsl:when test="$relationtype='branch'">
							<xsl:sequence select="'+'"/>
						</xsl:when>
						<xsl:when test="$relationtype='successor'">
							<xsl:sequence select="'&lt;'"/>
						</xsl:when>
					</xsl:choose>
					<xsl:call-template name="createMTGDatatree">
						<xsl:with-param name="actualNode" select="$node"/>
						<xsl:with-param name="alreadyProcessedNodesIDs" select="$alreadyProcessedNodesIDs"/>
					</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="nonameyet">
		<xsl:param name="edges" as="node()*"/>
		<xsl:param name="alreadyProcessedNodesIDs" as="xs:string*"/>
		<xsl:if test="exists($edges)">
			<xsl:variable name="actualEdge" select="$edges[1]"/>
			<xsl:variable name="targetNode" select="$msobject//*[@id=$actualEdge/@target]"/>
			<xsl:variable name="temp" as="xs:string*">
				<xsl:if test="not(msml2mtg:isNodeAlreadyProcessed($targetNode,$alreadyProcessedNodesIDs))"><!-- is the node of the @target-attribut of the actual edge not already written in the MTG-Datatree? -->
					<xsl:choose>
						<xsl:when test="$actualEdge/@type='branch'">
							<xsl:sequence select="'+'"/>
						</xsl:when>
						<xsl:when test="$actualEdge/@type='successor'">
							<xsl:sequence select="'&lt;'"/>
						</xsl:when>
					</xsl:choose>
					<xsl:call-template name="createMTGDatatree">
						<xsl:with-param name="actualNode" select="$targetNode"/>
						<xsl:with-param name="alreadyProcessedNodesIDs" select="$alreadyProcessedNodesIDs"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:variable>
			<xsl:if test="exists($temp)">
				<xsl:sequence select="$temp"/>
			</xsl:if>
			<xsl:variable name="actualAlreadyProcessedNodesIDs" as="xs:string*">
				<xsl:sequence select="$alreadyProcessedNodesIDs"/>
				<xsl:if test="exists($temp)">
					<xsl:for-each select="$temp">
						<xsl:if test="(current()!='&lt;')and(current()!='+')and(current()!='/')">
							<xsl:sequence select="msml2mtg:getNodeID(current())"/>
						</xsl:if>
					</xsl:for-each>	
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="nonameyet">
				<xsl:with-param name="edges" select="subsequence($edges,2)"/>
				<xsl:with-param name="alreadyProcessedNodesIDs" select="distinct-values($actualAlreadyProcessedNodesIDs)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:function name="msml2mtg:isNodeAlreadyProcessed" as="xs:boolean">
		<xsl:param name="node" as="node()"/>
		<xsl:param name="alreadyProcessedNodesIDs" as="xs:string*"/>
		<xsl:value-of select="if (empty(index-of($alreadyProcessedNodesIDs,$node/@id)))
			then false()
			else true()"/>
	</xsl:function>
</xsl:transform>