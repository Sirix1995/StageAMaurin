package de.grogra.imp;

import java.util.Iterator;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.pf.ui.Context;

public class HierarchicalObjectInspector extends ObjectInspector {

	public HierarchicalObjectInspector(Context ctx, GraphManager graph) {
		super(ctx, graph);
	}
	
	@Override
	public void initialize() {
	}
	
	@Override
	public void buildTree() {
		Node graphRoot = graph.getRoot();
		rootNode = new TreeNode(graphRoot, null);
		visitNodes(graphRoot, rootNode);
	}
	
	private void visitNodes(Node graphNode, TreeNode treeNode) {
		for (Edge edge = graphNode.getFirstEdge(); edge != null; edge = edge.getNext(graphNode)) {
			if (!(edge.testEdgeBits(Graph.SUCCESSOR_EDGE) || edge.testEdgeBits(Graph.BRANCH_EDGE)))
				continue;
			Node targetGraphNode = edge.getTarget();
			if (targetGraphNode != graphNode) {
				if (filter == null) {
					TreeNode targetTreeNode = new TreeNode(targetGraphNode, treeNode);
					treeNode.addChild(targetTreeNode);
					visitNodes(targetGraphNode, targetTreeNode);
				}
				else {
					if (hierarchicFilter) {
						Iterator<NType> it = filter.iterator();
						boolean found = false;
						while (it.hasNext()) {
							NType type = it.next();
							if (type.isInstance(targetGraphNode)) {
								found = true;
								break;
							}
						}
						if (found) {
							TreeNode targetTreeNode = new TreeNode(targetGraphNode, treeNode);
							treeNode.addChild(targetTreeNode);
							visitNodes(targetGraphNode, targetTreeNode);
						}
						else {
							visitNodes(targetGraphNode, treeNode);
						}
					}
					else {
						if (filter.contains(targetGraphNode.getNType())) {
							TreeNode targetTreeNode = new TreeNode(targetGraphNode, treeNode);
							treeNode.addChild(targetTreeNode);
							visitNodes(targetGraphNode, targetTreeNode);
						}
						else {
							visitNodes(targetGraphNode, treeNode);
						}
					}
				}
			}
		}
	}

	
}
