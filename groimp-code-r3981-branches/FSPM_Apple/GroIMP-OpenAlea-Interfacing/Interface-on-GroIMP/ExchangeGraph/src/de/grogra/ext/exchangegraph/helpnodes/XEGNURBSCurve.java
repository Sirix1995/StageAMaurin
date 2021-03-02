
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
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.NURBSCurve;
import de.grogra.math.BSplineCurve;
import de.grogra.math.BezierCurve;

public class XEGNURBSCurve {

    public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {

    	
    	class HelperCurve extends BezierCurve
    	{
    		int degree;
    		
    		HelperCurve(float[] data, int dimension, int degree)
    		{
    			super(data, dimension);
    			this.degree = degree;
    		}
    		
    		@Override public int getDegree(GraphState gs)
    		{
    			return degree;
    		}
    	}
    	
    	float[] points = null;
    	int dimension = 0;
    	int degree = 0;
        for (Property p : properties) {

        	if (p.getName().equals("ctrlpoints")) {
        		
        		String[] parts = p.getValue().split(",");
        	    points = new float[parts.length];
        	    for (int i = 0; i < parts.length; ++i) {
        	        float number = Float.parseFloat(parts[i]);
        	        float rounded = (int) Math.round(number * 1000) / 1000f;
        	        points[i] = rounded;
        	    }
        		
        		handledProperties.add(p);
        	}else if (p.getName().equals("dimension")) {
                dimension = Integer.valueOf(p.getValue());
                handledProperties.add(p);
            }
        	else if (p.getName().equals("degree")) {
                degree = Integer.valueOf(p.getValue());
                handledProperties.add(p);
            }
        }
        
        if (points != null && dimension > 0 && degree > 0) {
        	HelperCurve curve = new HelperCurve(points, dimension, degree);
        	NURBSCurve.curve$FIELD.setObject(node, curve);
        }

    }

    public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
        Property xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("ctrlpoints");
        
        GraphState gs = node.getCurrentGraphState();       
        BSplineCurve bsc = ((NURBSCurve)node).getCurve();
        int vlistsize = bsc.getSize(gs);
        Integer dimension = bsc.getDimension(gs);
        Integer degree = bsc.getDegree(gs);
        StringBuffer ps = new StringBuffer("");
        
        for (int i = 0; i < vlistsize; i++) {
        	float[] out = new float[dimension];
        	bsc.getVertex(out, i, gs);
    	    for (int j = 0; j< out.length; j++) {
    	    	//DecimalFormat df = new DecimalFormat("#,###.##");  
    	    	//String strValue = df.format(out[j]);
    	    	ps.append(out[j]); 
    	    	if  (j!= out.length -1){
    	    		ps.append(",");
    	    	}
    	    }
    	    if (i != vlistsize -1){
    	    	ps.append(",");
    	    }
        }
        xmlProperty.setValue(ps.toString());
        
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("dimension");
		xmlProperty.setValue(dimension.toString());

		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("degree");
		xmlProperty.setValue(degree.toString());
    }

}