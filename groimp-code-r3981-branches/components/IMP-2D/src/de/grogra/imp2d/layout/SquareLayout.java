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

import java.util.LinkedList;

/** 
* This class implements a graph changing layout. The nodes will be
* arranged in a square
* 
* @date 26.03.2007
*/
public class SquareLayout extends Layout
{
	//enh:sco

	//number of rows/columns of the layout
	private int layerCount = 3;
	//enh:field getter setter
	
	private int r, eachLayerNodes;

	private double layerDistance = 1f;
	//enh:field

	private double nodeDistance = 1.2f;
	//enh:field

	private boolean topDown = false;
	//enh:field

	
	@Override
	protected Algorithm createAlgorithm ()
	{
		return new Algorithm ()
		{
			@Override
			protected void layout (Node nodes)
			{
				//getting the nodes in depth first search
				GraphUtilities gu = new GraphUtilities();
				LinkedList nodesList = gu.getNodesListDFS(nodes);
				if (layerCount < 1)
				{
					layerCount = 1;
				}
				//Compute Max and nodeNumber 
				int nodeCount = 0;
				double max = 1;
				r = 0;
				for (int i = 0; i < nodesList.size(); i++)
				{
					Node nodeTemp = (Node)nodesList.get(i);
					
					nodeCount++;
					max = (float)Math.max(max,Math.max(
							nodeTemp.height,
							nodeTemp.width));
				}//for nodeTemp
				
				layerDistance = Math.max((max+1), layerDistance);		
				nodeDistance = Math.max(nodeDistance, max);
				//Compute radius
				eachLayerNodes = Math.max(1, ((int)(int)nodeCount / layerCount));
				if (nodeCount % layerCount > 0)
				{
					eachLayerNodes+= 1;
				}
					
				//Update location
				int counter = 0;
				
				for (int i = 0; i < nodesList.size(); i++)
				{
					Node nodeTemp = (Node)nodesList.get(i);
					
					 if (topDown)
					 {
						 nodeTemp.x =  r;
						 nodeTemp.y =  -(float)(counter * nodeDistance);
					 } else
					 {
						 nodeTemp.x = -(float)(-counter * nodeDistance);
						 nodeTemp.y = -r; 
					 }
					
					//calculate position of the node in the recent column
					if ((counter+1) == eachLayerNodes) 
					 {
						r += layerDistance;
						
					   	counter = -1;
					 }
					 counter++;
				}//for nodeTemp
			}

		};//Algorithm
		
	}//void layout
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field layerCount$FIELD;
	public static final Type.Field layerDistance$FIELD;
	public static final Type.Field nodeDistance$FIELD;
	public static final Type.Field topDown$FIELD;

	public static class Type extends Layout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SquareLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Layout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Layout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Layout.Type.FIELD_COUNT + 4;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 3:
					((SquareLayout) o).topDown = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 3:
					return ((SquareLayout) o).topDown;
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((SquareLayout) o).layerCount = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((SquareLayout) o).getLayerCount ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((SquareLayout) o).layerDistance = (double) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((SquareLayout) o).nodeDistance = (double) value;
					return;
			}
			super.setDouble (o, id, value);
		}

		@Override
		protected double getDouble (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((SquareLayout) o).layerDistance;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((SquareLayout) o).nodeDistance;
			}
			return super.getDouble (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SquareLayout ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SquareLayout.class);
		layerCount$FIELD = Type._addManagedField ($TYPE, "layerCount", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 0);
		layerDistance$FIELD = Type._addManagedField ($TYPE, "layerDistance", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 1);
		nodeDistance$FIELD = Type._addManagedField ($TYPE, "nodeDistance", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 2);
		topDown$FIELD = Type._addManagedField ($TYPE, "topDown", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

	public int getLayerCount ()
	{
		return layerCount;
	}

	public void setLayerCount (int value)
	{
		this.layerCount = (int) value;
	}

//enh:end

}//class SquareLayout
