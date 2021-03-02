package de.grogra.imp3d.glsl.renderpass;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.utility.Drawable;
import de.grogra.imp3d.glsl.utility.DrawableContainer;

public class CachedRenderPass extends FullRenderPass {
	@Override
	protected int getID() {
		return 1;
	}

	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		gl.glPopAttrib();

//		glState.setDepthMask(true);
		glState.disable(OpenGLState.STENCIL_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
	}
	
	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {

		GL gl = glState.getGL();
		glState.getDeferredShadingFBO().drawBuffers(glState, 4);

		//XXX: TODO CONTINUE WORKING HERE
		
		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, glState.width, glState.height);
		
		// Setup Stencil Buffer!
		
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

		glState.enable(OpenGLState.DEPTH_TEST);
		
//		gl.glDepthFunc(GL.GL_ALWAYS);
		gl.glDepthFunc(GL.GL_EQUAL);
//		glState.setDepthMask(false);
		
		// This should only change 1st bit
		glState.enable(OpenGLState.STENCIL_TEST);
		gl.glStencilFunc(GL.GL_ALWAYS, 0x1, 0x1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
	}
	
	@Override
	public void postDrawCallback(Drawable dr, OpenGLState glState, GLSLDisplay disp) {
		dr.activateGLSLShader(glState, disp);
	}
	
	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert(data instanceof DrawableContainer);
		DrawableContainer cache = (DrawableContainer)data;
		
		renderVector(disp, glState.getWorldToView(), true, cache);
	}
}
