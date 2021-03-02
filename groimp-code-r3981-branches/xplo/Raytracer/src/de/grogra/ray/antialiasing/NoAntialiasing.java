package de.grogra.ray.antialiasing;

import javax.vecmath.Color4f;

import de.grogra.ray.RTCamera;
import de.grogra.ray.tracing.RayProcessor;
import de.grogra.ray.util.Ray;


/**
 * Although this class is implemented as antialiasing method it will not
 * perform any aliasing. Contrary it is used if no antialiasing is needed.
 * 
 * The method getColorFromFrustum will only return the color of a single ray
 * that is in the middle of the pixel frustum.
 * 
 * @author Micha
 *
 */
public class NoAntialiasing implements Antialiasing {

	private Ray          m_ray = new Ray();
	private RTCamera     m_camera;
	private RayProcessor m_processor;


	public void initialize(RTCamera camera, RayProcessor processor) {
		m_camera = camera;
		m_processor = processor;
	}
	
	
	public void getColorFromFrustum(double x, double y, double width,
			double height, Color4f color) {

		m_camera.getRayFromCoordinates(x+width*0.5,y+height*0.5,m_ray);
		m_processor.getColorFromRay(m_ray,color);
	}

}