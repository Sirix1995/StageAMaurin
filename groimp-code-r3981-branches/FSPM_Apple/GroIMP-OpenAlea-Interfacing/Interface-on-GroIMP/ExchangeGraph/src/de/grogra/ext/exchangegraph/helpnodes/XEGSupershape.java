
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package de.grogra.ext.exchangegraph.helpnodes;

import java.util.List;

import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.Supershape;

public class XEGSupershape {

    public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {

    	for (Property p : properties) {
			if (p.getName().equals("a")) {
				((Supershape) node).setA(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("b")) {
				((Supershape) node).setB(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("m1")) {
				((Supershape) node).setM1(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("m2")) {
				((Supershape) node).setM2(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("n11")) {
				((Supershape) node).setN11(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("n12")) {
				((Supershape) node).setN12(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("n13")) {
				((Supershape) node).setN13(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("n21")) {
				((Supershape) node).setN21(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("n22")) {
				((Supershape) node).setN22(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("n23")) {
				((Supershape) node).setN23(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
		}
    	
    }

    public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
    	Property xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("a");
		xmlProperty.setValue(String.valueOf(((Supershape)node).getA()));
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("b");
		xmlProperty.setValue(String.valueOf(((Supershape)node).getB()));
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("m1");
		xmlProperty.setValue(String.valueOf(((Supershape)node).getM1()));
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("m2");
		xmlProperty.setValue(String.valueOf(((Supershape)node).getM2()));
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("n11");
		xmlProperty.setValue(String.valueOf(((Supershape)node).getN11()));
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("n12");
		xmlProperty.setValue(String.valueOf(((Supershape)node).getN12()));
    	
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("n13");
		xmlProperty.setValue(String.valueOf(((Supershape)node).getN13()));
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("n21");
		xmlProperty.setValue(String.valueOf(((Supershape)node).getN21()));
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("n22");
		xmlProperty.setValue(String.valueOf(((Supershape)node).getN22()));
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("n23");
		xmlProperty.setValue(String.valueOf(((Supershape)node).getN23()));
    }

}