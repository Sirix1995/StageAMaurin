package de.grogra.imp3d.glsl.renderpass;

import java.nio.FloatBuffer;

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
public class ToneMappingPass extends FullRenderPass {
	
	@Override
	protected int getID() {
		return 4;
	}

	
	GLSLShader presentToneMapped = null;
	GLSLShader presentBlaToneMapped = null;
	GLSLShader presentStaticToneMapped = null;
	class StaticTonemappingShader extends GLSLShader {

		public StaticTonemappingShader(OpenGLState glState) {
			super(glState);
		}

		final String[] toneMappedS = {
				"#version 110\n",
				"#extension GL_ARB_texture_rectangle : enable\n",
				"uniform sampler2DRect inputTex;\n",
				"uniform float brightness;\n",
				"void main() {\n",
				" vec4 color = texture2DRect(inputTex, gl_FragCoord.st);\n",
				" vec3 col = color.rgb;\n",
				" gl_FragColor = vec4( (color.rgb) * (brightness != 0.0 ? brightness : 1.0) , color.a);\n",
				"}" };

		@Override
		protected String[] getFragmentShader(Object data) {
			return toneMappedS;
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
		protected void setupDynamicUniforms(GL gl, GLSLDisplay disp,
				Object data, int shaderNo) {
			gl.glUniform1f(brightnessLoc, disp.getBrightness());
		}
		
		int brightnessLoc = -1;
		
		@Override
		protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
			int loc = gl.glGetUniformLocation(getShaderProgramNumber(),
					"inputTex");
			gl.glUniform1i(loc, 2);
			brightnessLoc = gl.glGetUniformLocation(getShaderProgramNumber(), "brightness");
		}

	};

	class SimpleTonemappingShader extends GLSLShader {

		public SimpleTonemappingShader(OpenGLState glState) {
			super(glState);
		}

		final String[] toneMappedS = {
				// "uniform sampler2DRect inputTex;",
				// "uniform sampler2D luminTex;",
				// "uniform sampler2D averageTex;",
				//				
				// "uniform float exposure;",
				// "varying vec2 TexUnit;",
				// "vec4 checkBoard() {",
				// //
				// " 	bool tmp = (mod(floor(0.05* gl_FragCoord.x - 0.05* gl_FragCoord.y),2.0) == 1.0);",
				// " 	bool tmp = (mod(floor(0.05* gl_FragCoord.x) + floor(0.05* gl_FragCoord.y),2.0) == 1.0);",
				// "	return tmp?vec4(0.6, 0.6, 0.6, 1.0):vec4(0.75, 0.75, 0.75, 1.0);",
				// "}",
				// "void main() {",
				// // " float exposure = 0.5976572;",
				// " float brightMax = 1.0;",
				// " vec4 color = texture2DRect(inputTex, gl_FragCoord.st);",
				// " if(color.a < 1.0) gl_FragColor = checkBoard();",
				// " else{",
				// // Perform tone-mapping
				// " float Y = dot(vec4(0.30, 0.59, 0.11, 0.0), color);",
				// " float YD = exposure * (exposure/brightMax + 1.0) / (exposure + 1.0);",
				// " color *= YD;", "  color.a = 1.0;",
				// "  gl_FragColor = clamp(color, 0.0, 1.0);", "}}" };

				"#version 110\n",
				"#extension GL_ARB_texture_rectangle : enable\n",
				"uniform sampler2DRect inputTex;\n",
				"uniform sampler2DRect luminTex;\n",
				"uniform sampler2DRect averageTex;\n",
				"varying vec2 TexUnit;\n",

				"void tone(inout vec3 rgb, float f_i, float m, float a, float c, vec3 Cav, float Lav, float Lmin, float Lmax) {\n",
				"float f = exp(-f_i);\n",
				"vec3 LUMINANCE = vec3(0.2125, 0.7154, 0.0721);\n",
				"float L = dot(rgb, LUMINANCE);\n",
				"vec3 I_l = c * rgb + (1.0-c) * L;\n",
				"vec3 I_g = c * Cav + (1.0-c) * Lav;\n",
				"vec3 I_a = a * I_l + (1.0-a) * I_g;\n",
				"rgb /= (rgb + vec3 (	 pow(f * I_a.r, m),\n"
						+ "						 pow(f * I_a.g, m),\n"
						+ "						 pow(f * I_a.b, m)\n" + "			)			);\n",

				// XXX: here should be a normilization step!
				// just a test:
				// "float Lmax_approx = Lmax / (Lmax + pow(f * dot(vec3(1./3.),Cav), m));",
				// "rgb = clamp(rgb/Lmax_approx, 0.0, 1.0);",
				// "rgb = clamp((rgb - Lmin)/(Lmax-Lmin), 0.0, 1.0);",
				// "rgb = clamp(rgb/Lmax, 0.0, 1.0);",
				"}\n",

				"void main() {\n",
				" vec4 color = texture2DRect(inputTex, gl_FragCoord.st);\n",
				" vec3 col = color.rgb;\n",
				" vec4 Cav = texture2DRect(averageTex, vec2(0.5));\n",
				" vec4 lumin = texture2DRect(luminTex, vec2(0.5));\n",

				" float Lmin = lumin.r;\n", " float Lav = lumin.g;\n",
				" float Lmax = lumin.b;\n",
				" float Colmax = Cav.a;\n",

				/*
				 * "	float f = 0.0;", "	float a = 0.0;", "	float c = 1.0;",
				 * "	float m = 0.3 + 0.7 * pow( (log(Lmax) - log(Lav)) / (log(Lmax) - log(Lmin)), 1.4 );"
				 * , "	tone(col, f, m, a, c, Cav.rgb, Lav, Lmin, Lmax);",
				 * "	vec3 colScaled = color.rgb / Lmax;",
				 * "  gl_FragColor = vec4(mix(col, colScaled, step(TexUnit.s, 0.5)), 1.0);"
				 * , "}"
				 */
				// " gl_FragColor = vec4( (color.rgb - Lmin) / (Lmax - Lmin) , 1.0);",
				// "}"
				" Colmax = Colmax != 0.0 ? 1.0/Colmax : 1.0;\n",
				" gl_FragColor = vec4( (color.rgb) * (Colmax) , color.a);\n",
				"}" };

