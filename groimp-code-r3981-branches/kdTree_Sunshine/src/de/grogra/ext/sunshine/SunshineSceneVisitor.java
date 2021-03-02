package de.grogra.ext.sunshine;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector2f;
import de.grogra.ext.sunshine.objects.*;
import de.grogra.ext.sunshine.shader.*;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.Path;
import de.grogra.imp3d.DisplayVisitor;
import de.grogra.imp3d.Polygonization;
import de.grogra.imp3d.PolygonizationCache;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.ViewConfig3D;
import de.grogra.imp3d.VolumeBuilder;
import de.grogra.imp3d.objects.*;
import de.grogra.imp3d.shading.AffineUVTransformation;
import de.grogra.imp3d.shading.ChannelMapNode;
import de.grogra.imp3d.shading.Checker;
import de.grogra.math.ChannelMap;
import de.grogra.math.ColorMap;
import de.grogra.math.Graytone;
import de.grogra.math.RGBColor;
import de.grogra.imp3d.shading.ImageMap;
import de.grogra.imp3d.shading.Interior;
import de.grogra.imp3d.shading.Phong;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.imp3d.shading.Shader;
import de.grogra.imp3d.shading.UVTransformation;
import de.grogra.pf.ui.Workbench;
import de.grogra.ray.physics.Light;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.DefaultCellIterator;
import de.grogra.vecmath.geom.OctreeUnion;
import de.grogra.vecmath.geom.Volume;
import de.grogra.xl.util.ObjectList;

/**
 * this class travers the scene graph a second time
 * it handles the objects, the glsl phong shader and the textures
 * @author Mankmil
 *
 */
public class SunshineSceneVisitor extends DisplayVisitor implements ProgressMonitor
{
	// width and height of the images for the texture atlas 
	static final int IMAGE_WIDTH 	= 512;
	static final int IMAGE_HEIGHT 	= 512;

	
	private Workbench workbench;
	private SunshineObject sObject;
	private ObjectHandler oh;
	private Shader shader;
	private int count = 0;
	
	 int volumeID = 0;
	

	private VolumeBuilder builder;
	
	// hastable for the shader generating classes
	Hashtable<String, Class> sunshineShaders = new Hashtable<String, Class>();
	final Hashtable<Integer, SunshinePhong> phongCache = new Hashtable<Integer, SunshinePhong>();
	final Hashtable<Phong, Integer> pc;
	
	private boolean infinite;
	private OctreeUnion sceneVolume;
	private ArrayList<Volume> volumes;
	private ArrayList<Volume> infiniteVolumes;
	private ObjectList<Shader> shaders = new ObjectList<Shader> ();
	private ObjectList<Matrix4d> transforms = new ObjectList<Matrix4d> ();
	PhongHandler ph;


	public SunshineSceneVisitor(Workbench wb, Graph graph, ViewConfig3D view,
			ObjectHandler oh, Options opts, float epsilon, int[] objects,
			Hashtable<Phong, Integer> phongCache)
	{
		Matrix4d m = new Matrix4d();
		m.setIdentity();

		init(GraphState.current(graph), m, view, view != null);

		this.oh = oh;
		workbench = wb;
		workbench.beginStatus (this);
		
		pc = phongCache;
		ph = new PhongHandler();
		
		
		// store the shader classes in the hashtable
		sunshineShaders.put("checker", SunshineChecker.class);
		sunshineShaders.put("image", SunshineImage.class);
		sunshineShaders.put("uv", SunshineUVTravo.class);
		

		// the volume builder is needed for the octree implementation
		builder = new VolumeBuilder (
				new PolygonizationCache (state, Polygonization.COMPUTE_NORMALS
					| Polygonization.COMPUTE_UV, ((Number) opts.get ("flatness",
					new Float (1))).floatValue (), true), epsilon)
		{
			@Override
			protected void addVolume (Volume v, Matrix4d t, Shader s)
			{
				SunshineSceneVisitor.this.addVolume (v, t, s);
			}

			@Override
			protected Matrix4d getCurrentTransformation ()
			{
				return SunshineSceneVisitor.this.getCurrentTransformation ();
			}

			public Shader getCurrentShader ()
			{
				return SunshineSceneVisitor.this.getCurrentShader ();
			}

			public GraphState getRenderGraphState ()
			{
				return SunshineSceneVisitor.this.getGraphState ();
			}
			
		};
		
		volumes 		= new ArrayList<Volume> ();
		infiniteVolumes = new ArrayList<Volume> ();
	} // Constructor

