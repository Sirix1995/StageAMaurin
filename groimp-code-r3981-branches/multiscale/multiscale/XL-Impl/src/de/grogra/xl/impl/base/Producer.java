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
	
	//yong 20 mar 2012 - for multiscale rules
	private ObjectList prevNodeMacro;
	private ObjectList prevNodeMicro;
	
	private ObjectList firstNodeMacro;
	private ObjectList firstNodeMicro;
	
	private ObjectList<ObjectList> firstDeepNodesMacro;
	private ObjectList<ObjectList> firstDeepNodesMicro;
	
	private ObjectList lastNodeMacro;
	private ObjectList lastNodeMicro;
	//yong end
	
	//yong 26 mar 2012
	//for macro state tracking
	//private HashMap<Object, ObjectList> macroRefinementReferences; //yong 30 mar 2012 - commented. using universal refine ref at QueryState
	//yong 26 mar 2012 - end

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

	protected Producer (QueryState match)
	{
		this.match = (Graph.QState) match;
		this.graph = (Graph) match.getGraph ();
		this.model = graph.model;
		
		//yong 21 mar 2012 - scaling
		initMultiScaleReferences();
		//yong 21 mar 2012 - end
	}

	protected Producer (RuntimeModel model)
	{
		this.match = null;
		this.graph = null;
		this.model = model;
		
		//yong 21 mar 2012 - scaling
		initMultiScaleReferences();
		//yong 21 mar 2012 - end
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
		
		//yong 20 mar 2012
		clearMultiScaleReferences();
		//yong end
		
		//yong 26 mar 2012 - update references for macro scale nodes
		//updateMacroRefinementReferences();
		//yong 26 mar 2012 - end
		
		return graph.applyMatch (match);
	}
	
	//yong 26 mar 2012
	/**
	 * Clear multiscale references in producer.
	 */
	private void initMultiScaleReferences()
	{
		prevNodeMacro = new ObjectList<Object>();
		prevNodeMicro = new ObjectList<Object>();
		firstNodeMacro = new ObjectList<Object>();
		firstNodeMicro = new ObjectList<Object>();
		lastNodeMacro = new ObjectList<Object>();
		lastNodeMicro = new ObjectList<Object>();
		firstDeepNodesMacro = new ObjectList<ObjectList>();
		firstDeepNodesMicro = new ObjectList<ObjectList>();
		
		//for tracking number of out going refinement edges from macro nodes
		//macroRefinementReferences = new HashMap<Object, ObjectList>(); //yong 30 mar 2012 - commented. using universal refine ref at QueryState
	}
	
	/**
	 * Clear multiscale references in producer
	 */
	private void clearMultiScaleReferences()
	{
		prevNodeMacro.clear();
		prevNodeMicro.clear();
		firstNodeMacro.clear();
		firstNodeMicro.clear();
		lastNodeMacro.clear();
		lastNodeMicro.clear();
		firstDeepNodesMacro.clear();
		firstDeepNodesMicro.clear();
		
		//yong 26 mar 2012
		// macroRefinementReferences not cleared for the lifetime of a producer. 
		// It maintains the number of outgoing refinement edges for each macro node for each match.
	}
	 
	/**
	 * Update refinement references for macro scale nodes.
	 * This occurs for each match during the lifetime of a producer.
	 */
	/* //yong 30 mar 2012 - commented. using universal refine ref at QueryState
	private void updateMacroRefinementReferences()
	{
		//for each node matched, get the node data and thereafter the macro scale nodes
		for (NodeData d = match.getFirstNodeData (); d != null; d = (NodeData) d.listNext)
		{
			ObjectList macroList = d.getMacroNodes();
			
			if(macroList!=null)
			{
				//for each related macro node to the matched node
				for(int i=0; i<macroList.size();++i)
				{
					Object macroObj = macroList.get(i);
					
					Object currNode = null;
					if(i==0)
						currNode = d.node;
					else
						currNode = macroList.get(i-1);
					
					//if reference for macro node already exist
					if(macroRefinementReferences.containsKey(macroObj))
					{
						ObjectList refineList = macroRefinementReferences.get(macroObj);
						
						//remove reference of current node
						for(int j=0; j<refineList.size(); ++j)
						{
							if(refineList.get(j).equals(currNode))
							{
								refineList.remove(j);
								break;
							}
						}
					}
					//else add reference for macro node into map
					else
					{
						ObjectList refineList = graph.getRefinements(macroObj);
						if(refineList!=null)
						{
							//remove reference of current node
							for(int j=0; j<refineList.size(); ++j)
							{
								if(refineList.get(j).equals(currNode))
								{
									refineList.remove(j);
									break;
								}
							}
							macroRefinementReferences.put(macroObj, refineList);
						}
					}
				}
			}
		}
	}
	
	private void addMacroRefinementReference(Object key, Object newReference)
	{
		if(macroRefinementReferences.containsKey(key))
		{
			ObjectList refineObjs = macroRefinementReferences.get(key);
			if(refineObjs!=null)
			{
				refineObjs.add(newReference);
				macroRefinementReferences.put(key, refineObjs);
			}
			else
			{
				refineObjs = new ObjectList();
				refineObjs.add(newReference);
				macroRefinementReferences.put(key, refineObjs);
			}
		}
		else
		{
			ObjectList refineObjs = new ObjectList();
			refineObjs.add(newReference);
			macroRefinementReferences.put(key, refineObjs);
		}
	}
	*/
	//yong 26 mar 2012 - end //yong 30 mar 2012 - commented. using universal refine ref at QueryState

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
					
					//yong 21 mar 2012 - delete matched nodes from other scales
					ObjectList macroList = d.getMacroNodes();
					ObjectList microList = d.getMicroNodes();
					if(macroList!=null)
					{
						for(int i=0; i<macroList.size();++i)
						{
							//yong 26 mar 2012 - use refinement references to determine if to delete
							/*
							if(macroRefinementReferences.containsKey(macroList.get(i)))
							{
								ObjectList refineList = macroRefinementReferences.get(macroList.get(i));
								if(refineList==null)
									dq.deleteNode(macroList.get(i));
								else
								{
									if(refineList.size()==0)
										dq.deleteNode(macroList.get(i));
								}
							}
							*/
							//yong 26 mar 2012 - end
							
							//yong 30 mar 2012 - use universal refinement references at QueryState instead
							HashMap<Object,ObjectList> refineRef = this.match.getMacroRefinementReferences(); 
							if(refineRef!=null)
							{
								if(refineRef.containsKey(macroList.get(i)))
								{
									ObjectList refineList = refineRef.get(macroList.get(i));
									if(refineList==null)
										dq.deleteNode(macroList.get(i));
									else
									{
										if(refineList.size()==0)
											dq.deleteNode(macroList.get(i));
									}
								}
								else
									dq.deleteNode(macroList.get(i));
							}
							else
								dq.deleteNode(macroList.get(i));
							//yong 30 mar 2012 - end
						}
					}
					if(microList!=null)
					{
						for(int i=0; i<microList.size();++i)
						{
							dq.deleteNode(microList.get(i));
						}
					}
					//yong 21 mar 2012 - end
					
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
					
					//yong 26 mar deep nodes embedding
					for(int i=0; i< this.firstDeepNodesMacro.size(); ++i)
					{
						ObjectList macroList = firstDeepNodesMacro.get(i);
						if(macroList!=null)
						{
							NodeData inData = match.getNodeData(in);
							ObjectList macroListIn = inData.getMacroNodes();
							if(macroListIn!=null)
							{
								for(int j=0; j< macroList.size(); ++j)
								{
									if(macroListIn.size()-1 >= j)
									{
										if(macroListIn.get(j) != macroList.get(j))
										if (useOperators)
										{
											eq.connectIncoming (macroListIn.get(j), macroList.get(j), model.branchIn);
										}
										else
										{
											eq.copyIncoming (macroListIn.get(j), macroList.get(j), RuntimeModel.SUCCESSOR_EDGE | RuntimeModel.BRANCH_EDGE, 0, RuntimeModel.BRANCH_EDGE);
										}
									}
								}
							}
						}
					}
					
					for(int i=0; i< this.firstDeepNodesMicro.size(); ++i)
					{
						ObjectList microList = firstDeepNodesMicro.get(i);
						if(microList!=null)
						{
							NodeData inData = match.getNodeData(in);
							ObjectList microListIn = inData.getMicroNodes();
							if(microListIn!=null)
							{
								for(int j=0; j< microList.size(); ++j)
								{
									if(microListIn.size()-1 >= j)
									{
										if(microListIn.get(j) != microList.get(j))
										if (useOperators)
										{
											eq.connectIncoming (microListIn.get(j), microList.get(j), model.branchIn);
										}
										else
										{
											eq.copyIncoming (microListIn.get(j), microList.get(j), RuntimeModel.SUCCESSOR_EDGE | RuntimeModel.BRANCH_EDGE, 0, RuntimeModel.BRANCH_EDGE);
										}
									}
								}
							}
						}
					}
					//yong 26  mar deep nodes embedding - end
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
							//yong 16 mar 2012 - scaling
							//eq.connectIncoming (in, firstNode, model.copyIn);
							eq.connectIncoming (in, firstNode, model.copyInNoRefine);
							//yong 16 mar 2012 - end
							
							//yong 21 mar 2012 - scaling - implicit embeddeding for other scales
							NodeData inData = match.getNodeData(in);
							if(inData!=null)
							{
								ObjectList macroList = inData.getMacroNodes();
								ObjectList microList = inData.getMicroNodes();
								IntList microIndices = inData.getMicroScaleIndices();
								
								//yong 2 apr 2012 - remove naive macro embedding
//								if(macroList!=null)
//								{
//									for(int i=0; i<macroList.size(); ++i)
//									{
//										if(firstNodeMacro.size()-1 >= i)
//										{
//											Object inMacro = macroList.get(i);
//											Object firstMacro = firstNodeMacro.get(i);
//											
//											if((inMacro!=null) && (firstMacro!=null))
//											{
//												if(inMacro!=firstMacro)
//													eq.connectIncoming (inMacro, firstMacro, model.copyInNoRefine);
//											}
//										}
//									}
//								}
								//yong 2 apr 2012 - remove naive macro embedding - end
								if((microList!=null)&&(microIndices!=null))
								{
									for(int i=0; i<firstNodeMicro.size(); ++i)
									{	
										if(microIndices.size()-1>=i)
										{
											int indexOfFirstMicro = 0;
											if(i>0)
											{
												indexOfFirstMicro = microIndices.get(i-1) + 1;
											}
											
											Object inMicro = microList.get(indexOfFirstMicro);
											Object firstMicro = firstNodeMicro.get(i);
											if((inMicro!=null) && (firstMicro!=null))
											{
												if(inMicro!=firstMicro)
													eq.connectIncoming (inMicro, firstMicro, model.copyInNoRefine);
											}
										}
										else
											break;
									}
								}
							}
							
							//yong 21 mar 2012 - end
							
							if (!inDeleted)
							{
								deleteEdgeQueue.deleteCurrentEdges (in, inConnEdges, false);
								
								//yong 27 mar
								if(inData!=null)
								{
									ObjectList macroList = inData.getMacroNodes();
									ObjectList microList = inData.getMicroNodes();
									if(macroList!=null)
									{
										for(int i=0; i<macroList.size();++i)
										{
											deleteEdgeQueue.deleteCurrentEdges (macroList.get(i), inConnEdges, false);
										}
									}
									if(microList!=null)
									{
										for(int i=0; i<microList.size();++i)
										{
											deleteEdgeQueue.deleteCurrentEdges (microList.get(i), inConnEdges, false);
										}
									}
								}
								//yong 27 mar - end
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
							//yong 16 mar 2012 - scaling
							//eq.connectOutgoing (out, lastNode, model.copyOut);
							eq.connectOutgoing (out, lastNode, model.copyOutNoRefine);
							//yong 16 mar 2012 - end
							
							//yong 26 mar 2012 - scaling - implicit embedding for other scales
							NodeData outData = match.getNodeData(out);
							if(outData!=null)
							{
								ObjectList macroList = outData.getMacroNodes();
								ObjectList microList = outData.getMicroNodes();
								IntList microIndices = outData.getMicroScaleIndices();
								//yong 2 apr 2012 - remove naive macro embedding
//								if(macroList!=null)
//								{
//									for(int i=0; i<macroList.size(); ++i)
//									{
//										if(lastNodeMacro.size()-1 >= i)
//										{
//											Object outMacro = macroList.get(i);
//											Object lastMacro = lastNodeMacro.get(i);
//											
//											if((outMacro!=null) && (lastMacro!=null))
//											{
//												if(outMacro!=lastMacro)
//													eq.connectOutgoing (outMacro, lastMacro, model.copyOutNoRefine);
//											}
//										}
//									}
//								}
								//yong 2 apr 2012 - remove naive macro embedding - end
								if((microList!=null)&&(microIndices!=null))
								{
									for(int i=0; i<lastNodeMicro.size(); ++i)
									{	
										if(microIndices.size()-1>=i)
										{
											int indexOfLastMicro = microIndices.get(i);
											Object outMicro = microList.get(indexOfLastMicro);
											Object lastMicro = lastNodeMicro.get(i);
											if((outMicro!=null) && (lastMicro!=null))
											{
												if(outMicro!=lastMicro)
													eq.connectOutgoing (outMicro, lastMicro, model.copyOutNoRefine);
											}
										}
										else
											break;
									}
								}
							}
							
							//yong 26 mar 2012 - end
							
							if (!outDeleted)
							{
								deleteEdgeQueue.deleteCurrentEdges (out, outConnEdges, true);
								
								//yong 27 mar
								if(outData!=null)
								{
									ObjectList macroList = outData.getMacroNodes();
									ObjectList microList = outData.getMicroNodes();
									if(macroList!=null)
									{
										for(int i=0; i<macroList.size();++i)
										{
											deleteEdgeQueue.deleteCurrentEdges (macroList.get(i), outConnEdges, true);
										}
									}
									if(microList!=null)
									{
										for(int i=0; i<microList.size();++i)
										{
											deleteEdgeQueue.deleteCurrentEdges (microList.get(i), outConnEdges, true);
										}
									}
								}
								//yong 27 mar - end
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
			}
		}
		for (int i = restoreContextFlag.size - 1; i >= 0; i--)
		{
			restoreContextFlag.get (i).context = false;
		}
	}

	protected void pushImpl ()
	{
		depth++;
		istack.push (stdEdgeType);
		istack.push (firstPart ? 1 : 0);
		//yong 20 mar 2012
		ObjectList stackedMicro = new ObjectList();
		stackedMicro.addAll(prevNodeMicro);
		ObjectList stackedMacro = new ObjectList();
		stackedMacro.addAll(prevNodeMacro);
		ostack.push(stackedMicro);
		ostack.push(stackedMacro);
		//yong end
		ostack.push (prevNode);
		stdEdgeType = EdgePattern.BRANCH_EDGE;
	}

	protected void popImpl ()
	{
		if (--depth < 0)
		{
			throw new IllegalStateException ();
		}
		prevNode = ostack.pop ();
		//yong 20 mar 2012
		prevNodeMacro.clear();
		prevNodeMacro.addAll((ObjectList)(ostack.pop()));
		prevNodeMicro.clear();
		prevNodeMicro.addAll((ObjectList)(ostack.pop()));
		//yong end
		
		firstPart = istack.pop () != 0;
		stdEdgeType = istack.pop ();
	}

	protected void separateImpl ()
	{
		//yong 20 mar 2012
		prevNodeMicro.clear();
		prevNodeMacro.clear();
		//yong end
		
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
	
	/**
	 * Get the next macro node available for embedding in sequential order of match
	 * @param currMacroNode
	 * @param scale
	 * @return
	 */
	private Object getNextMacroMatch(Object currMacroNode, int scale)
	{
		boolean currMacroNodeFound = false;
		for (NodeData d = match.getFirstNodeData (); d != null; d = (NodeData) d.listNext)
		{
			ObjectList macroNodes = d.getMacroNodes();
			if(macroNodes==null)
				continue;
			else
			{
				if(macroNodes.size()-1 < scale)
					continue;
				else
				{
					Object macroNode = macroNodes.get(scale);
					if(currMacroNodeFound)
					{
						HashMap<Object,ObjectList> refineRef = this.match.getMacroRefinementReferences();
						if(refineRef.containsKey(macroNode))
						{
							ObjectList refineNodes = refineRef.get(macroNode);
							if(refineNodes==null)
								continue;
							if(refineNodes.size()==0)
								continue;
							
							return macroNode;
							
						}
						else
						{
							continue;
						}
					}
					else
					{
						if(macroNode == currMacroNode)
							currMacroNodeFound=true;
					}
				}
			}
		}
		
		return null;
	}
	
	protected void nodeUsedOtherScales(Object object,boolean addEdge, int edgeType, EdgeDirection dir)
	{
		if (model != null)
		{
			if (addNodeQueue != null)
			{				
				//yong 20 mar 2012 - multiscale
				
				//if this is the first node on RHS of rule to be added, 
				//  load macro and micro scales of "InValue" from match
				//  in prevNodeMacro/Micro and firstNodeMacro/Micro
				if((prevNode==null)&&(match!=null))
				{
					Object in = match.getInValue();
					
					if(in!=null)
					{
						boolean isFirst = (firstNode==object);
						if(!isFirst)
						{
							if(firstDeepNodes.size()>0)
							{
								if(firstDeepNodes.get(firstDeepNodes.size()-1)==object)
									isFirst=true;
							}
						}
						if(isFirst)
						{
							if(model.isNode(in))
							{
								NodeData d = match.getNodeData (in);
								if(d!=null)
								{
									//not sure if need to do deep copy here
									ObjectList macroList = d.getMacroNodes();
									if(macroList!=null)
									{
										for(int i=0;i<macroList.size();++i)
										{
											prevNodeMacro.add(macroList.get(i));
											//firstNodeMacro.add(macroList.get(i));
										}
									}
									
									//add last node at each micro scale to prevNodeMicro
									ObjectList microList = d.getMicroNodes();
									IntList microListIndices = d.getMicroScaleIndices();
									if(microList!=null)
									{
										for(int i=0;i<microListIndices.size();++i)
										{
											prevNodeMicro.add(microList.get(microListIndices.get(i)));
											//firstNodeMicro.add(microList.get(i));
										}
									}
								}
							}
						}
					}
				}
				
				//yong 20 mar 2012 - add macro scale nodes
				//check if previous macro node can "absorb" and refine to the newly added node
				
				for(int i=0; i<prevNodeMacro.size();++i)
				{
					Object currScaleNode=null;
					if(i==0)
						currScaleNode=object;
					else
						currScaleNode=prevNodeMacro.get(i-1);
					
					//search for next macro node that can adopt current scale node
					Object nextMacro=prevNodeMacro.get(i);
					int skippedMacro=0;
					boolean findNextMacro=true;
					HashMap<Object,ObjectList> refineRef = match.getMacroRefinementReferences();
					if((!graph.canAbsorbL(prevNodeMacro.get(i), currScaleNode, refineRef.get(prevNodeMacro.get(i)))) && 
							(!model.testEdgeBits(edgeType, model.BRANCH_EDGE)))
					{
						while(findNextMacro)
						{
							nextMacro = getNextMacroMatch(nextMacro, i);
							skippedMacro++;
							//if ==null, there are no more old macro nodes available that can absorb current scale new nodes
							if(nextMacro==null)
							{
								findNextMacro=false;
								continue;
							}
							
							//if next macro found and it is able to adopt currScaleNode, stop search for macro
							if((graph.canAbsorbL(nextMacro, currScaleNode, refineRef.get(nextMacro))) && 
									(!model.testEdgeBits(edgeType, model.BRANCH_EDGE)))
								findNextMacro=false;
						}
						
					}
					else
					{
						nextMacro = prevNodeMacro.get(i);
					}
					
					
					//if((graph.canAbsorbL(prevNodeMacro.get(i), currScaleNode, macroRefinementReferences.get(prevNodeMacro.get(i)))) && (!model.testEdgeBits(edgeType, model.BRANCH_EDGE)))
					if(nextMacro!=null)
					{
						//refine curr node from next macro
						addEdgeImpl(nextMacro,currScaleNode,model.REFINEMENT_EDGE,EdgeDirection.FORWARD);
						
						//if skipped macro nodes because some macro nodes have zero references, join adjacent macro nodes
						if(skippedMacro>1)
						{
							//addEdgeImpl(nextMacro,prevNodeMacro.get(i),edgeType,dir);
							//yong 27 mar 2012 - add connecting edges for 'operator$space' for other scales
							if(addEdge && (prevNodeMacro.get(i)!=null))
							{
								addEdgeImpl (nextMacro, prevNodeMacro.get(i), model.getStandardEdgeFor (stdEdgeType), EdgeDirection.FORWARD);
							}
							//yong 27 mar 2012 - end
						}
						
						//set previous macro to the next one
						prevNodeMacro.set(i, nextMacro);
						lastNodeMacro.set(i, nextMacro);
						
						//yong 26 mar 2012 - add macro refinement references
						match.addMacroRefinementReference(nextMacro, currScaleNode);
						//yong 26 mar 2012 -end 
						break;
					}
					else
					{
						Object newMacroNode = graph.newMacro(currScaleNode);
						if ((addNodeQueue != null)&&(newMacroNode!=null))
						{
							addNodeQueue.addNode (newMacroNode);
							//connect old macro node to new macro node
							//addEdgeImpl(newMacroNode,prevNodeMacro.get(i),edgeType,dir);
							
							//yong 27 mar 2012 - add connecting edges for 'operator$space' for other scales
							if(addEdge && (prevNodeMacro.get(i)!=null))
							{
								addEdgeImpl (prevNodeMacro.get(i), newMacroNode, model.getStandardEdgeFor (stdEdgeType), EdgeDirection.FORWARD);
							}
							//yong 27 mar 2012 - end
							
							//set prev macro node to new macro node
							prevNodeMacro.set(i, newMacroNode);
							//set last macro node to new macro node
							lastNodeMacro.set(i, newMacroNode);
							
							//refine new macro node to new added node
							addEdgeImpl(newMacroNode,currScaleNode,model.REFINEMENT_EDGE,EdgeDirection.FORWARD);
							
							//yong 26 mar 2012 - add macro refinement references
							match.addMacroRefinementReference(newMacroNode, currScaleNode);
							//yong 26 mar 2012 -end 
							
							//set first node for macro scale - for implicit embedding later	
							
							//add "null" to required scales
							int missingNumMacro = 0;
							ObjectList firstDeepMacro=null;
							if((prevNode==null)&&(firstPart))
							{
								if(depth==0)
								{
									missingNumMacro = (i+1) - firstNodeMacro.size();
									if(missingNumMacro>0)
									{
										for(int j=0; j<missingNumMacro; ++j)
										{
											firstNodeMacro.add(null);
										}
									}
								}
								else
								{
									firstDeepMacro = firstDeepNodesMacro.get(firstDeepNodesMacro.size()-1);
									missingNumMacro = (i+1) - firstDeepMacro.size();
									if(missingNumMacro>0)
									{
										for(int j=0; j<missingNumMacro; ++j)
										{
											firstDeepMacro.add(null);
										}
									}
								}
								
								//if first node at required scale was not previously set, set to the newly added macro node
								//else newly added macro node is not first to be added at the particular scale, i.e. not a 'first node',
								//    not set into list
								if(depth==0)
								{
									if(firstNodeMacro.get(i)==null)
										firstNodeMacro.set(i, newMacroNode);
								}
								else
								{
									if(firstDeepMacro!=null)
									{
										if(firstDeepMacro.get(i)==null)
											firstDeepMacro.set(i, newMacroNode);
									}
								}		
							}
						}
					}
				}
				
				
				//yong 20 mar 2012 - add micro scale nodes
				//for all micro scale levels where implicit embedding is required
				ObjectList prevScaleAddedNodes = new ObjectList();
				for(int i=0; i<prevNodeMicro.size();++i)
				{
					//buffer for newly created micro scale nodes
					ObjectList newMicroNodes=null;
					
					if(i==0)
					{
						newMicroNodes = graph.newMicro(object);
						
						if(newMicroNodes!=null)
						{
							for(int p=0; p<newMicroNodes.size(); ++p)
							{
								if (addNodeQueue != null)
								{
									//add new micro node
									addNodeQueue.addNode (newMicroNodes.get(p));
									//refine macro node to newly added micro node
									addEdgeImpl(object,newMicroNodes.get(p),model.REFINEMENT_EDGE,EdgeDirection.FORWARD);
									//connect adjacent micro nodes
									if(p>=1)
										addEdgeImpl(newMicroNodes.get(p-1),newMicroNodes.get(p),model.SUCCESSOR_EDGE,EdgeDirection.FORWARD);
								}
							}
						}
					}
					else
					{
						//use newly added nodes at the previous (macro) scale to add new nodes at current (micro) scale
						for(int m=0; m<prevScaleAddedNodes.size(); ++m)
						{
							ObjectList tempMicroNodes = graph.newMicro(prevScaleAddedNodes.get(m));
							for(int p=0; p<tempMicroNodes.size(); ++p)
							{
								newMicroNodes.add(tempMicroNodes.get(p));
								
								if (addNodeQueue != null)
								{
									//add new micro node
									addNodeQueue.addNode (tempMicroNodes.get(p));
									//refine macro node to newly added micro node
									addEdgeImpl(prevScaleAddedNodes.get(m),tempMicroNodes.get(p),model.REFINEMENT_EDGE,EdgeDirection.FORWARD);
									//connect adjacent micro nodes
									if(newMicroNodes.size()>1)
										addEdgeImpl(newMicroNodes.get(p-1),newMicroNodes.get(p),model.SUCCESSOR_EDGE,EdgeDirection.FORWARD);
								}
							}
						}
					}
					
					if(newMicroNodes!=null)
					{
						//yong 21 mar 2012 - add connecting edges for 'operator$space' for other scales
						if(addEdge && (prevNodeMicro.get(i)!=null) && (newMicroNodes.size()>0))
						{
							addEdgeImpl (prevNodeMicro.get(i), newMicroNodes.get(0), model.getStandardEdgeFor (stdEdgeType), EdgeDirection.FORWARD);
						}
						//yong 21 mar 2012 - end
						
						if(newMicroNodes.size()>0)
						{
							prevNodeMicro.set(i, newMicroNodes.get(newMicroNodes.size()-1));
							lastNodeMicro.set(i, newMicroNodes.get(newMicroNodes.size()-1));
						}
						
						//clear buffer for newly added nodes
						prevScaleAddedNodes.clear();
						
						for(int k=0; k<newMicroNodes.size();++k)
						{
							//add to list for next iteration's usage
							prevScaleAddedNodes.add(newMicroNodes.get(k));
							
							//add
							if((firstPart)&&(k==0))
							{
								if (prevNode == null)
								{
									if (depth == 0)
									{
										firstNodeMicro.add(newMicroNodes.get(0));
									}
									else
									{
										firstDeepNodesMicro.get(firstDeepNodesMicro.size()-1).add(newMicroNodes.get(0));
									}
								}
							}
						}
					}
				} //end micro scale handling
				//yong 20 mar 2012 - end
			}
		}
	}
	
	protected void addNodeImpl(Object object,boolean addEdge)
	{
		if (addEdge && (prevNode != null))
		{
			addEdgeImpl (prevNode, object, model.getStandardEdgeFor (stdEdgeType), EdgeDirection.FORWARD);
		}
		nodeUsed (object);
		if (firstPart)
		{
			if (prevNode == null)
			{
				if (depth == 0)
				{
					firstNode = object;
				}
				else
				{
					firstDeepNodes.add (object);
				}
			}
			if (depth == 0)
			{
				lastNode = object;
			}
		}

		prevNode = object;
		stdEdgeType = EdgePattern.SUCCESSOR_EDGE;
	}

	protected void addNodeImpl (Object object, boolean addEdge,int edgeType,EdgeDirection dir)
	{
		if (addEdge && (prevNode != null))
		{
			addEdgeImpl (prevNode, object, model.getStandardEdgeFor (stdEdgeType), EdgeDirection.FORWARD);
		}
		nodeUsed (object);
		if (firstPart)
		{
			if (prevNode == null)
			{
				if (depth == 0)
				{
					firstNode = object;
				}
				else
				{
					firstDeepNodes.add (object);
					firstDeepNodesMacro.add(new ObjectList<Object>());
					firstDeepNodesMicro.add(new ObjectList<Object>());
				}
			}
			if (depth == 0)
			{
				lastNode = object;
			}
		}
		//yong 20 mar 2012 - scaling
		nodeUsedOtherScales(object,addEdge,edgeType,dir);
		//yong -end
		prevNode = object;
		stdEdgeType = EdgePattern.SUCCESSOR_EDGE;
	}

	protected Object getPreviousNode ()
	{
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

}
