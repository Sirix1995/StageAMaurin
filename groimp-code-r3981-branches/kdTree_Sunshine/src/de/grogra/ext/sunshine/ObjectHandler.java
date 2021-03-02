package de.grogra.ext.sunshine;

import static javax.media.opengl.GL.GL_CLAMP;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_RGBA32F_ARB;
import static javax.media.opengl.GL.GL_NEAREST;
import static javax.media.opengl.GL.GL_RGBA;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_RECTANGLE_ARB;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Hashtable;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.*;
import com.sun.opengl.util.BufferUtil;

import de.grogra.ext.sunshine.objects.*;


/**
 * @author Thomas
 *
 */
public class ObjectHandler 
{
	// 4 values for rgba * 4 byte: size of a float variable	
	private final int RGBA = 4 * BufferUtil.SIZEOF_FLOAT;
	private final int SIZEOF_SPHERE 	= 112; // 7
	private final int SIZEOF_BOX 		= 112;
	private final int SIZEOF_CFC 		= 128; // 8
	private final int SIZEOF_PLANE 		= 112; //7*16
	private final int SIZEOF_PARA 		= 128;
	private final int SIZEOF_LIGHT 		= 144; // 9
	private int boxPart		= 0;
	private int cfcPart 	= 0;
	private int planePart	= 0;
	private int paraPart	= 0;
	private int lightPart	= 0;
	

	private ByteBuffer sceneTex;
	private ByteBuffer result;
	
	private int texWidth;
	private int texHeight;
	private int sphereCount 	= 0;
	private int boxCount 		= 0;
	private int cfcCount 		= 0;
	private int planeCount 		= 0;
	private int paraCount 		= 0;
	private int lightCount		= 0;

		
	private float camOrigin[] 	= new float[3];
	private float up[] 			= new float[3];
	private float right[] 		= new float[3];
	private float direction[] 	= new float[3];
	
	
	private Image[] images;
	
	
	private int texTarget = GL_TEXTURE_RECTANGLE_ARB;
	private int texInternalFormat = GL_RGBA32F_ARB;
	final Hashtable<Image, Integer> imgCache;
	
	
	public ObjectHandler(int width, int height, int sceneSize, int[] objects, Hashtable<Image, Integer> imagCache)
	{
		texWidth 	= width;
		texHeight 	= height;
		
		boxPart 	= objects[0]*SIZEOF_SPHERE;
		cfcPart		= boxPart + objects[1]*SIZEOF_BOX;
		planePart	= cfcPart + objects[2]*SIZEOF_CFC;
		paraPart	= planePart + objects[3]*SIZEOF_PLANE;
		lightPart	= paraPart + objects[4]*SIZEOF_PARA;
		
		int tmp 	= texHeight * texWidth * RGBA;
		result 		= BufferUtil.newByteBuffer( tmp );
		
		sceneTex 	= BufferUtil.newByteBuffer( sceneSize*sceneSize*RGBA );
		
		images 		= new Image[imagCache.size() < 64 ? imagCache.size() : 64];
		imgCache 	= imagCache;
	} //constructor

	
	/**
	 * assign the primitives to explict texture positions
	 * @param object
	 */
	public void storePrimitive(SunshineObject sObject)
	{		
		switch(sObject.getID())
		{
			case 0: storeSphere(sObject); break;
		
			case 1: storeBox(sObject); break;
			
			case 2: storeCFC(sObject); break;
			
			case 3: storePlane(sObject); break;
			
			case 4: storePara(sObject); break;
			
			case 10: storeLight(sObject); break;
									
		} //switch
		
	} //setScene	
	
	
	public String getCamPosString()
	{
		return camOrigin[0]+","+camOrigin[1]+","+camOrigin[2];
	}
	