	// stores the created volumes (need to initialize the octree)
	void addVolume (Volume v, Matrix4d t, Shader s)
	{
		int id = addVolume (v);
	
		shaders.set (id, s);
		Matrix4d m = new Matrix4d();
		m.m33 = 1;
		Math2.invertAffine(t, m);
		transforms.set(id, m);
	}

	private int addVolume (Volume v)
	{ 
		int id = volumeID;
		v.setId (id);
		(infinite ? infiniteVolumes : volumes).add (v);
		
		return id;
	}

	@Override
	protected void visitImpl(Object object, boolean asNode, Shader s, Path path)
	{
		// get the current shape
		Object shape = state.getObjectDefault (object, asNode, Attributes.SHAPE, null);
		
		if (shape != null)
		{
			
			infinite = state.getBooleanDefault (object, asNode,
					Attributes.TREATED_AS_INFINITE, false);
			
			/* 
			 * check objects, if there a sphere, box, cone...
			 * then create corresponding sunshine objects and deliver them 
			 * to the texture handler
			 */
			if (shape instanceof Renderable)
			{
				shader = getCurrentShader();
				
				if(shape instanceof Sphere)
				{
					volumeID = oh.getSphereID();
					builder.drawSphere( ((Sphere) shape).getRadius(), 
							null, 0, null);
					
				
					
					sObject = new SunshineSphere(((Sphere) shape).getRadius());
					
					Interior ior = ((Sphere)shape).getInterior();

//					if(ior != null) System.out.println("iorRatio" + ((IOR)ior).getIndexOfRefraction() );
				} //sphere
				
				if(shape instanceof Cylinder)
				{
					volumeID = oh.getCfcID();
					float r = state.getFloat (object, asNode, Attributes.RADIUS);
					float len = (float) state.getDouble (object, asNode, Attributes.LENGTH);
					
					builder.drawFrustum(len, r, r, 
							!((Cylinder) shape).isBaseOpen(), 
							!((Cylinder) shape).isTopOpen(), 
							((Cylinder) shape).isScaleV () ? len : 1, 
									null, 0, null);
					
					sObject = new SunshineCFC(	r, len,
							((Cylinder) shape).isTopOpen(),
							((Cylinder) shape).isBaseOpen());
					
				} //Cylinder
				
				if(shape instanceof Box)
				{
					volumeID = oh.getBoxID();
					builder.drawBox(((Box) shape).getWidth()*0.5f, 
							((Box) shape).getLength()*0.5f, 
							((Box) shape).getHeight(), 
							null, 0, null);
					
					
					sObject = new SunshineBox(	((Box) shape).getWidth(),
													((Box) shape).getHeight(), 
													((Box) shape).getLength());
					
				} //box
				
				if(shape instanceof Cone)
				{
					volumeID = oh.getCfcID();
					
					builder.drawFrustum( ((Cone) shape).getLength(), 
							((Cone) shape).getRadius(), 0, !((Cone) shape).isOpen(), 
							false, 
							((Cone) shape).isScaleV() ? ((Cone) shape).getLength() : 1, 
									null, 0, null);
					
					
					sObject = new SunshineCone(	((Cone) shape).isOpen(),
													((Cone) shape).getRadius(), 
													((Cone) shape).getLength());
					
				} //Cone
				
				if(shape instanceof Frustum)
				{
					volumeID = oh.getCfcID();
					
						// if top and base radius are equal then build a cylidner
						if (((Frustum) object).getBaseRadius() == ((Frustum) shape)
								.getTopRadius())
						{
							builder.drawFrustum( ((Frustum) shape).getLength(), 
									((Frustum) shape).getBaseRadius(), 
									((Frustum) shape).getBaseRadius(), 
									!((Frustum) shape).isBaseOpen(), 
									!((Frustum) shape).isTopOpen(), 
									((Frustum) shape).isScaleV () ? 
											((Frustum) shape).getLength() : 1, 
											null, 0, null);

							
							sObject = new SunshineCFC(((Frustum) shape)
									.getBaseRadius(), ((Frustum) shape)
									.getLength(), ((Frustum) shape).isTopOpen(),
									((Frustum) shape).isBaseOpen());
							
						}
						else
						{
							builder.drawFrustum(((Frustum) shape).getLength(), 
									((Frustum) shape).getBaseRadius(), 
									((Frustum) shape).getTopRadius(), 
									!((Frustum) shape).isBaseOpen(), 
									!((Frustum) shape).isTopOpen(), 
									((Frustum) shape).isScaleV () ? 
											((Frustum) shape).getLength() : 1, 
											null, 0, null);

							sObject = new SunshineFrustum(((Frustum) shape)
									.getBaseRadius(), ((Frustum) shape)
									.getTopRadius(),
									((Frustum) shape).getLength(),
									((Frustum) shape).isTopOpen(),
									((Frustum) shape).isBaseOpen());

						} // if

				} //Frustum
				
				if(shape instanceof Plane)
				{
					volumeID = oh.getPlaneID();
					
					builder.drawPlane(null, 0, null);
					
					sObject = new SunshinePlane();
				} //Plane
				
				
				if (shape instanceof Parallelogram)
				{
					volumeID = oh.getParaID();
					
					/*
					 * parallelogram can also be a light source
					 * when this happen two objects are stored 
					 */ 
					AreaLight light = ((Parallelogram)shape).getLight();
					boolean isLight = light != null;

					builder.drawParallelogram(((Parallelogram)shape).getLength(), 
							((Parallelogram)shape).getAxis(), 1, 
							((Parallelogram)shape).isScaleV() ? 
							((Parallelogram)shape).getLength() : 1, 
									null, 0, null);
					
					
					sObject = new SunshineParalellogram( 
							((Parallelogram)shape).getLength(),
							((Parallelogram)shape).getAxis(), isLight);
					
					
					if(isLight)
					{
						SunshineObject areaLight = new SunshineAreaLight(
								light.getPower(),
								light.getExponent(),
								light.isShadowless(),
								((Parallelogram)shape).getLength(),
								((Parallelogram)shape).getAxis(), count
								);
						
						Shader sh = ((Parallelogram)shape).getShader();
						if(sh instanceof RGBAShader)
						{
							areaLight.setShader( (RGBAShader) sh );							
						}
							
						
						areaLight.setTransformMatrix( getCurrentTransformation() );
						oh.storePrimitive(areaLight);
						
						System.out.println("color: "+areaLight.getColor());
					}
					/*
					 * count is an identifier for parallelograms which can also be
					 * light sources
					 */
					count++;
				} //Parallelogram
			}
		}
		
	
		// get light source
		Light light = (Light) state.getObjectDefault(object, asNode, Attributes.LIGHT, null);

		if (light != null && light instanceof PointLight)
		{
			sObject = new SunshineLight(((PointLight)light).getPower(),
					((PointLight)light).getAttenuationDistance(),
					((PointLight)light).getAttenuationExponent(),
					light.isShadowless());
			

			Color3f tmpColor = new Color3f();
			tmpColor = ((PointLight) light).getColor();
			sObject.setShader(tmpColor.x, tmpColor.y, tmpColor.z, 1);
			shader = null;
			
			if(light instanceof SpotLight)
			{
				((SunshineLight)sObject).setInnerAngle( ((SpotLight)light).getInnerAngle() );
				((SunshineLight)sObject).setOuterAngle( ((SpotLight)light).getOuterAngle() );
			}
		
			
		} // if

		
		
		if (sObject != null)
		{
			if(shader != null)
				sObject.setShader(getShader(shader));

			sObject.setTransformMatrix(getCurrentTransformation());
			
			// deliver the complete sunshine object to the texture handler
			oh.storePrimitive(sObject);
			sObject = null;
			shader = null;
		} // if

	} // visitImpl


