
// NOTE: This file was generated automatically.

// ********************************************
// *               DO NOT EDIT!               *
// ********************************************


// This file was created from XL-Impl/src/de/grogra/xl/impl/base/GraphQueueImpl.vm

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

import de.grogra.xl.query.EdgeDirection;


import java.io.IOException;
import de.grogra.reflect.*;

import de.grogra.xl.util.EHashMap;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.util.IntList;
import de.grogra.xl.lang.ObjectConsumer;
import de.grogra.xl.query.NodeData;
import de.grogra.xl.query.RuntimeModelException;

import de.grogra.xl.impl.queues.*;








public
class GraphQueueImpl extends GraphQueue implements ObjectConsumer
{
	public static final int INCOMING = 1;
	public static final int OUTGOING = 2;

	static final int ADD_NODE = GraphQueue.MIN_UNUSED_ITEM;
	static final int ADD_EDGES = ADD_NODE + 1;
	static final int ADD_SIMPLE_EDGES = ADD_EDGES + 1;
	static final int ADD_UNDIRECTED_EDGES = ADD_SIMPLE_EDGES + 1;
	static final int DELETE_EDGES = ADD_UNDIRECTED_EDGES + 1;
	static final int DELETE_SIMPLE_EDGES = DELETE_EDGES + 1;
	static final int DELETE_NODE = DELETE_SIMPLE_EDGES + 1;

	static final int COPY_INCOMING = DELETE_NODE + 1;
	static final int MOVE_INCOMING = COPY_INCOMING + 1;
	static final int XCOPY_INCOMING = MOVE_INCOMING + 1;
	static final int COPY_OUTGOING = XCOPY_INCOMING + 1;
	static final int MOVE_OUTGOING = COPY_OUTGOING + 1;
	static final int XCOPY_OUTGOING = MOVE_OUTGOING + 1;

	static final int CONNECT_INCOMING = XCOPY_OUTGOING + 1;
	static final int CONNECT_OUTGOING = CONNECT_INCOMING + 1;

	static final int CONNECT = CONNECT_OUTGOING + 1;

	static final int CONNECT_ADJACENT = CONNECT + 1;

	static final int EMBED_INTERPRETIVE = CONNECT_ADJACENT + 1;

	static final int EXECUTE = EMBED_INTERPRETIVE + 1;


	final GraphImpl graph;


	final RuntimeModel model;

	public GraphQueueImpl (QueueDescriptor descr, QueueCollection qc
		)
	{
		super (descr, true, false);

// generated


		setItemSize (ADD_NODE, 0, 0, 0, 0, 0, 1);
		setItemSize (ADD_EDGES, 0, 1, 0, 0, 0, 2);
		setItemSize (ADD_SIMPLE_EDGES, 0, 0, 0, 0, 0, 2);
		setItemSize (ADD_UNDIRECTED_EDGES, 0, 1, 0, 0, 0, 2);
		setItemSize (DELETE_EDGES, 0, 1, 0, 0, 0, 2);
		setItemSize (DELETE_SIMPLE_EDGES, 0, 0, 0, 0, 0, 2);
		setItemSize (DELETE_NODE, 0, 0, 0, 0, 0, 1);
// generated

		setItemSize (COPY_INCOMING, 0, 1, 0, 0, 0, 2);
		setItemSize (MOVE_INCOMING, 0, 1, 0, 0, 0, 2);
		setItemSize (XCOPY_INCOMING, 0, 3, 0, 0, 0, 2);
		setItemSize (COPY_OUTGOING, 0, 1, 0, 0, 0, 2);
		setItemSize (MOVE_OUTGOING, 0, 1, 0, 0, 0, 2);
		setItemSize (XCOPY_OUTGOING, 0, 3, 0, 0, 0, 2);
		setItemSize (CONNECT_INCOMING, 0, 0, 0, 0, 0, 3);
		setItemSize (CONNECT_OUTGOING, 0, 0, 0, 0, 0, 3);
		setItemSize (CONNECT, 0, 0, 0, 0, 0, 4);
// generated

		setItemSize (CONNECT_ADJACENT, 0, 0, 0, 0, 0, 2);
// generated

		setItemSize (EMBED_INTERPRETIVE, 0, 0, 0, 0, 0, 3);
// generated

		setItemSize (EXECUTE, 0, 0, 0, 0, 0, 1);
// generated

		graph = (GraphImpl) qc.getGraph ();

		this.model = (RuntimeModel) qc.getModel ();
	}

// generated
	
	@Override
	protected RuntimeModel getModel ()
	{
		return model;
	}