	public void storeCamera(Vector3d origin, Vector3d up, Vector3d right, Vector3d dir)
	{
		camOrigin[0] = (float)origin.x;
		camOrigin[1] = (float)origin.y;
		camOrigin[2] = (float)origin.z;
		
		this.up[0] = (float)up.x;	
		this.up[1] = (float)up.y;
		this.up[2] = (float)up.z;
		
		this.right[0] = (float)right.x;
		this.right[1] = (float)right.y;
		this.right[2] = (float)right.z;
		
		this.direction[0] = (float)dir.x;
		this.direction[1] = (float)dir.y;
		this.direction[2] = (float)dir.z;		
	}
	
	
	public String getUpString()
	{
		return up[0]+","+up[1]+","+up[2];
	}

	
	public String getRightString()
	{
		return right[0]+","+right[1]+","+right[2];
	}

	
	public String getDirString()
	{
		return direction[0]+","+direction[1]+","+direction[2];
	}


	public void rewind()
	{
		sceneTex.rewind();	
	} //rewind
	
	
	public int getLightPart()
	{
		return lightPart / RGBA;
	}


	public ByteBuffer getResult() 
	{		
		return result;
	}
	

	public ByteBuffer getSceneTex() 
	{
		return sceneTex;
	}
	
	
	private void storeColor(Color4f rgba)
	{
		sceneTex.putFloat( rgba.x );
		sceneTex.putFloat( rgba.y );
		sceneTex.putFloat( rgba.z );
		sceneTex.putFloat( rgba.w );
	} //storeColor
	
	
	private void storeTransformMatrix(Matrix4f m)
	{
		for(int i = 0; i < 2; i++)
		{
			sceneTex.putFloat( m.m00 );
			sceneTex.putFloat( m.m10 );
			sceneTex.putFloat( m.m20 );
			
			sceneTex.putFloat( m.m01 );
			sceneTex.putFloat( m.m11 );
			sceneTex.putFloat( m.m21 );
			
			sceneTex.putFloat( m.m02 );
			sceneTex.putFloat( m.m12 );
			sceneTex.putFloat( m.m22 );
			
			sceneTex.putFloat( m.m03 );
			sceneTex.putFloat( m.m13 );
			sceneTex.putFloat( m.m23 );
			
			m.invert();
		}

	} //storeTransformMatrix


	private void storeSphere(SunshineObject sob)
	{	
		sceneTex.position(SIZEOF_SPHERE * sphereCount);
		
		storeTransformMatrix( sob.getTransformMatrix() );
		storeColor( sob.getColor() );

		sphereCount++;
	} //setSphere


	private void storeBox(SunshineObject sob)
	{
		sceneTex.position(boxPart + SIZEOF_BOX * boxCount);
		
		storeTransformMatrix(sob.getTransformMatrix());
		storeColor(sob.getColor());
		
		boxCount++;
	} //storeBox
	
	
	private void storePlane(SunshineObject sob)
	{
		sceneTex.position(planePart + SIZEOF_PLANE * planeCount);
		
		storeTransformMatrix( sob.getTransformMatrix() );
		storeColor( sob.getColor() );
		
		planeCount++;
	} //storeBox
	
	
	private void storePara(SunshineObject sob)
	{
		sceneTex.position(paraPart + SIZEOF_PARA * paraCount);
		storeTransformMatrix( sob.getTransformMatrix() );
		storeColor( sob.getColor() );
		
		sceneTex.putFloat( ((SunshineParalellogram)sob).isLight() ? 1 : 0 );

		
		paraCount++;
	}
	
