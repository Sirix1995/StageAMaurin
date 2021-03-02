package de.grogra.ray.debug3d;

import javax.vecmath.Tuple3f;

import de.grogra.ray.RTCamera;
import de.grogra.ray.RTScene;
import de.grogra.ray.util.Ray;


public class Debug3d {

//	private final static boolean DEBUG_3D = true;
	private static boolean m_isDebug3DEnabled = false;
		
	private static Debug3dImpl m_debugImpl = new Debug3dVRMLImpl();
	
	
	private static Debug3dImpl getImplementation() { return m_debugImpl; } 
	
	
	public static void enableDebug3D(boolean value) { m_isDebug3DEnabled = value; }
	
	
	public static void logCamera(RTCamera camera) {
		if (m_isDebug3DEnabled) {
			getImplementation().logCamera(camera);
		}
	}
	
	
	public static void logScene(RTScene scene) {
		if (m_isDebug3DEnabled) {
			getImplementation().logScene(scene);
		}
	}
	
	
	public static void logInfiniteRay(Ray ray,float weight) {
		if (m_isDebug3DEnabled) {
			getImplementation().logInfiniteRay(ray,weight);
		}
	}
	
	
	public static void logFiniteRay(Ray ray,float scale,float weight) {
		if (m_isDebug3DEnabled) {
			getImplementation().logFiniteRay(ray,scale,weight);
		}
	}
	
	
	public static void logNormal(Tuple3f point,Tuple3f direction) {
		if (m_isDebug3DEnabled) {
			getImplementation().logNormal(point,direction);
		}
	}
	
	
	public static void logDirectLightRay(Ray ray) {
		if (m_isDebug3DEnabled) {
			getImplementation().logDirectLightRay(ray);
		}
	}
	
	
	public static void clear() {
		if (m_isDebug3DEnabled) {
			getImplementation().clear();
		}
	}
	
	
	public static void flush() {
		if (m_isDebug3DEnabled) {
			getImplementation().flush();
		}
	}
	
}