	@Override
	public void addNode (Object node)
	{
		writeItem (ADD_NODE);
					writeObjectInQueue (node);
		}

	private static final int SIMPLE_EDGE_MASK = ((1 << (MAX_UNUSED_BIT + 1 - MIN_UNUSED_BIT)) - 1) << RuntimeModel.MIN_NORMAL_BIT_INDEX;
	private static final int SIMPLE_EDGE_SHIFT = MIN_UNUSED_BIT - RuntimeModel.MIN_NORMAL_BIT_INDEX;

// generated

	@Override
	public void addEdgeBits (Object source, Object target, int edges)
	{
								if ((edges & ~SIMPLE_EDGE_MASK) != 0)
		{
			writeItem (ADD_EDGES);
						writeObjectInQueue (source);
							writeObjectInQueue (target);
				writeInt (edges);
		}
		else
		{
			writeItem (ADD_SIMPLE_EDGES | (edges << SIMPLE_EDGE_SHIFT));
						writeObjectInQueue (source);
							writeObjectInQueue (target);
			}
	}

// generated

	@Override
	public void addUndirectedEdgeBits (Object source, Object target, int edges)
	{
								writeItem (ADD_UNDIRECTED_EDGES);
					writeObjectInQueue (source);
						writeObjectInQueue (target);
			writeInt (edges);
	}

// generated

	@Override
	public void deleteEdgeBits (Object source, Object target, int edges)
	{
		if ((edges & ~SIMPLE_EDGE_MASK) != 0)
		{
			writeItem (DELETE_EDGES);
						writeObjectInQueue (source);
							writeObjectInQueue (target);
				writeInt (edges);
		}
		else
		{
			writeItem (DELETE_SIMPLE_EDGES | (edges << SIMPLE_EDGE_SHIFT));
						writeObjectInQueue (source);
							writeObjectInQueue (target);
			}
	}

	@Override
	public void deleteCurrentEdges (Object node, int edges, boolean outgoing)
	{
					EdgeIterator i = model.createEdgeIterator (node, EdgeDirection.UNDIRECTED);
		while (i.hasEdge ())
		{
			Object s = i.source, t = i.target;
			int b = i.edgeBits;
			i.moveToNext ();
				if (			RuntimeModel.testEdgeBits (b, edges)
	)
			{
				b &= edges;
				if (s == node)
				{
					if (outgoing)
					{
						deleteEdgeBits (s, t, b);
					}
				}
				else
				{
					if (!outgoing)
					{
						deleteEdgeBits (s, t, b);
					}
				}
			}
		}
	}

// generated

	@Override
	public void deleteNode (Object node)
	{
		writeItem (DELETE_NODE);
					writeObjectInQueue (node);
		}


	@Override
	public void copyIncoming (Object source, Object target, int edges)
	{
		if (edges == 0)
		{
			return;
		}
					writeItem (COPY_INCOMING);
					writeObjectInQueue (source);
						writeObjectInQueue (target);
			writeInt (edges);
	}


	@Override
	public void moveIncoming (Object source, Object target, int edges)
	{
		if (edges == 0)
		{
			return;
		}
					writeItem (MOVE_INCOMING);
					writeObjectInQueue (source);
						writeObjectInQueue (target);
			writeInt (edges);
	}


	@Override
	public void copyIncoming (Object source, Object target, int edges, int copyMask, int addMask)
	{
		if ((edges == 0) || (((copyMask & edges) == 0) && (addMask == 0)))
		{
			return;
		}
					writeItem (XCOPY_INCOMING);
					writeObjectInQueue (source);
						writeObjectInQueue (target);
			writeInt (edges);
		writeInt (copyMask);
		writeInt (addMask);
	}


	@Override
	public void copyOutgoing (Object source, Object target, int edges)
	{
		if (edges == 0)
		{
			return;
		}
					writeItem (COPY_OUTGOING);
					writeObjectInQueue (source);
						writeObjectInQueue (target);
			writeInt (edges);
	}


	@Override
	public void moveOutgoing (Object source, Object target, int edges)
	{
		if (edges == 0)
		{
			return;
		}
					writeItem (MOVE_OUTGOING);
					writeObjectInQueue (source);
						writeObjectInQueue (target);
			writeInt (edges);
	}


	@Override
	public void copyOutgoing (Object source, Object target, int edges, int copyMask, int addMask)
	{
		if ((edges == 0) || (((copyMask & edges) == 0) && (addMask == 0)))
		{
			return;
		}
					writeItem (XCOPY_OUTGOING);
					writeObjectInQueue (source);
						writeObjectInQueue (target);
			writeInt (edges);
		writeInt (copyMask);
		writeInt (addMask);
	}


