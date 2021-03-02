
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.WeakHashMap;

import de.grogra.graph.impl.GraphManager;
import de.grogra.imp.IMPWorkbench;
import de.grogra.pf.ui.Workbench;



/**
 * This class implements a general purpose layout
 * @date 10.08.2010
 */
public class GeneralPurposeLayout extends Layout 
{	
	//enh:sco

	private float idealDistance  = 1.5f ;
	//enh:field

	private float yIdealDistance = 1.5f ;
	//enh:field
	
	// number maximum of iterations
	private int maxNbOfSteps = 25000 ;
	//enh:field
	
	private boolean fast = false ;
	//enh:field	
	
	private boolean startAgain = false ;
	//enh:field
	
	private final WeakHashMap<Object,Integer> oldNodes = new WeakHashMap<Object,Integer>() ;


	public GeneralPurposeLayout()
	{
		maxNbOfSteps$FIELD.setInt(this, 25000);
		
		idealDistance$FIELD.setFloat(this, 1.5f);
		idealDistance$FIELD.setFloat(this, 1.5f);
		
		startAgain$FIELD.setBoolean(this, false) ;
		fast$FIELD.setBoolean(this, false) ;
	}


	@Override
	protected Algorithm createAlgorithm ()
	{
		return new Algorithm ()
		{
			// global variables for the algorithm
			float springLength = 3 * idealDistance / 4 ;
			float forceConst = 0.5f * idealDistance * idealDistance * (idealDistance - springLength) ;
			final float criticalDistance = 0.01f * idealDistance ;
			boolean edgeVisitedValue = false ;
			//

			// compute the coordinates of the nodes for the 2D drawing
			@Override
			protected void layout (Node nodes)
			{
				// # # # # # # # # # # # # # # # # # # # # # # # # #
				// preparations for the algorithm
				
				// 'cleaning' the nodes and identifying the root
				// ( all the 'isAccessed' fields of all nodes / edges
				// are set to !node/edgeVisitedValue )
				boolean nodeVisitedValue = false ;
				Node root = initialisation(nodes, !nodeVisitedValue, !edgeVisitedValue) ;
				//
				
				// # # # # # # # # # # # # # # # # # # # # # # # # #
				
				
				// # # # # # # # # # # # # # # # # # # # # # # # # #
				// choosing an algorithm

				// probing the structure of the graph
				// (loops / trees )
				ArrayList<HashMap<Integer, Node>> loops = new ArrayList<HashMap<Integer,Node>>();
				HashMap<Integer, Node> treeNodes = new HashMap<Integer, Node>();

				goThroughLinkedNodes(root, null, loops, treeNodes, nodeVisitedValue) ;
				edgeVisitedValue = !edgeVisitedValue ;
				nodeVisitedValue = !nodeVisitedValue ;
				
			
				// --- the graph is a tree    ---
				// --- using a tree algorithm ---
				if ( loops.isEmpty() )
				{
					tree(root, 0, 0, nodeVisitedValue) ;
				}
				// ---

				// --- using a mixed algorithm ---
				else
				{
					if (startAgain)
					{
						oldNodes.clear() ;
					}
					treeLikeLayout(treeNodes, loops, root, maxNbOfSteps, nodeVisitedValue) ;
				}
				// ---


					
				/*
				 * old version : pure energy algorithm
				// --- using an energy algorithm ---
				else
				{
					if (startAgain)
					{
						oldNodes.clear() ;
					}
					energyAlgorithm(root, nodes, nodeVisitedValue) ;
				}
				// ---
				 */

				// # # # # # # # # # # # # # # # # # # # # # # # # #

			}//void layout




			// * * * Energy algorithm related functions * * *			
			
			/**
			 * calls an energy algorithm to layout
			 * the nodes ;
			 * 
			 * the nodes are divided in "old nodes"
			 * (nodes already present at the previous
			 * call) and "new nodes" (nodes recently
			 * added) ;
			 * 
			 * the new nodes are first placed according
			 * to an heuristic, and then their position
			 * is optimized
			 * 
			 * @param root root of the graph
			 * @param nodes 
			 * @boolean nodeVisitedValue a node has been 'visited'
			 * if its 'isAccessed field' is equals to this value
			 */
			private void energyAlgorithm(Node root, Node nodes, boolean layoutOnlyNewNodes, boolean nodeVisitedValue )
			{
				// # # # # # # # # # # # # # # # # # # # # # # # # #
				
				ArrayList<Node> allNodes = new ArrayList<Node>() ;
				ArrayList<Node> newNodes = new ArrayList<Node>() ;
				ArrayList<Node> oldNodesList = new ArrayList<Node>() ;
								
				// *** the first 2 nodes of ***
				// *** oldNodesList are the ***
				// *** root and the first   ***
				// *** node 'RGG root'      ***
	
				// root and firstNode are always considered
				// as 'old'
				Node firstNode = root.getFirstEdge().target ;
				oldNodesList.add(root) ;
				oldNodesList.add(firstNode) ;
	
				if ( !oldNodes.containsKey(root.object)  )
				{
					oldNodes.put(root.object, 0) ;
					root.x = 0 ;
					root.y = 0 ;
				}
				if ( !oldNodes.containsKey(firstNode.object))
				{
					oldNodes.put(firstNode.object, 0) ;
					firstNode.x = 0 ;
					firstNode.y = - idealDistance ;
				}		
	
				// listing new / old nodes
				for ( Node currentNode = nodes ; currentNode != null ; currentNode = currentNode.next )
				{	
					if ( oldNodes.containsKey(currentNode.object) )
					{
						if (currentNode != root && currentNode != firstNode)
						{
							oldNodesList.add(currentNode) ;
						}
					}
					else
					{
						if (currentNode != root && currentNode != firstNode)
						{
							newNodes.add(currentNode) ;
						}
					}
				}
				//				
	
				// *** order is important : ***
				// *** old nodes first      ***
				// *** new ones then        ***
				allNodes.addAll(oldNodesList) ;
				allNodes.addAll(newNodes) ;
				// ***                      ***
	
				// # # # # # # # # # # # # # # # # # # # # # # # # # 
	
	
				// # # # # # # # # # # # # # # # # # # # # # # # # #
				// performing the actual layouting part
	
				// placing the new nodes
				if ( newNodes.size() > 0 )
				{
					int maxDepth = 2 ;
					placingNewNodes(newNodes, oldNodesList, nodeVisitedValue, layoutOnlyNewNodes, maxDepth) ;
					nodeVisitedValue = !nodeVisitedValue ;
				}
				//
				if ( !layoutOnlyNewNodes)
				{
					// optimization of the whole layout
					optimizationMethods( allNodes, allNodes, maxNbOfSteps, nodeVisitedValue ) ;
				}
				else
				{
					// optimization of the new nodes only
					optimizationMethods( newNodes, allNodes, maxNbOfSteps, nodeVisitedValue ) ;
				}
				//
				centringAndRotating(root, firstNode, allNodes) ;
				//			
	
				// # # # # # # # # # # # # # # # # # # # # # # # # #
	
	
				// # # # # # # # # # # # # # # # # # # # # # # # # #
	
				// updating oldNodes
				for (Node currentNode : newNodes )
				{
					oldNodes.put(currentNode.object, 0) ;
				}
				//
	
				// # # # # # # # # # # # # # # # # # # # # # # # # #
				
			}//void energyAlgorithm	
			
			
			/**
			 * places the nodes in 'newNodes' :
			 * identifies the nodes linked to
			 * old nodes, and places these nodes
			 * and their children (up to a
			 * maximum depth of maxDepth) ;
			 * 
			 * repeat this until all newNodes are
			 * placed ;
			 * 
			 * the placing part starts with some
			 * heuristic placement, then completed
			 * by some optimization methods (see
			 * 'optimizationNewNodes' ) ;
			 * 
			 * @param newNodes
			 * @param oldNodesList
			 * @param edgeVisitedValue
			 * @param nodeVisitedValue
			 * @param maxDepth
			 * 
			 * @see optimizationNewNodes
			 */
			private void placingNewNodes(ArrayList<Node> newNodes, ArrayList<Node> oldNodesList, boolean nodeVisitedValue, boolean layoutOnlyNewNodes, int maxDepth)
			{				
				// allLayoutedNodes = all nodes layouted
				// at this point
				ArrayList<Node> allLayoutedNodes= new ArrayList<Node>() ;
				allLayoutedNodes.addAll(oldNodesList) ;
				//
				// newLayoutedNodes = all *new* nodes layouted
				// at this point
				ArrayList<Node> newLayoutedNodes = new ArrayList<Node>() ;
				//
				for (Node n : allLayoutedNodes)
				{
					n.isAccessed = nodeVisitedValue ;
				}
				//
				// identifying the 'new' nodes linked to 'old' nodes
				ArrayList<Node> links = linksNewOld(newNodes) ;
				//				
				while ( links.size() > 0 )
				{
					// placing the 'new' nodes that are linked to 'old' nodes first
					// heuristic placement
					for (Node linkNode : links )
					{
						placeNode(linkNode, nodeVisitedValue) ;
					}
					//
					// placing the child (of depth <= maxDepth)
					// of the 'link' nodes
					// heuristic placement
					ArrayList<Node> newLinkNodes = new ArrayList<Node>() ;
					ArrayList<Node> newPlacedNodes = new ArrayList<Node>() ;
					for (Node linkNode : links )
					{
						recursivelyDepthLimitedPlacingNode(linkNode, newPlacedNodes, newLinkNodes, nodeVisitedValue, 0, maxDepth) ;
					}
					//
					// 'newPlacedNodes' now contains all the newly placed nodes
					// except for the (now old)link-nodes
					// 'newLinkNodes' now contains the new 'link' nodes
					//
					// optimization :
					// the position of the new roughly placed
					// nodes will be improved according to
					// some optimization methods
					// updating 'allNodes'

					newPlacedNodes.addAll(links) ;
					//
					allLayoutedNodes.addAll(newPlacedNodes) ;

					if (layoutOnlyNewNodes)
					{
						// optimization of the position
						// of the new (layouted) nodes only
						newLayoutedNodes.addAll(newPlacedNodes) ;
						optimizationNewNodes(newPlacedNodes, newLayoutedNodes, allLayoutedNodes, !nodeVisitedValue) ;
					}
					else
					{	
						// optimization of the postion
						// of all (layouted) nodes
						optimizationNewNodes(newPlacedNodes, allLayoutedNodes, allLayoutedNodes, !nodeVisitedValue) ;
					}
					links = newLinkNodes ;
					//
				}
			}//void placingNewNodes


			/**
			 * controls the optimization of 
			 * the position of the nodes in
			 * 'listOfNodes' ;
			 * 
			 * the optimization is done by
			 * successively applying a 
			 * simulated annealing method
			 * (see simulatedAnnealing) and 
			 * a Newton method (see
			 * newtonOptimization )
			 * 
			 * @param listOfNodes
			 * @param nbStepMax
			 * @param nodeVisitedValue
			 * 
			 * @see simulatedAnnealing
			 * @see newtonOptimization
			 */
			private void optimizationMethods(ArrayList<Node> nodesToLayout, ArrayList<Node> listOfNodes, int nbStepMax, boolean nodeVisitedValue )
			{
				// repartition of the steps for the 3 methods
				int nbStepsNewton1 = (int) Math.floor(0.2 * nbStepMax ) ;
				int nbStepsSATot = (int) Math.floor(0.5 * nbStepMax ) ;
				int nbStepsNewton2 = (int) Math.floor(0.3 * nbStepMax ) ;
				//
				// optimization until a
				// roughly good position
				float sqNormMinGradFAST = (idealDistance * idealDistance) / 16 ;
				int stepsUsedNewton1 = newtonPlacement(nodesToLayout, listOfNodes, nbStepsNewton1, sqNormMinGradFAST, nodeVisitedValue) ;
				//
				// simulated annealing try
				//
				//
				nbStepsSATot += nbStepsNewton1 - stepsUsedNewton1 ;
				
				if (nbStepsSATot >= 2 * nodesToLayout.size() )
				{
					int nbAttempts ;
					int nbStepsSA ;
					
					if (nbStepsSATot / (10 * nodesToLayout.size()) < 20)
					{
						nbAttempts = (int) Math.floor(  Math.sqrt( nbStepsSATot/(2*nodesToLayout.size()) )  ) ;
						nbStepsSA = 2 * nbAttempts ;
					}
					else
					{
						nbAttempts = 10 ;
						nbStepsSA = nbStepsSATot / nbAttempts ;
					}
					
					//
					// to define here
					float initProba = 0.95f ;
					float lambdaMovement = (float) Math.exp( -3*Math.log(10)/nbStepsSA ) ;
					float lambdaProbability = lambdaMovement ;
					//
					simulatedAnnealing(listOfNodes, nodesToLayout, nbStepsSA, nbAttempts, initProba, lambdaProbability, lambdaMovement, nodeVisitedValue ) ;
					//
				}
				//	
				// 'precise' optimization
				float sqNormMinGrad = (idealDistance * idealDistance) / 100 ;
				newtonPlacement(nodesToLayout, listOfNodes, nbStepsNewton2, sqNormMinGrad, nodeVisitedValue) ;
				//
			}//void optimizationMethods

			/**
			 * fast layout for the nodes
			 * in loopHM (the nodes are
			 * places on the vertices
			 * of a regular polygon)
			 * 
			 * @param loopHM nodes to place
			 * @param nodeVisitedValue
			 */
			private void fastLoopLayout( HashMap<Integer, Node> loopHM, boolean nodeVisitedValue )
			{
				Node[] loop = new Node[loopHM.size()] ;
				loopHM.values().toArray(loop) ;
				
				int n = loopHM.size() ;
				
				if (n >=3)
				{
					float theta = 2 * (float)Math.PI / n ;
					float d =  idealDistance / ( 2 * (float)Math.sin(theta)) ;
					
					placeNodesCircle(theta, d, 0, loopHM, loop[0], nodeVisitedValue) ;
				}
				else if ( n == 2)
				{
					loop[0].x = 0 ;
					loop[0].y = 0 ;
					
					loop[1].x = 0 ;
					loop[1].y = -idealDistance ;
				}
				else if ( n ==1 )
				{
					loop[0].x = 0 ;
					loop[0].y = 0 ;
				}
			}//void fastLoopLayout
			
			
			/**
			 * recursively places the nodes in
			 * loopHM on a circle (diameter 2*d),
			 * with an angle theta between two
			 * consecutive nodes (thus forming
			 * a regular polygon if
			 * theta = 2*Pi / nbOfNodes) ;
			 * 
			 * starting from one node in loopHM,
			 * the nodes are placed according to
			 * a depth-first search ;
			 * 
			 * @param theta
			 * @param d
			 * @param p
			 * @param loopHM
			 * @param currentNode
			 * @param nodeVisitedValue
			 */
			private int placeNodesCircle(float theta, float d, int p, HashMap<Integer, Node> loopHM, Node currentNode, boolean nodeVisitedValue)
			{
				currentNode.isAccessed = nodeVisitedValue ;
				
				currentNode.x = d * (float) Math.cos(p * theta) ;
				currentNode.y = d * (float) Math.sin(p * theta) ;
				p ++ ;
				
				for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
				{
					Node otherNode = (currentEdge.target == currentNode )? currentEdge.source : currentEdge.target ;
					if ( !loopHM.containsKey(otherNode.object.hashCode()) )
					{
						continue ;
					}
					if (currentEdge.isAccessed == edgeVisitedValue)
					{
						currentEdge.isAccessed = !edgeVisitedValue ;
						continue ;
					}
					currentEdge.isAccessed = edgeVisitedValue ;
					
					if (otherNode.isAccessed == nodeVisitedValue)
					{
						continue ;
					}
					
					p = placeNodesCircle(theta, d, p, loopHM, otherNode, nodeVisitedValue) ;
					
				}
				currentNode.isAccessed = !nodeVisitedValue ;
				return p ;
			}//void placeNodeCircle
			
			
			/**
			 * controls the call of
			 * optimization methods
			 * for layouting loops ;
			 * 
			 * @param loopHM
			 * @param nbStepMax
			 * @param nodeVisitedValue
			 */
			private void loopLayout( HashMap<Integer, Node> loopHM, int nbStepMax, boolean nodeVisitedValue, boolean init )
			{
				Node[] loop = new Node[loopHM.size()] ;
				loopHM.values().toArray(loop) ;
				
				//
				int n = loopHM.size() ;
				//
				if (init)
				{
					placeNewNodesLoop(loopHM, loop, nodeVisitedValue) ;
				}
				//
				
				// repartition of the steps for the 3 methods
				int nbStepsNewton1 = (int) Math.floor(0.2 * nbStepMax ) ;
				int nbStepsSATot = (int) Math.floor(0.5 * nbStepMax ) ;
				int nbStepsNewton2 = (int) Math.floor(0.3 * nbStepMax ) ;
				//
				// 'Newton' optimization until
				// 	a roughly good position
				float sqNormMinGradFAST = (idealDistance * idealDistance) / 16 ;
				int stepsUsedNewton1 = newtonOptimisationHM(loopHM, loop, nbStepsNewton1, sqNormMinGradFAST, nodeVisitedValue) ;
				//
				// simulated annealing try
				//
				nbStepsSATot += nbStepsNewton1 - stepsUsedNewton1 ;

				if (nbStepsSATot >= 2*n)
				{
					int nbAttempts ;
					int nbStepsSA ;

					if (nbStepsSATot / (10 * n) < 20)
					{
						nbAttempts = (int) Math.floor(  Math.sqrt( nbStepsSATot/(2*n) )  ) ;
						nbStepsSA = nbStepsSATot / ( n * nbAttempts ) ;
					}
					else
					{
						nbAttempts = 10 ;
						nbStepsSA = nbStepsSATot / nbAttempts ;
					}
					//
					float initProba = 0.95f ;
					float lambdaMovement = (float) Math.exp( -3*Math.log(10)/nbStepsSA ) ;
					float lambdaProbability = lambdaMovement ;
					//
					simulatedAnnealingHM(loopHM, loop, nbStepsSA, nbAttempts, initProba, lambdaProbability, lambdaMovement, nodeVisitedValue ) ;
					//
				}
				//					
				// 'precise' Newton optimization
				float sqNormMinGrad = (idealDistance * idealDistance) / 100 ;
				newtonOptimisationHM(loopHM, loop, nbStepsNewton2, sqNormMinGrad, nodeVisitedValue) ;		
				//
			}//void loopLayout
			
			/**
			 * heuristic placement for the new nodes
			 * 
			 * @param loopHM
			 * @param loop
			 * @param nodeVisitedValue
			 */
			private void placeNewNodesLoop(HashMap<Integer, Node> loopHM, Node[] loop, boolean nodeVisitedValue )
			{				
				HashMap<Integer, Node> newNodesHM = new HashMap<Integer, Node>() ;
				for (Node currentNode : loop)
				{
					if (!oldNodes.containsKey(currentNode.object.hashCode()))
					{
						newNodesHM.put(currentNode.object.hashCode(),currentNode) ;
					}
				}
				
				if (newNodesHM.size() == loopHM.size())
				{
					fastLoopLayout(loopHM, nodeVisitedValue) ;
				}
				else
				{
					boolean allPlaced = false ;
					while ( !allPlaced )
					{
						allPlaced = placeLinkNodesLoop(newNodesHM,loopHM) ;
					}
				}
			}//void placeNewNodesLoop
			
			
			/**
			 * place the nodes in newNodes
			 * that are linked to already
			 * placed nodes in the loop
			 * 
			 * @param newNodesHM
			 * @param loopHM
			 * @return
			 */
			private boolean placeLinkNodesLoop(HashMap<Integer, Node> newNodesHM, HashMap<Integer, Node> loopHM)
			{
				Collection<Node> newNodes = newNodesHM.values() ;
				ArrayList<Node> toRemove = new ArrayList<Node>() ;
				
				for (Node currentNode : newNodes)
				{
					boolean isPlaced = false ;
					for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
					{
						Node otherNode = ( currentEdge.target == currentNode ) ? currentEdge.source : currentEdge.target ;
						if ( loopHM.containsKey(otherNode.object.hashCode()) &&
							 ! newNodesHM.containsKey(otherNode.object.hashCode()) )
						{
							placeNodeLoop(currentNode, newNodesHM, loopHM) ;
							isPlaced = true ;
							break ;
						}
					}
					if (isPlaced)
					{
						toRemove.add(currentNode) ;
					}
				}
				
				for (Node n :toRemove)
				{
					newNodesHM.remove(n.object.hashCode()) ;
				}
				
				return newNodesHM.isEmpty() ;
			}//boolean placeLinkNodesLoop
			
			
			/**
			 * place a node in mean
			 * direction given by 
			 * the already placed nodes
			 * (in the same loop)
			 * linked to it
			 * 
			 * @param currentNode
			 * @param loopHM
			 * @param newNodesHM
			 */
			private void placeNodeLoop(Node currentNode, HashMap<Integer,Node> loopHM, HashMap<Integer,Node> newNodesHM)
			{
				int count = 0 ;
				float x = 0 ;
				float y = 0 ;
				
				for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
				{
					Node otherNode = ( currentEdge.target == currentNode ) ? currentEdge.source : currentEdge.target ;
					if ( loopHM.containsKey(otherNode.object.hashCode()) &&
						 ! newNodesHM.containsKey(otherNode.object.hashCode()) )
					{
						float[] direction = directionLoop(otherNode, loopHM, newNodesHM) ;
						int s = ( currentEdge.target == currentNode ) ? 1 : -1 ;
						
						x += otherNode.x + s*direction[0] ;
						y += otherNode.x + s*direction[1] ;
						count ++ ;
					}
				}
				if (count > 1)
				{
					float norm = (float) Math.sqrt(x*x+y*y) ;
					x = x / norm * idealDistance + (2*(float)Math.random()-1)*0.25f*idealDistance ;
					y = y / norm * idealDistance + (2*(float)Math.random()-1)*0.25f*idealDistance ;
				}
				currentNode.x = x ;
				currentNode.y = y ;
			}//void placeNodeLoop
			
			/**
			 * return the direction in which
			 * a node linked to currentNode
			 * should be placed
			 * 
			 * @param currentNode
			 * @param loopHM
			 * @param newNodesHM
			 * @return
			 */
			private float[] directionLoop(Node currentNode, HashMap<Integer,Node> loopHM, HashMap<Integer,Node> newNodesHM)
			{
				float xDirection = 0 ;
				float yDirection = 0 ;
				
				for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
				{
					Node otherNode = ( currentEdge.target == currentNode ) ? currentEdge.source : currentEdge.target ;
					if ( loopHM.containsKey(otherNode.object.hashCode()) &&
						 ! newNodesHM.containsKey(otherNode.object.hashCode()) )
					{
						int s = ( currentEdge.target == currentNode ) ? 1 : -1 ;
						xDirection += s * (currentNode.x - otherNode.x) ;
						yDirection += s * (currentNode.y - otherNode.y) ;
					}
				}
				float norm = (float) Math.sqrt(xDirection*xDirection + yDirection*yDirection) ;
				if ( norm > 0.1 )
				{
					float newNorm = (float) Math.sqrt(idealDistance/2) ;
					xDirection = xDirection / norm * newNorm ;
					yDirection = yDirection / norm * newNorm ;
					
					float[] rep = {xDirection,yDirection} ;
					return rep ;
				}
				else
				{
					xDirection = 0 ;
					yDirection = -idealDistance ;
					float[] rep = {xDirection,yDirection} ;
					return rep ;
				}
			}
			
			
			/**
			 * computes the rank (distance to root)
			 * of each node
			 * 
			 * @param rootNode root of the graph
			 * @param nodeVisitedValue boolean indicated
			 * if a node has been visited
			 * 
			 */
			private void hierarchicalRank (Node rootNode, boolean nodeVisitedValue)
			{
				// list of nodes to rank
				LinkedList<Node> nodesToRank = new LinkedList<Node>() ;
				nodesToRank.addLast(rootNode) ;
				rootNode.index = 0 ;
				rootNode.isAccessed = nodeVisitedValue ;
				//
				while (nodesToRank.size() > 0)
				{
					Node currentNode = nodesToRank.getFirst() ;
					//
					// currentNode is supposed to
					// already be ranked
					int rank = currentNode.index ;
					//
					// visiting the children of currentNode
					for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode) )
					{
						Node otherNode = (currentEdge.target == currentNode) ? currentEdge.source : currentEdge.target ;
						//
						// if otherNode has not yet
						// been accessed
						if (otherNode.isAccessed == nodeVisitedValue)
						{
							continue ;
						}
						otherNode.isAccessed = nodeVisitedValue ;
						//
						// ranking otherNode
						otherNode.index = rank+1 ;
						//
						// the children of otherNode
						// will be processed next
						nodesToRank.addLast(otherNode) ;
						//
					}
					//
					// removing currentNode from
					// the list of nodes to process
					nodesToRank.remove() ;
					//
				}//while

			}//void hierarchical rank
			
			

