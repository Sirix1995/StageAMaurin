package de.grogra.ext.limelight.integrators;

import de.grogra.ext.limelight.*;
import de.grogra.ext.limelight.objects.*;
import java.util.ArrayList;
import javax.vecmath.*;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.IntersectionList;

public class BiDiPathTracingIntegrator extends Integrator {

	private MersenneTwister rand;
	private int maxLength;

	LimelightVisitor scene;

	private Spectrum3f tmpSpec = new Spectrum3f();
	private Spectrum3f tmpSpec2 = new Spectrum3f();
	Point3f tmpPoint = new Point3f();
	Vector3f tmpVec = new Vector3f();
	Vector3f tmpVec2 = new Vector3f();

	IntersectionList ilist = new IntersectionList();
	Intersection isect = new Intersection(ilist);

	private Spectrum3f factor = new Spectrum3f(1.f, 1.f, 1.f);

	private Ray tmpRay = new Ray();

	private Vector3f normal = new Vector3f();
	private Vector3f wIn = new Vector3f();
	private Vector3f wOut = new Vector3f();

	private Point3f plight = new Point3f();
	private Point3f peye = new Point3f();

	private ArrayList<LLEdge> lightPath = new ArrayList<LLEdge>(0);
	private ArrayList<LLEdge> eyePath = new ArrayList<LLEdge>(0);

	Matrix3f T = new Matrix3f();
	Matrix3f Tinv = new Matrix3f();

	private boolean useWeights = false;
	private boolean onlyDirectLight = false;

	public BiDiPathTracingIntegrator(int maxL, LimelightVisitor s,
			MersenneTwister r) {
		super();
		maxLength = maxL;
		scene = s;
		rand = r;
	}

	// Calculates new Edge in light or eyepath by shooting ray into the scene
	// from oldEdge
	LLEdge traceRay(LLEdge oldEdge) {

		// sample bsdf
		wOut.set(oldEdge.wIn);

		oldEdge.worldToEdge.transform(wOut);

		float pSig = oldEdge.bsdf.sampleDirection(wOut, wIn, rand, factor);

		oldEdge.edgeToWorld.transform(wOut);
		oldEdge.edgeToWorld.transform(wIn);

		tmpRay.o.set(oldEdge.p);
		tmpRay.d.set(wIn);

		isect = scene.intersect(tmpRay, null);
		if (isect != null) {

			LLEdge newEdge = new LLEdge(isect);

			newEdge.bsdf = scene.getBSDF(isect.volume).clone();
			newEdge.wIn.set(wIn);

			newEdge.alpha.set((Spectrum) factor);
			newEdge.alpha.scale(1.f / pSig);
			newEdge.alpha.mul((Spectrum) oldEdge.alpha);

			newEdge.pIn = pSig;
			newEdge.pOut = newEdge.bsdf.pdf(wIn, wOut);

			tmpVec.set(wIn);
			tmpVec.negate();
			newEdge.g = Math.abs(Math2.dot(oldEdge.n, wIn)
					* Math2.dot(newEdge.n, tmpVec))
					/ oldEdge.p.distanceSquared(newEdge.p);

			newEdge.light = scene.getAreaLight(isect.volume);

			return newEdge;
		} else
			return null;
	}

