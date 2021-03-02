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
public class PreprocessPass extends FullRenderPass {

	GLSLShader cs = null;
	
	class PrepareLightingShader extends GLSLShader {
		
		public PrepareLightingShader(OpenGLState glState) {
			super(glState);
		}

		final String baseLightF[] = {
				"#version 110\n",
				"#extension GL_ARB_texture_rectangle : enable\n",
				"uniform sampler2DRect firstTex;\n",
				"uniform sampler2DRect secondTex;\n",
				"uniform sampler2DRect thirdTex;\n",
				"uniform sampler2DRect fourthTex;\n",
				"uniform sampler2DRect fithTex;\n",

				"void main() {\n", 
				"	vec4 diffuse = texture2DRect(secondTex, gl_FragCoord.st);\n",
				"	vec4 em = texture2DRect(thirdTex, gl_FragCoord.st);\n",
				"	vec4 alpha = texture2DRect(fourthTex, gl_FragCoord.st);\n",
				"	gl_FragColor = (vec4(alpha.rgb, alpha.r)) * texture2DRect(fithTex, gl_FragCoord.st) + (vec4(1.) - vec4(alpha.rgb, alpha.r)) * vec4(em.rgb, 0.0);\n",
				"}" };

		@Override
		public String[] getFragmentShader(Object sh) {
			return baseLightF;
		}

		@Override
		public void setupShader(GL gl, GLSLDisplay disp, Object data) {

			int tex0 = gl
					.glGetUniformLocation(getShaderProgramNumber(), "firstTex");
			int tex1 = gl.glGetUniformLocation(getShaderProgramNumber(),
					"secondTex");
			int tex2 = gl
					.glGetUniformLocation(getShaderProgramNumber(), "thirdTex");
			int tex3 = gl
			.glGetUniformLocation(getShaderProgramNumber(), "fourthTex");
			int tex4 = gl
			.glGetUniformLocation(getShaderProgramNumber(), "fithTex");

			gl.glUniform1i(tex0, 0);
			gl.glUniform1i(tex1, 1);
			gl.glUniform1i(tex2, 2);
			gl.glUniform1i(tex3, 3);
			gl.glUniform1i(tex4, 4);
		}
	};

	
	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof LightPos);
		GL gl = glState.getGL();


		glState.disable(OpenGLState.STENCIL_TEST);		

		glState.setDepthMask(true);
		glState.enable(OpenGLState.DEPTH_TEST);

		ViewPerspective(glState);		
		gl.glPopAttrib();
		
		deactivateTextures(gl, 4);
	}
	
	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof Integer);
		
		GL gl = glState.getGL();
		

		glState.switchFloatRT();
		glState.getHDRFBO().drawBuffer(glState, glState.getFloatRT());
		
		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, glState.width, glState.height);
		GLSLDisplay.printDebugInfoN("Preprocessing: " + (4 + glState.getFloatRT()));
		
		if(cs == null)
			cs = new PrepareLightingShader(glState);
		
		cs.activateShader(glState, disp, null);

		ViewOrtho(glState);
		glState.disable(OpenGLState.DEPTH_TEST);

		// Disable Write to Depth-Buffer
		glState.setDepthMask(false);

		glState.getDeferredShadingFBO().bindAllAttachmentsAsTextures(glState);

		glState.getHDRFBO().bindAttachmentAsTexture(glState, glState.getFloatRTLast(), 4);
		
		// Set stencil test to only work on drawn fragments
		glState.enable(OpenGLState.STENCIL_TEST);
		gl.glStencilFunc(GL.GL_EQUAL, 0x1, 0x1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);		
	}

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		Camera c = disp.getView3D().getCamera();
		drawPrjQuad(glState, c);
	}
}
