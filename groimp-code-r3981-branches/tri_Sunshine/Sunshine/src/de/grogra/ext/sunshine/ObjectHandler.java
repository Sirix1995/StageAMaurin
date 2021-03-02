package de.grogra.ext.sunshine;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Hashtable;
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
	public static final int RGBA 		= 4 * BufferUtil.SIZEOF_FLOAT;
	public static final int RGB 		= 3 * BufferUtil.SIZEOF_FLOAT;
	private final int SIZEOF_SPHERE 	= 112;
	private final int SIZEOF_BOX 		= 112;
	private final int SIZEOF_CFC 		= 128; // 8
	private final int SIZEOF_PLANE 		= 112; //7*16
	private final int SIZEOF_PARA 		= 128;
	private final int SIZEOF_TRI		= 80; // 5 * 16 //24 * 16
	private final int SIZEOF_MESH		= 112; // (6 (Matrices) + 1 (Color)) * 16
	private final int SIZEOF_LIGHT 		= 144; // 9
	
	private final int SPHERE 	= 0;
	private final int BOX 		= 1;
	private final int CFC 		= 2;
	private final int PLANE		= 3;
	private final int PARA 		= 4;
	private final int TRI		= 5;
	private final int LIGHT		= 10;
	
	
	private int boxPart		= 0;
	private int cfcPart 	= 0;
	private int planePart	= 0;
	private int paraPart	= 0;
	private int meshPart	= 0;
	private int lightPart	= 0;
	
	private int triPart		= 0;

	private ByteBuffer sceneTex;
	
	private int sphereCount 	= 0;
	private int boxCount 		= 0;
	private int cfcCount 		= 0;
	private int planeCount 		= 0;
	private int meshCount		= 0;
	private int paraCount 		= 0;
	private int lightCount		= 0;
	
	private int triCount		= 0;
		
	private float camOrigin[] 	= new float[3];
	private float up[] 			= new float[3];
	private float right[] 		= new float[3];
	private float direction[] 	= new float[3];
	
	
	private Image[] images;
	
	
	final Hashtable<Image, Integer> imgCache;
	
	
	public ObjectHandler(int sceneSize, int[] objects, int[] trianglesCount, Hashtable<Image, Integer> imagCache)
	{
		for(int i = 0; i < trianglesCount.length; i++)
			triPart += trianglesCount[i];
		
		triPart 	*= SIZEOF_TRI;
		
		boxPart 	= 				objects[0]*SIZEOF_SPHERE;
		cfcPart		= boxPart 	+ 	objects[1]*SIZEOF_BOX;
		planePart	= cfcPart 	+ 	objects[2]*SIZEOF_CFC;
		meshPart	= planePart + 	objects[3]*SIZEOF_PLANE;
		paraPart	= meshPart 	+ 	objects[4]*SIZEOF_MESH + triPart;
		lightPart	= paraPart 	+ 	objects[5]*SIZEOF_PARA;
		
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
			case SPHERE: 	storeSphere(sObject); 	break;
		
			case BOX: 		storeBox(sObject); 		break;
			
			case CFC: 		storeCFC(sObject); 		break;
			
			case PLANE: 	storePlane(sObject); 	break;
			
			case PARA: 		storePara(sObject); 	break;
			
			case TRI: 		storeTri(sObject); 		break;
			
			case LIGHT: 	storeLight(sObject); 	break;
			
			default:
				// error ?
				System.err.println("called storePrimitive with unknown ID");
		} //switch
		
	} //storePrimitive	
	
	
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
	
	public int getMeshPart()
	{
		return meshPart / RGBA;
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
		
		// 1 Pixel for Color
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
		
		// 6 Pixel for the two matrices

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
	
	private void storeTri(SunshineObject sob)
	{
		sceneTex.position(meshPart + SIZEOF_MESH * meshCount + triCount*SIZEOF_TRI);
		
		triCount = 0;
		
		storeTransformMatrix( sob.getTransformMatrix() );
		storeColor( sob.getColor() );
		
		//TODO 1	Storing the position values of each vertex in a separate texture
		//TODO 2	Finally store the indices of these vertices in sceneTex
		
		float[] vertexList 	= ((SunshineTriangles) sob).getVertexList();
		float[] normalList	= ((SunshineTriangles) sob).getNormalList();
		float[] textureList	= ((SunshineTriangles) sob).getTextureCoorList();
		int[] indexList		= ((SunshineTriangles) sob).getIndexList();
		
		int offset;
		
		// Since a tri has 3 vertices and each of these vertex has 3 value as 
		// coordinates we have to put 9 values to the vertexTex
		int listPosition;
		float nx, ny;
		
		// 
		for(int i = 0; i < indexList.length; i++  )
		{
			listPosition = indexList[i] * 3;
			
			//nx		= normalList[listPosition + 0];
			//ny		= normalList[listPosition + 1];			
			
			// Position of a vertex
			sceneTex.putFloat( vertexList[listPosition + 0] );	// x value of a vertex
			sceneTex.putFloat( vertexList[listPosition + 1] );	// y value of a vertex
			sceneTex.putFloat( vertexList[listPosition + 2] );	// z value of a vertex			
			
			// Normals of a vertex
			sceneTex.putFloat( normalList[listPosition + 0] );	// x value of a normal
			sceneTex.putFloat( normalList[listPosition + 1] );	// y value of a normal
			sceneTex.putFloat( normalList[listPosition + 2] );	// z value of a normal
			
			// Check whether that the third vertex has been added...
			if((i + 1) % 3 == 0)	
			{
				// .. Then fill the last two free position for that triangle.
				sceneTex.putFloat( 0.0f );
				sceneTex.putFloat( 0.0f );
			}
			
			// Normals of a vertex
			//sceneTex.putFloat( nx );	// x value
			//sceneTex.putFloat( ny );	// y value
			
		}
		
		
		
		triCount += indexList.length / 3;
		meshCount++;
	}
	
	
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
	
	public int getTriangleID()
	{
		return triCount * 4;
	}
	
	
	
} //class
