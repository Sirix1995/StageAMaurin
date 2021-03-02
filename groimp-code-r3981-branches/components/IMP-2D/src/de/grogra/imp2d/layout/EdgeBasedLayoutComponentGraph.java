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
import java.util.Hashtable;
import java.util.LinkedList;

import javax.vecmath.Vector2d;

import de.grogra.graph.Graph;

/**
* This class implements an edge-based graph changing layout. 

* @date 19.03.2006
*/
public abstract class EdgeBasedLayoutComponentGraph extends Layout
{
	//enh:sco
	
	//in case if nodes overlap each other
	double degreeDeviationAgainstOverlapping = 45;
	//enh:field
	
	DrawingEdgeTypePropertiesComponentGraph detp;
	Graph sourceGraph;
	Node nodesFromGraph;
	
	@Override
	protected Algorithm createAlgorithm ()
	{
		return new Algorithm ()
		{
			@Override
			protected void layout (Node nodes)
			{
				sourceGraph = graph;
				nodesFromGraph = nodes;
				
				detp = getEdgeTypeProperties(nodes);
				
				//setting all nodes to non-visited
				GraphUtilities gu = new GraphUtilities();
				gu.setAllNodesEdgesAccessed(nodes, false);
				
				LinkedList<Node> roots = gu.getRoots(nodes);
				
				for (int i = 0; i < roots.size(); i++) {
					setNewCoordinatesForNeighbour(roots.get(i));
				}
			}//void layout
			
			/**
			 * The neighbour node of n gets new position values
			 * @param n
			 */
			private void setNewCoordinatesForNeighbour(Node n)
			{
				/* Counts how many the specific edges types are used in this graph*/
				Hashtable<Integer,Integer> edgeTypeCounter = new Hashtable<Integer,Integer>();
				
				for (Edge e = n.getFirstEdge(); e != null; e = e.getNext(n))
				{
					int bitsOfAnEdge = ((de.grogra.graph.impl.Edge)(e.object)).getEdgeBits();
					if (e.isAccessed == true || e.target.equals(n) ||e.target.isAccessed == true)
					{
						continue;
					}
					e.isAccessed = true;
					
					if (!edgeTypeCounter.containsKey(bitsOfAnEdge))
					{
						edgeTypeCounter.put(bitsOfAnEdge, 0);
					} else
					{
						int number = (edgeTypeCounter.get(bitsOfAnEdge));
						number++;
						edgeTypeCounter.put(bitsOfAnEdge, number);
					}
					
					double direction = detp.getDirection(bitsOfAnEdge);
					if(bitsOfAnEdge!=Graph.COMPONENT_INPUT_SLOT_EDGE && bitsOfAnEdge!=Graph.COMPONENT_OUTPUT_SLOT_EDGE && bitsOfAnEdge!=Graph.DUMMY_EDGE) {
						if (edgeTypeCounter.get(bitsOfAnEdge) % 2 == 0) {
							direction +=  edgeTypeCounter.get(bitsOfAnEdge)/2 * (Math.PI/180*degreeDeviationAgainstOverlapping);
						} else {
							direction += edgeTypeCounter.get(bitsOfAnEdge)/2 * (Math.PI/180*degreeDeviationAgainstOverlapping) + Math.PI;
						}
					}
					e.target.x = (float)(e.source.x + (detp.getLength(bitsOfAnEdge) * Math.cos(direction)));
					e.target.y = (float)(e.source.y + (detp.getLength(bitsOfAnEdge) * Math.sin(direction)));
					
					int abbruchzaehler = 0;
					while (checkHidingAnotherNodes(e.target, nodesFromGraph) && abbruchzaehler < 100) {
						/* calculating new deviation in case of overlapping*/
						e.target.x = (float)(e.source.x + (detp.getLength(bitsOfAnEdge)));
						abbruchzaehler++;
					} 
					e.target.isAccessed = true;
					setNewCoordinatesForNeighbour(e.target);
				}
			}
			
			/**
			 * Check if node n overlaps with recently setted nodes
			 */
			private boolean checkHidingAnotherNodes(Node n, Node allNodes)
			{
				for (Node nodeTemp = allNodes; nodeTemp != null; nodeTemp = nodeTemp.next)
				{
					//recently setted
					if (nodeTemp.isAccessed == true)
					{
						Vector2d rangeX = new Vector2d(n.x - n.width, n.x + n.width);
						Vector2d rangeY = new Vector2d(n.y - n.height, n.y + n.height);
				
						if ((nodeTemp.x > rangeX.x && nodeTemp.x < rangeX.y) && (nodeTemp.y > rangeY.x && nodeTemp.y < rangeY.y))
						{
							return true;
						}
					}
				}
				return false;
			}
		};
	}//Algorithm createAlgorithm
	
	/**
	 * Getting a List of all edgeTypes and their directions
	 * @param nodes
	 * @return
	 */
	public abstract DrawingEdgeTypePropertiesComponentGraph getEdgeTypeProperties(Node nodes);
	
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field degreeDeviationAgainstOverlapping$FIELD;

	public static class Type extends Layout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (EdgeBasedLayoutComponentGraph representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Layout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Layout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Layout.Type.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			return super.getBoolean (o, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((EdgeBasedLayoutComponentGraph) o).degreeDeviationAgainstOverlapping = value;
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
					return ((EdgeBasedLayoutComponentGraph) o).degreeDeviationAgainstOverlapping;
			}
			return super.getDouble (o, id);
		}
	}

	static
	{
		$TYPE = new Type (EdgeBasedLayoutComponentGraph.class);
		degreeDeviationAgainstOverlapping$FIELD = Type._addManagedField ($TYPE, "degreeDeviationAgainstOverlapping", 0 | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

//enh:end

}//class EdgeBasedLayoutComponentGraph

class DrawingEdgeTypePropertiesComponentGraph {
	private final Hashtable<Integer,LinkedList<Double>> edgeTypes;

	DrawingEdgeTypePropertiesComponentGraph() {
		edgeTypes = new Hashtable<Integer,LinkedList<Double>>();
	}

	void addEdgeType(int edgeBits, double direction, double length) {
		LinkedList<Double> properties = new LinkedList<Double>();
		properties.add(direction);
		properties.add(length);
		
		edgeTypes.put(edgeBits, properties);
	}

	double getDirection(int edgeBits) {
		LinkedList<Double> properties = edgeTypes.get(edgeBits);
		return properties.get(0);
	}

	double getLength(int edgeBits) {
		LinkedList<Double> properties = edgeTypes.get(edgeBits);
		return properties.get(1);
	}

	boolean containsEdgeBits(int edgeBits) {
		if (edgeTypes.containsKey(edgeBits)) return true;
		return false;
	}
}
