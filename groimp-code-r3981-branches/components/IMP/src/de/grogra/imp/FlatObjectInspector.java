package de.grogra.imp;

import java.util.Iterator;
import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.pf.ui.Context;

public class FlatObjectInspector extends ObjectInspector {

	public FlatObjectInspector(Context ctx, GraphManager graph) {
		super(ctx, graph);
	}
	
	@Override
	public void initialize() {
	}
	
	@Override
	public void buildTree() {
		Node graphRoot = graph.getRoot();
		rootNode = new TreeNode("Nodes", null);
		visitNodes(graphRoot, rootNode);
	}
	
	private void visitNodes(Node node, TreeNode rootNode) {
		if (filter != null) {
			if (hierarchicFilter) {
				Iterator<NType> it = filter.iterator();
				while (it.hasNext()) {
					NType type = it.next();
					if (type.isInstance(node)) {
						rootNode.addChild(new TreeNode(node, rootNode));
						break;
					}
				}
			}
			else {
				if (filter.contains(node.getNType()) && (node.getId() != 0L))
					rootNode.addChild(new TreeNode(node, rootNode));
			}
		}
		else if (node.getId() != 0L)
			rootNode.addChild(new TreeNode(node, rootNode));
		
		for (Edge edge = node.getFirstEdge(); edge != null; edge = edge.getNext(node)) {
			if (edge.getTarget() == node)
				continue;
			if (edge.testEdgeBits(Graph.SUCCESSOR_EDGE) ||  edge.testEdgeBits(Graph.BRANCH_EDGE))
				visitNodes(edge.getTarget(), rootNode);
		}
	}

}
