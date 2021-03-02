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


public class RTCylinder extends RaytracerLeaf implements RTObject {
	
	public final Intersections.CylinderInput m_cylinderInput = 
		new Intersections.CylinderInput(); 
	public final Intersections.CylinderLocalVariables m_cylinderLocalVariables =
		new Intersections.CylinderLocalVariables();
	public final Intersections.ObjectOutput m_cylinderOutput =
		new Intersections.ObjectOutput();
	
	private final Vector3f m_vec1 = new Vector3f();
	private final Vector3f m_vec2 = new Vector3f();
	private final Vector3f m_vec3 = new Vector3f();
	private final Matrix4f m_axisTransform = new Matrix4f();
	private boolean        m_shadeable = true;
	
	private BoundingBox    m_boundingVolume = null;
 	
	
	public RTCylinder(Object object,boolean asNode,long pathId,
			float radius,Vector3f axis,boolean openTop,boolean openBottom) {
		super(object, asNode, pathId);
		
		if (radius==0.0f) {
			m_shadeable = false;
			return;
		}
		
		m_cylinderInput.top_normal.normalize(axis);
		m_cylinderInput.open_top    = false;
		m_cylinderInput.open_bottom = false;
		
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
		
		m_cylinderInput.top_tangenteU.set(m_vec1);
		m_cylinderInput.top_tangenteV.set(m_vec2);	
	}
	
	
	public boolean isConvex() {
		return (!m_cylinderInput.open_top&&!m_cylinderInput.open_bottom);
	}
	
	
	public boolean isShadeable() {
		return m_shadeable;
	}
	
	
	public boolean isSolid() {
		return (!m_cylinderInput.open_top&&!m_cylinderInput.open_bottom);
	}
	
	
	public void setTransformation(Matrix4f mat) {
		super.setTransformation(mat);
		if (m_shadeable) {
			mat.transform(m_cylinderInput.top_normal);
			m_cylinderInput.transformation.mul(mat,m_axisTransform);
			m_cylinderInput.invers_transformation.invert(m_cylinderInput.transformation);
			this.setShader(this.shader);
			if (this.interior!=null) {
				this.setMedium(this.interior);
			}
//			if ((this.shader.getFlags() & Intersections.EVALUATE_TRANSPARENCY)!=0) {
//				m_cylinderInput.transparencyShader = this.getRTShader();
//			} else {
//				m_cylinderInput.transparencyShader = null;
//			}
		}
		m_boundingVolume = null;
	}
	

	public float getDistance(Ray ray,RayContext context) {
		m_cylinderInput.ray.setRay(ray);
		if (this.getUserData().isInside) {
			m_cylinderInput.minIndex = 1;
		} else {
			m_cylinderInput.minIndex = 0;
		}
		
		if (this.isSolid()) {
			Intersections.getSolidCylinder_T(m_cylinderInput,m_cylinderOutput,m_cylinderLocalVariables);
		} else {
			Intersections.getCylinder_T(m_cylinderInput,m_cylinderOutput,m_cylinderLocalVariables);
		}
				
		if (!m_cylinderOutput.hasIntersection) {
			return Float.NaN;
		} else {
			return m_cylinderOutput.t;
		}
	}

	
	public void getIntersectionDescription(IntersectionDescription desc) {
		Intersections.getCylinder_IntersectionDescription(m_cylinderInput,this.shader.getFlags(),
				desc,m_cylinderLocalVariables);	
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
			
			//System.out.println("max:"+max_values);
			
			points[0] = new Point3f( 1.0f, 1.0f, 1.0f);
			points[1] = new Point3f(-1.0f, 1.0f, 1.0f);
			points[2] = new Point3f(-1.0f,-1.0f, 1.0f);
			points[3] = new Point3f( 1.0f,-1.0f, 1.0f);
			points[4] = new Point3f( 1.0f, 1.0f, 0.0f);
			points[5] = new Point3f(-1.0f, 1.0f, 0.0f);
			points[6] = new Point3f(-1.0f,-1.0f, 0.0f);
			points[7] = new Point3f( 1.0f,-1.0f, 0.0f);			
			for (int i=0;i<8;i++) {
				m_cylinderInput.transformation.transform(points[i]);
				if (points[i].x<min_values.x) { min_values.x = points[i].x; }
				if (points[i].y<min_values.y) { min_values.y = points[i].y; }
				if (points[i].z<min_values.z) { min_values.z = points[i].z; }
				if (points[i].x>max_values.x) { max_values.x = points[i].x; }
				if (points[i].y>max_values.y) { max_values.y = points[i].y; }
				if (points[i].z>max_values.z) { max_values.z = points[i].z; }
			}
			//System.out.println("min:"+min_values);
			//System.out.println("max:"+max_values);
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
	
	/*
	public void exportToVRML(BufferedWriter writer) {
		try {
			
			writer.write("  Transform {");writer.newLine();
			writer.write("    children [");writer.newLine();
			writer.write("      Shape {");writer.newLine();
			writer.write("        appearance Appearance {");writer.newLine();
			writer.write("          material Material {");writer.newLine();
			writer.write("            transparency 0.5");writer.newLine();
//			writer.write("            diffuseColor 0.5 0.5 0.5");writer.newLine();
//			writer.write("            emissiveColor 0.5 0.5 0.5");writer.newLine();
			writer.write("          }");writer.newLine();
			writer.write("        }");writer.newLine();
			writer.write("        geometry Cylinder {");writer.newLine();
			writer.write("          radius "+m_radius);writer.newLine();
			writer.write("          height "+m_vec3.length());writer.newLine();
			writer.write("        }");writer.newLine();
			writer.write("      }");writer.newLine();
			writer.write("    ]");writer.newLine();
			writer.write("    translation "+
					(m_cylinderInput.transformation.m03)+" "+
					(m_cylinderInput.transformation.m13)+" "+
					(m_cylinderInput.transformation.m23));writer.newLine();
			writer.write("  }");writer.newLine();
			
		} catch (Exception e) {
			System.err.println(e);
		}		
	}
	*/

	

}
