package de.grogra.ray;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

import de.grogra.ray.antialiasing.*;
import de.grogra.ray.debug3d.Debug3d;
import de.grogra.ray.event.*;
import de.grogra.ray.intersection.DefaultIntersectionProcessor;
import de.grogra.ray.intersection.IntersectionProcessor;
import de.grogra.ray.intersection.OctreeIntersectionProcessor;
import de.grogra.ray.light.DefaultLightProcessor;
import de.grogra.ray.light.LightProcessor;
import de.grogra.ray.memory.MemoryPool;
import de.grogra.ray.quality.Quality;
import de.grogra.ray.quality.Timer;
import de.grogra.ray.tracing.DefaultRayTracer;
import de.grogra.ray.tracing.PathTracerHS;
import de.grogra.ray.tracing.PathTracerMT;
import de.grogra.ray.tracing.PhotonMapping;
import de.grogra.ray.tracing.RayProcessor;
import de.grogra.ray.tracing.BidirectionalPathTracer;
import de.grogra.ray.util.Ray;


public class Raytracer extends ProgressNotifier {
	
//	public final static boolean EXPORT_ALPHA_CHANNEL = true;
	
	
	public final static int NO_ANTIALISING                 = 0;
	public final static int ADAPTIVE_SUPERSAMPLING         = 1;
	public final static int STOCHASTIC_SUPERSAMPLING       = 2;
	
	public final static int CONVENTIONAL_RAYTRACING        = 20;
	public final static int PATHTRACING_MT                 = 21;
	public final static int BIDIRECTIONAL_PATHTRACING      = 22;
	public final static int PATHTRACING_HS                 = 23;
	public final static int PHOTONMAPPING                  = 24;
	
	public final static int NAIVE_INTERSECTION             = 40;
	public final static int OCTREE_INTERSECTION            = 41;
	
	public final static int DIRECT_LIGHTS                  = 60;
	
	public final static int LOW_PRIORITY    = 0;
	public final static int MEDIUM_PRIORITY = 1;
	public final static int HIGH_PRIORITY   = 2;
	

	public final static String PREPARING_TIMER    = "timer.preparing";
	public final static String CALCULATION_TIMER  = "timer.calculation";
	
	
	protected Antialiasing          m_antialising           = null;
	protected RayProcessor          m_rayProcessor          = null;
	protected IntersectionProcessor m_intersectionProcessor = null;
	protected LightProcessor        m_lightProcessor            = null;
	protected int m_raytracingDepth = 8;
	
	private int m_priority = MEDIUM_PRIORITY;
	
	private int m_imageUpdateDistance = 5;
	
	private int m_lastWidth    = 0;
	private int m_lastHeight   = 0;
	private int m_objectsCount = 0;
	private int m_lightsCount  = 0;
	
	private boolean m_debugPixel       = false;
	private int     m_debugPixelX      = 0;
	private int     m_debugPixelY      = 0;
	private boolean m_debugPixelMarkIt = false;
	
