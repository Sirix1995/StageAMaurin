
package de.grogra.imp3d.glsl;

import java.nio.IntBuffer;

import javax.media.opengl.GL;

/**
 * In GroIMP3D images are converted to OpenGL textures on demand. The result
 * of such a conversion is an instance of this class, which provides all
 * necessary information about the OpenGL texture.
 *  
 * @author nmi
 *
 */
public class Texture
{

	/**
	 * OpenGL index number for texture.
	 * If index is 0, then there is no texture stored by opengl internally.
	 */
	public int index = 0;

	float w;
	float h;
	
	int k;

	/**
	 * To implement LRU each Texture has an associated stamp value.
	 */
	int stamp;

	
	/**
	 * Create a texture in OpenGL texture memory. The pixel data of
	 * the texture is specified in the pixels parameter. The create()
	 * function copies this data to OpenGLs texture memory. When the
	 * create() function returns, the array containing the pixel data
	 * may be released.
	 * 
	 * @param gl
	 * @param pixels
	 */
	boolean create(GL gl, int k, int[] pixels){
		// generate id for background texture
		int[] texId = new int[1];
		gl.glGenTextures (1, texId, 0);
		index = texId[0];

		this.k = k;
		
		// create an opengl texture
		IntBuffer buf = IntBuffer.wrap (pixels);
		gl.glBindTexture (GL.GL_TEXTURE_2D, index);

		gl.glTexParameteri (GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri (GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);

		gl.glTexImage2D (GL.GL_TEXTURE_2D,
				0 /* level of detail, needed for mip-mapping */,
				GL.GL_RGBA, k, k, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, buf);
		
		return gl.glGetError() != GL.GL_NO_ERROR;
	}
	
	/**
	 * Delete this texture from OpenGL texture memory.
	 * @param gl
	 */
	void delete (GL gl)
	{
		int[] iv = new int[1];
		iv[0] = index;
		gl.glDeleteTextures (1, iv, 0);
		// Clear GLError
		gl.glGetError();
		index = 0;
	}
	
	public int estimateSizeInByte () {
		if(index == 0)
			return 0;
		return k*k*4;
	} 
}
