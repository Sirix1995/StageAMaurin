package de.grogra.animation.handler;

import de.grogra.graph.impl.GraphManager;
import de.grogra.persistence.ManageableType;
import de.grogra.pf.ui.Workbench;

public abstract class PropertyHandler extends Handler {

	public PropertyHandler(GraphManager graph, Workbench wb) {
		super(graph, wb);
	}
	
	@Override
	public void addEdgeBits(int time, Object source, Object target, int mask) {
		// not provided
	}

	@Override
	public void removeEdgeBits(int time, Object source, Object target, int mask) {
		// not provided
	}

	@Override
	public void makeTransient(int time, Object node, ManageableType type) {
		// not provided
	}
	
}
