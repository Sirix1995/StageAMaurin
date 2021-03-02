package de.grogra.imp3d.glsl.light;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.light.shadow.GLSLShadowMap;
import de.grogra.imp3d.glsl.renderpass.RenderPass;

public class ShadowMapGenerationPass extends RenderPass {
	
@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
//		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
//		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		glState.setFaceCullingMode(GL.GL_BACK);
//		gl.glCullFace(GL.GL_NONE);
		// Bind Depthbuffer to FBO

		gl.glPopAttrib();
	}

	GLU glu = new GLU();
	
	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof GLSLShadowMap);
		GLSLShadowMap sMap = (GLSLShadowMap)data;
		GL gl = glState.getGL();
		
		/**
		 * Setup FBO to render to depth texture
		 */
		glState.getShadowFBO().bind(glState);
		gl.glReadBuffer(GL.GL_NONE);
		gl.glDrawBuffer(GL.GL_NONE);

		
//		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, 
//				GL.GL_DEPTH_ATTACHMENT_EXT, 
//				GL.GL_TEXTURE_2D, 
//				sMap.getIndex(), 0);
		GLSLDisplay.printDebugInfoN("Shadow Map: "+sMap);
//		glState.shadowFBO.attachDepthOnly(glState, sMap);
		
		// XXX: DEBUG
//		GLSLDisplay.printDebugInfo("ShadowMapFBO: ");
		glState.getShadowFBO().bind(glState);
//		glState.testFBO();
		
//		gl.glCullFace(GL.GL_FRONT);
				
		glState.setActiveProgram(0);

//		shadowShader.activateShader(gl, disp, data);

		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, sMap.getWidth(), sMap.getHeight());
		glState.disable(OpenGLState.STENCIL_TEST);
		glState.enable(OpenGLState.DEPTH_TEST);
	}

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof GLSLShadowMap);		
		((GLSLShadowMap)data).fill(disp, glState);
	}

}