	public void getRadiance(Ray ray, Spectrum3f radiance) {

		radiance.setZero();

		// ---create Light Path-----------------------------------------------

		lightPath.clear();
		LLEdge firstEdge = new LLEdge();
		firstEdge.alpha = new Spectrum3f(1.f, 1.f, 1.f);
		firstEdge.bsdf = new BSDFLambert(new Spectrum3f(1, 1, 1));
		firstEdge.g = 1;
		firstEdge.pIn = 1;
		lightPath.add(firstEdge);

		// sample random light source

		LLLight randLight = scene.getRandomLight(rand);
		float pA = randLight.getProp();
		LLEdge lightEdge = new LLEdge();

		pA *= randLight.samplePoint(tmpPoint, rand);

		lightEdge.p.set(tmpPoint);

		lightEdge.alpha.set((Spectrum) randLight.getDiffuseColor());
		lightEdge.alpha.scale(randLight.getIntensity());
		lightEdge.alpha.scale(1.f / pA);

		lightEdge.pIn = pA;
		lightEdge.g = 1;

		lightEdge.bsdf = randLight.clone();

		lightPath.add(lightEdge);

		if (randLight instanceof LLPointLight) { // pointlight
			normal.set(0, 0, 0);
			T.setIdentity();
			Tinv.setIdentity();
		} else {
			((LLAreaLight) randLight).getNormal(normal);
			Math2.getOrthogonalBasis(normal, T, true);
			Tinv.invert(T);
		}

		lightEdge.n.set(normal);

		lightEdge.edgeToWorld.set(T);
		lightEdge.worldToEdge.set(Tinv);

		normal.negate();
		lightEdge.wIn.set(normal);

		lightEdge.light = randLight;

		LLEdge oldEdge = lightEdge;

		for (int i = 1; i <= maxLength && oldEdge.alpha.sum() != 0.0; i++) {

			LLEdge newEdge = traceRay(oldEdge);
			if (newEdge == null)
				break;
			lightPath.add(newEdge);
			oldEdge = newEdge;

		}

		// ---create Eye Path--------------------------------------

		eyePath.clear();
		firstEdge = new LLEdge();
		firstEdge.alpha = new Spectrum3f(1.f, 1.f, 1.f);
		firstEdge.bsdf = new BSDFSpecular(new Spectrum3f(1, 1, 1));
		firstEdge.g = 1;
		firstEdge.pIn = 1;
		eyePath.add(firstEdge);

		LLEdge secondEdge = new LLEdge();
		secondEdge.p.set(ray.o);
		secondEdge.alpha.set(1.f, 1.f, 1.f);

		Matrix3f ct = new Matrix3f();

		scene.cameraTransform.get(ct);
		secondEdge.worldToEdge.set(ct);
		ct.invert();
		secondEdge.edgeToWorld.set(ct);

		tmpVec.set(0, 0, 1);
		ct.transform(tmpVec);
		secondEdge.n.set(tmpVec);

		secondEdge.bsdf = scene.getCamera();
		secondEdge.wIn.set(0, 0, 1);

		secondEdge.g = 1;
		secondEdge.pIn = 1;

		eyePath.add(secondEdge);

		isect = scene.intersect(ray, null);

		if (isect != null) {
			LLEdge thirdEdge = new LLEdge(isect);

			thirdEdge.wIn.set(ray.d);
			thirdEdge.alpha.set(1.f, 1.f, 1.f);
			thirdEdge.bsdf = scene.getBSDF(isect.volume).clone();
			thirdEdge.light = scene.getAreaLight(isect.volume);
			thirdEdge.pIn = 1.f;
			thirdEdge.pOut = 1;

			tmpVec.set(ray.d);
			tmpVec.negate();
			thirdEdge.g = Math.abs(Math2.dot(secondEdge.n, ray.d)
					* Math2.dot(thirdEdge.n, tmpVec))
					/ thirdEdge.p.distanceSquared(secondEdge.p);

			eyePath.add(thirdEdge);

			oldEdge = thirdEdge;
		}

		for (int i = 2; i <= maxLength && oldEdge.alpha.sum() != 0.0
				&& isect != null; i++) {

			LLEdge newEdge = traceRay(oldEdge);
			if (newEdge == null)
				break;
			eyePath.add(newEdge);
			oldEdge = newEdge;

		}

		// combine paths----------------------------------------------------
		// System.out.println("combine");
		for (int s = 0; s < lightPath.size(); s++) {
			for (int t = 0; t < eyePath.size(); t++) {

				lightEdge = lightPath.get(s);
				LLEdge eyeEdge = eyePath.get(t);
				plight = lightEdge.p;
				peye = eyeEdge.p;
				if (t == 0) {
					// light rays, that randomly intersect the camera.
					// impossible for pinhole camera
					tmpSpec.setZero();
				} else if (t == 1) {
					// TODO: directly connect point on lightray to pinhole
					tmpSpec.setZero();

				} else if (s == 0) {

					// eye rays, that randomly intersect area lights
					if (eyeEdge.light != null) {

						wOut.set(eyeEdge.wIn);
						wOut.negate();

						eyeEdge.worldToEdge.transform(wOut);
						eyeEdge.light.f(wOut, new Vector3f(0, 0, -1), tmpSpec);
						eyeEdge.edgeToWorld.transform(wOut);

						if (useWeights) {
							float weight = getWeight(t);
							checkWeight(weight);
							tmpSpec.scale(1.f / weight);
						} else
							tmpSpec.scale(1.f / (s + t + 1));

						tmpSpec.mul((Spectrum) lightEdge.alpha);
						tmpSpec.mul((Spectrum) eyeEdge.alpha);

					} else
						tmpSpec.setZero();

				} else {
					// continue, if path ends on specular surface because edges
					// can't be connected
					if (lightPath.get(s).bsdf.isSpecular()
							|| eyePath.get(t).bsdf.isSpecular())
						continue;

					if (scene.isVisible(peye, plight)) {

						// calculate unweighted contribution

						// calculate bsdf value for connecting edge on light
						// path
						wOut.set(peye.x - plight.x, peye.y - plight.y, peye.z
								- plight.z);
						wOut.normalize();
						wIn.set(lightEdge.wIn);

						lightEdge.worldToEdge.transform(wIn);
						lightEdge.worldToEdge.transform(wOut);
						lightEdge.bsdf.f(wOut, wIn, tmpSpec);
						float costhetai = wOut.z;
						lightEdge.edgeToWorld.transform(wIn);
						lightEdge.edgeToWorld.transform(wOut);

						// calculate bsdf value for connecting edge on eye path
						wIn.set(wOut);
						wOut.set(eyeEdge.wIn);
						wOut.negate();

						eyeEdge.worldToEdge.transform(wIn);
						eyeEdge.worldToEdge.transform(wOut);
						eyeEdge.bsdf.f(wIn, wOut, tmpSpec2);
						float costhetao = -wIn.z;
						eyeEdge.edgeToWorld.transform(wIn);
						eyeEdge.edgeToWorld.transform(wOut);

						// f_L*f_E
						tmpSpec.mul((Spectrum) tmpSpec2);

						float dist = lightEdge.p.distanceSquared(eyeEdge.p);

						// geometry term
						// G=|cos(theta_i)*cos(theta_o)|/||x-x'||^2
						float g = Math.abs(costhetai * costhetao) / dist;

						tmpSpec.scale(g);

						if (tmpSpec.sum() == 0)
							continue;

						if (useWeights) {
							float weight = getWeight(s, t, g);
							checkWeight(weight);
							tmpSpec.scale(1.f / weight);
						} else
							tmpSpec.scale(1.f / (s + t + 1));

						tmpSpec.mul((Spectrum) lightEdge.alpha);
						tmpSpec.mul((Spectrum) eyeEdge.alpha);

						if (onlyDirectLight)
							tmpSpec.setZero();

					} else
						tmpSpec.setZero();
				}

				checkSpectrum(tmpSpec);

				radiance.add((Spectrum) tmpSpec);

			}
		}
	}

