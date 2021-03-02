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

package de.grogra.imp2d.layout;

import java.util.Enumeration;
import java.util.Hashtable;

import de.grogra.graph.Graph;

/**
* This class implements an edge-based graph changing layout. 
* Adjusted for the special requirements of the component graph.
* 
* @date 29.04.2013
*/
public class EdgeBasedLayout2ComponentGraph extends EdgeBasedLayoutComponentGraph
{
	//enh:sco
	//the longer edge length for the two most common edge types
	private double longEdgeLength = 3;
	//enh:field
	
	//the shorter edge length for all other edge types
	private double shortEdgeLength = 2;
	//enh:field
	
	//length for slot edges 
	private final double slotEdgeLength = 0.2;
	
	private final static float PI2 = (float)Math.PI/2f;

	public EdgeBasedLayout2ComponentGraph() {
		setLayouted(false);
	}
	

	/**
	 * Getter for the list of edge types
	 */
	@Override
	public  DrawingEdgeTypePropertiesComponentGraph getEdgeTypeProperties(Node nodes)
	{
		//setting all nodes to non-visited
		GraphUtilities gu = new GraphUtilities();
		gu.setAllEdgesAccessed(nodes, false);
		
//		LinkedList<Node> nodesList = gu.getNodesList(nodes);
		Hashtable<Integer,Integer> edgeTypeCounter = new Hashtable<Integer,Integer>();
		for (Node n = nodes; n != null; n = n.next) {
			for (Edge e = n.getFirstEdge(); e != null; e = e.getNext(n)) {
				if (e.isAccessed) continue;
				e.isAccessed = true;
				
				int bitsOfAnEdge = ((de.grogra.graph.impl.Edge)(e.object)).getEdgeBits();
				if (!edgeTypeCounter.containsKey(bitsOfAnEdge)) {
					edgeTypeCounter.put(bitsOfAnEdge, 1); 
				} else {
					int number = edgeTypeCounter.get(bitsOfAnEdge);
					number++;
					edgeTypeCounter.put(bitsOfAnEdge, number);
				}
			}
		}

		//storing all edge types in detp
		DrawingEdgeTypePropertiesComponentGraph detp = new DrawingEdgeTypePropertiesComponentGraph();
		
		// set direction of input slots
		detp.addEdgeType(Graph.COMPONENT_INPUT_SLOT_EDGE, PI2, slotEdgeLength);
		// set direction of output slots
		detp.addEdgeType(Graph.COMPONENT_OUTPUT_SLOT_EDGE, -PI2, slotEdgeLength);

		// set direction of the rest of nodes
		detp.addEdgeType(Graph.REFINEMENT_EDGE, Math.PI, longEdgeLength);
		
		double radDistance = (Math.PI / (edgeTypeCounter.size() + 1));
		int counter = 1;
		Enumeration<Integer> enumeration = edgeTypeCounter.keys();
		while (enumeration.hasMoreElements()) {
			int edgeBits = enumeration.nextElement();
			if (!detp.containsEdgeBits(edgeBits)) {
				double direction = (counter+1)/2 * radDistance;
				if (counter % 2 == 0) {
					direction += Math.PI / 2;
				}
				detp.addEdgeType(edgeBits, direction, shortEdgeLength);
				counter++;
			}
		}
		return detp;
	}
	
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static class Type extends EdgeBasedLayoutComponentGraph.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (EdgeBasedLayout2ComponentGraph representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, EdgeBasedLayoutComponentGraph.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = EdgeBasedLayoutComponentGraph.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = EdgeBasedLayoutComponentGraph.Type.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((EdgeBasedLayout2ComponentGraph) o).longEdgeLength = value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((EdgeBasedLayout2ComponentGraph) o).shortEdgeLength = value;
					return;
			}
			super.setDouble (o, id, value);
		}

		@Override
		protected double getDouble (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((EdgeBasedLayout2ComponentGraph) o).longEdgeLength;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((EdgeBasedLayout2ComponentGraph) o).shortEdgeLength;
			}
			return super.getDouble (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new EdgeBasedLayout2ComponentGraph ();
		}

	}

	@Override
	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (EdgeBasedLayout2ComponentGraph.class);
		$TYPE.validate ();
	}

//enh:end

}//class EdgeBasedLayout2 ComponentGraph

