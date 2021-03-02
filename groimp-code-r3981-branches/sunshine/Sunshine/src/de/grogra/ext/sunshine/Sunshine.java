package de.grogra.ext.sunshine;

import de.grogra.ext.sunshine.SunshineSceneVisitor.COLOR_MODE;
import de.grogra.ext.sunshine.output.Image;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.Path;
import java.awt.image.ImageObserver;
import javax.vecmath.*;
import de.grogra.imp3d.*;
import de.grogra.imp3d.objects.*;
import de.grogra.imp3d.shading.ImageMap;
import de.grogra.imp3d.shading.Phong;
import de.grogra.imp3d.shading.Shader;
import de.grogra.pf.ui.Workbench;
import de.grogra.ray.physics.Light;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.reflect.Type;
import de.grogra.util.EnumerationType;
import de.grogra.util.Map;
import de.grogra.vecmath.Math2;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import de.grogra.xl.util.IntList;

/**
 * the main class of the raytracer
 * creates the texture handler and the sunshine raytracer
 * @author Thomas
 *
 */
public class Sunshine extends de.grogra.imp.Renderer implements Options, Runnable
{
	public static final EnumerationType RAYPROCESSOR = new EnumerationType (
			"rayprocessor", SunshinePlugin.I18N, new String[] {"standard", "pathtracer", "pathtracerOld", "bidirectional", "spectral"},
			new Class[] {SunshineStandardRaytracer.class, SunshinePathtracer.class, SunshinePathtracerOld.class, SunshineBidirPathtracer.class, SunshineSpectralPT.class}, Type.CLASS);
	
	
	private SunshineRaytracer renderer;
	
	private Camera camera;	
	private Map params;
	private ViewConfig3D view3D;
	
	private int grid = 3;
	private GraphState gs;
	
	private float relBright = 1.0f;
	
	Image image;
	
	/**
	 * It might happens that the size of textures isn't the optimal
	 * for the GPU to work with. On ATI some strange error occur: wrong
	 * values were read during texture fetching. An offset prevent this.
	 */
	private int texSizeOffset			= 0;
	
	
	public Sunshine (Map params)
	{
		this.params = params;
				
		texSizeOffset					= ((Number) ((Options) this).get ("SpecialOptions/texSizeOffset", 0)).intValue();
	
		relBright 						= ((Number) ((Options) this).get ("rel_brightness", 1.0)).floatValue();
	} //constructor
	
	
	public void render()
	{
		gs = GraphState.current(view.getGraph());
		Thread t = new Thread(this, getName());
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}
		
		
	public void run()
	{
		
		// start the timer
		long startTime = System.currentTimeMillis ();
		
		image = new Image(width, height); 
		
		image.setScale(relBright);
		
		view3D = (View3D) view;
		
		CountVisitor cv = new CountVisitor( view3D.getWorkbench(), 
				view3D.getGraph(), view3D, gs);
		
		//traverse the graph and count the size of the objects
		view3D.getGraph ().accept (null, cv, null);
		
		
		//create a texture handler with the counted size
		ObjectHandler oh = new ObjectHandler(cv.getSize(), cv.getObjects(),
				cv.getTriangleCount(), cv.getImageCache() );
		
		
		// store the camera parameter in the object handler
		setLookAt(oh);		
		
		SunshineSceneVisitor visitor = new SunshineSceneVisitor( 
				view3D.getWorkbench(), view3D.getGraph(), view3D, oh, this, 
				view3D.getEpsilon(), cv.getObjects(), cv.getPhongCache(), gs );

		renderer = (SunshineRaytracer)getClassOption("rayprocessor", new SunshinePathtracerOld());
		
		if(renderer instanceof SunshineSpectralPT)
			visitor.setColorMode(COLOR_MODE.MODE_SPECTRAL);
		
		visitor.setProgress (Resources.msg ("initializing"),
				ProgressMonitor.INDETERMINATE_PROGRESS);
		
		//traverse the graph a second time and store the objects
		view3D.getGraph ().accept (null, visitor, null);
		
		if(cv.getSize() > 0)
		{
			oh.rewind();
			
			renderer.initialize(this, visitor, oh, width, height, cv.getSize(), 
					cv.getObjects(), cv.getTriangleCount());
			
			grid = renderer.getGrid();
			
			// start the rendering computations
			renderer.startRender();
		} //if
		
		//stop the timer
		long time = System.currentTimeMillis () - startTime;

		//get statistics from used renderer
		visitor.showMessage(renderer.getStatistics(time).toString());
		
	} //render
	

