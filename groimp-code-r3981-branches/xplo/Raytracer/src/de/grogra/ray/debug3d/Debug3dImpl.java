package de.grogra.ray.debug3d;

import javax.vecmath.Tuple3f;

import de.grogra.ray.RTCamera;
import de.grogra.ray.RTScene;
import de.grogra.ray.util.Ray;


public interface Debug3dImpl {

	public void logCamera(RTCamera camera);
	public void logScene(RTScene scene);
	public void logInfiniteRay(Ray ray,float weight);
	public void logFiniteRay(Ray ray,float scale,float weight);
	public void logNormal(Tuple3f point,Tuple3f direction);
	public void logDirectLightRay(Ray ray);
	public void clear();
	public void flush();
	
}
