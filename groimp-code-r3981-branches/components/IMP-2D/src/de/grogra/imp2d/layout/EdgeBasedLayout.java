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
import java.util.*;

import javax.vecmath.*;
import de.grogra.graph.Graph;

/**
* This class implements an edge-based graph changing layout. 

* @date 19.03.2006
*/
public abstract class EdgeBasedLayout extends Layout
{
	//enh:sco
	
	//in case if nodes overlap each other
	double degreeDeviationAgainstOverlapping = 5;
	//enh:field
	
	boolean mirrorLayout = false;
	//enh:field
	
	DrawingEdgeTypeProperties detp;
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
				
				LinkedList roots = gu.getRoots(nodes);
				
				for (int i = 0; i < roots.size(); i++)
				{
					setNewCoordinatesForNeighbour((Node)roots.get(i));
				}
				
				if (mirrorLayout)
				{
					//Drehen des berechneten Layout um 90 Grad
					//Vertauschen der x- und y-Koordinaten miteinander
					
					for (Node n = nodes; n != null; n = n.next)
					{
						float tempX = n.x;
						n.x = -n.y;
						n.y = -tempX;
					}	
				}
			}//void layout
			
			/**
			 * The neighbor node of n gets new position values
			 * @param n
			 */
			private void setNewCoordinatesForNeighbour(Node n)
			{
				/* Counts how many the specific edges types are used in this graph*/
				Hashtable edgeTypeCounter = new Hashtable();
				
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
						int number = ((Integer)(edgeTypeCounter.get(bitsOfAnEdge))).intValue();
						number++;
						edgeTypeCounter.put(bitsOfAnEdge, number);
					}
					
					double direction = detp.getDirection(bitsOfAnEdge);
					if (((Integer)(edgeTypeCounter.get(bitsOfAnEdge))).intValue() % 2 == 0)
					{
						direction +=  (Integer)edgeTypeCounter.get(bitsOfAnEdge)/2 * (Math.PI/180*degreeDeviationAgainstOverlapping);
					} else
					{
						direction += (Integer)edgeTypeCounter.get(bitsOfAnEdge)/2 * (Math.PI/180*degreeDeviationAgainstOverlapping) + Math.PI;
					}
					
					e.target.x = (float)(e.source.x + (detp.getLength(bitsOfAnEdge) * Math.cos(direction)));
					e.target.y = (float)(e.source.y + (detp.getLength(bitsOfAnEdge) * Math.sin(direction)));
					
					int abbruchzaehler = 0;
					while (checkHidingAnotherNodes(e.target, nodesFromGraph) && abbruchzaehler < 100)
					{
						/* calculating new deviation in case of overlapping*/
						direction += Math.PI/180*degreeDeviationAgainstOverlapping;
						e.target.x = (float)(e.source.x + (detp.getLength(bitsOfAnEdge) * Math.cos(direction)));
						e.target.y = (float)(e.source.y + (detp.getLength(bitsOfAnEdge) * Math.sin(direction)));
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
	public abstract DrawingEdgeTypeProperties getEdgeTypeProperties(Node nodes);
	
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field degreeDeviationAgainstOverlapping$FIELD;
	public static final Type.Field mirrorLayout$FIELD;

	public static class Type extends Layout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (EdgeBasedLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Layout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Layout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Layout.Type.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((EdgeBasedLayout) o).mirrorLayout = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((EdgeBasedLayout) o).mirrorLayout;
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((EdgeBasedLayout) o).degreeDeviationAgainstOverlapping = (double) value;
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
					return ((EdgeBasedLayout) o).degreeDeviationAgainstOverlapping;
			}
			return super.getDouble (o, id);
		}
	}

	static
	{
		$TYPE = new Type (EdgeBasedLayout.class);
		degreeDeviationAgainstOverlapping$FIELD = Type._addManagedField ($TYPE, "degreeDeviationAgainstOverlapping", 0 | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 0);
		mirrorLayout$FIELD = Type._addManagedField ($TYPE, "mirrorLayout", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

//enh:end

}//class CircleGraphLayout
class DrawingEdgeTypeProperties
{
	Hashtable edgeTypes;
	
	DrawingEdgeTypeProperties()
	{
		edgeTypes = new Hashtable();
	}
	
	void addEdgeType(int edgeBits, double direction, double length)
	{
		LinkedList properties = new LinkedList();
		properties.add(direction);
		properties.add(length);
		
		edgeTypes.put(edgeBits, properties);
	}
	
	double getDirection(int edgeBits)
	{
		LinkedList properties = (LinkedList)edgeTypes.get(edgeBits);
		return (Double)properties.get(0);
	}
	 double getLength(int edgeBits)
	{
		LinkedList properties = (LinkedList)edgeTypes.get(edgeBits);
		return (Double)properties.get(1);
	}
	 
	 boolean containsEdgeBits(int edgeBits)
	 {
		 Enumeration e = edgeTypes.keys();
		 if (edgeTypes.containsKey(edgeBits))
			 return true;
		 return false;
	 }
}
