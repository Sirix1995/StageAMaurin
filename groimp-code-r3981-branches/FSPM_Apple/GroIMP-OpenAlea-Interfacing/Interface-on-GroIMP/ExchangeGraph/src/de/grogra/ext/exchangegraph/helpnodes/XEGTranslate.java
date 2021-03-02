
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
import de.grogra.turtle.Translate;

public class XEGTranslate {

	public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {
		
		for (Property p : properties) {
			if (p.getName().equals("translateX")) {
				Translate.translateX$FIELD.setFloat(node, Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("translateY")) {
				Translate.translateY$FIELD.setFloat(node, Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("translateZ")) {
				Translate.translateZ$FIELD.setFloat(node, Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
		}
		
	}
	
	public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
		Property xmlProperty = null;
		
		Float translateX = Translate.translateX$FIELD.getFloat(node);
		if (translateX != null){
			xmlProperty = xmlNode.addNewProperty();
			xmlProperty.setName("translateX");
			xmlProperty.setValue(String.valueOf(translateX));
		}

		Float translateY = Translate.translateY$FIELD.getFloat(node);
		if (translateY != null){
			xmlProperty = xmlNode.addNewProperty();
			xmlProperty.setName("translateY");
			xmlProperty.setValue(String.valueOf(Translate.translateY$FIELD.getFloat(node)));
		}

		Float translateZ = Translate.translateZ$FIELD.getFloat(node);
		if (translateZ != null){
			xmlProperty = xmlNode.addNewProperty();
			xmlProperty.setName("translateZ");
			xmlProperty.setValue(String.valueOf(Translate.translateZ$FIELD.getFloat(node)));
		}
	}
	
}
