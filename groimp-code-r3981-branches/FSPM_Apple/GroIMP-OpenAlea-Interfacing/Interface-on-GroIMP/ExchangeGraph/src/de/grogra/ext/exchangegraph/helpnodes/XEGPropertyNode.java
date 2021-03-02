
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

import java.util.HashMap;
import java.util.List;

import de.grogra.ext.exchangegraph.graphnodes.Property;
import de.grogra.ext.exchangegraph.graphnodes.PropertyNodeImpl;
import de.grogra.graph.impl.Node;

public class XEGPropertyNode {

	static HashMap<String, String> promap = new HashMap<String, String>();
	
	public static void handleImportProperties(Node node, List<de.grogra.ext.exchangegraph.xmlbeans.Property> properties, 
			List<de.grogra.ext.exchangegraph.xmlbeans.Property> handledProperties) {
		//System.out.print("pass handle import");
		((PropertyNodeImpl) node).setNodePropertiesFromXEGNode(properties);
		for (de.grogra.ext.exchangegraph.xmlbeans.Property p : properties){
			//System.err.println( p.getName() + ";" +p.getValue());
			//promap.put(p.getName(), p.getValue());
			handledProperties.add(p);
		}
		
	}

	public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
		//System.out.print("pass handle Export");

		//Set<Entry<String, String>> sets = promap.entrySet();
		List<Property> plist = ((PropertyNodeImpl) node).getNodeProperties();
		for (Property p : plist) {
			de.grogra.ext.exchangegraph.xmlbeans.Property xmlProperty = xmlNode.addNewProperty();
			xmlProperty.setName(p.getName());
			xmlProperty.setValue(p.getValue());
			//System.out.println(entry.getKey() + ", ");
			//System.out.println(entry.getValue());
		}

	}
	
}
