
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

import java.util.Vector;
import javax.vecmath.Point2d;
import de.grogra.imp2d.layout.Node;
import de.grogra.imp2d.layout.Layout.Algorithm;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;

/** the original from: Dexu Zhao
 * This class implements a graph changing layout. The nodes will be
 * arranged in sugiyama style
 * 
 */
public class SugiyamaLayout extends Layout 
{	
	//variables
	
	//enh:sco
	
	private double fGridAreaSize = Integer.MIN_VALUE;
	private boolean topDown = false;
	//enh:field
	
	private double minDistanceX = 2.0;
	//enh:field
	
	private double minDistanceY = 1.0;
	//enh:field
	
	private Point2d fSpacing;
	private Hashtable fWrappers = new Hashtable(1500);
	private Vector fLevels = new Vector();
	List fMovements = null;
	int fMovementsCurrentLoop = -1;
	int fMovementsMax = Integer.MIN_VALUE;
	int iteration = 0;
	
	@Override
	protected Algorithm createAlgorithm ()
	{
		return new Algorithm ()
		{
			@Override
			protected void layout (Node nodes)
			{
				GraphUtilities gu = new GraphUtilities();
				gu.setAllNodesEdgesAccessed(nodes, true);
				fSpacing  = new Point2d(minDistanceX, minDistanceY);
				
				fGridAreaSize = Integer.MIN_VALUE;
				fWrappers = new Hashtable(1500);
				fLevels = new Vector();
				fMovements = null;
				fMovementsCurrentLoop = -1;
				fMovementsMax = Integer.MIN_VALUE;
				iteration = 0;
			
				fGridAreaSize = Double.MIN_VALUE;
				Point2d maxBounds = new Point2d(0,0);
				for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
				{
					maxBounds.set(Math.max(nodeTemp.height,maxBounds.x), Math.max(nodeTemp.width,maxBounds.y));
				}//for nodeTemp
						
				if (fSpacing.x == 0)
				{
					fSpacing.x = 2 * maxBounds.x;
				}
						
				if (fSpacing.y == 0)
				{
					fSpacing.y = 2 * maxBounds.y;
				}

				LinkedList roots = gu.getRoots(nodes);
				gu.setAllNodesEdgesAccessed(nodes, true);
				//assign nodes to layers		
				for (int i = 0; i < roots.size(); i++)
				{
					fillLevels(fLevels, 0, (Node)roots.get(i));
				}//for nodeTemp
				
				//solveEdgeCrosses
				fMovements = new ArrayList(100);
				fMovementsCurrentLoop = -1;
				fMovementsMax = Integer.MIN_VALUE;
				iteration = 0;
						
				while (fMovementsCurrentLoop != 0)
				{
					//reset the movements per loop count
					fMovementsCurrentLoop = 0;
							
					//top down
					for (int i = 0; i < fLevels.size() -1; i++)
					{
						fMovementsCurrentLoop += solveEdgeCrosses(true, fLevels, i);
					}//for i
							
					//bottom up
					for (int i = fLevels.size() - 1; i >= 1; i--)
					{
						fMovementsCurrentLoop += solveEdgeCrosses(false, fLevels, i);
					}//for i
							
					//updateProgress4Movements
					//adds the current loop count
					fMovements.add(new Integer(fMovementsCurrentLoop));
					iteration++;
					//if the current loop count is higher than the max movements count
					//memorize the new max
					if (fMovementsCurrentLoop > fMovementsMax)
					{
						fMovementsMax = fMovementsCurrentLoop;
					}
				}//while
						
				//moveToBarycenter
				this.moveToBarycenter(nodes, fLevels);
				//getMinBounds
				Point2d min = new Point2d(10000, 10000);
				for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
				{
					min.set(Math.min(nodeTemp.height, min.x), Math.min(nodeTemp.width, min.y));
				}//for nodeTemp
						
				//updateGraph
				for (int rowCount = 0; rowCount < fLevels.size(); rowCount++) 
				{
					ArrayList level = (ArrayList) fLevels.get(rowCount);
					for (int j = 0;	j < level.size();j++) 
					{
						NodeWrapper wrapper = (NodeWrapper) level.get(j);
						Node nodeTemp  = (Node)wrapper.getNode();
							
						if (nodeTemp == null)
						{
							continue;
						}
						nodeTemp.x = (float)(min.x + fSpacing.x * ((topDown) ? wrapper.getGridPosition() : rowCount));
						nodeTemp.y = -(float)(min.y + fSpacing.y * ((topDown) ? rowCount :  wrapper.getGridPosition()));
					}//for j
				}//for rowCount
			}
			
			protected void updateProgress4Movements() 
			{
				// adds the current loop count
				fMovements.add(new Integer(fMovementsCurrentLoop));
				iteration++;

				// if the current loop count is higher than the max movements count
				// memorize the new max
				if (fMovementsCurrentLoop > fMovementsMax) {
					fMovementsMax = fMovementsCurrentLoop;
				}
			}
					
			protected void fillLevels(Vector levels, int level, Node nodeTemp)
			{
				if (nodeTemp == null)
				{
					return;
				}
				if (levels.size() == level)
				{
					levels.add(level, new ArrayList());
				}
				if (nodeTemp.isAccessed == false)
				{
					return;
				}

				nodeTemp.isAccessed = false;
				ArrayList currentLevel = (ArrayList)levels.get(level);
				int numberForTheEntry = currentLevel.size();
				NodeWrapper wrapper = new NodeWrapper(level, numberForTheEntry, nodeTemp);
				currentLevel.add(wrapper);
				fWrappers.put(nodeTemp, wrapper);
				if (nodeTemp.getFirstEdge() != null)
				{
					Edge edges = nodeTemp.getFirstEdge();
					for (Edge edgeTemp = edges; edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
					{
							if (edgeTemp.isAccessed == false)
							{
								continue;
							}
							edgeTemp.isAccessed = false;
							Node subINode = edgeTemp.target;
							if (subINode != nodeTemp)
							{
								fillLevels(levels, (level + 1), subINode);
							}
					}//for edgeTemp
				}
				if (currentLevel.size() > fGridAreaSize)
				{
					fGridAreaSize = currentLevel.size();
				}
			}//void fillLevels
					
			protected int solveEdgeCrosses(boolean isTopdown,Vector levels,int levelIndex) 
			{
				// Get the current level
				ArrayList currentLevel = (ArrayList) levels.get(levelIndex);
				int movements = 0;

				// restore the old sort
				Object[] levelSortBefore = currentLevel.toArray();

				// new sort
				Collections.sort(currentLevel);

				// test for movements
				for (int j = 0; j < levelSortBefore.length; j++)
				{
					if (((NodeWrapper) levelSortBefore[j]).getEdgeCrossesIndicator()
						!= ((NodeWrapper) currentLevel.get(j))
							.getEdgeCrossesIndicator()) {
						movements++;

					}
				}//for j

				// Colections Sort sorts the highest value to the first value
				for (int j = currentLevel.size() - 1; j >= 0; j--) 
				{
					NodeWrapper sourceWrapper = (NodeWrapper) currentLevel.get(j);

					Node sourceNode = (Node)sourceWrapper.getNode();
							
					if(sourceNode.getFirstEdge() != null)
					{
						Edge edges = sourceNode.getFirstEdge();
						Node targetNode = null;
						if (isTopdown)
						{
							for (Edge edgeTemp = edges; edgeTemp != null; edgeTemp = edgeTemp.getNext(sourceNode)) 
							{			
								targetNode= edgeTemp.target;
										
								if (targetNode == null)
									continue;
								NodeWrapper targetWrapper = (NodeWrapper)fWrappers.get(targetNode);
											
								if (targetWrapper != null 
										&& targetWrapper.getLevel() > levelIndex)
								{
									targetWrapper.addToEdgeCrossesIndicator
									(sourceWrapper.getEdgeCrossesIndicator());
								}
							}//for edgeTemp		
						}
												
						if (!isTopdown)
						{
							for (Edge edgeTemp = edges; edgeTemp != null; edgeTemp = edgeTemp.getNext(sourceNode)) 
							{			
								targetNode= edgeTemp.source;
										
								if (targetNode == null)
									continue;
										
								NodeWrapper targetWrapper = (NodeWrapper)fWrappers.get(targetNode);
											
								if (targetWrapper != null 
										&& targetWrapper.getLevel() < levelIndex)
								{
									targetWrapper.addToEdgeCrossesIndicator
									(sourceWrapper.getEdgeCrossesIndicator());
								}
							}//for edgeTemp
						}
					}
				}//for j	
				return movements;					
			}//int solveEdgeCrosses	
					
			protected void moveToBarycenter(Node nodes,Vector levels) 
			{
				for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
				{
					NodeWrapper currentwrapper =(NodeWrapper)fWrappers.get(nodeTemp);
					if (nodeTemp.getFirstEdge() != null)
					{
						Edge edges = nodeTemp.getFirstEdge();
						Node neighborNode = null;
								
						for (Edge edgeTemp = edges; edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
						{			
							neighborNode= edgeTemp.target;
									
							if (neighborNode == null)
								continue;
							NodeWrapper neighborWrapper = (NodeWrapper)fWrappers.get(neighborNode);
									
							if (currentwrapper == null
								|| neighborWrapper == null
								|| currentwrapper.level == neighborWrapper.level)
							{
								continue;
							}	
									
							currentwrapper.priority++;
						}//edgeTemp
										
						for (Edge edgeTemp = edges; edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
						{		
							neighborNode= edgeTemp.source;
									
							if (neighborNode == null)
								continue;
							NodeWrapper neighborWrapper = (NodeWrapper)fWrappers.get(neighborNode);
									
							if (currentwrapper == null
								|| neighborWrapper == null
								|| currentwrapper.level == neighborWrapper.level)
							{
								continue;
							}	
							currentwrapper.priority++;
						}//for edgeTemp
					}
				}//for nodeTemp			
						
				for (int i = 0; i < levels.size(); i++)
				{
					ArrayList level = (ArrayList)levels.get(i);
							
					for (int j = 0; j < level.size(); j++)
					{
								
						NodeWrapper wrapper = (NodeWrapper)level.get(j);
						wrapper.setGridPosition(j);
					}
				}
						
				fMovements.clear();
				fMovementsCurrentLoop = -1;
				fMovementsMax = Integer.MIN_VALUE;
				iteration = 0;

				//int movements = 1;
						
				while (fMovementsCurrentLoop != 0) 
				{
					//System.out.println("run moveToBarycenter");
					// reset movements
					fMovementsCurrentLoop = 0;
					//System.out.println("size of Levels in movements: "+levels.size());
					// top down
					for (int i = 1; i < levels.size(); i++) 
					{
						//System.out.println("movementscrrentLoop in down: " + movementsCurrentLoop);
						fMovementsCurrentLoop += moveToBarycenter(levels, i);;
							
					}

					// bottom up
					for (int i = levels.size() - 1; i >= 0; i--) 
					{
						//System.out.println("movementscrrentLoop in top: " + movementsCurrentLoop);
						fMovementsCurrentLoop += moveToBarycenter(levels, i);;
					}

					this.updateProgress4Movements();
				}
			}//void moveToBaryCenter
					
			protected int moveToBarycenter(List levels,int levelIndex) 
			{
				int movements = 0;
				ArrayList currentLevel = (ArrayList) levels.get(levelIndex);
						
				for (int currentIndexInTheLevel = 0;
					currentIndexInTheLevel < currentLevel.size();
					currentIndexInTheLevel++) 
				{

					NodeWrapper sourceWrapper =
						(NodeWrapper) currentLevel.get(currentIndexInTheLevel);

					float gridPositionsSum = 0;
					float countNodes = 0;

					Node sourceNode = (Node)sourceWrapper.getNode();
					if (sourceNode.getFirstEdge() != null)
					{
						Edge edges = sourceNode.getFirstEdge();	
						Node neighborNode = null;
								
						for(Edge edgeTemp = edges; edgeTemp != null; edgeTemp = edgeTemp.getNext(sourceNode)) 
						{
							neighborNode = edgeTemp.target;
									
							if (neighborNode == null)
								continue;
							NodeWrapper neighborWrapper = (NodeWrapper)fWrappers.get(neighborNode);
									
							if (sourceWrapper == neighborWrapper)
								continue;
							if (neighborWrapper == null ||
								neighborWrapper.getLevel() == levelIndex)
							{
								continue;
							}	
									
							gridPositionsSum += neighborWrapper.getGridPosition();
							countNodes++;
						}//for edgeTemp
										
						for (Edge edgeTemp = edges; edgeTemp != null; edgeTemp = edgeTemp.getNext(sourceNode)) 
								{			
							neighborNode = edgeTemp.source;
									
							if (neighborNode == null)
								continue;
							NodeWrapper neighborWrapper = (NodeWrapper)fWrappers.get(neighborNode);
									
							if (sourceWrapper == neighborWrapper)
								continue;
							if (neighborWrapper == null ||
								neighborWrapper.getLevel() == levelIndex)
							{
								continue;
							}	
									
							gridPositionsSum += neighborWrapper.getGridPosition();
							countNodes++;
						}
							   
						if (countNodes > 0) 
						{
							float tmp = (gridPositionsSum / countNodes);
							int newGridPosition = Math.round(tmp);
							boolean toRight =
								(newGridPosition > sourceWrapper.getGridPosition());

							boolean moved = true;

							while (newGridPosition != sourceWrapper.getGridPosition()
								&& moved) {
								moved =
									move(
										toRight,
										currentLevel,
										currentIndexInTheLevel,
										sourceWrapper.getPriority());

								if (moved)
									movements++;

							}
						}
					}			
				}
						
				return movements;
			}// int moveToBarycenter
					
			protected boolean move(boolean toRight,ArrayList currentLevel,
					int currentIndexInTheLevel,	int currentPriority)
			{

				NodeWrapper currentWrapper =
					(NodeWrapper) currentLevel.get(currentIndexInTheLevel);

				boolean moved = false;
				int neighborIndexInTheLevel =
				currentIndexInTheLevel + (toRight ? 1 : -1);
				int newGridPosition =
				currentWrapper.getGridPosition() + (toRight ? 1 : -1);

				if (0 > newGridPosition || newGridPosition >= fGridAreaSize) {
				return false;
				}

				if (toRight
					&& currentIndexInTheLevel == currentLevel.size() - 1
					|| !toRight
					&& currentIndexInTheLevel == 0) {
						moved = true;
				} else 
				{
					NodeWrapper neighborWrapper =
						(NodeWrapper) currentLevel.get(neighborIndexInTheLevel);

					int neighborPriority = neighborWrapper.getPriority();

					if (neighborWrapper.getGridPosition() == newGridPosition)
					{
						if (neighborPriority >= currentPriority) 
						{
							return false;
						} else 
						{
							moved =	move(toRight,currentLevel,
								neighborIndexInTheLevel,currentPriority);
						}
					} else 
					{
						moved = true;
					}
				}
				if (moved) 
				{
					currentWrapper.setGridPosition(newGridPosition);
				}
				return moved;
			}
		};
	}
			
	
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field topDown$FIELD;
	public static final Type.Field minDistanceX$FIELD;
	public static final Type.Field minDistanceY$FIELD;

	public static class Type extends Layout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SugiyamaLayout representative, de.grogra.persistence.SCOType supertype)
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
				case Type.SUPER_FIELD_COUNT + 0:
					((SugiyamaLayout) o).topDown = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((SugiyamaLayout) o).topDown;
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((SugiyamaLayout) o).minDistanceX = (double) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((SugiyamaLayout) o).minDistanceY = (double) value;
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
					return ((SugiyamaLayout) o).minDistanceX;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((SugiyamaLayout) o).minDistanceY;
			}
			return super.getDouble (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SugiyamaLayout ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SugiyamaLayout.class);
		topDown$FIELD = Type._addManagedField ($TYPE, "topDown", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 0);
		minDistanceX$FIELD = Type._addManagedField ($TYPE, "minDistanceX", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 1);
		minDistanceY$FIELD = Type._addManagedField ($TYPE, "minDistanceY", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 2);
		$TYPE.validate ();
	}

//enh:end
}// class SugiyamaLayout
	
