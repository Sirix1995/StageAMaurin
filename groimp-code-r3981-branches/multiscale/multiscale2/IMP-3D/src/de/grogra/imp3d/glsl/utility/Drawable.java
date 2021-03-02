package de.grogra.imp3d.glsl.utility;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;
import javax.vecmath.SingularMatrixException;

import de.grogra.imp.View;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.GLSLUpdateCache;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.material.GLSLMaterial;
import de.grogra.imp3d.glsl.renderable.GLSLRenderable;
import de.grogra.imp3d.glsl.renderpass.FullQualityRenderPass;
import de.grogra.imp3d.shading.Shader;
import de.grogra.vecmath.Matrix34d;
import de.grogra.vecmath.geom.VolumeBase;

public class Drawable implements Comparable<Drawable> {
	protected Matrix4d worldTransform = new Matrix4d();
	protected Matrix4d cachedWorldTransform = new Matrix4d();
	protected int layer;

	protected GLSLRenderable shape;

	protected Shader s;
	protected GLSLMaterial sh = null;
	protected boolean asNode;
	protected int type;

	private VolumeBase BoundingVolume;
	private float radius;

	private boolean changed;

	public GLSLRenderable getShape() {
		return shape;
	}

	public Shader getShader() {
		return s;
	}

	public void setShader(Shader s) {
		this.s = s;
	}

	public void setGLSLShader(GLSLMaterial sh) {
		this.sh = sh;
	}

	public GLSLMaterial getGLSLShader() {
		return sh;
	}
	
	public Drawable(GLSLRenderable shape, Shader s, GLSLMaterial sh,
			boolean asNode, int layer, Matrix4d worldTransform,
			Matrix4d cachedWorldTransform) {
		init(shape, s, sh, asNode, layer, worldTransform, cachedWorldTransform);
	}

	public Drawable(GLSLRenderable shape, Shader s, GLSLMaterial sh,
			boolean asNode, int layer, Matrix34d worldTransform,
			Matrix34d cachedWorldTransform) {
		init(shape, s, sh, asNode, layer, worldTransform, cachedWorldTransform);
	}

	public void init(GLSLRenderable shape, Shader s, GLSLMaterial sh,
			boolean asNode, int layer, Matrix4d worldTransform,
			Matrix4d cachedWorldTransform) {
		init(shape, s, sh, asNode, layer);
		this.worldTransform.set(worldTransform);
		this.cachedWorldTransform.set(cachedWorldTransform);
	}

	public void init(GLSLRenderable shape, Shader s, GLSLMaterial sh,
			boolean asNode, int layer, Matrix34d worldTransform,
			Matrix34d cachedWorldTransform) {
		init(shape, s, sh, asNode, layer);
		clearLastColumn(this.worldTransform);
		clearLastColumn(this.cachedWorldTransform);
		updateMat(this.worldTransform, worldTransform);
		updateMat(this.cachedWorldTransform, cachedWorldTransform);
	}

	private void clearLastColumn(Matrix4d mat) {
		mat.m30 = 0;
		mat.m31 = 0;
		mat.m32 = 0;
		mat.m33 = 1;
	}

	public void init(GLSLRenderable shape, Shader s, GLSLMaterial sh,
			boolean asNode, int layer) {
		this.shape = shape;
		this.s = s;
		this.sh = sh;
		this.asNode = asNode;
		this.layer = layer;
		this.changed = true;
		this.hasNoVolume = false;
		this.BoundingVolume = null;
	}

	public Matrix4d getWorldTransform() {
		return worldTransform;
	}

	public void activateGLSLShader(OpenGLState glState, GLSLDisplay disp) {
		activateGLSLShader(glState, disp, false);
	}

	public void activateGLSLShader(OpenGLState glState, GLSLDisplay disp,
			boolean depthonly) {
		activateGLSLShader(glState, disp, depthonly, false);
	}

	public void activateGLSLShader(OpenGLState glState, GLSLDisplay disp,
			boolean depthonly, boolean discard) {
		// if no basis exists no shader can be used
		if (s == null) {
			disp.getCurrentGLState().setActiveProgram(0);
			return;
		}

		// if we want cached shader, just activate it
		if ((sh != null) && !depthonly) {
			sh.activateShader(glState, disp, s);
			return;
		}

		if (sh == null) {
			// If we have not yet have the standard shader, cache it
			if (disp.isOptionAltDrawing() && (shape != null)
					&& shape.isShaderDependant(false)) {
				// if shape handles shader, do it
				sh = (GLSLMaterial) shape.findShader(glState, disp, s);
			} else
				sh = (GLSLMaterial) disp.findShader(s);

			// if we only need standard shader we have everything
			if (!depthonly) {
				sh.activateShader(glState, disp, null);
				return;
			}
		}

		if (discard && (!sh.isOpaque(s))) {
			// we have a full shader info but shader is transp. lets get a alpha
			// test shader
			glState.setShaderConfSwitch(OpenGLState.TRANSP_DEPTH_ONLY_MATERIAL);
			disp.findAndActivateShader(s);
			glState.setShaderConfSwitch(OpenGLState.DEFAULT_MATERIAL);
			return;
		}

		if (disp.isOptionAltDrawing() && (shape != null) 
				&& shape.isShaderDependant(true)) {
			// if shape handles shader, get a shadowshader
			shape.activateShader(glState, disp, s, true);
			return;
		}

		if (!sh.mayDiscard(s)) {
			// shader is not transp or will discard fragments so use fixed
			// pipeline
			disp.getCurrentGLState().setActiveProgram(0);
			return;
		}

		// all fails so shader is fully needed
		disp.findAndActivateShader(s);
	}

