package de.grogra.ray;

import javax.vecmath.Color3f;

import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.util.Ray;


/**
 * This is an object that cannot be treated as correct pysical and 
 * gemetric object. It is more a model. 
 * The only fake object that is implemented yet is a sky object. A sky object 
 * is only a theoretical object for rendering the sky. It is a surrounding 
 * textured sphere with an infinite radius.
 * 
 * @author Micha
 *
 */
public interface RTFakeObject extends RTObject {

	public void getColor(Ray ray, IntersectionDescription desc, Color3f color);
	
}
