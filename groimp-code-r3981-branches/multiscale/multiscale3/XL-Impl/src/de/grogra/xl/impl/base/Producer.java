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

package de.grogra.xl.impl.base;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executor;

import de.grogra.xl.impl.queues.Queue;
import de.grogra.xl.impl.queues.QueueCollection;
import de.grogra.xl.impl.queues.QueueDescriptor;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.EdgePattern;
import de.grogra.xl.query.NodeData;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.util.EHashMap;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

/**
 * This class is an abstract base implementation of <code>Producer</code>.
 * It uses subclasses of {@link GraphQueue} to represent the queues which 
 * store deferred actions of XL.
 * 
 * @author Ole Kniemeyer
 */
public abstract class Producer implements de.grogra.xl.query.Producer, Executor
{
	private final RuntimeModel model;
	private final Graph.QState match;
	private final Graph graph;

	private int depth;
	private int stdEdgeType;
	private Object prevNode;
	private boolean firstPart;

	private final IntList istack = new IntList ();
	private final ObjectList ostack = new ObjectList ();

	private final EHashMap<EdgeData> edgeMap = new EHashMap<EdgeData> ();
	private final EdgeData edgeKey = new EdgeData ();

	private final ObjectList<NodeData> restoreContextFlag = new ObjectList<NodeData> ();

	private QueueCollection queues;
	protected GraphQueue addNodeQueue;
	protected GraphQueue deleteNodeQueue;
	protected GraphQueue deleteEdgeQueue;

	private int arrow;

	private ObjectList firstDeepNodes = new ObjectList ();
	private Object firstNode;
	private Object lastNode;

	private boolean interpretiveRule;

	private int inConnEdges;
	private int outConnEdges;

	private boolean useOperators;
	
	//multiscale begin
	
	//list of first and last nodes at multiple scales
	ObjectList<FirstLastScale> firstLastMultiScale = new ObjectList<FirstLastScale>();
	//last modified FirstLastScale in list firstLastMultiScale
	FirstLastScale prevFirstLastScale; 
	
	//flag to indicate if current round of production is multiscalar
	boolean produceMultiScale = false;
	
	//tracing the last added edge
	private Object prevEdgeNodeSrc;
	private Object prevEdgeNodeTgt;
	private int prevEdgeBits;
	
	//flag to indicate clique specification is in progress
	private boolean produceClique = false;
	private ObjectList<ObjectList<Object> > cliques = new ObjectList<ObjectList<Object> >();
	private ObjectList<Object> cliqueNodes;
	
	//external edges shared by nodes in cliques
	private ObjectList<CliqueEdges> cliqueEdges = new ObjectList<CliqueEdges>();
	private class CliqueEdges
	{
		public Object sharedInSrc;
		public int sharedInBits;
		public ObjectList<Object> sharedOutTgts;
		public ObjectList<Integer> sharedOutBits;
		
		public CliqueEdges()
		{
			sharedInSrc = null;
			sharedInBits = -1;
			sharedOutTgts = new ObjectList<Object>();
			sharedOutBits = new ObjectList<Integer>();
		}
	}
	//multiscale end

	protected Producer (QueryState match)
	{
		this.match = (Graph.QState) match;
		this.graph = (Graph) match.getGraph ();
		this.model = graph.model;
	}

	protected Producer (RuntimeModel model)
	{
		this.match = null;
		this.graph = null;
		this.model = model;
	}

	/**
	 * Returns the match for which this producer constructs the
	 * replacement. The returned instance is the same as the one
	 * in the factory method {@link Graph#createProducer} which
	 * created this producer.
	 * 
	 * @return the match for which this producer constructs a replacement
	 */
	protected QueryState getQueryState ()
	{
		return match;
	}

	public de.grogra.xl.query.Graph producer$getGraph ()
	{
		return graph;
	}

	public boolean producer$beginExecution (int arrow)
	{
		this.arrow = arrow;
		prevNode = null;
		firstPart = true;
		edgeMap.clear ();
		firstNode = null;
		firstDeepNodes.clear ();
		lastNode = null;
		inConnEdges = -1;
		outConnEdges = -1;
		useOperators = true;
		interpretiveRule = (match.data.derivationMode & Graph.INTERPRETIVE_FLAG) != 0;
		queues = graph.getQueues ();
		addNodeQueue = queues.getQueue (GraphQueue.ADD_NODE_DESCRIPTOR);
		deleteNodeQueue = queues.getQueue (GraphQueue.DELETE_NODE_DESCRIPTOR);
		deleteEdgeQueue = queues.getQueue (GraphQueue.DELETE_EDGE_DESCRIPTOR);
		depth = 0;
		istack.clear ();
		ostack.clear ();
		restoreContextFlag.clear ();
		
		//multiscale begin
		firstLastMultiScale.clear();
		//prevFirstLastScale = null;
		if(graph.getTypeRoot()!=null)
			produceMultiScale = true;
		else
			produceMultiScale = false;
		//multiscale end
		
		return graph.applyMatch (match);
	}