		@Override
		protected String[] getFragmentShader(Object data) {
			return toneMappedS;
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
					"inputTex");
			gl.glUniform1i(loc, 2);
			loc = gl.glGetUniformLocation(getShaderProgramNumber(), "luminTex");
			gl.glUniform1i(loc, 0);
			loc = gl.glGetUniformLocation(getShaderProgramNumber(),
					"averageTex");
			gl.glUniform1i(loc, 1);
			// assert (data instanceof GLSLDisplay);
			// int exp = gl.glGetUniformLocation(getShaderProgramNumber(),
			// "exposure");
			// gl.glUniform1f(exp, disp.optionExposure);
		}

	};
	class BlaTonemappingShader extends GLSLShader {

		public BlaTonemappingShader(OpenGLState glState) {
			super(glState);
		}

		final String[] toneMappedS = {
				// "uniform sampler2DRect inputTex;",
				// "uniform sampler2D luminTex;",
				// "uniform sampler2D averageTex;",
				//				
				// "uniform float exposure;",
				// "varying vec2 TexUnit;",
				// "vec4 checkBoard() {",
				// //
				// " 	bool tmp = (mod(floor(0.05* gl_FragCoord.x - 0.05* gl_FragCoord.y),2.0) == 1.0);",
				// " 	bool tmp = (mod(floor(0.05* gl_FragCoord.x) + floor(0.05* gl_FragCoord.y),2.0) == 1.0);",
				// "	return tmp?vec4(0.6, 0.6, 0.6, 1.0):vec4(0.75, 0.75, 0.75, 1.0);",
				// "}",
				// "void main() {",
				// // " float exposure = 0.5976572;",
				// " float brightMax = 1.0;",
				// " vec4 color = texture2DRect(inputTex, gl_FragCoord.st);",
				// " if(color.a < 1.0) gl_FragColor = checkBoard();",
				// " else{",
				// // Perform tone-mapping
				// " float Y = dot(vec4(0.30, 0.59, 0.11, 0.0), color);",
				// " float YD = exposure * (exposure/brightMax + 1.0) / (exposure + 1.0);",
				// " color *= YD;", "  color.a = 1.0;",
				// "  gl_FragColor = clamp(color, 0.0, 1.0);", "}}" };
				"#version 110\n",
				"#extension GL_ARB_texture_rectangle : enable\n",

				"uniform sampler2DRect inputTex;\n",
				"uniform sampler2DRect luminTex;\n",
				"uniform sampler2DRect averageTex;\n",

				"void tone(inout vec3 rgb, float f_i, float m, float a, float c, vec3 Cav, float Lav, float Lmin, float Lmax) {\n",
				"float f = exp(-f_i);\n",
				"vec3 LUMINANCE = vec3(0.2125, 0.7154, 0.0721);\n",
				"float L = dot(rgb, LUMINANCE);\n",
				"vec3 I_l = c * rgb + (1.0-c) * L;\n",
				"vec3 I_g = c * Cav + (1.0-c) * Lav;\n",
				"vec3 I_a = a * I_l + (1.0-a) * I_g;\n",
				"rgb /= (rgb + vec3 (	 pow(f * I_a.r, m),\n"+
				"						 pow(f * I_a.g, m),\n"+
			  	"						 pow(f * I_a.b, m)\n" + 
				"			)			);\n",

				// XXX: here should be a normilization step!
				// just a test:
				// "float Lmax_approx = Lmax / (Lmax + pow(f * dot(vec3(1./3.),Cav), m));",
				// "rgb = clamp(rgb/Lmax_approx, 0.0, 1.0);",
				// "rgb = clamp((rgb - Lmin)/(Lmax-Lmin), 0.0, 1.0);",
				// "rgb = clamp(rgb/Lmax, 0.0, 1.0);",
				"}\n",

				"void main() {\n",
				" vec4 color = texture2DRect(inputTex, gl_FragCoord.st);\n",
				" vec3 col = color.rgb;\n",
				" vec4 Cav = texture2DRect(averageTex, vec2(0.5));\n",
				" vec4 lumin = texture2DRect(luminTex, vec2(0.5));\n",
				" float Lmin = lumin.r;\n", 
				" float Lav = lumin.g;\n",
				" float Lmax = lumin.b;\n",
				" float Colmax = Cav.a;\n",
				" if(Colmax==0.0) {\n",
				"  gl_FragColor = vec4(vec3(0.0), color.a);",
				"  return;",
				" }",
				" float f = 0.0;", "	float a = 0.0;", "	float c = 1.0;",
				" float m = 0.3 + 0.7 * pow( (log(Lmax) - log(Lav)) / (log(Lmax) - log(Lmin)), 1.4 );",
				" tone(col, f, m, a, c, Cav.rgb, Lav, Lmin, Lmax);",
				" vec3 colScaled = color.rgb / Lmax;",
				" gl_FragColor = vec4(colScaled, color.a);",
//				  "  gl_FragColor = vec4(mix(col, colScaled, step(TexUnit.s, 0.5)), 1.0);"
				"}"
			};

		@Override
		protected String[] getFragmentShader(Object data) {
			return toneMappedS;
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
					"inputTex");
			gl.glUniform1i(loc, 2);
			loc = gl.glGetUniformLocation(getShaderProgramNumber(), "luminTex");
			gl.glUniform1i(loc, 0);
			loc = gl.glGetUniformLocation(getShaderProgramNumber(),
					"averageTex");
			gl.glUniform1i(loc, 1);
			// assert (data instanceof GLSLDisplay);
			// int exp = gl.glGetUniformLocation(getShaderProgramNumber(),
			// "exposure");
			// gl.glUniform1f(exp, disp.optionExposure);
		}

	};

	ReduceImagePass rip = new ReduceImagePass();

	FloatBuffer buf = FloatBuffer.allocate(4 * 1);

	protected void pickColors(GLSLDisplay disp, OpenGLState glState) {
		glState.getDeferredShadingFBO().drawBuffers(glState, 2);
		for (int i = 0; i < 2; i++) {
			buf.clear();
			glState.getGL().glReadBuffer(GL.GL_COLOR_ATTACHMENT0_EXT+i);
			glState.getGL().glReadPixels(0, 0, 1, 1, GL.GL_BGRA,
					GL.GL_FLOAT, buf);

			System.out.println("Att"+i+":\nB: " + buf.get(0) + "\n" + "G: " + buf.get(1)
					+ "\n" + "R: " + buf.get(2) + "\n" + "A: " + buf.get(3));
		}
	}

	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		deactivateTextures(gl, 3);
		glState.setDepthMask(true);
		
