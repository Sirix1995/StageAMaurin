package de.grogra.imp3d.ray;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3f;

import net.goui.util.MTRandom;

import de.grogra.imp3d.shading.Shader;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.ray.shader.RTShader;
import de.grogra.ray.shader.ShadingEnvironment;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;


class GroIMPShader implements RTShader {

	static final Spectrum3f ID = new Spectrum3f (1, 1, 1);

	private Shader m_shader;
	private final ShadingEnvironment m_shaderIn = new ShadingEnvironment();//shaderIn
	
	
	private final RayList                m_rayList  = new RayList();
	private final Vector3f               m_out      = new Vector3f();
	private final Color3f                m_color_3  = new Color3f();
	
	private final RayList m_randomRayList  = new RayList(1);
	private static int m_seed = 2001;//(int)System.currentTimeMillis();
	
	/*
	private final Ray          m_reflectedRay = new Ray();
	private final Vector3f     m_reflectedVariance = new Vector3f();
	private final Ray          m_refractedRay = new Ray();
	private final Vector3f     m_refractedVariance = new Vector3f();
	*/
	
	public GroIMPShader() {
		/*
		m_reflectedRay.direction = new Vector3f();
		m_reflectedRay.color = new Color3f();
		
		m_refractedRay.direction = new Vector3f();
		m_refractedRay.color = new Color3f();
		*/
	} 
	
	public GroIMPShader(Shader shader) {
		setShader(shader);
		
		/*
		m_reflectedRay.direction = new Vector3f();
		m_reflectedRay.color = new Color3f();
		
		m_refractedRay.direction = new Vector3f();
		m_refractedRay.color = new Color3f();
		*/
	}
	
	
	public void setShader(Shader shader) {
		m_shader = shader;
	}
	
	
//	public void getShadingColor(Input in, Color4f color) {
//		
//		m_shaderIn.localPoint.set(in.localPoint);
//		m_shaderIn.point.set(in.point);
//		m_shaderIn.normal.set(in.normal);
//		//m_shaderIn.photonDirection = in.photonDirection;
//		m_shaderIn.solid = in.solid;
//		
//		m_rayList.setSize(in.rays.size());
//		for (int i=0;i<in.rays.size();i++) {
//			m_rayList.rays[i].color.set(in.rays.rays[i].getColor());
//			//m_rayList.rays[i].color.set (1, 1, 1);
//			m_rayList.rays[i].direction.set(in.rays.rays[i].getDirection());
//		}
//		
//		//m_rayList.rays[0].color.set (1, 1, 1);
//		//m_rayList.rays[0].direction.set (0, 0.0f, 0.8f);
//		
//		
//		m_out.set(in.normal);
//		m_shaderIn.uv.set(in.uv);
//		m_shaderIn.dpdu.set(in.tangenteU);
//		m_shaderIn.dpdv.set(in.tangenteV);
//		m_shaderIn.duv.set(0.01f,0.01f);
//		m_shaderIn.iorRatio = 1.0f;
//		
//		
//		//System.out.println("UV: "+in.uv);
//		
//		
//		//Color3f color_3 = MemoryPool.getPool().newColor3f();
//		m_shader.shade(m_shaderIn,m_rayList,m_out,m_color_3);
//		
//		color.set(m_color_3.x,m_color_3.y,m_color_3.z,1.0f);
//		
//		/*
//		if ((color.x==0.0f)&&(color.y==0.0f)&&(color.z==0.0f)) {
//			//color.set(1.0f,0.0f,0.0f,1.0f);
//			//System.out.println("dir: "+in.rays.rays[0].getDirection());
//		}
//		
//		System.out.println("shader: "+m_shader.getClass().getName());
//		*/
//		
//		/*
//		color.set((float)in.view.x,
//				(float)in.view.y,
//				(float)in.view.z,1.0f);
//		*/
//		
//			
//		
//		// clean up
//		//MemoryPool.getPool().freeColor3f(color_3);
//
//	}
	
	
	public void getShadingColor(ShadingEnvironment in, Color3f color) {

		/*
		m_shaderIn.localPoint.set(in.localPoint);
		m_shaderIn.point.set(in.point);
		m_shaderIn.normal.set(in.normal);
		m_shaderIn.solid = in.solid;
		
		m_rayList.setSize(in.rays.getSize());
		for (int i=0;i<in.rays.getSize();i++) {
			//m_rayList.rays[i].color.set(in.rays.rays[i].getColor());
			//m_rayList.rays[i].direction.set(in.rays.rays[i].getDirection());
			m_rayList.rays[i].setRay(in.rays.rays[i]);
			

		
		}
		
		m_out.set(in.normal);
		m_shaderIn.uv.set(in.uv);
		m_shaderIn.dpdu.set(in.dpdu);
		m_shaderIn.dpdv.set(in.dpdv);
		m_shaderIn.duv.set(0.01f,0.01f);
		m_shaderIn.iorRatio = 1.0f;

		
		m_shader.shade(m_shaderIn,m_rayList,m_out,color);
		*/
		

		//color.set(1.0f,0.0f,0.0f);
		
		//System.out.println(m_shader.getClass());
		
		//System.err.println("rays:"+in.rays.size());
		m_shader.shade(in,in.rays,in.view,ID,tmpColor);
		color.set (tmpColor);
		//m_shader.shade(in,in.rays,in.view in.normal,color);
		if ((color.x!=color.x) || (color.y!=color.y) || (color.z!=color.z)) {
			System.err.println("GroIMPShader: Shader.shade() return invalid color - NaN ERROR");
			
		}

	}


