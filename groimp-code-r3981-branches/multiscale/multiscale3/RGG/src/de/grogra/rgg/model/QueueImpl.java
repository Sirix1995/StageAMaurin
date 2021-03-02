
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

package de.grogra.rgg.model;

import de.grogra.graph.impl.*;
import de.grogra.persistence.*;
import de.grogra.xl.impl.base.*;


import java.io.IOException;
import de.grogra.reflect.*;

import de.grogra.xl.util.EHashMap;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.util.IntList;
import de.grogra.xl.lang.ObjectConsumer;
import de.grogra.xl.query.NodeData;
import de.grogra.xl.query.RuntimeModelException;

import de.grogra.xl.impl.queues.*;








final
class QueueImpl extends GraphQueue implements ObjectConsumer
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


	final GraphManager manager;
	final RGGGraph graph;
	final PropertyQueue makePersistentQueue;


	final RuntimeModel model;

	public QueueImpl (QueueDescriptor descr, QueueCollection qc
		, PropertyQueue mpq
		)
	{
		super (descr, true, false);

// generated



// generated

		setItemSize (ADD_NODE, 0, 0, 1, 0, 0, 0);
		setItemSize (ADD_EDGES, 0, 1, 2, 0, 0, 0);
		setItemSize (ADD_SIMPLE_EDGES, 0, 0, 2, 0, 0, 0);
		setItemSize (ADD_UNDIRECTED_EDGES, 0, 1, 2, 0, 0, 0);
		setItemSize (DELETE_EDGES, 0, 1, 2, 0, 0, 0);
		setItemSize (DELETE_SIMPLE_EDGES, 0, 0, 2, 0, 0, 0);
		setItemSize (DELETE_NODE, 0, 0, 1, 0, 0, 0);
// generated

		setItemSize (COPY_INCOMING, 0, 1, 2, 0, 0, 0);
		setItemSize (MOVE_INCOMING, 0, 1, 2, 0, 0, 0);
		setItemSize (XCOPY_INCOMING, 0, 3, 2, 0, 0, 0);
		setItemSize (COPY_OUTGOING, 0, 1, 2, 0, 0, 0);
		setItemSize (MOVE_OUTGOING, 0, 1, 2, 0, 0, 0);
		setItemSize (XCOPY_OUTGOING, 0, 3, 2, 0, 0, 0);
		setItemSize (CONNECT_INCOMING, 0, 0, 2, 0, 0, 1);
		setItemSize (CONNECT_OUTGOING, 0, 0, 2, 0, 0, 1);
		setItemSize (CONNECT, 0, 0, 2, 0, 0, 2);
// generated

		setItemSize (CONNECT_ADJACENT, 0, 1, 2, 0, 0, 0);
// generated

		setItemSize (EMBED_INTERPRETIVE, 0, 0, 3, 0, 0, 0);
// generated

		setItemSize (EXECUTE, 0, 0, 0, 0, 0, 1);
// generated

		this.graph = (RGGGraph) qc.getGraph ();
		this.manager = graph.manager;
		this.makePersistentQueue = mpq;
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
	}

	private static final int SIMPLE_EDGE_MASK = ((1 << (MAX_UNUSED_BIT + 1 - MIN_UNUSED_BIT)) - 1) << RuntimeModel.MIN_NORMAL_BIT_INDEX;
	private static final int SIMPLE_EDGE_SHIFT = MIN_UNUSED_BIT - RuntimeModel.MIN_NORMAL_BIT_INDEX;

