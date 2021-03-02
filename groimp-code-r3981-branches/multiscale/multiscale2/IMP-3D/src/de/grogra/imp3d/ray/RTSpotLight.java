package de.grogra.imp3d.ray;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import net.goui.util.MTRandom;

import de.grogra.imp3d.objects.SpotLight;
import de.grogra.imp3d.shading.Light;
import de.grogra.ray.RTLight;
import de.grogra.ray.Raytracer;
import de.grogra.ray.intersection.BoundingVolume;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.light.NoShadows;
import de.grogra.ray.light.ShadowProcessor;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.ray.shader.ShadingEnvironment;
import de.grogra.ray.util.RayContext;
import de.grogra.ray.util.RayList;
import de.grogra.ray.util.Ray;

public class RTSpotLight extends RaytracerLeaf implements RTLight {

	private Light   m_light        = null;
	private boolean m_isShadowless = true;

	private final Point3f  m_point     = new Point3f();
	ShadingEnvironment m_env       = new ShadingEnvironment();
	private final Ray      m_tmpRay1   = new Ray();
	private Ray            m_tmpRay2   = null;
	private ShadowProcessor    m_noShadows = new NoShadows();
	private float          m_rayLength;
	private final Vector3f m_out       = new Vector3f();
	private final Spectrum3f  m_color     = new Spectrum3f();
	
	private final RayContext m_rayContext = new RayContext();
		
	
	public RTSpotLight(Object object, boolean asNode, long pathId, Light light) {
		super(object, asNode, pathId);
		m_light        = light;
		m_isShadowless = m_light.isShadowless();
	}
	
	
	public boolean isConvex() {
		return false;
	}
	
	private final MTRandom rnd = new MTRandom ();


	public void setTransformation(Matrix4f mat) { 
		super.setTransformation(mat);
		m_point.set(mat.m03,mat.m13,mat.m23);
		//System.out.println("m_point:"+m_point);
		
//		// get light density
//		env.globalToLocal = new Matrix4f(mat);
//		env.globalToLocal.invert();
//		Color3f color = new Color3f(); 
//		Vector3f out = new Vector3f(100,100,100);
//		float desity = m_light.computeBSDF(env,null,out,false,color);
		
		// set light origin and color
		m_env.localToGlobal = new Matrix4f(mat);
		m_env.globalToLocal = new Matrix4f(mat);
		m_env.globalToLocal.invert();
		RayList list = new RayList();
		list.setSize(1);
		m_light.generateRandomOrigins(m_env,list,rnd);		
		m_tmpRay1.getColor().set(list.rays[0].color);
		
		// TODO remove this

//		m_tmpRay1.getColor().scale(0.2f);

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

	
	public int getLightRays(Ray view, IntersectionDescription desc, 
			ShadowProcessor shadowModel, RayList rays) {
		
//		m_out.set(m_point);
//		m_out.sub(desc.getPoint());
//		
		
		m_out.set(desc.getPoint());
		m_out.sub(m_point);
		m_out.normalize();
		
//		m_out.sub(m_point);
		if (m_light.computeBSDF(m_env,null, GroIMPShader.ID,m_out,false,m_color)==0.0f) {
			return 0;
		}
				
		ShadowProcessor cur_shadow_model;
		if (m_isShadowless) {
			cur_shadow_model = m_noShadows;
		} else {
			cur_shadow_model = shadowModel;
		}
		
//		cur_shadow_model.setNormal(desc.getNormal());
		m_tmpRay1.getOrigin().set(m_point);
		m_tmpRay1.getDirection().set(
				desc.getPoint().x-m_point.x,
				desc.getPoint().y-m_point.y,
				desc.getPoint().z-m_point.z);
		m_tmpRay1.getDirection().normalize();
		
		m_rayLength = Raytracer.getT(m_tmpRay1,desc.getPoint());
		m_rayContext.excludeObject = desc.getRTObject();
		
		if (!cur_shadow_model.shadowRay(m_tmpRay1,m_rayLength,view,desc)) {
			m_tmpRay2 = rays.nextRay();
			m_tmpRay2.getDirection().set(
					-m_tmpRay1.getDirection().x,
					-m_tmpRay1.getDirection().y,
					-m_tmpRay1.getDirection().z);
			m_tmpRay2.getOrigin().set(desc.getPoint());
			m_tmpRay2.getColor().set(m_tmpRay1.getColor());
			m_tmpRay2.getColor().x *= m_color.x;
			m_tmpRay2.getColor().y *= m_color.y;
			m_tmpRay2.getColor().z *= m_color.z;
			
			// TODO remove this
			m_tmpRay2.getColor().scale(0.2f);
			
			return 1;
		}
		return 0;
	}

	
	public float getDistance(Ray ray,RayContext context) {
		// do nothing
		return 0;
	}

	public void getIntersectionDescription(IntersectionDescription desc) {
		// do nothing
	}


	public BoundingVolume getBoundingVolume() {
		return null;
	}
	
	public void generateRandomOrigins(RayList out, int seed) {
		m_light.generateRandomOrigins(m_env, out, rnd);
	}

	public void generateRandomRays(Vector3f vout, RayList out, boolean adjoint, int seed) {
		m_light.generateRandomRays(m_env, vout, GroIMPShader.ID, out, adjoint, rnd);
	}

	private final Spectrum3f spectrum = new Spectrum3f ();

	public float computeBSDF(ShadingEnvironment env, Vector3f in, Vector3f out, boolean adjoint, Color3f bsdf) {
		float f = m_light.computeBSDF(env,in, GroIMPShader.ID,out,adjoint,spectrum);
		spectrum.get(bsdf);
		return f;
	}


	public Point3f getGlobalOrigin() {
		
		return m_point;
	}
	
}