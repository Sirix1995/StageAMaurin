package de.grogra.ray.shader;

import javax.vecmath.Color3f;
import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;





public interface RTShader {
	
	public float LAMBERTIAN_VARIANCE = (float) ((Math.PI * Math.PI - 4) / 8);
	
//	public void getShadingColor(Input in, Color4f color);
	public void getShadingColor(ShadingEnvironment env, Color3f color);
//	public void computeMaxRays(ShadingEnvironment in, 
//			Ray reflectedRay, Color3f reflectedWeight, Vector3f reflectedVariance,
//			Ray refractedRay, Color3f refractedWeight, Vector3f refractedVariance);
	public void computeMaxRays(ShadingEnvironment env, 
			Ray reflectedRay, Vector3f reflectedVariance,
			Ray refractedRay, Vector3f refractedVariance);
	
	public void generateRandomRay(ShadingEnvironment env,
			Ray randomRay);
	public void generateRandomRays(ShadingEnvironment env,
			RayList randomRays);
	
	public boolean isTransparent(TransparencyInput in);
	
	public boolean isTransparent();
	
	public int getShaderFlags();
	
	public float computeBSDF (ShadingEnvironment env,
			   Vector3f in, Vector3f out, boolean adjoint,
			   Color3f bsdf);
	
	public float getshadingColorByComputeBSDF(ShadingEnvironment env,
			   Vector3f in, boolean adjoint,
			   Color3f bsdf);
	
	public 	void generateRandomRays (ShadingEnvironment env, Vector3f out, RayList rays,
			 boolean adjoint, int seed);
	
	
	public class TransparencyInput {
		public final Point3f localPoint = new Point3f ();
		public final Point3f point = new Point3f ();
		public final Point2f uv = new Point2f ();
	}
	
	
	public class TransparencyInput_Double {
		public final Point3d localPoint = new Point3d ();
		public final Point3d point = new Point3d ();
		public final Point2d uv = new Point2d ();
	}
	
	
	
}