	/* 
	 * returns a rgba vector for the color or
	 * a "pointer" to the phong shader 
	 */ 
	private Color4f getShader(Shader shader)
	{
		Color4f color = new Color4f(Color.gray);
		if (shader instanceof RGBAShader)
		{
			return (RGBAShader) shader;
		} // RGBAShader

		// Phong shader
		if (shader instanceof Phong)
		{	
			
			Phong p = (Phong)shader;
			
			// try to look up the phong shader
			Integer result = pc.get(p);

			// if non was found, then create one
			if(result != null)
			{
				// sets a "pointer" to the phong function for this object
				color.y = result;
				storeSunshinePhong(p, result);
			}
			
			// indicator for a phong shader
			color.x = -1;
			color.z = 0;
			
			
		} // Phong

		return color;
	} // getShader


	/*
	 * the next functions traverse the shader tree
	 */
	private String getDiffuse(ChannelMap diffuse)
	{
		return getShaderTree(diffuse);
	} //getDiffuse
	
	
	private String getSpecular(ChannelMap specular)
	{	
		return getShaderTree(specular);
	} //getSpecular
	
	
	private String getEmissive(ChannelMap emissive)
	{
		return getShaderTree(emissive);
	} //getEmissive
	
	
	private String getAmbient(ChannelMap ambient)
	{
		return getShaderTree(ambient);
	} //getAmbient
	
	
	private String getTransparency(ChannelMap trans)
	{
		return getShaderTree(trans);
	} //getTransparency
	
	
	private String getDiffTrans(ChannelMap diffTrans)
	{
		return getShaderTree(diffTrans);
	}
	
	
	private String getShininess(ChannelMap shininess)
	{
		String result = new String("vec4(0.5)");
		if(shininess != null)
		{
			if(shininess instanceof Graytone)
			{
				result = "vec4("+((Graytone) shininess).getValue()+")";
			}
		}
		
		return result;
	}
	