	private float m_brightnessScaleFactor = 0.2f;
	
		
	public Raytracer() {
		this.setAntialisingPolicy(Raytracer.NO_ANTIALISING);
		this.setRaytracingPolicy(Raytracer.CONVENTIONAL_RAYTRACING);
		this.setIntersectionPolicy(Raytracer.NAIVE_INTERSECTION);
		this.setPriority(Raytracer.HIGH_PRIORITY);
		this.setLightModel(Raytracer.DIRECT_LIGHTS);
	}
	
	
	public void setAntialisingPolicy(int policy) {
		switch(policy) {
		case NO_ANTIALISING:
			if (!(m_antialising instanceof NoAntialiasing)) {
				m_antialising = new NoAntialiasing();
			}
			break;
		case ADAPTIVE_SUPERSAMPLING:
			if (!(m_antialising instanceof AdaptiveSupersampling)) {
				m_antialising = new AdaptiveSupersampling();
			}
			break;
		case STOCHASTIC_SUPERSAMPLING:
			if (!(m_antialising instanceof StochasticSupersampling)) {
				m_antialising = new StochasticSupersampling();
			}
			break;
		default:
			System.err.println("unknown antialising policy");
		}
	}
	
	
	public void setRaytracingPolicy(int policy) {
		switch(policy) {
		case CONVENTIONAL_RAYTRACING:
			if (!(m_rayProcessor instanceof DefaultRayTracer)) {
				m_rayProcessor = new DefaultRayTracer();
			}
			break;
		case PATHTRACING_MT:
			if (!(m_rayProcessor instanceof PathTracerMT)) {
				m_rayProcessor = new PathTracerMT();
			}
			break;
		case BIDIRECTIONAL_PATHTRACING:
			if (!(m_rayProcessor instanceof BidirectionalPathTracer)) {
				m_rayProcessor = new BidirectionalPathTracer();
			}
			break;
		case PATHTRACING_HS:
			if (!(m_rayProcessor instanceof PathTracerHS)) {
				m_rayProcessor = new PathTracerHS();
			}
			break;
		case PHOTONMAPPING:
			if (!(m_rayProcessor instanceof PhotonMapping)) {
				m_rayProcessor = new PhotonMapping();
			}
			break;
		default:
			System.err.println("unknown raytracing policy");
		}
	}
	
	
	public void setPathtracingPathCount(int value) {
		if (m_rayProcessor instanceof PathTracerMT) {
			((PathTracerMT)m_rayProcessor).setPathCount(value);
		}
	}
	
	
	public void setIntersectionPolicy(int policy) {
		switch(policy) {
		case NAIVE_INTERSECTION:
			if (!(m_intersectionProcessor instanceof DefaultIntersectionProcessor)) {
				m_intersectionProcessor = new DefaultIntersectionProcessor();
			}
			break;
		case OCTREE_INTERSECTION:
			if (!(m_intersectionProcessor instanceof OctreeIntersectionProcessor)) {
				m_intersectionProcessor = new OctreeIntersectionProcessor();
			}
			break;
		default:
			System.err.println("unknown intersection policy");	
		}
	}
	
	
	public void setLightModel(int policy) {
		if (m_rayProcessor.hasFixedLightProcessor()) {
			System.err.println("ray processor has fixxed light model - " +
					"light model cannot be changed or change " +
					"raytracing policy");
		}
		
		switch(policy) {
		case Raytracer.DIRECT_LIGHTS:	
			if (!(m_lightProcessor instanceof DefaultLightProcessor)) {
				m_lightProcessor = new DefaultLightProcessor();
			}
			break;
		default:
			System.err.println("unknown light model policy");	
		}
	}
	
	
	public void setPriority(int value) {
		if ((value>=LOW_PRIORITY)&&(value<=HIGH_PRIORITY)) {
			m_priority = value;
		}
	}
	
	
	public int getPriority() { return m_priority; }
	
	
	public void setRaytracingDepth(int value) {
		if (value>=0) { m_raytracingDepth = value; }
	}
	
	
	public int getRaytracingDepth() { return m_raytracingDepth; }
	

