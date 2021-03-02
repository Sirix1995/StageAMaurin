package de.grogra.imp3d.glsl.renderable;

import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;

/**
 * Translation of all renderables implementing the interface Polygonizable. This
 * is done in order to assure face culling is consistent with the global face
 * culling mode.
 * 
 * @author Konni Hartmann
 */
public abstract class GLSLPolygonizable extends GLSLNullRenderable {

	@Override
	public void draw(OpenGLState glState, RenderState rs) {
		if (rs instanceof GLSLDisplay)
			drawAlt(glState, (GLSLDisplay) rs);
		else
			super.draw(glState, rs);
	}

	@Override
	public void drawAlt(OpenGLState glState, GLSLDisplay rs) {
		assert (shape instanceof Polygonizable);
		rs.drawPolygons((Polygonizable) shape, state, asNode, null, -1, false, null,
				glState.getFaceCullingMode(), glState
						.getState(OpenGLState.CULLING), true);
	}

	@Override
	public abstract Class<?> instanceFor();
}
