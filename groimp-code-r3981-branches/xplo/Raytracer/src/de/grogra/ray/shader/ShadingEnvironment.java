package de.grogra.ray.shader;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.ray.util.RayList;

/**
 * This class serves as input to scattering calculations.
 * The fields together
 * constitute the local environment at a surface or light point,
 * including geometrical and optical properties.
 */
public final class ShadingEnvironment extends Environment
{
	
	
	
	
	
	public final RayList rays = new RayList(10);
	public final Vector3f view = new Vector3f ();
	public boolean photonDirection;
	
	
	
	public ShadingEnvironment(){
		super (null, new Spectrum3f(), STANDARD_RAY_TRACER);
		rays.clear();
	}
	
	
	public void print() {
		System.out.println("ShadingEnvironment "+this);
	}
}