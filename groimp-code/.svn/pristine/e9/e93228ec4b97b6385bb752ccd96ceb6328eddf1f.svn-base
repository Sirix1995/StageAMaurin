
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
import de.grogra.imp3d.objects.Polygon;
import de.grogra.math.VertexList;

// this class is for 3D coplanar polygon without holes
public class XEGPolygon {

    public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {

        for (Property p : properties) {
            if (p.getName().equals("vertices")) {           	
            	Polygon.vertices$FIELD.setObject(node, (VertexList)p);
				handledProperties.add(p);
            }
        }

    }

    public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
        
    	Property xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("vertices");
        
        VertexList vlist =  ((Polygon)node).getVertices();
        GraphState gs = node.getCurrentGraphState();
        int vlsize = vlist.getSize(gs);
        System.out.println(vlsize);
        StringBuffer vliststring = new StringBuffer("");
        for(int i=0; i<vlsize; i++){
        	float[] vertex = new float[3];
        	vlist.getVertex(vertex, i, gs);
        	
        	for (int j=0; j< vertex.length; j++){
        		vliststring.append(vertex[j]);
        		System.out.println(vertex[j]);
        		if (j!=vertex.length-1)
        			vliststring.append(",");
        	}
        	if (i!= vlsize-1)
        		vliststring.append(",");
        }
        System.out.println(vliststring.toString());
        xmlProperty.setValue(vliststring.toString());
        
        //PolygonArray p = new PolygonArray ();
        /*
         de.grogra.imp3d.Polygonizable surface = (de.grogra.imp3d.Polygonizable) node
			.getObject (Attributes.SHAPE);
		ContextDependent source = surface.getPolygonizableSource (gs);
		surface.getPolygonization ().polygonize (source, gs, p, flags, flatness);
         */
    }

}