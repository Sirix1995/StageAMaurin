
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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.vecmath.*;

/**
 * A second <code>ForceBasedLayout</code> computes a graph layout based on a 
 * force model. The concrete force model has to be implemented in subclasses.
 * (e. g. FruchtermanReingold, Eades, DavidsonHarel)
 * @author Birka Fonkeng
 * @date 26.07.2006
 */
public abstract class ForceBasedLayout2 extends Layout
{
	//variables
	Node node;
	
	public int fMaxIterations = 15;
	public int fMaxIterationsTemp;
	
	private float fUnconnectedNodeX = 0.4f;
	private float fUnconnectedNodeY = 0.4f;
	
	Vector connectedNodes = new Vector(); 
	Vector unconnectedNodes = new Vector();

	private float fNodeSizeX = 0;
	private float fNodeSizeY = 0; 
	
	protected float fDistanceMultiplierMin= 0.70f;
	protected float fDistanceMultiplierMax= 1f;
	protected float fDistanceMultiplier = 0;
	
	private float sMinDistanceX = 0.4f;
	private float sMinDistanceY = 0.4f;
	
	public class FBAlgorithm2 extends Algorithm
	{

		@Override
		protected void layout (Node nodes)
		{
			node = nodes;
			
			fMaxIterations = 15;
			
			// calculate the minimal distance between two nodes
			//init:
			calcMinDistance();
			disassociateNodes();
			fUnconnectedNodeY = (float)getMaxBounds(unconnectedNodes).y + 0.2f;

			int iter = fMaxIterations;
			Random rnd = new Random();
			
			for (Node nodeTemp = node; nodeTemp != null; nodeTemp = nodeTemp.next)
			{
				setRandomPosition(nodeTemp, rnd);
			}//for nodeTemp
			
			int unconnectedNodesCounter = 0;
			fUnconnectedNodeY = (float)getMaxBounds(unconnectedNodes).y + 0.2f;
			
			for (int i = 0; i < unconnectedNodes.size(); i ++)
			{
				double x = fUnconnectedNodeX;
				double y = 0.7f + unconnectedNodesCounter * fUnconnectedNodeY;
				unconnectedNodesCounter++;
				Node nodeTemp = (Node)unconnectedNodes.get(i);
				nodeTemp.x = (float)x;
				nodeTemp.y = (float)y;
			}//for i
			
			while (fMaxIterations > 0) 			
			{	
				//System.out.println("size of tttt fConnectedNodes : "+fConnectedNodes.size());			
				for (int k = 0; k < connectedNodes.size(); k++)
				{
					//System.out.println("run fConnectedNodes");
					Node nodeTemp = (Node)connectedNodes.get(k);
					Vector nodesTemp = allocationNodes(nodeTemp,connectedNodes);
					Vector connectionNodes = (Vector) nodesTemp.get(0);
					Point2d temp = new Point2d();
					Point2d count = new Point2d(0,0);
					Point2d count2 = new Point2d(0,0);				
					
					for (int i = 0; i < connectionNodes.size(); i++) 
					{
						temp.x = count.x + (springForce(nodeTemp, (Node)connectionNodes.get(i)).x);
						temp.y = count.y + (springForce(nodeTemp, (Node)connectionNodes.get(i)).y);
						count = temp;
					}//for i

					for (int j = 0; j < connectedNodes.size(); j++) 
					{
						if (!nodeTemp.equals((Node)connectedNodes.get(j)))
						{
							temp.x = count2.x + (repellingForce(nodeTemp, (Node)connectedNodes.get(j))).x;
							temp.y = count2.y + (repellingForce(nodeTemp, (Node)connectedNodes.get(j))).y;
							count2 = temp;
						}	
					}
					temp.x = count.x + count2.x;
					temp.y = count.y + count2.y;
					
					applyVectorToNode(nodeTemp, temp, getFDeltaFactor());
				} 
				fMaxIterations--;
			}
			fMaxIterations = iter;
			for (Node nodeTemp = node; nodeTemp != null;  nodeTemp = nodeTemp.next)
			{
				fNodeSizeX = Math.max(fNodeSizeX, nodeTemp.height);
				fNodeSizeY = Math.max(fNodeSizeY, nodeTemp.width);
			}//for nodeTemp
			
			 //fConnectedNodes = getConnectedNodes();
			 double positionX = fNodeSizeX + 3 * fUnconnectedNodeX ;
	         double diffX = getMinXNodePositionValue(null,connectedNodes) - positionX;
			 double diffY = getMinYNodePositionValue(null,connectedNodes) - 2 * fUnconnectedNodeY;		
			 moveNodesToUpperLeft(null,connectedNodes, diffX, diffY);
			 
			 //Test auf Ver�nderung der alten Variablen
			 System.out.println("fMaxIterations: "+fMaxIterations);
			 System.out.println("fUnconnectedNodeX: "+fUnconnectedNodeX);	
			 System.out.println("sMinDistanceX: "+sMinDistanceX);
			 System.out.println("sMinDistanceY: "+sMinDistanceY);
				
		}		
		
