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

import java.awt.image.BufferedImage;
import java.util.List;

import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node;
import de.grogra.imp.objects.FixedImageAdapter;
import de.grogra.imp3d.objects.HeightField;
import de.grogra.imp3d.objects.ImageHeightField;
import de.grogra.imp3d.objects.Patch;

public class XEGHeightField
{
	public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {

		int uSize = 0, vSize = 0;
		float zerolevel = 0;
		boolean water = false;
		float scale = 1;
		int[] rgbValues = null;
		
		for (Property p : properties) {
			if (p.getName().equals("usize")) {
                uSize = Integer.valueOf(p.getValue());
                handledProperties.add(p);
            }
			else if (p.getName().equals("vsize")) {
                vSize = Integer.valueOf(p.getValue());
                handledProperties.add(p);
            }
			else if (p.getName().equals("zerolevel")) {
                zerolevel = Float.valueOf(p.getValue());
                handledProperties.add(p);
            }
			else if (p.getName().equals("water")) {
                water = Boolean.valueOf(p.getValue());
                handledProperties.add(p);
            }
			else if (p.getName().equals("scale")) {
                scale = Float.valueOf(p.getValue());
                handledProperties.add(p);
            }
        }
		
		// Create Buffered image with dimensions uSize x vSize
		// Then set height values as RGB values
		if (uSize > 0 && vSize > 0)
        {
			BufferedImage img = new BufferedImage(uSize, vSize, BufferedImage.TYPE_INT_ARGB);
			
	        for (Property p : properties) {
	            if (p.getName().equals("heightValues")) { 
	            	
	            	String[] parts = p.getValue().split(",");
	            	rgbValues = new int[parts.length];
	            	for (int i=0; i < vSize; ++i)
	            		for (int j=0; j < uSize; ++j)
	            		{
	            			int index = i*uSize + j;
	            			int index2 = (vSize - 1 - i)*uSize + j;
	            			float number = Float.parseFloat(parts[index2]);
	            			float rounded = (int) Math.round(number * 1000) / 1000f;
	            			rgbValues[index] = (int) rounded;
	            		}
	            	
	        	    img.setRGB(0, 0, uSize, vSize, rgbValues, 0, uSize);
	            	
					handledProperties.add(p);
	            }
	        }
        
        	ImageHeightField heightfield = new ImageHeightField();
        	heightfield.setImage(new FixedImageAdapter(img));
        	heightfield.getMapping().setScale(scale);
        	heightfield.getMapping().setZeroLevel(zerolevel);
        	heightfield.getMapping().setWater(water);
        	((Patch) node).setGrid(heightfield);
        }

    }

    public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
        	
    	Property xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("heightValues");
        
        GraphState gs = node.getCurrentGraphState();
        HeightField heightfield = (HeightField) ((Patch)node).getGrid();
    	int uSize = heightfield.getUSize(gs),
    		vSize = heightfield.getVSize(gs);
    	
        StringBuffer ptr = new StringBuffer("");
        for (int i=0; i<vSize; ++i)
        	for (int j=0; j<uSize; ++j)
        	{
        		ptr.append(heightfield.getHeight(i, j, gs));
        		if(i != vSize-1 || j != uSize-1)
        			ptr.append(",");
        	}
        xmlProperty.setValue(ptr.toString());
        
        xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("usize");
        xmlProperty.setValue(String.valueOf(uSize));
        
        xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("vsize");
        xmlProperty.setValue(String.valueOf(vSize));
        
        xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("zerolevel");
        xmlProperty.setValue(String.valueOf(heightfield.getMapping().getZeroLevel()));
        
        xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("scale");
        xmlProperty.setValue(String.valueOf(heightfield.getMapping().getScale()));
        
        xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("water");
        xmlProperty.setValue(String.valueOf(heightfield.getMapping().isWater()));
    }
}