	private void storeCFC(SunshineObject sob)
	{
		sceneTex.position(cfcPart + SIZEOF_CFC * cfcCount);
		
		storeTransformMatrix( sob.getTransformMatrix() );
		storeColor( sob.getColor() );
		
		sceneTex.putFloat( ((SunshineCFC)sob).isTopOpen() ? 1 : 0 );
		sceneTex.putFloat( ((SunshineCFC)sob).isBaseOpen() ? 1 : 0 );
		sceneTex.putFloat( ((SunshineCFC)sob).getMax() );
		sceneTex.putFloat( ((SunshineCFC)sob).getType() );
		
		cfcCount++;
	} //storeCylinder
	
	
	private void storeLight(SunshineObject sob)
	{
		sceneTex.position(lightPart + SIZEOF_LIGHT*lightCount);
		
		storeTransformMatrix( sob.getTransformMatrix() );
		storeColor( sob.getColor() );
		
		//p0
		sceneTex.putFloat( ((SunshineLight)sob).getPower() );
		sceneTex.putFloat( ((SunshineLight)sob).getAtt() );
		sceneTex.putFloat( ((SunshineLight)sob).getExp() );
		sceneTex.putFloat( ((SunshineLight)sob).isShadowless() ? 1 : 0 );
		
		//p1
		sceneTex.putFloat( ((SunshineLight)sob).getTyp() );
		sceneTex.putFloat( ((SunshineLight)sob).getInnerAngle() );
		sceneTex.putFloat( ((SunshineLight)sob).getOuterAngle() );
		sceneTex.putFloat( ((SunshineLight)sob).getCount() );
		
		lightCount++;
	} //storeLight
	

	public Vector2f storeImage(Image image)
	{

		Integer result = imgCache.get(image); 

		if(result != null || result < 64)
		{
			images[result] = image;
		} else
		{
//			System.err.println("Nicht in Cache gefunden, kann gar nicht sein!");
		}
		
		
		Vector2f uv = new Vector2f();
		int size = images.length;
		int x = result;
		int y = 0;
		

		if(size > 8 && size < 64)
		{
			y = size % 8 == 0 ? size / 8 : size / 8 + 1;
			x = (int)Math.ceil( (float)size / y );
			
			y = result / x;
			x = result % x;
		}
		else if(size == 64)
		{
			y = 7;
			x = 7;
		}

		
		uv.x = x; 
		uv.y = y; System.out.println(x+", " +y);
		
		return uv;
	}


	public boolean hasImages()
	{
		return images.length > 0;
	}
	
	
	public int getImageCount()
	{
		return images.length;
	}


	/**
	 * Converts the image to an intbuffer.
	 * 
	 * @return the intbuffer
	 */
	public IntBuffer getPixels(int pos)
	{
		Image img = images[pos];
	
		int k = 512;
		// resize the image to be of size k times k
		img = img.getScaledInstance (k, k, Image.SCALE_SMOOTH);
		boolean grabbed = false;
		// grab the pixel data
		int[] pixels = new int[k * k];

		PixelGrabber pg = new PixelGrabber(img, 0, 0, k, k, pixels, 0, k);
	
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
			System.err.println("Fehler beim pixel grabben");
		}
		
		return IntBuffer.wrap (pixels);
	}
	
	
	
	/**
	 * 
	 * @param drawable
	 * @param texWidth
	 * @param texHeight
	 * @param count
	 * @param target
	 */
	public void generateTexture(GLAutoDrawable drawable, int texWidth, int texHeight, int count, int[] target)
	{
		GL gl = drawable.getGL();
		gl.glGenTextures(count, target, 0);

		for (int i = 0; i < count; i++)
		{
			gl.glBindTexture(texTarget, target[i]);
			gl.glTexImage2D
			(
				texTarget, 0, texInternalFormat, 
				texWidth, texHeight, 0, GL_RGBA, GL_FLOAT, null
			);
			gl.glTexParameterf(texTarget, GL_TEXTURE_WRAP_S, GL_CLAMP);
			gl.glTexParameterf(texTarget, GL_TEXTURE_WRAP_T, GL_CLAMP);
			gl.glTexParameterf(texTarget, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			gl.glTexParameterf(texTarget, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		} // for
	} //generateTexture


	public int getSphereID() 
	{
		return sphereCount * 7;
	}


	public int getBoxID() 
	{
		return boxCount * 7;
	}


	public int getPlaneID() 
	{
		return planeCount * 7;
	}


	public int getParaID() 
	{
		return paraCount * 8;
	}
	
	public int getCfcID() 
	{
		return cfcCount * 8;
	}
	
	
	
} //class