	public void producer$endExecution (boolean applied)
	{
		if (!applied)
		{
			return;
		}
		if (depth > 0)
		{
			throw new IllegalStateException ();
		}
		match.visitMatch (this);
		GraphQueue aq = queues.getQueue (GraphQueue.ADD_EDGE_DESCRIPTOR);
		GraphQueue auq = queues.getQueue (GraphQueue.ADD_UNDIRECTED_EDGE_DESCRIPTOR);
		for (EdgeData e = edgeMap.getFirstEntry (); e != null; e = (EdgeData) e.listNext)
		{
			int add = e.add;
			int del = e.delete;
			int bits = e.bits;
			if (bits == 0)
			{
				bits = model.getEdgeBits (e.source, e.target);
			}
			int s = add & RuntimeModel.SPECIAL_MASK;
			if (s != 0)
			{
				if (s == (bits & RuntimeModel.SPECIAL_MASK))
				{
					add &= ~RuntimeModel.SPECIAL_MASK;
					del &= ~RuntimeModel.SPECIAL_MASK;
				}
				else if ((bits & RuntimeModel.SPECIAL_MASK) != 0)
				{
					del |= RuntimeModel.SPECIAL_MASK;
				}
			}
			else if ((del & RuntimeModel.SPECIAL_MASK) != 0)
			{
				del |= RuntimeModel.SPECIAL_MASK;
			}
			if ((del &= ~add | RuntimeModel.SPECIAL_MASK) != 0)
			{
				deleteEdgeQueue.deleteEdgeBits (e.source, e.target, del);
			}
			if ((add &= ~bits | RuntimeModel.SPECIAL_MASK) != 0)
			{
				aq.addEdgeBits (e.source, e.target, add);
			}
			if (e.undirectedAdd != 0)
			{
				auq.addUndirectedEdgeBits (e.source, e.target, e.undirectedAdd);
			}
		}
		edgeMap.clear ();
		GraphQueue dq = queues.getQueue (GraphQueue.DELETE_NODE_DESCRIPTOR);
		Object in = match.getInValue (), out = match.getOutValue ();
		if (interpretiveRule)
		{
			if ((firstNode != null) && (in != null))
			{
				GraphQueue eq = queues.getQueue (GraphQueue.CONNECT_DESCRIPTOR);
				eq.embedInterpretive (in, firstNode, lastNode);
			}
		}
		else
		{
			boolean inDeleted = false, outDeleted = false;
			for (NodeData d = match.getFirstNodeData (); d != null; d = (NodeData) d.listNext)
			{
				if (!d.context)
				{
					if (!model.isNode (d.node))
					{
						throw new IllegalArgumentException ();
					}
					dq.deleteNode (d.node);
					inDeleted |= d.node == in;
					outDeleted |= d.node == out;
					if ((match.data.derivationMode & Graph.EXCLUDE_DELETED_FLAG) != 0)
					{
						match.data.excludeFromMatch (d.node);
					}
				}
			}
			if (arrow == SIMPLE_ARROW)
			{
				//multiscale begin
				if(graph.getTypeRoot()!=null) //if type graph exist, perform embedding using multiscale query state info
				{
					producer$endExecutionMultiScale();
				}
				else	//else if no type graph exist, perform embedding using original single scale method
				{
				//multiscale end
					GraphQueue eq = queues.getQueue (GraphQueue.CONNECT_DESCRIPTOR);
					
					if (in != null)
					{
						for (int i = 0; i < firstDeepNodes.size; i++)
						{
							Object d = firstDeepNodes.get (i);
							if (in != d)
							{
								if (useOperators)
								{
									eq.connectIncoming (in, d, model.branchIn);
								}
								else
								{
									eq.copyIncoming (in, d, RuntimeModel.SUCCESSOR_EDGE | RuntimeModel.BRANCH_EDGE, 0, RuntimeModel.BRANCH_EDGE);
								}
							}
						}
					}
					if (firstNode == null)
					{
						if ((in != null) && (out != null) && (outConnEdges != 0))
						{
							eq.connectAdjacent (in, out, outConnEdges);
						}
					}
					else
					{
						if ((in != null) && (in != firstNode))
						{
							if (useOperators && (inConnEdges == -1))
							{
								eq.connectIncoming (in, firstNode, model.copyIn);
								if (!inDeleted)
								{
									deleteEdgeQueue.deleteCurrentEdges (in, inConnEdges, false);
								}
							}
							else if (inDeleted)
							{
								eq.copyIncoming (in, firstNode, inConnEdges);
							}
							else
							{
								eq.moveIncoming (in, firstNode, inConnEdges);
							}
						}
						if ((out != null) && (out != lastNode))
						{
							if (useOperators && (outConnEdges == -1))
							{
								eq.connectOutgoing (out, lastNode, model.copyOut);
								if (!outDeleted)
								{
									deleteEdgeQueue.deleteCurrentEdges (out, outConnEdges, true);
								}
							}
							else if (outDeleted)
							{
								eq.copyOutgoing (out, lastNode, outConnEdges);
							}
							else
							{
								eq.moveOutgoing (out, lastNode, outConnEdges);
							}
						}
					}
				//multiscale begin
				}
				//multiscale end
			}
			//multiscale begin - establish edges for a graph clique
			if(cliques.size()>0)
			{
				for(int i=0; i<cliques.size();++i)
				{
					ObjectList<Object> clique = cliques.get(i);
					
					//connect each node to every other node in the clique
					for(int j=0; j<clique.size(); ++j)
					{
						Object nodeCurr = clique.get(j);
						
						//establish all clique connections (succ and branch edges to one-another)
						for(int k=0; k<clique.size(); ++k)
						{
							Object nodeTgt = clique.get(k);
							if(!nodeCurr.equals(nodeTgt))
							{
								aq.addEdgeBits (nodeCurr, nodeTgt, RuntimeModel.SUCCESSOR_EDGE);
								aq.addEdgeBits (nodeCurr, nodeTgt, RuntimeModel.BRANCH_EDGE);
							}
						}
					}
					
					//get shared external edges of this clique
					CliqueEdges edgesShared = cliqueEdges.get(i);
					
					//incoming shared edges
					Object inNode = edgesShared.sharedInSrc;
					int inBits = edgesShared.sharedInBits;
					
					if(inNode!=null)
					{
						//check if inNode is in another clique
						int inCliqueIndex = getCliqueIndex(inNode);
						if(inCliqueIndex == -1) //if src node is not in any clique
						{
							for(int j=0; j<clique.size(); ++j) //connect src node to all nodes in clique
							{
								Object nodeCurr = clique.get(j);
								aq.addEdgeBits (inNode, nodeCurr, inBits);
							}
						}
						else if(inCliqueIndex >= 0) //if src node is in a clique
						{
							//connect all nodes in src node's clique to all nodes in curr clique
							ObjectList<Object> cliqueSrc = cliques.get(inCliqueIndex);
							for(int j=0; j<clique.size(); ++j)
							{
								Object nodeCurr = clique.get(j);
								for(int k=0; k<cliqueSrc.size(); ++k)
								{
									Object nodeSrc = cliqueSrc.get(k);
									aq.addEdgeBits (nodeSrc, nodeCurr, inBits);
								}
							}
						}
					}
					
					//outgoing shared edges
					for(int m=0; m<edgesShared.sharedOutTgts.size(); ++m)
					{
						Object outNode = edgesShared.sharedOutTgts.get(m);
						int outBits = edgesShared.sharedOutBits.get(m);
						
						if(outNode!=null)
						{
							//check if outNode is in another clique
							int outCliqueIndex = getCliqueIndex(outNode);
							if(outCliqueIndex == -1) //if src node is not in any clique
							{
								for(int j=0; j<clique.size(); ++j) //connect src node to all nodes in clique
								{
									Object nodeCurr = clique.get(j);
									aq.addEdgeBits (nodeCurr, outNode, outBits);
								}
							}
							else if(outCliqueIndex >= 0) //if out node is in a clique
							{
								//connect all nodes in node's clique to all nodes in tgt node's clique
								ObjectList<Object> cliqueTgt = cliques.get(outCliqueIndex);
								for(int j=0; j<clique.size(); ++j)
								{
									Object nodeCurr = clique.get(j);
									for(int k=0; k<cliqueTgt.size(); ++k)
									{
										Object nodeTgt = cliqueTgt.get(k);
										aq.addEdgeBits (nodeCurr, nodeTgt, outBits);
									}
								}
							}
						}
					}
				}
				
				//clear cliques in this production
				cliques.clear();
			}
			//multiscale end
		}
		for (int i = restoreContextFlag.size - 1; i >= 0; i--)
		{
			restoreContextFlag.get (i).context = false;
		}
	}
	
