
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

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
import de.grogra.ray.intersection.Intersections;
import de.grogra.ray.shader.RTMedium;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;


public class RTSphere extends RaytracerLeaf implements RTObject, ExportableToVRML
{
	private Intersections.SphereInput m_sphereInput 
		= new Intersections.SphereInput();
	private Intersections.ObjectOutput m_sphereOutput 
		= new Intersections.ObjectOutput();
	private Intersections.SphereLocalVariables m_sphereLocalVariables =
		new Intersections.SphereLocalVariables();
	
	private BoundingVolume m_boundingVolume = null;
	

	public RTSphere (Object object, boolean asNode, long pathId, float radius) {
		super (object, asNode, pathId);
		setRadius(radius);
	}
	

	public RTMedium getMedium() {
		return this.getRTMedium();
	}
		
	
	public boolean isConvex() { return true; }
	
	
	public boolean isShadeable() {
		return true;
	}
	
	
	public boolean isSolid() {
		return true;
	}
	
	
	public float getRadius() { return m_sphereInput.radius; }
	
	
	public void setRadius(double value) {
		m_sphereInput.radius = (float)value;
		m_sphereInput.squareRadius = (float)(value*value);
	}
	
	
	public void setTransformation(Matrix4f mat) {
		super.setTransformation(mat);
		m_sphereInput.transformation.set(mat);
		m_sphereInput.invers_transformation.invert(mat);
		this.setShader(this.shader);
		if (this.interior!=null) {
			this.setMedium(this.interior);
		}
		m_boundingVolume=null;
	}
	
	
	public float getDistance(Ray ray,RayContext context) {
		m_sphereInput.ray.setRay(ray);
		if (this.getUserData().isInside) {
			m_sphereInput.minIndex = 1;
		} else {
			m_sphereInput.minIndex = 0;
		}
		
		Intersections.getSphere_T(m_sphereInput,m_sphereOutput,m_sphereLocalVariables);
		if (!m_sphereOutput.hasIntersection) {
			return Float.NaN;
		} else {
			return m_sphereOutput.t;
		}
	}

	
	public void getIntersectionDescription(IntersectionDescription desc) {
		Intersections.getSphere_IntersectionDescription(m_sphereInput,this.shader.getFlags(),
				desc,m_sphereLocalVariables);	
		desc.setRTObject(this);
	}

	
	public BoundingVolume getBoundingVolume() {
		if (m_boundingVolume==null) {
			Point3f[] points = new Point3f[8];
			Vector3f min_values = 
				new Vector3f(Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
			Vector3f max_values = 
				new Vector3f(-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE);
			
			float r = m_sphereInput.radius+0.001f;
			points[0] = new Point3f( r, r, r);
			points[1] = new Point3f(-r, r, r);
			points[2] = new Point3f(-r,-r, r);
			points[3] = new Point3f( r,-r, r);
			points[4] = new Point3f( r, r,-r);
			points[5] = new Point3f(-r, r,-r);
			points[6] = new Point3f(-r,-r,-r);
			points[7] = new Point3f( r,-r,-r);			
			for (int i=0;i<8;i++) {
				m_sphereInput.transformation.transform(points[i]);
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


	public void exportToVRML(BufferedWriter writer) {
		try {
			
			writer.write("  Transform {");writer.newLine();
			writer.write("    children [");writer.newLine();
			writer.write("      Shape {");writer.newLine();
			writer.write("        appearance Appearance {");writer.newLine();
			writer.write("          material Material {");writer.newLine();
			writer.write("            transparency 0.5");writer.newLine();
			writer.write("          }");writer.newLine();
			writer.write("        }");writer.newLine();
			writer.write("        geometry Sphere {");writer.newLine();
			writer.write("          radius "+m_sphereInput.radius);writer.newLine();
			writer.write("        }");writer.newLine();
			writer.write("      }");writer.newLine();
			writer.write("    ]");writer.newLine();
			writer.write("    translation "+
					m_sphereInput.transformation.m03+" "+
					m_sphereInput.transformation.m13+" "+
					m_sphereInput.transformation.m23);writer.newLine();
			writer.write("  }");writer.newLine();
			
		} catch (Exception e) {
			System.err.println(e);
		}		
	}

}
