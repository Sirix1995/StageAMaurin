package de.grogra.imp3d.glsl.renderable;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.GLSLDisplay;

public abstract class GLSLInstanceableRenderable extends GLSLRenderable {

	public static final byte INSTANCIATED = -1;
	public static final byte INDIRECT = 0;
	public static final byte DIRECT = 1;

	private int update_type = 0;

	@Override
	public void updateInstance(Object reference, Object state,
			boolean asNode, GraphState gs) {

		if (reference == state) {
			if (gs.getInstancingPathIndex() <= 0) {
				update_type = DIRECT;
				updateInstanceDirect((Renderable)reference);
			} else {
				update_type = INSTANCIATED;
				updateInstanceByInstancing((Renderable)reference, gs);
			}
		} else {
			update_type = INDIRECT;
			updateInstanceIndirect(state, asNode, gs);
		}
		if(GLSLDisplay.DEBUG)
			GLSLDisplay.printDebugInfoN("REFERENCE: "+reference+" for OBJECT: "+state+" UPDATED! IS OF TYPE: "+update_type);
	}

	protected abstract void updateInstanceDirect(Renderable reference);

	protected abstract void updateInstanceByInstancing(Renderable reference,
			GraphState gs);

	protected abstract void updateInstanceIndirect(Object state,
			boolean asNode, GraphState gs);
}
