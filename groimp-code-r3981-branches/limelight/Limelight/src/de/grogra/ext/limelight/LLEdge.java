package de.grogra.ext.limelight;


import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import de.grogra.ext.limelight.objects.BSDF;
import de.grogra.ext.limelight.objects.LLLight;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.Intersection;

public class LLEdge {

	//the point of the vertex
	public Point3f p=new Point3f();
	
	//unweighted contribution: alpha_i=f(z_(i-1)->z_(i-2)->z_(i-3))/p(z_(i-2)->z_(i-1))*alpha_(i-1)
	public Spectrum3f alpha = new Spectrum3f();
	
	//the BSDF at the vertex
	public BSDF bsdf;
	
	//the normal vector at the vertex
	public Vector3f n=new Vector3f();
	
	//the coordinate transformations at the vertex
	public Matrix3f edgeToWorld=new Matrix3f();
	public Matrix3f worldToEdge=new Matrix3f();
	
	//the incoming vector at the vertex
	public Vector3f wIn=new Vector3f();
	
	//the light if vertex is area light, null if not
	public LLLight light;
	
	//the geometry term g=cos(theta_i)*cos(theta_o)/||x-x'||^2
	public float g;
	
	//incoming probability density
	public float pIn;
	
	//outgoing probability density
	public float pOut;

	public LLEdge() {
	};
	
	//constructs an Edge with geometric information from an intersection
	public LLEdge(Intersection isect) {
		p.set(isect.getPoint());
		n.set(isect.getNormal());
		Math2.getOrthogonalBasis(n, edgeToWorld, true);
		worldToEdge.invert(edgeToWorld);
	
	};
	

}
