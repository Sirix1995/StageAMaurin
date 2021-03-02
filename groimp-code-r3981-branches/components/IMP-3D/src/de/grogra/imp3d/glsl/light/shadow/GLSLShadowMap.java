package de.grogra.imp3d.glsl.light.shadow;

import java.util.Iterator;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.GLSLUpdateCache;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.GLDisplay.GLVisitor;
import de.grogra.imp3d.glsl.light.LightPos;
import de.grogra.imp3d.glsl.material.GLSLMaterial;
import de.grogra.imp3d.glsl.utility.Drawable;
import de.grogra.imp3d.glsl.utility.TextureRenderTarget;

/**
 * This class is the base for all shadowmaps.
 * 
 * @author Konni Hartmann
 */
public abstract class GLSLShadowMap extends TextureRenderTarget {

	GLSLUpdateCache newVisit = null;

	Matrix4d LightTransf;

	public void setLightTransf(LightPos light) {
		LightTransf = light.getLightTransform();
		this.changed = true;
	}

	Matrix4d LightToView = new Matrix4d();
	Matrix4d ViewToClip = new Matrix4d();
	Vector3d camPos = new Vector3d();
	Vector3d camDir = null;
	boolean changed = true;

	public void setCamDir(Vector3d camDir) {
		this.camDir = camDir;
		this.changed = true;
	}

	public void setCamPos(Point4d camPos) {
		this.camPos.x = camPos.x;
		this.camPos.y = camPos.y;
		this.camPos.z = camPos.z;

		this.changed = true;
	}

	void setRotateFront(Matrix4d m) {
		m.m00 = 0;
		m.m01 = 0;
		m.m02 = -1;
		m.m10 = 0;
		m.m11 = 1;
		m.m12 = 0;
		m.m20 = 1;
		m.m21 = 0;
		m.m22 = 0;
		m.m30 *= 1;
		m.m31 *= 1;
		m.m32 *= -1;
	}

	void setRotateBack(Matrix4d m) {
		m.m00 = 0;
		m.m01 = 0;
		m.m02 = 1;
		m.m10 = 0;
		m.m11 = 1;
		m.m12 = 0;
		m.m20 = -1;
		m.m21 = 0;
		m.m22 = 0;
		m.m30 *= -1;
		m.m31 *= 1;
		m.m32 *= 1;
	}

	void setRotateTop(Matrix4d m) {
		m.m00 = 1;
		m.m01 = 0;
		m.m02 = 0;
		m.m10 = 0;
		m.m11 = 0;
		m.m12 = 1;
		m.m20 = 0;
		m.m21 = -1;
		m.m22 = 0;
		m.m30 *= 1;
		m.m31 *= -1;
		m.m32 *= 1;
	}

	void setRotateBottom(Matrix4d m) {
		m.m00 = 1;
		m.m01 = 0;
		m.m02 = 0;
		m.m10 = 0;
		m.m11 = 0;
		m.m12 = -1;
		m.m20 = 0;
		m.m21 = 1;
		m.m22 = 0;
		m.m30 *= 1;
		m.m31 *= 1;
		m.m32 *= -1;
	}

	void setRotateLeft(Matrix4d m) {
		m.m00 = -1;
		m.m01 = 0;
		m.m02 = 0;
		m.m10 = 0;
		m.m11 = 1;
		m.m12 = 0;
		m.m20 = 0;
		m.m21 = 0;
		m.m22 = -1;
		m.m30 *= -1;
		m.m31 *= 1;
		m.m32 *= -1;
	}

	void setRotateRight(Matrix4d m) {
		m.m00 = 1;
		m.m01 = 0;
		m.m02 = 0;
		m.m10 = 0;
		m.m11 = 1;
		m.m12 = 0;
		m.m20 = 0;
		m.m21 = 0;
		m.m22 = 1;
		m.m30 *= 1;
		m.m31 *= 1;
		m.m32 *= 1;
	}

	static final int NO_ROTATION = -1;

	public Matrix4d getLightToView() {
		return getLightToView(NO_ROTATION);
	}

	public Matrix4d getLightToView(int side) {
		if (changed || side > 0) {
			LightToView = new Matrix4d(LightTransf);
			switch (side) {
			case 0:
				// GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
				setRotateFront(LightToView);
				break;
			case 1:
				// GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
				setRotateBack(LightToView);
				break;
			case 2:
				// GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
				setRotateTop(LightToView);
				break;
			case 3:
				// GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
				setRotateBottom(LightToView);
				break;
			case 4:
				// GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
				setRotateRight(LightToView);
				break;
			case 5:
				// GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
				setRotateLeft(LightToView);
				break;
			default:
				break;
			}
			LightToView.invert();
		}
		return LightToView;
	}

	public Matrix4d getViewToClip() {
		return ViewToClip;
	};

	protected int GRAPH_STAMP = -1;
	protected boolean invalid = true;

	/**
	 * Mark this Shadow map as invalid may be called to mark changes in matrices
	 * that are not covered by changes in graph (for example caching may present
	 * a instance to other lightsources)
	 */
	public void setInvalid() {
		this.invalid = true;
		this.GRAPH_STAMP = -1;
	}

	protected void renderCachedScene(GLSLDisplay disp, OpenGLState glState,
			Matrix4d round) {

		GLVisitor old = disp.getVisitor();
		
		if(newVisit == null)
			newVisit = new GLSLUpdateCache(disp);
		
		newVisit.init(disp.getRenderGraphState(), round, 0);
		disp.setVisitor(newVisit);
		
		GL gl = glState.getGL();

		glState.enable(OpenGLState.CULLING);
		glState.setFaceCullingMode(GL.GL_FRONT);

		Iterator<Drawable> it = glState.deferredSolidRenderable.iterator();
		while (it.hasNext()) {
			Drawable dr = it.next();
			// Object shape = dr.getShape();
			// if ((shape instanceof Plane)) {
			// continue;
			// }
			dr.activateGLSLShader(glState, disp, true);
			dr.draw(disp, newVisit, round, false);
		}
		
		glState.setFaceCullingMode(GL.GL_BACK);
		glState.disable(OpenGLState.CULLING);

		it = glState.deferredTranspRenderable.iterator();
		while (it.hasNext()) {
			Drawable dr = it.next();
			// Object shape = dr.getShape();
			// if ((shape instanceof Plane)) {
			// continue;
			// }
			dr.activateGLSLShader(glState, disp, true, true);
			dr.draw(disp, newVisit, round, false);
		}

		disp.setVisitor(old);
	}

	public abstract void fill(GLSLDisplay disp, OpenGLState glState);

	public abstract boolean create(GL gl);

	public abstract boolean create(GL gl, int width, int height);

	public abstract int getSize();

	public abstract GLSLShadowMap getInstance();
	
	public abstract Class<?> getDefaultLightType();

	public abstract void setupTextureMatrices(OpenGLState glState,
			Matrix4d ViewToWorld, LightPos light);

	void setBiasMatrix(Matrix4d m) {
		m.m00 = 0.5;
		m.m01 = 0.0;
		m.m02 = 0.0;
		m.m03 = 0.5;
		m.m10 = 0.0;
		m.m11 = 0.5;
		m.m12 = 0.0;
		m.m13 = 0.5;
		m.m20 = 0.0;
		m.m21 = 0.0;
		m.m22 = 0.5;
		m.m23 = 0.5;
		m.m30 = 0.0;
		m.m31 = 0.0;
		m.m32 = 0.0;
		m.m33 = 1.0;
	}
	
	@Override
	public void delete(OpenGLState glState, boolean javaonly) {
		super.delete(glState, javaonly);
		setInvalid();
	}
}
