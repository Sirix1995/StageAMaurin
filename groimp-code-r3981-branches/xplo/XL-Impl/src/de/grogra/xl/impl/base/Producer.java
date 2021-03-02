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
		firstPart = istack.pop () != 0;
		stdEdgeType = istack.pop ();
	}

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

	protected void addNodeImpl (Object object, boolean addEdge)
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