	// returns weight for combining samples with power heuristic for techniques
	// with s=0
	private float getWeight(int t) {

		LLEdge eyeEdge = eyePath.get(t);
		tmpSpec.scale(eyeEdge.light.getIntensity());

		float pFrac = 1.f;
		float weight = 1;

		LLAreaLight al = (LLAreaLight) eyeEdge.light;

		if (t > 0) {

			// p(s)/p(s+1)

			pFrac *= al.getPower() / (scene.getTotalPower() * al.getArea())
					/ (eyeEdge.pIn * eyeEdge.g);

			// w(s+1)
			if (!al.getBSDF().isSpecular()
					&& !eyePath.get(t - 1).bsdf.isSpecular())
				weight += pFrac * pFrac;
		}

		if (t > 1) {
			// p(s)/p(s+2)
			tmpVec.set(0, 0, 1);

			tmpVec2.set(eyeEdge.wIn);
			tmpVec2.negate();
			eyeEdge.worldToEdge.transform(tmpVec2);

			pFrac *= al.pdf(tmpVec, tmpVec2) * eyeEdge.g
					/ (eyePath.get(t - 1).pIn * eyePath.get(t - 1).g);

			// w(s+2)
			if (!eyePath.get(t - 1).bsdf.isSpecular()
					&& !eyePath.get(t - 2).bsdf.isSpecular())
				weight += pFrac * pFrac;
		}

		// p(s+3) to p(s+t)
		for (int i = t - 1; i >= 2; i--) {

			pFrac *= eyePath.get(i + 1).pOut * eyePath.get(i).g
					/ (eyePath.get(i - 1).pIn * eyePath.get(i - 1).g);

			// w(s-2) to w(0)
			if (!eyePath.get(i - 1).bsdf.isSpecular()
					&& !eyePath.get(i - 2).bsdf.isSpecular())
				weight += pFrac * pFrac;
		}

		return weight;
	}

