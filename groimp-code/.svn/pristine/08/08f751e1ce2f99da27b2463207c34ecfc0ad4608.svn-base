
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

import javax.vecmath.Vector3d;

import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.impl.Node;
import de.grogra.turtle.RD;

public class XEGRD {

	public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {
		
		for (Property p : properties) {
			if (p.getName().equals("direction")) {
				/*int size = p.getListOfFloat().size();
                double[] t = new double[size];
                for (int i=0; i<size; i++){
                    t[i] = (double) p.getListOfFloat().get(i);
                }*/
				
				Vector3d v3d = new Vector3d();
				v3d.x = ((Vector3d)p).x;
				v3d.y = ((Vector3d)p).y;
				v3d.z = ((Vector3d)p).z;
                //((RD) node).direction = new Vector3d(t);
				RD.direction$FIELD.setObject(node, v3d);
				handledProperties.add(p);
			}else if(p.getName().equals("strength")) {
				RD.strength$FIELD.setFloat(node, Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
		}
		
	}
	
	public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
		Property xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("direction");
		Vector3d v = ((RD)node).direction;
	    StringBuffer vs = new StringBuffer("");
	    vs.append(v.x); vs.append(",");
	    vs.append(v.y); vs.append(",");
	    vs.append(v.z);
		xmlProperty.setValue(vs.toString());
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("strength");
		xmlProperty.setValue(String.valueOf(RD.strength$FIELD.getFloat(node)));
	}
	
}
