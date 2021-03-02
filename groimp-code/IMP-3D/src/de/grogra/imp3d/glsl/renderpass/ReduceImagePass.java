package de.grogra.imp3d.glsl.renderpass;

import javax.media.opengl.GL;

import de.grogra.imp3d.Camera;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.utility.GLSLShader;

/**
 * Simple ToneMapping to render results of the hd-lighting passes.
 * 
 * @author Konni Hartmann
 */
public class ReduceImagePass extends FullRenderPass {
	GLSLShader luminanceShader = null;
	
	class LuminanceShader extends GLSLShader {

		public LuminanceShader(OpenGLState glState) {
			super(glState);
		}

		final String[] shaderF = {
				"#version 110\n",
				"#extension GL_ARB_texture_rectangle : enable\n",
				"uniform sampler2DRect SourceTextureSampler;",
				"uniform sampler2DRect AverageTextureSampler;",
				"uniform vec2 SourceSize;",
				"varying vec2 uv;",
				"void main(){",
				// old:
				// "vec3 LUMINANCE = vec3(0.299, 0.587, 0.114);",
				"vec3 LUMINANCE = vec3(0.2125, 0.7154, 0.0721);",
				"float average = 0.0;",
				"vec3 colAverage = vec3(0.0);",
				"float minimum =  100000000000000000000.0;",
				"float maximum = -100000000000000000000.0;",
				"float maxRGB  = -100000000000000000000.0;",
				"vec4 color = vec4(0.0);",
				"vec3 Offsets2x2 = vec3(-1.0, 0.0, 1.0);",
				"for (int x = 0; x < 3; x++)",
				"{",
				"for (int y = 0; y < 3; y++)",
				"{",
				"vec2 vOffset = vec2(Offsets2x2[x], Offsets2x2[y]);",
				"color = texture2DRect(SourceTextureSampler, uv * SourceSize + vOffset);",

				"float GreyValue = dot(color.rgb, LUMINANCE);",

				"maximum = max(maximum, GreyValue);",
				"maxRGB = max(max(max(maxRGB, color.r), color.g), color.b);",
				"average += GreyValue;",
				"minimum = min(minimum, GreyValue);",
				"colAverage += color.rgb;",
				"}",
				"}",
				" gl_FragData[0] = vec4(minimum, average / 9.0, maximum, 1.0);",
				" gl_FragData[1] = vec4(colAverage / 9.0, maxRGB);", "}" };

		@Override
		protected String[] getFragmentShader(Object data) {
			return shaderF;
		}

		@Override
		public GLSLShader getInstance() {
			return this;
		}

		@Override
		public Class<?> instanceFor() {
			return null;
		}

		@Override
		public boolean needsRecompilation(Object data) {
			return false;
		}

