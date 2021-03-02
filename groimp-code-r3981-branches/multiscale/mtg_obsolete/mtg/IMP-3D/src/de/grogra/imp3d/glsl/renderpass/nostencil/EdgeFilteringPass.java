package de.grogra.imp3d.glsl.renderpass.nostencil;

import javax.media.opengl.GL;

import de.grogra.imp3d.Camera;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.renderpass.FullRenderPass;
import de.grogra.imp3d.glsl.utility.GLSLShader;

/**
 * Process deferred shading textures and render results into float-Textures
 * (ping-pong'ing)
 * 
 * @author Konni Hartmann
 */
public class EdgeFilteringPass extends FullRenderPass {

	GLSLShader cs = null;
	
	class FilterEdgeShader extends GLSLShader {
		
		public FilterEdgeShader(OpenGLState glState) {
			super(glState);
		}

		final String baseLightF[] = {
//				"varying vec2 TexCoord2;\n",

				"uniform sampler2DRect firstTex;\n",
				"uniform sampler2DRect inputTex;\n",
				"uniform vec2 e_barrier;\n", // x=norm(~.8f), y=depth(~.5f)
				"uniform vec2 e_weights;\n",  // x=norm, y=depth
				"uniform vec2 e_kernel;\n",   // x=norm, y=depth
//				"vec3 getEyePos(float depth){ return vec3(TexCoord2.st, -1.0) * depth;}\n",
				"vec3 getEyeNormal(vec2 encNorm){",				
				" encNorm = encNorm*4.0-2.0;\n",
				" float f = dot(encNorm,encNorm);\n",
				" float g = sqrt(1.0-f*0.25);\n",
				" return vec3(encNorm*g,1.0-f*0.5);\n",
				"}",

				"void main() {\n",
				"	vec2 tc0 = gl_FragCoord.st;\n", // Center
				"	vec2 tc1 = tc0 + vec2(-1.,-1.);\n", // Left Top
				"	vec2 tc2 = tc0 + vec2(1.,1.);\n", // Right Bottom
				"	vec2 tc3 = tc0 + vec2(1.,-1.);\n", // Right Top
				"	vec2 tc4 = tc0 + vec2(-1.,1.);\n", // Left Bottom
				"	vec4 tc5 = tc0.stst + vec4(-1.,0.,1.,0.);\n", // Left / Right
				"	vec4 tc6 = tc0.stst + vec4(0.,-1.,0.,1.);\n", // Top / Bottom				
				
				"	vec4 dn0 = texture2DRect(firstTex, tc0);\n",
				"	vec4 dn1 = texture2DRect(firstTex, tc1);\n",
				"	vec4 dn2 = texture2DRect(firstTex, tc2);\n",
				"	vec4 dn3 = texture2DRect(firstTex, tc3);\n",
				"	vec4 dn4 = texture2DRect(firstTex, tc4);\n",

				// find edges with normal
				" vec3 n0 = getEyeNormal(dn0.ba);\n",
				" vec4 nd;\n",
				" nd.x = dot(n0, getEyeNormal(dn1.ba));\n",
				" nd.y = dot(n0, getEyeNormal(dn2.ba));\n",
				" nd.z = dot(n0, getEyeNormal(dn3.ba));\n",
				" nd.w = dot(n0, getEyeNormal(dn4.ba));\n",

				" nd -= vec4(e_barrier.x);\n",
				" nd = step(0.0, nd);\n",

				" float ne = clamp(dot(nd, vec4(e_weights.x)), 0.0, 1.0);\n",

				 // Opposite coords
				" vec4 tc5r = tc5.zwxy;\n",
				" vec4 tc6r = tc6.zwxy;\n",

				 // Depth filter : compute gradiental difference:
				 // (c-sample1)+(c-sample1_opposite)

				 "float dc = -dn0.r;\n",
				 "vec4 dd;\n",

				 "dd.x = -dn1.r -\n",
				 "       dn2.r;\n",
				 "dd.y = -dn3.r -\n",
				 "       dn4.r;\n",
				 "dd.z = -texture2DRect(firstTex, tc5.st).r -\n",
				 "       texture2DRect(firstTex, tc5r.st).r;\n",
				 "dd.w = -texture2DRect(firstTex, tc6.st).r -\n",
				 "       texture2DRect(firstTex, tc6r.st).r;\n",

				 "dd = abs(2.0 * dc - dd) - vec4(e_barrier.y);\n",
				 "dd = step(0.0, dd);\n",

				 "float de = clamp(dot(dd, vec4(e_weights.y)), 0.0, 1.0);\n",
				 
				 // Weight
//				 "float w = (1.0 - de * ne) * e_kernel.x;", // 0 - no aa, 1=full aa
				 "float w = de * ne * e_kernel.x;", // 0 - no aa, 1=full aa
				// find edges with depth
//					"	gl_FragColor = vec4(vec3(w), 1.0);\n",

				 "vec4 s = texture2DRect(inputTex, tc0);\n",				 
				 "vec4 s0 = texture2DRect(inputTex, tc0 + vec2(-1.));\n",
				 "vec4 s1 = texture2DRect(inputTex, tc0 + vec2(1.));\n",
				 "vec4 s2 = texture2DRect(inputTex, tc0 + vec2(-1.,1.));\n",
				 "vec4 s3 = texture2DRect(inputTex, tc0 + vec2(1.,-1.));\n",

				"	gl_FragColor = vec4(s * (1.0-w) + (s0 + s1 + s2 + s3)/4.*w);\n",
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
		public Class instanceFor() {
			return null;
		}

		@Override
		public void setupShader(GL gl, GLSLDisplay disp, Object data) {
			int tex0 = gl.glGetUniformLocation(getShaderProgramNumber(), "firstTex");
			int tex1 = gl.glGetUniformLocation(getShaderProgramNumber(), "inputTex");
			
			gl.glUniform1i(tex0, 0);
			gl.glUniform1i(tex1, 1);
			
			int ebarrier = gl.glGetUniformLocation(getShaderProgramNumber(), "e_barrier");
			gl.glUniform2f(ebarrier, 0.8f, .05f);
			int eweights = gl.glGetUniformLocation(getShaderProgramNumber(), "e_weights");
			gl.glUniform2f(eweights, 1.f, 1.f);
//			gl.glUniform2f(eweights, 0.25f, 0.0f);
//			gl.glUniform2f(eweights, 0.0f, 0.25f);
			int ekernel = gl.glGetUniformLocation(getShaderProgramNumber(), "e_kernel");
			gl.glUniform2f(ekernel, 1.f, 1.f);
		}

		@Override
		public GLSLShader getInstance() {
			return this;
		}

	};


	
	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();


