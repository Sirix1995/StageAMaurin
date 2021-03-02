package de.grogra.ray;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.light.ShadowProcessor;
import de.grogra.ray.shader.ShadingEnvironment;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;


public interface RTLight extends RTObject {

	public Point3f getGlobalOrigin();
	
	public boolean isShadowless();
	//public int addOrigins(Vector3d[] origins, int offset);
	public int getLightRays(Ray view, IntersectionDescription desc, 
			ShadowProcessor shadowProcessor, RayList rays);
	
	public void generateRandomOrigins (RayList out, int seed);
	public void generateRandomRays (Vector3f out, RayList rays,	boolean adjoint, int seed);
	
	public float computeBSDF (ShadingEnvironment env,
			   Vector3f in, Vector3f out, boolean adjoint,
			   Color3f bsdf);
}
