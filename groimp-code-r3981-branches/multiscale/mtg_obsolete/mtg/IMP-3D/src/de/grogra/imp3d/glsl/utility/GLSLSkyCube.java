package de.grogra.imp3d.glsl.utility;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;

/**
 * Implements a textureCube to be used as a reference for Sky-Illumination.
 * @author Konni Hartman
 */
public class GLSLSkyCube extends GLSLTexture  {
	
	Matrix4d LightToView = new Matrix4d();
	Matrix4d ViewToClip = new Matrix4d();
	
	void getProjectionMatrix(Matrix4d m, float zNear, float zFar){
	double neg_depth = zNear-zFar;

	m.setZero();
	m.m00 = 1;
	m.m11 = 1;
	m.m22 = (zFar + zNear)/neg_depth;
	m.m32 = -1;
	m.m23 = 2.0f*(zNear*zFar)/neg_depth;
	}
	
	private final static int DEFAULT_SIZE = 256;

	public boolean create(GL gl) {
		return create(gl, DEFAULT_SIZE, DEFAULT_SIZE);
	}
	
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
		gl.glBindTexture (GL.GL_TEXTURE_CUBE_MAP, index);

		for(int i = 0; i < 6; ++i) {
			gl.glTexImage2D (GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
				0 /* level of detail, needed for mip-mapping */,
				GL.GL_RGBA16F_ARB, width, height, 0, GL.GL_BGRA, GL.GL_FLOAT, null);
		}
		
		gl.glTexParameterf(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S,
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_T,
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_R, 
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri (GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri (GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);
//				GL.GL_LINEAR_MIPMAP_LINEAR);
//		gl.glGenerateMipmapEXT(GL.GL_TEXTURE_CUBE_MAP);
		
		type = GL.GL_FLOAT;
		this.width = width;
		this.height = height;
		this.type = GL.GL_RGBA16F_ARB;
//		this.type = GL.GL_RGBA8;
		this.internalFormat = GL.GL_BGRA;
		this.texType = GL.GL_TEXTURE_CUBE_MAP;

		return gl.glGetError() != GL.GL_NO_ERROR;
}
	
	public Matrix4d setupAsRTAttachment(GL gl, int direction) {
		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, 
				GL.GL_COLOR_ATTACHMENT0_EXT, 
				GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X+direction, 
				getIndex(), 0);
		return getLightToView(direction); 
	}
	
	public void finish(GL gl) {
		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, 
				GL.GL_COLOR_ATTACHMENT0_EXT, 
				GL.GL_TEXTURE_2D, 
				0, 0);
		bindTo(gl, GL.GL_TEXTURE0);
//		gl.glGenerateMipmapEXT(GL.GL_TEXTURE_CUBE_MAP);
//		gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, 0);
	}
		
	public Matrix4d getLightToView(int side) {
			switch (side) {
			case 0:
//				GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X;				
				setRotateFront(LightToView);				
				break;
			case 1:
//				GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
				setRotateBack(LightToView);				
				break;
			case 2:
//				GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
				setRotateTop(LightToView);				
				break;
			case 3:
//				GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
				setRotateBottom(LightToView);				
				break;
			case 4:
//				GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
				setRotateRight(LightToView);				
				break;
			case 5:
//				GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;				
				setRotateLeft(LightToView);				
				break;
			default:
				break;
			}
		return LightToView;
	}
	
	public Matrix4d getViewToClip() {
		return ViewToClip;
	};
	
	void setRotateFront(Matrix4d m) {
		m.setZero();
		m.m02 =  -1;
		m.m11 =  -1;
		m.m20 =  -1;
		m.m33 =	  1;
	}

	void setRotateBack(Matrix4d m) {
		m.setZero();
		m.m02 =   1;
		m.m11 =  -1;
		m.m20 =   1;
		m.m33 =	  1;
	}

	// ok!
	void setRotateTop(Matrix4d m) {
		m.setZero();
		m.m00 =   1;
		m.m12 =  -1;
		m.m21 =   1;
		m.m33 =	  1;		
	}
	
	// ok!
	void setRotateBottom(Matrix4d m) {
		m.setZero();
		m.m00 =   1;
		m.m12 =   1;
		m.m21 =  -1;
		m.m33 =	  1;		
	}
	
	// ok!
	void setRotateLeft(Matrix4d m) {
		m.setZero();
		m.m00 =  -1;
		m.m11 =  -1;
		m.m22 =   1;
		m.m33 =	  1;		
	}

	void setRotateRight(Matrix4d m) {
		m.setZero();
		m.m00 =   1;
		m.m11 =  -1;
		m.m22 =  -1;
		m.m33 =	  1;		
	}
	
}
