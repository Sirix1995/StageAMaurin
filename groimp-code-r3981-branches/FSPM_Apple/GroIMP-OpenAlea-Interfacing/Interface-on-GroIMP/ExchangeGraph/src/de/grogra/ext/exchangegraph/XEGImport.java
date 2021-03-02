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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import de.grogra.ext.exchangegraph.helpnodes.XEGUnknown;
import de.grogra.ext.exchangegraph.xmlbeans.GraphDocument;
import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.grammar.RecognitionException;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.imp.IMPWorkbench;
import de.grogra.pf.io.ReaderSourceImpl;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.TypeItem;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Workbench;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.rgg.RGGRoot;
import de.grogra.rgg.model.CompilationFilter;
import de.grogra.rgg.model.RGGFilter;
import de.grogra.rgg.model.XLFilter;
import de.grogra.util.MimeType;
import de.grogra.xl.util.BidirectionalHashMap;

public class XEGImport {

	protected Node rootNode;
	protected Reader graphReader;
	protected IOContext ctx;
	protected String xlCode;
	protected String modelName;
	
	protected Type<?>[] compiledTypes;
	
	 
	private IMPWorkbench workench;
	private Registry registry;

	/**
	 * Constructor called by GroIMP (Object->Insert File).
	 * 
	 * @throws IOException
	 */
	public XEGImport(Reader graphReader, Node rootNode, IOContext ctx, String xlCode, String modelName) {
		this.rootNode = rootNode;
		this.graphReader = graphReader;
		this.ctx = ctx;
		this.xlCode = xlCode;
		this.modelName = modelName;
		this.workench = de.grogra.rgg.Library.workbench();
		this.registry = workench.getRegistry();
	}

	
	//TODO Find out how to add new edge to finish the function
	@SuppressWarnings("unused")
	private GraphDocument ajustEdgeFromRootForImport(GraphDocument graphDocument) throws Exception {
		
		de.grogra.ext.exchangegraph.xmlbeans.Graph graph = graphDocument.getGraph();
		de.grogra.ext.exchangegraph.xmlbeans.Root root, groot;
		if (graph.getRootList().size() !=1)
			throw new Exception("The graph has too many roots!");
		else{
			 groot = graph.getRootList().get(0);
			 root = groot;
		}
		 List<de.grogra.ext.exchangegraph.xmlbeans.Edge> elist =  graph.getEdgeList();
		List<Long> addEdgeIds = new ArrayList<Long>();
		long changeTypeEdgeId = -1;
		 for (de.grogra.ext.exchangegraph.xmlbeans.Edge e: elist){
			 long destId = -1;
			 if (e.getSrcId() == root.getRootId()){
				 destId = e.getDestId();
			 }
			 List<de.grogra.ext.exchangegraph.xmlbeans.Edge> toEdges = new ArrayList<de.grogra.ext.exchangegraph.xmlbeans.Edge>();
			 for (de.grogra.ext.exchangegraph.xmlbeans.Edge ee: elist){
				 if (destId == ee.getDestId()){
					 toEdges.add(ee);
				 }
			 }
			 if (toEdges.size()==1){
				 if (root == groot){
				 changeTypeEdgeId = toEdges.get(0).getId();
				 }else{
					 final org.apache.xmlbeans.SchemaType stype = null;
					 //de.grogra.ext.exchangegraph.xmlbeans.Edge newedge
					 //graph.addNewEdge(newedge);
				 }
			 }
		 }
		return null;
		
	}
	
	
	public void doImport() throws IOException {
		// should not set rggRoot's name to others
		//rootNode.setName("XEGRoot");
		
		//if xlcode is not provided (xlcode = null), the imported graph will have the initial Axiom node connect from RGG root
		//it need to be removed
		this.rootNode.removeAll(null);
		
		GraphDocument graphDocument = null;
		
		// parse document and test for errors
		try {
			XmlOptions opt = new XmlOptions();
			opt.setLoadLineNumbers();
			graphDocument = GraphDocument.Factory.parse(graphReader, opt);
		} catch (XmlException e) {
			throw new IOException("XEG parsing error: "
					+ e.getCause().getMessage());
		}
		
		ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
		XmlOptions validationOptions = new XmlOptions();
		validationOptions.setErrorListener(validationErrors);
		boolean valid = graphDocument.validate(validationOptions);
		
		if (!valid) {
			StringBuffer sb = new StringBuffer();
			sb.append("XEG document not valid!");
			for (int i = 0; i < validationErrors.size(); i++) {
				XmlError error = (XmlError) validationErrors.get(i);
				sb.append("\n");
				sb.append("Message: " + error.getMessage());
				sb.append("Location: line "
						+ error.getLine() + "; "
						+ error.getCursorLocation().xmlText() + "\n");
			}
			throw new IOException(sb.toString());
		}
		// copy the graph to the groimp graph
		copyGraph(graphDocument);
		//registry.getLogger().log(workench.GUI_INFO, ExchangeGraphPlugin.I18N.msg("import is done!")); 
		registry.getLogger().log(workench.SOFT_GUI_INFO, ExchangeGraphPlugin.I18N.msg("import is done!")); 
		//System.err.println("import is done!");
	}

