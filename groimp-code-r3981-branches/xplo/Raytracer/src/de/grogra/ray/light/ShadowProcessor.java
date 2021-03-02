package de.grogra.ray.light;

import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.util.Ray;


/**
 * A ShadowProcessor have to decide wether a possibly light/shadow ray
 * realy illuminates the specified intersection point. 
 * 
 * This interface makes shadow calculations exchangeable. 
 * 
 * @author Micha
 *
 */
public interface ShadowProcessor {
	
	/**
	 * Calculate to shadowed color of the light ray to the specified point.
	 * @param ray
	 * @return True if the the light ray is totally shadowed.
	 */
	public boolean shadowRay(Ray light,float length,Ray view,IntersectionDescription desc);
	
}
