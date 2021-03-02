package de.grogra.ray.intersection;

import java.util.Collection;

import javax.vecmath.Color4f;
import javax.vecmath.Point3d;

import de.grogra.ray.RTObject;
import de.grogra.ray.RTScene;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;


/**
 * An implementation of this interface encapsulates the algorithm of 
 * intersection determination of a single ray in a 3D-scene. 
 * 
 * Mostly additional reference data structures are used to accelarate this 
 * calculation. 
 *  
 * @author Micha
 *
 */
public interface IntersectionProcessor {

	public void prepareProcessing(RTScene sceneGraph);
	
	public void cleanupProcessing();
	
	public boolean getFirstIntersectionDescription(Ray ray, RayContext context, 
			IntersectionDescription desc);
	
	public float getFirstIntersectionT(Ray ray, RayContext context);
	
}
