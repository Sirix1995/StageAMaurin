package de.grogra.imp3d.glsl.renderable;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;

public class GLSLNullRenderable extends GLSLRenderable {

	Renderable shape;
	Object state;
	boolean asNode;
	
	@Override
	public void draw(OpenGLState glState, RenderState rs) {
		shape.draw(state, asNode, rs);
	}

	@Override
	public Class<?> instanceFor() {
		return null;
	}

	@Override
	public void updateInstance(Object reference, Object state, boolean asNode, GraphState gs) {
		this.shape = (Renderable)reference;
		this.state = state;
		this.asNode = asNode;
	}
	
	@Override
	public GLSLRenderable getInstance() {
		return new GLSLNullRenderable();
	}

}
