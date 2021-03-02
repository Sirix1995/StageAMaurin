package de.grogra.imp3d.ray;

import de.grogra.imp3d.shading.Interior;
import de.grogra.ray.shader.RTMedium;

public class GroIMPMedium implements RTMedium {

	private Interior m_interior = null;
	
	
	public GroIMPMedium(Interior interior) {
		setInterior(interior);
	}
	
	
	public void setInterior(Interior interior) {
		m_interior = interior;
	}
	
	
	public float getIndexOfRefraction() {
		return m_interior.getIndexOfRefraction(null);
	}

}
