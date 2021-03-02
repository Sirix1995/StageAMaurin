package de.grogra.ext.limelight;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.vecmath.*;

import de.grogra.ext.limelight.objects.BSDF;
import de.grogra.ext.limelight.objects.BSDFLambert;
import de.grogra.ext.limelight.objects.BSDFPhong;
import de.grogra.ext.limelight.objects.BSDFSpecular;
import de.grogra.ext.limelight.objects.LLAreaLight;
import de.grogra.ext.limelight.objects.LLCamera;
import de.grogra.ext.limelight.objects.LLLight;
import de.grogra.ext.limelight.objects.LLPointLight;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.Path;
import de.grogra.imp3d.*;
import de.grogra.imp3d.objects.AreaLight;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.Parallelogram;
import de.grogra.imp3d.objects.PointLight;
import de.grogra.imp3d.shading.*;

import de.grogra.math.Graytone;
import de.grogra.math.RGBColor;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.ray2.Options;
import de.grogra.vecmath.geom.*;

public class LimelightVisitor extends DisplayVisitor {

	private ArrayList<Volume> volumes;
	private Hashtable<Integer, Matrix4f> transformations;
	private Hashtable<Integer, BSDF> bsdfs;
	private Hashtable<Integer, LLAreaLight> areaLights;
	private BSDF tmpBsdf;
	private LLAreaLight tmpLight;
	private Spectrum3f tmpSpec = new Spectrum3f();
	private int volumeCount;
	private LLCamera camera;
	public Matrix4d cameraTransform;
	private Point3f tmpPoint = new Point3f();

	public ArrayList<LLLight> lights;
	private float totalPow = 0;
	private int lightCount;

	private VolumeBuilder builder;

	public LimelightVisitor(Graph graph, float epsilon, Options opts,
			ViewConfig3D view, LLCamera cam, Matrix4d cameraTrans) {

		volumes = new ArrayList<Volume>();
		transformations = new Hashtable<Integer, Matrix4f>();
		bsdfs = new Hashtable<Integer, BSDF>();
		lights = new ArrayList<LLLight>();
		areaLights = new Hashtable<Integer, LLAreaLight>();
		volumeCount = 0;
		lightCount = 0;
		camera = cam;
		cameraTransform = cameraTrans;

		Matrix4d m = new Matrix4d();
		m.setIdentity();
		init(GraphState.current(graph), m, view, view != null);

		PolygonizationCache cache = new PolygonizationCache(state,
				Polygonization.COMPUTE_NORMALS | Polygonization.COMPUTE_UV,
				((Number) opts.get("flatness", new Float(1))).floatValue(),
				true);
		builder = new VolumeBuilder(cache, epsilon) {

			public GraphState getRenderGraphState() {
				return LimelightVisitor.this.getGraphState();
			}

			public Shader getCurrentShader() {
				return LimelightVisitor.this.getCurrentShader();
			}

			@Override
			protected Matrix4d getCurrentTransformation() {
				return LimelightVisitor.this.getCurrentTransformation();
			}

			@Override
			protected void addVolume(Volume v, Matrix4d t, Shader s) {
				LimelightVisitor.this.addVolume(v, t, s);

			}
		};

	}

	protected void addVolume(Volume v, Matrix4d t, Shader s) {
		// TODO: integrate into kd-tree
		volumes.add(v);
		Matrix4f tf = new Matrix4f(t);
		transformations.put(v.hashCode(), tf);
		bsdfs.put(v.hashCode(), tmpBsdf);
		if (tmpLight != null)
			areaLights.put(v.hashCode(), tmpLight);
		volumeCount++;
	}

	@Override
	protected void visitImpl(Object object, boolean asNode, Shader s, Path path) {
		Object shape = state.getObjectDefault(object, asNode, Attributes.SHAPE,
				null);

		if (shape instanceof Renderable) {
			tmpLight = null;
			tmpBsdf = toBSDF(s);

			// Parallelograms can be area lights
			if (shape instanceof Parallelogram) {
				Parallelogram p = (Parallelogram) shape;

				AreaLight al = p.getLight();
				if (al != null) {
					float pow = al.getPower();
					Vector3f ax = new Vector3f(p.getAxis());
					Matrix4f tr = new Matrix4f(getCurrentTransformation());
					LLAreaLight lalight = new LLAreaLight(pow, tr, ax, p
							.getLength(), tmpBsdf.clone());
					lights.add(lalight);
					tmpLight = lalight;
					lightCount++;
					totalPow += lalight.getPower();
				}
			}

			((Renderable) shape).draw(object, asNode, builder);
		}

		Light light = (Light) state.getObjectDefault(object, asNode,
				Attributes.LIGHT, null);
		if (light != null) {

			if (light instanceof PointLight) {
				PointLight pl = (PointLight) light;
				float pow = pl.getPower();
				Matrix4f tr = new Matrix4f(getCurrentTransformation());

				RGBColor col = pl.getColor();

				Spectrum3f color = new Spectrum3f(col.x, col.y, col.z);
				BSDFLambert bsdf = new BSDFLambert(color);
				LLPointLight llight = new LLPointLight(pow, tr, bsdf);
				lights.add(llight);
				lightCount++;
				totalPow += llight.getPower();
			}

		}
	}

