
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
import de.grogra.imp2d.AWTCanvas2D;
import de.grogra.imp2d.layout.Node;
import de.grogra.pf.registry.Item;

import java.util.*;

/**
 * This class implements a graph changing layout. The nodes will be
 * arranged in tree style

 * @date 26.03.2007
 */
public class TreeLayout extends Layout 
{	
	
	//enh:sco
	
	private  float minDistanceX = 0.3f;//horizontal distance of the nodes
	//enh:field
	
	private  float minDistanceY = 0.3f;//vertical distance of the nodes
	//enh:field
	
	//if true then vertical drawing, if false then horizontal drawing
	private boolean topDown = false;
	//enh:field

	
	public TreeLayout() {
		minDistanceX$FIELD.setFloat(this, -0.4f);
		minDistanceY$FIELD.setFloat(this, 1f);
		topDown$FIELD.setBoolean(this, true);
	}
	
	@Override
	protected Algorithm createAlgorithm ()
	{
		return new Algorithm ()
		{
			@Override
			protected void layout (Node nodes)
			{	
				
				
				GraphUtilities gu = new GraphUtilities();
				LinkedList roots = gu.getRoots(nodes);
				
				double x = minDistanceX;
				double y = minDistanceY;
				
				gu.setAllEdgesAccessed(nodes, true);
				//setting all nodes and edge in the initial status
				for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
				{
					nodeTemp.layoutVarX = 0;
					nodeTemp.layoutVarY = 0;
				}
				
				//depth first search
				for (int i = 0; i < roots.size(); i++)
				{
					calcXCoord((Node)roots.get(i), x);
					
					gu.setAllEdgesAccessed(nodes, true);
					
					double tempY = calcYCoord((Node)roots.get(i), y);
					if (tempY >= 0)
					{
						y = tempY;
					}
					
					gu.setAllEdgesAccessed(nodes, true);
				}
				
				if (topDown)
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
			
			private void calcXCoord(Node nodeTemp, double x)
			{
				if (nodeTemp.isAccessed == true) 
				{
					return;
				}
							
				nodeTemp.isAccessed = true;
				
				nodeTemp.x = (float)x;
				nodeTemp.y = nodeTemp.layoutVarY;	
				x += nodeTemp.width + minDistanceX;
				
				nodeTemp.layoutVarX = nodeTemp.x;
				nodeTemp.layoutVarY = nodeTemp.y;		
				 	
				// clear access for recursion
				nodeTemp.isAccessed = false;
				// iterate over all attached subnodes
						
				for (Edge edgeTemp = nodeTemp.getFirstEdge(); edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
				{
					if (edgeTemp.isAccessed == false)
						continue;
					
					edgeTemp.isAccessed = false;
							
					if (edgeTemp.target != nodeTemp) 
					{
						calcXCoord( edgeTemp.target, x);
					} else 
					{
						calcXCoord(edgeTemp.source, x);
					}
				}//for edgeTemp
			}
			
			private double calcYCoord(Node nodeTemp, double y)
			{
				double newY = y;
				
				if (nodeTemp.isAccessed == true)
				{
					return ( -1);
				}
						
				nodeTemp.isAccessed = true;		
				
				if (nodeTemp.layoutVarY > 0) 
				{
					nodeTemp.isAccessed = false;
					return ( -1);
				}
						
				Node node1= null;
				Node node2= null;
						
				if (nodeTemp.getFirstEdge() != null)
				{
					for (Edge edgeTemp = nodeTemp.getFirstEdge(); edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
					{
						if ( edgeTemp.isAccessed == false)
						{
								continue;
						}
							
						edgeTemp.isAccessed = false;
						double tmpY ;
								
						Node tmpNode = null;
						if (edgeTemp.target != nodeTemp) 
						{
							tmpY= calcYCoord( edgeTemp.target, newY);
							tmpNode = edgeTemp.target;
						} else 
						{
							tmpY= calcYCoord(edgeTemp.source, newY);
							tmpNode = edgeTemp.source;
						}	
								
						if( tmpY >= 0 ) 
						{
							newY= tmpY;
							if( node1 == null )
							{	
								node1 = tmpNode;
							} else
							{
								node2 = tmpNode;
							}
						}
					}//for edgeTemp			
				}
						
				if( node1 == null ) 
				{
					// No subnodes -> Use proposed coordinate
							
					nodeTemp.y = (float)newY;
					nodeTemp.layoutVarX = (float)nodeTemp.x;
					nodeTemp.layoutVarY = (float)newY;
						
					newY += nodeTemp.height + minDistanceY;
					
				}
				else if (node2 == null) 
				{			
					double tmpY = 0;
					
					nodeTemp.y = (float)node1.y;
					tmpY= (double)(nodeTemp.y) + nodeTemp.height + minDistanceY;
					
					nodeTemp.layoutVarX = (float)nodeTemp.x;
					nodeTemp.layoutVarY = (float)nodeTemp.y;
					
					if( tmpY > newY )
						newY= tmpY;
				} else 
				{	
					double tmpY = 0;
			
					nodeTemp.y = (float)(node1.y + node2.y) / 2;
					tmpY= ((int)( node1.y + node2.y) / 2 ) + nodeTemp.height + minDistanceY;
					
					nodeTemp.layoutVarX = nodeTemp.x;
					nodeTemp.layoutVarY = nodeTemp.y;
							
					if( tmpY > newY )
						newY= tmpY;
				}
				// clear access for recursion
				nodeTemp.isAccessed = false;

				return newY;
			}
		};
	}//Algorithm createAlgorithm	
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field minDistanceX$FIELD;
	public static final Type.Field minDistanceY$FIELD;
	public static final Type.Field topDown$FIELD;

	public static class Type extends Layout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (TreeLayout representative, de.grogra.persistence.SCOType supertype)
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
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					((TreeLayout) o).topDown = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					return ((TreeLayout) o).topDown;
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((TreeLayout) o).minDistanceX = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((TreeLayout) o).minDistanceY = (float) value;
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
					return ((TreeLayout) o).minDistanceX;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((TreeLayout) o).minDistanceY;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new TreeLayout ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (TreeLayout.class);
		minDistanceX$FIELD = Type._addManagedField ($TYPE, "minDistanceX", Type.Field.PRIVATE   | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		minDistanceY$FIELD = Type._addManagedField ($TYPE, "minDistanceY", Type.Field.PRIVATE   | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		topDown$FIELD = Type._addManagedField ($TYPE, "topDown", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 2);
		$TYPE.validate ();
	}

//enh:end
}//class TreeLayout
	