	public void enableDebugPixel(int x,int y,boolean markPixel) {
		m_debugPixel = true;
		m_debugPixelX = x;
		m_debugPixelY = y;
		m_debugPixelMarkIt = markPixel;
	}
	
	
	public void disableDebugPixel() {
		m_debugPixel = false;
	}
	
	
	public void setBrightnessScaleFactor(float value) {
		m_brightnessScaleFactor = value;
	}
	
	
	public void renderScene(RTScene scene, RTCamera camera, 
			                BufferedImage image) {
		
//		MemoryPool.getPool().gc();
//		MemoryPool.getPool().resetReuse();	
		
		m_objectsCount = scene.getShadeablesCount();
		m_lightsCount  = scene.getLightsCount();
		
		prepareRaytracing(scene, camera);

		raytracing(image);
		System.out.println();
		
	}

	
	//--- prepare raytracing ----------------------------------------------		
	protected void prepareRaytracing(RTScene scene, RTCamera camera) {
		Timer preparing_timer = Quality.getQualityKit().getTimer(Raytracer.PREPARING_TIMER);
		preparing_timer.reset();
		
		fire_progressChanged(RTProgressListener.RENDERING_PREPROCESSING,0.0,
				RTResources.getString("de.grogra.ray.progress.raytracer." +
						"preprocessing.prepareIntersection"),
				0,0,0,0);
		
		// prepare intersection processor
		preparing_timer.start();
		m_intersectionProcessor.prepareProcessing(scene);
		preparing_timer.stop();
		
		fire_progressChanged(RTProgressListener.RENDERING_PREPROCESSING,0.7,
				RTResources.getString("de.grogra.ray.progress.raytracer." +
						"preprocessing.prepareRaytracing"),
				0,0,0,0);
		
		// prepare ray processor and also antialiasing
		preparing_timer.start();
		if (!m_rayProcessor.hasFixedLightProcessor()) {
			m_rayProcessor.setLightProcessor(m_lightProcessor);
		}
		m_rayProcessor.setRecursionDepth(m_raytracingDepth);
		m_rayProcessor.prepareRayProcessor(scene,m_intersectionProcessor);
		m_antialising.initialize(camera,m_rayProcessor);
		preparing_timer.stop();
		
		fire_progressChanged(RTProgressListener.RENDERING_PREPROCESSING,1.0,
				RTResources.getString("de.grogra.ray.progress.done"),
				0,0,0,0);
		
		// debug 
		Debug3d.logCamera(camera);
		Debug3d.logScene(scene);
	}
	
	
	//--- raytracing ------------------------------------------------------	
	protected void raytracing(BufferedImage image){
		
		// debug
		Debug3d.enableDebug3D(m_debugPixel);
		Debug3d.clear();
		
		Timer calculation_timer = Quality.getQualityKit().
			getTimer(Raytracer.CALCULATION_TIMER);
		calculation_timer.reset();

		fire_progressChanged(RTProgressListener.RENDERING_PROCESSING,0.0,
				RTResources.getString("de.grogra.ray.progress.raytracer." +
						"processing.calculate"),
				0,0,0,0);
		
		calculation_timer.start();
		int width = image.getWidth();
		int height = image.getHeight();
		m_lastWidth = width;
		m_lastHeight = height;
		double pixel_width  = 2.0 / width;
		double pixel_height = 2.0 / height;
		double x_offset = -1.0;
		double y_offset = -1.0;
		Color4f color = new Color4f();
		int lastImageUpdate = -1;
		String status;
		
		for (int y=0;y<height;y++) {
			
			// abort calculation ?
			if (Thread.interrupted ()) { return; }
			
			if (m_priority<=LOW_PRIORITY) {
				sleepALittle(100);
			}
			
			x_offset = -1.0;
			for (int x=0;x<width;x++) {
							
				if (m_priority<=MEDIUM_PRIORITY) {
					sleepALittle(0);
				}
				
				if (m_debugPixel) {
					if ((x==m_debugPixelX)&&(y==m_debugPixelY)) {
						Debug3d.enableDebug3D(true);
					} else {
						Debug3d.enableDebug3D(false);
					}
				}
				
//				if (((x==122)&&(y==143))) {
//					test = true;
//				} else {
//					test = false;
//				}
				
//				if ((x==width/2)&&(y==height/2)) {
//				if ((y==132)) {
//				if (((x==462)&&(y==229))||((x==387)&&(y==157))) {
//				if (((x==150)&&(y==237))||((x==150)&&(y==200))) {
//				if (((x==261)&&(y==139))) {
//				if (((x==212)&&(y==61))) {
//				if (true) {
				m_antialising.getColorFromFrustum(
						x_offset,y_offset,
						pixel_width,pixel_height,
						color);
				
				if (m_debugPixel&&m_debugPixelMarkIt) {
					if (((m_debugPixelX-2)<=x)&&(x<=(m_debugPixelX+2))&&
						((m_debugPixelY-2)<=y)&&(y<=(m_debugPixelY+2))&&
						!((x==m_debugPixelX)&&(y==m_debugPixelY))) {
						color.set(1000.0f,0.0f,0.0f,1.0f);
					}
				}
				
				validateColor(color);

				addBackgroundToColor(x,y,color);

				image.setRGB(x,y,getScaledIntColor(color));
//				}
				
				x_offset += pixel_width;
			}
					
			// progress changed
			if ( ((y%m_imageUpdateDistance)==(m_imageUpdateDistance-1)) ||
					(y==height-1) ) {
				
				int change_height;
				if (y<height-1) {
					for (int x=0;x<width;x++) {
						image.setRGB(x,y+1,255 << 24 | 255 << 16 | 255 << 8 | 255);
					}
					change_height = y-lastImageUpdate+1;
				} else {
					change_height = y-lastImageUpdate;
				}
				
				status = RTResources.getString("de.grogra.ray.progress.raytracer.processing.calculate")+
					" - "+percentToString(y/(height-1.0)*100.0);
				calculation_timer.stop();
				fire_progressChanged(RTProgressListener.RENDERING_PROCESSING,y/(height-1.0),
						status,
						0,lastImageUpdate+1,width,change_height);
				calculation_timer.start();
				lastImageUpdate = y;
			} 
			y_offset += pixel_height;
		}
		m_intersectionProcessor.cleanupProcessing();
		
		calculation_timer.stop();
				
		fire_progressChanged(RTProgressListener.RENDERING_PROCESSING,1.0,
				RTResources.getString("de.grogra.ray.progress.done"),
				0,0,0,0);
		
		// debug
		Debug3d.enableDebug3D(m_debugPixel);
		Debug3d.flush();
		
	}

	
	protected String percentToString(double percent) {
		double round_percent = Math.round(percent*100)/100.0;
		String res = ""+round_percent;
		while (res.indexOf(".")>=res.length()-2) {
			res = res+"0";
		}
		return res+"%";
	}
	
	
	private void addBackgroundToColor(int x, int y, Color4f color) {		

		if (color.w==0) {
			float bg = (((x/10 % 2) == 0) ^ ((y/10 % 2) == 0)) ? 0.75f : 0.7f;
			color.x = bg;
			color.y = bg;
			color.z = bg;		
		}
		
	}
	
	
	private int getScaledIntColor(Color4f color) {
		
		Color4f scaled_color = new Color4f(color);
		
		// scale color
		if (color.w>0) {
			scaled_color.x *= m_brightnessScaleFactor;
			scaled_color.y *= m_brightnessScaleFactor;
			scaled_color.z *= m_brightnessScaleFactor;
		}
		
		// clamp colors
		scaled_color.clampMax(1.0f);
		scaled_color.clampMin(0.0f);
		
		// convert to int
		Color rgba_color = scaled_color.get();
		return (rgba_color.getAlpha() << 24) + 
			   (rgba_color.getRed() << 16) + 
			   (rgba_color.getGreen() << 8) + 
			   (rgba_color.getBlue());
		
	}
	
	
	public void printStatistics() {
		long preparing_ms   = Quality.getQualityKit().
				getTimer(PREPARING_TIMER).getLastMillis();
		long calculation_ms = Quality.getQualityKit().
				getTimer(CALCULATION_TIMER).getLastMillis();
		
		System.out.println();
		System.out.println("RAYTRACER");
		System.out.println("----------------------------------------");
		System.out.println("Size:");
		System.out.println("  dimension: "+m_lastWidth+" x "+m_lastHeight+" pixel");
		System.out.println("Objects:");
		System.out.println("  objects: "+m_objectsCount);
		System.out.println("  lights:  "+m_lightsCount);
		System.out.println("Time:");
		System.out.println("  preparing:   "+preparing_ms+" ms");
		System.out.println("  calculation: "+calculation_ms+" ms");
		System.out.println("  overall:     "+(preparing_ms+calculation_ms)+" ms");
	}
	
	
	public static RTLight[] getLights(RTScene scene) {
		RTLight[] lights = new RTLight[scene.getLightsCount()];
		GetLightsVisitor visitor = new GetLightsVisitor(lights);
		scene.traversSceneLights(visitor);
		return lights;
	}
	
	
	private static class GetLightsVisitor implements RTSceneVisitor {