	public synchronized void dispose()
	{
		renderer.stop();
	} //dispose
		
	
	public String getName ()
	{
		return "Sunshine";
	} //getName
	
	
	public Object get (String key, Object def)
	{
		return params.get (key, def);
	} //get	
	
	/**
	 * displays the rendered image
	 * @param bb
	 */
	protected void showImage(ByteBuffer bb, int mode)
	{	
		image.setOutputMode(mode);
		image.createImage(bb);	
	
		for(ImageObserver observer : observers)
		{
			this.imageUpdate(image.getImage(), ImageObserver.ALLBITS, 0, 0, 
			image.getImage().getWidth(), image.getImage().getHeight());
		} //for
		
	} //showImage


	//set the camera parameter
	private void setLookAt(ObjectHandler objectHandler)
	{
		Vector3d right 		= new Vector3d(1, 0, 0);
		Vector3d up 		= new Vector3d(0, 1, 0);
		Vector3d direction 	= new Vector3d(0, 0, -1);
		
		camera = ((View3D) view).getCamera ();
		
		Matrix4d m = new Matrix4d();
		Math2.invertAffine(camera.getWorldToViewTransformation(), m);
				
		m.transform (right);
		m.transform(up);
		m.transform(direction);
		
		Projection p = camera.getProjection();
		right.scale(2 / p.getScaleX ());
		up.scale(2 / p.getScaleY ());
		
		Vector3d location = new Vector3d( m.m03, m.m13, m.m23);
		
		objectHandler.storeCamera(location, up, right, direction);

	} //setLookAt
	
	
	public Object getClassOption (String key, Object def)
	{
		if (params != null)
		{
			Object cls = params.get (key, null);
			if (cls instanceof Class)
			{
				try
				{
					return ((Class) cls).newInstance ();
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
		}
		return def;
	}
	
	/**
	 * this class travers the graph and count the primitives, the images 
	 * and phong shaders
	 * @author mankmil
	 *
	 */
	private class CountVisitor extends DisplayVisitor
	{
		// the sizes of the primitives in the texture
		private final int SIZEOF_SPHERE = 7;
		private final int SIZEOF_BOX 	= 7;
		private final int SIZEOF_CFC 	= 8;
		private final int SIZEOF_PLANE 	= 7;
		private final int SIZEOF_PARA 	= 8;
		private final int SIZEOF_MESH 	= 7;
		private final int SIZEOF_LIGHT 	= 9;
		private final int SIZEOF_TRI 	= 5;
		
		private int size;
		private int imageCounter;
		private int phongCounter;
		private int[] objects = new int[7]; // stores the number of the objects 
		
		final Hashtable<java.awt.Image, Integer> imgCache = new Hashtable<java.awt.Image, Integer> ();
		final Hashtable<Phong, Integer> phongCache = new Hashtable<Phong, Integer> ();
		
		private IntList trianglesCount = new IntList();
		private int vertexCount = 0;
		
		public CountVisitor(Workbench wb, Graph graph, ViewConfig3D view, GraphState gs)
		{
			Matrix4d m = new Matrix4d();
			m.setIdentity();

			init(gs, m, view, view != null);
			
			size 			= 0;
			imageCounter 	= 0;
			phongCounter 	= 0;
		} //Constructor
		
		
		protected void visitImpl(Object object, boolean asNode, Shader s, 
				Path path)
		{
			Object shape = state.getObjectDefault (object, asNode,
					de.grogra.imp3d.objects.Attributes.SHAPE, null);
			
			if (shape != null)
			{
				// count objects like sphere, box, cone
				if (shape instanceof Renderable)
				{
					if(shape instanceof Sphere)
					{
						size += SIZEOF_SPHERE;
						objects[0]++;
					} //sphere
					
					if(shape instanceof Box)
					{
						size += SIZEOF_BOX;
						objects[1]++;
					} //box
					
					if (shape instanceof Cylinder)
					{ 
						size += SIZEOF_CFC;
						objects[2]++;
					} //Cylinder
					
					if(shape instanceof Cone)
					{
						size += SIZEOF_CFC;
						objects[2]++;
					}
					
					if(shape instanceof Frustum)
					{
						size += SIZEOF_CFC;
						objects[2]++;
					}
					
					if(shape instanceof Plane)
					{
						size += SIZEOF_PLANE;
						objects[3]++;
					}
					
					if(shape instanceof MeshNode)
					{
						PolygonMesh p = (PolygonMesh) ((MeshNode) shape).getPolygons();
						
						int triCount 	= p.getIndexData().length / 3; // 3 indices per tri
						
						if(triCount > 0)
						{
							size+= SIZEOF_MESH + triCount*SIZEOF_TRI;
							objects[4]++;
							
							trianglesCount.add(triCount);
						}
					}
					
					if(shape instanceof Parallelogram)
					{
						size += SIZEOF_PARA;
						objects[5]++;
						
						//if parallelogram is also a lightsource
						if( ((Parallelogram)shape).getLight() != null )
						{
							size += SIZEOF_LIGHT;
							objects[6]++;
						}
						
					}
				} //if
				
				Shader shader = getCurrentShader();
				if(shader instanceof Phong)
					countShader(shader);
			}
			
			
			Light light = (Light) state.getObjectDefault(object, asNode, 
					Attributes.LIGHT, null);
				 
			if(light != null && light instanceof PointLight )
			{
				size += SIZEOF_LIGHT;
				objects[6]++;
			}
			
		} //visitImpl
		
		
		public void createStandardLight()
		{
			if(objects[6] == 0)
			{
				size += SIZEOF_LIGHT;
				objects[6]++;			
			} //if
		} //if
		
		public int[] getTriangleCount()
		{
			return trianglesCount.toArray();
		}
		
		// returns the size of the scene texture (nxn)
		public int getSize()
		{
			return (int)Math.ceil( Math.sqrt(size) ) + texSizeOffset;
		} //getSize
		

		public int[] getObjects()
		{
			return objects;
		} //getObjects

		
		// get the image-shader of the object
		private void countShader(Shader shader)
		{
			Phong p = (Phong)shader;
			
			// collect the phong shader
			storePhong(p);
			
			if(p.getEmissive() instanceof ImageMap)
				storeImage( (ImageMap)p.getEmissive() );
				
			if(p.getAmbient() instanceof ImageMap)
				storeImage( (ImageMap)p.getAmbient() );
			
			if(p.getDiffuse() instanceof ImageMap)
				storeImage( (ImageMap)p.getDiffuse() );
			
			if(p.getSpecular() instanceof ImageMap)
				storeImage( (ImageMap)p.getSpecular() );
			
			if(p.getTransparency() instanceof ImageMap)
				storeImage( (ImageMap)p.getTransparency() );
			
			if(p.getDiffuseTransparency() instanceof ImageMap)
				storeImage( (ImageMap)p.getDiffuseTransparency() );
						
		} //countImages
		
		
		//stores a found image-shader into a hashtable
		private void storeImage(ImageMap im)
		{
			java.awt.Image image = im.getImageAdapter().getNativeImage();
			
			// try to look up the Texture object
			Integer result = imgCache.get(image);

			// if non was found, then create one
			if (result == null)
			{
				imgCache.put(image, imageCounter);
				imageCounter++;
			}
		}
		
		// stores a found phong-shader into a hashtable
		private void storePhong(Phong p)
		{
			// try to look up the phong shader
			Integer result = phongCache.get(p);

			// if non was found, then create one
			if(result == null)
			{
				phongCache.put(p, phongCounter);
				phongCounter++;
			}
		}
		
		// returns the hashtable with the stored images
		public Hashtable<java.awt.Image, Integer> getImageCache()
		{
			return imgCache;
		}
		
		
		// returns the hashtable with the stored phong-shaders
		public Hashtable<Phong, Integer> getPhongCache()
		{	
			return phongCache;
		}
		
	} //class
			
} //class sunshine
