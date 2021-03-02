
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

import java.util.Random;

import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.xl.impl.queues.QueueCollection;
import de.grogra.xl.impl.queues.QueueDescriptor;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.MatchConsumer;
import de.grogra.xl.query.NodeData;
import de.grogra.xl.query.Pattern;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.util.EHashMap;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.Operators;
import de.grogra.xl.util.XBitSet;

/**
 * This class is an abstract base class for implementations of
 * XL's runtime model for graph-like structures. <code>Graph</code>
 * represents a single graph on which XL's query statements
 * may operate.
 * <p>
 * The {@linkplain de.grogra.xl.impl.base package documentation} contains
 * more information about the structure of the graph that is implied by
 * this base class.
 * <p>
 * The current derivation mode for rule applications acting on this graph
 * is set by {@link #setDerivationMode(int)} and may be truly parallel
 * ({@link #PARALLEL_MODE}), parallel and non-deterministic
 * ({@link #PARALLEL_NON_DETERMINISTIC_MODE}, one application is chosen
 * out of several applications which delete the same node),
 * sequential ({@link #SEQUENTIAL_MODE}, only the first rule application
 * per derivation step is used), or sequential and non-deterministic
 * ({@link #SEQUENTIAL_NON_DETERMINISTIC_MODE}, only one rule application
 * per derivation step is used).
 * 
 * @author Ole Kniemeyer
 */
public abstract class Graph implements de.grogra.xl.query.Graph, Cloneable
{
	/**
	 * Bit mask for {@link #getDerivationMode()} indicating a true parallel
	 * derivation mode. All rules are applied via every possible match in
	 * parallel. A single node may be deleted by several rule applications. 
	 */
	public static final int PARALLEL_MODE = 0;

	/**
	 * Bit mask for {@link #getDerivationMode()} indicating a parallel
	 * non-deterministic derivation mode. It is ensured that a single node is
	 * deleted by at most one actual rule application. If several potential
	 * rule applications delete the same node, one actual application is chosen
	 * pseudo-randomly.
	 */
	public static final int PARALLEL_NON_DETERMINISTIC_MODE = 1;

	/**
	 * Bit mask for {@link #getDerivationMode()} indicating a sequential
	 * derivation mode. Only one rule application is performed in a single step
	 * (as marked by {@link #derive()}). If several
	 * applications are possible, only the first one is chosen.
	 */
	public static final int SEQUENTIAL_MODE = 2;

	/**
	 * Bit mask for {@link #getDerivationMode()} indicating a sequential
	 * derivation mode. Only one rule application is performed in a single step
	 * (as marked by {@link #derive()}). If several
	 * applications are possible, one is chosen pseudo-randomly.
	 */
	public static final int SEQUENTIAL_NON_DETERMINISTIC_MODE = 3;

	/**
	 * Mask for {@link #getDerivationMode()} to obtain the mode part
	 * (one of {@link #PARALLEL_MODE},
	 * {@link #PARALLEL_NON_DETERMINISTIC_MODE},
	 * {@link #SEQUENTIAL_MODE}, {@link #SEQUENTIAL_NON_DETERMINISTIC_MODE}).
	 */
	public static final int MODE_MASK = 3;

	/**
	 * Bit mask for {@link #getDerivationMode()} which indicates that
	 * rules have to be applied as interpretive rules. 
	 */
	public static final int INTERPRETIVE_FLAG = 4;

	/**
	 * Bit mask for {@link #getDerivationMode()} which indicates that
	 * nodes which were already deleted by previous rule applications
	 * of the current derivation step shall be excluded from further
	 * matches of the same step.
	 */
	public static final int EXCLUDE_DELETED_FLAG = 8;

	final RuntimeModel model;

	final ThreadLocal<ThreadData> data = new ThreadLocal<ThreadData> ();
	
	protected ThreadData getThreadData ()
	{
		ThreadData d = data.get ();
		if (d == null)
		{
			d = createThreadData ();
			d.queues = new QueueCollection (this);
			data.set (d);
		}
		return d;
	}

	protected ThreadData createThreadData ()
	{
		return new ThreadData ();
	}

	public Graph (RuntimeModel model)
	{
		this.model = model;
	}
	
	public RuntimeModel getModel ()
	{
		return model;
	}

	
	protected abstract GraphQueue createQueue (QueueCollection qc, QueueDescriptor descr);


	public class QState extends QueryState
	{
		boolean allowNoninjective;
		ThreadData data;

