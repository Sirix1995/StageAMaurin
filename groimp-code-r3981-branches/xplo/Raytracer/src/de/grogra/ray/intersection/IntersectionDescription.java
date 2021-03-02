package de.grogra.ray.intersection;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import de.grogra.ray.RTObject;
import de.grogra.ray.shader.RTMedium;
import de.grogra.ray.shader.RTShader;
import de.grogra.ray.util.Ray;


public class IntersectionDescription {
	
	public int intersectionIndex;
	
	//private Ray      m_ray               = null;
	private int      m_intersectionCount = 0;
		
	private float   m_t             = 0.0f;	
	private final Point3f  m_point         = new Point3f();
	private final Point3f  m_localPoint    = new Point3f();
	private final Vector3f m_normal        = new Vector3f();
	private final Vector3f m_uTangente     = new Vector3f();
	private final Vector3f m_vTangente     = new Vector3f();
	private final Point2f  m_uvCoordinates = new Point2f();
	private RTShader m_shader        = null;
	private RTMedium m_medium        = null;
	private RTObject m_object        = null;
	
	
	public IntersectionDescription() {
		//m_ray = new Ray();
	}
	
	
	public IntersectionDescription(Ray ray, int count) {
		//m_ray = ray;
		m_intersectionCount = count;
	}
	
	
	public IntersectionDescription(Ray ray, float t) {
		//m_ray = ray;
		m_t = t;
		//m_point = new Point3d();
		//m_point.scaleAdd(t,m_ray.getDirection(),ray.getOrigin());
	}
	
	
	public IntersectionDescription(Ray ray, float t, Point3f point) {
		//m_ray = ray;
		m_t = t;
		getPoint().set(point);
	}
	
	public IntersectionDescription(IntersectionDescription desc) {
		m_intersectionCount = desc.getIntersectionCount();	
		m_t             = desc.getT();	
		m_point.set(desc.getPoint());
		m_localPoint.set(desc.getLocalPoint());
		m_normal.set(desc.getNormal());
		m_uTangente.set(desc.getTangenteU());
		m_vTangente.set(desc.getTangenteV());
		m_uvCoordinates.set(desc.getUVCoordinate());
//		m_shader        = desc.getShader();
//		m_medium        = desc.getMedium();
		m_object        = desc.getRTObject();
	}
	
	
	//public Ray getRay() { return m_ray; }
	public int getIntersectionCount() { return m_intersectionCount; }
	public void setIntersectionCount(int value) { m_intersectionCount = value; }
	
	public float getT() { return m_t; }
	public Point3f getPoint() { return m_point; }
	public Point3f getLocalPoint() { return m_localPoint; }
	public Vector3f getNormal() { return m_normal; }
	public Vector3f getTangenteU() { return m_uTangente; }
	public Vector3f getTangenteV() { return m_vTangente; }
	public Point2f getUVCoordinate() { return m_uvCoordinates; }
//	public RTShader getShader() { return m_shader; }
//	public RTMedium getMedium() { return m_medium; }
	public RTObject getRTObject() { return m_object; }
	
	public void setPoint(Point3f point, float t) { 
		if (point!=null) {
			getPoint().set(point);
		}
		m_t = t;
	}
	
	public void setLocalPoint(Point3f point) {
		getLocalPoint().set(point);
		//m_localPoint = point;
	}
	
	public void setNormal(Vector3f normal) {
		getNormal().set(normal);
		//m_normal = normal;
	}
	
	
//	public void setShader(RTShader shader) { 
//		m_shader = shader; 
//	}
//	
//	
//	public void setMedium(RTMedium medium) { 
//		m_medium = medium; 
//	}
	
	
	public void setRTObject(RTObject object) {
		m_object = object;
	}
	
	//public boolean isPointSet() { return m_point!=null; }
	//...
	
}
