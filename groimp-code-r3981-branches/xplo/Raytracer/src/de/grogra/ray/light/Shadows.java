package de.grogra.ray.light;

import de.grogra.ray.debug3d.Debug3d;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.intersection.IntersectionProcessor;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;


public class Shadows implements ShadowProcessor {

	private IntersectionProcessor m_processor = null; 
	
	private float m_nearestT;
	private float m_scalar1;
	private float m_scalar2;
	
	private final RayContext m_rayContext = new RayContext();

	
	public Shadows(IntersectionProcessor processor) {
		setIntersectionProcessor(processor);
	}

	
	public void setIntersectionProcessor(IntersectionProcessor processor) {
		if (processor==null) {
			System.err.println("Shadows: intersection processor should not be null");
		} else {
			m_processor = processor;
		}
	}

	
	public boolean shadowRay(Ray light,float length,Ray view,IntersectionDescription desc) {
		if (desc.getRTObject().isSolid()) {
			// clip light rays that are on the back side of the object
			m_scalar1 = 
				desc.getNormal().x*light.getDirection().x+
				desc.getNormal().y*light.getDirection().y+
				desc.getNormal().z*light.getDirection().z;
			if (m_scalar1>0.0f) {
				return true;
			}
		} else {
			// clip if light ray and view ray are on opposite sides
			m_scalar1 = 
				desc.getNormal().x*light.getDirection().x+
				desc.getNormal().y*light.getDirection().y+
				desc.getNormal().z*light.getDirection().z;
			m_scalar2 = 
				desc.getNormal().x*view.getDirection().x+
				desc.getNormal().y*view.getDirection().y+
				desc.getNormal().z*view.getDirection().z;
			if ((m_scalar1*m_scalar2)<0.0f) {
				Debug3d.logInfiniteRay(view,1.0f);
				Debug3d.logInfiniteRay(light,1.0f);
				return true;
			}
		}
				
		m_rayContext.excludeObject = desc.getRTObject();
		m_nearestT = m_processor.getFirstIntersectionT(light,m_rayContext);
		if ((m_nearestT)<length) {
			return true;
		}
		
		return false;
	}

}
