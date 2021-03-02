
package de.grogra.imp3d.glsl;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.media.opengl.GL;

import de.grogra.vecmath.Math2;

public class TextureManager
{

	static final int MAX_STAMP_DIFF = 100;

	final Hashtable texHash = new Hashtable ();
	//	final LinkedList textures = new LinkedList ();

	int stamp;

	public void deleteTexture(GL gl, Image image){
		Texture tex = (Texture)texHash.get(image);
		if (tex != null)
		{
			tex.delete(gl);
		}
		texHash.remove(image);
	}
	
	/**
	 * Delete all textures.
	 * @param gl
	 */
	public void deleteTextures(GL gl){
		Enumeration textures = texHash.elements();
		while(textures.hasMoreElements()){
			Texture tex = (Texture)textures.nextElement();
			tex.delete(gl);
		}
		texHash.clear();
	}
	
	/**
	 * Remove textures that were not recently used.
	 * 
	 * @param gl
	 */
	private void cleanupTextures (GL gl, int stampDiff)
	{
		//		Texture[] textures =(Texture[])texHash.entrySet().toArray(new Texture[texHash.size()]);
		Object[] keys = texHash.keySet ().toArray ();

		// for each texture
		for (int i = 0; i < keys.length; i++)
		{
			// get the key
			Object key = keys[i];

			// get texture
			Texture tex = (Texture) texHash.get (key);

			// check stamp 
			if ((stamp - tex.stamp) > stampDiff)
			{
				// texture was too old, so remove it
				tex.delete (gl);
				texHash.remove (key);
			}
		}
	}

	/**
	 * Convert the image to an opengl texture.
	 * 
	 * @param img
	 * @return
	 */
	private Texture createTexture (GL gl, Image img)
	{
		Texture result = null;

		// perform conversion of image data to texture
		int width = img.getWidth (null); // width of image data
		int height = img.getHeight (null); // height of image data

		// calculate next power of two for texture size,
		// otherwise opengl will complain
		int k = Math2.roundUpNextPowerOfTwo (Math.max (width, height));
		float w = (float) width / (float) k;
		float h = (float) height / (float) k;

		// check if k is not bigger than the maximum texture size 
		int[] iv = new int[10];
		gl.glGetIntegerv (GL.GL_MAX_TEXTURE_SIZE, iv, 0);
		if (k > iv[0])
		{
			k = iv[0];
		}

		// resize the image to be of size k times k
		img = img.getScaledInstance (k, k, Image.SCALE_SMOOTH);

		// grab the pixel data
		boolean grabbed = false;
		int[] pixels = new int[k * k];
		//		PixelGrabber pg = new PixelGrabber (img, 0, 0, width, height, pixels,
		//				0, k);
		PixelGrabber pg = new PixelGrabber (img, 0, 0, k, k, pixels, 0, k);
		try
		{
			pg.grabPixels ();
			if ((pg.getStatus () & ImageObserver.ABORT) == 0)
			{
				// grabbing successful
				grabbed = true;
			}
		}
		catch (InterruptedException e)
		{
			// grabbing was interrupted, act as if grabbing failed
		}

		// handle grabbed image
		if (grabbed)
		{
			// generate Texture
			result = new Texture ();

			// store width and height of texture in u/v-coordinates
			//			result.w = w;
			//			result.h = h;

			int stampDiff = MAX_STAMP_DIFF;

			// create an opengl texture
			// XXX: Gets called 2x for every texture... why?
			do
			{
				// remove unused textures from OpenGL texture memory
				cleanupTextures (gl, stampDiff);

				// if creation failed, clean more textures
				stampDiff /= 2;
			}
			while (result.create (gl, k, pixels));

			// put texture into list
			//			textures.add (result);
		}

		return result;
	}

	public Texture getTexture (GL gl, Image img)
	{
		// try to look up the Texture object
		Texture result = (Texture) texHash.get (img);

		// if non was found, then create one
		if (result == null)
		{
			result = createTexture (gl, img);
			texHash.put (img, result);
		}

		// LRU for access time
		result.stamp = this.stamp++;

		return result;
	}
	
	public int estimateSizeInByte() {
		int memory = 0;
		Iterator<Texture> it = texHash.values().iterator();
		while(it.hasNext())
			memory += it.next().estimateSizeInByte();
		return memory;
	}

}