//		if (GLSLDisplay.DEBUG)
//			pickColors(disp, glState);

		ViewPerspective(glState);
		gl.glPopAttrib();


		glState.enable(OpenGLState.DEPTH_TEST);

		glState.disable(OpenGLState.STENCIL_TEST);
	}

	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof Integer);
		if(disp.isOptionAutoAdjustBrightness())
			rip.process(disp, glState, data);

		GL gl = glState.getGL();

		glState.switchFloatRT();
		glState.getHDRFBO().drawBuffer(glState, glState.getFloatRT());
		GLSLDisplay.printDebugInfoN("Tonemapping to: "
				+ (4 + glState.getFloatRT()) + " from "
				+ (4 + glState.getFloatRTLast()));
		
		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
		glState.disable(OpenGLState.DEPTH_TEST);

		glState.disable(OpenGLState.STENCIL_TEST);

		if(presentToneMapped == null)
			presentToneMapped = new SimpleTonemappingShader(glState);
		if(presentStaticToneMapped == null)
			presentStaticToneMapped = new StaticTonemappingShader(glState);
		if(presentBlaToneMapped == null)
			presentBlaToneMapped = new BlaTonemappingShader(glState);
		
		if(!disp.isOptionAutoAdjustBrightness())
			presentStaticToneMapped.activateShader(glState, disp, data);
		else {
			switch (disp.getTonemapping()) {
			case 0:
				presentToneMapped.activateShader(glState, disp, disp);				
				break;
			case 1:
				presentBlaToneMapped.activateShader(glState, disp, disp);				
				break;
			default:
				break;
			}
		}

		ViewOrtho(glState);
		glState.setDepthMask(false);

		// Add read input for HDR-Pass
		glState.getHDRFBO().bindAttachmentAsTexture(glState, glState
				.getFloatRTLast(), 2);
		// Bind info from reduce pass
		glState.getDeferredShadingFBO().bindAllAttachmentsAsTextures(glState, 2);
	}

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		Camera c = disp.getView3D().getCamera();
		drawPrjQuad(glState, c);
	}

}
