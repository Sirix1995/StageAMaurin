
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

import java.util.List;
import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.Cylinder;

public class XEGCylinder {
	
	static List<Float> color = null;
	static boolean noproperty = false;
	public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {
		//if a instance x = Cylinder() was importing, a non-null property list will be built, but the list size will be 0 
		 if (properties.size() == 0 )
			 noproperty = true;
		
		
		for (Property p : properties) {
			if (p.getName().equals("radius")) {
				((Cylinder) node).setRadius(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("length")) {
				((Cylinder) node).setLength(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("base_open")) {
				((Cylinder) node).setBaseOpen(Boolean.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("top_open")) {
				((Cylinder) node).setTopOpen(Boolean.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			
			/*else if (p.getName().equals("color")) {
				
				if (p.isSetRgb())
					color = p.getRgb();
				else if (p.isSetRgba())
					color = p.getRgba();
				if (color != null)
					((Cylinder) node).setShader(XEGNode.getShader(color));
				handledProperties.add(p);
			}*/
		}
		
	}
	
	public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
		
		if (noproperty == false) {
			
			Property xmlProperty = null;
			
			Float radius = ((Cylinder)node).getRadius();
			if (radius != null){
				xmlProperty = xmlNode.addNewProperty();
				xmlProperty.setName("radius");		
				xmlProperty.setValue(String.valueOf(radius));
			}
			
			Float length = ((Cylinder)node).getLength();
			if (length != null){
				xmlProperty = xmlNode.addNewProperty();
				xmlProperty.setName("length");
				xmlProperty.setValue(String.valueOf(length));
			}
	
			Boolean top_open = ((Cylinder)node).isTopOpen();
			if (top_open != null){
				xmlProperty = xmlNode.addNewProperty();
				xmlProperty.setName("top_open");
				xmlProperty.setValue(String.valueOf(top_open));
			}
			
			Boolean base_open = ((Cylinder)node).isBaseOpen();
			if (base_open != null){
				xmlProperty = xmlNode.addNewProperty();
				xmlProperty.setName("base_open");
				xmlProperty.setValue(String.valueOf(base_open));
			}
			
			/*if (color != null){
				Shader s = ((ShadedNull)node).getShader();
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
					xmlProperty = xmlNode.addNewProperty();
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
			}*/
		}
	}

}