		QState (ThreadData data)
		{
			super (Graph.this);
			this.data = data;
			allowNoninjective = data.nextAllowNoninjective;
			data.nextAllowNoninjective = data.defaultAllowNoninjective;
		}

		protected boolean allowsNoninjectiveMatches ()
		{
			return allowNoninjective;
		}

		protected boolean excludeFromMatch (Object node, boolean context)
		{
			return !context && data.isExcludedFromMatch (node);
		}

		public void injective ()
		{
			allowNoninjective = false;
		}

		public void noninjective ()
		{
			allowNoninjective = true;
		}
	}

	public QState createQueryState ()
	{
		QState qs = new QState (getThreadData ());
		qs.initialize ();
		return qs;
	}


	public void allowNoninjectiveMatchesByDefault (boolean value)
	{
		ThreadData d = getThreadData ();
		d.defaultAllowNoninjective = value;
		d.nextAllowNoninjective = value;
	}


	@Deprecated
	public void allowNoninjectiveMatchesForNextQuery (boolean value)
	{
		getThreadData ().nextAllowNoninjective = value;
	}

	
	protected static class ThreadData
	{
		boolean isOpen = false;
		long stamp = Long.MIN_VALUE;
		QueueCollection queues;

		EHashMap excludeFromMatch = new EHashMap (new NodeData[1], 32, 0.75f);
		NodeData key = new NodeData ();

		boolean defaultAllowNoninjective = false;
		boolean nextAllowNoninjective = false;

		int seqMatchCount = 0;
		int seqSegment = -1;

		/**
		 * Current derivation mode.
		 * 
		 * @see Graph#getDerivationMode()
		 */
		int derivationMode = PARALLEL_MODE | EXCLUDE_DELETED_FLAG;
		
		boolean isExcludedFromMatch (Object node)
		{
			key.setNode (node);
			return excludeFromMatch.get (key) != null;
		}
		
		void excludeFromMatch (Object node)
		{
			NodeData d = (NodeData) excludeFromMatch.popEntryFromPool ();
			if (d == null)
			{
				d = new NodeData ();
			}
			d.setNode (node);
			excludeFromMatch.getOrPut (d);
		}

		void open ()
		{
			if (isOpen)
			{
				return;
			}
			((Graph) queues.getGraph ()).beginModifications ();
			seqSegment = -1;
			isOpen = true;
		}

		void close ()
		{
			if (isOpen)
			{
				int[] order = null;
				if ((derivationMode & MODE_MASK) == PARALLEL_NON_DETERMINISTIC_MODE)
				{
					int s = queues.startNewSegment ();
					order = new int[s];
					for (int i = 0; i < s; i++)
					{
						order[i] = i;
					}
					Random r = Operators.getRandomGenerator ();
					for (int i = 0; i < s; i++)
					{
						int j = r.nextInt (s);
						int t = order[i];
						order[i] = order[j];
						order[j] = t;
					}
				}
				try
				{
					if (queues.process (order))
					{
						stamp++;
					}
					((Graph) queues.getGraph ()).commitModifications ();
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
				excludeFromMatch.clear ();
			}
			isOpen = false;
		}
	}

	
	/**
	 * Returns the current derivation mode. This is a combination of one of
	 * the bit masks {@link #PARALLEL_MODE},
	 * {@link #PARALLEL_NON_DETERMINISTIC_MODE},
	 * {@link #SEQUENTIAL_MODE}, {@link #SEQUENTIAL_NON_DETERMINISTIC_MODE}
	 * with the flags {@link #INTERPRETIVE_FLAG} and {@link #EXCLUDE_DELETED_FLAG}.
	 * 
	 * @return current derivation mode
	 * 
	 * @see #setDerivationMode(int)
	 */
	public int getDerivationMode ()
	{
		return getThreadData ().derivationMode;
	}
	
	/**
	 * Sets the current derivation mode. <code>mode</code> is a combination
	 * of the bit masks {@link #PARALLEL_MODE},
	 * {@link #PARALLEL_NON_DETERMINISTIC_MODE},
	 * {@link #SEQUENTIAL_MODE}, {@link #SEQUENTIAL_NON_DETERMINISTIC_MODE} with the flags
	 * {@link #INTERPRETIVE_FLAG} and {@link #EXCLUDE_DELETED_FLAG}.
	 * 
	 * @param mode desired derivation mode
	 * 
	 * @see #getDerivationMode()
	 */
	public void setDerivationMode (int mode)
	{
		getThreadData ().derivationMode = mode;
		switch (mode & MODE_MASK)
		{
			case SEQUENTIAL_MODE:
			case SEQUENTIAL_NON_DETERMINISTIC_MODE:
				break;
			default:
				getThreadData ().seqSegment = -1;
				break;
		}
	}

