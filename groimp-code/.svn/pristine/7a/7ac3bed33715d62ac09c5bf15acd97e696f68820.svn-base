package de.grogra.imp3d.glsl.renderpass.nostencil;

import java.util.Iterator;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.GLSLUpdateCache;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.GLDisplay.GLVisitor;
import de.grogra.imp3d.glsl.material.GLSLMaterial;
import de.grogra.imp3d.glsl.renderpass.FullRenderPass;
import de.grogra.imp3d.glsl.utility.Drawable;
import de.grogra.imp3d.glsl.utility.GLSLShader;
import de.grogra.imp3d.objects.NumericLabel;
import de.grogra.imp3d.objects.TextLabel;

/**
 * Renders cached Nodes from the scenegraph. Used to postpone rendering of
 * lights (point and frustrum)
 * 
 * @author Konni Hartmann
 */
public class ToolRenderPass extends FullRenderPass {
	@Override
	protected int getID() {
		return 5;
	}

	GLSLShader useTransp = null;

	class DrawWithTranspShader extends GLSLShader {
		public DrawWithTranspShader(OpenGLState glState) {
			super(glState);
		}

		String[] vertexShader = { "#version 110\n","void main() {\n",
				"gl_FrontColor = gl_Color;\n", "gl_BackColor = gl_Color;\n",
				"gl_Position = ftransform();\n", "}" };

		String[] fragShader = {
				"#version 110\n",
				"#extension GL_ARB_texture_rectangle : enable\n",
				"uniform sampler2DRect inputTex;\n",
				"uniform sampler2DRect alphaTex;\n",

				"void main() {\n",
				" vec4 lastCol = texture2DRect(inputTex, gl_FragCoord.st);\n",
				" vec4 alpha = texture2DRect(alphaTex, gl_FragCoord.st);\n",
				" gl_FragColor = clamp(vec4(alpha.rgb, 1.0) * gl_Color + lastCol, 0.0, 1.0);\n",
				"}" };

		@Override
		protected String[] getVertexShader(Object data) {
			return vertexShader;
		}

		@Override
		protected String[] getFragmentShader(Object data) {
			return fragShader;
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

	CopyFloatTexturePass cftp = new CopyFloatTexturePass();

	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();

		gl.glPopAttrib();

		glState.enable(OpenGLState.STENCIL_TEST);
		gl.glStencilFunc(GL.GL_EQUAL, 0x3, 0x3);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);

		cftp.process(disp, glState, data);

		glState.switchFloatRT();

		// gl.glClear(GL.GL_STENCIL_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		glState.disable(OpenGLState.STENCIL_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		deactivateTextures(gl, 2, GL.GL_TEXTURE_RECTANGLE_ARB);

		// // gl.glEnable (GL.GL_BLEND); // Enable Blending
		// // gl.glBlendFunc (GL.GL_SRC_ALPHA , GL.GL_ONE_MINUS_SRC_ALPHA);
		// gl.glPolygonMode (GL.GL_FRONT, GL.GL_NONE); // Draw Backfacing
		// Polygons As Wireframes
		// gl.glPolygonMode (GL.GL_BACK, GL.GL_LINE); // Draw Backfacing
		// Polygons As Wireframes
		// gl.glLineWidth (5); // Set The Line Width
		//
		// gl.glDisable(GL.GL_LIGHTING);
		// // gl.glDisable(GL.GL_CULL_FACE);
		//		
		// gl.glCullFace (GL.GL_FRONT); // Don't Draw Any Front-Facing Polygons
		// gl.glDepthFunc (GL.GL_LEQUAL); // Change The Depth Mode
		//
		// // glState.disable(OpenGLState.DEPTH_TEST);
		// glState.setActiveProgram(0);
		//				
		// gl.glPolygonOffset(1, 1);
		// // gl.glPolygonOffset(-1, -1);
		// gl.glEnable(GL.GL_POLYGON_OFFSET_LINE);
		//
		// gl.glColor4f (0, .5f, 1.0f, 0.5f); // Set The Outline Color
		// renderVector(disp, glState.getWorldToView(), true,
		// glState.deferredSolidRenderable);
		//	
		//
		// gl.glEnable(GL.GL_LIGHTING);
		// gl.glDisable(GL.GL_POLYGON_OFFSET_LINE);
		//
		// gl.glDepthFunc (GL.GL_LESS); // Reset The Depth-Testing Mode
		// gl.glCullFace (GL.GL_BACK); // Reset The Face To Be Culled
		// gl.glPolygonMode (GL.GL_FRONT, GL.GL_FILL); // Draw Backfacing
		// Polygons As Wireframes
		// gl.glPolygonMode (GL.GL_BACK, GL.GL_FILL); // Reset Back-Facing
		// Polygon Drawing Mode
		// // gl.glDisable (GL.GL_BLEND); // Disable Blending
		// gl.glLineWidth (1); // Set The Line Width

	}

	boolean wrongShader = false;

	@Override
	public void postDrawCallback(Drawable dr, OpenGLState glState,
			GLSLDisplay disp) {
		GLSLMaterial m = dr.getGLSLShader();
		if ((m == null) || (m.getShaderProgramNumber() == 0)) {
			if (wrongShader) {
				useTransp.activateShader(glState, disp, null);
				wrongShader = false;
			}
		} else {
			glState.setShaderConfSwitch(OpenGLState.SKY_PREVIEW_MATERIAL);
			dr.activateGLSLShader(glState, disp);
			glState.setShaderConfSwitch(OpenGLState.DEFAULT_MATERIAL);
			wrongShader = true;
		}
	}

	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof Integer);
		GL gl = glState.getGL();

		glState.switchFloatRT();

		GLSLDisplay.printDebugInfoN("Render Tool to: "
				+ (4 + glState.getFloatRT()));
		glState.getHDRFBO().drawBuffer(glState, glState.getFloatRT());
		glState.setActiveProgram(0);
		
		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, glState.width, glState.height);

		gl.glDepthFunc(GL.GL_EQUAL);

		glState.getHDRFBO().bindAttachmentAsTexture(glState,
				glState.getFloatRTLast(), 0);
		glState.getAlphaFBO().bindAttachmentAsTexture(glState, 0, 1);

		glState.enable(OpenGLState.STENCIL_TEST);
		gl.glStencilFunc(GL.GL_ALWAYS, 0x3, 0x3);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
	}

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {

		// XXX: needs access to private functions of GLDisplay

		if (useTransp == null)
			useTransp = new DrawWithTranspShader(glState);

		GLVisitor old = disp.getVisitor();

		if (newVisit == null)
			newVisit = new GLSLUpdateCache(disp);

		newVisit.init(disp.getRenderGraphState(), glState.getWorldToView(), 0);
		disp.setVisitor(newVisit);
		useTransp.activateShader(glState, disp, null);
		wrongShader = false;
		if (disp.isOptionShowGrid())
			disp.drawGrid(glState.getGL());
		disp.setVisitor(old);
		renderVector(disp, glState.getWorldToView(), true,
				glState.deferredToolRenderable);

		GL gl = glState.getGL();
	}

}
