package de.grogra.ext.limelight.objects;

import javax.vecmath.*;

import de.grogra.ext.limelight.MersenneTwister;
import de.grogra.ext.limelight.Ray;
import de.grogra.ray.physics.Spectrum3f;


public abstract class LLLight implements BSDF{
	private Matrix4f transf;
	private float totalPower;	
	private BSDF bsdf;
	
	//propability for picking a point of this light in a scene, needed for bidi-PT
	private float prop;


	public Matrix4f getTransf() {
		return transf;
	}

	public void setTransformation(Matrix4f transf) {
		this.transf = transf;
	}

	
	public float getPower() {
		return totalPower;
	}
	
	public abstract float getIntensity();
	
	public void setPower(float p) {
		this.totalPower = p;
	}

	public BSDF getBSDF() {
		return bsdf;
	}

	public void setBSDF(BSDF bsdf) {
		this.bsdf = bsdf;
	}

	public abstract Spectrum3f getRadiance(Point3f p);
	
	public abstract float sampleDirection(Vector3f d, MersenneTwister rand);
	
	public abstract float samplePoint(Point3f p,MersenneTwister rand);
	
	public abstract LLLight clone();
	
	public Spectrum3f getDiffuseColor() {
		return bsdf.getDiffuseColor();
	}
	
	public abstract void f(Vector3f wOut, Vector3f wIn, Spectrum3f f); 

	public void setProp(float prop) {
		this.prop = prop;
	}

	public float getProp() {
		return prop;
	}
	

}