	/**
	 * Searches existing cliques for in specified node.
	 * @param node
	 * @return index of clique in which specified node resides. -1 if node is not in any clique.
	 */
	private int getCliqueIndex(Object node)
	{
		for(int i=0; i<cliques.size(); ++i)
		{
			ObjectList<Object> clique = cliques.get(i);
			for(int j=0; j<clique.size(); ++j)
			{
				Object cliqueNode = clique.get(j);
				if(node.equals(cliqueNode))
					return i;
			}
		}
		return -1;
	}
	
	private void producer$endExecutionMultiScale()
	{
		GraphQueue eq = queues.getQueue (GraphQueue.CONNECT_DESCRIPTOR);
		
		ObjectList<Object> firstNodesMS = match.getFirstNodes(); 	//first nodes queried multiscale
		ObjectList<Object> lastNodesMS = match.getLastNodes();		//last nodes queried multiscale
		
		boolean singleNodeQuery = false;
		if((firstNodesMS.size()==0)&&(match.getInValue()!=null))
			firstNodesMS.add(match.getInValue());singleNodeQuery=true;
		if((lastNodesMS.size()==0)&&(match.getOutValue()!=null))
			lastNodesMS.add(match.getOutValue());singleNodeQuery=true;
			
		if(singleNodeQuery)
			match.updateTrailingRefinements();
		
		
		HashMap<Object, ObjectList<Object> > trailingOut = match.getTrailingOutgoingRefinements(); // trailing refinement connections Outgoing
		HashMap<Object, ObjectList<Object> > trailingIn = match.getTrailingIncomingRefinements(); // trailing refinement connections Incoming
		
		//embedding first and first deep (in brackets) nodes
		if(firstNodesMS!=null)
		{
			//loop through all queried first nodes
			for(int i=0; i<firstNodesMS.size();++i)
			{
				Object lhsFirst = firstNodesMS.get(i);
				
				//find rhs at various scale in production graph
				for(int j=0; j<firstLastMultiScale.size();++j)
				{
					FirstLastScale rhs = firstLastMultiScale.get(j);
					Object rhsFirst = rhs.getFirstNode();
					
					//embed first node
					if(rhsFirst!=null)
					{
						if(graph.isSameScale(lhsFirst, rhsFirst)  && (lhsFirst != rhsFirst))
						{
							if (useOperators && (inConnEdges == -1))
							{
								eq.connectIncoming (lhsFirst, rhsFirst, model.copyInNoRefine);
	//							if (!inDeleted)
	//							{
	//								deleteEdgeQueue.deleteCurrentEdges (in, inConnEdges, false);
	//							}
							}
	//						else if (inDeleted)
	//						{
	//							eq.copyIncoming (in, firstNode, inConnEdges);
	//						}
							else
							{
								eq.moveIncoming (lhsFirst, rhsFirst, inConnEdges);
							}
							
							//embedding trailing refinements - outgoing
							ObjectList<Object> trailingOutFirstList = trailingOut.get(lhsFirst);
							if(trailingOutFirstList!=null)
							{
								GraphQueue aq = queues.getQueue (GraphQueue.ADD_EDGE_DESCRIPTOR);
								for(int k=0; k<trailingOutFirstList.size(); ++k)
								{
									aq.addEdgeBits(rhsFirst, trailingOutFirstList.get(k), model.REFINEMENT_EDGE);
								}
							}
							
							//embedding trailing refinements - incoming
							ObjectList<Object> trailingInFirstList = trailingIn.get(lhsFirst);
							ObjectList<Object> rhsNoEncoarseNodes = rhs.getNoEncoarseNodes();
							if((trailingInFirstList!=null)&&(rhsNoEncoarseNodes!=null))
							{
								GraphQueue aq = queues.getQueue (GraphQueue.ADD_EDGE_DESCRIPTOR);
								for(int k=0; k<rhsNoEncoarseNodes.size(); ++k)
								{
									Object rhsNoEncoarseObj = rhsNoEncoarseNodes.get(k);
									
									//last node on rhs without encoarsement takes the trailing from the last node on lhs
									if(rhsNoEncoarseObj == rhs.getLastNode()) 
										continue;
									
									//for the above node on the RHS without encoarsement, we establish refinement edges
									//from all the trailing incoming encoarsements to it
									for(int m=0; m<trailingInFirstList.size(); ++m)
									{
										aq.addEdgeBits(trailingInFirstList.get(m), rhsNoEncoarseObj, model.REFINEMENT_EDGE);
									}
								}
							}
						}
					}
					
					//embed deep first nodes
					ObjectList<Object> rhsFirstDeep = rhs.getFirstDeepNodes();
					for (int k = 0; k < rhsFirstDeep.size(); k++)
					{
						Object rhsFirstDeepNode = rhsFirstDeep.get(k);
						if((lhsFirst != rhsFirstDeepNode)&&(graph.isSameScale(lhsFirst, rhsFirstDeepNode)))
						{
							if (useOperators)
							{
								eq.connectIncoming (lhsFirst, rhsFirstDeepNode, model.branchIn);
							}
							else
							{
								eq.copyIncoming (lhsFirst, rhsFirstDeepNode, RuntimeModel.SUCCESSOR_EDGE | RuntimeModel.BRANCH_EDGE, 0, RuntimeModel.BRANCH_EDGE);
							}
						}
						
						//embedding trailing refinements - incoming
						ObjectList<Object> trailingInFirstList = trailingIn.get(lhsFirst);
						if(trailingInFirstList!=null)
						{
							if(trailingInFirstList.size()>0)
							{
								GraphQueue aq = queues.getQueue (GraphQueue.ADD_EDGE_DESCRIPTOR);
								for(int m=0; m<trailingInFirstList.size(); ++m)
								{
									Object trailingInFirstDeep = trailingInFirstList.get(m);
									graph.isSameScale(trailingInFirstDeep, rhsFirstDeepNode);
									aq.addEdgeBits(trailingInFirstDeep, rhsFirstDeepNode, model.BRANCH_EDGE);
									
									eq.copyIncoming (trailingInFirstDeep, rhsFirstDeepNode, RuntimeModel.REFINEMENT_EDGE, 0, RuntimeModel.REFINEMENT_EDGE);
								}
							}
						}
					}
				}
			}
		}
		
		//embedding last nodes
		if(lastNodesMS!=null)
		{
			//loop through all queried last nodes
			for(int i=0; i<lastNodesMS.size();++i)
			{
				Object lhsLast = lastNodesMS.get(i);
				
				//find rhs at same scale in production graph
				for(int j=0; j<firstLastMultiScale.size();++j)
				{
					FirstLastScale rhs = firstLastMultiScale.get(j);
					Object rhsLast = rhs.getLastNode();
					
					if(rhsLast!=null)
					{
						//embed first node
						if(graph.isSameScale(lhsLast, rhsLast)  && (lhsLast != rhsLast))
						{
							if (useOperators && (outConnEdges == -1))
							{
								eq.connectOutgoing (lhsLast, rhsLast, model.copyOutNoRefine);
	//							if (!outDeleted)
	//							{
	//								deleteEdgeQueue.deleteCurrentEdges (out, outConnEdges, true);
	//							}
							}
	//						else if (outDeleted)
	//						{
	//							eq.copyOutgoing (out, lastNode, outConnEdges);
	//						}
							else
							{
								eq.moveOutgoing (lhsLast, rhsLast, outConnEdges);
							}
							
							//embedding trailing refinements - outgoing
							ObjectList<Object> trailingOutLastList = trailingOut.get(lhsLast);
							if(trailingOutLastList!=null)
							{
								GraphQueue aq = queues.getQueue (GraphQueue.ADD_EDGE_DESCRIPTOR);
								for(int k=0; k<trailingOutLastList.size(); ++k)
								{
									aq.addEdgeBits(rhsLast, trailingOutLastList.get(k), model.REFINEMENT_EDGE);
								}
							}
							
							//embedding trailing refinements - incoming
							ObjectList<Object> trailingInLastList = trailingIn.get(lhsLast);
							ObjectList<Object> rhsNoEncoarseNodes = rhs.getNoEncoarseNodes();
							if((trailingInLastList!=null)&&(rhsNoEncoarseNodes!=null))
							{
								GraphQueue aq = queues.getQueue (GraphQueue.ADD_EDGE_DESCRIPTOR);
								
								Object rhsLastNode = rhs.getLastNode();
								if(rhsNoEncoarseNodes.contains(rhsLastNode)) //if last node on rhs has no encoarsement
								{
									for(int m=0; m<trailingInLastList.size(); ++m)
									{
										//last node on rhs gets trailing incoming refinement from lhs last node
										aq.addEdgeBits(trailingInLastList.get(m), rhsLastNode, model.REFINEMENT_EDGE);
									}
								}
							}
						}
					}
				}
			}
		}
		
		
		
		
	}

