package de.grogra.imp3d.glsl.renderable;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;

import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.shading.Shader;

public abstract class GLSLFrustumBase extends GLSLAxis {
	
	public static final int BASE_OPEN_MASK = 1 << GLSLAxis.USED_BITS;
	public static final int TOP_OPEN_MASK = 2 << GLSLAxis.USED_BITS;

	public static final int USED_BITS = GLSLAxis.USED_BITS + 2;
	
	void drawFrustum (OpenGLState glState, RenderState rs, float height, float baseRadius, float topRadius,
			  boolean baseClosed, boolean topClosed,
			  float scaleV, Shader s, int highlight, boolean asWireframe, Matrix4d t) {
		boolean closed = (baseClosed || baseRadius == 0) && (topClosed || topRadius == 0);
		boolean culling = glState.getState(OpenGLState.CULLING);
		if(!closed)
//			glState.disable(OpenGLState.CULLING);
		glState.getGL().glDisable(GL.GL_CULL_FACE);
		rs.drawFrustum(height, baseRadius, topRadius, baseClosed, topClosed, scaleV, s, highlight, asWireframe, t);
		if(!closed && culling)
//			glState.enable(OpenGLState.CULLING);
		glState.getGL().glEnable(GL.GL_CULL_FACE);
	}

}
