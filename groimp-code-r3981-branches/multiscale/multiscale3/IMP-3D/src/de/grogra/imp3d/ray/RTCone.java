package de.grogra.imp3d.ray;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import de.grogra.ray.RTObject;
import de.grogra.ray.intersection.BoundingBox;
import de.grogra.ray.intersection.BoundingVolume;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.intersection.Intersections;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;


public class RTCone extends RaytracerLeaf implements RTObject {

	public final Intersections.ConeInput m_coneInput = 
		new Intersections.ConeInput(); 
	public final Intersections.ConeLocalVariables m_coneLocalVariables =
		new Intersections.ConeLocalVariables();
	public final Intersections.ObjectOutput m_coneOutput =
		new Intersections.ObjectOutput();
	
	private final Vector3f m_vec1 = new Vector3f();
	private final Vector3f m_vec2 = new Vector3f();
	private final Vector3f m_vec3 = new Vector3f();
	private final Matrix4f m_axisTransform = new Matrix4f();
	private boolean        m_shadeable = true;
	private BoundingBox    m_boundingVolume = null;
	
	
	public RTCone(Object object, boolean asNode, long pathId,
			float radius,Vector3f axis,boolean openBottom) {
		super(object, asNode, pathId);
		
		if (radius==0.0f) {
			m_shadeable = false;
			return;
		}
				
		m_coneInput.bottom_normal.set(axis);
		m_coneInput.bottom_normal.negate();
		m_coneInput.bottom_normal.normalize();
		m_coneInput.open_bottom = false;
		
		m_vec3.set(axis);
		if ((m_vec3.x==0.0f) && (m_vec3.z==0.0f)) {
			m_vec2.set(0.0f,0.0f,-1.0f);
		} else {
			m_vec2.set(0.0f,1.0f,0.0f);
		}
		m_vec1.cross(m_vec2,m_vec3);
		m_vec2.cross(m_vec3,m_vec1);
		m_vec1.normalize();
		m_vec2.normalize();
		m_vec1.scale(radius);
		m_vec2.scale(radius);
		
		m_axisTransform.setIdentity();
		m_axisTransform.m00 = m_vec1.x;
		m_axisTransform.m10 = m_vec1.y;
		m_axisTransform.m20 = m_vec1.z;
		m_axisTransform.m01 = m_vec2.x;
		m_axisTransform.m11 = m_vec2.y;
		m_axisTransform.m21 = m_vec2.z;
		m_axisTransform.m02 = m_vec3.x;
		m_axisTransform.m12 = m_vec3.y;
		m_axisTransform.m22 = m_vec3.z;
		
		m_coneInput.bottom_tangenteU.set(m_vec1);
		m_coneInput.bottom_tangenteV.set(-m_vec2.x,-m_vec2.y,-m_vec2.z);
		
	}
	
	
	public void setTransformation(Matrix4f mat) {
		super.setTransformation(mat);
		
		if (m_shadeable) {
			mat.transform(m_coneInput.bottom_normal);
			m_coneInput.transformation.mul(mat,m_axisTransform);
			m_coneInput.invers_transformation.invert(m_coneInput.transformation);
			this.setShader(this.shader);
			if (this.interior!=null) {
				this.setMedium(this.interior);
			}
//			if ((this.shader.getFlags() & Intersections.EVALUATE_TRANSPARENCY)!=0) {
//				m_coneInput.transparencyShader = this.getRTShader();
//			} else {
//				m_coneInput.transparencyShader = null;
//			}
		}
	}


	public boolean isShadeable() {
		return m_shadeable;
	}
	
	
	public boolean isSolid() {
		return (!m_coneInput.open_bottom);
	}
	
	
	public boolean isConvex() {
		return (!m_coneInput.open_bottom);
	}
	
	
	public float getDistance(Ray ray,RayContext context) {
		m_coneInput.ray.setRay(ray);
		if (this.getUserData().isInside) {
			m_coneInput.minIndex = 1;
		} else {
			m_coneInput.minIndex = 0;
		}

		if (this.isSolid()) {
			Intersections.getSolidCone_T(m_coneInput,m_coneOutput,m_coneLocalVariables);
		} else {
			Intersections.getCone_T(m_coneInput,m_coneOutput,m_coneLocalVariables);
		}
		
		if (!m_coneOutput.hasIntersection) {
			return Float.NaN;
		} else {
			return m_coneOutput.t;
		}
	}
	

	public void getIntersectionDescription(IntersectionDescription desc) {
		Intersections.getCone_IntersectionDescription(m_coneInput,this.shader.getFlags(),
				desc,m_coneLocalVariables);	
		desc.setRTObject(this);
//		desc.setShader(this.getRTShader());
//		if (desc.intersectionIndex==0) {
//			desc.setMedium(this.getRTMedium());
//		} else {
//			desc.setMedium(null);
//		}
	}


	public BoundingVolume getBoundingVolume() {
		if (m_boundingVolume==null) {
			Point3f[] points = new Point3f[8];
			Vector3f min_values = 
				new Vector3f(Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
			Vector3f max_values = 
				new Vector3f(-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE);
			
			points[0] = new Point3f( 1.0f, 1.0f, 1.0f);
			points[1] = new Point3f(-1.0f, 1.0f, 1.0f);
			points[2] = new Point3f(-1.0f,-1.0f, 1.0f);
			points[3] = new Point3f( 1.0f,-1.0f, 1.0f);
			points[4] = new Point3f( 1.0f, 1.0f, 0.0f);
			points[5] = new Point3f(-1.0f, 1.0f, 0.0f);
			points[6] = new Point3f(-1.0f,-1.0f, 0.0f);
			points[7] = new Point3f( 1.0f,-1.0f, 0.0f);			
			for (int i=0;i<8;i++) {
				m_coneInput.transformation.transform(points[i]);
				if (points[i].x<min_values.x) { min_values.x = points[i].x; }
				if (points[i].y<min_values.y) { min_values.y = points[i].y; }
				if (points[i].z<min_values.z) { min_values.z = points[i].z; }
				if (points[i].x>max_values.x) { max_values.x = points[i].x; }
				if (points[i].y>max_values.y) { max_values.y = points[i].y; }
				if (points[i].z>max_values.z) { max_values.z = points[i].z; }
			}
 			m_boundingVolume = new BoundingBox(min_values,max_values);
		}
		return m_boundingVolume;
	}


//	public boolean isInsideBox(Vector3f minValues, Vector3f maxValues) {
//		if (this.getBoundingVolume()!=null) {
//			return this.getBoundingVolume().isInsideBox(minValues,maxValues);
//		} else {
//			return false;
//		}
//	}

	

}
