package de.grogra.imp3d.glsl.renderable;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.utility.GLSLManagedShader;
import de.grogra.imp3d.shading.Shader;

public abstract class GLSLRenderable {
	
	public abstract Class<?> instanceFor();
	
	public abstract void updateInstance(Object reference, Object state, boolean asNode, GraphState gs);
	
	public abstract void draw(OpenGLState glState, RenderState rs);

	public void drawAlt(OpenGLState glState, GLSLDisplay rs) {
		draw(glState, rs);
	}

	public abstract GLSLRenderable getInstance();
	
	public boolean isShaderDependant(boolean depthonly){ return false; }
	public void activateShader(OpenGLState glState, GLSLDisplay disp, Shader shader, boolean depthonly) {}
	public GLSLManagedShader findShader(OpenGLState glState, GLSLDisplay disp, Shader shader) { return null; }
}