	//multiscale begin
	public Object producer$cliqueBegin ()
	{
		produceClique = true;
		cliqueNodes = new ObjectList<Object>();
		cliqueEdges.add(new CliqueEdges());
		return null;
	}
	
	public Object producer$cliqueEnd ()
	{
		produceClique = false;
		cliques.add(cliqueNodes);
		return null;
	}
	//multiscale end
		
	protected void pushImpl ()
	{
		//multiscale begin
		if(produceMultiScale)
		{
			depth++;
			pushImplMS();
		}
		else
		{
		//multiscale end
			depth++;
			istack.push (stdEdgeType);
			istack.push (firstPart ? 1 : 0);
			ostack.push (prevNode);
			stdEdgeType = EdgePattern.BRANCH_EDGE;
		//multiscale begin
		}
		//multiscale end
	}
	
	//multiscale begin
	private void pushImplMS()
	{
		for(int i=0; i<firstLastMultiScale.size();++i)
		{
			FirstLastScale fls = firstLastMultiScale.get(i);
			fls.incrementDepth();
			fls.pushIStack(fls.getStdEdgeType());
			fls.pushIStack(fls.getFirstPart()? 1: 0);
			fls.pushOStack(fls.getPrevNode());
			fls.setStdEdgeType(EdgePattern.BRANCH_EDGE);
		}
		ostack.push(prevFirstLastScale);
	}
	//multiscale end

