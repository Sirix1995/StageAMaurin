package de.grogra.imp3d.glsl.renderpass;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;

import de.grogra.imp3d.Camera;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.shading.Light;
import de.grogra.vecmath.Math2;

/**
 * Simple ToneMapping to render results of the hd-lighting passes.
 * @author Konni Hartmann
 */
public class DrawSkyPass extends FullRenderPass {
	@Override
	protected int getID() {
		return 3;
	}

	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		glState.setDepthMask(true);
		ViewPerspective(glState);		
		
		gl.glPopAttrib();

		deactivateTextures(gl, 2, GL.GL_TEXTURE_RECTANGLE_ARB);	
	
		glState.enable(OpenGLState.DEPTH_TEST);
		glState.disable(OpenGLState.STENCIL_TEST);
	}

	
	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof Integer);
		
		GL gl = glState.getGL();
		GLSLDisplay.printDebugInfoN("Render BG to: " + (4+glState.floatRT));
		glState.getHDRFBO().drawBuffer(glState, glState.getFloatRT());

		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
		glState.disable(OpenGLState.STENCIL_TEST);
		glState.disable(OpenGLState.DEPTH_TEST);
				
		glState.enable(OpenGLState.STENCIL_TEST);
		gl.glStencilFunc(GL.GL_EQUAL, 0x0, 0x3);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);		

		Matrix4d ViewToWorld = new Matrix4d(glState.getWorldToView());
		ViewToWorld.m30 = 0;
		ViewToWorld.m31 = 0;
		ViewToWorld.m32 = 0;
		ViewToWorld.m03 = 0;
		ViewToWorld.m13 = 0;
		ViewToWorld.m23 = 0;
		ViewToWorld.invert();

		gl.glMatrixMode(GL.GL_TEXTURE);
		gl.glActiveTexture(GL.GL_TEXTURE0 + FullQualityRenderPass.CUSTOM_MATRIX_1);
		glState.loadMatrixd(ViewToWorld);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		
		
		glState.getHDRFBO().bindAttachmentAsTexture(glState, glState.getFloatRTLast(), 0);
		glState.getAlphaFBO().bindAttachmentAsTexture(glState, 0, 1);
		
		glState.setShaderConfSwitch( OpenGLState.SKY_MATERIAL );
		disp.findAndActivateShader(glState.getBGShader());
		glState.setShaderConfSwitch(OpenGLState.DEFAULT_MATERIAL);
		
		//		GLSLDisplay.printDebugInfoN("Radiant Power of BG is set to: " + glState.getBgPowerDensity() + " for Shader: "+glState.getActiveShader());


		ViewOrtho(glState);
				
		glState.setDepthMask(false);
	}

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		Camera c = disp.getView3D().getCamera();
		drawPrjQuad(glState, c);
	}

}