	public void computeMaxRays(ShadingEnvironment in, 
								Ray reflectedRay, Vector3f reflectedVariance,
								Ray refractedRay, Vector3f refractedVariance) {

		/*
		m_shaderIn.localPoint.set(in.localPoint);
		m_shaderIn.point.set(in.point);
		m_shaderIn.normal.set(in.normal);
		m_shaderIn.uv.set(in.uv);
		m_shaderIn.iorRatio = in.iorRatio;
		m_out.set(in.view);
		m_out.normalize();
		
//		m_shaderIn.localPoint.set(0.0f,0.0f,0.0f);
//		m_shaderIn.point.set(0.0f,0.0f,0.0f);
//		m_shaderIn.normal.set(0.0f,0.0f,-1.0f);
//		m_shaderIn.uv.set(0.0f,0.0f);
//		m_out.set(-1.0f,0.0f,1.0f);
//		m_out.normalize();
		
		m_shader.computeMaxRays(m_shaderIn,m_out,
				m_reflectedRay,m_reflectedVariance,
				m_refractedRay,m_refractedVariance);
		*/
		
		Vector3f out = new Vector3f(in.view);
		out.normalize();
		m_shader.computeMaxRays(in,out, ID,
				reflectedRay,reflectedVariance,
				refractedRay,refractedVariance);
		
		reflectedRay.getOrigin().set(in.point);
		refractedRay.getOrigin().set(in.point);
//		reflectedWeight.set(reflectedRay.color);
//		refractedWeight.set(refractedRay.color);
		
		/*
		reflectedRay.getOrigin().set(in.point);
		reflectedRay.getDirection().set(m_reflectedRay.direction);
		reflectedWeight.set(m_reflectedRay.color);
		reflectedVariance.set(m_reflectedVariance);
		
		refractedRay.getOrigin().set(in.point);
		refractedRay.getDirection().set(m_refractedRay.direction);
		refractedWeight.set(m_refractedRay.color);
		refractedVariance.set(m_refractedVariance);
		
//		System.out.println("out:"+m_out);
//		System.out.println("normal:"+m_shaderIn.normal);
//		System.out.println("dir:"+m_refractedRay.direction);
//		System.out.println("reflection density:"+m_reflectedRay.directionDensity);
//		System.out.println("reflection variance:"+m_reflectedVariance);
//		System.out.println("reflection color:"+m_reflectedRay.color);
//		System.out.println("refracted direction:"+m_refractedRay.direction);
//		System.out.println("refracted density:"+m_refractedRay.directionDensity);
//		System.out.println("refracted variance:"+m_refractedVariance);
//		System.out.println("refracted color:"+m_refractedRay.color);
		*/
	}
	
	
	public boolean isTransparent(TransparencyInput in) {
		
		m_shaderIn.localPoint.set(in.localPoint);
		m_shaderIn.point.set(in.point);
		m_shaderIn.normal.set(1.0f,0.0f,0.0f);
		m_shaderIn.solid = true;
		m_shaderIn.uv.set(in.uv);
		m_shaderIn.dpdu.set(0.0f,1.0f,0.0f);
		m_shaderIn.dpdv.set(0.0f,0.0f,1.0f);
		m_shaderIn.iorRatio = 1.0f;
		
		m_rayList.setSize(1);
		m_rayList.rays[0].color.set(1.0f,1.0f,1.0f);
		m_rayList.rays[0].direction.set(1.0f,0.0f,0.0f);
					
		m_out.set(-1.0f,0.0f,0.0f);
				
		m_shader.shade(m_shaderIn,m_rayList,m_out,ID,tmpColor);
		m_color_3.set (tmpColor);
		
		if ((m_color_3.x==1.0f) && (m_color_3.y==1.0f) && (m_color_3.z==1.0f)) {
			return true;
		} else {
			return false;
		}
		
		
		
		
//		if (Math.random()>0.5) {
//			return true;
//		} else {
//			return false;
//		}
	}
	
