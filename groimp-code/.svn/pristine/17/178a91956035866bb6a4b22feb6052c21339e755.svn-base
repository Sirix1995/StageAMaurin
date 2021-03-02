
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

import java.util.Arrays;
import java.util.List;

import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.NURBSSurface;
import de.grogra.math.BSplineSurface;
import de.grogra.math.BSplineSurfaceImpl;

public class XEGNURBSSurface {

    public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {

		/*
        for (Property p : properties) {
            if (p.getName().equals("surface")) {
                NURBSSurface.surface$FIELD.setObject(node, (BSplineSurface)p);
                handledProperties.add(p);
            }else if (p.getName().equals("flatness")){
                NURBSSurface.flatness$FIELD.setFloat(node, Float.valueOf(p.getValue()));
                handledProperties.add(p);
            }else if (p.getName().equals("visibleSides")){
                NURBSSurface.visibleSides$FIELD.setFloat(node, Integer.valueOf(p.getValue()));
                handledProperties.add(p);
            }
        }
*/

    	
    	class HelperSurface extends BSplineSurfaceImpl
		{
			int uSize, vSize, uDegree, vDegree;
			HelperSurface(float[] data, int uSize, int vSize, int uDegree, int vDegree, int dimension)
			{
				this.data = data;
				this.uSize = uCount = uSize;
				this.vSize = vSize;
				this.uDegree = uDegree;
				this.vDegree = vDegree;
				this.dimension = dimension;
			}
			
			@Override public int getUSize(GraphState gs)
			{
				return uSize;
			}

			@Override public int getVSize(GraphState gs)
			{
				return vSize;
			}

			@Override public int getDimension(GraphState gs)
			{
				return dimension;
			}

			@Override public int getUDegree(GraphState gs)
			{
				return uDegree;
			}

			@Override public int getVDegree(GraphState gs)
			{
				return vDegree;
			}
			
		}
    	
    	float[] ctrlpoints = null;
    	int uSize=0, vSize=0, dimension=0, uDegree=0, vDegree=0;
    	for (Property p : properties)
    	{    		
    		if (p.getName().equals("uSize"))
    		{
				uSize = Integer.valueOf(p.getValue());
				handledProperties.add(p);
			}
    		else if (p.getName().equals("vSize"))
    		{
				vSize = Integer.valueOf(p.getValue());
				handledProperties.add(p);
			}
    		else if (p.getName().equals("uDegree"))
    		{
				uDegree = Integer.valueOf(p.getValue());
				handledProperties.add(p);
			}
    		else if (p.getName().equals("vDegree"))
    		{
				vDegree = Integer.valueOf(p.getValue());
				handledProperties.add(p);
			}
			else if (p.getName().equals("dimension"))
			{
				dimension = Integer.valueOf(p.getValue());
				handledProperties.add(p);
			}
			else if (p.getName().equals("ctrlpoints"))
    		{
				List<String> slist = Arrays.asList(p.getValue().split(","));
				ctrlpoints = new float[slist.size()];
				for(int i=0; i < slist.size(); ++i)
					ctrlpoints[i] = Float.parseFloat(slist.get(i));
				
				handledProperties.add(p);
			}
        }
    	
    	if (ctrlpoints != null && uSize > 0 && vSize > 0 && uDegree > 0 && vDegree > 0 && dimension > 0)
    		((NURBSSurface) node).setSurface(new HelperSurface(ctrlpoints, uSize, vSize, uDegree, vDegree, dimension));
    

    }

    public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
        /*
        Property xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("ctrlpoints");
        
        GraphState gs = node.getCurrentGraphState();       
        BSplineSurface bss = ((NURBSSurface)node).getSurface();
        int udegree = bss.getUDegree(gs);
        int vdegree = bss.getVDegree(gs);       
        Integer dimension = bss.getDimension(gs);
        StringBuffer ps = new StringBuffer("");
        bss.getUSize(gs);
        bss.getVSize(gs);
        //bss.getVertexIndex(u, v, gs);
        
        /*for (int i = 0; i < vlistsize; i++) {
        	float[] out = new float[dimension];
        	bss.getVertex(out, i, gs);
    	    for (int j = 0; j< out.length; j++) {
    	    	ps.append(out[j]); 
    	    	if ((i != vlistsize -1) && (j!= out.length -1)){
    	    		ps.append(",");
    	    	}
    	    }	    		
        }*/
        /*
        xmlProperty.setValue(ps.toString());
        
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("dimension");
		xmlProperty.setValue(dimension.toString());
		*/

        Property xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("ctrlpoints");
       
        GraphState gs = node.getCurrentGraphState();       
        BSplineSurface bss = ((NURBSSurface)node).getSurface();      
        Integer dimension = bss.getDimension(gs);
        StringBuffer ps = new StringBuffer("");
        Integer uSize = bss.getUSize(gs);
        Integer vSize = bss.getVSize(gs);
        Integer uDegree = bss.getUDegree(gs);
        Integer vDegree = bss.getVDegree(gs);
        
        float[] out = new float[dimension];
        for (int i = 0; i < vSize; ++i)
        	for (int j = 0; j < uSize; ++j)
        	{
        		bss.getVertex(out, bss.getVertexIndex(j, i, gs), gs);
        		for (int k = 0; k < out.length; ++k)
        		{
        	    	ps.append(out[k]); 
        	    	if  (k != out.length -1)
        	    		ps.append(",");
        	    }
        		
        		if (i != vSize-1 || j != uSize-1)
        	    	ps.append(",");
        	}

        xmlProperty.setValue(ps.toString());
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("uSize");
		xmlProperty.setValue(uSize.toString());
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("vSize");
		xmlProperty.setValue(vSize.toString());
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("uDegree");
		xmlProperty.setValue(uDegree.toString());
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("vDegree");
		xmlProperty.setValue(vDegree.toString());
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("dimension");
		xmlProperty.setValue(dimension.toString());

    }

}