	// returns weight for combining samples with power heuristic, g is geometry
	// term between edges s and t
	private float getWeight(int s, int t, float g) {
		// calculate weights
		// w=1/sum_i( (p_i/p_s)^2 )

		LLEdge lightEdge = lightPath.get(s);
		LLEdge eyeEdge = eyePath.get(t);

		// p(s)
		float pFrac = 1;
		// w(s)
		float weight = 0;
		if (!lightPath.get(s).bsdf.isSpecular()
				&& !eyePath.get(t).bsdf.isSpecular())
			weight = 1;

		if (s > 0) {

			// p(s)/p(s-1)
			tmpVec.set(eyeEdge.wIn);
			tmpVec.negate();
			eyeEdge.worldToEdge.transform(tmpVec);

			tmpVec2.sub(eyeEdge.p, lightEdge.p);
			eyeEdge.worldToEdge.transform(tmpVec2);

			pFrac *= eyeEdge.bsdf.pdf(tmpVec, tmpVec2) * g
					/ (lightEdge.pIn * lightEdge.g);

			// w(s-1)
			if (!lightPath.get(s - 1).bsdf.isSpecular()
					&& !lightPath.get(s).bsdf.isSpecular())
				weight += pFrac * pFrac;
		}

		if (s > 1) {
			// p(s-1)/p(s-2)
			tmpVec.sub(eyeEdge.p, lightEdge.p);
			lightEdge.worldToEdge.transform(tmpVec);

			tmpVec2.set(lightEdge.wIn);
			tmpVec2.negate();
			lightEdge.worldToEdge.transform(tmpVec2);

			pFrac *= lightEdge.bsdf.pdf(tmpVec, tmpVec2) * lightEdge.g
					/ (lightPath.get(s - 1).pIn * lightPath.get(s - 1).g);

			// w(s-2)
			if (!lightPath.get(s - 2).bsdf.isSpecular()
					&& !lightPath.get(s - 1).bsdf.isSpecular())
				weight += pFrac * pFrac;
		}

		// p(s-3) to p(0)
		for (int i = s - 1; i >= 2; i--) {

			pFrac *= lightPath.get(i + 1).pOut * lightPath.get(i).g
					/ (lightPath.get(i - 1).pIn * lightPath.get(i - 1).g);

			// w(s-2) to w(0)
			if (!lightPath.get(i - 2).bsdf.isSpecular()
					&& !lightPath.get(i - 1).bsdf.isSpecular())
				weight += pFrac * pFrac;
		}

		// p(s)
		pFrac = 1;

		if (t > 0) {

			// p(s)/p(s+1)
			tmpVec.set(lightEdge.wIn);
			tmpVec.negate();
			lightEdge.worldToEdge.transform(tmpVec);

			tmpVec2.sub(eyeEdge.p, lightEdge.p);
			lightEdge.worldToEdge.transform(tmpVec2);

			pFrac *= lightEdge.bsdf.pdf(tmpVec, tmpVec2) * g
					/ (eyeEdge.pIn * eyeEdge.g);

			// w(s+1)
			if (!eyePath.get(t).bsdf.isSpecular()
					&& !eyePath.get(t - 1).bsdf.isSpecular())
				weight += pFrac * pFrac;
		}

		if (t > 1) {
			// p(s)/p(s+2)
			tmpVec.sub(lightEdge.p, eyeEdge.p);
			eyeEdge.worldToEdge.transform(tmpVec);

			tmpVec2.set(eyeEdge.wIn);
			tmpVec2.negate();
			eyeEdge.worldToEdge.transform(tmpVec2);

			pFrac *= eyeEdge.bsdf.pdf(tmpVec, tmpVec2) * eyeEdge.g
					/ (eyePath.get(t - 1).pIn * eyePath.get(t - 1).g);

			// w(s+2)
			if (!eyePath.get(t - 1).bsdf.isSpecular()
					&& !eyePath.get(t - 2).bsdf.isSpecular())
				weight += pFrac * pFrac;
		}

		// p(s+3) to p(s+t)
		for (int i = t - 1; i >= 2; i--) {

			pFrac *= eyePath.get(i + 1).pOut * eyePath.get(i).g
					/ (eyePath.get(i - 1).pIn * eyePath.get(i - 1).g);

			// w(s-2) to w(0)
			if (!eyePath.get(i - 1).bsdf.isSpecular()
					&& !eyePath.get(i - 2).bsdf.isSpecular())
				weight += pFrac * pFrac;
		}
		return weight;
	}

	// checks weight for errors
	private boolean checkWeight(float weight) {

		if (weight == 0) {
			System.err.println("Weight 0");
			return false;
		}
		if (Float.isNaN(weight)) {
			System.err.println("Weight NaN");
			return false;
		}
		if (Float.isInfinite(weight)) {
			System.err.println("Weight Infinte");
			return false;
		}
		if (weight < 0) {
			System.err.println("Weight negative");
			return false;
		}
		if (weight < 1) {
			System.err.println("Weight too small");
			return false;
		}
		return true;
	}
	
	private boolean checkSpectrum(Spectrum3f spec){
		if (spec.x < 0 | spec.y < 0 | spec.z < 0) {
			System.err
					.println("Negative radiance returned for image sample");
			spec.absolute();
			return false;
		}
		if (Float.isNaN(spec.x) | Float.isNaN(spec.y)
				| Float.isNaN(spec.z)) {
			System.err
					.println("NaN radiance returned for image sample");
			spec.setZero();
			return false;
		}
		if (Float.isInfinite(spec.x) | Float.isInfinite(spec.y)
				| Float.isInfinite(spec.z)) {
			System.err
					.println("Infinite radiance returned for image sample");
			spec.setZero();
			return false;
		}
		return true;
	}
}