		protected void applyVectorToNode(Node nodeTemp, Point2d diff, double delta) 
		{
			Point2d pos = new Point2d(nodeTemp.x, nodeTemp.y);
			double fdelta = delta;
			float fMaxXChange = 0.4f;
			float fMaxYChange = 0.4f;
			double diffX = diff.x * fdelta;
			double diffY = diff.y * fdelta;

			double tt1 = 0;
			double tt2 = 0;

			if (diffX > 0)
				tt1 = Math.min(fMaxXChange, diffX);
			else {
				fMaxXChange = fMaxXChange * (-1);
				tt1 = Math.max(fMaxXChange, diffX);
			}

			if (diffY > 0)
				tt2 = Math.min(fMaxYChange, diffY);
			else {
				fMaxYChange = fMaxYChange * (-1);
				tt2 = Math.max(fMaxYChange, diffY);
			}

			pos.x += diffX;
			pos.y += diffY;	
			
			nodeTemp.x = (float)pos.x;
			nodeTemp.y = (float)pos.y;
		}//void applyVectorsToNode
		
		/**
		 * Moves the nodes in the layout
		 * @param nodes Hashtables of nodes
		 * @param no Vector of a node
		 * @param diffX the nodes will be moved for diffx-length horizontally
		 * @param diffY the nodes will be moved for diffy-length vertically
		 */
		protected void moveNodesToUpperLeft(Hashtable nodesTemp, Vector no,double diffX, double diffY) 
		{
			Enumeration e;
			if (no == null)
			{
				 e= nodesTemp.keys();
			} else
			{
				 e = no.elements();
			}
			
			while(e.hasMoreElements()) 
			{
				Node nodeTemp = (Node)e.nextElement();
				
				double newX = (float)(nodeTemp.x) - diffX;
				double newY = (float)(nodeTemp.y) - diffY;
				nodeTemp.x = (float)newX;
				nodeTemp.y = (float)newY;
			}//while
		}//void moveNodesToUpperLegt
		
		/**
		 * Getting the smallest x-value of the coordinates of the nodes
		 * @param nodes Hashtable of nodes
		 * @param no Vector of a node
		 * @return the smallest x-position value
		 */
		protected double getMinXNodePositionValue(Hashtable nodesTemp,Vector no) 
		{
			double minValue=Integer.MAX_VALUE;
			Enumeration e;
			if (no == null)
			{
				 e= nodesTemp.keys();
			} else
			{
				 e = no.elements();
			}
			
			while(e.hasMoreElements()) 
			{
				Node nodeTemp= (Node)e.nextElement();
				minValue= Math.min(minValue,(float)(nodeTemp.x));
			}//while
			if(minValue == Integer.MAX_VALUE)
				minValue= fNodeSizeX + fUnconnectedNodeX ;
			return minValue;
		}//double getMinXNodePositionValue

		/**
		 * Getting the smallest y-value of the coordinates of the nodes
		 * @param nodes Hashtable of nodes
		 * @param no Vector of a node
		 * @return the smallest y-position value
		 */
		protected double getMinYNodePositionValue(Hashtable nodesTemp , Vector no) 
		{
			double minValue=Integer.MAX_VALUE;
			
			Enumeration e;
			if (no == null)
			{
				 e= nodesTemp.keys();
			} else
			{
				 e = no.elements();
			}
			
			while(e.hasMoreElements()) 
			{
				Node nodeTemp = (Node)e.nextElement();
				minValue = Math.min(minValue,(float)(nodeTemp.y));
			}//while
			if(minValue == Integer.MAX_VALUE)
				minValue = fUnconnectedNodeY;
			return minValue;
		}//getMinYNodePositionValue
		
