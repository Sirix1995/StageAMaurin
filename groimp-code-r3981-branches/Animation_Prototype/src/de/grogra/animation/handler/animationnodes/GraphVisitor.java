package de.grogra.animation.handler.animationnodes;

import de.grogra.graph.GraphState;
import de.grogra.graph.Path;
import de.grogra.graph.Visitor;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.reflect.Type;

public class GraphVisitor implements Visitor {

	static interface Executor {
		public void execute(Node n, AnimationNode target, int time, Transaction t);
	}
	
	static class UpdateExecutor implements Executor {
		public void execute(Node n, AnimationNode target, int time, Transaction t) {
			PersistenceField property = target.getProperty();
			Object value = target.getValue(time);
			Type<?> type = property.getType(); 
			if (type.equals(Type.BOOLEAN))
				property.setBoolean(n, null, (Boolean) value, t);
			else if (type.equals(Type.BYTE))
				property.setByte(n, null, (Byte) value, t);
			else if (type.equals(Type.SHORT))
				property.setShort(n, null, (Short) value, t);
			else if (type.equals(Type.CHAR))
				property.setChar(n, null, (Character) value, t);
			else if (type.equals(Type.INT))
				property.setInt(n, null, (Integer) value, t);
			else if (type.equals(Type.LONG))
				property.setLong(n, null, (Long) value, t);
			else if (type.equals(Type.FLOAT))
				property.setFloat(n, null, (Float) value, t);
			else if (type.equals(Type.DOUBLE))
				property.setDouble(n, null, (Double) value, t);
			else
				property.setObject(n, null, value, t);			
		}
	}
	
	static class ClearExecutor implements Executor {
		public void execute(Node n, AnimationNode target, int time,
				Transaction t) {
			n.removeEdgeBitsTo(target, ANIM_EDGE, t);
		}
	}

	public static int ANIM_EDGE = AnimationNode.ANIM_EDGE;
	
	final private GraphManager graph;
	private int time = 0;
	private Executor executor = null;
	
	public GraphVisitor(GraphManager graph) {
		this.graph = graph;
	}
	
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public GraphState getGraphState() {
		//TODO: richtig so?
		return graph.getMainState();
	}

	public Object visitEnter(Path path, boolean node) {
		if (node) {
			Node n = (Node) path.getObject(path.getNodeAndEdgeCount() - 2);
			Transaction t = graph.getActiveTransaction();
			for (Edge e = n.getFirstEdge(); e != null; e = e.getNext(n)) {
				Node target = e.getTarget();
				if (e.testEdgeBits(ANIM_EDGE) && (target instanceof AnimationNode) && (target != n)) {
					executor.execute(n, (AnimationNode) target, time, t);
				}
			}
			t.commitAll();
		}
		return null;
	}

	public Object visitInstanceEnter() {
		return null;
	}

	public boolean visitInstanceLeave(Object o) {
		return true;
	}

	public boolean visitLeave(Object o, Path path, boolean node) {
		return true;
	}
	
}
