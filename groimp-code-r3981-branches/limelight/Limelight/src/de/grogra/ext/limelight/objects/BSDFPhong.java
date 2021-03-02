package de.grogra.ext.limelight.objects;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import de.grogra.ext.limelight.MersenneTwister;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.vecmath.Math2;

public class BSDFPhong implements BSDF {

	private Spectrum3f kd = new Spectrum3f();
	private Spectrum3f ks = new Spectrum3f();
	private float n;

	private Spectrum3f tmpSpec = new Spectrum3f();
	private Vector3f tmpVec = new Vector3f();
	private Matrix3f tmpMat = new Matrix3f();

	public BSDFPhong(BSDFPhong b) {
		this(b.kd,b.ks,b.n);
	}
	
	public BSDFPhong(Spectrum3f k_d, Spectrum3f k_s, float nnew) {
		kd = k_d;
		ks = k_s;
		n = nnew;
	}

	public BSDFPhong clone(){
		return new BSDFPhong(kd,ks,n);
	}
	
	public void f(Vector3f wOut, Vector3f wIn, Spectrum3f f) {
		f.x = kd.x;
		f.y = kd.y;
		f.z = kd.z;
		//f.scale(Math2.M_1_PI);

		// find wOut's angle for reflection
		tmpVec.x = -wOut.x;
		tmpVec.y = -wOut.y;
		tmpVec.z = wOut.z;

		float beta = Math.abs(tmpVec.dot(wIn));
		if (beta < 0.f)
			beta = 0.f;

		float specFact = (float)((n + 2) * Math2.M_1_2PI * Math.pow(beta, n));

		tmpSpec.x = ks.x;
		tmpSpec.y = ks.y;
		tmpSpec.z = ks.z;
		tmpSpec.scale(specFact);

		f.add((Spectrum) tmpSpec);

	}

	// samples direction wIn from given BSDF. returns the probability with
	// respect to the projected solid angle for the direction. Factor is the
	// weight used in later calculations
	public float sampleDirection(Vector3f wOut, Vector3f wIn, MersenneTwister rand,
			Spectrum3f factor) {

		double r = rand.nextDouble() * (kd.sum() + ks.sum());
		
		
		if (r <= kd.sum()) { //diffuse reflection

			double theta = Math.acos(Math.sqrt((1 - rand.nextDouble())));
			double phi = Math2.M_2PI * rand.nextDouble();

			wIn.x = (float)(Math.sin(theta) * Math.cos(phi));
			wIn.y = (float)(Math.sin(theta) * Math.sin(phi));
			wIn.z = (float)Math.cos(theta);

			factor.set((Spectrum) kd);
			

			return (float)(kd.sum() / (kd.sum() + ks.sum()) * Math2.M_1_2PI);

		} else { //specular reflection

			// transform coordinates to optimal reflection as z-axis
			tmpVec.x = -wOut.x;
			tmpVec.y = -wOut.y;
			tmpVec.z = wOut.z;

			Math2.getOrthogonalBasis(tmpVec, tmpMat, true);

			double phi = rand.nextDouble() * Math2.M_2PI;
			double theta = Math
					.acos(Math.pow(rand.nextDouble(), 1 / (n + 1.0)));
			wIn.x = (float)(Math.sin(theta) * Math.cos(phi));
			wIn.y = (float)(Math.sin(theta) * Math.sin(phi));
			wIn.z = (float)Math.cos(theta);

			float wInz = wIn.z;
			
			
			
			tmpMat.transform(wIn);
			factor.set((Spectrum) ks);
			factor.scale((n + 2) / (n + 1));

						return (float)(ks.sum() / (kd.sum() + ks.sum()) * (n + 1) * Math2.M_1_2PI
					* Math.pow(wInz, n)* wIn.z);
		}

	}
	
	public float pdf(Vector3f wOut, Vector3f wIn) {
		if (wOut.z * wIn.z >= 0)
			return Math2.M_1_PI;
		else
			return 0.f;
	}

	public Spectrum3f getDiffuseColor() {
		return new Spectrum3f(kd.x,kd.y,kd.z);
	}

	public boolean isSpecular() {
		
		return false;
	}

}
