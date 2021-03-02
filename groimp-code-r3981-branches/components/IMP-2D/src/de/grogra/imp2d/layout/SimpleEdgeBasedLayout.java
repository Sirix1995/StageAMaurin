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

/**
* This class implements an edge based graph changing layout. 

* @date 19.03.2006
*/
public class SimpleEdgeBasedLayout extends EdgeBasedLayout
{
	//enh:sco
	
	//new edge length for all edges
	private double edgeLength = 3;
	//enh:field getter setter
	
	double radDistance = 0;
	int counter = 0;
	
	/**
	 * Getter for all edge types of the graph and their directions
	 */
	@Override
	public DrawingEdgeTypeProperties getEdgeTypeProperties(Node nodes)
	{
		DrawingEdgeTypeProperties detp = new DrawingEdgeTypeProperties();
		GraphUtilities gu = new GraphUtilities();
		gu.setAllEdgesAccessed(nodes, false);
		LinkedList nodesList = gu.getNodesList(nodes);
		edgeLength = Math.max(edgeLength, (double)(nodesList.size()/15));
		
		LinkedList edgeBits = getEdgeBits(nodes);
		
		for (Node n = nodes; n != null; n = n.next)
		{
			for (Edge e = n.getFirstEdge(); e != null; e = e.getNext(n))
			{
				if (e.isAccessed)
					continue;
				e.isAccessed = true;
				
				int bitsOfAnEdge = ((de.grogra.graph.impl.Edge)(e.object)).getEdgeBits();
				
				if (!detp.containsEdgeBits(bitsOfAnEdge))
				{
					double direction = 0;
					int edgeNumber = edgeBits.indexOf(bitsOfAnEdge);
					radDistance = Math.PI / edgeBits.size();
					
					direction = (edgeNumber) * radDistance;
					
					detp.addEdgeType(bitsOfAnEdge, direction, this.edgeLength);
					
				}
			}
		}
		
		return detp;
	}
	
	/**
	 * Getting all edge Bits of the graph
	 * @param nodes
	 * @return
	 */
	private LinkedList getEdgeBits(Node nodes)
	{	
		LinkedList edgeBits = new LinkedList();
		int counter = 0;
		
		for (Node n = nodes; n != null; n = n.next)
		{
			for ( Object e = sourceGraph.getFirstEdge(n.object); e != null; e = sourceGraph.getNextEdge(e, n.object))
			{
				int bitsOfEdge = (e instanceof de.grogra.graph.impl.Edge) ? ((de.grogra.graph.impl.Edge) e).getEdgeBits ()
				        : sourceGraph.getEdgeBits (e);
				
				if (!edgeBits.contains(bitsOfEdge))
				{
					edgeBits.add(bitsOfEdge);
				}
			}
		}
		return edgeBits;
	}
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field edgeLength$FIELD;

	public static class Type extends EdgeBasedLayout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SimpleEdgeBasedLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, EdgeBasedLayout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = EdgeBasedLayout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = EdgeBasedLayout.Type.FIELD_COUNT + 1;

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
					((SimpleEdgeBasedLayout) o).edgeLength = (double) value;
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
					return ((SimpleEdgeBasedLayout) o).getEdgeLength ();
			}
			return super.getDouble (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SimpleEdgeBasedLayout ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SimpleEdgeBasedLayout.class);
		edgeLength$FIELD = Type._addManagedField ($TYPE, "edgeLength", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public double getEdgeLength ()
	{
		return edgeLength;
	}

	public void setEdgeLength (double value)
	{
		this.edgeLength = (double) value;
	}

//enh:end

}//class CircleGraphLayout

