package de.grogra.ray.intersection;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import de.grogra.ray.util.Ray;

public class BoundingSphere implements BoundingVolume {

	private Intersections.SphereIntersectionInput m_sphereInput = 
		new Intersections.SphereIntersectionInput();
	private Intersections.SphereIntersectionLocalVariables m_sphereLocalVariables =
		new Intersections.SphereIntersectionLocalVariables();

	private final Vector3f m_minValues = new Vector3f();
	private final Vector3f m_maxValues = new Vector3f();
	
	
	public BoundingSphere(float radius, Point3f translation) {	
		m_sphereInput.squareRadius = radius*radius;
		m_sphereInput.invers_transformation.setIdentity();
		m_sphereInput.invers_transformation.m03 = -translation.x;
		m_sphereInput.invers_transformation.m13 = -translation.y;
		m_sphereInput.invers_transformation.m23 = -translation.z;
		m_minValues.x = translation.x-radius;
		m_minValues.y = translation.y-radius;
		m_minValues.z = translation.z-radius;
		m_maxValues.x = translation.x+radius;
		m_maxValues.y = translation.y+radius;
		m_maxValues.z = translation.z+radius;
	}
	
	
	public float getMinX() { return m_minValues.x;	}
	public float getMinY() { return m_minValues.y;	}
	public float getMinZ() { return m_minValues.z;	}
	
	public float getMaxX() { return m_maxValues.x;	}
	public float getMaxY() { return m_maxValues.y;	}
	public float getMaxZ() { return m_maxValues.z;	}

	
	public boolean hasIntersection(Ray ray) {
		m_sphereInput.ray.setRay(ray);
		return Intersections.getSphere_hasIntersection(
				m_sphereInput,m_sphereLocalVariables);
	}
	
	
	public boolean isInsideBox(Vector3f minValues, Vector3f maxValues) {
		return true;
	}

}
