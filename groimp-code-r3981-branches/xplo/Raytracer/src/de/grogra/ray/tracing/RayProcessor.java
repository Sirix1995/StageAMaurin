package de.grogra.ray.tracing;

import javax.vecmath.Color4f;

import de.grogra.ray.RTScene;
import de.grogra.ray.intersection.IntersectionProcessor;
import de.grogra.ray.light.LightProcessor;
import de.grogra.ray.util.Ray;


/**
 * An implementation of this interface encapsulates the raytracing algorithm.
 * This class uses an IntersectionProcessor for determining intersection
 * information about a single ray in the scene. Therefore it must not know the 
 * scene at all. It only interpretes the intersection information and sends out
 * and traces new (reflected,fractured,..) rays.
 * 
 * @author Micha
 *
 */
public interface RayProcessor {

	public boolean hasFixedLightProcessor();
	public void setLightProcessor(LightProcessor lightProcessor);
	public LightProcessor getLightProcessor();
	public void setRecursionDepth(int value);
	public int getRecursionDepth();
	
	
	/**
	 * With this method the processor is initialized with a 3d scene
	 * and an intersection processor it will use.
	 * 
	 * @param scene
	 * @param intersectionProcessor
	 */
	public void prepareRayProcessor(RTScene scene,IntersectionProcessor intersectionProcessor);
	
	
	/**
	 * The main methode of a ray processor. This methode has to calculate 
	 * a color (or luminance of the red, gren and blue ) for each ray.
	 * 
	 * @param ray input - calculate for this ray
	 * @param color output - the calculated color
	 */
	public void getColorFromRay(Ray ray, Color4f color);
	
}
