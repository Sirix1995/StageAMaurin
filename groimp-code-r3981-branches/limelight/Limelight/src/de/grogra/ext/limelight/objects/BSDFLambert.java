package de.grogra.ext.limelight.objects;

import javax.vecmath.Vector3f;
import de.grogra.ext.limelight.MersenneTwister;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.vecmath.Math2;

public class BSDFLambert implements BSDF {

	private Spectrum3f rho;

	public BSDFLambert(BSDFLambert b) {
		this(b.rho);

	}

	public BSDFLambert(Spectrum3f r) {
		rho = r;

	}

	public BSDFLambert clone() {
		return new BSDFLambert(rho);
	}

	public void f(Vector3f wOut, Vector3f wIn, Spectrum3f f) {

		f.set((Spectrum)rho);
		f.scale(Math2.M_1_PI);
		if (wOut.z * wIn.z >= 0)
			f.setZero();
	}

	public float sampleDirection(Vector3f wOut, Vector3f wIn,
			MersenneTwister rand, Spectrum3f factor) {

		// cosine distributed

		double v = rand.nextDouble();
		double phi = Math2.M_2PI * rand.nextDouble();
		double cosTheta = Math.sqrt(v);
		double sinTheta = Math.sqrt(1.0 - v);
		wIn.x = (float) (sinTheta * Math.cos(phi));
		wIn.y = (float) (sinTheta * Math.sin(phi));
		wIn.z = (float) cosTheta;
		if (wOut.z > 0)
			wIn.z = -wIn.z;
		
		factor.set((Spectrum) rho); 
		factor.scale(Math2.M_1_PI);

		return Math2.M_1_PI;

		/*
		 * double theta=Math.acos(Math.sqrt(1.f-rand.nextDouble())); double
		 * phi=Math2.M_2PI*rand.nextDouble();
		 * 
		 * wIn.x = (float)(Math.sin(theta) * Math.cos(phi)); wIn.y =
		 * (float)(Math.sin(theta) * Math.sin(phi)); wIn.z =
		 * (float)Math.cos(theta);
		 * 
		 * factor.set((Spectrum) rho); factor.scale(Math2.M_1_PI);
		 * 
		 * if(wout.z<0) wIn.negate();
		 * 
		 * return Math2.M_1_PI;
		 */
	}
	
	public float pdf(Vector3f wOut, Vector3f wIn) {
		if (wOut.z * wIn.z <= 0)
			return Math2.M_1_PI;
		else
			return 0.f;
	}

	public Spectrum3f getDiffuseColor() {
		return new Spectrum3f(rho.x, rho.y, rho.z);
	}

	public boolean isSpecular() {
		
		return false;
	}


}