	protected void popImpl ()
	{
		//multiscale begin
		if(produceMultiScale)
		{
			depth--;
			popImplMS();
		}
		else
		{
		//multiscale end
			if (--depth < 0)
			{
				throw new IllegalStateException ();
			}
			prevNode = ostack.pop ();
			firstPart = istack.pop () != 0;
			stdEdgeType = istack.pop ();
		//multiscale begin
		}
		//multiscale end
	}
	
	//multiscale begin
	private void popImplMS()
	{
		for(int i=firstLastMultiScale.size()-1; i>=0;--i)
		{
			FirstLastScale fls = firstLastMultiScale.get(i);
			int currDepth = fls.getDepth();
			if(currDepth-1 < 0)
			{
				firstLastMultiScale.remove(i);
			}
			else
			{
				fls.decrementDepth();
				
				Object prevObj = fls.popOStack();
				if(prevObj!=null)
					fls.setPrevNode(prevObj);
				else
					fls.setPrevNode(null);
				
				int prevI = fls.popIStack();
				if(prevI != -1)
					fls.setFirstPart(prevI!=0);
				else
					fls.setFirstPart(true);
				
				int prevE = fls.popIStack();
				if(prevI != -1)
					fls.setStdEdgeType(prevE);
				else
					fls.setStdEdgeType(EdgePattern.SUCCESSOR_EDGE);
			}
		}
		prevFirstLastScale = (FirstLastScale)ostack.pop();
	}
	//multiscale end

