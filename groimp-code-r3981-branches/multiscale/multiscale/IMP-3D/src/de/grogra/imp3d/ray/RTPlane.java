package de.grogra.imp3d.ray;

import javax.vecmath.Matrix4f;

import de.grogra.ray.RTObject;
import de.grogra.ray.intersection.BoundingVolume;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;
import de.grogra.ray.intersection.Intersections;


public class RTPlane extends RaytracerLeaf implements RTObject {

	private Intersections.PlaneInput m_planeInput = 
		new Intersections.PlaneInput();
	private Intersections.ObjectOutput m_planeOutput =
		new Intersections.ObjectOutput();
	private Intersections.PlaneLocalVariables m_planeLocalVariables =
		new Intersections.PlaneLocalVariables();
	
	
	public RTPlane(Object object, boolean asNode, long pathId)
	{
		super (object, asNode, pathId);
	}
	
	
	public void setTransformation(Matrix4f mat) {
		super.setTransformation(mat);
		m_planeInput.transformation.set(mat);
		m_planeInput.invers_transformation.invert(mat);
		m_planeInput.normal.set(0.0f,0.0f,1.0f);
		mat.transform(m_planeInput.normal);
		m_planeInput.normal.normalize();
		m_planeInput.tangenteU.set(1.0f,0.0f,0.0f);
		mat.transform(m_planeInput.tangenteU);
		m_planeInput.tangenteU.normalize();
		m_planeInput.tangenteV.set(0.0f,1.0f,0.0f);
		mat.transform(m_planeInput.tangenteV);
		m_planeInput.tangenteV.normalize();
		//m_planeInput.point.set(mat.m03,mat.m13,mat.m23);
		this.setShader(this.shader);
		if ((this.shader.getFlags() & Intersections.EVALUATE_TRANSPARENCY)!=0) {
			m_planeInput.transparencyShader = this.getRTShader();
		} else {
			m_planeInput.transparencyShader = null;
		}
	}
	
	
	public boolean isConvex() {
		return false;
	}
	
	
	public boolean isShadeable() {
		return true;
	}
	
	
	public boolean isSolid() {
		return false;
	}
	

	public float getDistance(Ray ray,RayContext context) {
		m_planeInput.ray.setRay(ray);
		//m_planeInput.transparencyShader = this.getRTShader();
		
		Intersections.getPlane_T(m_planeInput,m_planeOutput,m_planeLocalVariables);
		if (!m_planeOutput.hasIntersection) {
			return Float.NaN;
		} else {
			return m_planeOutput.t;
		}
	}

	
	public void getIntersectionDescription(IntersectionDescription desc) {
		Intersections.getPlane_IntersectionDescription(m_planeInput,this.shader.getFlags(),
				desc,m_planeLocalVariables);	
		desc.setRTObject(this);
//		desc.setShader(this.getRTShader());
	}


	public BoundingVolume getBoundingVolume() {
		return null;
	}


//	public boolean isInsideBox(Vector3f minValues, Vector3f maxValues) {
//		//System.out.println("isInside?");
//		
//		Point3f[] corners = new Point3f[8];
//		corners[0] = new Point3f(minValues.x,minValues.y,minValues.z);
//		corners[1] = new Point3f(minValues.x,minValues.y,maxValues.z);
//		corners[2] = new Point3f(minValues.x,maxValues.y,minValues.z);
//		corners[3] = new Point3f(minValues.x,maxValues.y,maxValues.z);
//		corners[4] = new Point3f(maxValues.x,minValues.y,minValues.z);
//		corners[5] = new Point3f(maxValues.x,minValues.y,maxValues.z);
//		corners[6] = new Point3f(maxValues.x,maxValues.y,minValues.z);
//		corners[7] = new Point3f(maxValues.x,maxValues.y,maxValues.z);
//		int pos = 0;
//		//int neg = 0;
//		for (int i=0;i<8;i++) {
//			m_planeInput.invers_transformation.transform(corners[i]);
//			if (corners[i].z>=0) { pos++; }
//		}
//		
//		if ((pos==0) || pos==8) {
//			return false;
//		} else {
//			return true;
//		}
//	}

}
