package de.grogra.imp3d.glsl.renderpass;

import java.awt.Dimension;

import javax.media.opengl.GL;


import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.utility.Drawable;
import de.grogra.imp3d.glsl.utility.GLSLShader;

// Pass to extract transparency layer via depth-peeling
public class ExtractSucessiveLayerPass extends ExtractLayerPass {

	@Override
	protected int getID() {
		return 1;
	}

	protected GLSLShader sdt = null;

	class ExtractSucessiveLayerShader extends GLSLShader {
		public ExtractSucessiveLayerShader(OpenGLState glState) {
			super(glState);
		}

		/**
		 * Standard Vertex Shader used by many other Shaders
		 */
		final String vStdSrc[] = { "#version 110\n", "varying vec3 normal;\n",
				"void main() {", "	gl_Position = ftransform();", "}" };

		@Override
		protected String[] getVertexShader(Object data) {
			return vStdSrc;
		};

		final String[] simpleDepthF = {
				"#version 110\n",
				"uniform sampler2D DepthMinTex;\n",
				"uniform sampler2D DepthMaxTex;\n",
				"uniform vec2 screenDim;\n",

				"void main(void)\n",
				"{\n",
				"	float fragDepth = gl_FragCoord.z;\n",

				"	float nearestDepth = texture2D(DepthMinTex, gl_FragCoord.xy*screenDim).r;\n",
				"	float farthestDepth = texture2D(DepthMaxTex, gl_FragCoord.xy*screenDim).r;\n",

				"	if (fragDepth <= nearestDepth || gl_FragCoord.z >= farthestDepth) {\n",
				"		discard;\n", "	}\n", "	gl_FragColor = vec4(1.0);\n", "}" };

		@Override
		protected String[] getFragmentShader(Object data) {
			return simpleDepthF;
		}

		int screenDimLoc = -1;
		
		@Override
		protected void setupDynamicUniforms(GL gl, GLSLDisplay disp, Object data, int shaderNumber) {
			Dimension dim = disp.getView().getSize();
			gl.glUniform2f(screenDimLoc, 1.f / dim.width, 1.f / dim.height);
		}
		
		@Override
		protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
			int tex0 = gl.glGetUniformLocation(getShaderProgramNumber(),
					"DepthMinTex");
			int tex1 = gl.glGetUniformLocation(getShaderProgramNumber(),
					"DepthMaxTex");

			gl.glUniform1i(tex0, 0);
			gl.glUniform1i(tex1, 1);

			screenDimLoc= gl.glGetUniformLocation(getShaderProgramNumber(),
			"screenDim");
		}

	};

	protected void loadTexture(OpenGLState glState) {
		GL gl = glState.getGL();
		glState.getDualDepthFBO().bind(glState);
		glState.getPeelingNearDepthTRT().bindTo(glState, 0);
		gl.glDrawBuffer(GL.GL_NONE);
		gl.glReadBuffer(GL.GL_NONE);
	}

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {

		GL gl = glState.getGL();

		gl.glBeginQuery(GL.GL_SAMPLES_PASSED, glState.occlusionQuery[0]);

		if(sdt == null)
			sdt = new ExtractSucessiveLayerShader(glState);
		
		sdt.activateShader(glState, disp, null);

		renderVector(disp, glState.getWorldToView(), true,
				glState.deferredTranspRenderable);

		gl.glEndQuery(GL.GL_SAMPLES_PASSED);

	}
}
