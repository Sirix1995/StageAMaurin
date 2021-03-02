package de.grogra.imp3d.ray;

import java.io.BufferedWriter;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import de.grogra.ray.RTObject;
import de.grogra.ray.debug3d.ExportableToVRML;
import de.grogra.ray.intersection.BoundingBox;
import de.grogra.ray.intersection.BoundingVolume;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;
import de.grogra.ray.intersection.Intersections;


public class RTBox extends RaytracerLeaf implements RTObject, ExportableToVRML {

	public final Intersections.BoxInput m_boxInput = 
		new Intersections.BoxInput(); 
	public final Intersections.BoxLocalVariables m_boxLocalVariables =
		new Intersections.BoxLocalVariables();
	public final Intersections.ObjectOutput m_boxOutput =
		new Intersections.ObjectOutput();
	
	private BoundingBox m_boundingVolume = null;
	
	
	public RTBox(Object object, boolean asNode, long pathId,
			float expansionX, float expansionY, float expansionZ) {
		super(object, asNode, pathId);
		m_boxInput.expansion_x = expansionX;
		m_boxInput.expansion_y = expansionY;
		m_boxInput.expansion_z = expansionZ;
		
		// bugfix because interpretation of box size has changed
		m_boxInput.expansion_x*=0.5f;
		m_boxInput.expansion_y*=0.5f;
		m_boxInput.expansion_z*=0.5f;
		
//		System.out.println("BOX:"+expansionX+" "+expansionY+" "+expansionZ);
	}
	
	
	public boolean isConvex() {
		return true;
	}
	
	
	public boolean isShadeable() {
		return true;
	}
	
	
	public boolean isSolid() {
		return true;
	}
	
	
	public void setTransformation(Matrix4f mat) {
		
		// bugfix because fix point of box has changed
//		mat.m23+=m_boxInput.expansion_z;
		
		Vector3f z_axis = new Vector3f(0.0f,0.0f,m_boxInput.expansion_z);
		mat.transform(z_axis);
		mat.m03+=z_axis.x;
		mat.m13+=z_axis.y;
		mat.m23+=z_axis.z;
		
		super.setTransformation(mat);
		m_boxInput.transformation.set(mat);
		m_boxInput.invers_transformation.invert(mat);
		m_boxInput.top_normal.set(0.0f,0.0f,1.0f);
		m_boxInput.front_normal.set(0.0f,-1.0f,0.0f);
		m_boxInput.right_normal.set(1.0f,0.0f,0.0f);
		mat.transform(m_boxInput.top_normal);
		mat.transform(m_boxInput.front_normal);
		mat.transform(m_boxInput.right_normal);
		this.setShader(this.shader);
		if (this.interior!=null) {
			this.setMedium(this.interior);
		}
//		if ((this.shader.getFlags() & Intersections.EVALUATE_TRANSPARENCY)!=0) {
//			m_boxInput.transparencyShader = this.getRTShader();
//		} else {
//			m_boxInput.transparencyShader = null;
//		}
		m_boundingVolume = null;
	}

	
	public float getDistance(Ray ray,RayContext context) {
		m_boxInput.ray.setRay(ray);
		if (this.getUserData().isInside) {
			m_boxInput.minIndex = 1;
		} else {
			m_boxInput.minIndex = 0;
		}
		
		
		Intersections.getBox_T(m_boxInput,m_boxOutput,m_boxLocalVariables);
		if (!m_boxOutput.hasIntersection) {
			return Float.NaN;
		} else {
			return m_boxOutput.t;
		}
	}

	
	public void getIntersectionDescription(IntersectionDescription desc) {
		Intersections.getBox_IntersectionDescription(m_boxInput,this.shader.getFlags(),
				desc,m_boxLocalVariables);	
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
			
			float exp_x = m_boxInput.expansion_x + 0.01f;
			float exp_y = m_boxInput.expansion_y + 0.01f;
			float exp_z = m_boxInput.expansion_z + 0.01f;
			points[0] = new Point3f( exp_x, exp_y, exp_z);
			points[1] = new Point3f(-exp_x, exp_y, exp_z);
			points[2] = new Point3f(-exp_x,-exp_y, exp_z);
			points[3] = new Point3f( exp_x,-exp_y, exp_z);
			points[4] = new Point3f( exp_x, exp_y,-exp_z);
			points[5] = new Point3f(-exp_x, exp_y,-exp_z);
			points[6] = new Point3f(-exp_x,-exp_y,-exp_z);
			points[7] = new Point3f( exp_x,-exp_y,-exp_z);			
			for (int i=0;i<8;i++) {
				m_boxInput.transformation.transform(points[i]);
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
			writer.write("        geometry Box {");writer.newLine();
			writer.write("          size "+
					(m_boxInput.expansion_x*2.0f)+" "+
					(m_boxInput.expansion_y*2.0f)+" "+
					(m_boxInput.expansion_z*2.0f));writer.newLine();
			writer.write("        }");writer.newLine();
			writer.write("      }");writer.newLine();
			writer.write("    ]");writer.newLine();
			writer.write("    translation "+
					(m_boxInput.transformation.m03+0.0f*m_boxInput.expansion_x)+" "+
					(m_boxInput.transformation.m13-0.0f*m_boxInput.expansion_y)+" "+
					(m_boxInput.transformation.m23+0.0f*m_boxInput.expansion_z));writer.newLine();
			writer.write("  }");writer.newLine();
			
		} catch (Exception e) {
			System.err.println(e);
		}		
	}
	

}