	boolean applyMatch (QueryState qs)
	{
		ThreadData d = getThreadData ();
		switch (d.derivationMode & MODE_MASK)
		{
			case PARALLEL_MODE:
				return true;
			case PARALLEL_NON_DETERMINISTIC_MODE:
				getQueues ().startNewSegment ();
				return true;
			case SEQUENTIAL_MODE:
				qs.breakMatching ();
				break;
			case SEQUENTIAL_NON_DETERMINISTIC_MODE:
				break;
			default:
				throw new IllegalStateException ();
		}
		if (d.seqSegment < 0)
		{
			d.seqSegment = getQueues ().startNewSegment ();
			d.seqMatchCount = 1;
			return true;
		}
		if (((d.derivationMode & MODE_MASK) == SEQUENTIAL_MODE)
			|| Operators.getRandomGenerator ().nextInt (++d.seqMatchCount) > 0)
		{
			return false;
		}
		d.queues.resetToSegment (d.seqSegment);
		return true;
	}
	
	public long derive ()
	{
		ThreadData d = getThreadData ();
		d.close ();
		return d.stamp;
	}


	protected abstract void beginModifications ();

	
	protected abstract void commitModifications ();


	public QueueCollection getQueues ()
	{
		ThreadData d = getThreadData ();
		d.open ();
		return d.queues;
	}


	public boolean canEnumerateNodes (Type type)
	{
		return Reflection.isSupertypeOrSame (model.getNodeType (), type);
	}

	
	public void enumerateEdges
		(Object node, EdgeDirection dir, Type edgeType, QueryState qs, int toIndex,
		 int patternIndex, java.io.Serializable pattern, int matchIndex, MatchConsumer consumer, int arg)
	{
		if (edgeType.getTypeId () != TypeId.INT)
		{
			return;
		}
		int bits;
		if (patternIndex >= 0)
		{
			bits = qs.ibound (patternIndex);
		}
		else if (pattern != null)
		{
			bits = ((Number) pattern).intValue ();
		}
		else
		{
			bits = -1;
		}
		for (EdgeIterator i = model.createEdgeIterator (node, dir); i.hasEdge (); i.moveToNext ())
		{
			Object o;
			if (dir == EdgeDirection.UNDIRECTED)
			{
				o = (node == i.source) ? i.target : i.source;;
			}
			else
			{
				o = (dir == EdgeDirection.BACKWARD) ? i.source : i.target;
				if (o == node)
				{
					continue;
				}
			}
			int b = i.edgeBits;
			if (RuntimeModel.testEdgeBits (b, bits))
			{
				switch (qs.ibind (matchIndex, b))
				{
					case QueryState.BINDING_MISMATCHED:
						break;
					case QueryState.BINDING_MATCHED:
						qs.amatch (toIndex, o, consumer, arg);
						break;
					case QueryState.BINDING_PERFORMED:
						try
						{
							qs.amatch (toIndex, o, consumer, arg);
						}
						finally
						{
							qs.unbind (matchIndex);
						}
						break;
				}
			}
		}
	}


	private static final Integer BRANCH_SUCCESSOR = RuntimeModel.BRANCH_EDGE | RuntimeModel.SUCCESSOR_EDGE;

	protected Object getPredecessor (Object node)
	{
		if (!canEnumerateEdges (EdgeDirection.BACKWARD, true, BRANCH_SUCCESSOR))
		{
			return null;
		}
		for (EdgeIterator i = model.createEdgeIterator (node, EdgeDirection.BACKWARD); i.hasEdge (); i.moveToNext ())
		{
			if ((i.source != node) && RuntimeModel.testEdgeBits (i.edgeBits, RuntimeModel.BRANCH_EDGE | RuntimeModel.SUCCESSOR_EDGE))
			{
				node = i.source;
				i.dispose ();
				return node;
			}
		}
		return null;
	}


	public Pattern.Matcher createMatcher
		(Pattern pred, XBitSet providedConstants, IntList neededConstantsOut)
	{
		return pred.createMatcher (this, providedConstants, neededConstantsOut);
	}

}
