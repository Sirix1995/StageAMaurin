
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
import de.grogra.turtle.F;

public class XEGF {

	public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {
		
		for (Property p : properties) {
			if (p.getName().equals("length")) {
				F.length$FIELD.setFloat(node, Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("diameter")) {
				F.diameter$FIELD.setFloat(node, Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("fcolor")) {
				F.color$FIELD.setInt(node, Integer.valueOf(p.getValue()));
				handledProperties.add(p);
			}
		}
		
	}
	
	public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
		Property xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("length");
		xmlProperty.setValue(String.valueOf(F.length$FIELD.getFloat(node)));

		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("diameter");
		xmlProperty.setValue(String.valueOf(F.diameter$FIELD.getFloat(node)));

		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("fcolor");
		xmlProperty.setValue(String.valueOf(F.color$FIELD.getInt(node)));
	}
	
}