		protected Vector allocationNodes(Node nodeTemp, Vector vNodes) 
		{
			Vector result= new Vector(); 				 
			Vector connectionNodes= new Vector();
			Vector unconnectionNodes= new Vector();
			 
			for (int i = 0; i< vNodes.size(); i++)
			{
				Node runNode = (Node)vNodes.get(i);
			 	
			 	if(runNode == nodeTemp)
					continue;
			 	
			 	boolean isNodesConnection = false;
			 	for (Edge edgeTemp = runNode.getFirstEdge(); edgeTemp != null; edgeTemp = edgeTemp.getNext(runNode))
			 	{
			 		if ((edgeTemp.source).equals(runNode) && (edgeTemp.target).equals(nodeTemp) || (edgeTemp.source).equals(nodeTemp) && (edgeTemp.target).equals(runNode))
			 		{
			 			isNodesConnection = true;
			 			break;
			 		}
			 	}//for edgeTemp
			 	
			 	if (!isNodesConnection)
			 	{
			 		for (Edge edgeTemp = nodeTemp.getFirstEdge(); edgeTemp != null; edgeTemp = edgeTemp.getNext(nodeTemp))
				 	{
				 		if ((edgeTemp.source).equals(runNode) && (edgeTemp.target).equals(nodeTemp) || (edgeTemp.source).equals(nodeTemp) && (edgeTemp.target).equals(runNode))
				 		{
				 			isNodesConnection = true;
				 			break;
				 		}
				 	}//for edgeTemp
			 	}
				if (isNodesConnection) 
				{
					connectionNodes.add(runNode);
				}
				else {
					unconnectionNodes.add(runNode);
				}
			 }
			 result.add(0, connectionNodes);
			 result.add(1, unconnectionNodes);
			 return result; 
		}//void allocationNodes
		
		/**
		 * Calculating the minimum x- and y-distance
		 *
		 */
		protected void calcMinDistance() 
		{
			float minX= 0;
			float minY= 0;
			float workX= 0;
			float workY= 0;
			
			double multi= (double)(fDistanceMultiplierMax
						 - fDistanceMultiplierMin) / (double)180.0;		// x
			
			double multi1= (double)fDistanceMultiplierMin 
						- ( (double)0.2 * multi);	// y
			
			double result= (double)connectedNodes.size() * multi + multi1;
			
			if(result < fDistanceMultiplierMin)
				result= fDistanceMultiplierMin;
			
			if(result > fDistanceMultiplierMax)
				result= fDistanceMultiplierMax;
			
			fDistanceMultiplier= (float)result;
			
			for (Node nodeTemp = node; nodeTemp != null; nodeTemp = nodeTemp.next)
			{
				workX = (float)nodeTemp.height;
				workY = (float)nodeTemp.width;
				minX = Math.max(minX, workX);
				minY = Math.max(minY, workY);
			}//for nodeTemp
			
			sMinDistanceX = minX + (2 * fDistanceMultiplier);				
			sMinDistanceY = minY + (1 * fDistanceMultiplier);				
			fNodeSizeX= minX;
		}//void calcMinDistance
		
		/**
		 * Calculating the maximum height and width of the nodes
		 * @param nodesTemp Vector with nodes
		 * @return maxBounds the maximum width and height values
		 */
		protected Point2d getMaxBounds(Vector nodesTemp)
		{
			if (nodesTemp.size() == 0)
				return new Point2d(0,0);
			
			Point2d maxBounds = new Point2d();
			
			for (int i = 0; i < nodesTemp.size(); i++)
			{
				maxBounds.set(
				Math.max(((Node)(nodesTemp.get(i))).height, maxBounds.x),
				Math.max(((Node)(nodesTemp.get(i))).width, maxBounds.y));
				
			}//for i
			return maxBounds;
		}//Point2d getMaxBounds
		
		/** Dividing the nodes of the graph into the two sets
		 * of connectedNodes (nodes that have connections to other nodes
		 * and unconnectedNodes (nodes that have not edges to other nodes
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
	}

	@Override
	protected Algorithm createAlgorithm ()
	{
		return new FBAlgorithm2();
	}

	/** Calculating the repelling force of two nodes
	 */
	protected abstract Point2d repellingForce(Node firstNode, Node secondNode);

	/** Calculating the spring force of two nodes
	 */
	protected abstract Point2d springForce(Node firstNode, Node secondNode);
	
	/** getting the fDeltaFactor
	 */
	protected abstract double getFDeltaFactor();

	/** Calculating the inital positions of the nodes
	 */
	protected abstract void setRandomPosition(Node nodeTemp, Random rnd);
}//class ForceBasedLayout2
