
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

import java.util.Arrays;
import java.util.List;
import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.impl.Node;
import de.grogra.math.BezierSurface;
import de.grogra.imp3d.objects.NURBSSurface;

public class XEGBezierSurface {
	
	public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {
		
		/*for (Property p : properties) {
			if (p.getName().equals("data")) {
				//((BezierSurface) node).setData(Float.valueOf(p.getValue()));
				List<String> slist = Arrays.asList(p.getValue().split(","));
				float[] datalist = new float[slist.size()];
				int[] indices = new int[slist.size()];
				for (int i=0; i< datalist.length; i++){
					BezierSurface.data$FIELD.setFloat(node, indices, datalist[i], node.getTransaction(false));
				}
				
				handledProperties.add(p);
			}
			else if (p.getName().equals("uCount")) {
				BezierSurface.uCount$FIELD.setInt(node, Integer.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("dimension")) {
				BezierSurface.dimension$FIELD.setInt(node, Integer.valueOf(p.getValue()));
				handledProperties.add(p);
			}
		}*/
		
		
		float[] datalist;
		int dimension = 0, uCount=0;
		
		for (Property p : properties) {
			if (p.getName().equals("uCount")) {
				uCount = Integer.valueOf(p.getValue());
				handledProperties.add(p);
			}
			else if (p.getName().equals("dimension")) {
				dimension = Integer.valueOf(p.getValue());
				handledProperties.add(p);
			}

		}
		
		for (Property p : properties) {		
			if (p.getName().equals("data")) {
				List<String> slist = Arrays.asList(p.getValue().split(","));
				datalist = new float[slist.size()];
				for(int i=0; i<slist.size(); i++)
					datalist[i] = Float.parseFloat(slist.get(i));
				((NURBSSurface) node).setSurface(new BezierSurface(datalist, dimension, uCount));
				handledProperties.add(p);
			}
		}
		
	}

	public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
		
		Property xmlProperty = null;
		
		BezierSurface bzs = (BezierSurface)((NURBSSurface) node).getSurface();
		Integer uCount = bzs.getUCount();
		if (uCount != null){
			xmlProperty = xmlNode.addNewProperty();
			xmlProperty.setName("uCount");
			xmlProperty.setValue(String.valueOf(bzs.getUCount()));
		}
		
		Integer dimension = bzs.getDimension();
		if (dimension != null){
			xmlProperty = xmlNode.addNewProperty();
			xmlProperty.setName("dimension");
			xmlProperty.setValue(String.valueOf(dimension));
		}
		
		float[] flist = bzs.getData();
		if (flist != null){
			StringBuffer ps = new StringBuffer("");
		
			for(int i=0; i<flist.length; i++){
				ps.append(flist[i]);
				if (i != flist.length - 1)
					ps.append(",");				
			}	
			xmlProperty = xmlNode.addNewProperty();
			xmlProperty.setName("data");
			xmlProperty.setValue(String.valueOf(ps));
		}
		
	}
	
}