		glState.setDepthMask(true);
		glState.enable(OpenGLState.DEPTH_TEST);

		ViewPerspective(glState);
		gl.glPopAttrib();

		deactivateTextures(gl, 2, GL.GL_TEXTURE_RECTANGLE_ARB);
	}

	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		// Set stencil test to only work on drawn fragments
		glState.switchFloatRT();
		GLSLDisplay.printDebugInfoN("Render Light to: "
				+ (4 + glState.getFloatRT()));
		glState.getHDRFBO().drawBuffer(glState, glState.getFloatRT());

		
		if(cs == null)
			cs = new FilterEdgeShader(glState);
		
		cs.activateShader(glState, disp, null);
		GLSLDisplay.printDebugInfoN("Rendering Light with Shader: " + cs);

		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, glState.width, glState.height);
		
		ViewOrtho(glState);
		glState.disable(OpenGLState.DEPTH_TEST);
		glState.disable(OpenGLState.STENCIL_TEST);
//		glState.enable(OpenGLState.STENCIL_TEST);
//		gl.glStencilFunc(GL.GL_EQUAL, 0x1, 0x1);
//		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);

		// Disable Write to Depth-Buffer
		glState.setDepthMask(false);

		glState.getDeferredShadingFBO().bindAttachmentAsTexture(glState, 0, 0);
		// Add read input for HDR-Pass
		glState.getHDRFBO().bindAttachmentAsTexture(glState, glState
				.getFloatRTLast(), 1);
		glState.getAlphaFBO().bindAttachmentAsTexture(glState, 0, 5);
	}

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		Camera c = disp.getView3D().getCamera();
		drawPrjQuad(glState, c);
	}

	@Override
	public void process(GLSLDisplay disp, OpenGLState glState, Object data) {
		super.process(disp, glState, data);
	}
}
