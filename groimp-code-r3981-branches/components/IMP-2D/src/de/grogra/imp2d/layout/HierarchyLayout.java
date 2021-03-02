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
import de.grogra.imp2d.layout.Node;

/**
 * A <code>HierarchyLayout</code> computes a graph layout based on a
 * hierarchy model. The concrete hierarchy model has to be implemented 
 * in subclasses (e. g. TreeLayout, SugiyamaLayout)
 * 
 * @author Birka Fonkeng
 * @date 20.07.2006
 */
public abstract class HierarchyLayout extends Layout
{
	Vector fRoots;//storing the nodes that are roots of the graph
	Vector unconnectedNodes;//storing all nodes with no edges to other nodes
	Vector connectedNodes;//storing all nodes that are connected by edges to other nodes
	int nodeCount;//storing the number of nodes of the graph
	Node node;
	
	/*
	 * Setting all nodes and edges of the graph marked or unmarked
	 * @param value <code>true</code> if unmarked, <code>false</code> if marked
	 */
	public void setAllNodesEdgesAccessed(Node nodeList, boolean value)
	{
		for (Node nodeTemp = nodeList; nodeTemp != null; nodeTemp = nodeTemp.next)
		{
			nodeTemp.isAccessed = value;
			for (Edge edgeTemp = nodeTemp.getFirstEdge(); edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
			{
				edgeTemp.isAccessed = value;
			}//for edgeTemp
		}//for nodeTemp
	}//void setAllNodesEdgesAccessed
	
	/*
	 * Setting all edges of the graph marked or unmarked
	 * @param value <code>true</code> if unmarked, <code>false</code> if marked
	 */
	public void setAllEdgesAccessed(Node nodeList, boolean value)
	{
		for (Node nodeTemp = nodeList; nodeTemp != null; nodeTemp = nodeTemp.next)
		{
			for (Edge edgeTemp = nodeTemp.getFirstEdge(); edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
			{
				edgeTemp.isAccessed = value;
			}//for edgeTemp
		}//for nodeTemp
	}//void setAllEdgesAccessed
	
	/**
	 * Returns the number of nodes of the graph
	 * @return nodeCount number of nodes
	 */
	public int getNodeCount()
	{	
		int count = 0;
		for (Node nodeTemp = node; nodeTemp != null; nodeTemp = nodeTemp.next)
		{
			count++;
		}//for nodeTemp
		
		return count;
	}//getNodeCount
	
	/**
	 * Setting a concrete Node marked or unmarked
	 * @param node Node that has to be marked
	 * @param value <code>true</code> if unmarked, <code>false</code> if marked
	 */
	public void setNodeAccessed(Node nodeTemp, boolean value)
	{
		nodeTemp.isAccessed = value;
	}//void setNodeAccessed
	
	/**
	 * Setting a concrete edge marked or unmarked
	 * @param edge Edge that has to be marked
	 * @param value <code>true</code> if unmarked, <code>false</code> if marked
	 */
	public void setEdgeAccessed(Edge edgeTemp, boolean value)
	{
		edgeTemp.isAccessed = value;
	}//void setEdgeAccessed
	
	/**
	 * checks if a node is marked or unmarked
	 * @param node Node that will be checked
	 * @return <code>true</code> if unmarked, <code>false</code> if marked
	 */
	public boolean isNodeAccessed(Node nodeTemp)
	{
		return nodeTemp.isAccessed;
	}//void isNodeAccessed
	
	/**
	 * checks if a edge is marked or unmarked
	 * @param edge Edge that will be checked
	 * @return <code>true</code> if unmarked, <code>false</code> if marked
	 */
	public boolean isEdgeAccessed(Edge edgeTemp)
	{
		return edgeTemp.isAccessed;
	}//isEdgeAccessed
	
	
	public class HAlgorithm extends Algorithm
	{
		@Override
		protected void layout(Node nodes)
		{
			node = nodes;
			fRoots = new Vector();
			unconnectedNodes = new Vector();
			connectedNodes = new Vector();
			nodeCount = getNodeCount();
			
			setAllNodesEdgesAccessed(node, true);
			disassociateNodes();
			setAllNodesEdgesAccessed(node, true);
			searchRoots();
			//setAllNodesEdgesAccessed(true);
			drawLayout(node);
		}//void layout
		
		
		/* 
		 * Searching for roots in the graph
		 * @param node the first Node of a node list
		 */
		void searchRoots()
		{
			boolean isRoot = false;
			setAllNodesEdgesAccessed(node, true);
			for (Node nodeTemp = node; nodeTemp != null; nodeTemp = nodeTemp.next)
			{
				//test if nodeTemp has connections to other nodes or not
				if (nodeTemp.getFirstEdge() != null)
				{
					if (isNodeAccessed(nodeTemp))
					{
						isRoot = true;
						for (Edge edgeTemp = nodeTemp.getFirstEdge(); edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
						{
							if (!isEdgeAccessed(edgeTemp))
							{
								isRoot = false;
							}
						}
						if (isRoot)
						{
							//fRoots.add(nodeTemp);
							//System.out.println("root added: "+nodeTemp);
						}
						setNodeAccessed(nodeTemp, false);
					}
				
					//all neighbor nodes and edges of the root will be marked (=false)
					Node workingNode = nodeTemp;
					setNodeAccessed(workingNode, false);
					Edge workingNodeEdges = workingNode.getFirstEdge();
					for (Edge workingEdgeTemp = workingNodeEdges; workingEdgeTemp != null; workingEdgeTemp = workingEdgeTemp.getNext(workingNode))
					{
						setEdgeAccessed(workingEdgeTemp, false);
						setNodeAccessed(workingEdgeTemp.source, false);
						setNodeAccessed(workingEdgeTemp.target, false);
					}//for workingEdgeTemp
				} else
				{
				//	fRoots.add(nodeTemp);
					//System.out.println("root added: "+nodeTemp);
				}
			}
			//ab hier f�ngt das suchen an
			setAllNodesEdgesAccessed(node, true);
			for (Node nodeTemp = node; nodeTemp != null; nodeTemp = nodeTemp.next)
			{
				for (Edge edgeTemp = nodeTemp.getFirstEdge(); edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
				{
					Node notRoot = edgeTemp.target;
					notRoot.isAccessed = false;
					edgeTemp.isAccessed = false;
				}//for edgeTemp
			}//for nodeTemp
			
			for (Node nodeTemp = node; nodeTemp != null; nodeTemp = nodeTemp.next)
			{
				if (nodeTemp.isAccessed == true)
				{
					//System.out.println("root added2: "+nodeTemp);
					fRoots.add(nodeTemp);
				}//if
			}//for nodeTemp
			setAllNodesEdgesAccessed(node, false);
		}//void searchRoots
		
		/**
		 * Dividing the nodes of the graph into connectedNodes (= nodes that
		 * have edges to other nodes) and unconnectedNodes (= nodes that don't
		 * have edges to other nodes of the graph)
		 */
		public void disassociateNodes()
		{
			for (Node nodeTemp = node; nodeTemp != null; nodeTemp = nodeTemp.next)
			{
				//	test if nodeTemp has connections to other nodes or not
				if (nodeTemp.getFirstEdge() != null)
				{
					connectedNodes.add(nodeTemp);
				} else
				{
					unconnectedNodes.add(nodeTemp);
				}
			}//for nodeTemp
		}//void disassociateNodes
	}//class HAlgorithm
	
	@Override
	protected Algorithm createAlgorithm()
	{
		return new HAlgorithm();
	}//Algorithm createAlgorithm
	
	/**
	 * The special drawing algorithm will be implemented in subclasses
	 * @param node first Node of a list of nodes of the graph
	 */
	abstract void drawLayout(Node node);
}//class HierarchyLayout