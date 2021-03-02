package de.grogra.ext.sunshine;

import de.grogra.ext.sunshine.Image;
import de.grogra.ext.sunshine.objects.SunshineBox;
import de.grogra.ext.sunshine.objects.SunshineObject;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.Path;
import de.grogra.graph.impl.Node;
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
import de.grogra.util.Map;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.Octree;
import java.nio.ByteBuffer;
import java.util.Hashtable;

/**
 * the main class of the raytracer
 * creates the texture handler and the sunshine raytracer
 * @author Thomas
 *
 */
public class Sunshine extends de.grogra.imp.Renderer implements Options
{
	// to get the parameters for the raytracer
	private static final String GRID_SIZE 	= "grid";
	private static final String REC_DEEP 	= "rec_deep";
	private static final String PATHTRACER 	= "pathTracer";
	private static final String OCTREE 		= "accelerator";
	
	
	private Camera camera;	
	private Map params;
	private ViewConfig3D view3D;
	
	private int grid = 3;
		
	
	public Sunshine (Map params)
	{
		this.params = params;
	} //constructor
	

	
		
	public void render ()
	{
		// start the timer
		long startTime = System.currentTimeMillis ();
		
		
		
		view3D = (View3D) view;
		

		CountVisitor cv = new CountVisitor( view3D.getWorkbench(), 
				view3D.getGraph(), view3D);
		
		//traverse the graph and count the size of the objects
		view3D.getGraph ().accept (null, cv, null);
		
		
		//create a texture handler with the counted size
		ObjectHandler oh = new ObjectHandler(width, height, cv.getSize(), 
				cv.getObjects(), cv.getImageCache() );
		
		
		// store the camera parameter in the texture handler
		setLookAt(oh);

		
		SunshineSceneVisitor visitor = new SunshineSceneVisitor( 
				view3D.getWorkbench(), view3D.getGraph(), view3D, oh, this, 
				view3D.getEpsilon(), cv.getObjects(), cv.getPhongCache() );


		visitor.setProgress (Resources.msg ("initializing"),
				ProgressMonitor.INDETERMINATE_PROGRESS);
		
		//traverse the graph a second time and store the objects
		view3D.getGraph ().accept (null, visitor, null);
		
		
		boolean debug = !true;
		

		if( cv.getSize() > 0 )
		{
			oh.rewind();
			
			grid 		= getNumericOption(GRID_SIZE, 2).intValue();
			int recDeep		= getNumericOption(REC_DEEP, 1).intValue();
			
			boolean pt 		= getBooleanOption(PATHTRACER, false);
			boolean ot	=	getBooleanOption(OCTREE, true);
			
			
			//create the raytracer
			SunshineRaytracer sunny = new SunshineRaytracer(oh, grid, recDeep, 
					width, height, cv.getSize(), cv.getObjects(), visitor, pt, ot);
			
			// start the rendering computations
			if(!debug)sunny.startRender();
		}
		
		//stop the timer
		long time = System.currentTimeMillis () - startTime;
		
		//display the result in the viewer window
		showImage(oh.getResult());
		
		
		//show stastistics
		StringBuffer stats = new StringBuffer ("<html><pre>");
		stats.append (Resources.msg ("raytracer.statistics",
			new Object[] {width, height, 1,
					(int) (time / 60000),
					(time % 60000) * 0.001f}));
		
		int objects = 0;
		for(int i = 0; i < cv.getObjects().length-1; i++)
		{
			objects += cv.getObjects()[i];
		}
		
		stats.append(Resources.msg("rayprocessor.default.statistics", new Object[]{width*height*grid*grid}) ); 
		
		stats.append(Resources.msg("raytracer.scene.statistics", new Object[]{objects, cv.getObjects()[5]}) );
		
		stats.append(Resources.msg("antialiasing.stochastic.statistics", new Object[]{grid, grid}) );
		
		visitor.showMessage (stats.append ("</pre></html>").toString ());
		
	} //render
	

	public synchronized void dispose ()
	{

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
	private void showImage(ByteBuffer bb)
	{
		Image image = new Image(width, height);
	
		image.createImage(bb);
	
	
		for(ImageObserver observer : observers)
		{
			observer.imageUpdate(image.getImage(), ImageObserver.ALLBITS, 0, 0, 
			image.getImage().getWidth(), image.getImage().getHeight());
		} //for
		
	} //showImage


	//set the camera parameter
	private void setLookAt(ObjectHandler th)
	{
		Vector3d right 		= new Vector3d(1, 0, 0);
		Vector3d up 			= new Vector3d(0, 1, 0);
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
		
		th.storeCamera(location, up, right, direction);

	} //setLookAt
	
	
	private Number getNumericOption(String key, Number def)
	{
		return (this != null) ? (Number) this.get (key, def) : def;
	} //getNumericOption
	
	
	private boolean getBooleanOption (String key, boolean def)
	{
		return (this != null) ? ((Boolean) this.get (key, Boolean.valueOf (def))).booleanValue () : def;
	} //getBooleanOption
	
	
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
		private final int SIZEOF_LIGHT 	= 9;
		
		private int size;
		private int imageCounter;
		private int phongCounter;
		private int[] objects = new int[6]; // stores the number of the objects 
		final Hashtable<java.awt.Image, Integer> imgCache = new Hashtable<java.awt.Image, Integer> ();
		final Hashtable<Phong, Integer> phongCache = new Hashtable<Phong, Integer> ();
		
		
		public CountVisitor(Workbench wb, Graph graph, ViewConfig3D view)
		{
			Matrix4d m = new Matrix4d();
			m.setIdentity();

			init(GraphState.current(graph), m, view, view != null);
			
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
					
					if(shape instanceof Parallelogram)
					{
						size += SIZEOF_PARA;
						objects[4]++;
						
						//if parallelogram is also a lightsource
						if( ((Parallelogram)shape).getLight() != null )
						{
							size += SIZEOF_LIGHT;
							objects[5]++;
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
				objects[5]++;
			}
			
		} //visitImpl
		
		
		public void createStandardLight()
		{
			if(objects[5] == 0)
			{
				size += SIZEOF_LIGHT;
				objects[5]++;			
			} //if
		} //if
		
		
		// returns the size of the scene texture (nxn)
		public int getSize()
		{
			return (int)Math.ceil( Math.sqrt(size) );
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