	@Override
	public void connectIncoming (Object source, Object target, Operator op)
	{
					writeItem (CONNECT_INCOMING);
					writeObjectInQueue (source);
						writeObjectInQueue (target);
			writeObjectInQueue (op);
	}


	@Override
	public void connectOutgoing (Object source, Object target, Operator op)
	{
					writeItem (CONNECT_OUTGOING);
					writeObjectInQueue (source);
						writeObjectInQueue (target);
			writeObjectInQueue (op);
	}


	@Override
	public void connect (Object from, Object to, Object param, Connector c)
	{
		c.getClass ();
					writeItem (CONNECT);
					writeObjectInQueue (from);
						writeObjectInQueue (to);
			writeObjectInQueue (param);
		writeObjectInQueue (c);
	}


	@Override
	public void connectAdjacent (Object start, Object end)
	{
		writeItem (CONNECT_ADJACENT);
					writeObjectInQueue (start);
						writeObjectInQueue (end);
		}


	@Override
	public void embedInterpretive (Object interpreted, Object start, Object end)
	{
								writeItem (EMBED_INTERPRETIVE);
					writeObjectInQueue (interpreted);
						writeObjectInQueue (start);
						writeObjectInQueue (end);
		}


	public void execute (Runnable task)
	{
		task.getClass ();
		writeItem (EXECUTE);
		writeObjectInQueue (task);
	}


	protected int maskConnectionEdges (int edges)
	{
		return edges;
	}

	private final TypeLoader loader = new ClassLoaderAdapter (getClass ().getClassLoader ());
	
	protected TypeLoader getTypeLoader ()
	{
		return loader;
	}


	EHashMap<NodeData> deletedNodes = new EHashMap<NodeData> (new NodeData[1], 32, 0.75f);
	NodeData key = new NodeData ();

	final ConnectionTransformation[] ctPool = new ConnectionTransformation[1];
	EHashMap<ConnectionTransformation> inConnTrans = new EHashMap<ConnectionTransformation> (ctPool, 32, 0.75f);
	EHashMap<ConnectionTransformation> outConnTrans = new EHashMap<ConnectionTransformation> (ctPool, 32, 0.75f);
	ConnectionTransformation ctKey = new ConnectionTransformation ();

	public void clearSegmentsToExclude (int[] segs)
	{
		if (!getDescriptor ().equals (DELETE_NODE_DESCRIPTOR))
		{
			return;
		}
		deletedNodes.clear ();
		clearSegmentsToExcludeImpl (segs);
	}


