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
* This class implements an edge-based graph changing layout. 

* @date 19.03.2006
*/
public class EdgeBasedLayout2 extends EdgeBasedLayout
{
	//enh:sco
	//the longer edge length for the two most common edge types
	private double longEdgeLength = 5;
	//enh:field
	
	//the shorter edge length for all other edge types
	private double shortEdgeLength = 3;
	//enh:field
	
	/**
	 * Getter for the list of edge types
	 */
	@Override
	public  DrawingEdgeTypeProperties getEdgeTypeProperties(Node nodes)
	{
		//setting all nodes to non-visited
		GraphUtilities gu = new GraphUtilities();
		gu.setAllEdgesAccessed(nodes, false);
		
		LinkedList nodesList = gu.getNodesList(nodes);
		longEdgeLength = Math.max(longEdgeLength, (double)(nodesList.size()/15)+shortEdgeLength);
		shortEdgeLength = Math.max(shortEdgeLength, (double)(nodesList.size()/15));
		Hashtable edgeTypeCounter = new Hashtable();
		for (Node n = nodes; n != null; n = n.next)
		{
			for (Edge e = n.getFirstEdge(); e != null; e = e.getNext(n))
			{
				if (e.isAccessed)
					continue;
				e.isAccessed = true;
				
				int bitsOfAnEdge = ((de.grogra.graph.impl.Edge)(e.object)).getEdgeBits();
				if (!edgeTypeCounter.containsKey(bitsOfAnEdge))
				{
					edgeTypeCounter.put(bitsOfAnEdge, 1);
				} else
				{
					int number = (Integer)edgeTypeCounter.get(bitsOfAnEdge);
					number++;
					edgeTypeCounter.put(bitsOfAnEdge, number);
				}
			}
		}
		int mostUsedEdgeBit = 0; int numberMost = 0;
		int secondUsedEdgeBit = 0; int numberSecond = 0;
		
		Enumeration enumeration = edgeTypeCounter.keys();
		while (enumeration.hasMoreElements())
		{
			int edgeBits = (Integer)enumeration.nextElement();
			int number = (Integer)edgeTypeCounter.get(edgeBits);
			if (number > numberMost)
			{
				numberSecond = numberMost;
				secondUsedEdgeBit = mostUsedEdgeBit;
				numberMost = number;
				mostUsedEdgeBit = edgeBits;
			} else if (number > numberSecond)
			{
				numberSecond = number;
				secondUsedEdgeBit = edgeBits;
			}
		}
				
		//storing all edge types in detp
		DrawingEdgeTypeProperties detp = new DrawingEdgeTypeProperties();
		detp.addEdgeType(mostUsedEdgeBit, 0, longEdgeLength);
		
		detp.addEdgeType(secondUsedEdgeBit, (Math.PI/2), longEdgeLength);
		
		
		double radDistance = (Math.PI / (edgeTypeCounter.size() + 1));
		int counter = 1;
		enumeration = edgeTypeCounter.keys();
		while (enumeration.hasMoreElements())
		{
			int edgeBits =  (Integer)enumeration.nextElement();
			if (!detp.containsEdgeBits(edgeBits))
			{
				double direction = (int)((counter+1)/2) * radDistance;
				if (counter % 2 == 0)
				{
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

	public static final Type.Field longEdgeLength$FIELD;
	public static final Type.Field shortEdgeLength$FIELD;

	public static class Type extends EdgeBasedLayout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (EdgeBasedLayout2 representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, EdgeBasedLayout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = EdgeBasedLayout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = EdgeBasedLayout.Type.FIELD_COUNT + 2;

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
					((EdgeBasedLayout2) o).longEdgeLength = (double) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((EdgeBasedLayout2) o).shortEdgeLength = (double) value;
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
					return ((EdgeBasedLayout2) o).longEdgeLength;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((EdgeBasedLayout2) o).shortEdgeLength;
			}
			return super.getDouble (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new EdgeBasedLayout2 ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (EdgeBasedLayout2.class);
		longEdgeLength$FIELD = Type._addManagedField ($TYPE, "longEdgeLength", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 0);
		shortEdgeLength$FIELD = Type._addManagedField ($TYPE, "shortEdgeLength", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

//enh:end

}//class EdgeBasedLayout

