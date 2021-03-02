package de.grogra.imp3d.glsl.light.shadow;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.light.LightPos;
import de.grogra.imp3d.glsl.renderpass.FullQualityRenderPass;
import de.grogra.imp3d.objects.DirectionalLight;

public class GLSLShadowParallel extends GLSLShadowMap {
	
	/**
	 * Sets the Projections diameter to width (should be )
	 * @param width
	 */
	public void setProjRadius(double radius){
		this.projRadius = radius;
	}
	
	double projRadius = 10;
	
	@Override
	public Matrix4d getViewToClip() {
		double one_over_w = 1.0 / projRadius;
		double neg_one_over_depth = -1.0 / projRadius;

		ViewToClip.setZero();
		ViewToClip.m00 = one_over_w;
		ViewToClip.m11 = one_over_w;
		ViewToClip.m22 = neg_one_over_depth;
		ViewToClip.m33 = 1;

		return ViewToClip;
	}
	
	private Vector3d translation;
	
	@Override
	public Matrix4d getLightToView() {
		LightTransf.setTranslation(translation);
		return super.getLightToView();
	}
	
	private final static int DEFAULT_SIZE = 2048;

	@Override
	public boolean create(GL gl) {
		return create(gl, DEFAULT_SIZE, DEFAULT_SIZE);
	}
	
	@Override
	public boolean create(GL gl, int width, int height) {
		if(index != 0) 
			if((this.width == width) && (this.height == height))
				return true;
			else
				delete(gl, false);
		
		// generate id for background texture
		int[] texId = new int[1];
		gl.glGenTextures (1, texId, 0);
		index = texId[0];

		// create an opengl texture
		gl.glBindTexture (GL.GL_TEXTURE_2D, index);

		gl.glTexImage2D (GL.GL_TEXTURE_2D,
				0 /* level of detail, needed for mip-mapping */,
				GL.GL_DEPTH_COMPONENT24, width, height, 0, GL.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE, null);

		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
				GL.GL_CLAMP_TO_BORDER);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				GL.GL_CLAMP_TO_BORDER);
		gl.glTexParameteri (GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri (GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);

		float borderColor[] = { 1.0f, 1.0f, 1.0f, 1.0f }; 
		gl.glTexParameterfv(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_BORDER_COLOR, borderColor, 0);
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_COMPARE_MODE_ARB, GL.GL_COMPARE_R_TO_TEXTURE);

	    //Shadow comparison should be true (ie not in shadow) if r<=texture
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_COMPARE_FUNC_ARB, GL.GL_LEQUAL);
		
		type = GL.GL_UNSIGNED_BYTE;
		this.width = width;
		this.height = height;
		this.type = GL.GL_DEPTH_COMPONENT;
		this.internalFormat = GL.GL_DEPTH_COMPONENT;
		this.texType = GL.GL_TEXTURE_2D;
		
		return gl.glGetError() != GL.GL_NO_ERROR;
}
	
	@Override
	public void fill(GLSLDisplay disp, OpenGLState glState) {
		int NEW_STAMP = disp.getView ().getGraph ().getStamp ();
		if(NEW_STAMP == GRAPH_STAMP)
			return;
		if(!invalid)
			return;
		
//		if(glState.volume.getRadius() <= 0.0)
//			return;
		
		setProjRadius( glState.volume.getRadius() );
//		System.err.println(glState.volume.getRadius());
		translation = glState.volume.getCenter() ;

		GL gl = glState.getGL();

		// XXX: Not managed
		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, 
				GL.GL_DEPTH_ATTACHMENT_EXT, 
				GL.GL_TEXTURE_2D, 
				getIndex(), 0);

		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();

		glState.loadMatrixd(getViewToClip());
		gl.glMatrixMode(GL.GL_MODELVIEW);
		
		Matrix4d round = getLightToView();

		//XXX: FIX HERE
		gl.glPolygonOffset(1.5f, 4);
//		gl.glPolygonOffset(1, 1);
		gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);

		renderCachedScene(disp, glState, round);	

		gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
		
		GRAPH_STAMP = NEW_STAMP;

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}
	
	@Override
	public int getSize() { return 1; }

	@Override
	public GLSLShadowMap getInstance() {
		return new GLSLShadowParallel();
	}
	
	@Override
	public void setupTextureMatrices(OpenGLState glState, Matrix4d ViewToWorld, LightPos light) {
		// Now setup WorldViewToLightClip matrix for reading back values
		GL gl = glState.getGL();
		
		Matrix4d shadowTransform = new Matrix4d();
		shadowTransform.setIdentity();
		
		Matrix4d bias = new Matrix4d();
		setBiasMatrix(bias);

		shadowTransform.mul(bias);
		shadowTransform.mul(getViewToClip());			
		shadowTransform.mul(getLightToView());
		shadowTransform.mul(ViewToWorld);
		
		gl.glActiveTexture(GL.GL_TEXTURE7);		
		gl.glBindTexture(GL.GL_TEXTURE_2D, getIndex());

		gl.glMatrixMode(GL.GL_TEXTURE);
		gl.glActiveTexture(GL.GL_TEXTURE0 + FullQualityRenderPass.CUSTOM_MATRIX_1);
		glState.loadMatrixd(shadowTransform);
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}

	@Override
	public Class<?> getDefaultLightType() {
		return DirectionalLight.class;
	}
}
