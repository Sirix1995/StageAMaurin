
/* Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
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

import javax.vecmath.Point2d;
import java.util.*;

/**
 * A <code>EnergyModelLAyout</code> computes a graph layout based on the
 * rPolyLog-Model
 * 
 * @date 26.03.2007
 */
public class EnergyModelLayout extends Layout
{
	//variables
	
	private MinimizerPolyLogBarnesHut fMiniPolyLog;
	
	private float[][] fPos= null;
	private float[][] fattr= null;
	
	//enh:sco
	private float gravitationFactor= 0.15f;
	//enh:field
	
	private float attractionExponent= 3.0f;
	//enh:field
	
	private float minDistanceX = 3f;
	//enh:field
	
	private float minDistanceY = 3f;
	//enh:field
	
	private float unconnectedNodeX = 0.5f;
	//enh:field
	
	private int count= 100;
	//enh:field
	
	@Override
	protected Algorithm createAlgorithm ()
	{
		return new Algorithm ()
		{
			@Override
			protected void layout (Node nodes)
			{
				GraphUtilities gu = new GraphUtilities();
				int connectedNodesSize = gu.getConnectedNodesSize(nodes);
				LinkedList fUnconnectedNodes = gu.getUnconnectedNodes(nodes);
				float fUnconnectedNodeY = (float)(getMaxBounds(fUnconnectedNodes).y);
				
				int i=0;
				int unconnectedCounter= 0;	
				
				if (connectedNodesSize > 0 )
				{
					fPos= new float[connectedNodesSize][3];
					fattr= new float[connectedNodesSize][connectedNodesSize];
				}
				
				for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next) 
				{
					if (nodeTemp.getFirstEdge() == null)
					{
						nodeTemp.x = unconnectedNodeX;
						nodeTemp.y = unconnectedCounter * fUnconnectedNodeY;
						unconnectedCounter++;
						continue;
					}
				
					fPos[i][0]= nodeTemp.x;
					fPos[i][1]= nodeTemp.y;
					fPos[i][2]= 0; // Z - Achse
		
					nodeTemp.index = i;
					i++;
				}//for nodeTemp		
				
				for(int j = 0; j < connectedNodesSize; j++) 
				{
					for(int k = 0; k < connectedNodesSize; k++) 
					{
						fattr[j][k]= 0;	
					}//for k
				}// for j		
				
				int edgeCounter = 0;
				double distance = 0.0f;		
				
				//for all edges
				for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
				{
					for (Edge edgeTemp = nodeTemp.getFirstEdge(); edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
					{
						Node source = edgeTemp.source;
						Node end = edgeTemp.target;	
						
						fattr[source.index][end.index]= edgeTemp.weight;
						fattr[end.index][source.index]= edgeTemp.weight;
						
						distance = distance + source.distance(end);
						edgeCounter++;
					}//for edgeTemp
				}//for nodeTemp
				
				distance= distance / edgeCounter;	// arithm. Mittel
				for(int j= 0; j < connectedNodesSize; j++) 
				{
					fPos[j][0]= (float)(fPos[j][0] / distance);
					fPos[j][1]= (float)(fPos[j][1] / distance);
				}
				
				fMiniPolyLog= new MinimizerPolyLogBarnesHut(connectedNodesSize, fattr, fPos);
				fMiniPolyLog.setAttractionExponent(attractionExponent);
				fMiniPolyLog.setGravitationFactor(gravitationFactor);
				fMiniPolyLog.minimizeEnergy(count);	
				
				//replace the value of the fPosition
				for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
				{ 
					nodeTemp.layoutVarX = fPos[nodeTemp.index][0];
					nodeTemp.layoutVarY = fPos[nodeTemp.index][1];
				}//while
				
				
				for(Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next) 
				{	
					nodeTemp.x = nodeTemp.layoutVarX * Math.max(0, minDistanceX);
					nodeTemp.y = nodeTemp.layoutVarY * Math.max(0, minDistanceY);
				}//while
			}//void layout
			
			/** Getting the maximum values of height and width
			 * of the nodes
			 */
			protected Point2d getMaxBounds(LinkedList nodes)
			{
				if (nodes == null)
					return new Point2d(0,0);
				
				Point2d maxBounds = new Point2d();
				
				for (int i = 0; i < nodes.size(); i++)
				{
					maxBounds.set(
					Math.max(((Node)nodes.get(i)).height, maxBounds.x),
					Math.max(((Node)nodes.get(i)).width, maxBounds.y));
				}//for
				return maxBounds;
			}//Point2d getMaxBounds
		};
	}//Algorithm createAlgorithm	
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field gravitationFactor$FIELD;
	public static final Type.Field attractionExponent$FIELD;
	public static final Type.Field minDistanceX$FIELD;
	public static final Type.Field minDistanceY$FIELD;
	public static final Type.Field unconnectedNodeX$FIELD;
	public static final Type.Field count$FIELD;

	public static class Type extends Layout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (EnergyModelLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Layout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Layout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Layout.Type.FIELD_COUNT + 6;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 5:
					((EnergyModelLayout) o).count = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 5:
					return ((EnergyModelLayout) o).count;
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((EnergyModelLayout) o).gravitationFactor = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((EnergyModelLayout) o).attractionExponent = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((EnergyModelLayout) o).minDistanceX = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((EnergyModelLayout) o).minDistanceY = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((EnergyModelLayout) o).unconnectedNodeX = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((EnergyModelLayout) o).gravitationFactor;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((EnergyModelLayout) o).attractionExponent;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((EnergyModelLayout) o).minDistanceX;
				case Type.SUPER_FIELD_COUNT + 3:
					return ((EnergyModelLayout) o).minDistanceY;
				case Type.SUPER_FIELD_COUNT + 4:
					return ((EnergyModelLayout) o).unconnectedNodeX;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new EnergyModelLayout ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (EnergyModelLayout.class);
		gravitationFactor$FIELD = Type._addManagedField ($TYPE, "gravitationFactor", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		attractionExponent$FIELD = Type._addManagedField ($TYPE, "attractionExponent", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		minDistanceX$FIELD = Type._addManagedField ($TYPE, "minDistanceX", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		minDistanceY$FIELD = Type._addManagedField ($TYPE, "minDistanceY", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 3);
		unconnectedNodeX$FIELD = Type._addManagedField ($TYPE, "unconnectedNodeX", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 4);
		count$FIELD = Type._addManagedField ($TYPE, "count", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 5);
		$TYPE.validate ();
	}

//enh:end
}//class EnergyModelLayout
