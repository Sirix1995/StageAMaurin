package de.grogra.ext.limelight.objects;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import de.grogra.ext.limelight.MersenneTwister;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.vecmath.Math2;

public class LLPointLight extends LLLight {

	public LLPointLight(float intensity, Matrix4f transf, BSDF b) {
		this.setPower(intensity);
		this.setTransformation(new Matrix4f(transf));
		this.setBSDF(b);
	}

	public Spectrum3f getRadiance(Point3f p) {

		Spectrum3f dE = new Spectrum3f(getPower(), getPower(), getPower());
		return dE;

	}

	public float sampleDirection(Vector3f d, MersenneTwister rand) {
		return sampleDirection(d, null, rand, new Spectrum3f());

	}

	public float samplePoint(Point3f p, MersenneTwister rand) {
		Matrix4f t = getTransf();
		p.set(t.m03, t.m13, t.m23);
		return 1.f;
	}

	public float sampleDirection(Vector3f wOut, Vector3f wIn,
			MersenneTwister rand, Spectrum3f factor) {
		// return random direction
		double theta = rand.nextDouble() * Math2.M_2PI;
		double u = rand.nextDouble() * 2 - 1;
		wOut.x = (float) (Math.cos(theta) * Math.sqrt(1 - u * u));
		wOut.y = (float) (Math.sin(theta) * Math.sqrt(1 - u * u));
		wOut.z = (float) u;

		factor.set((Spectrum)getDiffuseColor());
		return 0.5f * Math2.M_1_2PI;
	}

	public float pdf(Vector3f wOut, Vector3f wIn) {
		return Math2.M_1_PI;
	}
	
	public LLPointLight clone() {
		return new LLPointLight(getPower(), getTransf(), getBSDF());
	}

	public float getIntensity() {
		Spectrum3f spec=getDiffuseColor();
		return 0.5f * Math2.M_1_2PI / (float)spec.sum();
	}

	public void f(Vector3f wOut, Vector3f wIn, Spectrum3f f) {
		
	}

	public boolean isSpecular() {
		return false;
	}

}