	protected void separateImpl ()
	{
		prevNode = null;
		firstPart = false;
	}

	protected void nodeUsed (Object object)
	{
		if (model != null)
		{
			if (!model.isNode (object))
			{
				throw new IllegalArgumentException (object + " is not a valid node");
			}
			if (match != null)
			{
				NodeData d = match.getNodeData (object);
				if ((d != null) && !d.context)
				{
					d.context = true;
					restoreContextFlag.add (d);
				}
			}
			if (addNodeQueue != null)
			{
				addNodeQueue.addNode (object);
			}
		}
	}

	protected void addNodeImpl (Object node, boolean addEdge)
	{
		//multiscale begin
		if(produceMultiScale)
		{
			addNodeImplMS(node, addEdge);
		}
		else
		{
		//multiscale end
			if (addEdge && (prevNode != null))
			{
				addEdgeImpl (prevNode, node, model.getStandardEdgeFor (stdEdgeType), EdgeDirection.FORWARD);
			}
			nodeUsed (node);
			if (firstPart)
			{
				if (prevNode == null)
				{
					if (depth == 0)
					{
						firstNode = node;
					}
					else
					{
						firstDeepNodes.add (node);
					}
				}
				if (depth == 0)
				{
					lastNode = node;
				}
			}
			prevNode = node;
			stdEdgeType = EdgePattern.SUCCESSOR_EDGE;
			//multiscale begin
			if(produceClique)
			{
				CliqueEdges edgesInCurrClique = cliqueEdges.get(cliqueEdges.size()-1); //get shared edges of current clique
				cliqueNodes.add(node);
				if(cliqueNodes.size()==1)//for the first node in the clique
				{
					if(prevEdgeNodeTgt.equals(node))//check if an edge was just added to the first node in clique
					{
						edgesInCurrClique.sharedInSrc = prevEdgeNodeSrc;
						edgesInCurrClique.sharedInBits = prevEdgeBits;
					}
				}
			}
			//multiscale end
		//multiscale begin
		}
		//multiscale end
	}
	
