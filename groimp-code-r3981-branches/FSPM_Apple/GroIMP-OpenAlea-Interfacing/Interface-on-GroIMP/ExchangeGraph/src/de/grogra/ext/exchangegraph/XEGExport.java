/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.ext.exchangegraph;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.XmlOptions;

import de.grogra.ext.exchangegraph.helpnodes.XEGUnknown;
import de.grogra.ext.exchangegraph.xmlbeans.ExtendsType;
import de.grogra.ext.exchangegraph.xmlbeans.GraphDocument;
import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.ext.exchangegraph.xmlbeans.Root;
import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.imp.IMPWorkbench;
import de.grogra.persistence.ManageableType;
import de.grogra.pf.registry.Registry;
import de.grogra.reflect.Reflection;
import de.grogra.xl.util.BidirectionalHashMap;

public class XEGExport {

	private IOContext ctx;
	private Node startNode;

	public XEGExport(Node startNode, IOContext ctx) {

		this.ctx = ctx;
		this.startNode = startNode;
	}
	
	
	public String doExport() throws Exception {
		GraphDocument graphDocument = extractGraph(startNode);
		
		//GraphDocument adjustedGD = ajustEdgeFromRootForExport(graphDocument);

		XmlOptions xmlOptions = new XmlOptions();
		xmlOptions.setSavePrettyPrint();
		xmlOptions.setSavePrettyPrintIndent(2);
		IMPWorkbench workench = de.grogra.rgg.Library.workbench();
		Registry registry = workench.getRegistry();
		registry.getLogger().log(workench.SOFT_GUI_INFO, ExchangeGraphPlugin.I18N.msg("export is done!")); 
		//System.err.println("export is done!");

		return graphDocument.xmlText(xmlOptions);
	}
	

