package de.grogra.imp3d.ray;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import de.grogra.ray.RTFakeObject;
import de.grogra.ray.intersection.BoundingVolume;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.shader.ShadingEnvironment;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;


public class RTSky extends RaytracerLeaf implements RTFakeObject {

	private final static float FLOAT_PI = (float)Math.PI;
	private final static float LESS_THAN_FLOAT_MAX_VALUE = Float.MAX_VALUE*0.999f;
	
	private float m_angle;
	//private float m_alpha;
	private Ray m_lastRay;
	private final Vector3f m_vec = new Vector3f();
	
	private final ShadingEnvironment m_shaderInput = new ShadingEnvironment();
	
	
	public RTSky(Object object, boolean asNode, long pathId)
	{
		super (object, asNode, pathId);
		m_shaderInput.localPoint.set(0.0f,0.0f,0.0f);
		m_shaderInput.normal.set(0.0f,0.0f,1.0f);
		m_shaderInput.photonDirection = false;
		m_shaderInput.point.set(0.0f,0.0f,0.0f);
		Ray ray = m_shaderInput.rays.nextRay();
		ray.getOrigin().set(0.0f,0.0f,0.0f);
		ray.getDirection().set(0.0f,0.0f,1.0f);
		ray.getColor().set(1.0f,1.0f,1.0f);
		m_shaderInput.solid = false;
		m_shaderInput.dpdu.set(1.0f,0.0f,0.0f);
		m_shaderInput.dpdv.set(0.0f,1.0f,0.0f);
		m_shaderInput.view.set(0.0f,0.0f,1.0f);
	}
	
	
	public boolean isConvex() { return false; }
	
	
	public void setTransformation(Matrix4f mat) {
		super.setTransformation(mat);
		this.setShader(this.shader);
	}
	
	
	public float getDistance(Ray ray,RayContext context) {
		m_lastRay = ray;
		// return some little less than the maximum value
		return LESS_THAN_FLOAT_MAX_VALUE;
	}

	
	public void getIntersectionDescription(IntersectionDescription desc) {
		
		//System.out.println("sdf");
		
		// evaluate uv coordinates
		// u
		if ((m_lastRay.getDirection().x==0.0f) && (m_lastRay.getDirection().y==0.0f)) {
			desc.getUVCoordinate().x = 0.0f;
		} else {
			m_vec.x = m_lastRay.getDirection().x;
			m_vec.y = m_lastRay.getDirection().y;
			m_vec.z = 0.0f;
			m_vec.normalize();
			if (m_vec.y>=0.0) {
				m_angle = (float)Math.acos(m_vec.x);
				desc.getUVCoordinate().x = m_angle/FLOAT_PI*0.5f;
			} else {
				m_angle = (float)Math.acos(-m_vec.x);
				desc.getUVCoordinate().x = 0.5f+m_angle/FLOAT_PI*0.5f;
			}
		}
		// v
		m_angle = (float)Math.asin(m_lastRay.getDirection().z);
		desc.getUVCoordinate().y = (m_angle+FLOAT_PI/2.0f)/FLOAT_PI;
		
		desc.setPoint(null,LESS_THAN_FLOAT_MAX_VALUE);
		desc.setRTObject(this);
//		desc.setShader(this.getRTShader());
		
	}

	
	public boolean isShadeable() {
		return true;
	}
	
	
	public boolean isSolid() {
		return false;
	}

	
	public BoundingVolume getBoundingVolume() {
		return null;
	}


	public void getColor(Ray ray, IntersectionDescription desc, Color3f color) {
		//System.out.println("uv:"+desc.getUVCoordinate());
		m_shaderInput.localPoint.set(ray.getDirection());
		m_shaderInput.uv.set(desc.getUVCoordinate());
		//m_shaderInput.view.negate(ray.getDirection());
		//m_shaderInput.uv.scale(10000.0f);
		this.getRTShader().getShadingColor(m_shaderInput,color);
	}

}
