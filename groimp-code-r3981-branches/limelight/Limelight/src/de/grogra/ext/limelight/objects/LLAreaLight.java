package de.grogra.ext.limelight.objects;


import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import de.grogra.ext.limelight.MersenneTwister;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.vecmath.Math2;


public class LLAreaLight extends LLLight {

	private Vector3f axis = new Vector3f(1, 0, 0);
	private float length;
	private Vector3f tmpVec = new Vector3f();
	private Vector3f tmpVec2 = new Vector3f();
	private Point3f tmpPoint= new Point3f();

	public LLAreaLight(float p, Matrix4f transf, Vector3f a, float l, BSDF b) {
		this.setPower(p);
		this.setTransformation(new Matrix4f(transf));
		axis = a;
		length = l;
		setBSDF(b);
	}

	public Spectrum3f getRadiance(Point3f p) {
		Spectrum3f rad = getBSDF().getDiffuseColor().clone();
		rad.scale(getPower());
		return rad;

	}

	public float sampleDirection(Vector3f wOut, MersenneTwister rand) {
		return sampleDirection(null, wOut, rand, new Spectrum3f());
	}

	public float samplePoint(Point3f p, MersenneTwister rand) {

		Matrix4f t = getTransf();
		tmpVec.set(0, 0, length);
		t.transform(tmpVec);
		tmpVec2.set(axis);
		t.transform(tmpVec2);
		tmpVec.cross(tmpVec, tmpVec2);
		float area = 2 * tmpVec.length();

		float r = rand.nextFloat();
		float s = rand.nextFloat() * 2 - 1.f;
		
		tmpPoint.set(r, 0, s);
		t.transform(tmpPoint);
		p.set(tmpPoint);

		return 1.f / area;
	}

	public float sampleDirection(Vector3f wOut, Vector3f wIn,
			MersenneTwister rand, Spectrum3f factor) {

		float p = getBSDF().sampleDirection(wOut, wIn, rand, factor);

		return p;

	}
	
	public float pdf(Vector3f wOut, Vector3f wIn) {
		if (wOut.z  > 0)
			return getBSDF().pdf(wOut, wIn);
		else
			return 0.f;
	}

	public float getIntensity() {
		Spectrum3f spec = getDiffuseColor();
		return getPower() / (2 * axis.length() * length * (float) spec.sum());
	}

	public LLAreaLight clone() {
		return new LLAreaLight(getPower(), getTransf(), axis, length, getBSDF());
	}

	public void getNormal(Vector3f n) {
		Matrix4f t = getTransf();
		n.set(t.m01, t.m11, t.m21);
	}
	
	public float getArea(){
		Matrix4f t = getTransf();
		tmpVec.set(0, 0, length);
		t.transform(tmpVec);
		tmpVec2.set(axis);
		t.transform(tmpVec2);
		tmpVec.cross(tmpVec, tmpVec2);
		return 2 * tmpVec.length();
	}

	public void f(Vector3f wOut, Vector3f wIn, Spectrum3f f) {
		// Area lights only illuminate one direction
		if (wOut.z > 0)
			getBSDF().f(wOut, wIn, f);
		else
			f.setZero();

	}

	public boolean isSpecular() {
		return getBSDF().isSpecular();
	}

}