	@SuppressWarnings("rawtypes")
	private void copyGraph(GraphDocument graphDocument) throws IOException {
		// obtain the root element of the xml file
		de.grogra.ext.exchangegraph.xmlbeans.Graph graph = graphDocument
				.getGraph();
		
		// create groimp node types for type declarations <-- XEG converted from MAppleT files doesn't contain any additional type, 
		// additional type means the extended type. When XEG is imported to a GroIMP graph, 
		// the type extensions need to be added to the GroIMP graph if there are modules defined in XL rules
 		
		List<String> xegtypeStrings = new ArrayList<String>();
		for (de.grogra.ext.exchangegraph.xmlbeans.Type type : graph
				.getTypeList()){
			xegtypeStrings.add(type.getName());
		}
		
		HashMap<String, Type> tempTypes = getGroimpNodeTypes();
		HashMap<String, Type> additionalNodeTypes;
		
		if (xlCode != null){
			if (modelName.equals("RemoteModel")){
				additionalNodeTypes = createGroimpNodeTypes(graph);
			}else{
				boolean containTypes = true;				
				for (String st : xegtypeStrings){
					if(!tempTypes.keySet().contains(st))
						containTypes = false;
				}
				if (containTypes == true)
					additionalNodeTypes = getGroimpNodeTypes();
				else{
					additionalNodeTypes = createGroimpNodeTypes(graph);
				}
			}
		}else{
			additionalNodeTypes = createGroimpNodeTypes(graph);
		}
 		

		// read all node declarations and put them into a hashmap with id as key
		BidirectionalHashMap<Long, Node> nodeMap = this.ctx.getNodeMap();
		BidirectionalHashMap<Long, Edge> edgeMap = this.ctx.getEdgeMap();
		
		for (de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode : graph.getNodeList()) {

			Thread th = new Thread(new Thread() {
				public void run() {
					long id = xmlNode.getId();
					String typeName = xmlNode.isSetType() ? xmlNode.getType() : "Node";
					Node groimpNode = null;
					try {
						groimpNode = createGroimpNode(typeName, xmlNode, additionalNodeTypes);
					} catch (IOException e) {
						e.printStackTrace();
					}
					nodeMap.put(id, groimpNode);
				}
			});
			th.start();
			try {
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//registry.getLogger().log(workench.GUI_INFO, ExchangeGraphPlugin.I18N.msg("import is ongoing, please be patient!")); 
		registry.getLogger().log(workench.SOFT_GUI_INFO, ExchangeGraphPlugin.I18N.msg("import is ongoing, please be patient!")); 
		//System.err.println("import is ongoing, please be patient!");
		
		// set the rgg id as the xeg root node by using xeg rootId
		long rootId = graph.getRootArray(0).getRootId();
		nodeMap.put(rootId, this.rootNode);	
		
		// (plantId, edge from Graph root to plantId) map 
		HashMap<Long, de.grogra.ext.exchangegraph.xmlbeans.Edge> fromRootDstIdEdgeMap = ctx.getFromRootDstIdEdgeMap();
		
		// put nodes into GroIMP graph, namely connect GroIMP nodes to RggRoot by new created edges
		// firstly, connect Graph root to plant root of each scale
		HashMap<Long, de.grogra.ext.exchangegraph.xmlbeans.Edge> fromRootNonDecompositionNodeIdEdgeMap = new HashMap<Long, de.grogra.ext.exchangegraph.xmlbeans.Edge>();
		Set<Long> intraScaleDestNodeIds = new HashSet<Long>();
		
		for (de.grogra.ext.exchangegraph.xmlbeans.Edge xmlEdge : graph.getEdgeList()) {
			
			Thread th = new Thread(new Thread(){
		        public void run(){
		        	
					long srcId = xmlEdge.getSrcId();
					String xmlEdgeType = xmlEdge.getType().toLowerCase();
							
					if (srcId == rootId){
						long dstId = xmlEdge.getDestId();
						fromRootDstIdEdgeMap.put(dstId, xmlEdge);
						
						if (!xmlEdgeType.equals("decomposition")){
							fromRootNonDecompositionNodeIdEdgeMap.put(dstId, xmlEdge);
						}
					}
					
					//////////////////////
					if ("successor".equals(xmlEdge.getType().toLowerCase()) || "branch".equals(xmlEdge.getType().toLowerCase())){
						intraScaleDestNodeIds.add(xmlEdge.getDestId());
					}
					//////////////////////

					long dstId = xmlEdge.getDestId();
					Node srcNode = nodeMap.get(srcId);
					
					if (srcId != rootId) { 
						Node dstNode = nodeMap.get(dstId);
						int edgeBit = getEdgeBit(xmlEdgeType);
						Edge e = srcNode.getOrCreateEdgeTo(dstNode);
						e.addEdgeBits(edgeBit, null);
						edgeMap.put(xmlEdge.getId(), e);
					}	
		        } 
		    });
			
		    th.start();
		    try {
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Collection<Long> nodeIds = nodeMap.getKeyMap();
		Collection<Long> plantRootNodeIds = new HashSet<Long>();
		
		for(long nid: nodeIds){
			plantRootNodeIds.add(nid);
		}
		
		plantRootNodeIds.remove(rootId);
		plantRootNodeIds.removeAll(intraScaleDestNodeIds);
		
		Node rootNd = nodeMap.get(rootId);

		if (fromRootNonDecompositionNodeIdEdgeMap.keySet().size() == 0) {

			if (plantRootNodeIds.size() == 1){
				int edgeBit = getEdgeBit("successor");
				Edge e = rootNd.getOrCreateEdgeTo(nodeMap.get(plantRootNodeIds.iterator().next()));
				e.addEdgeBits(edgeBit, null);
			}else{
				
				for(long xmlPlantRootNodeId : plantRootNodeIds) {
					int edgeBit = getEdgeBit("branch");
					Edge e = rootNd.getOrCreateEdgeTo(nodeMap.get(xmlPlantRootNodeId));
					e.addEdgeBits(edgeBit, null);
				}
			}
		}else{
			// normally this case will not happen.
			for (Iterator<de.grogra.ext.exchangegraph.xmlbeans.Edge> iterator = fromRootNonDecompositionNodeIdEdgeMap.values().iterator(); iterator.hasNext();){
				de.grogra.ext.exchangegraph.xmlbeans.Edge fromRootNonDecompositionEdge = iterator.next();
				String xmlEdgeType = fromRootNonDecompositionEdge.getType().toLowerCase();
				int edgeBit = getEdgeBit(xmlEdgeType);
				Node dstNode = nodeMap.get(fromRootNonDecompositionEdge.getDestId());
				Edge e = rootNd.getOrCreateEdgeTo(dstNode);
				e.addEdgeBits(edgeBit, null);
			}
		}

	}


	/**
	 * Add the type graph to adapted groimp graph, so when exporting (node/edge map will be used), 
	 * no need to remove it from XEG graph. Also, add typegraph to XEG graph need to add new xmlEdge, 
	 * and srcid and dstid need to be set.
	 * 
	 * @param graph
	 * @param scaleTypesMap
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void addTypegraph(de.grogra.ext.exchangegraph.xmlbeans.Graph graph,
			LinkedHashMap<String, Set<String>> scaleTypesMap) throws IOException {

		long xmlEdgeIdMax = 0;
		for (de.grogra.ext.exchangegraph.xmlbeans.Edge xmlEdge : graph.getEdgeList()) {
			if (xmlEdgeIdMax < xmlEdge.getId())
				xmlEdgeIdMax = xmlEdge.getId();
		}

		
		// LinkedHashMap<String, Set<String>> scaleTypesMap = new
		// LinkedHashMap<String, Set<String>>();
		// getScaleTypesMap(xmlNodeId, graph, scaleTypesMap);
		

		Node preType = createGroimpNonGeoNode("TypeRoot", "TypeRoot");
		Node preScale = createGroimpNonGeoNode("SRoot", "SRoot");

		connectNodes(this.rootNode, preType, "decomposition");
		connectNodes(this.rootNode, preScale, "decomposition");

		for (String scaleName : scaleTypesMap.keySet()) {

			// add scale node and decomposition edge
			Node scaleNode = createGroimpNonGeoNode("ScaleClass", scaleName);
			connectNodes(preScale, scaleNode, "decomposition");

			// add type node and decomposition edge
			// Set<Node> typeNodeSet = new HashSet<Node>();
			ArrayList<Node> typeNodeList = new ArrayList<Node>();
			for (String typeName : scaleTypesMap.get(scaleName)) {
				Node typeNode = createGroimpNonGeoNode(typeName, typeName);
				typeNodeList.add(typeNode);
				connectNodes(preType, typeNode, "decomposition");
			}

			// add bidirectional successor and branch edges between type node
			for (Node typeNode : typeNodeList) {

				for (Node restNode : typeNodeList) {
					// no duplicated type, so index is unique
					if (typeNodeList.indexOf(restNode) == typeNodeList.indexOf(typeNode))
						continue;
					connectNodes(typeNode, restNode, "successor");
					connectNodes(typeNode, restNode, "branch");
				}
			}
		}
	}
	
	
	/**
	 * Creates GroIMP nodes for ScaleClass node in type graph.
	 * 
	 * @param typeName
	 * @return
	 */
	private Node createGroimpNonGeoNode(String typeName, String scaleName) throws IOException {

		Node node = null;
		// create instance
		try {
			if (IOContext.importNodeTypes.containsKey(typeName)) {
				node = (Node) Class.forName(IOContext.importNodeTypes.get(typeName)).newInstance();
			}
		} catch (Exception e) {
			System.out.println(typeName);
			e.printStackTrace();
		}
		// set name
		node.setName("Scale" + scaleName);

		return node;
	}
	
	
	private void connectNodes(Node srcNode, Node dstNode, 
			String edgeType){
		
		int edgeBit = getEdgeBit(edgeType);
		Edge e = srcNode.getOrCreateEdgeTo(dstNode);
		e.addEdgeBits(edgeBit, null);
	}

	
	
	@SuppressWarnings({ "rawtypes" })
	protected HashMap<String, Type> createGroimpNodeTypes(
			de.grogra.ext.exchangegraph.xmlbeans.Graph graph) {
		// create xl string with node declarations
		StringBuffer newTypesString = new StringBuffer();
		newTypesString.append("import de.grogra.imp3d.objects.*;");
		for (de.grogra.ext.exchangegraph.xmlbeans.Type type : graph
				.getTypeList()) {
			// do not create types for standard node types
			// (types which already exists in groimp)
			if (IOContext.importNodeTypes.containsKey(type.getName()))
				continue;

			newTypesString.append("public class ");
			newTypesString.append(type.getName());
			newTypesString.append(" extends ");
			String extendType = type.getExtends().getName();
			newTypesString.append(IOContext.importNodeTypes
					.containsKey(extendType) ? IOContext.importNodeTypes
					.get(extendType) : extendType);
			newTypesString.append("{");
			for (Property property : type.getPropertyList()) {
				newTypesString.append(property.getType() + " "
						+ property.getName() + ";");
			}
			newTypesString.append("};");
			
			/*newTypesString.append("module "+type.getName());
			
			
			String extendType = type.getExtends().getName();
			
			if (extendType.equals("Node")){
				
				newTypesString.append("(");
				List<Property> plist = type.getPropertyList();
				for (int i = 0; i<plist.size(); i++) {
					newTypesString.append(plist.get(i).getType() + " "
							+ plist.get(i).getName());
					if (i < (plist.size() - 1)){
						newTypesString.append(",");
					}
				}
				newTypesString.append(");");
				
			} else {

				newTypesString.append(" extends ");
				System.out.println("exxxtended type == "+extendType);
				newTypesString.append(IOContext.importNodeTypes
						.containsKey(extendType) ? IOContext.importNodeTypes
						.get(extendType) : extendType);
				
				newTypesString.append("(");
				for (Property property : type.getPropertyList()) {
					newTypesString.append(property.getType() + " "
							+ property.getName() + ",");
				}
				newTypesString.append(");");
			}
			
			//newTypesString.append("()");
			//newTypesString.append(";");
*/			
		}
		// compile xl string to get types
		try {
			XLFilter xlFilter = new XLFilter (null, new ReaderSourceImpl (new StringReader(newTypesString
					.toString()), "XEGNodeTypes", MimeType.TEXT_PLAIN, Registry.current (), null));
			
			CompilationFilter compilationFilter = new CompilationFilter (null, xlFilter);
			
			// compile optional xl code
			if (this.xlCode != null && !this.xlCode.trim().isEmpty()) {
				RGGFilter rggFilter = new RGGFilter(null, new ReaderSourceImpl(
						new StringReader(this.xlCode), this.modelName, MimeType.TEXT_PLAIN, Registry
								.current(), null));
				compilationFilter.addResource(rggFilter);
			}
			
			this.compiledTypes = compilationFilter.compile(null, null);			
		} catch (IOException e) {
			if (e.getCause() instanceof RecognitionException)
				System.err.println(((RecognitionException) e.getCause())
						.getDetailedMessage(false));
		} catch (Exception e) {
			System.err.println(e.getCause());
		}
		// create hashmap for xge type names and groimp types
		HashMap<String, Type> additionalNodeTypes = new HashMap<String, Type>();
		for (Type compiledType : this.compiledTypes) {
			// do not use getSimpleName, instead use getName without modelName
			String typeName = compiledType.getName();
			if ((modelName != null) && typeName.startsWith(modelName) && (typeName.length() > modelName.length()))
				typeName = typeName.substring(modelName.length() + 1, typeName.length());
			//System.out.println("typeName = " + typeName + ", compiledType = " + compiledType);
			additionalNodeTypes.put(typeName, compiledType);
		}
		return additionalNodeTypes;
	}
	
	
	@SuppressWarnings("rawtypes")
	private HashMap<String, Type> getGroimpNodeTypes() {

		Item classes = registry.getItem("/classes");
		HashMap<String, Type> additionalNodeTypes = new HashMap<String, Type>();
		
		ArrayList<Type> compiledTypeList = new ArrayList<Type>();;
		for (Node c = classes.getBranch(); c != null; c = c.getSuccessor()) {

			if (c instanceof TypeItem) {
				Object o = ((TypeItem) c).getObject();
				if (o instanceof de.grogra.reflect.Type) {

					de.grogra.reflect.Type t = (de.grogra.reflect.Type) o;
					
					
					compiledTypeList.add(t);
					
					//get compiledModel
					/*int iend = t.getName().indexOf(".");
					String subString = null;
					if (iend != -1) {
						subString = t.getName().substring(0 , iend); 
					}
					
					if (subString == null){
						compiledModel = t;
					}*/
					
					
                    //get compiled node types
					if (Reflection.isSuperclassOrSame(Node.class, t)
							&& !t.getSupertype().getSimpleName().equals("RGG")) {

						additionalNodeTypes.put(t.getSimpleName(), t);

					}
				}
			}
		}
		
		//get compiledTypes, e.g. (Model, Model.A, Model.A.Pattern, etc.)
		Iterator<Type> ti = compiledTypeList.iterator();
		compiledTypes = new Type[compiledTypeList.size()];
		
 		for (int i=0; i< compiledTypeList.size(); i++){
 			
 			compiledTypes[i] = ti.next();
 		}			
		
		return additionalNodeTypes;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unused" })
	private HashMap<String, HashMap<Type, Object>> getTypeFields(de.grogra.reflect.Type t){
		
		int modulefieldCount = t.getDeclaredFieldCount();

		HashMap<String, HashMap<Type, Object>> fntv = new HashMap<String, HashMap<Type, Object>>();
		
		for (int i = 0; i < modulefieldCount; i++) {
			Type fieldType = t.getDeclaredField(i).getType();
			if (!fieldType.getSimpleName().equals("NType")) {
				String fieldName = t.getDeclaredField(i).getSimpleName();
				Object fieldValue = t.getDefaultElementValue(fieldName);
				HashMap<Type, Object> fTypeValue = new HashMap<Type, Object>();
				fTypeValue.put(fieldType, fieldValue);
				fntv.put(fieldName, fTypeValue);
			}
		}
		
		return fntv;
	}
	
	
	/**
	 * Creates GroIMP nodes for specified type.
	 * 
	 * @param typeName
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Node createGroimpNode(String typeName,
			de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode,
			HashMap<String, Type> additionalNodeTypes) throws IOException {

		Node node = null;

		// create instance
		
		try {
			if (IOContext.importNodeTypes.containsKey(typeName)) {
				node = (Node) Class.forName(
						IOContext.importNodeTypes.get(typeName)).newInstance();
			} else {
				node = (Node) additionalNodeTypes.get(typeName).newInstance();
			}
		} catch (Exception e) {
			//System.out.println(typeName);
			e.printStackTrace();
		}

		// set properties
		List<Property> properties = xmlNode.getPropertyList();
		List<Property> handledProperties = new ArrayList<Property>(properties
				.size());
		Class nodeClass = node.getClass();

		try {
			// loop over hierarchy to set standard properties
			do {
				if (IOContext.xegNodeTypes.containsKey(nodeClass)) {
					Class xegClass = IOContext.xegNodeTypes.get(nodeClass);
					Method m = xegClass.getMethod("handleImportProperties",
							Node.class, List.class, List.class);
					m.invoke(null, node, properties, handledProperties);

					// remove set attributes
					for (Property p : handledProperties)
						properties.remove(p);
					handledProperties.clear();
				}
				// continue with superclass
				nodeClass = nodeClass.getSuperclass();
			} while (nodeClass != Object.class);

			// now set unknown properties (the rest in die properties list)
			XEGUnknown.handleImportProperties(node, properties);	
			
		} catch (Exception e) {
			// find root node of scene
			Node rootNode = UI.getRootOfProjectGraph(Workbench.current());
			if (rootNode != null){
			// then find the rggRoot
				if (rootNode.getFirstEdge() == null){
					RGGRoot rggRoot = new RGGRoot();
					rootNode.addEdgeBitsTo(rggRoot, de.grogra.graph.Graph.BRANCH_EDGE, null);
				}
			}else{
				System.out.println("rootNode is null!!!");;
			}
			throw new IOException("Couldn't create GroIMP node: " + e.getMessage());
		}

		// set name
		if (xmlNode.isSetName()	&& (!xmlNode.getName().trim().equals("")))
			node.setName(xmlNode.getName());

		return node;
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

	public Node getRootNode() {
		return rootNode;
	}

	public Type<?>[] getCompiledTypes() {
		return compiledTypes;
	}


	
	
}