package de.grogra.ext.limelight.objects;

import javax.vecmath.Vector3f;

import de.grogra.ext.limelight.MersenneTwister;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3f;

public class BSDFSpecular implements BSDF {

	private Spectrum3f ks = new Spectrum3f();
	
	public BSDFSpecular(Spectrum3f k){
		ks=k;
	

	}
	
	public void f(Vector3f wOut, Vector3f wIn, Spectrum3f f) {
		f.setZero();
	}

	public Spectrum3f getDiffuseColor() {
		return new Spectrum3f();
	}

	public float pdf(Vector3f wOut, Vector3f wIn) {
		return 1;
	}

	public float sampleDirection(Vector3f wOut, Vector3f wIn,
			MersenneTwister rand, Spectrum3f factor) {
		
		
			wIn.set(-wOut.x, -wOut.y, wOut.z);
			factor.set((Spectrum)ks);
			return 1;
		
	}

	public BSDFSpecular clone() {
		return new BSDFSpecular(ks);
	}

	public boolean isSpecular() {
		return true;
	}
}
