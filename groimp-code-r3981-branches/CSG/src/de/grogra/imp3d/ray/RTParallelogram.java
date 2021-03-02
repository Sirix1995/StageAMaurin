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


public class RTParallelogram extends RaytracerLeaf implements RTObject {

	private Intersections.ParallelogramInput m_parallelogramInput = 
		new Intersections.ParallelogramInput();
	private Intersections.ObjectOutput m_parallelogramOutput =
		new Intersections.ObjectOutput();
	private Intersections.ParallelogramLocalVariables m_parallelogramLocalVariables =
		new Intersections.ParallelogramLocalVariables();
	
	private final Vector3f m_vec1 = new Vector3f();
	private final Vector3f m_vec2 = new Vector3f();
	private final Vector3f m_vec3 = new Vector3f();
	private final Matrix4f m_axisTransform = new Matrix4f();
	private boolean        m_shadeable = true;
	private BoundingBox    m_boundingVolume = null;
	
	
	public RTParallelogram(Object object, boolean asNode, long pathId, 
			Vector3f axis1, Vector3f axis2) {
		super(object, asNode, pathId);
		
		
		
		m_vec1.set(axis1);
		m_vec2.set(axis2);
		m_vec3.cross(m_vec1,m_vec2);
		
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
		
		m_parallelogramInput.tangenteU.set(m_vec1);
		m_parallelogramInput.tangenteV.set(m_vec2);
		m_parallelogramInput.normal.set(m_vec3);
		
		//System.out.println("axis1:"+axis1);
		
//		m_parallelogramInput.axis1.set(axis1);
//		m_parallelogramInput.axis2.set(axis2);
//		m_parallelogramInput.localNormal.cross(axis1,axis2);
//		m_parallelogramInput.localNormal.normalize();
		//m_parallelogramInput.start1 = start1;
		//m_parallelogramInput.end1   = end1;
	}
	
	
	public boolean isConvex() {
		return false;
	}
	
	
	public boolean isShadeable() {
		return m_shadeable;
	}
	
	
	public boolean isSolid() {
		return false;
	}

	
	public void setTransformation(Matrix4f mat) {
		super.setTransformation(mat);
		
		mat.transform(m_parallelogramInput.normal);
		mat.transform(m_parallelogramInput.tangenteU);
		mat.transform(m_parallelogramInput.tangenteV);
		m_parallelogramInput.normal.normalize();
		m_parallelogramInput.tangenteU.normalize();
		m_parallelogramInput.tangenteV.normalize();
		
		m_parallelogramInput.transformation.mul(mat,m_axisTransform);
		m_parallelogramInput.invers_transformation.invert(m_parallelogramInput.transformation);
		
		this.setShader(this.shader);
		if ((this.shader.getFlags() & Intersections.EVALUATE_TRANSPARENCY)!=0) {
			m_parallelogramInput.transparencyShader = this.getRTShader();
		} else {
			m_parallelogramInput.transparencyShader = null;
		}
		
		
//		m_parallelogramInput.transformation.set(mat);
//		m_parallelogramInput.invers_transformation.invert(mat);
//		m_parallelogramInput.tangenteU.set(m_parallelogramInput.axis1);
//		m_parallelogramInput.tangenteV.set(m_parallelogramInput.axis2);
//		mat.transform(m_parallelogramInput.tangenteU);
//		mat.transform(m_parallelogramInput.tangenteV);
//		m_parallelogramInput.tangenteU.normalize();
//		m_parallelogramInput.tangenteV.normalize();
//		m_parallelogramInput.normal.cross(m_parallelogramInput.tangenteU,
//				m_parallelogramInput.tangenteV);
		/*
		m_parallelogramInput.normal.set(0.0f,1.0f,0.0f);
		mat.transform(m_parallelogramInput.normal);
		m_parallelogramInput.normal.normalize();
		m_parallelogramInput.tangenteU.set(1.0f,0.0f,0.0f);
		mat.transform(m_parallelogramInput.tangenteU);
		m_parallelogramInput.tangenteU.normalize();
		m_parallelogramInput.tangenteV.set(0.0f,0.0f,1.0f);
		mat.transform(m_parallelogramInput.tangenteV);
		m_parallelogramInput.tangenteV.normalize();
		*/
		//this.setShader(this.shader);
	}
	
	
	public float getDistance(Ray ray,RayContext context) {
		m_parallelogramInput.ray.setRay(ray);
		//m_parallelogramInput.transparencyShader = this.getRTShader();
		
		Intersections.getParallelogram_T(
				m_parallelogramInput,
				m_parallelogramOutput,
				m_parallelogramLocalVariables);
		if (!m_parallelogramOutput.hasIntersection) {
			return Float.NaN;
		} else {
			return m_parallelogramOutput.t;
		}
	}

	
	public void getIntersectionDescription(IntersectionDescription desc) {
		Intersections.getParallelogram_IntersectionDescription(
				m_parallelogramInput,this.shader.getFlags(),
				desc,m_parallelogramLocalVariables);	
		desc.setRTObject(this);
//		desc.setShader(this.getRTShader());
	}


	public BoundingVolume getBoundingVolume() {
		if (m_boundingVolume==null) {
			Point3f[] points = new Point3f[4];
			Vector3f min_values = 
				new Vector3f(Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
			Vector3f max_values = 
				new Vector3f(-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE);
						
			points[0] = new Point3f( 1.0f, 1.0f, 0.0f);
			points[1] = new Point3f( 0.0f, 1.0f, 0.0f);
			points[2] = new Point3f( 0.0f, -1.0f, 0.0f);
			points[3] = new Point3f( 1.0f, -1.0f, 0.0f);			
			for (int i=0;i<4;i++) {
				m_parallelogramInput.transformation.transform(points[i]);
				if (points[i].x<min_values.x) { min_values.x = points[i].x; }
				if (points[i].y<min_values.y) { min_values.y = points[i].y; }
				if (points[i].z<min_values.z) { min_values.z = points[i].z; }
				if (points[i].x>max_values.x) { max_values.x = points[i].x; }
				if (points[i].y>max_values.y) { max_values.y = points[i].y; }
				if (points[i].z>max_values.z) { max_values.z = points[i].z; }
			}
			
			float space = 0.01f;
			min_values.x -= space;
			min_values.y -= space;
			min_values.z -= space;
			max_values.x += space;
			max_values.y += space;
			max_values.z += space;
			
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
