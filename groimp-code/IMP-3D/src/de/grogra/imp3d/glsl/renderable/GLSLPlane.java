package de.grogra.imp3d.glsl.renderable;

import javax.media.opengl.GL;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.SingularMatrixException;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.Camera;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.renderpass.FullQualityRenderPass;
import de.grogra.imp3d.glsl.renderpass.RenderPass;
import de.grogra.imp3d.glsl.utility.GLSLManagedShader;
import de.grogra.imp3d.glsl.utility.GLSLShader;
import de.grogra.imp3d.objects.Plane;
import de.grogra.imp3d.shading.Shader;

public class GLSLPlane extends GLSLRenderable {

	Matrix4d localToGlobal = new Matrix4d();

	GLSLShader plane = null;
	class PlaneShader extends GLSLShader {
		public PlaneShader(OpenGLState glState) {
			super(glState);
		}

		/**
		 * Standard Vertex Shader used by many other Shaders
		 */
		final String vStdSrc[] = { 
				"#version 110\n", 
				"varying vec3 normal;\n",
				"varying vec2 TexUnit2;\n",
				"void main() {", 
				"  gl_Position = ftransform();",
				"  TexUnit2 = gl_MultiTexCoord1.st;", 
				"}" };

		@Override
		protected String[] getVertexShader(Object data) {
			return vStdSrc;
		};

		final String[] simpleDepthF = {
				"#version 110\n",
				"varying vec2 TexUnit2;\n",

				"uniform mat3 normalMat;\n",

				"void main(void)\n",
				"{\n",
				"  vec3 normal = normalize(normalMat * vec3(0.0, 0.0, 1.0));",
				"  vec4 base = (gl_TextureMatrix[2] * vec4(0.0, 0.0, 0.0, 1.0));",
				"  vec3 pos = normalize(vec3(TexUnit2, -1.0));\n",

				"  float dotP = dot(normal, pos);",
				"  if (dotP >= 0.0) {\n",
				"    discard;\n",
				"  }\n",

				"  float t = ( dot(base.xyz, normal) ) / dotP;\n ",
				"  if(t < 0.0) discard;",
				"  vec4 clipPos = vec4(t * pos, 1.0);",
				"  clipPos = gl_TextureMatrix[0] * clipPos;",
				"  gl_FragDepth =  clamp((clipPos.z / clipPos.w)*0.5+0.5, 0.0, 1.0);",
				"}" };

		@Override
		protected String[] getFragmentShader(Object data) {
			return simpleDepthF;
		}
	};

	Matrix3f mat = new Matrix3f();

	@Override
	public void drawAlt(OpenGLState glState, GLSLDisplay rs) {
		try {
			localToGlobal.invert(rs.getTransformation(null));
		} catch (SingularMatrixException e) {
			return;
		}
		
		GL gl = glState.getGL();
		
//		GLSLDisplay.printDebugInfoN("Render Plane");

		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);

		gl.glMatrixMode(GL.GL_TEXTURE);

		gl.glActiveTexture(GL.GL_TEXTURE0 + FullQualityRenderPass.CUSTOM_MATRIX_1);
//		System.err.println(rs.getTransformation(null));
		glState.loadMatrixd(rs.getTransformation(null));
		// glState.loadMatrixd(glState.getWorldToView());
		gl.glActiveTexture(GL.GL_TEXTURE0 + FullQualityRenderPass.CUSTOM_MATRIX_2);
		glState.loadMatrixd(localToGlobal);
		// glState.loadMatrixd(glState.getInvWorldToView());
		gl.glMatrixMode(GL.GL_MODELVIEW);

		//XXX: 	Suboptimal since getUniformLocation takes a lot time 
		//		and does not change between linking
		normalMatLoc = gl.glGetUniformLocation(glState.getActiveShader(),"normalMat");
		rs.getTransformation(null).getRotationScale(mat);
		mat.invert();
		mat.transpose();
		gl.glUniformMatrix3fv(normalMatLoc, 1, false, rs.getCurrentGLState()
				.toGLMatrix3f(mat), 0);
		
		RenderPass.ViewOrtho(glState);

		Camera c = rs.getView3D().getCamera();
		RenderPass.drawPrjQuad(glState, c);

		RenderPass.ViewPerspective(glState);

		gl.glPopAttrib();
	}
	
	int normalMatLoc = -1;

	@Override
	public void draw(OpenGLState glState, RenderState rs) {
		rs.drawPlane(null, 0, false, null);
	}

	/**
	 * Since planes do not have internal values just share the whole plane
	 */
	@Override
	public GLSLRenderable getInstance() {
		return this;
	}

	@Override
	public Class<?> instanceFor() {
		return Plane.class;
	}

	@Override
	public void updateInstance(Object reference, Object state,
			boolean asNode, GraphState gs) {
	}

	@Override
	public boolean isShaderDependant(boolean depthonly) {
		return true;
	}

	@Override
	public void activateShader(OpenGLState glState, GLSLDisplay disp,
			Shader shader, boolean depthonly) {
		if (depthonly) {
			if(plane == null)
				plane = new PlaneShader(glState);
			plane.activateShader(glState, disp, null);
		} else {
			glState.setShaderConfSwitch(OpenGLState.INFINITY_PLANE_MATERIAL);
			disp.findAndActivateShader(shader);
			glState.setShaderConfSwitch(OpenGLState.DEFAULT_MATERIAL);
		}
	}

	@Override
	public GLSLManagedShader findShader(OpenGLState glState, GLSLDisplay disp,
			Shader shader) {
		glState.setShaderConfSwitch(OpenGLState.INFINITY_PLANE_MATERIAL);
		GLSLManagedShader sh = disp.findShader(shader);
		glState.setShaderConfSwitch(OpenGLState.DEFAULT_MATERIAL);
		return sh;
	}

}
