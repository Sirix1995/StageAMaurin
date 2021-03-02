package de.grogra.imp3d.glsl.utility;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.OpenGLState;

/**
 * Simple Texture class base for some "deferred Textures" which setup data but create
 * glTextures only on demand. Also used as base for ShadowMaps.
 * @author Konni Hartmann
 *
 */
public class GLSLTexture implements GLSLOpenGLObject {
	protected int width;
	protected int height;
	protected int index = 0;
	
	protected int internalFormat;
	protected int texType;
	protected int type;
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getType() {
		return type;
	}
	
	public int getTexType() {
		return texType;
	}
	
	boolean create(GL gl, int width, int height, int filter, int internalFormat, int format, int type) {
		return create(gl, width, height, filter, internalFormat, format, type, GL.GL_TEXTURE_2D);
	}

	boolean create(GL gl, int width, int height, int filter, int internalFormat, int format, int type, int texType) {
		// generate id for background texture
		int[] texId = new int[1];
		
		if(		(index != 0) &&
				(this.internalFormat == internalFormat) &&
				(this.texType == texType) &&
				(this.height == height) &&
				(this.width == width))
			return true;
		
		// if already exists than kill
		if(index != 0) {
			delete(gl, false);
		}

		
		gl.glGenTextures (1, texId, 0);
		index = texId[0];

		// create an opengl texture
		gl.glBindTexture (texType, getIndex());

		gl.glTexParameteri (texType, GL.GL_TEXTURE_MAG_FILTER,
				filter);
		gl.glTexParameteri (texType, GL.GL_TEXTURE_MIN_FILTER,
				filter);

		gl.glTexImage2D (texType,
				0 /* level of detail, needed for mip-mapping */,
				internalFormat, width, height, 0, format, type, null);
		
		this.width = width;
		this.height = height;
		this.type = type;
		this.internalFormat = internalFormat;
		this.texType = texType;
		
		return gl.glGetError() != GL.GL_NO_ERROR;		
	}

	boolean createFloat(GL gl, int width, int height, float[] pixels){
		// generate id for background texture
		int[] texId = new int[1];
		gl.glGenTextures (1, texId, 0);
		index = texId[0];

		// create an opengl texture
		FloatBuffer buf = FloatBuffer.wrap (pixels);
		gl.glBindTexture (GL.GL_TEXTURE_2D, getIndex());

		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri (GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_NEAREST);
		gl.glTexParameteri (GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_NEAREST);

		gl.glTexImage2D (GL.GL_TEXTURE_2D,
				0 /* level of detail, needed for mip-mapping */,
				GL.GL_LUMINANCE32F_ARB, width, height, 0, GL.GL_LUMINANCE, GL.GL_FLOAT, buf);
		
		type = GL.GL_FLOAT;
		internalFormat = GL.GL_LUMINANCE32F_ARB;
		texType= GL.GL_TEXTURE_2D;
		this.width = width;
		this.height = height;
		
		return gl.glGetError() != GL.GL_NO_ERROR;
	}
	
	public void bindTo(GL gl, int targetTexNo) {
		gl.glActiveTexture(targetTexNo);
		gl.glBindTexture(texType, index);
	}
	
	boolean createByte(GL gl, int k, int[] pixels){
		// generate id for background texture
		int[] texId = new int[1];
		gl.glGenTextures (1, texId, 0);
		index = texId[0];

		// create an opengl texture
		IntBuffer buf = IntBuffer.wrap (pixels);
		gl.glBindTexture (GL.GL_TEXTURE_2D, getIndex());

		gl.glTexParameteri (GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri (GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);

		gl.glTexImage2D (GL.GL_TEXTURE_2D,
				0 /* level of detail, needed for mip-mapping */,
				GL.GL_RGBA, k, k, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, buf);
		
		internalFormat = GL.GL_RGBA;
		texType= GL.GL_TEXTURE_2D;
		type = GL.GL_UNSIGNED_BYTE;
		width = k;
		height = k;
		
		return gl.glGetError() != GL.GL_NO_ERROR;
	}
	
	/**
	 * Delete this texture from OpenGL texture memory.
	 * @param gl
	 */
	public void delete (GL gl, boolean javaonly)
	{
		if(index == 0)
			return;
		int[] iv = new int[1];
		iv[0] = index;
		if(!javaonly)
			gl.glDeleteTextures (1, iv, 0);
		index = 0;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}	
	
	public void cleanup(OpenGLState glState, boolean javaOnly) {
		delete(glState.getGL(), javaOnly);
	}

	private int estimateBPP () {
		switch (internalFormat) {
		case GL.GL_RGBA:
			return 4;
		case GL.GL_LUMINANCE32F_ARB:
			return 4;
		case GL.GL_RGBA16F_ARB:
			return 8;
		case GL.GL_RGBA8:
			return 4;
		case GL.GL_DEPTH_COMPONENT: 
			return 3;
		case GL.GL_RGBA32F_ARB:
			return 16;
		case GL.GL_RGBA16UI_EXT:
			return 8;
		default:
			return 0;
		}
	}
	
	public int estimateSizeInByte()
	{
		if(index == 0)
			return 0;
		return width*height*estimateBPP()*(texType == GL.GL_TEXTURE_CUBE_MAP ? 6 : 1);
	} 

}
