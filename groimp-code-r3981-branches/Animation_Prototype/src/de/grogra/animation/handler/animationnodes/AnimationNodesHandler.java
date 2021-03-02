package de.grogra.animation.handler.animationnodes;

import java.util.Set;

import de.grogra.animation.AnimJob;
import de.grogra.animation.handler.PropertyHandler;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Workbench;
import de.grogra.vfs.FileSystem;

public class AnimationNodesHandler extends PropertyHandler {

	private static int ANIM_EDGE = GraphVisitor.ANIM_EDGE;
	
	final private GraphVisitor visitor;
	final private GraphVisitor.Executor clearExecutor;
	final private GraphVisitor.Executor updateExecutor;
	
	public AnimationNodesHandler(GraphManager graph, Workbench wb) {
		super(graph, wb);
		visitor = new GraphVisitor(graph);
		clearExecutor = new GraphVisitor.ClearExecutor();
		updateExecutor = new GraphVisitor.UpdateExecutor();
	}

	@Override
	public void clearValues() {
		// traverse scene graph, remove all animation nodes
		visitor.setExecutor(clearExecutor);
		graph.accept(graph.getRoot(), visitor, null);
	}

	@Override
	public void getTimes(Object node, Set<Integer> times) {
		if (node instanceof Node) {
			Node n = (Node) node;
			for (Edge e = n.getFirstEdge(); e != null; e = e.getNext(n)) {
				Node target = e.getTarget();
				if (e.testEdgeBits(ANIM_EDGE) && (target instanceof AnimationNode)
						&& (target != n)) {
					AnimationNode aniTarget = (AnimationNode) target;
					aniTarget.getTimes(times);
				}
			}
		}
	}

	@Override
	public void getTimesForProperty(Object node, PersistenceField property,
			Set<Integer> times) {
		if (node instanceof Node) {
			Node n = (Node) node;
			for (Edge e = n.getFirstEdge(); e != null; e = e.getNext(n)) {
				Node target = e.getTarget();
				if (e.testEdgeBits(ANIM_EDGE) && (target instanceof AnimationNode)
						&& (target != n)) {
					AnimationNode aniTarget = (AnimationNode) target;
					if (aniTarget.getProperty().equals(property))
						aniTarget.getTimes(times);
				}
			}
		}
	}

	@Override
	public boolean putValue(int time, Object node, PersistenceField property, Object value) {
		boolean result = false;
		if (node instanceof Node) {
			final Node n = (Node) node;
			AnimationNode animNode = getAnimationNodeForProperty(n, property);
			// no animation node yet for property
			if (animNode == null) {
				animNode = new AnimationNode();
				animNode.setProperty(property);
			}
			result = animNode.putValue(time, value);
			
			AnimJob job = new AnimJob(animNode, wb) {
				@Override
				protected void runImpl(Object arg, Context ctx) {
					Transaction t = graph.getActiveTransaction();
					n.addEdgeBitsTo((Node) arg, ANIM_EDGE, t);
					t.commitAll();
				}
			};
			job.execute();
		}
		return result;
	}
	
	@Override
	public void changeValue(int oldTime, int newTime, Object node,
			PersistenceField property, Object value) {
		if (node instanceof Node) {
			final Node n = (Node) node;
			AnimationNode animNode = getAnimationNodeForProperty(n, property);
			animNode.changeValue(oldTime, newTime, value);
		}
	}

	@Override
	public Object getValue(double time, Object node, PersistenceField property) {
		Object result = null;
		if (node instanceof Node) {
			final Node n = (Node) node;
			AnimationNode animNode = getAnimationNodeForProperty(n, property);
			result = animNode.getValue(time);
		}
		return result;
	}

	@Override
	public void update(int time) {
		// traverse scene graph, set all values to value at time
		visitor.setExecutor(updateExecutor);
		visitor.setTime(time);
		graph.accept(graph.getRoot(), visitor, null);
	}
	
	private AnimationNode getAnimationNodeForProperty(Node node, PersistenceField property) {
		AnimationNode result = null;
		
		for (Edge e = node.getFirstEdge(); e != null; e = e.getNext(node)) {
			Node target = e.getTarget();
			if (e.testEdgeBits(ANIM_EDGE) && (target instanceof AnimationNode)
					&& (target != node)) {
				AnimationNode aniTarget = (AnimationNode) target;
				if (aniTarget.getProperty().equals(property)) {
					result = aniTarget;
					break;
				}
			}
		}
		
		return result;
	}
	
	@Override
	public boolean saveData(FileSystem fs, int currentTime) {
		// not needed, animation nodes are saved by GroIMP
		return true;
	}

	@Override
	public boolean restoreData(FileSystem fs) {
		// not needed, animation nodes are restored by GroIMP
		return true;
	}

}