	public boolean isTransparent(){
		return m_shader.isTransparent();
	}
	
	
	private final Spectrum3f spectrum = new Spectrum3f ();
	
	private final Tuple3d tmpColor = new Point3d ();

	public float computeBSDF(ShadingEnvironment env, Vector3f in, Vector3f out, boolean adjoint, Color3f bsdf) {
		float f = m_shader.computeBSDF(env, in, ID, out, adjoint, spectrum);
		spectrum.get (bsdf);
		return f;
	}
	
	
	private final MTRandom rnd = new MTRandom ();

	public void generateRandomRay(ShadingEnvironment env, Ray randomRay) {
		m_randomRayList.rays[0] = randomRay;
//		m_shader.generateRandomRays(env,env.view,m_randomRayList,!env.photonDirection,2001);
		m_shader.generateRandomRays(env,env.view, ID,m_randomRayList,!env.photonDirection,rnd);
		m_seed = m_seed*34747+27373;
		// set origin for random ray
		randomRay.getOrigin().set(env.point);
	}
	
	
	public void generateRandomRays(ShadingEnvironment env, RayList randomRays) {
		m_shader.generateRandomRays(env,env.view, ID,randomRays,!env.photonDirection,rnd);
		// set origin for each ray
		for (int i=0;i<randomRays.size();i++) {
			randomRays.rays[i].getOrigin().set(env.point);
		}
	}


	public 	void generateRandomRays (ShadingEnvironment env, Vector3f out, RayList rays,
			 boolean adjoint, int seed){
		
		m_shader.generateRandomRays(env, out, ID, rays,adjoint, rnd);
		
		for(int i=0; i<rays.getSize();i++){
			rays.rays[i].getOrigin().set(env.point);
		}
	
	}

	public int getShaderFlags() {
		return m_shader.getFlags();
		
	}

	public float getshadingColorByComputeBSDF(ShadingEnvironment env, Vector3f out, boolean adjoint, Color3f bsdf) {
		float rayDensity =1.0f;
		
		bsdf.set(0.0f,0.0f,0.0f);
		for(int i= 0; i< env.rays.getSize(); i++){
			Color3f subBSDF = new Color3f();
			Vector3f diffLightIn = env.rays.rays[i].direction;
			rayDensity *= computeBSDF(env, diffLightIn, out, adjoint, subBSDF);
			bsdf.add(subBSDF);
		}
		
		
		return rayDensity;
	}


}
