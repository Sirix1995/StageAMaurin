
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
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.GlobalTransformation;
import de.grogra.turtle.RN;
import de.grogra.vecmath.Matrix34d;

public class XEGRN {

	public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {
		
		for (Property p : properties) {
			if (p.getName().equals("target")) {
                RN.target$FIELD.setObject(node, ((Node) p));
				handledProperties.add(p);
			}else if(p.getName().equals("strength")) {
				RN.strength$FIELD.setFloat(node, Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
		}
		
	}
	
	public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
		Property xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("target");
		Node target = ((RN)node).getTarget();
		GraphState gs = ((RN)node).getCurrentGraphState();
		Matrix34d t = GlobalTransformation.get(target, true, gs, true);
	    StringBuffer ps = new StringBuffer("");
	    ps.append(t.m03); ps.append(",");
	    ps.append(t.m13); ps.append(",");
	    ps.append(t.m23);
		xmlProperty.setValue(ps.toString());
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("strength");
		xmlProperty.setValue(String.valueOf(RN.strength$FIELD.getFloat(node)));
	}
	
}
