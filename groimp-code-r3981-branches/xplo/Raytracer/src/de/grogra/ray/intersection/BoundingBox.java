package de.grogra.ray.intersection;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import de.grogra.ray.util.Ray;

public class BoundingBox implements BoundingVolume {

	private Intersections.BoxIntersectionInput m_boxInput = 
		new Intersections.BoxIntersectionInput();
	private Intersections.BoxIntersectionLocalVariables m_boxLocalVariables =
		new Intersections.BoxIntersectionLocalVariables();
	
	
	public BoundingBox(Vector3f minValues, Vector3f maxValues) {
		m_boxInput.minValues.set(minValues);
		m_boxInput.maxValues.set(maxValues);
	}
	
	
	public float getMinX() { return m_boxInput.minValues.x; }
	public float getMinY() { return m_boxInput.minValues.y; }
	public float getMinZ() { return m_boxInput.minValues.z; }

	public float getMaxX() { return m_boxInput.maxValues.x; }
	public float getMaxY() { return m_boxInput.maxValues.y; }
	public float getMaxZ() { return m_boxInput.maxValues.z; }
	
	
	public boolean hasIntersection(Ray ray) {
		m_boxInput.ray.setRay(ray);
		return Intersections.getBox_hasIntersection(
				m_boxInput,m_boxLocalVariables);
	}
	
	
	public boolean isInsideBox(Vector3f minValues, Vector3f maxValues) {
//		Point3f[] corners = new Point3f[8];
//		corners[0] = new Point3f();
//		corners[0].x = m_boxInput.maxValues.x;
//		corners[0].y = m_boxInput.maxValues.y;
//		corners[0].z = m_boxInput.maxValues.z;
//		corners[1] = new Point3f();
//		corners[1].x = m_boxInput.minValues.x;
//		corners[1].y = m_boxInput.maxValues.y;
//		corners[1].z = m_boxInput.maxValues.z;
//		corners[2] = new Point3f();
//		corners[2].x = m_boxInput.minValues.x;
//		corners[2].y = m_boxInput.minValues.y;
//		corners[2].z = m_boxInput.maxValues.z;
//		corners[3] = new Point3f();
//		corners[3].x = m_boxInput.maxValues.x;
//		corners[3].y = m_boxInput.minValues.y;
//		corners[3].z = m_boxInput.maxValues.z;
//		corners[4] = new Point3f();
//		corners[4].x = m_boxInput.maxValues.x;
//		corners[4].y = m_boxInput.maxValues.y;
//		corners[4].z = m_boxInput.minValues.z;
//		corners[5] = new Point3f();
//		corners[5].x = m_boxInput.minValues.x;
//		corners[5].y = m_boxInput.maxValues.y;
//		corners[5].z = m_boxInput.minValues.z;
//		corners[6] = new Point3f();
//		corners[6].x = m_boxInput.minValues.x;
//		corners[6].y = m_boxInput.minValues.y;
//		corners[6].z = m_boxInput.minValues.z;
//		corners[7] = new Point3f();
//		corners[7].x = m_boxInput.maxValues.x;
//		corners[7].y = m_boxInput.minValues.y;
//		corners[7].z = m_boxInput.minValues.z;
//		for (int i=0;i<8;i++) {
//			if (Intersections.isPointInsideBox(corners[i],minValues,maxValues)) {
//				return true;
//			}
//		}
//		return false;
		if ((m_boxInput.minValues.x>maxValues.x) || (m_boxInput.maxValues.x<minValues.x)) { return false; }
		if ((m_boxInput.minValues.y>maxValues.y) || (m_boxInput.maxValues.y<minValues.y)) { return false; }
		if ((m_boxInput.minValues.z>maxValues.z) || (m_boxInput.maxValues.z<minValues.z)) { return false; }
		return true;
	}
	
	
	

}