	@Override
	protected Processor createProcessor ()
	{
		return new Processor (getTypeLoader ())
		{
			@Override
			protected boolean process (int item)
				throws IOException, RuntimeModelException
			{
				boolean incoming;
				boolean outgoing;
				int edges;
				switch (item & ITEM_MASK)
				{
					case ADD_NODE:
						if (!execute)
						{
							return false;
						}
						graph.addNode (readObjectInQueue ());
						return true;
					case ADD_EDGES:
						if (!execute)
						{
							return false;
						}
						graph.addEdgeBits (readObjectInQueue (),
											readObjectInQueue (), readInt ());
						return true;
					case ADD_SIMPLE_EDGES:
						if (!execute)
						{
							return false;
						}
						graph.addEdgeBits (readObjectInQueue (),
											readObjectInQueue (),
											(item >>> SIMPLE_EDGE_SHIFT) & SIMPLE_EDGE_MASK);
						return true;
					case ADD_UNDIRECTED_EDGES:
					{
						if (!execute)
						{
							return false;
						}
									Object source = readObjectInQueue ();
										Object target = readObjectInQueue ();
							int add = readInt ();
						int bits = model.getEdgeBits (source, target);
						if ((bits == 0) && ((bits = model.getEdgeBits (target, source)) != 0))
						{
							Object t = source; source = target; target = t;
						}
						int s = add & RuntimeModel.SPECIAL_MASK;
						int del = 0;
						if (s != 0)
						{
							if (s == (bits & RuntimeModel.SPECIAL_MASK))
							{
								add &= ~RuntimeModel.SPECIAL_MASK;
							}
							else if ((bits & RuntimeModel.SPECIAL_MASK) != 0)
							{
								del = RuntimeModel.SPECIAL_MASK;
							}
						}
						if (del != 0)
						{
										graph.removeEdgeBits (source, target, del);
							}
						if ((add &= ~bits | RuntimeModel.SPECIAL_MASK) != 0)
						{
										graph.addEdgeBits (source, target, add);
							}
						return true;
					}
					case DELETE_EDGES:
						if (!execute)
						{
							return false;
						}
						graph.removeEdgeBits (readObjectInQueue (),
											   readObjectInQueue (), readInt ());
						return true;
					case DELETE_SIMPLE_EDGES:
						if (!execute)
						{
							return false;
						}
						graph.removeEdgeBits (readObjectInQueue (),
											   readObjectInQueue (),
											   (item >>> SIMPLE_EDGE_SHIFT) & SIMPLE_EDGE_MASK);
						return true;
					case DELETE_NODE:
						Object del;
						del = readObjectInQueue ();
						if (execute)
						{
							graph.removeNode (del);
						}
						else if (checkApplicability)
						{
							key.setNode (del);
							if (deletedNodes.get (key) != null)
							{
								clearSegment = true;
							}
						}
						else if (markApplicability)
						{
							NodeData d = deletedNodes.popEntryFromPool ();
							if (d == null)
							{
								d = new NodeData ();
							}
							d.setNode (del);
							deletedNodes.getOrPut (d);
						}
						return true;
					case COPY_INCOMING:
					case MOVE_INCOMING:
					case XCOPY_INCOMING:
					case COPY_OUTGOING:
					case MOVE_OUTGOING:
					case XCOPY_OUTGOING:
					case CONNECT:
					{
						if (!execute)
						{
							return false;
						}
									Object source = readObjectInQueue ();
										Object target = readObjectInQueue ();
							int copyMask;
						int addMask;
						boolean move = false;
						switch (item & ITEM_MASK)
						{
							case MOVE_INCOMING:
								move = true;
								// no break
							case COPY_INCOMING:
								incoming = true;
								outgoing = false;
								edges = readInt ();
								copyMask = -1;
								addMask = 0;
								break;
							case XCOPY_INCOMING:
								incoming = true;
								outgoing = false;
								edges = readInt ();
								copyMask = readInt ();
								addMask = readInt ();
								break;
							case MOVE_OUTGOING:
								move = true;
								// no break
							case COPY_OUTGOING:
								incoming = false;
								outgoing = true;
								edges = readInt ();
								copyMask = -1;
								addMask = 0;
								break;
							case XCOPY_OUTGOING:
								incoming = false;
								outgoing = true;
								edges = readInt ();
								copyMask = readInt ();
								addMask = readInt ();
								break;
							case CONNECT:
								Object param = readObjectInQueue ();
								((Connector) readObjectInQueue ())
									.connect (source, target, param, GraphQueueImpl.this);
								return true;
							default:
								throw new AssertionError ();
						}
						edges = maskConnectionEdges (edges);
									EdgeIterator i = model.createEdgeIterator (source, EdgeDirection.UNDIRECTED);
		while (i.hasEdge ())
		{
			Object s = i.source, t = i.target;
			int b = i.edgeBits;
			i.moveToNext ();
								if (			RuntimeModel.testEdgeBits (b, edges)
	)
							{
								b &= edges & copyMask;
								int add = b | addMask;
								if (s == source)
								{
									if (outgoing && (t != target))
									{
										if (move && (b != 0))
										{
														graph.removeEdgeBits (source, t, b);
											}
													graph.addEdgeBits (target, t, add);
										}
								}
								else if (incoming && (s != target))
								{
									if (move && (b != 0))
									{
													graph.removeEdgeBits (s, source, b);
										}
												graph.addEdgeBits (s, target, add);
									}
							}
						}
						return true;
					}
					case CONNECT_ADJACENT:
					{
						if (!execute)
						{
							return false;
						}
									Object start = readObjectInQueue ();
										Object end = readObjectInQueue ();
							Object pred = (Object) graph.getPredecessor (start);
						if (pred == null)
						{
							return false;
						}
									EdgeIterator i = model.createEdgeIterator (end, EdgeDirection.UNDIRECTED);
		while (i.hasEdge ())
		{
			Object s = i.source, t = i.target;
			int b = i.edgeBits;
			i.moveToNext ();
								if ((t != end)) // && TEST("succEdges"))
							{
											graph.removeEdgeBits (end, t, b);
									if (pred != t)
								{
												graph.addEdgeBits (pred, t, b);
									}
							}
						}
						return true;
					}
					case CONNECT_INCOMING:
					case CONNECT_OUTGOING:
					{
						if (!execute)
						{
							return false;
						}
									Object source = readObjectInQueue ();
										Object target = readObjectInQueue ();
							ConnectionTransformation t = inConnTrans.popEntryFromPool ();
						if (t == null)
						{
							t = new ConnectionTransformation ();
						}
						t.setSource (source);
						t.target = target;
						t.operator = (Operator) readObjectInQueue ();
						(((item & ITEM_MASK) == CONNECT_INCOMING) ? inConnTrans : outConnTrans).add (t);
						return false;
					}
					case EMBED_INTERPRETIVE:
					{
						if (!execute)
						{
							return false;
						}
									Object interpreted = readObjectInQueue ();
										Object start = readObjectInQueue ();
										Object end = readObjectInQueue ();
										EdgeIterator i = model.createEdgeIterator (interpreted, EdgeDirection.UNDIRECTED);
		while (i.hasEdge ())
		{
			Object s = i.source, t = i.target;
			int b = i.edgeBits;
			i.moveToNext ();
								b &= RuntimeModel.SUCCESSOR_EDGE | RuntimeModel.BRANCH_EDGE;
							if ((b != 0) && (s != interpreted))
							{
											graph.removeEdgeBits (s, interpreted, b);
												graph.addEdgeBits (s, start, b | RuntimeModel.CONTAINMENT_EDGE);
									break;
							}
						}
									graph.addEdgeBits (end, interpreted, RuntimeModel.SUCCESSOR_EDGE | RuntimeModel.CONTAINMENT_END_EDGE);
							Object mark = graph.createInterpretiveMark ();
									graph.addEdgeBits (start, mark, RuntimeModel.MARK_EDGE);
										graph.addEdgeBits (mark, end, RuntimeModel.MARK_EDGE);
							return true;
					}
					case EXECUTE:
					{
						if (!execute)
						{
							return false;
						}
						((Runnable) readObjectInQueue ()).run ();
						return true;
					}
					default:
						return super.process (item);
				}
			}
		};
	}