	//multiscale begin
	protected void addNodeImplMS(Object node, boolean addEdge)
	{
		FirstLastScale flScale = getFirstLastScale(node);
		if(flScale == null) //no node of this scale was previously added, create new FirstLastScale
		{
			flScale = new FirstLastScale(model,depth);
			firstLastMultiScale.add(flScale);
		}
		
		Object prevNode = flScale.getPrevNode();
		
		//add edge
		if (addEdge && (prevNode != null) && (prevNode!=node))
		{
			addEdgeImpl (prevNode, node, model.getStandardEdgeFor (flScale.getStdEdgeType()), EdgeDirection.FORWARD);
		}
		nodeUsed (node);
		
		//keep track of first and last node in this scale
		if (flScale.getFirstPart())
		{
			if (prevNode == null)
			{
				if (flScale.getDepth() == 0)
				{
					flScale.setFirstNode(node);
				}
				else
				{
					flScale.addFirstDeepNode(node);
				}
			}
			if (flScale.getDepth() == 0)
			{
				flScale.setLastNode(node);
			}
		}
		flScale.setPrevNode(node);
		flScale.setStdEdgeType(EdgePattern.SUCCESSOR_EDGE);
		
		//find the last added finest encoarsement node of this node.
		//Add refinement edge from that encoarse node to this node.
		Iterator<FirstLastScale> flMSIter = firstLastMultiScale.iterator();
		int minEncoarseDepthDiff = Integer.MAX_VALUE;
		ObjectList<Object> minEncoarseNodes = new ObjectList<Object>();
		while(flMSIter.hasNext())
		{
			FirstLastScale currFLS = flMSIter.next();
			Object currFLSPrevNode = currFLS.getPrevNode();
			if(currFLSPrevNode != null)
			{
				int depthDiff = graph.getMinimumEncoarseDepthDiff(currFLSPrevNode, node);
				if((depthDiff!=-1)&&(depthDiff!=0)&&(depthDiff<=minEncoarseDepthDiff))
				{
					if(depthDiff<minEncoarseDepthDiff)
						minEncoarseNodes.clear();
					minEncoarseDepthDiff = depthDiff;
					minEncoarseNodes.add(currFLSPrevNode);
				}
			}
		}
		if((minEncoarseNodes.size()>0)&&(addEdge))
		{
			for(int i=0; i<minEncoarseNodes.size(); ++i)
				addEdgeImpl (minEncoarseNodes.get(i), node, model.REFINEMENT_EDGE, EdgeDirection.FORWARD);	
		}
		else
		{
			flScale.pushNoEncoarse(node);//add this node to the list of nodes without encoarsement in this scale
		}
		
		prevFirstLastScale = flScale; //track the last modified FirstLastScale instance
	}
	//multiscale end

	protected Object getPreviousNode ()
	{
		//multiscale begin
		if(produceMultiScale)
		{
			return prevFirstLastScale.getPrevNode();
		}
		//multiscale end
		return prevNode;
	}

	protected final void addEdgeImpl (Object first, Object second, int bits, EdgeDirection direction)
	{
		while (true)
		{
			if (direction == EdgeDirection.BACKWARD)
			{
				Object t = first;
				first = second;
				second = t;
			}
			if (match == null)
			{
				model.addEdgeBits (first, second, bits);
			}
			else
			{
				EdgeData e = getEdgeDataForAdd (first, second);
				if (direction == EdgeDirection.UNDIRECTED)
				{
					e.add = 0;
					e.undirectedAdd = bits;
				}
				else
				{
					e.add = bits;
					e.undirectedAdd = 0;
				}
				edgeMap.put (e);
			}
			//multiscale begin
			prevEdgeNodeSrc = first;
			prevEdgeNodeTgt = second;
			prevEdgeBits = bits;
			
			if(cliques.size()>0)
			{
				for(int i=0; i<cliques.size(); ++i)
				{
					ObjectList<Object> cliqueCurr = cliques.get(i);
					if(cliqueCurr.size()>0)
					{
						Object nodeCurr = cliqueCurr.get(cliqueCurr.size()-1);
						if(nodeCurr.equals(first))
						{
							CliqueEdges edgesInCurrClique = cliqueEdges.get(i); //get shared edges of current clique
							edgesInCurrClique.sharedOutTgts.add(second);
							edgesInCurrClique.sharedOutBits.add(new Integer(bits));
						}
					}
				}
			}
			//multiscale end
			if (direction != EdgeDirection.BOTH)
			{
				return;
			}
			direction = EdgeDirection.BACKWARD;
		}
		
	}

	private EdgeData getEdgeDataForAdd (Object first, Object second)
	{
		if (!model.isNode (first))
		{
			throw new IllegalArgumentException (first + " is not a valid node");
		}
		if (!model.isNode (second))
		{
			throw new IllegalArgumentException (second + " is not a valid node");
		}
		EdgeData e = edgeMap.popEntryFromPool ();
		if (e == null)
		{
			e = new EdgeData ();
		}
		e.set (first, second);
		e.delete = 0;
		e.bits = 0;
		return e;
	}