			/**
			 * controls the optimization of the
			 * position of the 'newNodes' in the
			 * layout formed by 'allNodes' ;
			 * 
			 * 3 functions are successively called :
			 * 
			 * (1) a Newton optimization for the
			 * newNodes exclusively (see
			 * 'newtonPlacement' ) ;
			 * (2) a simulated annealing optimization
			 * for allNodes (see 'simulatedAnnealing' ) ;
			 * (3) a Newton optimisation for
			 * allNodes (see 'newtonOptimization' );
			 * 
			 * @param newNodes
			 * @param allNodes
			 * @param nodeVisitedValue
			 * 
			 * @see newtonPlacement
			 * @see simulatedAnnealing
			 * @see newtonOptimization
			 */
			private void optimizationNewNodes(ArrayList<Node> newNodes, ArrayList<Node> allNodesToLayout, ArrayList<Node> allNodes, boolean nodeVisitedValue)
			{
				// first : 'fast' Newton on the new nodes only
				int nbNewNodes = newNodes.size() ;
				int nbAllNodes = allNodes.size() ;

				float sqNormMinGrad = (idealDistance - springLength) * (idealDistance - springLength) / 16 ;

				newtonPlacement(newNodes, allNodes, 3*nbNewNodes, sqNormMinGrad, nodeVisitedValue) ;
				//
				// then : 'fast' Newton on all layouted nodes
				newtonPlacement(allNodesToLayout, allNodes, 5*(nbAllNodes-2), sqNormMinGrad, nodeVisitedValue) ;
				//
				// then : 'fast' SA on all layouted nodes
				int nbAttempts = 5 ;
				int nbStepsSA = 10 ;

				float initProba = 0.95f ;
				float lambdaMovement = (float) Math.exp( -3*Math.log(10)/nbStepsSA ) ;
				float lambdaProbability = lambdaMovement ;

				simulatedAnnealing(allNodes, allNodesToLayout, nbStepsSA, nbAttempts, initProba, lambdaProbability, lambdaMovement, nodeVisitedValue) ;
				//					
				// finally : 'fast' Newton on all layouted nodes
				newtonPlacement(allNodesToLayout, allNodes, 5*(nbAllNodes-2), sqNormMinGrad, nodeVisitedValue) ;
				//

			}//void optimizationNewNodes


			/**
			 * recursively places node and their children
			 * (until a maxDepth maximum depth)
			 * on the same principle as 'placeNode', and
			 * keeps track of the nodes on the maximum
			 * depth and the nodes placed ;
			 * 
			 * marks the node placed  to nodeVisitedValue
			 * in their 'isAccessed' field ;
			 * 
			 * @param currentNode node to be placed, and
			 * whose children should be placed
			 * @param nodeVisitedValue indicates if the
			 * node has already been placed
			 * @param currentDepth the currentDepth
			 * @param maxDepth maximum depth
			 * 
			 * @return maxDepthNodes node of depth equals
			 * to maxDepth
			 */
			private void recursivelyDepthLimitedPlacingNode( Node currentNode, ArrayList<Node> placedNodes, ArrayList<Node> maxDepthNodes,
					boolean nodeVisitedValue, int currentDepth, int maxDepth )
			{
				// this node should be placed
				if ( currentDepth <= maxDepth )
				{
					// placing this node if it's not already the case
					if (currentNode.isAccessed == !nodeVisitedValue)
					{
						placeNode(currentNode, nodeVisitedValue) ;
						placedNodes.add(currentNode) ;
					}
					//
					// placing its children
					for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
					{
						Node otherNode = (currentEdge.source == currentNode) ? currentEdge.target : currentEdge.source ;

						if (otherNode.isAccessed == nodeVisitedValue || oldNodes.containsKey(otherNode.object))
						{
							continue ;
						}

						recursivelyDepthLimitedPlacingNode(otherNode, placedNodes, maxDepthNodes, nodeVisitedValue, currentDepth+1, maxDepth) ;
					}
				}
				// this node will be a 'linkNode'
				else
				{
					if (currentNode.isAccessed == ! nodeVisitedValue )
					{
						maxDepthNodes.add(currentNode) ;
						currentNode.isAccessed = nodeVisitedValue ;
					}
				}

			}//void recursivelyDepthLimitedPlacingNode


			/**
			 * returns the direction in which a node linked to
			 * currentNode should roughly be placed, with respect
			 * to how currentNode is linked to the layout ;
			 * 
			 * the direction is given assuming that the node to
			 * be placed is a 'child' of currentNode, the
			 * opposite direction should be used otherwise
			 * 
			 * @param currentNode
			 * @param nodeVisitedValue
			 * @return float[dirX,dirY] X/Y directions
			 */
			private float[] directionPlacement(Node currentNode, boolean nodeVisitedValue)
			{
				float directionX = 0 ;
				float directionY = 0 ;

				for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
				{
					Node otherNode = (currentEdge.source == currentNode) ? currentEdge.target : currentEdge.source ;

					// if otherNode has already been placed
					if( otherNode.isAccessed == nodeVisitedValue || oldNodes.containsKey(otherNode.object) )
					{
						int s = (currentEdge.source == currentNode) ? -1 : 1 ;

						directionX += s * ( currentNode.x - otherNode.x ) ;
						directionY += s * ( currentNode.y - otherNode.y ) ; 
					}
				}

				// direction in which to place other nodes linked to currentNode
				float norm = (float) Math.sqrt((directionX*directionX + directionY * directionY)) ;
				directionX = directionX / norm * idealDistance * 0.5f ;
				directionY = directionY / norm * idealDistance * 0.5f ;

				float[] rep = {directionX, directionY} ;
				return rep ;
			}//float[] directionPlacement


			/**
			 * places a node by taking into account the position of
			 * nodes to which it's linked, and how these nodes are
			 * linked in the layout ; also add a random component ;
			 * 
			 * switch the 'isAccessed' field of the node
			 * to 'nodeVisitedValue'
			 * 
			 * @param currentNode the node to be placed
			 * @param nodeVisitedValue indicating if a node has already
			 * been placed
			 */
			private void placeNode(Node currentNode, boolean nodeVisitedValue)
			{
				int nb = 0 ;

				for ( Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode) )
				{
					Node otherNode = (currentEdge.source == currentNode) ? currentEdge.target : currentEdge.source ;

					int s = (currentEdge.source == currentNode) ? -1 : 1 ;

					// if otherNode has already been placed
					if( otherNode.isAccessed == nodeVisitedValue || oldNodes.containsKey(otherNode.object) )
					{
						float[] direction = directionPlacement(otherNode, nodeVisitedValue) ;
						//
						//position as induced by otherNode
						currentNode.x = otherNode.x + s*direction[0] ;
						currentNode.y = otherNode.y + s*direction[1] ;
						//
						nb ++ ;					
					}
				}
				//
				// mean
				if ( nb  > 1 )
				{
					currentNode.x = currentNode.x / nb ;
					currentNode.y = currentNode.y / nb ;
				}
				//
				//
				//random component
				currentNode.x += (2 * (float) Math.random()-1 ) * idealDistance * 0.25f ;
				currentNode.y += (2 * (float) Math.random()-1 ) * idealDistance * 0.25f ;
				//