		private RTLight[] m_lights       = null;
		private int       m_lightsOffset = 0;
		
		public GetLightsVisitor(RTLight[] lights) {
			initialize(lights);
		}
		
		public void initialize(RTLight[] lights) {
			m_lights = lights;
			m_lightsOffset = 0;
		}
		
		public void visitObject(RTObject object) {
			if (!(object instanceof RTLight)) { return; }
			
			m_lights[m_lightsOffset++] = (RTLight)object;
		}
		
	}
	
	
	public static RTObject[] getShadeables(RTScene scene) {
		RTObject[] objects = new RTObject[scene.getShadeablesCount()];
		GetShadeablesVisitor visitor = new GetShadeablesVisitor(objects);
		scene.traversSceneObjects(visitor);
		
		return objects;
	}
	
	
	private static class GetShadeablesVisitor implements RTSceneVisitor {

		private RTObject[] m_objects       = null;
		private int        m_objectsOffset = 0;
		
		public GetShadeablesVisitor(RTObject[] objects) {
			initialize(objects);
		}
		
		public void initialize(RTObject[] objects) {
			m_objects = objects;
			m_objectsOffset = 0;
		}
		
		public void visitObject(RTObject object) {
			if (!object.isShadeable()) { return; }
			
			m_objects[m_objectsOffset] = object;
			m_objectsOffset++;
		}
		
	}
	
	
	public static float getT(Ray ray,Point3f point) {
		if (ray.getDirection().x!=0.0) {
			return (point.x-ray.getOrigin().x)/ray.getDirection().x;
		} else if (ray.getDirection().y!=0.0) {
			return (point.y-ray.getOrigin().y)/ray.getDirection().y;
		} else if (ray.getDirection().z!=0.0) {
			return (point.z-ray.getOrigin().z)/ray.getDirection().z;
		}
		return Float.NaN;
	}
	
	
	private void sleepALittle(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}	
	
	
	private void validateColor(Color4f color) {
		if ((color.x!=color.x)||(color.y!=color.y)||(color.z!=color.z)||
			(color.w!=color.w)) {
			System.err.println("  invalid color value:"+color);
		}
	}


}
