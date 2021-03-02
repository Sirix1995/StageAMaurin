package de.grogra.imp3d.glsl.renderpass.nostencil;

import javax.media.opengl.GL;

import de.grogra.imp3d.Camera;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.renderpass.FullRenderPass;
import de.grogra.imp3d.glsl.utility.GLSLShader;

/**
 * Simple ToneMapping to render results of the hd-lighting passes.
 * 
 * @author Konni Hartmann
 */
public class DrawTranspBackgroundPass extends FullRenderPass {
	@Override
	protected int getID() {
		return 3;
	}

	GLSLShader presentCheckboardBG = null;

	class CheckBoardBGShader extends GLSLShader {

		public CheckBoardBGShader(OpenGLState glState) {
			super(glState);
		}

		final String[] checkboardF = {
				"#version 110\n",
				"#extension GL_ARB_texture_rectangle : enable\n",
				"uniform sampler2DRect inputTex;\n",
				"uniform sampler2DRect alphaTex;\n",

				"float checkBoard() {\n",
				" 	float tmp = (mod(floor(0.05* gl_FragCoord.x) + floor(0.05* gl_FragCoord.y),2.0));\n",
				"	return tmp*0.15 + 0.6;\n",
				"}\n",

				"void main() {\n",
				" vec4 lastCol = texture2DRect(inputTex, gl_FragCoord.st);\n",
				" vec4 alpha = texture2DRect(alphaTex, gl_FragCoord.st);\n",
				// " gl_FragColor = vec4(1, 0.0, 0.0, 1.0);\n",
				// " gl_FragColor = lastCol;\n",
				// " gl_FragColor = clamp(vec4(vec3((lastCol.a) * checkBoard()), 0.0) + lastCol, 0.0, 1.0);\n",
				" gl_FragColor = clamp(vec4(alpha.rgb, 1.0) * vec4(checkBoard()) + lastCol, 0.0, 1.0);\n",
				"}" };

		@Override
		protected String[] getFragmentShader(Object data) {
			return checkboardF;
		}

		@Override
		public GLSLShader getInstance() {
			return this;
		}

		@Override
		public Class instanceFor() {
			return null;
		}

		@Override
		public boolean needsRecompilation(Object data) {
			return false;
		}

		@Override
		protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
			int tex0 = gl.glGetUniformLocation(getShaderProgramNumber(),
					"inputTex");
			gl.glUniform1i(tex0, 0);
			int tex1 = gl.glGetUniformLocation(getShaderProgramNumber(),
					"alphaTex");
			gl.glUniform1i(tex1, 1);
		}

	};

	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		glState.setDepthMask(true);

		ViewPerspective(glState);
		gl.glPopAttrib();

		deactivateTextures(gl, 2);

		glState.enable(OpenGLState.DEPTH_TEST);
		glState.disable(OpenGLState.STENCIL_TEST);
	}

	CopyFloatTexturePass cftp = new CopyFloatTexturePass();

	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof Integer);

		GL gl = glState.getGL();
		glState.getHDRFBO().attachDepthStencil(glState, glState.getDepthRB());

		gl.glStencilFunc(GL.GL_GEQUAL, 0x1, 0x3);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
		glState.enable(OpenGLState.STENCIL_TEST);
		cftp.process(disp, glState, null);

		glState.switchFloatRT();

		GLSLDisplay.printDebugInfoN("Render Transp BG to: "
				+ (4 + glState.getFloatRT()));
		glState.getHDRFBO().drawBuffer(glState, glState.getFloatRT());
		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
		glState.disable(OpenGLState.DEPTH_TEST);

		// glState.disable(OpenGLState.STENCIL_TEST);
		glState.enable(OpenGLState.STENCIL_TEST);
		gl.glStencilFunc(GL.GL_EQUAL, 0x0, 0x3);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);

		glState.getHDRFBO().bindAttachmentAsTexture(glState, glState
				.getFloatRTLast(), 0);
		glState.getAlphaFBO().bindAttachmentAsTexture(glState, 0, 1);

		if(presentCheckboardBG == null)
			presentCheckboardBG = new CheckBoardBGShader(glState);
		
		presentCheckboardBG.activateShader(glState, disp, disp);

		ViewOrtho(glState);

		glState.setDepthMask(false);
	}

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		Camera c = disp.getView3D().getCamera();
		drawPrjQuad(glState, c);
	}

}
