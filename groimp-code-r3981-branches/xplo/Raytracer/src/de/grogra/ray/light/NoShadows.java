package de.grogra.ray.light;

import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.util.Ray;


public class NoShadows implements ShadowProcessor {

	private float m_scalar;


	public boolean shadowRay(Ray light,float length,Ray view,IntersectionDescription desc) {
		if (desc.getRTObject().isSolid()) {
			// ignore light rays that are on the back side of the object
			m_scalar = 
				desc.getNormal().x*light.getDirection().x+
				desc.getNormal().y*light.getDirection().y+
				desc.getNormal().z*light.getDirection().z;
			if (m_scalar>0.0f) {
				return true;
			}
		} else {
			// ignore light rays that are behind the view ray
			m_scalar = 
				view.getDirection().x*light.getDirection().x+
				view.getDirection().y*light.getDirection().y+
				view.getDirection().z*light.getDirection().z;
			if (m_scalar<0.0f) {
				return true;
			}
		}
		return false;
	}

}
