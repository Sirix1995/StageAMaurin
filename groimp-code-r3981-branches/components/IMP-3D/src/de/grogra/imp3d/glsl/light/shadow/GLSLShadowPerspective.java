package de.grogra.imp3d.glsl.light.shadow;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.light.LightPos;
import de.grogra.imp3d.glsl.renderpass.FullQualityRenderPass;
import de.grogra.imp3d.objects.SpotLight;

public class GLSLShadowPerspective extends GLSLShadowMap {
	
	@Override
	public Matrix4d getViewToClip() {
		getProjectionMatrix(ViewToClip, FoV, 0.01f, 2000.f);
		return ViewToClip;
	}
	
	void getProjectionMatrix(Matrix4d m, float FoV, float zNear, float zFar){
		double neg_depth = zNear-zFar;
		double h = 1/Math.tan(FoV / 2.0);
		m.setZero();
		m.m00 = h;
		m.m11 = h;
		m.m22 = (zFar + zNear)/neg_depth;
		m.m32 = -1;
		m.m23 = 2.0f*(zNear*zFar)/neg_depth;
	}
	
	float FoV = (float)(Math.PI / 3.0);
	
	public void setFieldOfView(float FoV) {
		this.FoV = FoV;
	}
	
	private final static int DEFAULT_SIZE = 1024;

	@Override
	public boolean create(GL gl){
		return create(gl, DEFAULT_SIZE, DEFAULT_SIZE);
	}
	
	@Override
	public boolean create(GL gl, int width, int height){
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
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri (GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri (GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);

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
		
		GL gl = glState.getGL();
		
		// XXX: Not managed!!
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
		gl.glPolygonOffset(1.1f, 4);
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
		return new GLSLShadowPerspective();
	}
	
	@Override
	public void setLightTransf(LightPos light) {
		super.setLightTransf(light);
		setFieldOfView(((SpotLight)light.getLight()).getOuterAngle()*2);
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
		return SpotLight.class;
	}

}