				// currentNode is now placed
				currentNode.isAccessed = nodeVisitedValue ;
				//

			}//void placeNode


			/**
			 * searches for the 'new' nodes that are linked to 'old' nodes ;
			 *   
			 *  @param newNodes a list of the new nodes
			 *  @param edgeVisitedValue boolean value indicating if a given edge
			 *  has already been visited
			 *  
			 *  @return linkedNodes a list of the 'new' nodes linked to 'old nodes'
			 */
			private ArrayList<Node> linksNewOld(ArrayList<Node> newNodes)
			{
				ArrayList<Node >linkedNodes = new ArrayList<Node>() ; 

				for ( Node currentNode : newNodes )
				{
					for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
					{
						Node otherNode = (currentEdge.source == currentNode) ? currentEdge.target : currentEdge.source ;
						//
						// otherNode is an 'old' node 
						if ( oldNodes.containsKey(otherNode.object) )
						{
							linkedNodes.add(currentNode) ;
						}
						//
					}//for currentEdge
				}//for currentNode
				//
				return linkedNodes ;
				//
			}//ArrayList<Node> linksNewOld
			
			// * * *



			// * * * Initialization & 'small helping' functions * * *

			/**
			 * performs a translation of the nodes
			 * in listOfNode of toMoveX on the
			 * x-axis and toMoveY on the y-axis
			 * 
			 * @param listOfNode list of the nodes
			 * to translate
			 * @param toMoveX x-translation
			 * @param toMovzY y-translation
			 */
			private void translationXY(ArrayList<Node> listOfNodes, float toMoveX, float toMoveY)
			{
				for ( Node currentNode : listOfNodes )
				{
					currentNode.x += toMoveX ;
					currentNode.y += toMoveY ;					
				}
			}//void translationX


			/**
			 * performs a translation of the nodes
			 * in listOfNode of toMoveX on the
			 * x-axis and toMoveY on the y-axis
			 * 
			 * @param listOfNode list of the nodes
			 * to translate
			 * @param toMoveX x-translation
			 * @param toMovzY y-translation
			 */
			private void translationXY(Collection<Node> listOfNodes, float toMoveX, float toMoveY)
			{
				for ( Node currentNode : listOfNodes )
				{
					currentNode.x += toMoveX ;
					currentNode.y += toMoveY ;					
				}
			}//void translationX


			/**
			 * performs a rotation of the nodes
			 * in listOfNodes, with the point
			 * (0,0) as centre, and of angle A
			 * such that cos(A) = cosAngle
			 * and sin(A) = sinAngle ;
			 * 
			 * @param listOfNodes list of nodes to
			 * rotate
			 * @param cosAngle sinus of the rotation angle
			 * @param sinAngle cosinus of the roation angle
			 */
			private void rotation(ArrayList<Node> listOfNodes, float cosAngle, float sinAngle)
			{
				for (Node currentNode : listOfNodes )
				{
					float distance = (float) Math.sqrt(currentNode.x * currentNode.x + currentNode.y * currentNode.y ) ;

					if (distance >= 0.01f )
					{
						float oldCos = currentNode.x / distance ;
						float oldSin = currentNode.y / distance ;

						currentNode.x = distance * (oldCos * cosAngle - oldSin * sinAngle) ;
						currentNode.y = distance * (oldSin * cosAngle + sinAngle * oldCos) ;
					}
				}
			}//void rotation
			
			
			/**
			 * performs a rotation of the nodes
			 * in listOfNodes, with the point
			 * (xCenter,yCenter) as centre, and of angle A
			 * such that cos(A) = cosAngle
			 * and sin(A) = sinAngle ;
			 * 
			 * @param listOfNodes list of nodes to
			 * rotate
			 * @param cosAngle sinus of the rotation angle
			 * @param sinAngle cosinus of the roation angle
			 */
			private void rotation(Collection<Node> listOfNodes, float xCenter, float yCenter, float cosAngle, float sinAngle)
			{
				for (Node currentNode : listOfNodes )
				{
					float distance = (float) Math.sqrt( (currentNode.x-xCenter) * (currentNode.x-xCenter) + (currentNode.y-yCenter) * (currentNode.y-yCenter) ) ;

					if (distance >= 0.01f )
					{
						float oldCos = (currentNode.x-xCenter) / distance ;
						float oldSin = (currentNode.y-yCenter) / distance ;

						currentNode.x = distance * (oldCos * cosAngle - oldSin * sinAngle) + xCenter ;
						currentNode.y = distance * (oldSin * cosAngle + sinAngle * oldCos) + yCenter ;
					}
				}
			}//void rotation


			/**
			 * performs a translation of all the nodes in
			 * listOfNodes to bring the node root at the
			 * (0,0) position and then performs a rotation
			 * of center (0,0) and of angle the opposite
			 * of the angle between the node 'root' and 
			 * the node 'firstNode' (thus aligning these
			 * 2 nodes along the vertical axis and in the
			 * downward direction) ;
			 * 
			 * @param root node to bring to the (0,0) position
			 * @param firstNode node to align with the node 'root'
			 * @param listOfNodes list of nodes to modify
			 */
			private void centringAndRotating(Node root, Node firstNode, ArrayList<Node> listOfNodes)
			{
				//
				// performing a translation
				// to bring 'root' at the (0,0)
				// position
				if (root.x != 0 || root.y != 0)
				{
					translationXY(listOfNodes, -root.x, -root.y) ;
				}
				//
				// performing the rotation to align
				// 'firstNode' and 'root'
				if( Math.abs(firstNode.x) > 0.001 &&  Math.abs(firstNode.y) > 0.001 )
				{
					float distance = (float)Math.sqrt(firstNode.x * firstNode.x + firstNode.y * firstNode.y) ;

					// cosinus of the rotation angle
					// angle = -Pi/2 - angle(root,firstNode)
					float cosAngle = - firstNode.y / distance ;
					float sinAngle = - firstNode.x / distance ;

					rotation(listOfNodes, cosAngle, sinAngle) ;
				}
				//
			}//void centringAndRotating


			/**
			 * reset all the fields of all the nodes (except the coordinates)
			 * and set the isAccessed field of all nodes/edges to the value
			 * of the parameters nodeVisitedValue / edgeVisitedValue ;
			 * 
			 * also returns the root of the graph
			 * 
			 * @param firstNode the first node in the linked list of all the nodes
			 * @param nodeVisitedValue the 'isAccessed' field of all the nodes
			 * will be set to this value
			 * @param edgeVisitedValue the 'isAccessed' field of all the edges
			 * will be set to this value
			 * 
			 * @return root the root of the graph
			 */
			private Node initialisation( Node firstNode, boolean nodeVisitedValue, boolean booleanEdgeVisitedValue)
			{
				// for the identification of the root
				Node root = firstNode ;
				boolean found = false ;

				IMPWorkbench w = (IMPWorkbench) Workbench.current ();
				GraphManager graph = (w != null) ? w.getRegistry ().getProjectGraph () : null;
				//
				//
				for ( Node currentNode = firstNode ; currentNode != null ; currentNode = currentNode.next )
				{
					// resetting these fields
					currentNode.index = 0 ;
					currentNode.finalX = 0 ;
					currentNode.finalY = 0 ;
					currentNode.initialX = 0 ;
					currentNode.initialY = 0 ;
					currentNode.layoutVarX = 0 ;
					currentNode.layoutVarY = 0 ;
					//
					//
					// 'isAccessed' fields
					currentNode.isAccessed = nodeVisitedValue ;

					for(Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
					{
						currentEdge.isAccessed = booleanEdgeVisitedValue ;
					}
					//
					//
					// identification of the root
					if ( found == false )
					{
						if ( currentNode.object == graph.getRoot() )
						{
							found = true ;
							root = currentNode ;
						}
					}
					//

				}//for currentNode

				return root ;

			}//Node initialisation

			// * * *

			

			// * * * Tree-related function * * *

			
			/**
			 * places all the nodes linked to
			 * currentNode in a tree layout ;
			 * 
			 * this function must be called
			 * with currentNode = root of the
			 * tree as argument ;
			 * 
			 * leftX / Y do not matter for the
			 * first call, they only define the
			 * absolute position of the tree,
			 * different values will give the
			 * same layout translated ;
			 * 
			 * 
			 * @param currentNode node to place
			 * 
			 * @param Y y-position where
			 * currentNode should be placed ;
			 * 
			 * @param leftX minimum value
			 * that can be assigned as
			 * x-position for currentNode
			 * or the nodes 'under' him
			 */
			private float[] tree(Node currentNode, float leftX, float Y, boolean nodeVisitedValue)
			{
				currentNode.isAccessed = nodeVisitedValue ;
				// assigning the Y position
				currentNode.y = Y ;
				float x = 0 ;
				float mX = leftX ;
				//
				// number of child of currentNode
				int child = 0 ;

				float[] v ;

				for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
				{
					if (currentEdge.isAccessed == edgeVisitedValue)
					{
						continue ;
					}
					currentEdge.isAccessed = edgeVisitedValue ;

					Node otherNode = (currentEdge.source == currentNode)? currentEdge.target : currentEdge.source ;
					if ( otherNode.isAccessed == nodeVisitedValue )
					{
						continue ;
					}
					child ++ ;
					v = tree( otherNode, mX, Y-yIdealDistance, nodeVisitedValue) ;

					x += v[1] ;
					mX = v[0] ;
				}
				//
				// no child
				if ( child == 0 )
				{
					// placing currentNode on leftX
					currentNode.x = leftX ;
					float[] rep = {leftX + idealDistance, leftX} ;
					return  rep ;
				}
				// children : centring
				// currentNode with respect to them
				else
				{
					currentNode.x = x / child ;
					float[] rep = {mX,currentNode.x} ;
					return rep ;
				}

			}//float[] tree


			/**
			 * places the node in 'tree'
			 * in a tree layout ;
			 * 
			 * @param currentNode
			 * @param leftX
			 * @param Y
			 * @param tree
			 * @return
			 */
			private float[] subTree(Node currentNode, float leftX, float Y, HashMap<Integer, Node> tree, boolean nodeVisitedValue  )
			{
				currentNode.isAccessed = nodeVisitedValue ;
				// assigning the Y position
				currentNode.y = Y ;
				float x = 0 ;
				float mX = leftX ;
				//
				// number of child of currentNode
				int child = 0 ;

				float[] v ;

				for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
				{
					Node otherNode = (currentEdge.source == currentNode)? currentEdge.target : currentEdge.source ;
					
					if ( !tree.containsKey(otherNode.object.hashCode()) )
					{
						continue ;
					}
					
					if (currentEdge.isAccessed == edgeVisitedValue)
					{
						currentEdge.isAccessed = !edgeVisitedValue ;
						continue ;
					}
					currentEdge.isAccessed = edgeVisitedValue ;
					
					if (otherNode.isAccessed == nodeVisitedValue)
					{
						continue ;
					}
					child ++ ;
				
					v = subTree( otherNode, mX, Y-yIdealDistance, tree, nodeVisitedValue) ;

					x += v[1] ;
					mX = v[0] ;
				}
				//
				// no child
				if ( child == 0 )
				{
					// placing currentNode on leftX
					currentNode.x = leftX ;
					float[] rep = {leftX + idealDistance, leftX} ;
					
					// resetting isAccessed field
					currentNode.isAccessed = !nodeVisitedValue ;
					return  rep ;
				}
				// children : centring
				// currentNode with respect to them
				else
				{
					currentNode.x = x / child ;
					float[] rep = {mX,currentNode.x} ;

					// resetting isAccessed field
					currentNode.isAccessed = !nodeVisitedValue ;
					return rep ;
				}

			}//float[] tree
			
			
			/**
			 * tree-algorithm for placing
			 * 'strutures' (set of linked
			 * nodes ; e.g. sub-trees or
			 * loops) ;
			 * 
			 * 
			 * @param startingNode
			 * @param trees
			 * @param loops
			 * @param leftX
			 * @param Y
			 * @param nodeVisitedValue
			 * @return
			 */
			private float[] treeForStructure(Node startingNode, ArrayList< HashMap<Integer, Node> > trees,
							ArrayList< HashMap<Integer, Node> > loops, float leftX, float Y, HashMap<Integer,Node> treeNodes,
							boolean nodeVisitedValue  )
			{
				// finding which structure (ie which
				// tree / loop) startingNode belongs to
				HashMap<Integer, Node > currentStructureHM ;
				boolean loop = false ;
				int index = startingNode.index ;
				
				if ( treeNodes.get(startingNode.object.hashCode()) != null)
				{
					currentStructureHM = trees.get(index) ;
				}
				else
				{
					currentStructureHM = loops.get(index) ;
					loop = true ;
				}
				//

				//
				float x = 0 ;
				float mX = leftX ;

				float minX = startingNode.x ;
				float maxX = startingNode.x ;
				float minY = startingNode.y ;
				float maxY = startingNode.y ;
				float meanX = 0 ;
				float meanY = 0 ;
				//
				// startingNode will be used 
				// as reference to define the
				// position of currentStructure
				//
				// number of child of currentNode
				//int child = 0 ;

				float[] v ;
				//
				//
				Collection<Node> currentStructure = currentStructureHM.values() ;
				//			
				// rotation to place
				// startingNode
				if (loop)
				{
					for (Node currentNode : currentStructure)
					{
						meanX += currentNode.x ;
						meanY += currentNode.y ;
					}					
					
					meanX = meanX / currentStructure.size() ;
					meanY = meanY / currentStructure.size() ;
					
					float deltaX = startingNode.x - meanX ;
					float deltaY = startingNode.y - meanY ;
					
					if ( Math.abs(deltaX)> 0.1 || Math.abs(deltaY) > 0.1 )
					{
						float distance = (float) Math.sqrt( deltaX*deltaX + deltaY*deltaY ) ;
						
						// angle = Pi/2 - angle(mean,startingNode)
						float cosAngle = deltaY / distance ;
						float sinAngle = deltaX / distance ;
						
						rotation(currentStructure, startingNode.x, startingNode.y, cosAngle, sinAngle) ;
					}
				}
				//
				// probing the size of currentStructure
				for (Node currentNode : currentStructure)
				{
					if (currentNode.x > maxX)
					{
						maxX = currentNode.x ;
					}
					else if( currentNode.x < minX)
					{
						minX = currentNode.x ;
					}
					if (currentNode.y > maxY)
					{
						maxY = currentNode.y ;
					}
					else if ( currentNode.y < minY )
					{
						minY = currentNode.y ;
					}
					currentNode.isAccessed = nodeVisitedValue ;
				}
				//
				// searching jonction points with other structures
				HashMap<Integer, ArrayList<Node>> otherNodeStructures = new HashMap<Integer, ArrayList<Node>>() ;
				int child = 0 ;
				for( Node currentNode : currentStructure )
				{
					ArrayList<Node> linkedNodes = new ArrayList<Node>() ;
					for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
					{
						if (currentEdge.isAccessed == edgeVisitedValue)
						{
							continue ;
						}
						currentEdge.isAccessed = edgeVisitedValue ;
						
						Node otherNode = (currentEdge.target == currentNode) ? currentEdge.source : currentEdge.target ;

						if ( !currentStructureHM.containsKey(otherNode.object.hashCode()) &&
							  otherNode.isAccessed != nodeVisitedValue )
						{
							otherNode.isAccessed = nodeVisitedValue ;
							linkedNodes.add(otherNode) ;
							child ++ ;
						}
					}//for currentEdge
					if (!linkedNodes.isEmpty())
					{
						otherNodeStructures.put( (int)Math.floor( currentNode.x * 10000) , linkedNodes) ;
					}
				}//for currentNode
				//
				// sorting the child structures to avoid edge crossing
				if ( child > 0 )
				{
					int[] arrayXPosition = new int[otherNodeStructures.size()] ;
					int iter = 0 ;
					
					for (int value : otherNodeStructures.keySet())
					{
						arrayXPosition[iter] = value ;
						iter ++ ;
					}
					Arrays.sort(arrayXPosition) ;
					//
					// placing child structures
					for ( int xPosition : arrayXPosition )
					{
						ArrayList<Node> childNodeStructure = otherNodeStructures.get(xPosition) ;
						for (Node n : childNodeStructure)
						{
							v = treeForStructure(n, trees, loops, mX, Y + minY - maxY - yIdealDistance, treeNodes, nodeVisitedValue) ;
		
							x += v[1] ;
							mX = v[0] ;
						}
					}
					//
				}
				//
				// placing currentStructure
				// no child structure
				if ( child == 0 )
				{
					float newLeftX = maxX - startingNode.x ;

					// placing currentStructure
					translationXY(currentStructure, leftX - minX, Y - maxY) ;

					newLeftX = startingNode.x + newLeftX + idealDistance ;

					float[] rep = {newLeftX, startingNode.x} ;
					
					return  rep ;
				}
				// child-structures : centring
				// currentStructure with respect to them
				else
				{
					float newLeftX = maxX - startingNode.x ;
					
					float place = Math.max(x/child, leftX+startingNode.x-minX) ;
					
					// placing currentStructure
					translationXY(currentStructure, place - startingNode.x, Y - maxY) ;

					newLeftX = startingNode.x + newLeftX + idealDistance ;
					mX = Math.max(newLeftX, mX) ;
					
					float[] rep = {mX,startingNode.x} ;

					return rep ;
				}

			}//float[] treeForStructure

			
			/**
			 * determines the structure of the graph :
			 * the loops are returned in 'loops', the
			 * other nodes belongs to 'tree-like' parts
			 * 
			 * @param currentNode
			 * @param loops
			 * @param loopNodes
			 * @param jonctionNodes
			 * @param nodeVisitedValue
			 * @return
			 */
			private ArrayList< HashMap<Integer, Node> > goThroughLinkedNodes( Node currentNode, Node comeFromNode, ArrayList<HashMap<Integer, Node>> loops,
					HashMap<Integer, Node> treeNodes, boolean nodeVisitedValue )
					{
						if (currentNode.isAccessed == nodeVisitedValue)
						{
							HashMap<Integer, Node> rep0 = new HashMap<Integer, Node>() ;
							rep0.put(currentNode.object.hashCode(), currentNode) ;

							// rep0 : loopNodes
							ArrayList<HashMap<Integer, Node> > rep = new ArrayList<HashMap<Integer,Node>>() ;
							rep.add(rep0) ;

							return rep ;
						}
						else
						{
							currentNode.isAccessed = nodeVisitedValue ;
							
							boolean isLoop = false ;
							
							// nodes linked to currentNode and that are 'loopNodes'
							HashMap<Integer, Node> loop = new HashMap<Integer, Node>() ;
							HashMap<Integer, Node> loopNodes = new HashMap<Integer, Node>() ;
							HashMap<Integer, Node> linkedNodes = new HashMap<Integer, Node>() ;
							//
							for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
							{
								if (currentEdge.isAccessed == edgeVisitedValue)
								{
									continue ;
								}
								currentEdge.isAccessed = edgeVisitedValue ;

								Node otherNode = (currentEdge.source == currentNode) ? currentEdge.target : currentEdge.source ;
								
								// not taking into account two nodes linked by two edges
								if (otherNode == comeFromNode)
								{
									continue ;
								}
								
								ArrayList< HashMap<Integer, Node> > temp = goThroughLinkedNodes(otherNode, currentNode, loops, treeNodes, nodeVisitedValue) ;

								if (!temp.isEmpty())
								{
									if (temp.size() >= 1)
									{
										loopNodes.putAll(temp.get(0)) ;
										isLoop = true ;
									}
									if (temp.size() >= 2)
									{
										loop.putAll(temp.get(1)) ;
									}
								}
							}
							//
							// currentNode is part of a tree
							if (!isLoop)
							{
								treeNodes.put(currentNode.object.hashCode(), currentNode ) ;

								// belongs to a loop / newLoop
								ArrayList<HashMap<Integer, Node>> rep = new ArrayList<HashMap<Integer,Node>>() ;
								return rep ;
							}
							// currentNode belongs to a loop
							else
							{
								loop.put(currentNode.object.hashCode(), currentNode) ;

								// currentNode is a loopNode
								if ( loopNodes.containsKey(currentNode.object.hashCode()))
								{
									loopNodes.remove(currentNode.object.hashCode()) ;
								}
									
								// end of a loop
								if (loopNodes.isEmpty())
								{
									loops.add(loop) ;
									for (Node n : loop.values())
									{
										n.index = loops.size()-1 ;
									}
									
									ArrayList<HashMap<Integer, Node>> rep = new ArrayList<HashMap<Integer,Node>>() ;
									
									return rep ;
								}
								else
								{
									ArrayList<HashMap<Integer, Node>> rep = new ArrayList<HashMap<Integer,Node>>() ;
									rep.add(loopNodes) ;
									rep.add(loop) ;
									
									return rep ;
								}
							}//if loop 
						}
					}//boolean[] goThroughLinkedNodes

			
			/**
			 * identifies the different subtrees
			 * in treeNodes, and their roots ;
			 * 
			 * subtrees are returned in 'trees',
			 * roots in 'roots', the root of one
			 * subtree and the subtree itself
			 * having the same index in their
			 * respective lists ;
			 * 
			 * a new subtree is formed each time
			 * a node linked to loop is visited ;
			 * 
			 * @param trees
			 * @param roots
			 * @param treeNodes
			 * @param currentNode
			 * @param index
			 * @param newTree
			 * @param nodeVisitedValue
			 */
			private void identifyRootsAndTrees(ArrayList<HashMap<Integer, Node>> trees, HashMap<Integer,Integer> roots, HashMap<Integer, Node> treeNodes,
					 	Node currentNode, Node fromNode, int index, boolean newTree, boolean nodeVisitedValue )
			{
				if (currentNode.isAccessed == !nodeVisitedValue )
				{
					currentNode.isAccessed = nodeVisitedValue ;

					// currentNode belongs to a tree
					if (treeNodes.containsKey(currentNode.object.hashCode()))
					{
						if (newTree)
						{
							HashMap<Integer, Node> tree = new HashMap<Integer, Node>() ;
							tree.put(currentNode.object.hashCode(), currentNode) ;
							trees.add(tree) ;
							currentNode.index = trees.size()-1 ;
							
							roots.put(currentNode.object.hashCode(), trees.size()-1) ;
							
							index = trees.size()-1 ;
						}
						else
						{
							currentNode.index = index ;
							trees.get(index).put(currentNode.object.hashCode(),currentNode) ;
						}
					
						newTree = false ;
						
						for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
						{
							if (currentEdge.isAccessed == edgeVisitedValue )
							{
								continue ;
							}

							Node otherNode = (currentEdge.target == currentNode) ? currentEdge.source : currentEdge.target ;
							
							if (otherNode == fromNode)
							{
								continue ;
							}
							
							if (!treeNodes.containsKey(otherNode.object.hashCode()))
							{
								newTree = true ;
								break ;
							}
						}
						for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
						{
							if (currentEdge.isAccessed == edgeVisitedValue )
							{
								continue ;
							}
							currentEdge.isAccessed = edgeVisitedValue ;
							Node otherNode = (currentEdge.target == currentNode) ? currentEdge.source : currentEdge.target ;

							if (otherNode == fromNode)
							{
								continue ;
							}
							
							identifyRootsAndTrees(trees, roots, treeNodes, otherNode, currentNode, index, newTree, nodeVisitedValue) ;
						}
					}
					// currentNode belongs to a loop
					else
					{
						newTree = true ;
						
						for (Edge currentEdge = currentNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(currentNode))
						{
							if (currentEdge.isAccessed == edgeVisitedValue )
							{
								continue ;
							}
							currentEdge.isAccessed = edgeVisitedValue ;

							Node otherNode = (currentEdge.target == currentNode) ? currentEdge.source : currentEdge.target ;
							if (otherNode == fromNode)
							{
								continue ;
							}
							identifyRootsAndTrees(trees, roots, treeNodes, otherNode, currentNode, 0, newTree, nodeVisitedValue) ;
						}
					}
					//

					
				}
			}//identifyRootsAndTrees

			
			/**
			 * performs a tree like layout :
			 * the graph is divided in loops
			 * and subtree. The subtrees are
			 * drawn according to a tree
			 * algorithm, and the loops according
			 * to an energy algorithm (except if
			 * fastTreeLike equals true). Then an
			 * algorithm combines theses
			 * structures in a tree-like fashion ;
			 * 
			 * 
			 * @param treeNodes
			 * @param loops
			 * @param root
			 * @param nbStepsMax
			 * @param nodeVisitedValue
			 */
			private void treeLikeLayout(HashMap<Integer, Node> treeNodes, ArrayList<HashMap<Integer, Node>> loops, Node root,
										 int nbStepsMax, boolean nodeVisitedValue )
			{
				HashMap<Integer,Integer> roots = new HashMap<Integer, Integer>() ;
				
				ArrayList< HashMap<Integer, Node> > trees  = new ArrayList<HashMap<Integer,Node>>() ;
				
				// # # # # # # # # # # # # # # # # # # # # # # # # #
				// identifying the new and old loops
				ArrayList<HashMap<Integer, Node>> newLoops = new ArrayList<HashMap<Integer,Node>>();
				for (int i = 0 ; i < loops.size() ; i ++ )
				{
					Collection<Node> c = loops.get(i).values() ;
					for (Node currentNode : c )
					{
						if ( !oldNodes.containsKey(currentNode.object.hashCode()) )
						{
							newLoops.add(loops.get(i)) ;
							break ;
						}
					}
				}
				
				// identifying the subtrees in treeNodes
				identifyRootsAndTrees(trees, roots, treeNodes, root, null, 0, true, nodeVisitedValue) ;
				nodeVisitedValue = !nodeVisitedValue ;
				edgeVisitedValue = !edgeVisitedValue ;
				//
				
				// # # # # # # # # # # # # # # # # # # # # # # # # #
				
				
				// # # # # # # # # # # # # # # # # # # # # # # # # #
				// performing the actual layout algorithm
				
				//  *** layouting the subtrees ***
				Set<Integer> setRoots = roots.keySet() ;
				for ( int key : setRoots )
				{
					Node currentRoot = treeNodes.get(key) ;
					int index = roots.get(key) ;
					
					subTree(currentRoot, 0, 0, trees.get(index), nodeVisitedValue) ;
				}
				// ***
				
				// *** layouting the loops ***
				boolean onlyOldLoops = false ;
				
				// layouting the new loops only
				// except if there are no new loops
				if ( newLoops.isEmpty() )
				{
					newLoops = loops ;
					onlyOldLoops = true ;
				}
				if ( fast )
				{
					// calling the fast version on already-layouted loops
					// will not improve them : useless to call it again
					if ( !onlyOldLoops ) 
					{
						// fast version
						for (int i = 0 ; i < newLoops.size() ; i ++)
						{
							fastLoopLayout(loops.get(i), nodeVisitedValue) ;
						}
					}
				}
				else
				{
					int nbNodesLoop = 0 ;
					int nbLoopsOpt = 0 ;
					
					for (int i = 0 ; i < newLoops.size() ; i ++)
					{
						int size = newLoops.get(i).size() ;
						if ( size > 3 )
						{
							nbNodesLoop += size ;
							nbLoopsOpt ++ ;
						}
					}

					if (nbStepsMax <= nbNodesLoop || nbLoopsOpt == 0 )
					{
						// fast version used instead
						for (int i = 0 ; i < loops.size() ; i ++)
						{
							fastLoopLayout(newLoops.get(i), nodeVisitedValue) ;
						}
					}
					else
					{
						float nbStepsPerNode = nbStepsMax / nbNodesLoop ;
						int nbStepUsed = 0 ;
						int nbLoopsOpted = 0 ;
						
						for (int i = 0 ; i < newLoops.size() ; i ++)
						{
							if ( newLoops.get(i).size() <= 3 )
							{
								fastLoopLayout(newLoops.get(i), nodeVisitedValue) ;
							}
							else
							{
								int nbSteps ;
								if (nbLoopsOpt - nbLoopsOpted > 1)
								{
									nbSteps = (int)Math.floor(nbStepsPerNode*newLoops.get(i).size()) ;
								}
								else
								{
									nbSteps = nbStepsMax - nbStepUsed ;
								}							
								loopLayout(newLoops.get(i), nbSteps, nodeVisitedValue,!onlyOldLoops) ;
								nbStepUsed += nbSteps ;
								nbLoopsOpted ++ ;
							}
						}
					}
				}//if fast
				// ***
				
				// *** combining the subtrees and the ***
				// *** loops to form the whole graph ***
				treeForStructure(root, trees, loops, 0, 0, treeNodes, nodeVisitedValue) ;
				
				// ***
				
				// updating oldNodes
				if ( !onlyOldLoops )
				{
					for( int i = 0 ; i < newLoops.size() ; i ++)
					{
						Collection<Node> c = newLoops.get(i).values() ;
						for (Node n : c)
						{
							oldNodes.put(n.object.hashCode(), 0) ;
						}
					}
				}
				Collection<Node> c = treeNodes.values() ;
				for (Node n : c)
				{
					oldNodes.put(n.object.hashCode(), 0) ;
				}
				//
				
			}//void treeLikeLayout
			
			
			// * * *
			


			// * * * Simulated Annealing Optimisation * * *

			
			/**
			 * performs a simulated annealing optimisation on the energy function
			 * of the layout to find a 'good' configuration ;
			 * 
			 * elistism is implemented : the new layout will be better than the old
			 * one (or unchanged in the worst case) ;
			 * 
			 * 'nbStepSA' are performed, making for each step 'nbAttempts' attempts to
			 * modify the position of each node ;
			 * 
			 * the maximum authorised movements in each direction follow a geometric
			 * law from maxMove to minMove ;
			 * 
			 * this function "cleans" the nodes before ending (fields x/y set to the
			 * position of each node, the other fields set to 0) ;
			 * 
			 * @param listOfNodes list of the nodes
			 * @param nbStepSA number of steps of simulated annealing to perform
			 * @param nbAttempts number of attempts to modify the position of a
			 * node in stepSA
			 * @param maxMove maximum movement authorised for the SA steps
			 * @param minMove minimum movement authorised for the SA steps
			 */
			private int simulatedAnnealing( ArrayList<Node> listOfNodes, ArrayList<Node> nodesToMove, int nbStepsSA, int nbAttempts, float initProba,
					float lambdaProbability, float lambdaMovement, boolean nodeVisitedValue )
			{
				// elitism : remembering the best layout ever found
				float initEnergy = computeTotalEnergy(listOfNodes, nodeVisitedValue, true) ;
				float bestEnergy = initEnergy ;
				//
				// keeping the value of the layout's energy enables
				// to update the new value (when the position of 
				// one node is modified) in linear time rather than
				// having to compute it again from scratch (quadratic time)
				float oldEnergy =  bestEnergy ;
				//
				// 'temperatures' :
				// probabilitySA rules the probability to accept a worse
				// layout than the current one (to escape from local
				// minimums), movementSA limits the movement of the nodes
				float probabilitySA = initProba ;
				float movementSA = 1 ;
				//
				//typical temperature 'kB'
				float kB = 0.5f*(idealDistance-springLength)*(idealDistance-springLength)
				+ 2 * forceConst / idealDistance ;
				//
				int nbSteps = 0 ;
				//
				while ( nbSteps < nbStepsSA  )
				{
					float[] var = stepSA(listOfNodes, nodesToMove, oldEnergy, bestEnergy, movementSA, kB, probabilitySA, nbAttempts, nodeVisitedValue) ;
					oldEnergy = var[0] ;
					bestEnergy = var[1] ;

					// updating the temperatures
					probabilitySA *= lambdaProbability ;
					movementSA *= lambdaMovement ;
					//
					nbSteps ++ ;					
				}
				// if oldEnergy (current layout) is not the best configuration 
				// ever found, the positions are set to the best (elitism) and
				// the fields used are cleaned
				if (bestEnergy < oldEnergy)
				{
					bestLayoutSA(listOfNodes) ;
				}
				else
				{
					cleanSA(listOfNodes) ;
				}
				//

				return nbSteps ;
			}//void simulatedAnnealing


			/**
			 * performs some simulating-annealing steps on
			 * nodesToMove ; the best layout ever found is remembered
			 * (elitism) through the fields finalX/Y of each node
			 * 
			 * @param listOfNodes list of the nodes
			 * @param initEnergy energy before any modifications
			 * @param tempSA simulated-annealing temperature
			 * 
			 * @return a length-2 array {layoutEnergy,bestEnergy} with
			 * layoutEnergy the energy of the current configuration,
			 * and bestLayoutEnergy the energy for the best
			 * configuration ever found -- !! the finalX/Y
			 * fields of each node are also modified !!
			 */
			private float[] stepSA( ArrayList<Node> allNodes, ArrayList<Node> nodesToMove, float initLayoutEnergy, float bestLayoutEnergy,
					float movementSA, float kB, float probabilitySA, int nbAttempts, boolean nodeVisitedValue )
			{
				// total energy of the layout
				float layoutEnergy = initLayoutEnergy ;
				float bestEnergy = bestLayoutEnergy ;

				// nbAttempts attempts to modify  the position of each node
				for(int i = 0 ; i < nbAttempts ; i ++ )
				{
					for(int j = 0 ; j < nodesToMove.size() ; j ++ )
					{
						Node nodeToModify = nodesToMove.get(j) ;
						
						float maxMovement = 3*idealDistance ;
						float movement = movementSA * maxMovement + idealDistance/5 ;

						// random translation (x,y) in [-temperature ; temperature]^2
						float newX = nodeToModify.x + (2 * (float)Math.random() - 1 ) * movement ;
						float newY = nodeToModify.y + (2 * (float)Math.random() - 1 ) * movement ;
						//
						// energy of nodeToModify prior to the translation
						float oldNodeEnergy = nodeToModify.initialX ;
						//
						//energy of nodeToModify after the translation
						float newNodeEnergy = computeModifiedEnergy(allNodes, nodeToModify, newX, newY, nodeVisitedValue) ;
						//
						// the translation improves the layout ;
						// it is always accepted
						if ( newNodeEnergy < oldNodeEnergy )
						{
							modifyPosition(allNodes, nodeToModify, newX, newY, nodeVisitedValue) ;
							// updating the layout energy rather
							// than recomputing it from scratch
							// (faster)
							layoutEnergy += newNodeEnergy - oldNodeEnergy ;
							//
							//elitism : keeping track of the best position found so far
							if ( layoutEnergy < bestEnergy )
							{
								bestEnergy = layoutEnergy ;	
								elitism(nodesToMove) ;
							}
							//
						}
						// the translation worsens the layout ;
						// it is probabilistically accepted
						else
						{
							float rn = (float) Math.random() ;

							if( Math.exp((oldNodeEnergy - newNodeEnergy)/ (kB * probabilitySA)) >= rn)
							{
								modifyPosition(allNodes, nodeToModify, newX, newY, nodeVisitedValue) ;
								layoutEnergy += newNodeEnergy - oldNodeEnergy ;
							}
						}//if
					}//for nodeToModify
				}// for i

				float[] rep = {layoutEnergy,bestEnergy} ;
				return rep ;
			}

			
			/**
			 * performs a simulated annealing optimisation on the energy function
			 * of the layout to find a 'good' configuration ;
			 * 
			 * elistism is implemented : the new layout will be better than the old
			 * one (or unchanged in the worst case) ;
			 * 
			 * 'nbStepSA' are performed, making for each step 'nbAttempts' attempts to
			 * modify the position of each node ;
			 * 
			 * the maximum authorised movements in each direction follow a geometric
			 * law from maxMove to minMove ;
			 * 
			 * this function "cleans" the nodes before ending (fields x/y set to the
			 * position of each node, the other fields set to 0)
			 * 
			 * @param listOfNodes list of the nodes
			 * @param nbStepSA number of steps of simulated annealing to perform
			 * @param nbAttempts number of attempts to modify the position of a
			 * node in stepSA
			 * @param maxMove maximum movement authorised for the SA steps
			 * @param minMove minimum movement authorised for the SA steps
			 */
			private int simulatedAnnealingHM( HashMap<Integer, Node> listOfNodesHM, Node[] listOfNodes, int nbStepsSA, int nbAttempts, float initProba,
					float lambdaProbability, float lambdaMovement, boolean nodeVisitedValue )
			{
				// elitism : remembering the best layout ever found
				float initEnergy = computeTotalEnergyHM(listOfNodesHM, nodeVisitedValue, true) ;
				float bestEnergy = initEnergy ;

				//
				// keeping the value of the layout's energy enables
				// to update the new value (when the position of 
				// one node is modified) in linear time rather than
				// having to compute it again from scratch (quadratic time)
				float oldEnergy =  bestEnergy ;
				//
				// 'temperatures' :
				// probabilitySA rules the probability to accept a worse
				// layout than the current one (to escape from local
				// minimums), movementSA limits the movement of the nodes
				float probabilitySA = initProba ;
				float movementSA = 1 ;
				//
				//typical temperature 'kB'
				float kB = 0.5f*(idealDistance-springLength)*(idealDistance-springLength)
				+ 2 * forceConst / idealDistance ;
				//
				int nbSteps = 0 ;
				//
				while ( nbSteps < nbStepsSA  )
				{
					float[] var = stepSAHM(listOfNodesHM, listOfNodes, oldEnergy, bestEnergy, movementSA, kB, probabilitySA, nbAttempts, nodeVisitedValue) ;
					oldEnergy = var[0] ;
					bestEnergy = var[1] ;

					// updating the temperatures
					probabilitySA *= lambdaProbability ;
					movementSA *= lambdaMovement ;
					//
					nbSteps ++ ;					
				}
				//

				// if oldEnergy (current layout) is not the best configuration 
				// ever found, the positions are set to the best (elitism) and
				// the fields used are cleaned
				if (bestEnergy < oldEnergy)
				{
					bestLayoutSA(listOfNodes) ;
				}
				else
				{
					cleanSA(listOfNodes) ;
				}
				//

				return nbSteps ;
			}//void simulatedAnnealing
			
			
			/**
			 * performs some simulating-annealing steps on
			 * nodesToMove ; the best layout ever found is remembered
			 * (elitism) through the fields finalX/Y of each node
			 * 
			 * @param listOfNodes list of the nodes
			 * @param initEnergy energy before any modifications
			 * @param tempSA simulated-annealing temperature
			 * 
			 * @return a length-2 array {layoutEnergy,bestEnergy} with
			 * layoutEnergy the energy of the current configuration,
			 * and bestLayoutEnergy the energy for the best
			 * configuration ever found -- !! the finalX/Y
			 * fields of each node are also modified !!
			 */
			private float[] stepSAHM( HashMap<Integer,Node> allNodesHM, Node[] allNodes, float initLayoutEnergy, float bestLayoutEnergy,
					float movementSA, float kB, float probabilitySA, int nbAttempts, boolean nodeVisitedValue )
			{
				// total energy of the layout
				float layoutEnergy = initLayoutEnergy ;
				float bestEnergy = bestLayoutEnergy ;

				// nbAttempts attempts to modify  the position of each node
				for(int i = 0 ; i < nbAttempts ; i ++ )
				{
					for(int j = 0 ; j < allNodesHM.size() ; j ++ )
					{
						Node nodeToModify = allNodes[j] ;

						// simmering in fact
						float maxMovement = 3*idealDistance ;
						float movement = movementSA * maxMovement + idealDistance/5 ;
						//
						// random translation (x,y) in [-movement ; movement]^2
						float newX = nodeToModify.x + (2 * (float)Math.random() - 1 ) * movement ;
						float newY = nodeToModify.y + (2 * (float)Math.random() - 1 ) * movement ;
						//
						// energy of nodeToModify prior to the translation
						float oldNodeEnergy = nodeToModify.initialX ;
						//
						//energy of nodeToModify after the translation
						float newNodeEnergy = computeModifiedEnergyHM(allNodesHM, allNodes, nodeToModify, newX, newY, nodeVisitedValue) ;
						//
						// the translation improves the layout ;
						// it is always accepted
						if ( newNodeEnergy < oldNodeEnergy )
						{
							modifyPositionHM(allNodesHM, allNodes, nodeToModify, newX, newY, nodeVisitedValue) ;
							// updating the layout energy rather
							// than recomputing it from scratch
							// (faster)
							layoutEnergy += newNodeEnergy - oldNodeEnergy ;
							//
							//elitism : keeping track of the best position found so far
							if ( layoutEnergy < bestEnergy )
							{
								bestEnergy = layoutEnergy ;	
								elitism(allNodes) ;
							}
							//
						}
						// the translation worsens the layout ;
						// it is probabilistically accepted
						else
						{
							float rn = (float) Math.random() ;

							if( Math.exp((oldNodeEnergy - newNodeEnergy)/ (kB * probabilitySA)) >= rn)
							{
								modifyPositionHM(allNodesHM, allNodes, nodeToModify, newX, newY, nodeVisitedValue) ;
								layoutEnergy += newNodeEnergy - oldNodeEnergy ;
							}
						}
					}//for nodeToModify
				}// for i

				float[] rep = {layoutEnergy,bestEnergy} ;
				return rep ;
			}
			

			/**
			 * sets the x-y position of each node to the value
			 * in final X/Y, which corresponds, in the case of simulated
			 * annealing optimisation, to the best layout ever found ;
			 * 
			 * the final X/Y fields of each node are also re-set to 0
			 * 
			 * @param listOfNode list of the nodes
			 */
			void bestLayoutSA( ArrayList<Node> listOfNode)
			{
				for ( Node currentNode : listOfNode )
				{
					currentNode.x = currentNode.finalX ;
					currentNode.y = currentNode.finalY ;

					currentNode.finalX = 0 ;
					currentNode.finalY = 0 ;
				}
			}//void bestLayoutSA

			
			/**
			 * sets the x-y position of each node to the value
			 * in final X/Y, which corresponds, in the case of simulated
			 * annealing optimisation, to the best layout ever found ;
			 * 
			 * the final X/Y fields of each node are also re-set to 0
			 * 
			 * @param listOfNode list of the nodes
			 */
			void bestLayoutSA( Node[] listOfNode)
			{
				for ( Node currentNode : listOfNode )
				{
					currentNode.x = currentNode.finalX ;
					currentNode.y = currentNode.finalY ;

					currentNode.finalX = 0 ;
					currentNode.finalY = 0 ;
				}
			}//void bestLayoutSA
			
			
			/**
			 * for all nodes in listOfNodes
			 * assigns the 'x' field to
			 * the 'finalX' value and the
			 * 'y' field to the 'finalY'
			 * value
			 * 
			 * @param listOfNodes nodes on
			 * which to apply this transformation
			 */
			private void elitism(ArrayList<Node> listOfNodes)
			{
				for (Node currentNode : listOfNodes)
				{
					currentNode.finalX = currentNode.x ;
					currentNode.finalY = currentNode.y ;
				}
			}//void elistism
			
			
			/**
			 * for all nodes in listOfNodes
			 * assigns the 'x' field to
			 * the 'finalX' value and the
			 * 'y' field to the 'finalY'
			 * value
			 * 
			 * @param listOfNodes nodes on
			 * which to apply this transformation
			 */
			private void elitism( Node[] listOfNodes)
			{
				for (Node currentNode : listOfNodes)
				{
					currentNode.finalX = currentNode.x ;
					currentNode.finalY = currentNode.y ;
				}
			}//void elistism
			
			
			/**
			 * re-set the final X/Y and initialX fields
			 * of each nodes to 0
			 * 
			 * @param listOfNode list of the nodes
			 */
			void cleanSA( Node[] listOfNodes )
			{
				for ( Node currentNode : listOfNodes )
				{
					currentNode.finalX = 0 ;
					currentNode.finalY = 0 ;

					currentNode.initialX = 0 ;

				}
			}//void cleanSA
			

			/**
			 * re-set the final X/Y and initialX fields
			 * of each nodes to 0
			 * 
			 * @param listOfNode list of the nodes
			 */
			void cleanSA(ArrayList<Node> listOfNodes)
			{
				for ( Node currentNode : listOfNodes )
				{
					currentNode.finalX = 0 ;
					currentNode.finalY = 0 ;

					currentNode.initialX = 0 ;

				}
			}//void cleanSA

			
			// * * *

			

			// * * * General purpose energy-related functions * * *


			/**
			 * compute the total energy of the layout
			 * the contribution of each node to the total energy
			 * is written in the initialX field of the node
			 * 
			 * !! ATTENTION !! the total energy of the layout IS NOT
			 * the sum of the energy of each node !
			 * 
			 * !! the initialX field of the nodes must be clean in
			 * order to use this function !!
			 * 
			 * @param listOfNodes list of the nodes
			 * @param SAelitism whether the position of the nodes
			 * should also be written in finalX/Y (for SA-elistism)
			 *  
			 * @return energy of the current configuration
			 * 		   (and modify the fields initialX and final X/Y
			 * 			(if SAelitism = true)  of all the nodes)
			 */
			private float computeTotalEnergy(ArrayList<Node> listOfNodes, boolean nodeVisitedValue, boolean SAelitism)
			{
				float totalLayoutEnergy = 0 ;

				for (int i = 0 ; i < listOfNodes.size()-1 ; i ++ )
				{
					Node refNode = listOfNodes.get(i) ;

					for (Edge currentEdge = refNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(refNode))
					{
						if (currentEdge.isAccessed == edgeVisitedValue)
						{
							continue ;
						}
						currentEdge.isAccessed = edgeVisitedValue ;

						Node otherNode = (currentEdge.source == refNode)? currentEdge.target : currentEdge.source ;
						otherNode.isAccessed = nodeVisitedValue ;

						float distance  = (float) Math.sqrt((otherNode.x - refNode.x)*(otherNode.x - refNode.x) +
								(otherNode.y - refNode.y)*(otherNode.y - refNode.y)) ;
						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
						}

						float energy = 0.25f * (distance - springLength) * (distance - springLength) + forceConst / distance ;

						//this field is used to accumulate the energy for each node
						refNode.initialX += energy;
						otherNode.initialX += energy ;

						totalLayoutEnergy += energy ;
					}


					for(int j = i+1 ; j < listOfNodes.size() ; j ++)
					{
						Node tempNode = listOfNodes.get(j) ;

						if (tempNode.isAccessed == nodeVisitedValue )
						{
							tempNode.isAccessed = !nodeVisitedValue ;
							continue ;
						}

						float distance  = (float) Math.sqrt((tempNode.x - refNode.x)*(tempNode.x - refNode.x) +
								(tempNode.y - refNode.y)*(tempNode.y - refNode.y)) ;

						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
						}


						float energy = forceConst / distance ;

						//this field is used to accumulate the energy for each node
						tempNode.initialX += energy ;
						refNode.initialX += energy ;

						totalLayoutEnergy += energy ;
					}//for int j	
				}//for int i

				edgeVisitedValue = !edgeVisitedValue ;				

				// - - 
				if(SAelitism)
				{
					for (Node refNode : listOfNodes)
					{
						//remembering the initial position in case
						// the SA algo fails to find a better layout
						refNode.finalX = refNode.x ;
						refNode.finalY = refNode.y ;
						//
					}
				}
				// - -


				return totalLayoutEnergy ;					
			}// float computeTotalEnergy
			
			
			/**
			 * compute the total energy of the layout
			 * the contribution of each node to the total energy
			 * is written in the initialX field of the node
			 * 
			 * !! ATTENTION !! the total energy of the layout IS NOT
			 * the sum of the energy of each node !
			 * 
			 * !! the initialX field of the nodes must be clean in
			 * order to use this function !!
			 * 
			 * @param listOfNodes list of the nodes
			 * @param SAelitism whether the position of the nodes
			 * should also be written in finalX/Y (for SA-elistism)
			 *  
			 * @return energy of the current configuration
			 * 		   (and modify the fields initialX and final X/Y
			 * 			(if SAelitism = true)  of all the nodes)
			 */
			private float computeTotalEnergyHM(HashMap<Integer, Node> listOfNodesHM, boolean nodeVisitedValue, boolean SAelitism)
			{
				float totalLayoutEnergy = 0 ;
				
				Node[] listOfNodes = new Node[listOfNodesHM.size()] ;
				listOfNodesHM.values().toArray(listOfNodes) ;
				
				
				for (int i = 0 ; i < listOfNodes.length -1 ; i ++ )
				{
					Node refNode = listOfNodes[i] ;

					for (Edge currentEdge = refNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(refNode))
					{
						Node otherNode = (currentEdge.source == refNode)? currentEdge.target : currentEdge.source ;
						
						if ( !listOfNodesHM.containsKey(otherNode.object.hashCode()) )
						{
							continue ;
						}
						
						if (currentEdge.isAccessed == edgeVisitedValue)
						{
							currentEdge.isAccessed = !edgeVisitedValue ;
							continue ;
						}
						currentEdge.isAccessed = edgeVisitedValue ;
						
						otherNode.isAccessed = nodeVisitedValue ;

						float distance  = (float) Math.sqrt((otherNode.x - refNode.x)*(otherNode.x - refNode.x) +
								(otherNode.y - refNode.y)*(otherNode.y - refNode.y)) ;
						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
						}

						float energy = 0.25f * (distance - springLength) * (distance - springLength) + forceConst / distance ;

						//this field is used to accumulate the energy for each node
						refNode.initialX += energy;
						otherNode.initialX += energy ;

						totalLayoutEnergy += energy ;
					}


					for(int j = i+1 ; j < listOfNodes.length ; j ++)
					{
						Node tempNode = listOfNodes[j] ;

						if (tempNode.isAccessed == nodeVisitedValue )
						{
							tempNode.isAccessed = !nodeVisitedValue ;
							continue ;
						}

						float distance  = (float) Math.sqrt((tempNode.x - refNode.x)*(tempNode.x - refNode.x) +
								(tempNode.y - refNode.y)*(tempNode.y - refNode.y)) ;

						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
						}

						float energy = forceConst / distance ;

						//this field is used to accumulate the energy for each node
						tempNode.initialX += energy ;
						refNode.initialX += energy ;

						totalLayoutEnergy += energy ;
					}//for int j	
				}//for int i
				//
				Node lastNode = listOfNodes[listOfNodes.length-1] ;
				for (Edge currentEdge = lastNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(lastNode))
				{
					currentEdge.isAccessed = !edgeVisitedValue ;
				}
				//
				// - - 
				if(SAelitism)
				{
					for (Node refNode : listOfNodes)
					{
						//remembering the initial position in case
						// the SA algo fails to find a better layout
						refNode.finalX = refNode.x ;
						refNode.finalY = refNode.y ;
						//
					}
				}
				// - -

				return totalLayoutEnergy ;					
			}// float computeTotalEnergyHM
			
			
			/**
			 * compute the energy contribution of modifiedNode to the total energy
			 * if it were at the coordinates (newX,newY)
			 * 
			 * does NOT modify any properties of the nodes
			 * 
			 * @param listOfNodes list of the nodes
			 * @param modifiedNode the node whose position is (virtually) modified to compute the new energy
			 * @param newX new (virtual) x position of modifiedNode
			 * @param newY new (virtual) y position of modifiedNode
			 * 
			 * @return energy contribution of modifiedNode for the coordinates (newX,newY)
			 */
			private float computeModifiedEnergy( ArrayList<Node> listOfNodes, Node modifiedNode, float newX, float newY, boolean nodeVisitedValue)
			{
				float newEnergy = 0 ;

				for (Edge currentEdge = modifiedNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(modifiedNode))
				{
					Node otherNode = (currentEdge.source == modifiedNode)? currentEdge.target : currentEdge.source ;
					otherNode.isAccessed = nodeVisitedValue ;

					float distance  = (float) Math.sqrt((otherNode.x - newX)*(otherNode.x - newX) +
							(otherNode.y - newY)*(otherNode.y - newY)) ;
					if (distance <= criticalDistance )
					{
						distance = criticalDistance ;
					}

					newEnergy += 0.25f * (distance - springLength) * (distance - springLength) + forceConst / distance ;
				}

				for( Node tempNode : listOfNodes )
				{					
					if ( tempNode != modifiedNode )
					{
						if (tempNode.isAccessed == nodeVisitedValue)
						{
							tempNode.isAccessed = !nodeVisitedValue ;
							continue ;
						}

						float distance  = (float) Math.sqrt((tempNode.x - newX)*(tempNode.x - newX) +
								(tempNode.y - newY)*(tempNode.y - newY)) ;

						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
						}

						newEnergy += forceConst / distance ;
					}
				}

				return newEnergy ;

			}// float computeModifiedEnergy

			
			/**
			 * compute the energy contribution of modifiedNode to the total energy
			 * if it were at the coordinates (newX,newY)
			 * 
			 * does NOT modify any properties of the nodes
			 * 
			 * @param listOfNodes list of the nodes
			 * @param modifiedNode the node whose position is (virtually) modified to compute the new energy
			 * @param newX new (virtual) x position of modifiedNode
			 * @param newY new (virtual) y position of modifiedNode
			 * 
			 * @return energy contribution of modifiedNode for the coordinates (newX,newY)
			 */
			private float computeModifiedEnergyHM( HashMap<Integer, Node> listOfNodesHM, Node[] listOfNodes, Node modifiedNode, float newX, float newY, boolean nodeVisitedValue)
			{
				float newEnergy = 0 ;
				
				for (Edge currentEdge = modifiedNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(modifiedNode))
				{
					Node otherNode = (currentEdge.source == modifiedNode)? currentEdge.target : currentEdge.source ;
					
					if ( !listOfNodesHM.containsKey(otherNode.object.hashCode()) )
					{
						continue ;
					}
					otherNode.isAccessed = nodeVisitedValue ;

					float distance  = (float) Math.sqrt((otherNode.x - newX)*(otherNode.x - newX) +
							(otherNode.y - newY)*(otherNode.y - newY)) ;
					if (distance <= criticalDistance )
					{
						distance = criticalDistance ;
					}

					newEnergy += 0.25f * (distance - springLength) * (distance - springLength) + forceConst / distance ;
				}

				for( Node tempNode : listOfNodes )
				{					
					if ( tempNode != modifiedNode )
					{
						if (tempNode.isAccessed == nodeVisitedValue)
						{
							tempNode.isAccessed = !nodeVisitedValue ;
							continue ;
						}

						float distance  = (float) Math.sqrt((tempNode.x - newX)*(tempNode.x - newX) +
								(tempNode.y - newY)*(tempNode.y - newY)) ;

						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
						}

						newEnergy += forceConst / distance ;
					}
				}

				return newEnergy ;

			}// float computeModifiedEnergyHM
			
			
			/**
			 * set the position of modifiedNode to (newX,newY)
			 * and modify the energy contribution of all the nodes
			 * to take this new position into account
			 * 
			 * @param listOfNodes list of the nodes
			 * @param modifiedNode node whose position will be modified
			 * @param newX	new x position of modifiedNode
			 * @param newY new x position of modifiedNode
			 * 
			 * @return (modify the initialX field of all the nodes)
			 */
			private void modifyPosition(ArrayList<Node> listOfNodes, Node modifiedNode, float newX, float newY, boolean nodeVisitedValue)
			{
				modifiedNode.initialX = 0 ;

				for (Edge currentEdge = modifiedNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(modifiedNode))
				{
					Node otherNode = (currentEdge.source == modifiedNode)? currentEdge.target : currentEdge.source ;
					otherNode.isAccessed = nodeVisitedValue ;

					float newDistance  = (float) Math.sqrt( (otherNode.x - newX)*(otherNode.x - newX)
							+ (otherNode.y - newY)*(otherNode.y - newY)) ;
					float oldDistance = (float) Math.sqrt( (otherNode.x - modifiedNode.x)*(otherNode.x - modifiedNode.x)
							+ (otherNode.y - modifiedNode.y)*(otherNode.y - modifiedNode.y)) ;

					if (newDistance <= criticalDistance )
					{
						newDistance = criticalDistance ;
					}
					if (oldDistance <= criticalDistance )
					{
						oldDistance = criticalDistance ;
					}

					float newEnergy =  0.25f * (newDistance - springLength) * (newDistance - springLength) + forceConst / newDistance ;

					otherNode.initialX += -0.25f * (oldDistance - springLength) * (oldDistance - springLength) - forceConst / oldDistance + newEnergy;
					modifiedNode.initialX += newEnergy ;
				}

				for( Node tempNode : listOfNodes )
				{
					if ( tempNode != modifiedNode )
					{
						if( tempNode.isAccessed == nodeVisitedValue )
						{
							tempNode.isAccessed = !nodeVisitedValue ;
							continue ;
						}
						float newDistance  = (float) Math.sqrt( (tempNode.x - newX)*(tempNode.x - newX)
								+ (tempNode.y - newY)*(tempNode.y - newY)) ;
						float oldDistance = (float) Math.sqrt( (tempNode.x - modifiedNode.x)*(tempNode.x - modifiedNode.x)
								+ (tempNode.y - modifiedNode.y)*(tempNode.y - modifiedNode.y)) ;

						if (newDistance <= criticalDistance )
						{
							newDistance = criticalDistance ;
						}
						if (oldDistance <= criticalDistance )
						{
							oldDistance = criticalDistance ;
						}

						float newEnergy = forceConst / newDistance ;

						tempNode.initialX += - forceConst / oldDistance + newEnergy ;
						modifiedNode.initialX += newEnergy ;
					}
				}

				modifiedNode.x = newX ;
				modifiedNode.y = newY ;

			}//void ModifyPosition
			
			
			/**
			 * set the position of modifiedNode to (newX,newY)
			 * and modify the energy contribution of all the nodes
			 * to take this new position into account
			 * 
			 * @param listOfNodes list of the nodes
			 * @param modifiedNode node whose position will be modified
			 * @param newX	new x position of modifiedNode
			 * @param newY new x position of modifiedNode
			 * 
			 * @return (modify the initialX field of all the nodes)
			 */
			private void modifyPositionHM(HashMap<Integer, Node> listOfNodesHM, Node[] listOfNodes, Node modifiedNode, float newX, float newY, boolean nodeVisitedValue)
			{
				modifiedNode.initialX = 0 ;

				for (Edge currentEdge = modifiedNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(modifiedNode))
				{
					Node otherNode = (currentEdge.source == modifiedNode)? currentEdge.target : currentEdge.source ;
					
					if(!listOfNodesHM.containsKey(otherNode.object.hashCode()))
					{
						continue ;
					}
					otherNode.isAccessed = nodeVisitedValue ;

					float newDistance  = (float) Math.sqrt( (otherNode.x - newX)*(otherNode.x - newX)
							+ (otherNode.y - newY)*(otherNode.y - newY)) ;
					float oldDistance = (float) Math.sqrt( (otherNode.x - modifiedNode.x)*(otherNode.x - modifiedNode.x)
							+ (otherNode.y - modifiedNode.y)*(otherNode.y - modifiedNode.y)) ;

					if (newDistance <= criticalDistance )
					{
						newDistance = criticalDistance ;
					}
					if (oldDistance <= criticalDistance )
					{
						oldDistance = criticalDistance ;
					}

					float newEnergy =  0.25f * (newDistance - springLength) * (newDistance - springLength) + forceConst / newDistance ;

					otherNode.initialX += -0.25f * (oldDistance - springLength) * (oldDistance - springLength) - forceConst / oldDistance + newEnergy;
					modifiedNode.initialX += newEnergy ;
				}

				for( Node tempNode : listOfNodes )
				{
					if ( tempNode != modifiedNode )
					{
						if( tempNode.isAccessed == nodeVisitedValue )
						{
							tempNode.isAccessed = !nodeVisitedValue ;
							continue ;
						}
						float newDistance  = (float) Math.sqrt( (tempNode.x - newX)*(tempNode.x - newX)
								+ (tempNode.y - newY)*(tempNode.y - newY)) ;
						float oldDistance = (float) Math.sqrt( (tempNode.x - modifiedNode.x)*(tempNode.x - modifiedNode.x)
								+ (tempNode.y - modifiedNode.y)*(tempNode.y - modifiedNode.y)) ;

						if (newDistance <= criticalDistance )
						{
							newDistance = criticalDistance ;
						}
						if (oldDistance <= criticalDistance )
						{
							oldDistance = criticalDistance ;
						}

						float newEnergy = forceConst / newDistance ;

						tempNode.initialX += - forceConst / oldDistance + newEnergy ;
						modifiedNode.initialX += newEnergy ;
					}
				}

				modifiedNode.x = newX ;
				modifiedNode.y = newY ;

			}//void ModifyPositionHM
			

			// * * *



			// * * * (Modified) Newton Optimisation * * *
			
			
			/*
			 * performs a heuristic optimisation to find a layout configuration :
			 * the energy is considered as a function of only two variables
			 * at a time (one node moving, the others being frozen),
			 * and this function is optimized via a (modified) Newton
			 * method ;
			 * 
			 * the node moving is chosen as the one for which the norm
			 * of the gradient of the energy (as a function of its 
			 * position only) is maximum (except the root -- considered
			 * as fixed) ;
			 * 
			 * sqNormMinGrad gives a convergence criterion for the method 
			 * (squared norm of the gradient < sqNormGrad), and 
			 * a maximum number of iteration is also specified ;
			 * 
			 * !!! the initialX field of the nodes must be 'clean'
			 * (set to 0) in order to use this function !!! ;
			 * 
			 * this function "cleans" the nodes before ending (fields x/y set to the
			 * position of each node, other fields set to 0) ;
			 * 
			 * 
			 * @param listOfNodes list of the nodes
			 * @param nbStepMax total maximum number of iterations
			 * for this method (among all nodes)
			 */
			/*
			private int newtonOptimisation(ArrayList<Node> listOfNodes, int nbStepMax, float sqNormMinGrad, boolean nodeVisitedValue )
			{
				int nbStep = 0 ;

				// the field initialX is used by this function !
				computeTotalEnergy(listOfNodes, nodeVisitedValue, false) ;
				//
				while( nbStep < nbStepMax )
				{
					// choosing the node which has the greatest energy gradient
					// (in norm) when considering the energy as a function
					// of its position (the other node being frozen)
					int nodeIndex = initOptimisation(listOfNodes, nodeVisitedValue) ;
					//
					Node movingNode = listOfNodes.get(nodeIndex) ;
					//
					// maximum squared (euclidian) norm of the
					// gradient among the nodes
					float sqNormGradMax = movingNode.finalX ;
					//
					// cleaning this field
					movingNode.finalX = 0 ;
					//
					// exiting if the algorithm has converged
					if( sqNormGradMax < sqNormMinGrad )
					{
						break ;
					}
					// else continuing to improve the layout
					//

					boolean continueStep = true ;
					boolean visitedFieldValue = movingNode.isAccessed ;

					float energy = movingNode.initialX ;

					// saving the old position enables to update the
					// energy function later rather than computing it
					// from scratch
					float oldX = movingNode.x ;
					float oldY = movingNode.y ;
					//
					// optimisation of the energy as a function of
					// the position of moving node (the other nodes
					// being frozen
					while( continueStep && (nbStep < nbStepMax) )
					{
						energy = newtonOpimisationStep(listOfNodes, movingNode, energy, sqNormMinGrad, nodeVisitedValue) ;
						continueStep = movingNode.isAccessed ;
						nbStep ++ ;
					}
					//
					// resetting this field
					movingNode.isAccessed = visitedFieldValue ;
					//
					// updating the energy of the layout
					float newX = movingNode.x ;
					float newY = movingNode.y ;
					movingNode.x = oldX ;
					movingNode.y = oldY ;
					modifyPosition(listOfNodes, movingNode, newX, newY, nodeVisitedValue) ;
					//

				}//while

				cleanNewton(listOfNodes) ;

				return nbStep ;

			}//void newtonOptimisation
			*/
			
			/**
			 * places the 'newNodes' according
			 * to a Newton optimization
			 * 
			 * @param newNodes
			 * @param allNodes
			 * @param nbStepMax
			 * @param sqNormMinGrad
			 */
			private int newtonPlacement(ArrayList<Node> newNodes, ArrayList<Node> allNodes, int nbStepMax, float sqNormMinGrad, boolean nodeVisitedValue )
			{
				int nbStep = 0 ;

				// the field initialX is used by this function !
				computeTotalEnergy(allNodes, nodeVisitedValue, false) ;
				//

				while( nbStep < nbStepMax )
				{
					// choosing the node which has the greatest energy gradient
					// (in norm) when considering the energy as a function
					// of its position (the other node being frozen)
					int nodeIndex = initOptimisation(allNodes, newNodes, nodeVisitedValue) ;
					//

					Node nodeToPlace = allNodes.get(nodeIndex) ;

					// maximum squared (euclidian) norm of the
					// gradient among the nodes
					float sqNormGradMax = nodeToPlace.finalX ;
					//
					// cleaning this field
					nodeToPlace.finalX = 0 ;
					//
					// exiting if the algorithm has converged
					if( sqNormGradMax < sqNormMinGrad )
					{	
						break ;
					}
					// else continuing to improve the layout
					//

					boolean continueStep = true ;
					boolean fieldVisitedValue = nodeToPlace.isAccessed ;

					float energy = nodeToPlace.initialX ;

					// saving the old position enables to update the
					// energy function later rather than computing it
					// from scratch
					float oldX = nodeToPlace.x ;
					float oldY = nodeToPlace.y ;
					//
					// optimisation of the energy as a function of
					// the position of moving node (the other nodes
					// being frozen
					while( continueStep && (nbStep < nbStepMax) )
					{
						energy = newtonOpimisationStep(allNodes, nodeToPlace, energy, sqNormMinGrad, nodeVisitedValue) ;
						continueStep = nodeToPlace.isAccessed ;
						nbStep ++ ;
					}

					// updating the energy of the layout
					float newX = nodeToPlace.x ;
					float newY = nodeToPlace.y ;
					nodeToPlace.x = oldX ;
					nodeToPlace.y = oldY ;
					modifyPosition(allNodes, nodeToPlace, newX, newY, nodeVisitedValue) ;
					//
					// resetting this field
					nodeToPlace.isAccessed = fieldVisitedValue ;
					//

				}//while

				cleanNewton(allNodes) ;
				return nbStep ;
			}
			
			
			/**
			 * performs a heuristic optimisation to find a layout configuration :
			 * the energy is considered as a function of only two variables
			 * at a time (one node moving, the others being frozen),
			 * and this function is optimized via a (modified) Newton
			 * method ;
			 * 
			 * the node moving is chosen as the one for which the norm
			 * of the gradient of the energy (as a function of its 
			 * position only) is maximum (except the root -- considered
			 * as fixed) ;
			 * 
			 * sqNormMinGrad gives a convergence criterion for the method 
			 * (squared norm of the gradient < sqNormGrad), and 
			 * a maximum number of iteration is also specified ;
			 * 
			 * !!! the initialX field of the nodes must be 'clean'
			 * (set to 0) in order to use this function !!! ;
			 * 
			 * this function "cleans" the nodes before ending (fields x/y set to the
			 * position of each node, other fields set to 0) ;
			 * 
			 * 
			 * @param listOfNodes list of the nodes
			 * @param nbStepMax total maximum number of iterations
			 * for this method (among all nodes)
			 */
			private int newtonOptimisationHM(HashMap<Integer, Node> listOfNodesHM, Node[] listOfNodes, int nbStepMax, float sqNormMinGrad, boolean nodeVisitedValue )
			{
				int nbStep = 0 ;

				// the field initialX is used by this function !
				computeTotalEnergyHM(listOfNodesHM, nodeVisitedValue, false) ;
				//

				while( nbStep < nbStepMax )
				{
					// choosing the node which has the greatest energy gradient
					// (in norm) when considering the energy as a function
					// of its position (the other node being frozen)
					int nodeIndex = initOptimisationHM(listOfNodesHM, listOfNodes, nodeVisitedValue) ;
					//
					Node movingNode = listOfNodes[nodeIndex] ;
					//
					// maximum squared (euclidian) norm of the
					// gradient among the nodes
					float sqNormGradMax = movingNode.finalX ;
					//
					// cleaning this field
					movingNode.finalX = 0 ;
					//
					// exiting if the algorithm has converged
					if( sqNormGradMax < sqNormMinGrad )
					{
						break ;
					}
					// else continuing to improve the layout
					//

					boolean continueStep = true ;
					boolean visitedFieldValue = movingNode.isAccessed ;

					float energy = movingNode.initialX ;

					// saving the old position enables to update the
					// energy function later rather than computing it
					// from scratch
					float oldX = movingNode.x ;
					float oldY = movingNode.y ;
					//
					// optimisation of the energy as a function of
					// the position of moving node (the other nodes
					// being frozen
					while( continueStep && (nbStep < nbStepMax) )
					{
						energy = newtonOpimisationStepHM(listOfNodesHM, listOfNodes, movingNode, energy, sqNormMinGrad, nodeVisitedValue) ;
						continueStep = movingNode.isAccessed ;
						nbStep ++ ;
					}
					//
					// resetting this field
					movingNode.isAccessed = visitedFieldValue ;
					//
					// updating the energy of the layout
					float newX = movingNode.x ;
					float newY = movingNode.y ;
					movingNode.x = oldX ;
					movingNode.y = oldY ;
					modifyPositionHM(listOfNodesHM, listOfNodes, movingNode, newX, newY, nodeVisitedValue) ;
					//

				}//while

				cleanNewton(listOfNodes) ;

				return nbStep ;

			}//void newtonOptimisationHM
			
			
			/*
			 * returns the index of the node (except the root) in listOfNodes
			 * which gradient is the greatest (in norm) when considering 
			 * the energy as a function on its coordinates
			 * (the other nodes being 'frozen') ;
			 * 
			 * the finalX field of the node for which the gradient 
			 * is maximum is set to the square of the (euclidian) norm
			 * of the gradient ;
			 * 
			 * !!! the fields final X/Y have to be clean to use this method !!!
			 * 
			 * @param listOfNodes list of the nodes
			 * @return indexMax index of the node which gradient is the greatest (in norm)
			 * (and set its finalX field to the value of this norm)
			 */
			/*
			private int initOptimisation(ArrayList<Node> listOfNodes, boolean nodeVisitedValue)
			{
				float gradMax = 0 ;
				int indexMax = 1 ;

				for (int i = 0 ; i < listOfNodes.size()-1 ; i ++ )
				{
					Node refNode = listOfNodes.get(i) ;

					// edges
					for (Edge currentEdge = refNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(refNode))
					{
						if (currentEdge.isAccessed == edgeVisitedValue)
						{
							continue ;
						}
						currentEdge.isAccessed = edgeVisitedValue ;

						Node otherNode = (currentEdge.source == refNode)? currentEdge.target : currentEdge.source ;
						otherNode.isAccessed = nodeVisitedValue ;

						float deltaX = refNode.x - otherNode.x ;
						float deltaY = refNode.y - otherNode.y ;

						float squaredDistance = deltaX * deltaX + deltaY * deltaY ;
						float distance  = (float) Math.sqrt(squaredDistance) ;

						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
							squaredDistance = criticalDistance * criticalDistance ;

							float signX = (deltaX != 0)? Math.signum(deltaX) : (float)Math.signum(2*Math.random() -1) ;
							float signY = (deltaY != 0)? Math.signum(deltaY) : (float)Math.signum(2*Math.random() -1) ;

							deltaX = signX * criticalDistance / (float) Math.sqrt(2) ;
							deltaY = signY * criticalDistance / (float) Math.sqrt(2) ;							
						}

						float temp = 0.5f * (distance - springLength) - forceConst / squaredDistance ;
						float tempX = temp * deltaX / distance ;
						float tempY = temp * deltaY / distance ;

						refNode.finalX += tempX ;
						refNode.finalY += tempY ;
						otherNode.finalX += -tempX ;
						otherNode.finalY += -tempY ;
					}//for currentEdge
					//
					//non linked nodes
					for(int j = i+1 ; j < listOfNodes.size() ; j ++)
					{
						Node otherNode = listOfNodes.get(j) ;

						if ( otherNode.isAccessed == nodeVisitedValue)
						{
							otherNode.isAccessed = !nodeVisitedValue ;
							continue ;
						}

						float deltaX = refNode.x - otherNode.x ;
						float deltaY = refNode.y - otherNode.y ;

						float squaredDistance = deltaX * deltaX + deltaY * deltaY ;
						float distance  = (float) Math.sqrt(squaredDistance) ;

						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
							squaredDistance = criticalDistance * criticalDistance ;

							float signX = (deltaX != 0)? Math.signum(deltaX) : (float)Math.signum(2*Math.random() -1) ;
							float signY = (deltaY != 0)? Math.signum(deltaY) : (float)Math.signum(2*Math.random() -1) ;

							deltaX = signX * criticalDistance / (float) Math.sqrt(2) ;
							deltaY = signY * criticalDistance / (float) Math.sqrt(2) ;							
						}

						float temp = -forceConst / squaredDistance ;
						float tempX = temp * deltaX / distance ;
						float tempY = temp * deltaY / distance ;

						refNode.finalX += tempX ;
						refNode.finalY += tempY ;
						otherNode.finalX += -tempX ;
						otherNode.finalY += -tempY ;						
					}//for j

					if (hierarchical)
					{
						refNode.finalY += 2*hierarchicalCoefficient * (refNode.y + refNode.index * idealDistance) ;
					}

					float sqNormGrad = refNode.finalX*refNode.finalX + refNode.finalY*refNode.finalY  ;

					if ( sqNormGrad > gradMax )
					{
						indexMax = i ;
						gradMax = sqNormGrad ;
					}

					//clearing these fields
					refNode.finalX = 0 ;
					refNode.finalY = 0 ;

					//

				}//for i

				Node lastNode = listOfNodes.get(listOfNodes.size()-1) ;

				if (hierarchical)
				{
					lastNode.finalY += 2*hierarchicalCoefficient*(lastNode.y + lastNode.index * idealDistance) ;
				}

				if ( (lastNode.finalX*lastNode.finalX + lastNode.finalY*lastNode.finalY) > gradMax )
				{
					indexMax = listOfNodes.size()-1 ;
					gradMax = lastNode.finalX*lastNode.finalX + lastNode.finalY*lastNode.finalY ;
				}

				//clearing these fields
				lastNode.finalX = 0 ;
				lastNode.finalY = 0 ;

				listOfNodes.get(indexMax).finalX = gradMax ;

				edgeVisitedValue = !edgeVisitedValue ;

				return indexMax ;

			}//int initOptimisation
			*/

			/**
			 * returns the index of the node (except the root) in nodesToSearch
			 * which gradient is the greatest (in norm) when considering 
			 * the energy as a function of its coordinates exclusively
			 * (the other nodes being 'frozen') ;
			 * 
			 * the finalX field of the node for which the gradient 
			 * is maximum is set to the square of the (euclidian) norm
			 * of the gradient ;
			 * 
			 * !!! the fields final X/Y have to be clean to use this method !!!
			 * 
			 * @param listOfNodes list of the nodes
			 * @return indexMax index of the node which gradient is the greatest (in norm)
			 * (and set its finalX field to the value of this norm)
			 */
			private int initOptimisation( ArrayList<Node> allNodes, ArrayList<Node> nodesToSearch, boolean nodeVisitedValue)
			{
				// *** IMPORTANT ***
				// nodesToSearch must be 'at the end' of allNodes

				float gradMax = 0 ;
				int indexMax = 1 ;
				int indexBeginSearch = allNodes.size() - nodesToSearch.size() ;

				for (int i = 0 ; i < allNodes.size()-1 ; i ++ )
				{
					Node refNode = allNodes.get(i) ;

					// linked nodes
					for (Edge currentEdge = refNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(refNode))
					{
						if (currentEdge.isAccessed == edgeVisitedValue)
						{
							continue ;
						}
						currentEdge.isAccessed = edgeVisitedValue ;

						Node otherNode = (currentEdge.source == refNode)? currentEdge.target : currentEdge.source ;
						otherNode.isAccessed = nodeVisitedValue ;

						float deltaX = refNode.x - otherNode.x ;
						float deltaY = refNode.y - otherNode.y ;

						float squaredDistance = deltaX * deltaX + deltaY * deltaY ;
						float distance  = (float) Math.sqrt(squaredDistance) ;

						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
							squaredDistance = criticalDistance * criticalDistance ;

							float signX = (deltaX != 0)? Math.signum(deltaX) : (float)Math.signum(2*Math.random() -1) ;
							float signY = (deltaY != 0)? Math.signum(deltaY) : (float)Math.signum(2*Math.random() -1) ;

							deltaX = signX * criticalDistance / (float) Math.sqrt(2) ;
							deltaY = signY * criticalDistance / (float) Math.sqrt(2) ;							
						}

						float temp = 0.5f * (distance - springLength) - forceConst / squaredDistance ;
						float tempX = temp * deltaX / distance ;
						float tempY = temp * deltaY / distance ;

						refNode.finalX += tempX ;
						refNode.finalY += tempY ;
						otherNode.finalX += -tempX ;
						otherNode.finalY += -tempY ;
					}//for currentEdge


					for(int j = i+1 ; j < allNodes.size() ; j ++)
					{
						Node otherNode = allNodes.get(j) ;

						if (otherNode.isAccessed == nodeVisitedValue )
						{
							otherNode.isAccessed = !nodeVisitedValue ;
							continue ;
						}

						float deltaX = refNode.x - otherNode.x ;
						float deltaY = refNode.y - otherNode.y ;

						float squaredDistance = deltaX * deltaX + deltaY * deltaY ;
						float distance  = (float) Math.sqrt(squaredDistance) ;

						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
							squaredDistance = criticalDistance * criticalDistance ;

							float signX = (deltaX != 0)? Math.signum(deltaX) : (float)Math.signum(2*Math.random() -1) ;
							float signY = (deltaY != 0)? Math.signum(deltaY) : (float)Math.signum(2*Math.random() -1) ;

							deltaX = signX * criticalDistance / (float) Math.sqrt(2) ;
							deltaY = signY * criticalDistance / (float) Math.sqrt(2) ;							
						}

						float temp = -forceConst / squaredDistance ;
						float tempX = temp * deltaX / distance ;
						float tempY = temp * deltaY / distance ;

						refNode.finalX += tempX ;
						refNode.finalY += tempY ;
						otherNode.finalX += -tempX ;
						otherNode.finalY += -tempY ;						
					}//for j

					float sqNormGrad = refNode.finalX*refNode.finalX + refNode.finalY*refNode.finalY  ;

					// i >= indexBeginSearch : search only in nodesToSearch
					if ( indexBeginSearch <= i  && sqNormGrad > gradMax )
					{
						indexMax = i ;
						gradMax = sqNormGrad ;
					}

					//clearing these fields
					refNode.finalX = 0 ;
					refNode.finalY = 0 ;
					//

				}//for i

				Node lastNode = allNodes.get(allNodes.size()-1) ;

				if ( (lastNode.finalX*lastNode.finalX + lastNode.finalY*lastNode.finalY) > gradMax )
				{
					indexMax = allNodes.size()-1 ;
					gradMax = lastNode.finalX*lastNode.finalX + lastNode.finalY*lastNode.finalY ;
				}

				//clearing these fields
				lastNode.finalX = 0 ;
				lastNode.finalY = 0 ;
				//

				allNodes.get(indexMax).finalX = gradMax ;

				edgeVisitedValue = !edgeVisitedValue ;

				return indexMax ;
			}

			
			/**
			 * returns the index of the node (except the root) in listOfNodes
			 * which gradient is the greatest (in norm) when considering 
			 * the energy as a function on its coordinates
			 * (the other nodes being 'frozen') ;
			 * 
			 * the finalX field of the node for which the gradient 
			 * is maximum is set to the square of the (euclidian) norm
			 * of the gradient ;
			 * 
			 * !!! the fields final X/Y have to be clean to use this method !!!
			 * 
			 * @param listOfNodes list of the nodes
			 * @return indexMax index of the node which gradient is the greatest (in norm)
			 * (and set its finalX field to the value of this norm)
			 */
			private int initOptimisationHM(HashMap<Integer, Node> listOfNodesHM, Node[] listOfNodes, boolean nodeVisitedValue)
			{
				float gradMax = 0 ;
				int indexMax = 1 ;
				
				for (int i = 0 ; i < listOfNodes.length-1 ; i ++ )
				{
					Node refNode = listOfNodes[i] ;

					// edges
					for (Edge currentEdge = refNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(refNode))
					{
						Node otherNode = (currentEdge.source == refNode)? currentEdge.target : currentEdge.source ;
						
						if( ! listOfNodesHM.containsKey(otherNode.object.hashCode()) )
						{
							continue ;
						}
						
						if (currentEdge.isAccessed == edgeVisitedValue)
						{
							currentEdge.isAccessed = !edgeVisitedValue ;
							continue ;
						}
						currentEdge.isAccessed = edgeVisitedValue ;
						
						otherNode.isAccessed = nodeVisitedValue ;

						float deltaX = refNode.x - otherNode.x ;
						float deltaY = refNode.y - otherNode.y ;

						float squaredDistance = deltaX * deltaX + deltaY * deltaY ;
						float distance  = (float) Math.sqrt(squaredDistance) ;

						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
							squaredDistance = criticalDistance * criticalDistance ;

							float signX = (deltaX != 0)? Math.signum(deltaX) : (float)Math.signum(2*Math.random() -1) ;
							float signY = (deltaY != 0)? Math.signum(deltaY) : (float)Math.signum(2*Math.random() -1) ;

							deltaX = signX * criticalDistance / (float) Math.sqrt(2) ;
							deltaY = signY * criticalDistance / (float) Math.sqrt(2) ;							
						}

						float temp = 0.5f * (distance - springLength) - forceConst / squaredDistance ;
						float tempX = temp * deltaX / distance ;
						float tempY = temp * deltaY / distance ;

						refNode.finalX += tempX ;
						refNode.finalY += tempY ;
						otherNode.finalX += -tempX ;
						otherNode.finalY += -tempY ;
					}//for currentEdge
					//
					//non linked nodes
					for(int j = i+1 ; j < listOfNodes.length ; j ++)
					{
						Node otherNode = listOfNodes[j] ;

						if ( otherNode.isAccessed == nodeVisitedValue)
						{
							otherNode.isAccessed = !nodeVisitedValue ;
							continue ;
						}

						float deltaX = refNode.x - otherNode.x ;
						float deltaY = refNode.y - otherNode.y ;

						float squaredDistance = deltaX * deltaX + deltaY * deltaY ;
						float distance  = (float) Math.sqrt(squaredDistance) ;

						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
							squaredDistance = criticalDistance * criticalDistance ;

							float signX = (deltaX != 0)? Math.signum(deltaX) : (float)Math.signum(2*Math.random() -1) ;
							float signY = (deltaY != 0)? Math.signum(deltaY) : (float)Math.signum(2*Math.random() -1) ;

							deltaX = signX * criticalDistance / (float) Math.sqrt(2) ;
							deltaY = signY * criticalDistance / (float) Math.sqrt(2) ;							
						}

						float temp = -forceConst / squaredDistance ;
						float tempX = temp * deltaX / distance ;
						float tempY = temp * deltaY / distance ;

						refNode.finalX += tempX ;
						refNode.finalY += tempY ;
						otherNode.finalX += -tempX ;
						otherNode.finalY += -tempY ;						
					}//for j


					float sqNormGrad = refNode.finalX*refNode.finalX + refNode.finalY*refNode.finalY  ;

					if ( sqNormGrad > gradMax )
					{
						indexMax = i ;
						gradMax = sqNormGrad ;
					}

					//clearing these fields
					refNode.finalX = 0 ;
					refNode.finalY = 0 ;

				}//for i

				Node lastNode = listOfNodes[listOfNodes.length-1] ;

				if ( (lastNode.finalX*lastNode.finalX + lastNode.finalY*lastNode.finalY) > gradMax )
				{
					indexMax = listOfNodes.length-1 ;
					gradMax = lastNode.finalX*lastNode.finalX + lastNode.finalY*lastNode.finalY ;
				}

				//clearing these fields
				lastNode.finalX = 0 ;
				lastNode.finalY = 0 ;

				for (Edge currentEdge = lastNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(lastNode))
				{
					currentEdge.isAccessed = !edgeVisitedValue ;
				}

				listOfNodes[indexMax].finalX = gradMax ;

				return indexMax ;

			}//int initOptimisation
			

			/**
			 * compute the gradient and the hessian of the energy when
			 * considering it as a function of the coordinates of the node
			 * 'refNode', the other nodes being 'frozen'
			 * 
			 * the gradient is writen in the node final X/Y fields
			 * and the hessian in the fields layoutVar X/Y (Hxx/Hyy) and
			 * initialY (Hxy) -- the hessian being symetric -- !!! these fields
			 * must be 'clean' (set to 0) in order to use this method !!!
			 * 
			 * @param listOfNodes list of the nodes
			 * @param refNode node which 'moves'
			 */
			private void computeGradientHessian(ArrayList<Node> listOfNodes, Node refNode, boolean nodeVisitedValue)
			{
				// linked nodes
				for (Edge currentEdge = refNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(refNode))
				{
					Node otherNode = (currentEdge.source == refNode)? currentEdge.target : currentEdge.source ;
					otherNode.isAccessed = nodeVisitedValue ;

					float deltaX = refNode.x - otherNode.x ;
					float deltaY = refNode.y - otherNode.y ;

					float squaredDeltaX = deltaX * deltaX ;
					float squaredDeltaY = deltaY * deltaY ;

					float squaredDistance =  squaredDeltaX + squaredDeltaY ;
					float distance  = (float) Math.sqrt(squaredDistance) ;

					if (distance <= criticalDistance )
					{
						distance = criticalDistance ;
						squaredDistance = criticalDistance * criticalDistance ;

						float signX = (deltaX != 0)? Math.signum(deltaX) : (float)Math.signum(2*Math.random() -1) ;
						float signY = (deltaY != 0)? Math.signum(deltaY) : (float)Math.signum(2*Math.random() -1) ;

						deltaX = signX * criticalDistance / (float) Math.sqrt(2) ;
						deltaY = signY * criticalDistance / (float) Math.sqrt(2) ;							
					}

					float tempGrad = 0.5f * (distance - springLength) ;
					float tempHess = 0.5f * springLength ;
					float tempHess2 = 0.5f * (1 - springLength / distance) ;

					float temp = forceConst / squaredDistance ;

					//intermediate variables to reduce the
					// number of operations
					tempGrad += - temp ;
					tempHess += 3 * temp ;
					//

					//gradient(x-y)
					refNode.finalX += tempGrad * deltaX / distance ;
					refNode.finalY += tempGrad * deltaY / distance ;
					//

					float tempHessX = tempHess * squaredDeltaX - forceConst ;
					float tempHessY = tempHess * squaredDeltaY - forceConst ;

					temp = distance * squaredDistance ;

					//hessian
					//Hxx
					refNode.layoutVarX += tempHessX / temp + tempHess2 ;
					//Hyy
					refNode.layoutVarY += tempHessY / temp + tempHess2 ;	
					//Hxy = Hyx
					refNode.initialY += tempHess / temp * deltaX * deltaY ;
					//
				}//for currentEdge
				//
				// non linked Nodes
				for (Node otherNode : listOfNodes)
				{										
					if ( otherNode != refNode)
					{
						if( otherNode.isAccessed == nodeVisitedValue)
						{
							otherNode.isAccessed = !nodeVisitedValue ;
							continue ;
						}

						float deltaX = refNode.x - otherNode.x ;
						float deltaY = refNode.y - otherNode.y ;

						float squaredDeltaX = deltaX * deltaX ;
						float squaredDeltaY = deltaY * deltaY ;

						float squaredDistance =  squaredDeltaX + squaredDeltaY ;
						float distance  = (float) Math.sqrt(squaredDistance) ;

						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
							squaredDistance = criticalDistance * criticalDistance ;

							float signX = (deltaX != 0)? Math.signum(deltaX) : (float)Math.signum(2*Math.random() -1) ;
							float signY = (deltaY != 0)? Math.signum(deltaY) : (float)Math.signum(2*Math.random() -1) ;

							deltaX = signX * criticalDistance / (float) Math.sqrt(2) ;
							deltaY = signY * criticalDistance / (float) Math.sqrt(2) ;	
						}

						float temp = forceConst / squaredDistance ;

						//intermediate variables to reduce the
						// number of operations
						float tempGrad = - temp ;
						float tempHess = 3 * temp ;
						//

						//gradient(x-y)
						refNode.finalX += tempGrad * deltaX / distance ;
						refNode.finalY += tempGrad * deltaY / distance ;
						//

						float tempHessX = tempHess * squaredDeltaX - forceConst ;
						float tempHessY = tempHess * squaredDeltaY - forceConst ;

						temp = distance * squaredDistance ;

						//hessian
						//Hxx
						refNode.layoutVarX += tempHessX / temp ;
						//Hyy
						refNode.layoutVarY += tempHessY / temp ;	
						//Hxy = Hyx
						refNode.initialY += tempHess / temp * deltaX * deltaY ;
						//
					}
				}//for int i

			}//computeGradientHessian

			
			/**
			 * compute the gradient and the hessian of the energy when
			 * considering it as a function of the coordinates of the node
			 * 'refNode', the other nodes being 'frozen'
			 * 
			 * the gradient is writen in the node final X/Y fields
			 * and the hessian in the fields layoutVar X/Y (Hxx/Hyy) and
			 * initialY (Hxy) -- the hessian being symetric -- !!! these fields
			 * must be 'clean' (set to 0) in order to use this method !!!
			 * 
			 * @param listOfNodes list of the nodes
			 * @param refNode node which 'moves'
			 */
			private void computeGradientHessianHM(HashMap<Integer, Node> listOfNodesHM, Node refNode, boolean nodeVisitedValue)
			{
				Collection<Node> listOfNodes = listOfNodesHM.values() ;
				// linked nodes
				for (Edge currentEdge = refNode.getFirstEdge() ; currentEdge != null ; currentEdge = currentEdge.getNext(refNode))
				{
					Node otherNode = (currentEdge.source == refNode)? currentEdge.target : currentEdge.source ;
					
					if ( !listOfNodesHM.containsKey(otherNode.object.hashCode()) )
					{
						continue ;
					}
					
					otherNode.isAccessed = nodeVisitedValue ;

					float deltaX = refNode.x - otherNode.x ;
					float deltaY = refNode.y - otherNode.y ;

					float squaredDeltaX = deltaX * deltaX ;
					float squaredDeltaY = deltaY * deltaY ;

					float squaredDistance =  squaredDeltaX + squaredDeltaY ;
					float distance  = (float) Math.sqrt(squaredDistance) ;

					if (distance <= criticalDistance )
					{
						distance = criticalDistance ;
						squaredDistance = criticalDistance * criticalDistance ;

						float signX = (deltaX != 0)? Math.signum(deltaX) : (float)Math.signum(2*Math.random() -1) ;
						float signY = (deltaY != 0)? Math.signum(deltaY) : (float)Math.signum(2*Math.random() -1) ;

						deltaX = signX * criticalDistance / (float) Math.sqrt(2) ;
						deltaY = signY * criticalDistance / (float) Math.sqrt(2) ;							
					}

					float tempGrad = 0.5f * (distance - springLength) ;
					float tempHess = 0.5f * springLength ;
					float tempHess2 = 0.5f * (1 - springLength / distance) ;

					float temp = forceConst / squaredDistance ;

					//intermediate variables to reduce the
					// number of operations
					tempGrad += - temp ;
					tempHess += 3 * temp ;
					//

					//gradient(x-y)
					refNode.finalX += tempGrad * deltaX / distance ;
					refNode.finalY += tempGrad * deltaY / distance ;
					//

					float tempHessX = tempHess * squaredDeltaX - forceConst ;
					float tempHessY = tempHess * squaredDeltaY - forceConst ;

					temp = distance * squaredDistance ;

					//hessian
					//Hxx
					refNode.layoutVarX += tempHessX / temp + tempHess2 ;
					//Hyy
					refNode.layoutVarY += tempHessY / temp + tempHess2 ;	
					//Hxy = Hyx
					refNode.initialY += tempHess / temp * deltaX * deltaY ;
					//
				}//for currentEdge
				//
				// non linked Nodes
				for (Node otherNode : listOfNodes)
				{										
					if ( otherNode != refNode)
					{
						if( otherNode.isAccessed == nodeVisitedValue)
						{
							otherNode.isAccessed = !nodeVisitedValue ;
							continue ;
						}

						float deltaX = refNode.x - otherNode.x ;
						float deltaY = refNode.y - otherNode.y ;

						float squaredDeltaX = deltaX * deltaX ;
						float squaredDeltaY = deltaY * deltaY ;

						float squaredDistance =  squaredDeltaX + squaredDeltaY ;
						float distance  = (float) Math.sqrt(squaredDistance) ;

						if (distance <= criticalDistance )
						{
							distance = criticalDistance ;
							squaredDistance = criticalDistance * criticalDistance ;

							float signX = (deltaX != 0)? Math.signum(deltaX) : (float)Math.signum(2*Math.random() -1) ;
							float signY = (deltaY != 0)? Math.signum(deltaY) : (float)Math.signum(2*Math.random() -1) ;

							deltaX = signX * criticalDistance / (float) Math.sqrt(2) ;
							deltaY = signY * criticalDistance / (float) Math.sqrt(2) ;	
						}

						float temp = forceConst / squaredDistance ;

						//intermediate variables to reduce the
						// number of operations
						float tempGrad = - temp ;
						float tempHess = 3 * temp ;
						//

						//gradient(x-y)
						refNode.finalX += tempGrad * deltaX / distance ;
						refNode.finalY += tempGrad * deltaY / distance ;
						//

						float tempHessX = tempHess * squaredDeltaX - forceConst ;
						float tempHessY = tempHess * squaredDeltaY - forceConst ;

						temp = distance * squaredDistance ;

						//hessian
						//Hxx
						refNode.layoutVarX += tempHessX / temp ;
						//Hyy
						refNode.layoutVarY += tempHessY / temp ;	
						//Hxy = Hyx
						refNode.initialY += tempHess / temp * deltaX * deltaY ;
						//
					}
				}//for int i
			}//computeGradientHessianHM

			
			/**
			 * performs a step of a (modified) Newton optimisation method :
			 * computes the gradient and the hessian of the energy (as a function
			 * of the position of movingNode, the other nodes being frozen) choose
			 * the descent direction ("p = - B^-1 . Grad", B symmetric definite positive,
			 * the hessian matrix --modified to be positive definite 'enough' if it 
			 * is not the case), and the stepsize in this direction (using a
			 * backtracking-Armijo linesearch along p) ;
			 * 
			 * this is done if the gradient is big enough (otherwise meaning that the
			 * algorithm has converged) ;
			 * 
			 * movingNode.isAccessed is modified to false to indicate the convergence,
			 * and to true to continue the iterations ;
			 * 
			 * the current energy of the configuration is returned so that it is not
			 * re-computed ;
			 * 
			 * uses the fields layoutVar X/Y, initialY and finalX/Y of movingNode,
			 * but leave these fields clean when exiting ;
			 * 
			 * uses the field 'isAccessed' of movingNode as an indicator, and does
			 * NOT clean it
			 * 
			 * @param listOfNodes
			 * @param movingNode
			 * @param normMinGrad
			 * @param nodeVisitedValue
			 * 
			 * @return the value of the energy function at this point (and modify
			 * movingNode.isAccessed)
			 */
			private float newtonOpimisationStep(ArrayList<Node> listOfNodes, Node movingNode, float energy, float sqNormMinGrad, boolean nodeVisitedValue )
			{
				// fields layoutVar X/Y, initialY and
				// final X/Y are used by this function !
				computeGradientHessian(listOfNodes, movingNode, nodeVisitedValue) ;
				//

				// gradient values
				float gx = movingNode.finalX ;
				float gy = movingNode.finalY ;
				//
				// hessian values (symetric)
				// hxy = hyx
				float hxx = movingNode.layoutVarX ;
				float hyy = movingNode.layoutVarY ;
				float hxy = movingNode.initialY ;
				//

				// cleaning up these fields for the
				// next iterations
				movingNode.finalX = 0 ;
				movingNode.finalY = 0 ;
				movingNode.layoutVarX = 0 ;
				movingNode.layoutVarY = 0 ;
				movingNode.initialY = 0 ;
				//				
				// norm of the gradient is less than the normMinGrad
				// the algorithm has converged, exiting
				if( (gx*gx + gy*gy) < sqNormMinGrad )
				{
					// indicating that the algorithm has converged
					movingNode.isAccessed = false ;
					//
					return -1 ;
				}
				//
				// else, continuing

				// temporary variables to reduce the number
				// of operations
				float squaredHxy = hxy * hxy ;
				float delta = (hxx-hyy)*(hxx-hyy) + 4 * squaredHxy ;
				//
				//eigenvalues
				float lambda1 = 0.5f * (hxx + hyy + delta)  ;
				float lambda2 = 0.5f * (hxx + hyy - delta)  ;
				//
				// to ensure that H is 'enough' definite positive
				float epsilon = 0.00001f;
				//
				// coefficents of the 'inverse of H'
				// (eventually shifted)
				float b11 ;
				float b12 ;
				float b22 ;

				// if H (enough) definite positive
				if ( lambda1 >= epsilon && lambda2 >= epsilon)
				{
					float det = hxx * hyy - squaredHxy ;
					b11 = hyy / det ;
					b22 = hxx / det ;
					b12 = -hxy / det ;
				}
				// else, shifting the eigenvalues
				else
				{
					float squaredL1ma = (lambda1 - hxx) * (lambda1 - hxx) ;
					float squaredL2ma = (lambda2 - hxx) * (lambda2 - hxx) ;

					float squaredN1 = squaredHxy + squaredL1ma ;
					float squaredN2 = squaredHxy + squaredL2ma ;
					float n1 = (float)Math.sqrt(squaredN1) ;
					float n2 = (float)Math.sqrt(squaredN2) ;

					// shifting of the eigenvalues
					lambda1 = Math.max(epsilon, Math.abs(lambda1)) ;
					lambda2 = Math.max(epsilon, Math.abs(lambda2)) ;
					//

					float temp = squaredHxy / lambda1 ;

					b11 = ( temp + squaredL1ma / lambda2 ) / squaredN1 ;
					b22 = ( temp + squaredL2ma / lambda2 ) / squaredN2 ;
					b12 = ( temp + (lambda1 - hxx) * (lambda2 - hxx) / lambda2 ) / (n1 * n2) ;
				}
				//

				// descent direction p
				// " p = - H^-1 * Grad "
				float px = -b11 * gx - b12 * gy ;
				float py = -b12 * gx - b22 * gy  ;
				//

				// choosing the step-size using a backtracking-Armijo
				// linesearch along p
				float alpha = 1f ; 
				float beta = 0.001f ;
				float tau = 0.7f ;

				float newX = movingNode.x + alpha * px ;
				float newY = movingNode.y + alpha * py ;
				float newEnergy = computeModifiedEnergy(listOfNodes, movingNode, newX, newY, nodeVisitedValue) ;

				// to ensure that the backtracking stops
				int count = 0 ;

				// backtracking-Armijo linesearch along p
				while( newEnergy > (energy + alpha * beta * (px*gx + py*gy)) && count < 30)
				{
					count ++ ;

					alpha = tau * alpha ;
					newX = movingNode.x + alpha * px ;
					newY = movingNode.y + alpha * py ;
					newEnergy = computeModifiedEnergy(listOfNodes, movingNode, newX, newY, nodeVisitedValue) ;


				}
				//

				// changing the position of movingNode WHITHOUT using the
				// modifyPosition method, as this position is temporary
				// and it's useless to update the total layout energy
				// at this point
				movingNode.x = newX ;
				movingNode.y = newY ;
				//

				// indicating that the algorithm has not yet converged
				movingNode.isAccessed = true ;
				//

				// for the next step
				return newEnergy ;
				//
			}//newtonOptimisationStep
			
			
			/**
			 * performs a step of a (modified) Newton optimisation method :
			 * computes the gradient and the hessian of the energy (as a function
			 * of the position of movingNode, the other nodes being frozen) choose
			 * the descent direction ("p = - B^-1 . Grad", B symmetric definite positive,
			 * the hessian matrix --modified to be positive definite 'enough' if it 
			 * is not the case), and the stepsize in this direction (using a
			 * backtracking-Armijo linesearch along p) ;
			 * 
			 * this is done if the gradient is big enough (otherwise meaning that the
			 * algorithm has converged) ;
			 * 
			 * movingNode.isAccessed is modified to false to indicate the convergence,
			 * and to true to continue the iterations ;
			 * 
			 * the current energy of the configuration is returned so that it is not
			 * re-computed ;
			 * 
			 * uses the fields layoutVar X/Y, initialY and finalX/Y of movingNode,
			 * but leave these fields clean when exiting ;
			 * 
			 * uses the field 'isAccessed' of movingNode as an indicator, and does
			 * NOT clean it
			 * 
			 * @param listOfNodes
			 * @param movingNode
			 * @param normMinGrad
			 * @param nodeVisitedValue
			 * 
			 * @return the value of the energy function at this point (and modify
			 * movingNode.isAccessed)
			 */
			private float newtonOpimisationStepHM(HashMap<Integer,Node> listOfNodesHM, Node[] listOfNodes, Node movingNode, float energy, float sqNormMinGrad, boolean nodeVisitedValue )
			{
				// fields layoutVar X/Y, initialY and
				// final X/Y are used by this function !
				computeGradientHessianHM(listOfNodesHM, movingNode, nodeVisitedValue) ;
				//

				// gradient values
				float gx = movingNode.finalX ;
				float gy = movingNode.finalY ;
				//
				// hessian values (symetric)
				// hxy = hyx
				float hxx = movingNode.layoutVarX ;
				float hyy = movingNode.layoutVarY ;
				float hxy = movingNode.initialY ;
				//

				// cleaning up these fields for the
				// next iterations
				movingNode.finalX = 0 ;
				movingNode.finalY = 0 ;
				movingNode.layoutVarX = 0 ;
				movingNode.layoutVarY = 0 ;
				movingNode.initialY = 0 ;
				//				
				// norm of the gradient is less than the normMinGrad
				// the algorithm has converged, exiting
				if( (gx*gx + gy*gy) < sqNormMinGrad )
				{
					// indicating that the algorithm has converged
					movingNode.isAccessed = false ;
					//
					return -1 ;
				}
				//
				// else, continuing

				// temporary variables to reduce the number
				// of operations
				float squaredHxy = hxy * hxy ;
				float delta = (hxx-hyy)*(hxx-hyy) + 4 * squaredHxy ;
				//
				//eigenvalues
				float lambda1 = 0.5f * (hxx + hyy + delta)  ;
				float lambda2 = 0.5f * (hxx + hyy - delta)  ;
				//
				// to ensure that H is 'enough' definite positive
				float epsilon = 0.00001f;
				//
				// coefficents of the 'inverse of H'
				// (eventually shifted)
				float b11 ;
				float b12 ;
				float b22 ;

				// if H (enough) definite positive
				if ( lambda1 >= epsilon && lambda2 >= epsilon)
				{
					float det = hxx * hyy - squaredHxy ;
					b11 = hyy / det ;
					b22 = hxx / det ;
					b12 = -hxy / det ;
				}
				// else, shifting the eigenvalues
				else
				{
					float squaredL1ma = (lambda1 - hxx) * (lambda1 - hxx) ;
					float squaredL2ma = (lambda2 - hxx) * (lambda2 - hxx) ;

					float squaredN1 = squaredHxy + squaredL1ma ;
					float squaredN2 = squaredHxy + squaredL2ma ;
					float n1 = (float)Math.sqrt(squaredN1) ;
					float n2 = (float)Math.sqrt(squaredN2) ;

					// shifting of the eigenvalues
					lambda1 = Math.max(epsilon, Math.abs(lambda1)) ;
					lambda2 = Math.max(epsilon, Math.abs(lambda2)) ;
					//

					float temp = squaredHxy / lambda1 ;

					b11 = ( temp + squaredL1ma / lambda2 ) / squaredN1 ;
					b22 = ( temp + squaredL2ma / lambda2 ) / squaredN2 ;
					b12 = ( temp + (lambda1 - hxx) * (lambda2 - hxx) / lambda2 ) / (n1 * n2) ;
				}
				//

				// descent direction p
				// " p = - H^-1 * Grad "
				float px = -b11 * gx - b12 * gy ;
				float py = -b12 * gx - b22 * gy  ;
				//

				// choosing the step-size using a backtracking-Armijo
				// linesearch along p
				float alpha = 1f ; 
				float beta = 0.001f ;
				float tau = 0.7f ;

				float newX = movingNode.x + alpha * px ;
				float newY = movingNode.y + alpha * py ;
				float newEnergy = computeModifiedEnergyHM(listOfNodesHM, listOfNodes, movingNode, newX, newY, nodeVisitedValue) ;

				// to ensure that the backtracking stops
				int count = 0 ;

				// backtracking-Armijo linesearch along p
				while( newEnergy > (energy + alpha * beta * (px*gx + py*gy)) && count < 30)
				{
					count ++ ;

					alpha = tau * alpha ;
					newX = movingNode.x + alpha * px ;
					newY = movingNode.y + alpha * py ;
					newEnergy = computeModifiedEnergyHM(listOfNodesHM, listOfNodes, movingNode, newX, newY, nodeVisitedValue) ;
				}
				//

				// changing the position of movingNode WHITHOUT using the
				// modifyPosition method, as this position is temporary
				// and it's useless to update the total layout energy
				// at this point
				movingNode.x = newX ;
				movingNode.y = newY ;
				//

				// indicating that the algorithm has not yet converged
				movingNode.isAccessed = true ;
				//

				// for the next step
				return newEnergy ;
				//
			}//newtonOptimisationStep
			
			
			/**
			 * re-set the initialX 
			 * fields of each nodes to 0
			 * 
			 * @param listOfNode list of the nodes
			 */
			void cleanNewton( Node[] listOfNodes)
			{
				for ( Node n : listOfNodes)
				{
					n.initialX = 0 ;				
				}
			}//void cleanNewton
			
			
			/**
			 * re-set the initialX 
			 * fields of each nodes to 0
			 * 
			 * @param listOfNode list of the nodes
			 */
			void cleanNewton(ArrayList<Node> listOfNodes)
			{
				for ( Node n : listOfNodes)
				{
					n.initialX = 0 ;				
				}
			}//void cleanNewton

			
			// * * *

		};
	}//Algorithm createAlgorithm


	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field idealDistance$FIELD;
	public static final Type.Field yIdealDistance$FIELD;
	public static final Type.Field maxNbOfSteps$FIELD;
	public static final Type.Field fast$FIELD;
	public static final Type.Field startAgain$FIELD;

	public static class Type extends Layout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (GeneralPurposeLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Layout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Layout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Layout.Type.FIELD_COUNT + 5;

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
					((GeneralPurposeLayout) o).fast = value;
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((GeneralPurposeLayout) o).startAgain = value;
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
					return ((GeneralPurposeLayout) o).fast;
				case Type.SUPER_FIELD_COUNT + 4:
					return ((GeneralPurposeLayout) o).startAgain;
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					((GeneralPurposeLayout) o).maxNbOfSteps = value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					return ((GeneralPurposeLayout) o).maxNbOfSteps;
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((GeneralPurposeLayout) o).idealDistance = value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((GeneralPurposeLayout) o).yIdealDistance = value;
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
					return ((GeneralPurposeLayout) o).idealDistance;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((GeneralPurposeLayout) o).yIdealDistance;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new GeneralPurposeLayout ();
		}

	}

	@Override
	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (GeneralPurposeLayout.class);
		idealDistance$FIELD = Type._addManagedField ($TYPE, "idealDistance", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		yIdealDistance$FIELD = Type._addManagedField ($TYPE, "yIdealDistance", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		maxNbOfSteps$FIELD = Type._addManagedField ($TYPE, "maxNbOfSteps", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 2);
		fast$FIELD = Type._addManagedField ($TYPE, "fast", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 3);
		startAgain$FIELD = Type._addManagedField ($TYPE, "startAgain", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 4);
		$TYPE.validate ();
	}

//enh:end

}//class OctaveLayout

