package de.grogra.animation.trackview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.imp.ObjectInspector;
import de.grogra.persistence.ManageableType;
import de.grogra.pf.ui.Context;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;

public class NodePropertyInspector extends ObjectInspector {

	private Set<Node> visitedNodes;
	
	public NodePropertyInspector(Context ctx, GraphManager graph) {
		super(ctx, graph);
	}
	
	@Override
	public void initialize() {
		visitedNodes = new HashSet<Node>();
	}	
	
	@Override
	public void buildTree() {
		Node graphRoot = graph.getRoot();
		rootNode = new TreeNode("Nodes", null);
		visitedNodes.clear();
		visitNodes(graphRoot, rootNode, visitedNodes);
	}

	private void visitNodes(Node node, TreeNode rootNode, Set<Node> visitedNodes) {
		//TODO: add nodes and their properties
		if (visitedNodes.contains(node))
			return;
		
		// add node
		TreeNode treeNode = new TreeNode(node, rootNode);
		rootNode.addChild(treeNode);
		visitedNodes.add(node);
		
		// add properties of node
		handleProperties(node, treeNode);
		
		// loop over children of node
		for (Edge edge = node.getFirstEdge(); edge != null; edge = edge.getNext(node)) {
			if (edge.getTarget() == node)
				continue;
			visitNodes(edge.getTarget(), rootNode, visitedNodes);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void handleProperties(Node node, TreeNode treeNode) {
		NType nodeType = node.getNType();
		int fieldCount = nodeType.getManagedFieldCount();
		
		for (int i = 0; i < fieldCount; i++) {
			ManageableType.Field mf = nodeType.getManagedField(i);
			
			//TODO: write properties in hierarchical manner (tvector3d -> x & y & z)
			Type pType = mf.getType();
			
			// filter out all fields which are not important
			if (Reflection.isPrimitiveOrString(mf.getType())) {
						
				TreeNode propertyNode = new TreeNode(mf, treeNode);
				treeNode.addChild(propertyNode);
				
			}
		}
	}


}
