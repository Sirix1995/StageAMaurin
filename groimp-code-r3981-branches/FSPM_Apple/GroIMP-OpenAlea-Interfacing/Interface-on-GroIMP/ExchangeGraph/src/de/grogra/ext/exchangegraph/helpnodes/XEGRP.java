
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

import javax.vecmath.Point3d;
import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.impl.Node;
import de.grogra.turtle.RP;

public class XEGRP {

	public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {
		
		for (Property p : properties) {
			if (p.getName().equals("target")) {                
                Point3d p3d = new Point3d();
                p3d.x = ((Point3d)p).x;
                p3d.y = ((Point3d)p).y;
                p3d.z = ((Point3d)p).z;
				RP.target$FIELD.setObject(node, p3d);
				handledProperties.add(p);
			}else if(p.getName().equals("strength")) {
				RP.strength$FIELD.setFloat(node, Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
		}
		
	}
	
	public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
		Property xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("target");
		Point3d p = ((RP)node).target;
	    StringBuffer ps = new StringBuffer("");
	    ps.append(p.x); ps.append(",");
	    ps.append(p.y); ps.append(",");
	    ps.append(p.z);
		xmlProperty.setValue(ps.toString());
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("strength");
		xmlProperty.setValue(String.valueOf(RP.strength$FIELD.getFloat(node)));
	}
	
}
