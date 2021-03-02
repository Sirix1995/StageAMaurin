
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

package de.grogra.ext.exchangegraph.helpnodes;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4d;
import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.Null;
import de.grogra.imp3d.objects.ShadedNull;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.imp3d.shading.Shader;
import de.grogra.math.TMatrix4d;

public class XEGNode {
	
	@SuppressWarnings("unchecked")
	public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {
		
		if (properties!= null){
			for (Property p : properties) {
				if (p.getName().equals("transform")) {
					((Null) node).setTransform(getTransform(p.getMatrix()));
					handledProperties.add(p);
				}
				else if (p.getName().equals("color")) {
					List<Float> color = null;
					if (p.isSetRgb())
						color = p.getRgb();
					else if (p.isSetRgba())
						color = p.getRgba();
					if (color != null)
						((ShadedNull) node).setShader(getShader(color));
					handledProperties.add(p);
				}
			}
		}
		
	}
    
    /**
     * Puts all elements of the matrix in a list of size 16. The elements
     * are listet in a row.
     * @return list of elements
     */
    public static final List<Double> getElementsOfMatrix(Matrix4d m) {
    	List<Double> result = new ArrayList<Double>(16);
    	result.add(m.m00);result.add(m.m01);result.add(m.m02);result.add(m.m03);
    	result.add(m.m10);result.add(m.m11);result.add(m.m12);result.add(m.m13);
    	result.add(m.m20);result.add(m.m21);result.add(m.m22);result.add(m.m23);
    	result.add(m.m30);result.add(m.m31);result.add(m.m32);result.add(m.m33);
    	return result;
    }

	public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
		
		// write transformation
		if (node instanceof Null) {
			Null nn = (Null) node;
			if (nn.getTransform() != null) {
				TMatrix4d in = new TMatrix4d();
				TMatrix4d out = new TMatrix4d();
				nn.getTransform().transform(in, out);
				Property xmlProperty = xmlNode.addNewProperty();
				xmlProperty.setName("transform");
				xmlProperty.setMatrix(getElementsOfMatrix(out));
			}
		}
		
		// write color
		if (node instanceof ShadedNull) {
			//get the shader in node.(setShader(s))
			/*Shader shader4ShadedNull = null;
			if (node instanceof F0) {
				shader4ShadedNull = ((F0)node).getShader();
			} else if(node instanceof F){
				shader4ShadedNull = ((F)node).getShader();	
			} else {
				shader4ShadedNull = ((ShadedNull)node).getShader();
			}
			if (shader4ShadedNull!=null){
				ArrayList<Float> cf = convertShader2Rgb(shader4ShadedNull);
				for (Float i: cf){
				}
			}*/
			// find the correct shader (also the inherited)
			//GraphState gs = Workbench.current().getRegistry().getProjectGraph().getMainState();
			//Shader s = (Shader) gs.getObject(node, true, Attributes.SHADER);
			Shader s = ((ShadedNull)node).getShader();
			//Node parent = node.findAdjacent(true, false, Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE);
			
			/*while (s == null) {
				if (parent == null)
					break;
				if (parent instanceof ShadedNull)
					s = (Shader) gs.getObject(parent, true, Attributes.SHADER);
				parent = parent.findAdjacent(true, false, Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE);
			}*/
			
			if (s != null) {
				int color = ((Shader) s).getAverageColor();
				int alpha = (color >> 24) & 255;
				boolean needAlpha = false;
				float a = 0;
				if (alpha != 0xFF) {
					needAlpha = true;
					a = alpha * (1f / 255f);
				}
				float r = ((color >> 16) & 255) * (1f / 255f);
				float g = ((color >> 8) & 255) * (1f / 255f);
				float b = (color & 255) * (1f / 255f);
				Property xmlProperty = xmlNode.addNewProperty();
				xmlProperty.setName("color");
				ArrayList<Float> rgb = new ArrayList<Float>(needAlpha ? 4 : 3);
				rgb.add(r); rgb.add(g); rgb.add(b); 
				if (needAlpha) {
					rgb.add(a);
					xmlProperty.setRgba(rgb);
				}
				else
					xmlProperty.setRgb(rgb);
			}
		}
		
	}
	
	public static ArrayList<Float> convertShader2Rgb(Shader s){
		int color = ((Shader) s).getAverageColor();
		int alpha = (color >> 24) & 255;
		boolean needAlpha = false;
		float a = 0;
		if (alpha != 0xFF) {
			needAlpha = true;
			a = alpha * (1f / 255f);
		}
		float r = ((color >> 16) & 255) * (1f / 255f);
		float g = ((color >> 8) & 255) * (1f / 255f);
		float b = (color & 255) * (1f / 255f);

		ArrayList<Float> rgb = new ArrayList<Float>(needAlpha ? 4 : 3);
		rgb.add(r); rgb.add(g); rgb.add(b); 
		if (needAlpha) {
			rgb.add(a);			
		}
		return rgb;
	}
	
	private static Matrix4d getTransform(List<Float> matrix) {
		Matrix4d m = new Matrix4d();
		Float values[] = new Float[16];
		matrix.toArray(values);
		double f[] = new double[16];
		for (int i = 0; i < 16; i++)
		{
			f[i] = values[i];
		}
		m.set(f);
		return m;
	}
	
	protected static RGBAShader getShader(List<Float> rgb) {
		float r = rgb.get(0).floatValue();
		float g = rgb.get(1).floatValue();
		float b = rgb.get(2).floatValue();
		float a = rgb.size() == 4 ? rgb.get(3).floatValue() : 1;
		return new RGBAShader(r, g, b, a);
	}

}
