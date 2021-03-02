package de.grogra.imp3d.ray;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import net.goui.util.MTRandom;

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

public class RTAreaLight extends RaytracerLeaf implements RTLight {

//	private int LIGHT_RAYS_DENSITY = 40;
//	private int m_maximalLightRays = 150;
	
	private Light   m_light        = null;
	private boolean m_isShadowless = true;

	private final ShadingEnvironment m_env = new ShadingEnvironment();
	private final RayList m_rayList = new RayList();
	private final int m_seed         = (int)System.currentTimeMillis();
	private ShadowProcessor    m_noShadows = new NoShadows();
	private int m_added;
	private final Ray      m_tmpRay1   = new Ray();
	private Ray            m_tmpRay2   = null;
	private float          m_rayLength;
	private final Vector3f m_out = new Vector3f();
	private final Spectrum3f  m_bsdfColor = new Spectrum3f();
	
	private final RayContext m_rayContext = new RayContext();
	
	private int m_maximalLightRays = 50;
	
	
	public RTAreaLight(Object object, boolean asNode, long pathId, 
			Light light, Vector3f axis1, Vector3f axis2) {
		super(object, asNode, pathId);
		m_light        = light;
		m_isShadowless = m_light.isShadowless();
		Vector3f normal = new Vector3f();
		normal.cross(axis1,axis2);
//		m_lightRays = m_maximalLightRays;//Math.round(normal.length()*LIGHT_RAYS_DENSITY);
		m_rayList.setSize(m_maximalLightRays);
//		System.out.println("rays:"+m_lightRays);
	}
	
	
	public boolean isConvex() {
		return false;
	}
	
	
	public void setTransformation(Matrix4f mat) { 
		super.setTransformation(mat);
		
		// set light environment
		m_env.localToGlobal = new Matrix4f(mat);
		m_env.globalToLocal = new Matrix4f(mat);
		m_env.globalToLocal.invert();
		
		
		//m_env.userVector = new Vector3f();
		//m_env.userVector2 = new Vector3f();
		//m_env.userVector3 = new Vector3f();
//		RayList list = new RayList();
//		list.setSize(1);
//		m_light.generateRandomOrigins(m_env,list,0);		
//		m_tmpRay1.getColor().set(list.rays[0].color);
//		
//		// TODO remove this
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

	private final MTRandom rnd = new MTRandom ();

	
	public int getLightRays(Ray view, IntersectionDescription desc, 
			ShadowProcessor shadowModel, RayList rays) {
		
		m_added = 0;
		m_light.generateRandomOrigins(m_env,m_rayList,rnd);
		
		ShadowProcessor cur_shadow_model;
		if (m_isShadowless) {
			cur_shadow_model = m_noShadows;
		} else {
			cur_shadow_model = shadowModel;
		}
		
//		cur_shadow_model.setNormal(desc.getNormal());
		
		for (int i=0;i<m_maximalLightRays;i++) {
			
			//System.out.println("POINT:"+m_rayList.rays[i].origin);
			
			m_tmpRay1.getOrigin().set(m_rayList.rays[i].origin);
			m_tmpRay1.getDirection().set(
					desc.getPoint().x-m_rayList.rays[i].origin.x,
					desc.getPoint().y-m_rayList.rays[i].origin.y,
					desc.getPoint().z-m_rayList.rays[i].origin.z);
			m_tmpRay1.getDirection().normalize();
			
			m_rayLength = Raytracer.getT(m_tmpRay1,desc.getPoint());
			m_rayContext.excludeObject = desc.getRTObject();
			
			//System.out.println("LENGTH:"+m_rayLength);
			
			if (!cur_shadow_model.shadowRay(m_tmpRay1,m_rayLength,view,desc)) {
				m_tmpRay2 = rays.nextRay();
				m_tmpRay2.getDirection().set(
						-m_tmpRay1.getDirection().x,
						-m_tmpRay1.getDirection().y,
						-m_tmpRay1.getDirection().z);
				m_tmpRay2.getOrigin().set(desc.getPoint());
				m_tmpRay2.getColor().set(m_rayList.rays[i].color);

				
				m_out.set(desc.getPoint());
				m_out.sub(m_rayList.rays[i].origin);
				m_out.normalize();
				//m_out.negate();
				m_light.computeBSDF(m_env,null,GroIMPShader.ID,m_out,false,m_bsdfColor);
				
//				m_bsdfColor.scale(0.07f/m_maximalLightRays);
//				m_bsdfColor.scale(0.07f/LIGHT_RAYS_DENSITY);
				m_tmpRay2.getColor().x *= m_bsdfColor.x;
				m_tmpRay2.getColor().y *= m_bsdfColor.y;
				m_tmpRay2.getColor().z *= m_bsdfColor.z;
				
				
				// TODO remove this
				//m_tmpRay2.getColor().scale(0.05f/m_lightRays);
				
				//System.out.println("ADDED COLOR: "+m_tmpRay2.getColor());
				
				m_added++;
			}
		}
		
		
		
		
		return m_added;
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
		spectrum.get (bsdf);
		return f;
	}


	public Point3f getGlobalOrigin() {
		m_light.generateRandomOrigins(m_env,m_rayList,rnd);
		return m_rayList.rays[0].origin;
	}

	
	
}
