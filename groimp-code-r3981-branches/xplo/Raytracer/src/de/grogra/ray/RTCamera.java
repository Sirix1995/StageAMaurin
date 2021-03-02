package de.grogra.ray;

import de.grogra.ray.util.Ray;


/**
 * This interface represents an interface of a scene camera. 
 * 
 * @author Micha
 *
 */
public interface RTCamera {

	/**
	 * This method generates a ray that has its origin in the cameras origin 
	 * and will go through the given pixel in the image plane.
	 * 
	 * @param x Represents the relative x postion of the top left corner of
	 *          the pixel. It ranges from -1 to +1.
	 * @param y Represents the relative y postion of the top left corner of
	 *          the pixel. It ranges from -1 to +1.
	 * @param ray The generated ray is stored in this parameter.
	 */
	public void getRayFromCoordinates(double x, double y, Ray ray);
	
}