		@Override
		protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
			int loc = gl.glGetUniformLocation(getShaderProgramNumber(),
					"SourceTextureSampler");
			gl.glUniform1i(loc, 0);
			sourceDimLoc = gl.glGetUniformLocation(luminanceShader
					.getShaderProgramNumber(), "SourceSize");
		}

	};

	GLSLShader reduceShader = null;
	
	class ReduceShader extends GLSLShader {

		public ReduceShader(OpenGLState glState) {
			super(glState);
		}

		final String[] shaderF = {
				"#version 110\n",
				"#extension GL_ARB_texture_rectangle : enable\n",
				"uniform sampler2DRect SourceTextureSampler;",
				"uniform sampler2DRect AverageTextureSampler;",
				"void main(){",
				" float average = 0.0;",
				" vec3 colAverage = vec3(0.0);",
				" float minimum =  100000000000000000000.0;",
				" float maximum = -100000000000000000000.0;",
				" float maxRGB  = -100000000000000000000.0;",

				" vec4 color = vec4(0.0);",
				" vec4 color2 = vec4(0.0);",

				// Samples 9 points with the current pixel at the lower left of
				// the box!
				// (not in the center! remember: gl_FragCoord is the center of a
				// pixel (offset by 0.5!)
				// thus multiplying by 3 gives an additional offset of 1.0)
				" vec3 Offsets3x3 = vec3(-1.0, 0.0, 1.0);",
				"for (int x = 0; x < 3; x++)",
				"{",
				"for (int y = 0; y < 3; y++)",
				"{",
				"vec2 vOffset = vec2(Offsets3x3[x], Offsets3x3[y]);",
				"color = texture2DRect(SourceTextureSampler, gl_FragCoord.st * 3.0 + vOffset);",
				"color2 = texture2DRect(AverageTextureSampler, gl_FragCoord.st * 3.0 + vOffset);",
				"colAverage += color2.rgb;",
				"minimum = min( minimum, color.r );", "average += color.g;",
				"maximum = max( maximum, color.b );",
				"maxRGB = max( maxRGB, color2.a );", "}", "}",
				"average /= 9.0;", "colAverage /= 9.0;",
				" gl_FragData[0] = vec4(minimum, average, maximum, 1.0);",
				" gl_FragData[1] = vec4(colAverage, maxRGB);", "}" };

		@Override
		protected String[] getFragmentShader(Object data) {
			return shaderF;
		}

		@Override
		public GLSLShader getInstance() {
			return this;
		}

		@Override
		public Class<?> instanceFor() {
			return null;
		}

		@Override
		public boolean needsRecompilation(Object data) {
			return false;
		}

		@Override
		protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
			int loc = gl.glGetUniformLocation(getShaderProgramNumber(),
					"SourceTextureSampler");
			gl.glUniform1i(loc, 0);
			loc = gl.glGetUniformLocation(getShaderProgramNumber(),
					"AverageTextureSampler");
			gl.glUniform1i(loc, 1);
		}

	};

	int sourceDimLoc = -1;
	
	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		deactivateTextures(gl, 2);
		glState.setDepthMask(true);
		
		ViewPerspective(glState);

		glState.enable(OpenGLState.DEPTH_TEST);
		glState.disable(OpenGLState.STENCIL_TEST);
		GLSLDisplay.printDebugInfoN("-- - ------------- --");

	}

	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof Integer);
		glState.disable(OpenGLState.DEPTH_TEST);
		glState.disable(OpenGLState.STENCIL_TEST);

		glState.setDepthMask(false);
		// gl.glClearColor(0, 1, 0, 1);

		ViewOrtho(glState);
		
		GLSLDisplay.printDebugInfoN("-- - Reduce Image - --");

	}

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof Integer);

		Camera c = disp.getView3D().getCamera();
		GL gl = glState.getGL();

		// Add read input for HDR-Pass
		
		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);

		if(luminanceShader == null) 
			luminanceShader = new LuminanceShader(glState);
		
		luminanceShader.activateShader(glState, disp, null);

		gl.glUniform2f(sourceDimLoc, glState.width, glState.height);

		int maxSize = Math.max(glState.height, glState.width);
		int minSize = Math.min(glState.height, glState.width);
		
		int destSize = 1;
		int i = 0;
		while ((2 * destSize < maxSize) && (destSize < minSize)) {
			destSize *= 3;
			i++;
		}
		destSize /= 3;
		i--;
		
		int bufSwitch = (i % 2) * 2;
		glState.getDeferredShadingFBO().drawBuffers(glState, 2, bufSwitch);
		glState.getHDRFBO()
				.bindAttachmentAsTexture(glState, glState.getFloatRT(), 0);
		GLSLDisplay.printDebugInfoN("reduced to "+ bufSwitch+ " with Size " +destSize);
		drawPrjQuad(glState, c, 0, glState.height - destSize + .1f,
				destSize - .1f, destSize - .1f);
		i--;
		destSize /= 3;

		if(reduceShader == null)
			reduceShader = new ReduceShader(glState);
		
		reduceShader.activateShader(glState, disp, null);
		for (; i >= 0; i--) {
			glState.getDeferredShadingFBO().drawBuffers(glState, 2,
					(bufSwitch + 2) % 4);
			glState.getDeferredShadingFBO().bindAllAttachmentsAsTextures(glState, 2,
					bufSwitch);
			GLSLDisplay.printDebugInfoN("reduced to "+ ((bufSwitch + 2) % 4) + " with Size " +destSize);
			drawPrjQuad(glState, c, 0, glState.height - destSize + .1f,
					destSize - .1f, destSize - .1f);
			bufSwitch = (i % 2) * 2;
			destSize /= 3;
		}
		gl.glPopAttrib();
	}

}
