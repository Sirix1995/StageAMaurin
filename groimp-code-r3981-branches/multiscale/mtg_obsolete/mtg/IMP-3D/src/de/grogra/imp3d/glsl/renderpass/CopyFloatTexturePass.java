package de.grogra.imp3d.glsl.renderpass;

import javax.media.opengl.GL;

import de.grogra.imp3d.Camera;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.light.LightPos;
import de.grogra.imp3d.glsl.utility.GLSLShader;

/**
 * This class sets up primary transparency information and renders emissive materials which may need to be removed and worked into 
 * light shaders!
 * @author Konni Hartmann
 */
public class CopyFloatTexturePass extends FullRenderPass {

	GLSLShader cs = null;
		
	class CopyFloatTextureShader extends GLSLShader {
		
		public CopyFloatTextureShader(OpenGLState glState) {
			super(glState);
		}

		final String baseLightF[] = {
				"#version 110\n",
				"#extension GL_ARB_texture_rectangle : enable\n",
				"uniform sampler2DRect inputTex;\n",
				"void main() {\n", 
				"	gl_FragColor = texture2DRect(inputTex, gl_FragCoord.st);\n",
				"}" };

		@Override
		public String[] getFragmentShader(Object sh) {
			return baseLightF;
		}

		@Override
		public boolean needsRecompilation(Object data) {
			return false;
		}

		@Override
		public Class<?> instanceFor() {
			return null;
		}

		@Override
		public void setupShader(GL gl, GLSLDisplay disp, Object data) {
			int tex0 = gl
					.glGetUniformLocation(getShaderProgramNumber(), "inputTex");
			gl.glUniform1i(tex0, 0);
		}

		@Override
		public GLSLShader getInstance() {
			return this;
		}
	};

	
	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		glState.disable(OpenGLState.STENCIL_TEST);		

		glState.setDepthMask(true);
		glState.enable(OpenGLState.DEPTH_TEST);
		
		deactivateTextures(gl, 1);

		ViewPerspective(glState);		
		gl.glPopAttrib();
	}
	
	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {	
		GL gl = glState.getGL();

		GLSLDisplay.printDebugInfoN("Copying: " + (4 + glState.getFloatRT()) + " to "+ (4 + glState.getFloatRTLast()));
		glState.getHDRFBO().drawBuffer(glState, glState.getFloatRTLast());
//		GLSLDisplay.printDebugInfoN("Copying: " + (4 + glState.getFloatRTLast()) + " to "+ (4 + glState.getFloatRT()));
//		glState.HDRFBO.drawBuffer(glState, glState.getFloatRT());

		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, glState.width, glState.height);
		
		if(cs == null)
			cs = new CopyFloatTextureShader(glState);
		cs.activateShader(glState, disp, null);

		ViewOrtho(glState);
		glState.disable(OpenGLState.DEPTH_TEST);

		// Disable Write to Depth-Buffer
		glState.setDepthMask(false);

		glState.getHDRFBO().bindAttachmentAsTexture(glState, glState.getFloatRT(), 0);
//		glState.HDRFBO.bindAttachmentAsTexture(glState, glState.getFloatRTLast(), 0);
	}

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		Camera c = disp.getView3D().getCamera();
		drawPrjQuad(glState, c);
	}
}
