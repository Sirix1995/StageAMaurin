

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
import javax.vecmath.Color3f;

import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.PointCloud;

public class XEGPointCloud {

    public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {

        for (Property p : properties) {
            if (p.getName().equals("points")) {
                int size = p.getListOfFloat().size();
                float[] points = new float[size];
                for (int i=0; i<size; i++){
                    points[i] = (float) p.getListOfFloat().get(i);
                }
                ((PointCloud) node).setPoints(points);
                handledProperties.add(p);

            }else if (p.getName().equals("color")) {
                @SuppressWarnings("unchecked")
				List<Float> rgb = p.getRgb();
                Color3f c3 = new Color3f();
                c3.x=(float) rgb.get(0); c3.y=(float) rgb.get(1);c3.z=(float) rgb.get(2);
                ((PointCloud) node).setColor(c3);
                handledProperties.add(p);

            }else if (p.getName().equals("pointSize")) {
                ((PointCloud) node).setPointSize(Float.valueOf(p.getValue()));
                handledProperties.add(p);
            }

        }

    }

    public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
        
    	Property xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("points");
        float[] points = ((PointCloud)node).getPoints();
        StringBuffer ptr = new StringBuffer("");
        int len = points.length;
        for (int i=0; i<len; i++){
        	ptr.append(points[i]);
        	if(i!=len-1)
        		ptr.append(",");
        }
        xmlProperty.setValue(ptr.toString()); 

        xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("color");
        Color3f c3f = ((PointCloud)node).getColor();
        StringBuffer ctr = new StringBuffer("");
        ctr.append(c3f.x+","); ctr.append(c3f.y+","); ctr.append(c3f.z+","); ctr.append(0.0);
        xmlProperty.setValue(ctr.toString());
      

        xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("pointSize");
        xmlProperty.setValue(String.valueOf(((PointCloud)node).getPointSize()));

    }

}
