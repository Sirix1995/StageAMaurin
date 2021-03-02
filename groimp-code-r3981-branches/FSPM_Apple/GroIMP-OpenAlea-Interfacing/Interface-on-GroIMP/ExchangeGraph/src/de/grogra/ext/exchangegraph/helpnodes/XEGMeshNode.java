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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.MeshNode;
import de.grogra.imp3d.objects.PolygonMesh;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class XEGMeshNode {

	@SuppressWarnings("unchecked")
	public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {
		
		List<Integer> indices = new ArrayList<Integer>();
		List<Float> coords = new ArrayList<Float>();
		List<Float> normals = new ArrayList<Float>();
		
		for (Property p : properties) {
			if (p.getName().equals("indices")) {
				//indices = p.getListOfInt();				
				List<String> indexlist = Arrays.asList(p.getValue().split(","));				
				for(int i=0; i < indexlist.size(); ++i)
					indices.add(i, Integer.parseInt(indexlist.get(i)));
				
				handledProperties.add(p);
			}
			else if (p.getName().equals("coords")) {
				//coords = p.getListOfFloat();
				List<String> coordlist = Arrays.asList(p.getValue().split(","));				
				for(int i=0; i < coordlist.size(); ++i)
					coords.add(i, Float.parseFloat(coordlist.get(i)));
				
				handledProperties.add(p);
			}
			else if (p.getName().equals("normals")) {
				//normals =p.getListOfFloat();
				List<String> normallist = Arrays.asList(p.getValue().split(","));				
				for(int i=0; i < normallist.size(); ++i)
					normals.add(i, Float.parseFloat(normallist.get(i)));
				
				handledProperties.add(p);
			}
		}
		
		createMeshNode((MeshNode) node, indices, coords, normals);
		
	}

	public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
		
		PolygonMesh p = null;		
		if (((MeshNode) node).getPolygons() instanceof PolygonMesh)
			p = (PolygonMesh) ((MeshNode) node).getPolygons();

		if (p == null)
			return;
		
		Property xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("indices");
		StringBuffer indexStr = new StringBuffer("");
		int[] indexArray = p.getIndexData();
		ArrayList<Integer> indexList = new ArrayList<Integer>(indexArray.length);
		for (int i : indexArray)
			indexList.add(i);
			
		for (int i = 0; i< indexList.size(); i++){
			indexStr.append(indexList.get(i));
			if(i != indexList.size()-1)
				indexStr.append(",");
		}
		//xmlProperty.setListOfInt(indexList);
		xmlProperty.setValue(indexStr.toString());
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("coords");
		StringBuffer coordStr = new StringBuffer("");
		float[] coordArray = p.getVertexData();
		ArrayList<Float> coordList = new ArrayList<Float>(coordArray.length);
		for (float f : coordArray)
			coordList.add(f);
		
		for (int i = 0; i< coordList.size(); i++){
			coordStr.append(coordList.get(i));
			if(i != coordList.size()-1)
				coordStr.append(",");
		}
		//xmlProperty.setListOfFloat(coordList);
		xmlProperty.setValue(coordStr.toString());

		if (p.getNormalData() != null) {
			xmlProperty = xmlNode.addNewProperty();
			xmlProperty.setName("normals");
			StringBuffer norStr = new StringBuffer("");
			float[] normalArray = p.getNormalData();
			ArrayList<Float> normalList = new ArrayList<Float>(normalArray.length);
			for (float f : normalArray)
				normalList.add(f);
			
			for (int i = 0; i< normalList.size(); i++){
				norStr.append(normalList.get(i));
				if(i != normalList.size()-1)
					norStr.append(",");
			}
			//xmlProperty.setListOfFloat(normalList);
			xmlProperty.setValue(norStr.toString());
		}
	}
	
	public static void createMeshNode(MeshNode node, List<Integer> indices,
			List<Float> coords, List<Float> normals) {
		
		PolygonMesh p = new PolygonMesh();
		
		IntList indexList = new IntList(indices.size());
		for (Integer i : indices)
			indexList.add(i);
		p.setIndexData(indexList);
		
		FloatList coordList = new FloatList(coords.size());
		for (Float f : coords)
			coordList.add(f);
		p.setVertexData(coordList);
		
		if (normals != null && normals.size() !=0) {
			FloatList normalList = new FloatList(normals.size());
			for (Float f : normals)
				normalList.add(f);
			p.setNormalData(normalList.elements);
		}
				
		node.setPolygons(p);
		
	}
	
}