// generated

	@Override
	public void addEdgeBits (Object source, Object target, int edges)
	{
					{
			Node node = (Node) source;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
						{
			Node node = (Node) target;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
			if ((edges & ~SIMPLE_EDGE_MASK) != 0)
		{
			writeItem (ADD_EDGES);
						writeLong (((Node) source).getId ());
							writeLong (((Node) target).getId ());
				writeInt (edges);
		}
		else
		{
			writeItem (ADD_SIMPLE_EDGES | (edges << SIMPLE_EDGE_SHIFT));
						writeLong (((Node) source).getId ());
							writeLong (((Node) target).getId ());
			}
	}

// generated

	@Override
	public void addUndirectedEdgeBits (Object source, Object target, int edges)
	{
					{
			Node node = (Node) source;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
						{
			Node node = (Node) target;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
			writeItem (ADD_UNDIRECTED_EDGES);
					writeLong (((Node) source).getId ());
						writeLong (((Node) target).getId ());
			writeInt (edges);
	}

// generated

	@Override
	public void deleteEdgeBits (Object source, Object target, int edges)
	{
		if ((edges & ~SIMPLE_EDGE_MASK) != 0)
		{
			writeItem (DELETE_EDGES);
						writeLong (((Node) source).getId ());
							writeLong (((Node) target).getId ());
				writeInt (edges);
		}
		else
		{
			writeItem (DELETE_SIMPLE_EDGES | (edges << SIMPLE_EDGE_SHIFT));
						writeLong (((Node) source).getId ());
							writeLong (((Node) target).getId ());
			}
	}

	@Override
	public void deleteCurrentEdges (Object node, int edges, boolean outgoing)
	{
					Node _n = (Node) node;
		Edge f;
		for (Edge e = _n.getFirstEdge (); e != null; e = f)
		{
			Node s = e.getSource (), t = e.getTarget ();
			int b = e.getEdgeBits ();
			f = e.getNext (_n);
				if (			e.testEdgeBits (edges)
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
					writeLong (((Node) node).getId ());
		}


	@Override
	public void copyIncoming (Object source, Object target, int edges)
	{
		if (edges == 0)
		{
			return;
		}
					{
			Node node = (Node) target;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
			writeItem (COPY_INCOMING);
					writeLong (((Node) source).getId ());
						writeLong (((Node) target).getId ());
			writeInt (edges);
	}


	@Override
	public void moveIncoming (Object source, Object target, int edges)
	{
		if (edges == 0)
		{
			return;
		}
					{
			Node node = (Node) target;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
			writeItem (MOVE_INCOMING);
					writeLong (((Node) source).getId ());
						writeLong (((Node) target).getId ());
			writeInt (edges);
	}


	@Override
	public void copyIncoming (Object source, Object target, int edges, int copyMask, int addMask)
	{
		if ((edges == 0) || (((copyMask & edges) == 0) && (addMask == 0)))
		{
			return;
		}
					{
			Node node = (Node) target;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
			writeItem (XCOPY_INCOMING);
					writeLong (((Node) source).getId ());
						writeLong (((Node) target).getId ());
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
					{
			Node node = (Node) target;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
			writeItem (COPY_OUTGOING);
					writeLong (((Node) source).getId ());
						writeLong (((Node) target).getId ());
			writeInt (edges);
	}


	@Override
	public void moveOutgoing (Object source, Object target, int edges)
	{
		if (edges == 0)
		{
			return;
		}
					{
			Node node = (Node) target;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
			writeItem (MOVE_OUTGOING);
					writeLong (((Node) source).getId ());
						writeLong (((Node) target).getId ());
			writeInt (edges);
	}


	@Override
	public void copyOutgoing (Object source, Object target, int edges, int copyMask, int addMask)
	{
		if ((edges == 0) || (((copyMask & edges) == 0) && (addMask == 0)))
		{
			return;
		}
					{
			Node node = (Node) target;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
			writeItem (XCOPY_OUTGOING);
					writeLong (((Node) source).getId ());
						writeLong (((Node) target).getId ());
			writeInt (edges);
		writeInt (copyMask);
		writeInt (addMask);
	}


	@Override
	public void connectIncoming (Object source, Object target, Operator op)
	{
					{
			Node node = (Node) target;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
			writeItem (CONNECT_INCOMING);
					writeLong (((Node) source).getId ());
						writeLong (((Node) target).getId ());
			writeObjectInQueue (op);
	}


	@Override
	public void connectOutgoing (Object source, Object target, Operator op)
	{
					{
			Node node = (Node) target;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
			writeItem (CONNECT_OUTGOING);
					writeLong (((Node) source).getId ());
						writeLong (((Node) target).getId ());
			writeObjectInQueue (op);
	}


	@Override
	public void connect (Object from, Object to, Object param, Connector c)
	{
		c.getClass ();
					{
			Node node = (Node) to;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
			writeItem (CONNECT);
					writeLong (((Node) from).getId ());
						writeLong (((Node) to).getId ());
			writeObjectInQueue (param);
		writeObjectInQueue (c);
	}


	@Override
	public void connectAdjacent (Object start, Object end, int edges)
	{
		if (edges == 0)
		{
			return;
		}
		writeItem (CONNECT_ADJACENT);
					writeLong (((Node) start).getId ());
						writeLong (((Node) end).getId ());
			writeInt (edges);
	}


	@Override
	public void embedInterpretive (Object interpreted, Object start, Object end)
	{
					{
			Node node = (Node) start;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
						{
			Node node = (Node) end;
			if (node.getId () < 0)
			{
				makePersistentQueue.makePersistent (node);
			}
		}
			writeItem (EMBED_INTERPRETIVE);
					writeLong (((Node) interpreted).getId ());
						writeLong (((Node) start).getId ());
						writeLong (((Node) end).getId ());
		}


	public void execute (Runnable task)
	{
		task.getClass ();
		writeItem (EXECUTE);
		writeObjectInQueue (task);
	}


	
	protected TypeLoader getTypeLoader ()
	{
		return manager.getBindings ().getTypeLoader ();
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

	Transaction transaction;

	@Override
	protected Processor createProcessor ()
	{
		return new Processor (getTypeLoader ())
		{
			@Override
			protected boolean process (int item)
				throws IOException, RuntimeModelException
			{
				switch (item & ITEM_MASK)
				{
					case ADD_NODE:
						if (!execute)
						{
							return false;
						}
						throw new AssertionError ();
					case ADD_EDGES:
						if (!execute)
						{
							return false;
						}
						manager.getObject (readLong ())
							.addEdgeBitsTo (manager.getObject (readLong ()),
											readInt (), transaction);
						return true;
					case ADD_SIMPLE_EDGES:
						if (!execute)
						{
							return false;
						}
						manager.getObject (readLong ())
							.addEdgeBitsTo (manager.getObject (readLong ()),
											(item >>> SIMPLE_EDGE_SHIFT) & SIMPLE_EDGE_MASK,
											transaction);
						return true;
					case ADD_UNDIRECTED_EDGES:
					{
						if (!execute)
						{
							return false;
						}
									Node source = manager.getObject (readLong ());
										Node target = manager.getObject (readLong ());
							int add = readInt ();
						int bits = model.getEdgeBits (source, target);
						if ((bits == 0) && ((bits = model.getEdgeBits (target, source)) != 0))
						{
							Node t = source; source = target; target = t;
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
										source.removeEdgeBitsTo (target, del, transaction);
							}
						if ((add &= ~bits | RuntimeModel.SPECIAL_MASK) != 0)
						{
										source.addEdgeBitsTo (target, add, transaction);
							}
						return true;
					}
					case DELETE_EDGES:
						if (!execute)
						{
							return false;
						}
						manager.getObject (readLong ())
							.removeEdgeBitsTo (manager.getObject (readLong ()),
											   readInt (), transaction);
						return true;
					case DELETE_SIMPLE_EDGES:
						if (!execute)
						{
							return false;
						}
						manager.getObject (readLong ())
							.removeEdgeBitsTo (manager.getObject (readLong ()),
											   (item >>> SIMPLE_EDGE_SHIFT) & SIMPLE_EDGE_MASK,
											   transaction);
						return true;
					case DELETE_NODE:
					{
						Node del;
						del = manager.getObject (readLong ());
						if (execute)
						{
							for (Edge e = del.getFirstEdge (), f; e != null; e = f)
							{
								f = e.getNext (del);
								if (del == e.getSource ())
								{
									if (e.getEdgeBits () == GraphManager.EDGENODE_IN_EDGE)
									{
										e.getTarget ().removeAll (transaction);
										continue;
									}
								}
								else
								{
									if (e.getEdgeBits () == GraphManager.EDGENODE_OUT_EDGE)
									{
										e.getSource ().removeAll (transaction);
										continue;
									}
								}
								e.remove (transaction);
							}
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
					}
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
									Node source = manager.getObject (readLong ());
										Node target = manager.getObject (readLong ());
							boolean incoming;
						boolean outgoing;
						int edges;
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
									.connect (source, target, param, QueueImpl.this);
								return true;
							default:
								throw new AssertionError ();
						}
									Node _n = (Node) source;
		Edge f;
		for (Edge e = _n.getFirstEdge (); e != null; e = f)
		{
			Node s = e.getSource (), t = e.getTarget ();
			int b = e.getEdgeBits ();
			f = e.getNext (_n);
								if (			e.testEdgeBits (edges)
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
														source.removeEdgeBitsTo (t, b, transaction);
											}
													target.addEdgeBitsTo (t, add, transaction);
										}
								}
								else if (incoming && (s != target))
								{
									if (move && (b != 0))
									{
													s.removeEdgeBitsTo (source, b, transaction);
										}
												s.addEdgeBitsTo (target, add, transaction);
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
									Node start = manager.getObject (readLong ());
										Node end = manager.getObject (readLong ());
							int edges = readInt ();
						Node pred = (Node) graph.getPredecessor (start);
						if (pred == null)
						{
							return false;
						}
									Node _n = (Node) end;
		Edge f;
		for (Edge e = _n.getFirstEdge (); e != null; e = f)
		{
			Node s = e.getSource (), t = e.getTarget ();
			int b = e.getEdgeBits ();
			f = e.getNext (_n);
								if ((t != end) && 			e.testEdgeBits (edges)
	)
							{
											end.removeEdgeBitsTo (t, b, transaction);
									if (pred != t)
								{
												pred.addEdgeBitsTo (t, b, transaction);
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
									Node source = manager.getObject (readLong ());
										Node target = manager.getObject (readLong ());
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
									Node interpreted = manager.getObject (readLong ());
										Node start = manager.getObject (readLong ());
										Node end = manager.getObject (readLong ());
										Node _n = (Node) interpreted;
		Edge f;
		for (Edge e = _n.getFirstEdge (); e != null; e = f)
		{
			Node s = e.getSource (), t = e.getTarget ();
			int b = e.getEdgeBits ();
			f = e.getNext (_n);
								b &= RuntimeModel.SUCCESSOR_EDGE | RuntimeModel.BRANCH_EDGE;
							if ((b != 0) && (s != interpreted))
							{
											s.removeEdgeBitsTo (interpreted, b, transaction);
												s.addEdgeBitsTo (start, b | RuntimeModel.CONTAINMENT_EDGE, transaction);
									break;
							}
						}
									end.addEdgeBitsTo (interpreted, RuntimeModel.SUCCESSOR_EDGE | RuntimeModel.CONTAINMENT_END_EDGE, transaction);
							Node mark = new InterpretiveMark ();
									start.addEdgeBitsTo (mark, RuntimeModel.MARK_EDGE, transaction);
										mark.addEdgeBitsTo (end, RuntimeModel.MARK_EDGE, transaction);
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
			int e = curConnTrans.operator.getUnilateralEdgeBits (curConnTrans.source, p);
			if (e != 0)
			{
				if (curConnTransOut)
				{
					connNodes.push (curConnTrans.target, p.node);
				}
				else
				{
					connNodes.push (p.node, curConnTrans.target);
				}
				connEdges.push (e);
			}
		}
	}

	
	private final NodeEdgePair nodeEdgePair = new NodeEdgePair ();

	@Override
	public boolean process (int[] segs) throws RuntimeModelException
	{
		transaction = manager.getActiveTransaction ();
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
				Node t = (Node) connNodes.pop ();
							((Node) connNodes.pop ()).addEdgeBitsTo (t, connEdges.pop (), transaction);
				}
		}
		inConnTrans.clear ();
		outConnTrans.clear ();
		connNodes.clear ();
		connEdges.clear ();
		transaction = null;
		return modified;
	}

}

