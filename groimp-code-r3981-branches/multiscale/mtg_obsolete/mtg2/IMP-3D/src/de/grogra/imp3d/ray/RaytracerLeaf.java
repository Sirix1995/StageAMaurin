
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

import javax.vecmath.Matrix4f;

import de.grogra.imp3d.shading.Interior;
import de.grogra.imp3d.shading.Shader;
import de.grogra.ray.RTObject.RTObjectUserData;
import de.grogra.ray.shader.RTMedium;
import de.grogra.ray.shader.RTShader;


public class RaytracerLeaf extends de.grogra.imp3d.objects.SceneTreeWithShader.Leaf
{
	
	private Matrix4f m_transformation;
	private GroIMPShader m_shader = null;
	private GroIMPMedium m_medium = null;
	
	private RTObjectUserData m_userData = null;
	
	
	public RaytracerLeaf (Object object, boolean asNode, long pathId)
	{
		super (object, asNode, pathId);		
	}
	
	public Matrix4f getTransformation() { return m_transformation; }
	public void setTransformation(Matrix4f mat) { m_transformation = mat; }
	
	// TODO remove this
	public RTShader getRTShader() { return m_shader; }
	public RTMedium getRTMedium() { return m_medium; }
	
	public RTMedium getMedium() { return m_medium; }
	public RTShader getShader() { return m_shader; }
	
//	public void createRTShader(Shader shader) {
//		//System.out.println("createRTShader");
//		m_shader = new GroIMPShader(shader);
//	}
	
	
	public RTObjectUserData getUserData() {
		if (m_userData==null) {
			m_userData = new RTObjectUserData();
		}
		return m_userData;
	}
	
	
	public void setShader(Shader shader) {
		if (m_shader==null) {
			m_shader = new GroIMPShader(shader);
		} else {
			m_shader.setShader(shader);
		}
	}
	
	
	public void setMedium(Interior interior) {
		
		if (m_medium==null) {
			m_medium = new GroIMPMedium(interior);
		} else {
			m_medium.setInterior(interior);
		}
//		System.err.println("setMedium:"+m_medium+" "+this);
	} 
		
}