	/*
	 * the most simple shader is a rgba shader
	 * 
	 */
	private String getColor(ChannelMap cm)
	{
		String result = new String();
		
		if(cm instanceof ColorMap)
		{
			int rgba = ((ColorMap) cm).getAverageColor();
			float t = ((rgba & 255) + ((rgba >> 8) & 255) + ((rgba >> 16) & 255))
				* (1f / (3 * 255));
			
			result = "vec4("+t+")";
		} //if


		if (cm instanceof RGBColor)
		{
			Color3f col = ((Color3f) cm);
			result = "vec4("+col.x+","+col.y+","+col.z+",1.0)";
		} // RGBAColor

		return result;
	} //getColor1
	
	
	private String getChecker(ChannelMap cm)
	{
		String result = new String();
		
		//found a checker shader
		try
		{
			ph.appendFunction( ((SunshineShader)sunshineShaders.get("checker").newInstance()) );
		} catch(Exception e)
		{
			showMessage("Could not load class: " + sunshineShaders.get("checker"));
		} //try
		
		
		result = "getCheckerColor("; //function call
		
		result += checkInput(cm);
		
		result += "," + getColor( ((Checker)cm).getColor1() ) + "," + getColor( ((Checker)cm).getColor2()) +")";

		return result;
	} //getChecker
	
	
	private String getImage(ChannelMap cm)
	{
		String result = new String();
		
		//found an image shader
		try
		{
			ph.appendFunction( ((SunshineShader)sunshineShaders.get("image").newInstance()) );
		} catch(Exception e)
		{
			showMessage("Could not load class: " + sunshineShaders.get("image"));
		} //try
		
		
		Image image = ((ImageMap)cm).getImageAdapter().getNativeImage();
		
		Vector2f uv = new Vector2f(0,0);
		
		if (image != null)
		{
			uv = oh.storeImage( image );
		}
		
		result = "getImage(";
		
		result += checkInput(cm);
		
		result += ", "+ (int)uv.x*512 +" , "+(int)uv.y*512+")";
				
		return result;
	} //getImage
	
	
	private String checkInput(ChannelMap cm)
	{
		String result = new String();
		
		ChannelMapNode cmn = (ChannelMapNode) cm;
		
		ChannelMap input = cmn.getInput();
		
		if(input != null)
		{
			if(input instanceof UVTransformation)
			{
				//found an UV shader
				try
				{
					ph.appendFunction( ((SunshineShader)sunshineShaders.get("uv").newInstance()) );
				} catch(Exception e)
				{
					showMessage("Could not load class: " + sunshineShaders.get("uv"));
				} //try
				
	
				result = "getUvFloat(uv, "
					+((AffineUVTransformation)input).getScaleU()
					+", "+((AffineUVTransformation)input).getScaleV()
					+", "+((AffineUVTransformation)input).getAngle()+")";
			}
		}
		else
		{
			result = "uv";
		}
		
		return result;
	}
	
	
	private String getShaderTree(ChannelMap cm)
	{
		String result = new String("vec4(0.0)");
		
		if(cm instanceof RGBColor || cm instanceof Graytone)
			return getColor(cm);
		
		if (cm instanceof ImageMap)
			return result = getImage(cm);
		
		if(cm instanceof Checker)
			return result = getChecker(cm);
		
		return result;
	} //getShaderTree