	// finds first intersection of ray with the scene objects and stores it in
	// isect
	// exclude: First intersection to exclude
	public Intersection intersect(Ray ray, Intersection exclude) {
		// TODO: traverse kd-tree
		IntersectionList ilist = new IntersectionList();
		Iterator<Volume> it = volumes.iterator();

		float oldDist = Float.MAX_VALUE;
		Intersection isect = null;

		while (it.hasNext()) {
			Volume v = it.next();
			Line l = ray.ToLine();
			v.computeIntersections(l, Intersection.ALL, ilist, exclude, null);
			if (ilist.size > 0) {

				tmpPoint.set(ilist.elements[0].getPoint());
				float newDist = tmpPoint.distance(ray.o);

				// closest intersection is volume itself
				if (newDist < 0.1f) {
					// take second intersection
					if (ilist.elements[1] != null) {
						ilist.elements[0] = ilist.elements[1];
						tmpPoint.set(ilist.elements[0].getPoint());
						newDist = tmpPoint.distance(ray.o);
					} else
						newDist = Float.MAX_VALUE;
				}

				if (newDist < oldDist) {
					isect = ilist.elements[0];
					oldDist = newDist;
				}

				ilist.elements[0] = null;
				ilist.clear();

			}

		}
		return isect;
	}

	// returns true if no object is between point a and b
	// point a can be a singularity i.e. a point light. Point b has to be an
	// intersectable surface
	public boolean isVisible(Point3f a, Point3f b) {
		Vector3f ab = new Vector3f(b.x - a.x, b.y - a.y, b.z - a.z);
		Ray ray = new Ray(a, ab);
		Intersection isect = intersect(ray, null);

		if (isect != null) {
			tmpPoint.set(isect.getPoint());
			return (tmpPoint.distance(b) < 0.01);
		} else
			return false;

	}

	public Volume getVolume(int i) {
		return volumes.get(i);
	}

	public int getVolumeCount() {

		return volumeCount;
	}

	public LLLight getLight(int i) {

		return lights.get(i);
	}

	public LLLight getRandomLight(MersenneTwister rand) {

		float r = rand.nextFloat() * totalPow;
		float psum = 0;
		for (int i = 0; i < lights.size(); i++) {

			float pow = lights.get(i).getPower();
			psum += pow;
			if (r <= psum) {
				lights.get(i).setProp(1.f / totalPow);
				return lights.get(i);

			}
		}
		return null;
	}

	public int getLightCount() {
		return lightCount;
	}

	public void getEmittedRadiance(Intersection isect, Vector3d wOut,
			Spectrum3f spec) {
		Volume v = isect.volume;

		LLAreaLight l = areaLights.get(v.hashCode());
		if (l == null) {
			spec.setZero();
			return;
		}

		else {
			tmpSpec = l.getBSDF().getDiffuseColor();
			tmpSpec.scale(l.getIntensity());
			spec.set((Spectrum) tmpSpec);
		}
	}

	public Matrix4f getTransformation(Volume volume) {
		return transformations.get(volume.hashCode());

	}

	//transforms a GroImp shader to a Limelight BSDF
	private BSDF toBSDF(Shader s) {

		if (s instanceof RGBAShader)
			return new BSDFLambert(new Spectrum3f(((RGBAShader) s).x,
					((RGBAShader) s).y, ((RGBAShader) s).z));

		else if (s instanceof Phong) {

			// TODO: textures, greyvalues etc
			Phong p = (Phong) s;

			Spectrum3f rhod = new Spectrum3f();
			Spectrum3f rhos = new Spectrum3f();

			float n = 4.f;

			RGBColor ctra = new RGBColor();

			if (p.getTransparency() instanceof Graytone) {
				float val = ((Graytone) (p.getTransparency())).getValue();
				ctra.set(val, val, val);
			} else if (p.getTransparency() instanceof RGBColor) {
				ctra = (RGBColor) (p.getTransparency());
			} else
				ctra.set(0.f, 0.f, 0.f);

			RGBColor cdif = new RGBColor();

			if (p.getDiffuse() == null)
				cdif.set(0.5f, 0.5f, 0.5f);
			if (p.getDiffuse() instanceof Graytone) {
				float val = ((Graytone) (p.getDiffuse())).getValue();
				cdif.set(val, val, val);
			} else if (p.getDiffuse() instanceof RGBColor)
				cdif = (RGBColor) (p.getDiffuse());

			rhod.x = cdif.x * (1.f - ctra.x);
			rhod.y = cdif.y * (1.f - ctra.y);
			rhod.z = cdif.z * (1.f - ctra.z);

			RGBColor cspe = new RGBColor();

			if (p.getSpecular() instanceof Graytone) {
				float val = ((Graytone) (p.getSpecular())).getValue();
				cspe.set(val, val, val);
			} else if (p.getSpecular() instanceof RGBColor)
				cspe = (RGBColor) (p.getSpecular());

			rhos.x = cspe.x * (1.f - ctra.x);
			rhos.y = cspe.y * (1.f - ctra.y);
			rhos.z = cspe.z * (1.f - ctra.z);

			if (p.getShininess() instanceof Graytone)
				n = Phong.convertShininess(((Graytone) (p.getShininess()))
						.getValue());
			if (p.getShininess() instanceof RGBColor)
				n = Phong.convertShininess(((RGBColor) (p.getShininess())).x);

			//maximal shininess produces complete specular surface
			if (n == Phong.MAX_SHININESS)
				return new BSDFSpecular(rhos);
			else
				return new BSDFPhong(rhod, rhos, n);

		} else
			return null;
	}

	public BSDF getBSDF(Volume volume) {
		return bsdfs.get(volume.hashCode());
	}

	public LLCamera getCamera() {
		return camera;
	}

	public LLAreaLight getAreaLight(Volume v) {
		return areaLights.get(v.hashCode());
	}

	public float getTotalPower() {
		return totalPow;
	}

}