	private ConnectionTransformation curConnTrans;
	private boolean curConnTransOut;
	private EHashMap<ConnectionTransformation> otherConnTransMap;
	private ObjectList connNodes = new ObjectList ();
	private IntList connEdges = new IntList ();

	public void consume (Object v)
	{
		NodeEdgePair p = (NodeEdgePair) v;
		ctKey.setSource (p.node);
		boolean found = false;
		for (ConnectionTransformation t = otherConnTransMap.get (ctKey); t != null; t = (ConnectionTransformation) t.next ())
		{
			int r = t.operator.match (curConnTrans.source, curConnTrans.operator, p);
			if (r != 0)
			{
				found = true;
				if (curConnTransOut && (r != Operator.ONLY_CT_EDGES_MATCH))
				{
					connNodes.push (curConnTrans.target, t.target);
					connEdges.push (r);
				}
			}
		}
		if (!found)
		{
			if (curConnTransOut)
			{
				connNodes.push (curConnTrans.target, p.node);
			}
			else
			{
				connNodes.push (p.node, curConnTrans.target);
			}
			connEdges.push (p.edgeBits);
		}
	}

	
	private final NodeEdgePair nodeEdgePair = new NodeEdgePair ();

	@Override
	public boolean process (int[] segs) throws RuntimeModelException
	{
		inConnTrans.clear ();
		outConnTrans.clear ();
		connNodes.clear ();
		connEdges.clear ();
		boolean modified = super.process (segs);
		otherConnTransMap = inConnTrans;
		curConnTransOut = true;
		for (ConnectionTransformation t = outConnTrans.getFirstEntry (); t != null; t = (ConnectionTransformation) t.listNext)
		{
			curConnTrans = t;
			nodeEdgePair.node = t.source;
			t.operator.evaluate (this, nodeEdgePair);
		}
		otherConnTransMap = outConnTrans;
		curConnTransOut = false;
		for (ConnectionTransformation t = inConnTrans.getFirstEntry (); t != null; t = (ConnectionTransformation) t.listNext)
		{
			curConnTrans = t;
			nodeEdgePair.node = t.source;
			t.operator.evaluate (this, nodeEdgePair);
		}
		if (!connNodes.isEmpty ())
		{
			modified = true;
			while (!connNodes.isEmpty ())
			{
				Object t = (Object) connNodes.pop ();
							graph.addEdgeBits (((Object) connNodes.pop ()), t, connEdges.pop ());
				}
		}
		inConnTrans.clear ();
		outConnTrans.clear ();
		connNodes.clear ();
		connEdges.clear ();
		return modified;
	}

}