	public void showMessage(String message)
	{
		workbench.logGUIInfo(message);
	} // showMessage
	
	
	public void setProgress (String text, float progress)
	{
		workbench.setStatus (this, text);
		if (progress < 0)
		{
			workbench.setIndeterminateProgress (this);
		}
		else if (progress == DONE_PROGRESS)
		{
			workbench.clearProgress (this);
		}
		else
		{
			workbench.setProgress (this, progress);
		}
	}
	
	public static int MAX_DEPTH = 5;
	public static int MIN_OBJ = 2;
	
	OctreeUnion getOctree ()
	{
		OctreeUnion v = new OctreeUnion ();
		v.volumes.addAll (volumes);
		volumes.clear ();
		volumes = null;
		v.initialize (MAX_DEPTH, MIN_OBJ, new DefaultCellIterator ());
	
		sceneVolume = v;
		
		return sceneVolume;
	}
	
	
	private SunshinePhong createSunshinePhong(Phong p, int z)
	{
		SunshinePhong sp = new SunshinePhong(z);
		
		Phong m = (Phong) getCurrentShader();

			
		sp.setAmbient( getAmbient(m.getAmbient() ) );

		sp.setEmissive( getEmissive(m.getEmissive() ) );
		
		sp.setDiffuse( getDiffuse( m.getDiffuse() ) );
		
		sp.setSpecular( getSpecular( m.getSpecular() ) );
		
		sp.setTransparency( getTransparency( m.getTransparency() ) );
		
		sp.setDiffTrans( getDiffTrans( m.getDiffuseTransparency() ) );

		sp.setShininess( getShininess( m.getShininess() ));
		
		return sp;
	}
	
	
	private void storeSunshinePhong(Phong p, Integer i)
	{
		SunshinePhong result = phongCache.get(i);
		
		if(result == null)
		{
			result = createSunshinePhong(p, i);
			phongCache.put(i, result);
			ph.setPhong(result, pc.size());
		}
	} //storeSunshinePhong
	
	
	public String getPhong()
	{
		return ph.getPhong();
	}

} // class
