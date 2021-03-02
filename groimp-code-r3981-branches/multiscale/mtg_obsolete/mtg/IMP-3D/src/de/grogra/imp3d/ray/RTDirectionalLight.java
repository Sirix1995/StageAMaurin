package de.grogra.imp3d.ray;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import net.goui.util.MTRandom;

import de.grogra.imp3d.objects.SpotLight;
import de.grogra.imp3d.shading.Light;
import de.grogra.ray.RTLight;
import de.grogra.ray.intersection.BoundingVolume;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.light.ShadowProcessor;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.ray.shader.ShadingEnvironment;
import de.grogra.ray.util.RayContext;
import de.grogra.ray.util.RayList;
import de.grogra.ray.util.Ray;

public class RTDirectionalLight extends RaytracerLeaf implements RTLight {

	private Light   m_light        = null;
	private boolean m_isShadowless = true;
	
	private Ray           m_tmpRay2 = null;
	private final Spectrum3f m_color   = new Spectrum3f();
	
	private Vector3f m_toLight = new Vector3f(0.0f,0.0f,-1.0f);
	
	private ShadingEnvironment env = null;
	
	
	public RTDirectionalLight(Object object, boolean asNode, long pathId, Light light) {
		super(object, asNode, pathId);
		m_light = light;
		m_isShadowless = m_light.isShadowless();	
	}
	
	
	public void setTransformation(Matrix4f mat) { 
		super.setTransformation(mat);
		mat.transform(m_toLight);
		m_toLight.normalize();
		m_light.computeExitance(null,m_color);
		//m_color.scale(0.4f);

		// create ShadingEnvironment for generateRandomOrigins and generateRandomRays
		env = new ShadingEnvironment();
		env.localToGlobal = new Matrix4f(mat);
		env.globalToLocal = new Matrix4f(mat);
		env.globalToLocal.invert();
	}
	
	
	public boolean isShadowless() {
		return m_isShadowless;
	}
	

	public boolean isShadeable() {
		return false;
	}


	public boolean isSolid() {
		return false;
	}
	

	public int getLightRays(IntersectionDescription desc, RayList rays) {
		m_tmpRay2 = rays.nextRay();
		m_tmpRay2.getDirection().set(m_toLight);
		m_tmpRay2.getOrigin().set(desc.getPoint());
		m_tmpRay2.getColor().set(m_color);
		return 1;
	}

	public float getDistance(Ray ray) {
		// do nothing
		return 0;
	}

	public void getIntersectionDescription(IntersectionDescription desc) {
		// do nothing
	}


	public BoundingVolume getBoundingVolume() {
		return null;
	}

	private final MTRandom rnd = new MTRandom ();


	public void generateRandomOrigins(RayList out, int seed) {
		m_light.generateRandomOrigins(env, out, rnd);
	}

	public void generateRandomRays(Vector3f vout, RayList out, boolean adjoint, int seed) {
		m_light.generateRandomRays(env, vout, GroIMPShader.ID, out, adjoint, rnd);		
	}

	private final Spectrum3f spectrum = new Spectrum3f ();


	public float computeBSDF(ShadingEnvironment env, Vector3f in, Vector3f out, boolean adjoint, Color3f bsdf) {
		float f= m_light.computeBSDF(env,in,GroIMPShader.ID,out,adjoint,spectrum);
		spectrum.get(bsdf);
		return f;
	}


	public Point3f getGloabalOrigin() {
		// TODO Auto-generated method stub
		return null;
	}


	public Point3f getGlobalOrigin() {
		// TODO Auto-generated method stub
		return null;
	}


	public int getLightRays(Ray view, IntersectionDescription desc, ShadowProcessor shadowProcessor, RayList rays) {
		// TODO Auto-generated method stub
		return 0;
	}


	public float getDistance(Ray ray, RayContext context) {
		// TODO Auto-generated method stub
		return 0;
	}


	public boolean isConvex() {
		// TODO Auto-generated method stub
		return false;
	}

}