	/**
	 * Parse the scene graph and returns new xml graph. Type definition of nodes
	 * are taken from old xml graph.
	 * 
	 * @param rggRoot
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	private GraphDocument extractGraph(Node root) {
		GraphDocument graphDocument = GraphDocument.Factory.newInstance();
		de.grogra.ext.exchangegraph.xmlbeans.Graph graph = graphDocument
				.addNewGraph();

		final Set<String> types = new HashSet<String>();
		BidirectionalHashMap<Integer, String> edgeTypes = ctx.getEdgeTypes();
		BidirectionalHashMap<Long, Node> nodeMap = ctx.getNodeMap();
		
		//during import process, edges not from root are stored in edgeMap
		BidirectionalHashMap<Long, Edge> edgeMap = ctx.getEdgeMap();
		
		//during import process, edges from root are stored in fromRootDstIdEdgeMap
		HashMap<Long, de.grogra.ext.exchangegraph.xmlbeans.Edge> fromRootDstIdEdgeMap = ctx.getFromRootDstIdEdgeMap();

		// walk through the graph and find all nodes, will be stored in the set
		final Set<Node> groimpNodes = new HashSet<Node>();
		visitNodes(groimpNodes, root);

		// calculate start node id
		long actNodeId = 0;
		Collection<Long> nids = nodeMap.getKeyMap();
		for (Long l : nids) {
			if (l > actNodeId){
				actNodeId = l;
			}
		}
		actNodeId++;

		// calculate start edge id
		long actEdgeId = 0;
		for (Long l : edgeMap.getKeyMap()) {
			if (l > actEdgeId)
				actEdgeId = l;
		}
		
		for (de.grogra.ext.exchangegraph.xmlbeans.Edge  fromRootEdge : fromRootDstIdEdgeMap.values()) {
			if (fromRootEdge.getId() > actEdgeId)
				actEdgeId = fromRootEdge.getId();
		}
		
		actEdgeId++;
		
		// !!! This original id restore process will be put on the Openalea side
		// get the original node id from node name (in groalea, node name have been designed in pattern: nodeType.nodeId) 
		// to allow MAppleT to get the updated property value using the same id in MTG
		// If it's not the MAppleT retroaction, which means the graph structure has been modified, then the node will have no name
		// so its id will be assigned normally

		// own loop to assign nodes with its corresponding id or a new id
		// important to access when set edges

		// own loop to assign nodes with its corresponding id or a new id
		// important to access when set edges		
		for (Node groimpNode : groimpNodes) {
			if (!nodeMap.containsValue(groimpNode) && groimpNode != root) {
				nodeMap.put(actNodeId++, groimpNode);
			}
		}
	
		// remove rggRoot node
		groimpNodes.remove(root);
		//add new root to graph and set its id to 0
		Root xmlRoot = graph.addNewRoot();
		long rootId = nodeMap.getKey(root) == null ? 0 : nodeMap.getKey(root);
		xmlRoot.setRootId(rootId);

		// now handle every node
		for (Node groimpNode : groimpNodes) {

			// write type to xml document
			String type = writeType(types, groimpNode, graph);

			// write node to xml document
			de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode = graph
					.addNewNode();
			xmlNode.setId(nodeMap.getKey(groimpNode));
			xmlNode.setName(groimpNode.getName());
			xmlNode.setType(type);
			
			// write standard node properties
			List<Class> unknownTypes = new ArrayList<Class>(); 
			boolean unknownType = true;
			Class<?> nodeClass = groimpNode.getClass();
			List<Class> handledClasses = new ArrayList<Class>();
			try {
				// loop over hierarchy to set standard properties
				do {
					if (IOContext.xegNodeTypes.containsKey(nodeClass)) {
						Class<?> xegClass = IOContext.xegNodeTypes.get(nodeClass);
						if (!handledClasses.contains(xegClass)) {
							Method m = xegClass.getMethod("handleExportProperties", Node.class, de.grogra.ext.exchangegraph.xmlbeans.Node.class);
							m.invoke(null, groimpNode, xmlNode);
							unknownType = false;
							handledClasses.add(xegClass);
						}
					}
					else {
						if (unknownType)
							unknownTypes.add(nodeClass);
					}
					// continue with superclass
					nodeClass = nodeClass.getSuperclass();
				} while (nodeClass != Object.class);
			} catch (Exception e) {e.printStackTrace();}

			// write unknown property values of the node
			XEGUnknown.handleExportProperties(groimpNode, xmlNode, unknownTypes);

			// write edges to xml document
			for (Edge groimpEdge = groimpNode.getFirstEdge(); groimpEdge != null; groimpEdge = groimpEdge
					.getNext(groimpNode)) {
				
				Node targetNode = groimpEdge.getTarget();
				
				if (targetNode == groimpNode) {
					Node srcNode = groimpEdge.getSource();
					long dstId = nodeMap.getKey(targetNode);
					long edgeId;
					
					if (edgeMap.containsValue(groimpEdge)){
						edgeId = edgeMap.getKey(groimpEdge);
					}else if (fromRootDstIdEdgeMap.keySet().contains(dstId)){
						edgeId = fromRootDstIdEdgeMap.get(dstId).getId();
					}else{
						edgeId = actEdgeId++;
					}

					// write only incoming edges to avoid duplicates
					int edgeBits = groimpEdge.getEdgeBits();
					
					if (srcNode.equals(root)){
						
						boolean singleToTargetEdge = true;
						
						for (Edge groimpEdgee = groimpNode.getFirstEdge(); groimpEdgee != null; groimpEdgee = groimpEdgee
								.getNext(groimpNode)) {
							
							if (groimpEdgee.getTarget().equals(targetNode) && !groimpEdgee.getSource().equals(root)){
								singleToTargetEdge = false;
								break;
							}	
						}
						
						if (singleToTargetEdge){
							
							boolean isMultiScale = false;
							
							for (Edge groimpEdgee = groimpNode.getFirstEdge(); groimpEdgee != null; groimpEdgee = groimpEdgee
									.getNext(groimpNode)) {
								if (groimpEdgee.getSource().equals(targetNode) &&
									(groimpEdgee.getEdgeBits() & Graph.REFINEMENT_EDGE) == Graph.REFINEMENT_EDGE){
									isMultiScale = true;
									break;
								}
							}
							
							if(isMultiScale){
								addXmlEdge(getEdgeBit("decomposition"), rootId, dstId, edgeId, edgeTypes, graph);
								
							}else{
								Set<Node> subNodes = new HashSet<Node>();
								visitNodes(subNodes, targetNode);
								for(Node subNode: subNodes){
									long subId = nodeMap.getKey(subNode);
									
									if (fromRootDstIdEdgeMap.keySet().contains(subId)){
										edgeId = fromRootDstIdEdgeMap.get(subId).getId();
									}else{
										edgeId = actEdgeId++;
									}
									
									addXmlEdge(getEdgeBit("decomposition"), rootId, subId, edgeId, edgeTypes, graph);
								}
							}
						}else{
							// do nothing
						}		
					}else{
						long srcId = nodeMap.getKey(srcNode);
						addXmlEdge(edgeBits, srcId, dstId, edgeId, edgeTypes, graph);
					}

				}
			}
		}

		// graphDocument.validate();
		return graphDocument;
	}
	
	
	private void addXmlEdge(int edgeBits, long srcId, long dstId, long edgeId, BidirectionalHashMap<Integer, String> edgeTypes, de.grogra.ext.exchangegraph.xmlbeans.Graph graph){
		if ((edgeBits & Graph.SUCCESSOR_EDGE) == Graph.SUCCESSOR_EDGE) {
			de.grogra.ext.exchangegraph.xmlbeans.Edge xmlEdge = graph
					.addNewEdge();
			xmlEdge.setId(edgeId);
			xmlEdge.setSrcId(srcId);
			xmlEdge.setDestId(dstId);
			xmlEdge.setType("successor");
		}
		if ((edgeBits & Graph.BRANCH_EDGE) == Graph.BRANCH_EDGE) {
			de.grogra.ext.exchangegraph.xmlbeans.Edge xmlEdge = graph
					.addNewEdge();
			xmlEdge.setId(edgeId);
			xmlEdge.setSrcId(srcId);
			xmlEdge.setDestId(dstId);
			xmlEdge.setType("branch");
		}
		
		if ((edgeBits & Graph.REFINEMENT_EDGE) == Graph.REFINEMENT_EDGE) {
			de.grogra.ext.exchangegraph.xmlbeans.Edge xmlEdge = graph
					.addNewEdge();
			xmlEdge.setId(edgeId);
			xmlEdge.setSrcId(srcId);
			xmlEdge.setDestId(dstId);
			xmlEdge.setType("decomposition");
		}
		
		for (int i = 0; i < 15; i++) {
			if (((edgeBits >> i) & Graph.MIN_UNUSED_EDGE) == Graph.MIN_UNUSED_EDGE) {
				de.grogra.ext.exchangegraph.xmlbeans.Edge xmlEdge = graph
						.addNewEdge();
				xmlEdge.setId(edgeId);
				xmlEdge.setSrcId(srcId);
				xmlEdge.setDestId(dstId);
				String edgeType = edgeTypes.get(i);
				if (edgeType == null)
					edgeType = "EDGE_" + String.valueOf(i);
				xmlEdge.setType(edgeType);
			}
		}
	}

	private String writeType(Set<String> types, Node groimpNode,
			de.grogra.ext.exchangegraph.xmlbeans.Graph graph) {

		NType nodeType = groimpNode.getNType();
		Class<?> nodeClass = groimpNode.getClass();

		String type = getTypeForNodeClass(nodeClass);
		String resultType = new String(type);

		// write type to xml document if not already exists
		while (!types.contains(type)) {
			
			// do not add standard node types
			if (IOContext.importNodeTypes.containsKey(type))
				break;

			// add type to xml file
			types.add(type);
			de.grogra.ext.exchangegraph.xmlbeans.Type xmlType = graph
					.addNewType();
			xmlType.setName(type);

			// set properties to type
			int fieldCount = nodeType.getManagedFieldCount();
			for (int i = 0; i < fieldCount; i++) {
				ManageableType.Field mf = nodeType.getManagedField(i);
				if ((mf.getDeclaringType() == nodeType)
						&& (Reflection.isPrimitiveOrString(mf.getType()))) {
					Property xmlProperty = xmlType.addNewProperty();
					xmlProperty.setName(mf.getSimpleName());
					xmlProperty.setType(mf.getType().getSimpleName());
				}
			}

			// find out if super class needs to be written to xml file
			if (!IOContext.exportNodeTypes.containsKey(nodeClass.getName())) {
				ExtendsType extend = xmlType.addNewExtends();
				nodeClass = nodeClass.getSuperclass();
				nodeType = (NType) nodeType.getManageableSupertype();
				type = getTypeForNodeClass(nodeClass);
				extend.setName(type);
			}
		}
		return resultType;
	}

	private String getTypeForNodeClass(Class<?> nodeClass) {
		String className = nodeClass.getName();
		if (className.indexOf("$") != -1)
			className = className.substring(className.indexOf("$") + 1);
		String type = IOContext.exportNodeTypes.get(nodeClass.getName());
		if (type == null) {
			type = nodeClass.getSimpleName();
		}
		return type;
	}

	/**
	 * Put node into the set and visit child nodes.
	 * 
	 * @param set
	 * @param node
	 */
	private void visitNodes(Set<Node> set, Node node) {
		String simpleNodeType = node.getManageableType().getSimpleName().toString();
		if (!set.contains(node)) {
			set.add(node);
			for (Edge edge = node.getFirstEdge(); edge != null; edge = edge.getNext(node)) {
				if (simpleNodeType.equals("RGGRoot")){
					if ((edge.getEdgeBits() & Graph.REFINEMENT_EDGE) != Graph.REFINEMENT_EDGE){
						visitNodes(set, edge.getTarget());
					}
				}else{
					visitNodes(set, edge.getTarget());
				}
			}
		}
	}
	
	/**
	 * Returns the GroIMP specific edge bits for a given xml edge type.
	 * 
	 * @param type
	 * @return
	 */
	protected int getEdgeBit(String type) {
		BidirectionalHashMap<Integer, String> edgeTypes = ctx.getEdgeTypes();

		if ("successor".equals(type))
			return de.grogra.graph.Graph.SUCCESSOR_EDGE;
		if ("branch".equals(type))
			return de.grogra.graph.Graph.BRANCH_EDGE;
		if ("decomposition".equals(type))
			return de.grogra.graph.Graph.REFINEMENT_EDGE;
		if (edgeTypes.containsValue(type)) {
			return edgeTypes.getKey(type);
		} else {
			int minEdge = edgeTypes.size();
			edgeTypes.put(minEdge, type);
			return de.grogra.graph.Graph.MIN_UNUSED_EDGE << minEdge;
		}
	}
	
	
	

}
