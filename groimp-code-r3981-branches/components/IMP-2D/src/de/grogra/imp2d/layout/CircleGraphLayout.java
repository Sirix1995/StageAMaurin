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
* arranged in one or more circles
* the first node of the list will be placed on top of the inner
* circle
* 
* @date 26.03.2007
*/
public class CircleGraphLayout extends Layout
{
	//enh:sco
	
	private int layerCount = 2;//number of circles
	//enh:field getter setter
	
	private int r, eachLayerNodes;

	private double phi;
	
	private double layerDistance = 1f;
	//enh:field getter setter
	
	private double nodeDistance = 1f;	
	//enh:field getter setter

	
	@Override
	protected Algorithm createAlgorithm ()
	{
		return new Algorithm ()
		{
			@Override
			protected void layout (Node nodes)
			{
				// Compute max and nodeNumber 
				double max = 1;
				int nodeCount = 0;
				
				for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
				{
					max = (float)Math.max(max,Math.max(
							nodeTemp.height,
							nodeTemp.width));
					nodeCount++;
				}//for nodeTemp
				
				layerDistance = Math.max(max+nodeDistance,layerDistance);		
				
				eachLayerNodes = (int) nodeCount / layerCount;
				if ((nodeCount % layerCount) != 0)
				{
					eachLayerNodes++;
				}
				r = (int)Math.max((eachLayerNodes * max) / Math.PI, 1);
				phi = 2 * Math.PI / (eachLayerNodes );
				
				//update location
				int counter = 0;

				GraphUtilities gu = new GraphUtilities();
				LinkedList nodesList = gu.getNodesListDFS(nodes);
				
				for (int i = 0; i < nodesList.size(); i++)
				{
					Node nodeTemp = (Node)nodesList.get(i);
					counter++; 
					if (layerCount > 1)				 
					 {
					   	if (counter % (eachLayerNodes+1) ==  0) 
					   	{
					   		//new layer
					   		r += layerDistance;
					   		counter = 0;
					    }
					 }
					 
					 nodeTemp.y =  (float)(r * Math.cos((counter-1) * phi));
					 nodeTemp.x =  (float)(r * Math.sin((counter-1) * phi));
				}//for nodeTemp
				
			}//void layout
		};
	}//Algorithm createAlgorithm	
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field layerCount$FIELD;
	public static final Type.Field layerDistance$FIELD;
	public static final Type.Field nodeDistance$FIELD;

	public static class Type extends Layout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (CircleGraphLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Layout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Layout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Layout.Type.FIELD_COUNT + 3;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((CircleGraphLayout) o).layerCount = (int) value;
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
					return ((CircleGraphLayout) o).getLayerCount ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((CircleGraphLayout) o).layerDistance = (double) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((CircleGraphLayout) o).nodeDistance = (double) value;
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
					return ((CircleGraphLayout) o).getLayerDistance ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((CircleGraphLayout) o).getNodeDistance ();
			}
			return super.getDouble (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new CircleGraphLayout ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (CircleGraphLayout.class);
		layerCount$FIELD = Type._addManagedField ($TYPE, "layerCount", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 0);
		layerDistance$FIELD = Type._addManagedField ($TYPE, "layerDistance", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 1);
		nodeDistance$FIELD = Type._addManagedField ($TYPE, "nodeDistance", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 2);
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

	public double getLayerDistance ()
	{
		return layerDistance;
	}

	public void setLayerDistance (double value)
	{
		this.layerDistance = (double) value;
	}

	public double getNodeDistance ()
	{
		return nodeDistance;
	}

	public void setNodeDistance (double value)
	{
		this.nodeDistance = (double) value;
	}

//enh:end

}//class CircleGraphLayout

