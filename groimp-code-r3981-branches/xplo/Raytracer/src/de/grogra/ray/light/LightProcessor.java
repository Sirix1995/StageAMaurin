package de.grogra.ray.light;

import de.grogra.ray.RTScene;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.intersection.IntersectionProcessor;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;


/**
 * Interface that encapsulates the specific light calculation. It has 
 * the return all light/shadow ray that directly or indirectly illuminates 
 * a specific intersection point. 
 * 
 * @author Micha
 *
 */
public interface LightProcessor {

	public void prepareLightProcessor(RTScene scene,
			IntersectionProcessor intersectionProcessor); 
	
	/**
	 * Adds lights/shadow rays that illuminalte the specified intersection 
	 * point.
	 * 
	 * @param view 
	 * @param desc description object that specifies the intersection point
	 * @param rays all determined rays are added to this object
	 * @return number of rays that are added to the rays object
	 */
	public int getLightRays(Ray view,IntersectionDescription desc, RayList rays);
	
}
