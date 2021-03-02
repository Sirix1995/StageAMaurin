package de.grogra.imp3d.ray;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.grogra.imp3d.Camera;
import de.grogra.ray.RTCamera;
import de.grogra.ray.util.Ray;


public class GroIMPCamera implements RTCamera {

	private Camera  m_camera;
	private double  m_ratio;
	private Point3d  m_origin    = new Point3d();
	private Vector3d m_direction = new Vector3d();
	
		
	public GroIMPCamera(Camera camera, double ration) {
		m_camera = camera;
		m_ratio = ration;
	}
	
	
	public void getRayFromCoordinates(double x, double y, Ray ray) {
		
		//Point3f origin = MemoryPool.getPool().newPoint3f();//(Point3d)MemoryPool.getPool().newInstance(Point3d.class);
		//Vector3f direction = MemoryPool.getPool().newVector3f();//(Vector3d)MemoryPool.getPool().newInstance(Vector3d.class);
		
		m_camera.getRay ((float)x, (float)(-1.0*y/m_ratio), m_origin, m_direction);
		
		
		//Ray ray = new Ray(m_origin,m_direction,1.0f);
		ray.getOrigin().set(m_origin);
		ray.getDirection().set(m_direction);
		ray.setImportance(1.0f);
//		ray.nextId();
		
		// clean up
		//MemoryPool.getPool().freeInstance(direction);
		//MemoryPool.getPool().freeInstance(origin);
		//MemoryPool.getPool().freeVector3d(direction);
		//MemoryPool.getPool().freePoint3d(origin);
		
		//return ray;
	}

}
