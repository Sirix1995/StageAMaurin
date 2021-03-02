package de.grogra.imp3d.glsl.light.shadow;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.light.LightPos;
import de.grogra.imp3d.glsl.renderpass.FullQualityRenderPass;
import de.grogra.imp3d.objects.PointLight;

/**
 * Implements a textureCube to be used in point light shadow generation and
 * rendering. The scene is rendered for each face of the cube resulting in 6
 * passes for each point light.
 * 
 * @author Konni Hartman
 */
public class GLSLShadowCube extends GLSLShadowMap {

	void getProjectionMatrix(Matrix4d m, float zNear, float zFar) {
		double neg_depth = zNear - zFar;

		m.setZero();
		m.m00 = 1;
		m.m11 = 1;
		m.m22 = (zFar + zNear) / neg_depth;
		m.m32 = -1;
		m.m23 = 2.0f * (zNear * zFar) / neg_depth;
	}

	private final static int DEFAULT_SIZE = 512;

	@Override
	public boolean create(GL gl) {
		return create(gl, DEFAULT_SIZE, DEFAULT_SIZE);
//		int probe_size = DEFAULT_SIZE;
//		while ((probe_size >= 2) &&!create(gl, probe_size, probe_size)) {
//			probe_size /= 2;
//		};
//		return probe_size != 1;
	}

	@Override
	public boolean create(GL gl, int width, int height) {
		if (index != 0)
			if ((this.width == width) && (this.height == height))
				return true;
			else
				delete(gl, false);
		// generate id for background texture
		int[] texId = new int[1];
		gl.glGenTextures(1, texId, 0);
		index = texId[0];

		// create an opengl texture
		gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, index);

			for (int i = 0; i < 6; ++i) {
				gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0 /*
																		 * level
																		 * of
																		 * detail
																		 * ,
																		 * needed
																		 * for
																		 * mip
																		 * -mapping
																		 */,
						GL.GL_DEPTH_COMPONENT24, width, height, 0,
						GL.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE, null);
			}

		gl.glTexParameterf(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S,
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_T,
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_R,
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);

		type = GL.GL_UNSIGNED_BYTE;
		this.width = width;
		this.height = height;
		this.type = GL.GL_DEPTH_COMPONENT;
		this.internalFormat = GL.GL_DEPTH_COMPONENT;
		this.texType = GL.GL_TEXTURE_CUBE_MAP;

		return gl.glGetError() != GL.GL_NO_ERROR;
	}

	@Override
	public void fill(GLSLDisplay disp, OpenGLState glState) {
		int NEW_STAMP = disp.getView().getGraph().getStamp();
		if (NEW_STAMP == GRAPH_STAMP)
			return;
		if (!invalid)
			return;

		GL gl = glState.getGL();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		// this.proj.getTransformation (0.01f, 2000.0f, ViewToClip, null);
		getProjectionMatrix(ViewToClip, 0.01f, 2000.0f);
		glState.loadMatrixd(getViewToClip());
		gl.glMatrixMode(GL.GL_MODELVIEW);

		// TODO: FIX HERE
		gl.glPolygonOffset(1.1f, 4);
		gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);

		Matrix4d round;
		for (int i = 0; i < 6; ++i) {
			round = getLightToView(i);

			gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT,
					GL.GL_DEPTH_ATTACHMENT_EXT,
					GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, getIndex(), 0);
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

			renderCachedScene(disp, glState, round);
		}
		GRAPH_STAMP = NEW_STAMP;

		gl.glPolygonOffset(0, 0);
		gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}

	@Override
	public Matrix4d getLightToView() {
		Matrix4d m = new Matrix4d(LightTransf);
		m.m00 = 1;
		m.m01 = 0;
		m.m02 = 0;
		m.m10 = 0;
		m.m11 = -1;
		m.m12 = 0;
		m.m20 = 0;
		m.m21 = 0;
		m.m22 = -1;
		m.m30 *= 1;
		m.m31 *= -1;
		m.m32 *= -1;
		m.invert();
		return m;
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public GLSLShadowMap getInstance() {
		return new GLSLShadowCube();
	}

	@Override
	public void setupTextureMatrices(OpenGLState glState, Matrix4d ViewToWorld,
			LightPos light) {
		// Now setup WorldViewToLightClip matrix for reading back values
		GL gl = glState.getGL();
		// Matrix4d ViewToWorld = new Matrix4d();
		// ViewToWorld.invert(disp.getView3D().getCamera().getWorldToViewTransformation());

		Matrix4d shadowTransform = new Matrix4d();
		shadowTransform.setIdentity();

		shadowTransform.mul(getLightToView());
		shadowTransform.mul(ViewToWorld);

		gl.glActiveTexture(GL.GL_TEXTURE7);
		gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, getIndex());
		// gl.glLoadIdentity();
		gl.glMatrixMode(GL.GL_TEXTURE);
		gl.glActiveTexture(GL.GL_TEXTURE0
				+ FullQualityRenderPass.CUSTOM_MATRIX_1);
		glState.loadMatrixd(shadowTransform);
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}

	@Override
	public Class<?> getDefaultLightType() {
		return PointLight.class;
	}
}
