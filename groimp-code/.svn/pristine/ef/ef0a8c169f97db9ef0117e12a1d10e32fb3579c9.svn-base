package de.grogra.imp3d.glsl.renderpass.nostencil;

import javax.media.opengl.GL;

import de.grogra.imp3d.Camera;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.renderpass.RenderPass;
import de.grogra.imp3d.glsl.utility.GLSLShader;

/**
 * Renderpass that renders a texture to opengls drawbuffer. Used to present the 
 * content of a framebufferobject to the user.  
 * @author Konni Hartmann
 */
public class PresentScenePass extends RenderPass {
	protected int getID() {
		return 6;
	}
	
	GLSLShader presentScene = null;
	
	class PresentSceneShader extends GLSLShader {

		public PresentSceneShader(OpenGLState glState) {
			super(glState);
		}

		final String[] sceneF = {
			"#version 110\n",
			"#extension GL_ARB_texture_rectangle : enable\n",
			"uniform sampler2DRect inputTex;",
			"void main() {",
			" vec4 color = texture2DRect(inputTex, gl_FragCoord.st);",
			"  gl_FragColor = clamp(vec4(color.rgb, 1.0), 0.0, 1.0);", 
			"}"
		};
		
		@Override
		protected String[] getFragmentShader(Object data) {
			return sceneF;
		}

		@Override
		protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
			int loc = gl.glGetUniformLocation(getShaderProgramNumber(), "inputTex");
			gl.glUniform1i(loc, 0);
		}

	};
	
	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		deactivateTextures(gl, 1);
		glState.setDepthMask(true);
		
		ViewPerspective(glState);	
		gl.glPopAttrib();

		glState.disable(OpenGLState.STENCIL_TEST);	
		
		glState.enable(OpenGLState.DEPTH_TEST);

	}

	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof Integer);
		
		glState.enable(OpenGLState.DEPTH_TEST);
		glState.setActiveProgram(0);
		renderVector(disp, glState.getWorldToView(), false, glState.deferredLabelRenderable);

		GL gl = glState.getGL();
		glState.setFBO(0);
		
		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
		gl.glClearColor(.5f, .5f, .5f, 0.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ZERO);		

		
		if(presentScene == null)
			presentScene = new PresentSceneShader(glState);
		
		presentScene.activateShader(glState, disp, disp);

		ViewOrtho(glState);
		glState.setDepthMask(false);

		// Add read input for HDR-Pass
		GLSLDisplay.printDebugInfoN("Presenting: " + (4 + glState.getFloatRT()));
		glState.getHDRFBO().bindAttachmentAsTexture(glState, glState.getFloatRT(), 0);
	}

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		Camera c = disp.getView3D().getCamera();
		drawPrjQuad(glState, c);
	}

}
