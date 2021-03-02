package de.grogra.imp3d.glsl.renderpass.nostencil;

import java.awt.Dimension;
import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.renderpass.RenderPass;
import de.grogra.imp3d.glsl.utility.Drawable;
import de.grogra.imp3d.glsl.utility.GLSLShader;

// Pass to extract transparency layer via depth-peeling
public class ExtractLayerPass extends RenderPass {

	@Override
	protected int getID() {
		return 1;
	}

	protected GLSLShader sdt = null;
	
	class ExtractFirstLayerShader extends GLSLShader {
		
		public ExtractFirstLayerShader(OpenGLState glState) {
			super(glState);
		}

		/**
		 * Standard Vertex Shader used by many other Shaders
		 */
		final String vStdSrc[] = { "#version 110\n",
				"varying vec3 normal;\n",
				"void main() {", 
				"	gl_Position = ftransform();", 
				"}" };

		@Override
		protected String[] getVertexShader(Object data) {
			return vStdSrc;
		};

		final String[] simpleDepthF = {
				"#version 110\n",
				"uniform sampler2D DepthMaxTex;\n",
				"uniform vec2 screenDim;\n",
				
				"void main(void)\n",
				"{\n",
				"	float fragDepth = gl_FragCoord.z;\n",
//				"	float farthestDepth = texture2DRect(DepthMaxTex, gl_FragCoord.xy).r;\n",
				"	float farthestDepth = texture2D(DepthMaxTex, gl_FragCoord.xy * screenDim.xy).r;\n",

				"	if (gl_FragCoord.z >= farthestDepth) {\n",
				"		discard;\n",
				"	}\n",				
				"	gl_FragColor = vec4(1.0);\n",
				"}"
				};
			
			@Override
			protected String[] getFragmentShader(Object data) {
				return simpleDepthF;
			}

			@Override
			protected void setupDynamicUniforms(GL gl, GLSLDisplay disp, Object data, int shaderNumber) {
				Dimension dim = disp.getView().getSize();
				gl.glUniform2f(screenDimLoc, 1.f/dim.width, 1.f/dim.height);
			}
			
			int screenDimLoc = -1;
			
		@Override
		protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
			int tex1 = gl.glGetUniformLocation(getShaderProgramNumber(), "DepthMaxTex");
			gl.glUniform1i(tex1, 1);
			screenDimLoc= gl.glGetUniformLocation(getShaderProgramNumber(), "screenDim");
		}
		
	};
	
	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		gl.glPopAttrib();
//		deactivateTextures(gl, 2, GL.GL_TEXTURE_RECTANGLE_ARB);
		deactivateTextures(gl, 2, GL.GL_TEXTURE_2D);
		gl.glColorMask(true, true, true, true);
	}
	
	protected void loadTexture(OpenGLState glState) {
		GL gl = glState.getGL();
		glState.getDeferredShadingFBO().bind(glState);
		glState.getDeferredShadingFBO().attachDepthStencil(glState, glState.getPeelingRB());
		glState.getHDRFBO().attachDepthStencil(glState, glState.getPeelingRB());
		glState.getAlphaFBO().attachDepthStencil(glState, glState.getPeelingRB());
		
		glState.getDualDepthFBO().bind(glState);
		gl.glDrawBuffer(GL.GL_NONE);
		gl.glReadBuffer(GL.GL_NONE);
//		glState.dualDepthFBO.isComplete(glState);
	}
	
	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		gl.glColorMask(false, false, false, false);
		gl.glDepthMask(true);
		
		loadTexture(glState);		
		
		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, glState.width, glState.height);

		// Setup Stencil Buffer!
		gl.glClearStencil(0);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		
		glState.getPeelingFarDepthTRT().bindTo(gl, GL.GL_TEXTURE1);
			
		glState.disable(OpenGLState.STENCIL_TEST);
		
		glState.enable(OpenGLState.DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
	}
	
	@Override
	public void postDrawCallback(Drawable dr, OpenGLState glState,
			GLSLDisplay disp) {
//		dr.activateGLSLShader(glState, disp, true, false);
	}
	
	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		
		if(sdt == null)
			sdt = new ExtractFirstLayerShader(glState);
		
		sdt.activateShader(glState, disp, null);		
		gl.glBeginQuery(GL.GL_SAMPLES_PASSED, glState.occlusionQuery[0]);

		renderAndUpdateVector(disp, glState.getWorldToView(), glState.deferredTranspRenderable);

		gl.glEndQuery(GL.GL_SAMPLES_PASSED);
	}

}
