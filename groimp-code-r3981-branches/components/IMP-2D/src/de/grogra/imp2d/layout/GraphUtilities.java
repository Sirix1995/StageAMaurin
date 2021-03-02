package de.grogra.imp2d.layout;

import de.grogra.imp2d.layout.Node;
import java.util.*;
import de.grogra.imp2d.layout.Edge;

/** A Utility-Class with helping methods for layouting graphs
 * especially for hierarchical graph layouts
 * 
 * @date 26.03.2007
 */
public class GraphUtilities {
	
	/*
	 * Converting Node nodes to a linked list
	 */
	public LinkedList getNodesList(Node nodes)
	{
		LinkedList roots = getRoots(nodes);
		LinkedList nodesList = new LinkedList();
		nodesList.add(roots.get(0));
		for (Node n = nodes; n != null; n = n.next)
		{
			if (!n.equals(roots.get(0)))
			{
				nodesList.add(n);
			}
		}
		
		return nodesList;
	}
	
	/**
	 * Getting all nodes converted to a linked list and
	 * arranged in depth first search-style
	 * @param nodes
	 * @return
	 */
	public LinkedList getNodesListDFS(Node nodes)
	{
		setAllNodesEdgesAccessed(nodes, false);
		LinkedList roots = getRoots(nodes);
		LinkedList nodesList = new LinkedList();
		
		for (int i = 0; i < roots.size(); i++)
		{
			addToNodesListDFS((Node)roots.get(i), nodesList);
		}
		
		return nodesList;
	}
	
	/**
	 * Private adder for adding a node to a linked list
	 */
	private void addToNodesListDFS(Node n, LinkedList nodesList)
	{
		if (!n.isAccessed)
		{	
			n.isAccessed = true;
			nodesList.add(n);
		} else
		{
			return;
		}
		for (Edge e = n.getFirstEdge(); e != null; e = e.getNext(n))
		{
			if (e.target != n)
			{
				addToNodesListDFS(e.target, nodesList);
			}
		}
	}
	
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
	public int getNodeCount(Node nodes)
	{	
		int count = 0;
		int edgeCount = 0;
		setAllEdgesAccessed(nodes, false);
		for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
		{
			for (Edge edgeTemp = nodeTemp.getFirstEdge(); edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
			{
				if (!edgeTemp.isAccessed)
				{
					edgeTemp.isAccessed = true;
					edgeCount++;
				}
			}
			count++;
		}//for nodeTemp
		System.out.println("nodeCount: "+count);
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
	
	/* 
	 * Searching for roots in the graph
	 * @param node the first Node of a node list
	 */
	public LinkedList getRoots(Node nodes)
	{
		LinkedList roots = new LinkedList();
		boolean isRoot = false;
		setAllNodesEdgesAccessed(nodes, true);
		for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
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
			}
		}
		//searching
		setAllNodesEdgesAccessed(nodes, true);
		for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
		{
			for (Edge edgeTemp = nodeTemp.getFirstEdge(); edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
			{
				Node notRoot = edgeTemp.target;
				notRoot.isAccessed = false;
				edgeTemp.isAccessed = false;
			}//for edgeTemp
		}//for nodeTemp
		
		for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
		{
			if (nodeTemp.isAccessed == true)
			{
				roots.add(nodeTemp);
			}//if
		}//for nodeTemp
		setAllNodesEdgesAccessed(nodes, false);
		
		return roots;
	}//void searchRoots
	
	/**
	 * Dividing the nodes of the graph into connectedNodes (= nodes that
	 * have edges to other nodes) and unconnectedNodes (= nodes that don't
	 * have edges to other nodes of the graph)
	 */
	public LinkedList getConnectedNodes(Node nodes)
	{
		LinkedList connectedNodes = new LinkedList();
		
		for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
		{
			//	test if nodeTemp has connections to other nodes or not
			if (nodeTemp.getFirstEdge() != null)
			{
				connectedNodes.add(nodeTemp);
			}
		}//for nodeTemp
		
		return connectedNodes;
	}//void getConnectedNodes
	
	/*
	 * Returns the number of nodes that have edges to other nodes
	 */
	protected int getConnectedNodesSize(Node nodes)
	{
		int connCounter = 0;
		for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
		{
			if (nodeTemp.getFirstEdge() != null)
			{
				connCounter++;
			}//if
		}//for
		
		return connCounter;
	}//int getConnectedNodesSize
	
	/**
	 * Dividing the nodes of the graph into connectedNodes (= nodes that
	 * have edges to other nodes) and unconnectedNodes (= nodes that don't
	 * have edges to other nodes of the graph)
	 */
	public LinkedList getUnconnectedNodes(Node nodes)
	{
		LinkedList unconnectedNodes = new LinkedList();
		
		for (Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
		{
			//	test if nodeTemp has connections to other nodes or not
			if (nodeTemp.getFirstEdge() != null)
			{
				unconnectedNodes.add(nodeTemp);
			}
		}//for nodeTemp
		
		return unconnectedNodes;
	}//LinkedList getUnconnectedNodes
}