	public void producer$visitEdge (EdgePattern pattern)
	{
		Object s = match.abound (0);
		Object t = match.abound (1);
		boolean both = pattern.needsBothDirections (); 
		int matchTerm = pattern.getMatchIndex ();
		while (true)
		{
			edgeKey.set (s, t);
			EdgeData e = edgeMap.get (edgeKey);
			int maskTerm = pattern.getPatternIndex ();
			int del = (maskTerm >= 0) ? match.ibound (maskTerm) : ((Number) pattern.getPattern ()).intValue ();
			if (e != null)
			{
				e.bits = match.ibound (matchTerm);
				e.delete |= del & e.bits;
			}
			else
			{
				deleteEdgeQueue.deleteEdgeBits (s, t, del & match.ibound (matchTerm));
			}
			if (both)
			{
				both = false;
				matchTerm++;
				Object x;
				x = s; s = t; t = x; 
			}
			else
			{
				return;
			}
		}
	}

	public void copyIncoming (Object s, Object t, int edges)
	{
		queues.getQueue (GraphQueue.CONNECT_DESCRIPTOR).copyIncoming (s, t, edges);
	}

	public void copyOutgoing (Object s, Object t, int edges)
	{
		queues.getQueue (GraphQueue.CONNECT_DESCRIPTOR).copyOutgoing (s, t, edges);
	}

	public void moveIncoming (Object s, Object t, int edges)
	{
		queues.getQueue (GraphQueue.CONNECT_DESCRIPTOR).moveIncoming (s, t, edges);
	}

	public void moveOutgoing (Object s, Object t, int edges)
	{
		queues.getQueue (GraphQueue.CONNECT_DESCRIPTOR).moveOutgoing (s, t, edges);
	}

	/**
	 * Adds an action to the connecting queue <code>q</code>
	 * ({@link GraphQueue#CONNECT_DESCRIPTOR}) which induces the invocation
	 * <code>c.connect(s, t, param, q)</code> on queue application.
	 * 
	 * @param s node of the match for the left hand side of the rule
	 * @param t replacing node of the right hand side of the rule
	 * @param param some parameter for the connector
	 * @param c a connector
	 */
	public <N,P> void connect (N s, N t, P param, Connector<N,P> c)
	{
		queues.getQueue (GraphQueue.CONNECT_DESCRIPTOR).connect (s, t, param, c);
	}

	public void execute (Runnable r)
	{
		queues.getQueue (GraphQueue.EXECUTE_DESCRIPTOR).execute (r);
	}

	public void setConnectionEdges (int edges)
	{
		setInConnectionEdges (edges);
		setOutConnectionEdges (edges);
	}

	public void setInConnectionEdges (int edges)
	{
		inConnEdges = edges;
	}

	public void setOutConnectionEdges (int edges)
	{
		outConnEdges = edges;
	}

	public void cut ()
	{
		setOutConnectionEdges (0);
	}

	public void useOperators (boolean value)
	{
		useOperators = value;
	}

	public void interpretive ()
	{
		interpretiveRule = true;
	}

	public void consume (Object node)
	{
		match.data.excludeFromMatch (node);
	}

	public QueueCollection getQueues ()
	{
		return queues;
	}

	public <Q extends Queue> Q getQueue (QueueDescriptor<Q> descr)
	{
		return queues.getQueue (descr);
	}

	//multiscale begin
	public FirstLastScale getFirstLastScale(int index)
	{
		if(index<0)
			return null;
		
		if(index > firstLastMultiScale.size()-1)
			return null;
		
		return firstLastMultiScale.get(index);
	}
	
	/**
	 * Returns an existing FirstLastScale for the scale of the input node
	 * @param node
	 * @return FirstLastScale that has scale of input node, null if no FirstLastScale at scale of input node
	 */
	public FirstLastScale getFirstLastScale(Object node)
	{
		Iterator<FirstLastScale> flScalesIter = firstLastMultiScale.iterator();
		while(flScalesIter.hasNext())
		{
			FirstLastScale flScale = flScalesIter.next();
			Object prevNode = flScale.getPrevNode();
			if(prevNode!=null)
			{
				if(graph.isSameScale(prevNode, node)) //if prev node in FirstLastScale same scale as node, return this FirstLastScale
					return flScale;
			}
			else
			{
				prevNode = flScale.getFirstNode();
				if(prevNode!=null)
				{
					if(graph.isSameScale(prevNode, node)) //if prev node in FirstLastScale same scale as node, return this FirstLastScale
						return flScale;
				}
				else
				{
					ObjectList<Object> deepNodes = flScale.getFirstDeepNodes();
					for(int i=0; i<deepNodes.size(); ++i)
					{
						prevNode = deepNodes.get(i);
						if(prevNode!=null)
						{
							if(graph.isSameScale(prevNode, node)) //if prev node in FirstLastScale same scale as node, return this FirstLastScale
								return flScale;
						}
					}
				}
			}
		}
		
		return null; //no FirstLastScale at the same scale as node found
	}
	
	
	
	//multiscale end
}
