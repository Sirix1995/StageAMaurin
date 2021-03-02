package de.grogra.ext.limelight.objects;

import javax.vecmath.Point3d;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.ext.limelight.MersenneTwister;
import de.grogra.ext.limelight.Ray;
import de.grogra.imp3d.Camera;
import de.grogra.ray.physics.Spectrum3f;

public class LLCamera implements BSDF {

	public Camera cam;
	float ratio;

	public LLCamera(Camera camera, float r) {
		cam = camera;
		ratio = r;
	}

	public LLCamera(Camera camera, int width, int height) {
		cam = camera;
		ratio = (float) width / (float) height;
	}

	public void GenerateRay(Vector2f sample, Ray ray) {
		Vector3d dd=new Vector3d(ray.o);
		Point3d od=new Point3d(ray.d);
		cam.getRay(sample.x, -1.f * sample.y / ratio, od, dd);
		ray.o.set(od);
		ray.d.set(dd);

	}

	public void f(Vector3f wOut, Vector3f wIn, Spectrum3f f) {
		f.set(1.f,1.f,1.f);

	}

	public Spectrum3f getDiffuseColor() {
		return new Spectrum3f(1.f,1.f,1.f);
	}

	public float sampleDirection(Vector3f wOut, Vector3f wIn,
			MersenneTwister rand, Spectrum3f factor) {
		return 1.f;
	}
	

	public float pdf(Vector3f wOut, Vector3f wIn) {
		return 0;
	}

	public LLCamera clone() {
		return new LLCamera(cam, ratio);
	}

	public boolean isSpecular() {
		return false;
	}


}