class NodeWrapper implements Comparable 
{
	private double edgeCrossesIndicator = 0;
	private int additions = 0;
	int level = 0;
	int gridPosition = 0;
	int priority = 0;
	Object node = null;

	NodeWrapper(int level,double edgeCrossesIndicator,Object nd) 
	{
		this.level = level;
		this.edgeCrossesIndicator = edgeCrossesIndicator;
		this.node = nd;
		additions++;
	}

	Object getNode() 
	{
		return node;
	}

	
	void resetEdgeCrossesIndicator() 
	{
		edgeCrossesIndicator = 0;
		additions = 0;
	}

	double getEdgeCrossesIndicator() 
	{
		if (additions == 0)
			return 0;
		return edgeCrossesIndicator / additions;
	}

	void addToEdgeCrossesIndicator(double addValue) 
	{
		edgeCrossesIndicator += addValue;
		additions++;
	}
	
	int getLevel() 
	{
		return level;
	}

	
	int getGridPosition() 
	{
		return gridPosition;
	}

	void setGridPosition(int pos) 
	{
		this.gridPosition = pos;
	}

	void incrementPriority() 
	{
		priority++;
	}

	int getPriority() 
	{
		return priority;
	}

	public int compareTo(Object compare) 
	{
		if (((NodeWrapper) compare).getEdgeCrossesIndicator()
			== this.getEdgeCrossesIndicator())
			return 0;

		double compareValue =
			(((NodeWrapper) compare).getEdgeCrossesIndicator()
				- this.getEdgeCrossesIndicator());

		return (int) (compareValue * 1000);

	}//int compareTo
}//NodeWrapper
