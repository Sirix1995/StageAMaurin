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
import de.grogra.ext.sunshine.spectral.MaterialHandler;
import de.grogra.ext.sunshine.spectral.lights.SpectralAreaLight;
import de.grogra.ext.sunshine.spectral.shader.LambertShader;
import de.grogra.ext.sunshine.spectral.shader.SunshineSpectralShader;
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
import de.grogra.imp3d.shading.IOR;
import de.grogra.imp3d.shading.ImageMap;
import de.grogra.imp3d.shading.Interior;
import de.grogra.imp3d.shading.Phong;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.imp3d.shading.Shader; //import de.grogra.imp3d.shading.SunshineSpectralShader;
import de.grogra.imp3d.shading.SunSkyLight;
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
import java.nio.ByteBuffer;

/**
 * this class travers the scene graph a second time it handles the objects, the
 * glsl phong shader and the textures
 * 
 * @author Thomas
 * 
 */
public class SunshineSceneVisitor extends DisplayVisitor implements
		ProgressMonitor
{
	public static enum COLOR_MODE {
		MODE_RGB,
		MODE_SPECTRAL		
	}
	
	// width and height of the images for the texture atlas
	static final int IMAGE_WIDTH = 512;
	static final int IMAGE_HEIGHT = 512;

	private static final String CHECKER = "checker";
	private static final String IMAGE = "image";
	private static final String UV = "uv";

	private Workbench workbench;
	private SunshineObject sObject;
	private ObjectHandler oh;
	private Shader shader;
	private int count = 0;

	private COLOR_MODE spectralMode = COLOR_MODE.MODE_RGB;
	
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
	private ObjectList<Shader> shaders = new ObjectList<Shader>();
	private ObjectList<Matrix4d> transforms = new ObjectList<Matrix4d>();
	PhongHandler ph;
	private Options options;
	private boolean hasSunSky = false;
	
	private Sky sky;
	private String skyParas = "";
	
	/* SunshineSpectral property */
	MaterialHandler mh;


	public SunshineSceneVisitor(Workbench wb, Graph graph, ViewConfig3D view,
			ObjectHandler oh, Options opts, float epsilon, int[] objects,
			Hashtable<Phong, Integer> phongCache, GraphState gs)
	{
		Matrix4d m = new Matrix4d();
		m.setIdentity();

		init(gs, m, view, view != null);

		this.oh = oh;
		workbench = wb;
		workbench.beginStatus(this);

		pc = phongCache;
		ph = new PhongHandler();

		options = opts;

		// store the shader classes in the hashtable
		sunshineShaders.put(CHECKER, SunshineChecker.class);
		sunshineShaders.put(IMAGE, SunshineImage.class);
		sunshineShaders.put(UV, SunshineUVTravo.class);

		
		// the volume builder is needed for the octree implementation
		builder = new SunshineVolumeBuilder(new PolygonizationCache(state,
				Polygonization.COMPUTE_NORMALS | Polygonization.COMPUTE_UV,
				((Number) opts.get("flatness", new Float(1))).floatValue(),
				true), epsilon);

		
		volumes = new ArrayList<Volume>();
		infiniteVolumes = new ArrayList<Volume>();
	} // Constructor


	protected void storeImage(ByteBuffer imageBB, int mode)
	{
		if(options instanceof Sunshine)
			((Sunshine) options).showImage(imageBB, mode);
	}


	// stores the created volumes (need to initialize the octree)
	void addVolume(Volume v, Matrix4d t, Shader s)
	{
		int id = addVolume(v);

		shaders.set(id, s);
		Matrix4d m = new Matrix4d();
		m.m33 = 1;
		Math2.invertAffine(t, m);
		transforms.set(id, m);
	}


	
	
	public void setColorMode(COLOR_MODE mode)
	{
		spectralMode = mode;
	}
	
	private int addVolume(Volume v)
	{
		int id = volumeID;
		v.setId(id);
		(infinite ? infiniteVolumes : volumes).add(v);

		return id;
	}

	@Override
	protected void visitImpl(Object object, boolean asNode, Shader s, Path path)
	{
		// get the current shape
		Object shape = state.getObjectDefault(object, asNode, Attributes.SHAPE, null);
		
		float ior = 1f;

		if(shape != null)
		{

			infinite = state.getBooleanDefault(object, asNode,
					Attributes.TREATED_AS_INFINITE, false);

			/*
			 * check objects, if there a sphere, box, cone... then create
			 * corresponding sunshine objects and deliver them to the texture
			 * handler
			 */
			if(shape instanceof Renderable)
			{
				shader = getCurrentShader();
				
				if(shape instanceof Sky)
				{	
					sky = (Sky) shape;
					hasSunSky = true;			
					
					if(shader instanceof SunSkyLight)
					{
						SunshineSunSky sunshinesky = new SunshineSunSky();
						
						sunshinesky.setSun(((SunSkyLight) shader).getSun());
						sunshinesky.setDisableSun(((SunSkyLight) shader).isDisableSun());
						sunshinesky.setDisableLight(((SunSkyLight) shader).isDisableLight());
						sunshinesky.setRadianceFactor(((SunSkyLight) shader).getRadianceFactor());
						sunshinesky.setTurbidity(((SunSkyLight) shader).getTurbidity());
						
						skyParas = sunshinesky.getParams();
					}

					sObject = null;
					shader = null;
					
					return;
				}
				
				if(shape instanceof Sphere)
				{
					volumeID = oh.getSphereID();
					builder.drawSphere(((Sphere) shape).getRadius(), null, 0,
							null);

					sObject = new SunshineSphere(((Sphere) shape).getRadius());

//					setIOR(sObject, ((Sphere) shape).getInterior());
					
				} // sphere

				if(shape instanceof Cylinder)
				{
					volumeID = oh.getCfcID();
					float r = state.getFloat(object, asNode, Attributes.RADIUS);
					float len = (float) state.getDouble(object, asNode,
							Attributes.LENGTH);

					builder.drawFrustum(len, r, r, !((Cylinder) shape)
							.isBaseOpen(), !((Cylinder) shape).isTopOpen(),
							((Cylinder) shape).isScaleV() ? len : 1, null, 0,
							null);

					sObject = new SunshineCFC(r, len, ((Cylinder) shape)
							.isTopOpen(), ((Cylinder) shape).isBaseOpen());

				} // Cylinder

				if(shape instanceof Box)
				{
					volumeID = oh.getBoxID();
					builder.drawBox(((Box) shape).getWidth() * 0.5f,
							((Box) shape).getLength() * 0.5f, ((Box) shape)
									.getHeight(), null, 0, null);

					sObject = new SunshineBox(((Box) shape).getWidth(),
							((Box) shape).getHeight(), ((Box) shape)
									.getLength());

				} // box

				if(shape instanceof Cone)
				{
					volumeID = oh.getCfcID();

					builder.drawFrustum(((Cone) shape).getLength(),
							((Cone) shape).getRadius(), 0, !((Cone) shape)
									.isOpen(), false,
							((Cone) shape).isScaleV() ? ((Cone) shape)
									.getLength() : 1, null, 0, null);

					sObject = new SunshineCone(((Cone) shape).isOpen(),
							((Cone) shape).getRadius(), ((Cone) shape)
									.getLength());

				} // Cone

				if(shape instanceof Frustum)
				{
					volumeID = oh.getCfcID();

					// if top and base radius are equal then build a cylidner
					if(((Frustum) object).getBaseRadius() == ((Frustum) shape)
							.getTopRadius())
					{
						builder
								.drawFrustum(
										((Frustum) shape).getLength(),
										((Frustum) shape).getBaseRadius(),
										((Frustum) shape).getBaseRadius(),
										!((Frustum) shape).isBaseOpen(),
										!((Frustum) shape).isTopOpen(),
										((Frustum) shape).isScaleV() ? ((Frustum) shape)
												.getLength()
												: 1, null, 0, null);

						sObject = new SunshineCFC(((Frustum) shape)
								.getBaseRadius(),
								((Frustum) shape).getLength(),
								((Frustum) shape).isTopOpen(),
								((Frustum) shape).isBaseOpen());

					}
					else
					{
						builder
								.drawFrustum(
										((Frustum) shape).getLength(),
										((Frustum) shape).getBaseRadius(),
										((Frustum) shape).getTopRadius(),
										!((Frustum) shape).isBaseOpen(),
										!((Frustum) shape).isTopOpen(),
										((Frustum) shape).isScaleV() ? ((Frustum) shape)
												.getLength()
												: 1, null, 0, null);

						sObject = new SunshineFrustum(((Frustum) shape)
								.getBaseRadius(), ((Frustum) shape)
								.getTopRadius(), ((Frustum) shape).getLength(),
								((Frustum) shape).isTopOpen(),
								((Frustum) shape).isBaseOpen());

					} // if

				} // Frustum

				if(shape instanceof Plane)
				{
					volumeID = oh.getPlaneID();

					builder.drawPlane(null, 0, null);

					sObject = new SunshinePlane();
				} // Plane

				if(shape instanceof Parallelogram)
				{
					volumeID = oh.getParaID();

					/*
					 * parallelogram can also be a light source when this happen
					 * two objects are stored
					 */
					AreaLight light = ((Parallelogram) shape).getLight();
					boolean isLight = light != null;

					builder.drawParallelogram(((Parallelogram) shape)
									.getLength(), ((Parallelogram) shape)
									.getAxis(), 1, ((Parallelogram) shape)
									.isScaleV() ? ((Parallelogram) shape)
									.getLength() : 1, null, 0, null);

					sObject = new SunshineParalellogram(((Parallelogram) shape)
							.getLength(), ((Parallelogram) shape).getAxis(),
							isLight);

					if(isLight)
					{
						SunshineObject areaLight = new SunshineAreaLight(light
								.getPower(), light.getExponent(), light
								.isShadowless(), ((Parallelogram) shape)
								.getLength(),
								((Parallelogram) shape).getAxis(), count);

						Shader sh = ((Parallelogram) shape).getShader();
						
						if(light instanceof SpectralAreaLight)
						{
							LambertShader lam = new LambertShader();
							lam.setChannel(((SpectralAreaLight) light).getChannel());
							
							areaLight.setShader(getShader(lam, 1.0f));
							
						} else if(sh instanceof SunshineSpectralShader) {
							
							areaLight.setShader(getShader(sh, 1.0f));
							
						} else if(sh instanceof RGBAShader)
						{
							areaLight.setShader((RGBAShader) sh);
						} 


						areaLight.setTransformMatrix(getCurrentTransformation());
						oh.storePrimitive(areaLight);

					}
					/*
					 * count is an identifier for parallelograms which can also
					 * be light sources
					 */
					count++;
				} // Parallelogram

				if(shape instanceof MeshNode)
				{
					volumeID = oh.getTriangleID();					

					builder.drawPolygons((MeshNode) shape, object, asNode,
							null, 0, getCurrentTransformation());

					PolygonMesh p = (PolygonMesh) ((MeshNode) shape).getPolygons();

					// every triangle has 3 pointer (indices) on vertices
					int[] index = p.getIndexData();

					
					if(index.length > 0)
					{
						sObject = new SunshineTriangles(true, p.getVertexData(), index,
								p.getNormalData(), p.getTextureData());

						if(shader != null)
							sObject.setShader(getShader(shader, getIOR(sObject, (ShadedNull) shape)));

						sObject.setTransformMatrix(getCurrentTransformation());

						// deliver the complete sunshine object to the texture
						// handler
						oh.storePrimitive(sObject);					
						
						sObject = null;
						shader = null;
					}
				} // Triangle
			}
		}

		// get light source
		Light light = (Light) state.getObjectDefault(object, asNode,
				Attributes.LIGHT, null);

		if(light != null && light instanceof PointLight)
		{
			sObject = new SunshineLight(((PointLight) light).getPower(),
					((PointLight) light).getAttenuationDistance(),
					((PointLight) light).getAttenuationExponent(), light
							.isShadowless());

			Color3f tmpColor = new Color3f();
			tmpColor = ((PointLight) light).getColor();
			
			sObject.setShader(tmpColor.x, tmpColor.y, tmpColor.z, 1);
			shader = null;

			if(light instanceof SpotLight)
			{
				((SunshineLight) sObject).setInnerAngle(((SpotLight) light)
						.getInnerAngle());
				((SunshineLight) sObject).setOuterAngle(((SpotLight) light)
						.getOuterAngle());
			}

		} // if

		if(sObject != null)
		{
			if(!(shape instanceof LightNode))
			{
				ior = getIOR(sObject, (ShadedNull) shape);
				sObject.setIOR(ior);
			}
			
			if(shader != null)
				sObject.setShader(getShader(shader, ior));

			sObject.setTransformMatrix(getCurrentTransformation());

			// deliver the complete sunshine object to the texture handler
			oh.storePrimitive(sObject);
			sObject = null;
			shader 	= null;
		} // if

	} // visitImpl

	public String getSunSkyParas()
	{
		return skyParas;
	}
	
	public boolean hasSunSky()
	{
		return hasSunSky;
	}
	
	private float getIOR(SunshineObject sobject, ShadedNull object)
	{
		Interior ior = object.getInterior();
		
		if(ior != null)
		{
			return ((IOR) ior).getIndexOfRefraction();
		}
		
		return 1f;
	}

	/*
	 * returns a rgba vector for the color or a "pointer" to the phong shader
	 */
	private Color4f getShader(Shader shader, float ior)
	{
		Color4f color = new Color4f(Color.gray);
		if(shader instanceof RGBAShader)
		{
			return (RGBAShader) shader;
		} // RGBAShader

		// Phong shader
		if(shader instanceof Phong)
		{
			Phong p = (Phong) shader;

			// try to look up the phong shader
			Integer result = pc.get(p);

			// if non was found, then create one
			if(result != null)
			{
				// sets a "pointer" to the phong function for this object
				color.y = result;
				storeSunshinePhong(p, result, ior);
			}

			// indicator for a phong shader
			color.x = -1;
			color.z = 0;

		} // Phong

		if(shader instanceof SunshineSpectralShader) 
		{ 
			
			switch (spectralMode)
			{
				case MODE_RGB:
				{
					int intCol	= ((SunshineSpectralShader) shader).getAverageColor();
					
					color.w 	= ((intCol >> 24) & 255) * (1f / 255);
					color.x 	= ((intCol >> 16) & 255) * (1f / 255);
					color.y 	= ((intCol >> 8) & 255) * (1f / 255);
					color.z 	= (intCol & 255) * (1f / 255);				
					
					break;
				}
				case MODE_SPECTRAL:
				{
					if(mh == null)
					{
						mh = new MaterialHandler();
					}
					
					color = mh.addShader((SunshineSpectralShader) shader);
					break;
				}
				default :
				{
					System.err.println("ERROR: Unknown color mode in " + this.getClass().getName());
					break;
				}
			}
			
		}
		
		return color;
	} // getShader


	private String getShininess(ChannelMap shininess, 
			ChannelMap transparentShininess, float ior)
	{		
		String result = new String("vec4(SHININESS, TSHININESS, 0.5, IOR)");
		
		if(shininess != null && shininess instanceof Graytone)
		{
			String s = "" + ((Graytone) shininess).getValue();
			result = result.replaceFirst("SHININESS", s);
		} 
		else
		{
			result = result.replaceFirst("SHININESS", "-1.0");
		}

		
		if(transparentShininess != null && transparentShininess instanceof Graytone)
		{	
			String ts = "" + ((Graytone) transparentShininess).getValue();
			result = result.replaceFirst("TSHININESS", ts);
		} 
		else
		{
			result = result.replaceFirst("TSHININESS", "-1.0");
		}
		

		result = result.replaceFirst("IOR", "" + ior);
		
		return result;
	} //getShininess

	
	public MaterialHandler getMatHandler()
	{
		return mh;
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

			result = "vec4(" + t + ", " + t + "," + t + ",1.0)";
		} // if

		if(cm instanceof RGBColor)
		{
			Color3f color = ((Color3f) cm);
			result = "vec4(" + color.x + "," + color.y + "," + color.z
					+ ",1.0)";
		} // RGBAColor

		return result;
	} // getColor

	private String getChecker(ChannelMap cm)
	{
		String result = new String();
		SunshineShader checker;

		// found a checker shader
		try
		{
			checker = (SunshineShader) sunshineShaders.get(CHECKER)
					.newInstance();
			ph.appendFunction(checker);

			result = checker.getMethodCall();

			result = result.replaceFirst("UV", checkInput(cm));
			result = result.replaceFirst("COLOR1", getColor(((Checker) cm)
					.getColor1()));
			result = result.replaceFirst("COLOR2", getColor(((Checker) cm)
					.getColor2()));

		}
		catch(Exception e)
		{
			showMessage("Could not load class: " + sunshineShaders.get(CHECKER));
		} // try

		return result;
	} // getChecker


	private String getImage(ChannelMap cm)
	{
		String result = new String();
		Vector2f uv = new Vector2f(0, 0);
		SunshineShader imageShader;

		// found an image shader
		try
		{
			imageShader = (SunshineShader) sunshineShaders.get(IMAGE)
					.newInstance();
			ph.appendFunction(imageShader);

			Image image = ((ImageMap) cm).getImageAdapter().getNativeImage();

			if(image != null)
			{
				uv = oh.storeImage(image);
			}

			result = imageShader.getMethodCall();

			result = result.replaceFirst("UV", checkInput(cm));
			result = result.replaceFirst("X_COORD", "" + uv.x * 512);
			result = result.replaceFirst("Y_COORD", "" + uv.y * 512);

		}
		catch(Exception e)
		{
			showMessage("Could not load class: " + sunshineShaders.get(IMAGE));
		} // try

		return result;
	} // getImage


	private String checkInput(ChannelMap cm)
	{
		String result = new String(UV);

		ChannelMapNode cmn = (ChannelMapNode) cm;

		ChannelMap input = cmn.getInput();

		SunshineShader inputShader;

		if(input != null)
		{
			if(input instanceof UVTransformation)
			{
				// found an UV shader
				try
				{
					inputShader = (SunshineShader) sunshineShaders.get(UV)
							.newInstance();
					ph.appendFunction(inputShader);

					result = inputShader.getMethodCall();

					result = result.replaceFirst("SCALE_U", ""
							+ ((AffineUVTransformation) input).getScaleU());
					result = result.replaceFirst("SCALE_V", ""
							+ ((AffineUVTransformation) input).getScaleV());
					result = result.replaceFirst("ANGLE", ""
							+ ((AffineUVTransformation) input).getAngle());

				}
				catch(Exception e)
				{
					showMessage("Could not load class: "
							+ sunshineShaders.get(UV));
				} // try

			}
		} // if

		return result;
	} // checkInput


	private String getShaderTree(ChannelMap cm)
	{
		String result = new String("vec4(0.0, 0.0, 0.0, 1.0)");

		if(cm instanceof RGBColor || cm instanceof Graytone)
			return getColor(cm);

		if(cm instanceof ImageMap)
			return result = getImage(cm);

		if(cm instanceof Checker)
			return result = getChecker(cm);

		return result;
	} // getShaderTree


	public void showMessage(String message)
	{
		workbench.logGUIInfo(message);
	} // showMessage


	public void setProgress(String text, float progress)
	{
		workbench.setStatus(this, text);
		if(progress < 0)
		{
			workbench.setIndeterminateProgress(this);
		}
		else if(progress == DONE_PROGRESS)
		{
			workbench.clearProgress(this);
		}
		else
		{
			workbench.setProgress(this, progress);
		}
	}

	public static int MAX_DEPTH = 5;
	public static int MIN_OBJ 	= 2;


	OctreeUnion getOctree()
	{
		OctreeUnion v = new OctreeUnion();
		v.volumes.addAll(volumes);
		volumes.clear();
		volumes = null;
		v.initialize(MAX_DEPTH, MIN_OBJ, new DefaultCellIterator());

		sceneVolume = v;

		return sceneVolume;
	}
	
	private SunshinePhong createSunshinePhong(Phong p, int z, float ior)
	{
		SunshinePhong sp = new SunshinePhong(z);

		Phong m = (Phong) getCurrentShader();
		
		sp.setAmbient(getShaderTree(m.getAmbient()));

		sp.setEmissive(getShaderTree(m.getEmissive()));

		sp.setDiffuse(getShaderTree(m.getDiffuse()));

		sp.setSpecular(getShaderTree(m.getSpecular()));

		sp.setTransparency(getShaderTree(m.getTransparency()));

		sp.setDiffTrans(getShaderTree(m.getDiffuseTransparency()));

		sp.setShininess(getShininess(m.getShininess(), m.getTransparencyShininess(), ior));
		
		return sp;
	}


	private void storeSunshinePhong(Phong p, Integer i, float ior)
	{
		SunshinePhong result = phongCache.get(i);

		if(result == null)
		{
			result = createSunshinePhong(p, i, ior);
			phongCache.put(i, result);
			ph.setPhong(result, pc.size());
		}
	} // storeSunshinePhong


	public String getPhong()
	{
		return ph.getPhong();
	}
	
	private class SunshineVolumeBuilder extends VolumeBuilder
	{
		/**
		 * @param polyCache
		 * @param epsilon
		 */
		public SunshineVolumeBuilder(PolygonizationCache polyCache,
				float epsilon)
		{
			super(polyCache, epsilon);
		}
		
		@Override
		protected void addVolume(Volume v, Matrix4d t, Shader s)
		{
			SunshineSceneVisitor.this.addVolume(v, t, s);
		}


		@Override
		protected Matrix4d getCurrentTransformation()
		{
			return SunshineSceneVisitor.this.getCurrentTransformation();
		}


		public Shader getCurrentShader()
		{
			return SunshineSceneVisitor.this.getCurrentShader();
		}


		public GraphState getRenderGraphState()
		{
			return SunshineSceneVisitor.this.getGraphState();
		}

	}

} // class