	private static final Matrix4d mat = new Matrix4d();

	// public void draw(GLSLDisplay disp, Renderable shape, Object data,
	// GLSLCachedVisitor visit, Matrix4d worldToView) {
	// draw(disp, shape, data, visit, worldToView, true);
	// }

	boolean hasNoVolume = true;

	public void rebuildVolumeData(OpenGLState glState) {
		shape.draw(glState, glState.volume);

		this.BoundingVolume = glState.volume.getCurrentVolume();
		this.radius = glState.volume.getCurrentRadius();
		if (this.BoundingVolume == null)
			hasNoVolume = false;
		changed = false;
	}

	static Matrix4d localToGlobal = new Matrix4d();

	protected void draw(GLSLDisplay disp, OpenGLState glState) {
		GL gl = glState.getGL();		
//		if ((glState.csc.getCurrentShader() != null)
//				&& (glState.csc.getCurrentShader().getConfig() != null)
//				&& (glState.csc.getCurrentShader().getConfig().getBit(
//						ShaderConfiguration.USE_GLOBAL_POS)) ) {
		// Since sh saves one version of a shader and global transformations are the same for all
		// configurations this should be enough... it would be better the ask for the current active shader
		if ((sh != null)
				&& (sh.getConfig() != null)
				&& (sh.getConfig().getBit(ShaderConfiguration.USE_GLOBAL_POS)) ) {
			gl.glMatrixMode(GL.GL_TEXTURE);
			gl.glActiveTexture(GL.GL_TEXTURE0
					+ FullQualityRenderPass.CUSTOM_MATRIX_2);
			glState.loadMatrixd(worldTransform);
			gl.glMatrixMode(GL.GL_MODELVIEW);
		}

		if (disp.isVisible(layer))
			if (disp.isOptionAltDrawing())
				shape.drawAlt(glState, disp);
			else
				shape.draw(glState, disp);

		// if (changed && !hasNoVolume) {
		if (glState.volume.needsData()) {
			// System.err.println("update for volume");
			rebuildVolumeData(glState);
		}
	}

	public void drawAndUpdate(GLSLDisplay disp, GLSLUpdateCache visit,
			Matrix4d worldToView) {
		mat.mul(worldToView, worldTransform);
		cachedWorldTransform.set(mat);
		visit.setCurrentTransformation(mat);
		draw(disp, disp.getCurrentGLState());
	}

	public void draw(GLSLDisplay disp, GLSLUpdateCache visit,
			Matrix4d worldToView, boolean normal) {
		if (!normal) {
			mat.mul(worldToView, worldTransform);
		} else {
			mat.set(cachedWorldTransform);
			// if (!changed
			// && !(BoundingVolume == null)
			// &&
			// !disp.getSh().glState.frustumCullingTester.isSphereInFrustum(mat,
			// radius))
			// return;
		}
		visit.setCurrentTransformation(mat);
		draw(disp, disp.getCurrentGLState());
	}

	void updateMat(Matrix4d local, Matrix34d val) {
		if (val == null)
			return;
		local.m00 = val.m00;
		local.m01 = val.m01;
		local.m02 = val.m02;
		local.m03 = val.m03;
		local.m10 = val.m10;
		local.m11 = val.m11;
		local.m12 = val.m12;
		local.m13 = val.m13;
		local.m20 = val.m20;
		local.m21 = val.m21;
		local.m22 = val.m22;
		local.m23 = val.m23;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int compareTo(Drawable o) {
		return this.sh.getShaderProgramNumber() - o.sh.getShaderProgramNumber();
	}

	// @SuppressWarnings("unchecked")
	// public void attributeChanged(AttributeChangeEvent event) {
	// Attribute<?> att = event.getAttribute();
	// if(att == GlobalTransformation.ATTRIBUTE) {
	// Matrix34dPair mat = (Matrix34dPair)event.getGraphState().getObject(data,
	// true, GlobalTransformation.ATTRIBUTE);
	// updateMat(this.worldTransform, mat.get(false));
	// } else
	// if(att == Attributes.SHADER) {
	// Shader val = (Shader)event.getGraphState().getObjectDefault (data,
	// asNode,
	// Attributes.SHADER, RGBAShader.GRAY);
	// if(val == null)
	// val = RGBAShader.GRAY;
	// if(val instanceof ShaderRef)
	// val = ((ShaderRef)val).resolve();
	// if(this.s != val) {
	// this.s = val;
	// this.sh = null;
	// }
	// } else
	// if(att == de.grogra.graph.Attributes.LAYER) {
	// Layer = event.getGraphState().getIntDefault(data, asNode,
	// de.grogra.graph.Attributes.LAYER, 0);
	// }
	// }
}
