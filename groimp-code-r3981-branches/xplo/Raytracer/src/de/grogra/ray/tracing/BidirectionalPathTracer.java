package de.grogra.ray.tracing;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;


import de.grogra.ray.RTFakeObject;
import de.grogra.ray.RTLight;
import de.grogra.ray.RTObject;
import de.grogra.ray.RTScene;
import de.grogra.ray.RTSceneVisitor;
import de.grogra.ray.Raytracer;
import de.grogra.ray.intersection.BoundingVolume;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.intersection.IntersectionProcessor;
import de.grogra.ray.intersection.Intersections;
import de.grogra.ray.light.DefaultLightProcessor;
import de.grogra.ray.light.LightProcessor;
import de.grogra.ray.light.NoShadows;
import de.grogra.ray.light.ShadowProcessor;
import de.grogra.ray.light.Shadows;
import de.grogra.ray.memory.MemoryPool;
import de.grogra.ray.shader.RTShader;
import de.grogra.ray.shader.ShadingEnvironment;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.tracing.LightModelProcessor;
import de.grogra.vecmath.Math2;



public class BidirectionalPathTracer implements RayProcessor  {
	
	private  PathingStrategies myStrategy; 
	
	
	
	
	

	
	

	private final static int INTERSECTION_EVALUATION_PARAMS = 
		Intersections.EVALUATE_POINT | 
		Intersections.EVALUATE_NORMAL |
		Intersections.EVALUATE_SHADER |
		Intersections.EVALUATE_TANGET_VECTORS |
		Intersections.EVALUATE_UV_COORDINATES;

	
	public BidirectionalPathTracer(){
		super();
		myStrategy =  new HagensBiDiStrategie1();


	}
	
	
	public void prepareRayProcessor(RTScene scene,IntersectionProcessor intersectionProcessor) {
		myStrategy.prepareRayProcessor(scene, intersectionProcessor);
		

		

	}

	
	

	
	
	public void getColorFromRay(Ray ray, Color4f color) {

		myStrategy.getColorFromRay(ray, color);

	}
	
	

	
	
	
	
	
	
	
	
	
	
	



	

	public boolean hasFixedLightProcessor() {
		return true;
	}

	public void setLightProcessor(LightProcessor proc) {
		//lightPathLightModel = model;

		myStrategy.setLightProcessor(proc);

	}

	public LightProcessor getLightProcessor() {
		return myStrategy.getLightProcessor();
	}

	public void setIntersectionProcessor(IntersectionProcessor processor) {
		myStrategy.setIntersectionProcessor(processor);
	}
	
	
	
	
	public interface PathingStrategies {
		
		

		public void getColorFromRay(Ray ray, Color4f color);
		public void prepareRayProcessor(RTScene scene, IntersectionProcessor intersectionProcessor);
		public void setLightProcessor(LightProcessor proc);


		public LightProcessor getLightProcessor();

		public void setIntersectionProcessor(IntersectionProcessor processor);
		
		public void setRecursionDepth(int value);
		

		public int getRecursionDepth();

	}


	public void setRecursionDepth(int value) {
		myStrategy.setRecursionDepth(value);

		
	}


	public int getRecursionDepth() {
		return myStrategy.getRecursionDepth();
	}




}
