
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.xl.impl.queues.QueueCollection;
import de.grogra.xl.impl.queues.QueueDescriptor;
import de.grogra.xl.query.CompoundPattern;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.Frame;
import de.grogra.xl.query.MatchConsumer;
import de.grogra.xl.query.NodeData;
import de.grogra.xl.query.Pattern;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.query.QueryStateMultiScale;
import de.grogra.xl.query.QueryStateMultiScaleException;
import de.grogra.xl.query.Variable;
import de.grogra.xl.query.QueryState.Break;
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
	
	//multiscale begin
	private static HashMap<Object, Integer> typePOrder; //hashmap contain longest refinement path from typeRoot to each node in the type graph
	
	//caches for type graph references - to avoid repeated traversals of the type graph
	private static HashMap<Type, HashMap<Type, Boolean> > cacheScaleSame;
	private static HashMap<Type, HashMap<Type, Boolean> > cacheScaleComparable;
	private static HashMap<Type, Object> cacheTypeNode;
	private static HashMap<Type, HashMap<Type, Integer> > cacheMinEncoarseDiff;
	//multiscale end
	
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
		
		//multiscale begin
		initScaleCaches();
		//multiscale end
	}
	
	//multiscale begin
	private void initScaleCaches() {
		if(cacheScaleSame==null)
			cacheScaleSame = new HashMap<Type, HashMap<Type, Boolean> >();
		else
			cacheScaleSame.clear();
		
		if(cacheScaleComparable==null)
			cacheScaleComparable = new HashMap<Type, HashMap<Type, Boolean> >();
		else
			cacheScaleComparable.clear();
		
		if(cacheTypeNode==null)
			cacheTypeNode = new HashMap<Type, Object>();
		else
			cacheTypeNode.clear();
		
		if(cacheMinEncoarseDiff==null)
			cacheMinEncoarseDiff = new HashMap<Type, HashMap<Type, Integer> >();
		else
			cacheMinEncoarseDiff.clear();
	}
	
	private Integer cacheGetMinEncoarseDiff(Type typeA, Type typeB)
	{
		HashMap<Type, Integer> cacheMap = cacheMinEncoarseDiff.get(typeA);
		if(cacheMap!=null)
			return cacheMap.get(typeB);

		return null;
	}
	
	private void cacheSetMinEncoarseDiff(Type typeA, Type typeB, int diff)
	{
		HashMap<Type, Integer> cacheMap = cacheMinEncoarseDiff.get(typeA);
		if(cacheMap == null)
		{
			cacheMap = new HashMap<Type, Integer>();
			cacheMinEncoarseDiff.put(typeA, cacheMap);
		}
		
		cacheMap.put(typeB, new Integer(diff));
	}
	
	private Boolean cacheGetScaleSame(Type typeA, Type typeB)
	{
		HashMap<Type, Boolean> cacheMap = cacheScaleSame.get(typeA);
		if(cacheMap!=null)
			return cacheMap.get(typeB);

		return null;
	}
	
	private void cacheSetScaleSame(Type typeA, Type typeB, boolean same)
	{
		HashMap<Type, Boolean> cacheMap = cacheScaleSame.get(typeA);
		if(cacheMap == null)
		{
			cacheMap = new HashMap<Type, Boolean>();
			cacheScaleSame.put(typeA, cacheMap);
		}

		cacheMap.put(typeB, new Boolean(same));
	}
	
	private Boolean cacheGetScaleComparable(Type typeA, Type typeB)
	{
		HashMap<Type, Boolean> cacheMap = cacheScaleComparable.get(typeA);
		if(cacheMap!=null)
			return cacheMap.get(typeB);

		return null;
	}
	
	private void cacheSetScaleComparable(Type typeA, Type typeB, boolean comparable)
	{
		HashMap<Type, Boolean> cacheMap = cacheScaleComparable.get(typeA);
		if(cacheMap == null)
		{
			cacheMap = new HashMap<Type, Boolean>();
			cacheScaleComparable.put(typeA, cacheMap);
		}
		
		cacheMap.put(typeB, new Boolean(comparable));
	}
	
	private Object cacheGetTypeNode(Type typeA)
	{
		return cacheTypeNode.get(typeA);
	}
	
	private Object cacheSetTypeNode(Type typeA, Object node)
	{
		return cacheTypeNode.put(typeA, node);
	}
	//multiscale end

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
			super (Graph.this, new QStateMultiScale(Graph.this));
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
		
		//multiscale begin
		/**
		 * Finds the matches for this query state and the input compound pattern.
		 * @param pred the compound pattern
		 * @param matcher linked-list of Matcher objects of patterns in the compound pattern
		 * @param frame 
		 * @param variables
		 * @param consumer
		 * @param forProduction
		 */
		void findMatches
			(CompoundPattern pred, Pattern.Matcher matcher, Frame frame,
			 Variable[] variables, Object consumer, boolean forProduction)
		{
			this.frame = frame;
			sp = 0;
			int s = newVariables (variables.length);
			for (int i = 0; i < variables.length; i++)
			{
				if (variables[i] != null)
				{
					addVariable (variables[i]);
				}
				else
				{
					addVariable ();
				}
			}
			setVariables (s);
			
			this.matchConsumer = consumer;
			this.pred = pred;
			this.matcher = matcher;
			this.forProduction = forProduction;

			this.matcherSp = sp;
			try
			{
				matcher.findMatches (this, CONSUMER, s);
			}
			catch (Break e)
			{
				if (e != breakAll)
				{
					throw e;
				}
			}
			finally
			{
				istack.clear ();
				lstack.clear ();
				fstack.clear ();
				dstack.clear ();
				astack.clear ();
				localVariables.clear ();
				stackVariables.clear ();
				frame = null;
				dispose ();
				
				//multiscale begin
				qsMultiScale.clear();
				//multiscale end
			}
		}
		//multiscale end
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
			//multiscale begin debug
			//long start = System.nanoTime();
			//multiscale end debug
			
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
			
			//multiscale begin debug
			//long end = System.nanoTime();
			//long duration = end-start;
			//double seconds = (double)duration / 1000000000.0;
			//System.out.println("Derive Time: " + seconds);
			//multiscale end debug
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

	//multiscale begin
	/**
	 * Searches the graph (with reference to the type graph) for matching multiscale patterns
	 * @param node node already bound before this spacing
	 * @param dir edge direction
	 * @param edgeType class type of edge, normally int
	 * @param qs QueryState instance
	 * @param pattern edge pattern, i.e. successor, branching, refinement, etc.
	 * @param consumer CompoundPattern instance for continuing match logic
	 */
	public void enumerateSpaces
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
			
			//categorize type of accepted relation between the 2 nodes of this edge
			int acceptedRelation = -1;
			if( (toIndex>=0) &&
				((((CompoundPattern.Matcher)consumer).aparamsBound.length -1) >= toIndex)
				)
			{
				Type nextType = ((CompoundPattern.Matcher)consumer).types[((CompoundPattern.Matcher)consumer).aparamsBound[toIndex]];
				if(this.isSameScale(node, getTypeNode(nextType)))
				{
					if(RuntimeModel.testEdgeBits (b, bits)) //accept successor/branching edges based on the required edge type
						acceptedRelation = QStateMultiScale.RELATION_EQUAL;
				}
				else
				{
					if(RuntimeModel.testEdgeBits (b, RuntimeModel.REFINEMENT_EDGE)) //accept refinement edges
						acceptedRelation = QStateMultiScale.RELATION_REFINE;
				}
			}
			else
			{
				//categorize type of accepted relation between the 2 nodes of this edge
				//int acceptedRelation = -1;
				if(RuntimeModel.testEdgeBits (b, bits)) //accept successor/branching edges based on the required edge type
					acceptedRelation = QStateMultiScale.RELATION_EQUAL;
				else if(RuntimeModel.testEdgeBits (b, RuntimeModel.REFINEMENT_EDGE)) //accept refinement edges
					acceptedRelation = QStateMultiScale.RELATION_REFINE;
			}
			
			if((acceptedRelation==QStateMultiScale.RELATION_EQUAL)||
				(acceptedRelation==QStateMultiScale.RELATION_REFINE)
				)
			{
				switch (qs.ibind (matchIndex, b))
				{
					case QueryState.BINDING_MISMATCHED:
						break;
					case QueryState.BINDING_MATCHED:
						try
						{
							qs.addRelation(i.source, i.target, acceptedRelation);
						}
						catch (QueryStateMultiScaleException e)
						{
							qs.unbind (matchIndex);
							break;
						}
						qs.amatch (toIndex, o, consumer, arg);
						//multiscale begin
						try
						{
							if(qs.getIsMultiScaleMatcherSize()>0)
							{
								int numOfTrue = qs.getIsMultiScaleMatcherTrueCount();
								int numOfRelations = qs.getRelationCount();
								
								//get last Matcher used
								boolean isMs = qs.getIsMultiScaleMatcher();
								if((isMs)&&(numOfTrue==numOfRelations))
									qs.popRelation();							
							}
						}
						catch(QueryStateMultiScaleException e)
						{

						}
						//multiscale end
						break;
					case QueryState.BINDING_PERFORMED:
						try
						{
							qs.addRelation(i.source, i.target, acceptedRelation);
						}
						catch (QueryStateMultiScaleException e)
						{
							qs.unbind (matchIndex);
							break;
						}
						
						try
						{	
							qs.amatch (toIndex, o, consumer, arg);
						}
						finally
						{
							qs.unbind (matchIndex);
							
							//multiscale begin
							try
							{
								if(qs.getIsMultiScaleMatcherSize()>0)
								{
									int numOfTrue = qs.getIsMultiScaleMatcherTrueCount();
									int numOfRelations = qs.getRelationCount();
									
									//get last Matcher used
									boolean isMs = qs.getIsMultiScaleMatcher();
									if((isMs)&&(numOfTrue==numOfRelations))
										qs.popRelation();							
								}
							}
							catch(QueryStateMultiScaleException e)
							{

							}
							//multiscale end
						}
						break;
				}
			}
		}
		
		//for the case where this pair of nodes form the boundary between 2 word groups
		enumerateSpacesWordCross(node, node, dir, bits, qs, matchIndex, toIndex, consumer, arg);
	}
	
	private void enumerateSpacesWordCross(Object nodeOriginal, 
			Object node, 
			EdgeDirection dir, 
			int bits, 
			QueryState qs, 
			int matchIndex, 
			int toIndex, 
			MatchConsumer consumer, 
			int arg)
	{
		//Multiscale TODO: Modify later to handle the case where 2 word group are connected by 2 scales that are not connected.
		
		//assume bound node is coarser one for BACKWARD
		if(EdgeDirection.BACKWARD == dir)
		{
			//iterate finer scales of 'node', find all incoming edges from finer scales of bound node
			for (EdgeIterator j = model.createEdgeIterator (node, EdgeDirection.FORWARD); j.hasEdge (); j.moveToNext ())
			{
				if(RuntimeModel.testEdgeBits (j.edgeBits, RuntimeModel.REFINEMENT_EDGE))
				{
					Object otherScaleNode = j.target;
					
					if (otherScaleNode == node) //check for self-loop edge
					{
						continue;
					}
					else //found refined object of node
					{	
						//go through incoming edges of refined node
						for (EdgeIterator k = model.createEdgeIterator (otherScaleNode, EdgeDirection.BACKWARD); k.hasEdge (); k.moveToNext ())
						{
							Object otherScaleConnected = k.source;
							if (otherScaleConnected == otherScaleNode) //check for self-loop edge
							{
								continue;
							}
							else
							{
								if(RuntimeModel.testEdgeBits (k.edgeBits, bits))
								{
									int acceptedRelation=QStateMultiScale.RELATION_CROSS;
									switch (qs.ibind (matchIndex, k.edgeBits))
									{
										case QueryState.BINDING_MISMATCHED:
											break;
										case QueryState.BINDING_MATCHED:
											try
											{
												qs.addRelation(k.source, nodeOriginal, acceptedRelation);
											}
											catch (QueryStateMultiScaleException e)
											{
												qs.unbind (matchIndex);
												break;
											}
											qs.amatch (toIndex, k.source, consumer, arg);
											//multiscale begin
											try
											{
												if(qs.getIsMultiScaleMatcherSize()>0)
												{
													int numOfTrue = qs.getIsMultiScaleMatcherTrueCount();
													int numOfRelations = qs.getRelationCount();
													
													//get last Matcher used
													boolean isMs = qs.getIsMultiScaleMatcher();
													if((isMs)&&(numOfTrue==numOfRelations))
														qs.popRelation();							
												}
											}
											catch(QueryStateMultiScaleException e)
											{

											}
											//multiscale end
											break;
										case QueryState.BINDING_PERFORMED:
											try
											{
												qs.addRelation(k.source, nodeOriginal, acceptedRelation);
											}
											catch (QueryStateMultiScaleException e)
											{
												qs.unbind (matchIndex);
												break;
											}
											
											try
											{	
												qs.amatch (toIndex, k.source, consumer, arg);
											}
											finally
											{
												qs.unbind (matchIndex);
												//multiscale begin
												try
												{
													if(qs.getIsMultiScaleMatcherSize()>0)
													{
														int numOfTrue = qs.getIsMultiScaleMatcherTrueCount();
														int numOfRelations = qs.getRelationCount();
														
														//get last Matcher used
														boolean isMs = qs.getIsMultiScaleMatcher();
														if((isMs)&&(numOfTrue==numOfRelations))
															qs.popRelation();							
													}
												}
												catch(QueryStateMultiScaleException e)
												{

												}
												//multiscale end
											}
											break;
									}
								}
							}
						}
						
						//proceed to next finer scale
						enumerateSpacesWordCross(node, otherScaleNode, dir, bits, qs, matchIndex, toIndex, consumer, arg);
					}
				}
			}
		}
		//assume bound node is finer one for FORWARD
		else if(EdgeDirection.FORWARD == dir)
		{
			//iterate coarser scales of 'node', find all outgoing edges from coarser scales of bound node
			for (EdgeIterator j = model.createEdgeIterator (node, EdgeDirection.BACKWARD); j.hasEdge (); j.moveToNext ())
			{
				if(RuntimeModel.testEdgeBits (j.edgeBits, RuntimeModel.REFINEMENT_EDGE))
				{
					//found encoarsement node of node on lhs
					Object otherScaleNode = j.source;
					if (otherScaleNode == node) //check for self-loop edge
					{
						continue;
					}
					else
					{
						//go through outgoing edges of encoarsed node
						for (EdgeIterator k = model.createEdgeIterator (otherScaleNode, EdgeDirection.FORWARD); k.hasEdge (); k.moveToNext ())
						{
							Object otherScaleConnected = k.target;
							if (otherScaleConnected == otherScaleNode) //check for self-loop edge
							{
								continue;
							}
							else
							{
								if(RuntimeModel.testEdgeBits (k.edgeBits, bits))
								{
									int acceptedRelation=QStateMultiScale.RELATION_CROSS;
									switch (qs.ibind (matchIndex, k.edgeBits))
									{
										case QueryState.BINDING_MISMATCHED:
											break;
										case QueryState.BINDING_MATCHED:
											try
											{
												qs.addRelation(nodeOriginal, k.target, acceptedRelation);
											}
											catch (QueryStateMultiScaleException e)
											{
												qs.unbind (matchIndex);
												break;
											}
											qs.amatch (toIndex, k.target, consumer, arg);
											//multiscale begin
											try
											{
												if(qs.getIsMultiScaleMatcherSize()>0)
												{
													int numOfTrue = qs.getIsMultiScaleMatcherTrueCount();
													int numOfRelations = qs.getRelationCount();
													
													//get last Matcher used
													boolean isMs = qs.getIsMultiScaleMatcher();
													if((isMs)&&(numOfTrue==numOfRelations))
														qs.popRelation();							
												}
											}
											catch(QueryStateMultiScaleException e)
											{

											}
											//multiscale end
											break;
										case QueryState.BINDING_PERFORMED:
											try
											{
												qs.addRelation(nodeOriginal, k.target, acceptedRelation);
											}
											catch (QueryStateMultiScaleException e)
											{
												qs.unbind (matchIndex);
												break;
											}
											
											try
											{	
												qs.amatch (toIndex, k.target, consumer, arg);
											}
											finally
											{
												qs.unbind (matchIndex);
												//multiscale begin
												try
												{
													if(qs.getIsMultiScaleMatcherSize()>0)
													{
														int numOfTrue = qs.getIsMultiScaleMatcherTrueCount();
														int numOfRelations = qs.getRelationCount();
														
														//get last Matcher used
														boolean isMs = qs.getIsMultiScaleMatcher();
														if((isMs)&&(numOfTrue==numOfRelations))
															qs.popRelation();							
													}
												}
												catch(QueryStateMultiScaleException e)
												{

												}
												//multiscale end
											}
											break;
									}
								}
							}
						}
						
						//proceed to next coarser scale
						enumerateSpacesWordCross(node, otherScaleNode, dir, bits, qs, matchIndex, toIndex, consumer, arg);
					}
				}
			}
		}
	}
	
	/**
	 * Check if a node is in the type graph
	 * @param node
	 * @return true if input node is part of the type graph, false otherwise
	 */
	public boolean isInTypeGraph(Object node)
	{
		Object typeRoot = this.getTypeRoot();
		if(typeRoot==null) //type graph does not exist
			return false;
		if(node==typeRoot)
			return true;
		
		for (EdgeIterator j = model.createEdgeIterator (node, EdgeDirection.BACKWARD); j.hasEdge (); j.moveToNext ())
		{
			if(RuntimeModel.testEdgeBits (j.edgeBits, RuntimeModel.REFINEMENT_EDGE))
			{
				//found encoarsement node of input node
				Object otherScaleNode = j.source;
				if (otherScaleNode == node) //check for self-loop edge
				{
					continue;
				}
				else
				{
					if(otherScaleNode == typeRoot)
						return true;
					else
					{
						boolean encoarseInType = isInTypeGraph(otherScaleNode);
						if(encoarseInType)
							return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public HashMap<Object, Integer> sortedTypeGraph()
	{
		Object typeRoot = this.getTypeRoot();
		if(typeRoot==null) //type graph does not exist
		{
			typePOrder = null;
			return typePOrder;
		}
		
		if(typePOrder != null)
			typePOrder.clear();
		else
			typePOrder = new HashMap<Object,Integer>();
		
		typePOrder.put(typeRoot, 0);
		sortedTypeGraphInternal(typeRoot, 1);
		
		
		return typePOrder;
	}
	
	private void sortedTypeGraphInternal(Object currNode, int nextDepth)
	{
		for (EdgeIterator j = model.createEdgeIterator (currNode, EdgeDirection.FORWARD); j.hasEdge (); j.moveToNext ())
		{
			if(RuntimeModel.testEdgeBits (j.edgeBits, RuntimeModel.REFINEMENT_EDGE))
			{
				//found refinement node of input node
				Object otherScaleNode = j.target;
				if (otherScaleNode == currNode) //check for self-loop edge
				{
					continue;
				}
				else
				{
					Integer nextPOrder = typePOrder.get(otherScaleNode);
					if(nextPOrder==null)
						typePOrder.put(otherScaleNode, new Integer(nextDepth));
					else
					{
						if(nextDepth > nextPOrder.intValue()) //only replace if this is the longest path
						{
							typePOrder.put(otherScaleNode, new Integer(nextDepth));
						}
					}
					
					sortedTypeGraphInternal(otherScaleNode, nextDepth+1);
				}
			}
		}
	}
	
	public int getScaleValue(HashMap<Object, Integer> sortedTypeGraph, Object node)
	{
		Set<Entry<Object, Integer> > typeOrderSet = sortedTypeGraph.entrySet();
		Iterator<Entry<Object, Integer> > typeOrderSetIter = typeOrderSet.iterator();
		while(typeOrderSetIter.hasNext())
		{
			Entry<Object,Integer> ent = typeOrderSetIter.next();
			
			if(Reflection.equal(Reflection.getType(ent.getKey()), Reflection.getType(node)))
				return ent.getValue().intValue();
		}
		
		return -1;
	}
	
	public Object getTypeNode(Type t)
	{
		//check cache
		Object cacheTypeNode = cacheGetTypeNode(t);
		if(cacheTypeNode!=null) //found in cache, return type node
			return cacheTypeNode;
		
		Object typeRoot = this.getTypeRoot();
		if(typeRoot==null) //type graph does not exist
			return null;
		if(Reflection.equal(Reflection.getType(typeRoot), t))
		{
			cacheSetTypeNode(t, typeRoot);
			return typeRoot;
		}	
		
		Object typeNode = getTypeNodeInternal(typeRoot, t);
		if(typeNode!=null)
			cacheSetTypeNode(t, typeNode);
		
		return typeNode;
	}
	
	private Object getTypeNodeInternal(Object node, Type inputType)
	{
		for (EdgeIterator j = model.createEdgeIterator (node, EdgeDirection.FORWARD); j.hasEdge (); j.moveToNext ())
		{			
			if(!RuntimeModel.testEdgeBits(j.edgeBits, RuntimeModel.REFINEMENT_EDGE))
				continue;
			
			if(j.target == node)
				continue;
			
			if(Reflection.equal(Reflection.getType(j.target), inputType))
				return j.target;
			else
			{
				Object nodeNext = getTypeNodeInternal(j.target,inputType);
				if(nodeNext!=null)
					return nodeNext;
			}
		}
		return null;
	}
	
	/**
	 * Get node in type graph representing the type of the input node
	 * @param node
	 * @return node from type graph representing type of input node
	 */
	public Object getTypeNode(Object node)
	{
		//check cache
		Type typeA = Reflection.getType(node);
		Object cacheTypeNode = cacheGetTypeNode(typeA);
		if(cacheTypeNode!=null) //found in cache, return type node
			return cacheTypeNode;
		
		Object typeRoot = this.getTypeRoot();
		if(typeRoot==null) //type graph does not exist
			return null;
		if(Reflection.equal(Reflection.getType(typeRoot), Reflection.getType(node)))
		{
			cacheSetTypeNode(typeA, typeRoot);
			return typeRoot;
		}
		
		//search for type node in type graph
		Object typeNode = getTypeNodeInternal(typeRoot, node);
		
		//add type node to cache to avoid searching type graph in future
		if(typeNode!=null)
			cacheSetTypeNode(typeA, typeNode);
		
		return typeNode;
	}
	
	private Object getTypeNodeInternal(Object node, Object inputNode)
	{
		for (EdgeIterator j = model.createEdgeIterator (node, EdgeDirection.FORWARD); j.hasEdge (); j.moveToNext ())
		{			
			if(!RuntimeModel.testEdgeBits(j.edgeBits, RuntimeModel.REFINEMENT_EDGE))
				continue;
			
			if(j.target == node)
				continue;
			
			if(!RuntimeModel.testEdgeBits(j.edgeBits, RuntimeModel.REFINEMENT_EDGE))
				continue;
			
			if(Reflection.equal(Reflection.getType(j.target), Reflection.getType(inputNode)))
				return j.target;
			else
			{
				Object nodeNext = getTypeNodeInternal(j.target,inputNode);
				if(nodeNext!=null)
					return nodeNext;
			}
		}
		return null;
	}
	
	public boolean areComparableScales(Object nodeA, Object nodeB)
	{
		//check cache
		Type typeA = Reflection.getType(nodeA);
		Type typeB = Reflection.getType(nodeB);
		Boolean cacheResult = cacheGetScaleComparable(typeA, typeB);
		if(cacheResult!=null)
			return cacheResult.booleanValue();
		
		Object typeRoot = this.getTypeRoot();
		if(typeRoot==null) //type graph does not exist
			return false;
		
		if(isSameScale(nodeA,nodeB))
		{
			cacheSetScaleComparable(typeA, typeB, true);
			return true;
		}

		//get nodes from type graph representing types of nodeA and nodeB
		Object nodeAType = getTypeNode(nodeA);
		Object nodeBType = getTypeNode(nodeB);
		
		if((nodeAType==null)||(nodeBType==null))
		{
			cacheSetScaleComparable(typeA, typeB, false);
			return false;
		}
		
		boolean BisRefineFromA = areComparableScalesInternal(nodeAType,nodeBType, EdgeDirection.FORWARD);
		boolean BisEncoarseFromA = areComparableScalesInternal(nodeAType,nodeBType, EdgeDirection.BACKWARD);
		
		if(BisRefineFromA || BisEncoarseFromA)
		{
			cacheSetScaleComparable(typeA, typeB, true);
			return true;
		}
		else
		{
			cacheSetScaleComparable(typeA, typeB, false);
			return false;
		}
	}
	
	private boolean areComparableScalesInternal(Object node, Object nodeBType, EdgeDirection dir)
	{
		for (EdgeIterator j = model.createEdgeIterator (node, dir); j.hasEdge (); j.moveToNext ())
		{
			if(!RuntimeModel.testEdgeBits(j.edgeBits, RuntimeModel.REFINEMENT_EDGE))
				continue;
			
			Object nodeNext= null;
			if(dir==EdgeDirection.BACKWARD)
				nodeNext=j.source;
			else
				nodeNext=j.target;
			
			if(nodeNext == node)
				continue;
			
			if(nodeNext == nodeBType)
				return true;
			else if(isSameScale(nodeNext,nodeBType))
				return true;
			else
			{
				if(areComparableScalesInternal(nodeNext, nodeBType, dir))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if 2 nodes are in the same scale in the type graph.
	 * @param nodeA
	 * @param nodeB
	 * @return
	 */
	public boolean isSameScale(Object nodeA, Object nodeB)
	{
		//check cache
		Type typeA = Reflection.getType(nodeA);
		Type typeB = Reflection.getType(nodeB);
		Boolean cacheResult = cacheGetScaleSame(typeA, typeB);
		if(cacheResult!=null)
			return cacheResult.booleanValue();
		
		Object typeRoot = this.getTypeRoot();
		if(typeRoot==null) //type graph does not exist
			return false;
		
		//get nodes from type graph representing types of nodeA and nodeB
		Object nodeAType = getTypeNode(nodeA);
		Object nodeBType = getTypeNode(nodeB);
		
		if((nodeAType==null)||(nodeBType==null))
		{
			cacheSetScaleSame(typeA, typeB, false);
			return false;
		}
		if(nodeAType == nodeBType) //if nodeA and node B are same type, they are the same scale
		{
			cacheSetScaleSame(typeA, typeB, true);
			return true;
		}
		
		//clique
		//if there exists a successor edges between nodeAType and nodeBType, they are the same scale
		boolean BisForwardFromA = isSameScaleInternal(nodeAType,nodeBType, EdgeDirection.FORWARD);
		if(BisForwardFromA)
		{
			cacheSetScaleSame(typeA, typeB, true);
			return true;
		}
		else
		{
			cacheSetScaleSame(typeA, typeB, false);
			return false;
		}
		
		//if there exists a path of successor edges between nodeAType and nodeBType,
		//nodeA and nodeB are the same scale
		//boolean BisForwardFromA = isSameScaleInternal(nodeAType,nodeBType, EdgeDirection.FORWARD);
		//boolean BisBackwardFromA = isSameScaleInternal(nodeAType,nodeBType, EdgeDirection.BACKWARD);
		
		//if(BisForwardFromA || BisBackwardFromA)
		//	return true;
		//else
		//	return false;
	}
	
	private boolean isSameScaleInternal(Object node, Object nodeBType, EdgeDirection dir)
	{
		for (EdgeIterator j = model.createEdgeIterator (node, dir); j.hasEdge (); j.moveToNext ())
		{
			if(!RuntimeModel.testEdgeBits(j.edgeBits, RuntimeModel.SUCCESSOR_EDGE))
				continue;
			
			Object nodeNext= null;
			if(dir==EdgeDirection.BACKWARD)
				nodeNext=j.source;
			else
				nodeNext=j.target;
			
			if(nodeNext == node)
				continue;
			
			if(nodeNext == nodeBType)
				return true;
//			else
//			{
//				if(isSameScaleInternal(nodeNext, nodeBType, dir))
//					return true;
//			}
		}
		return false;
	}
	
	/**
	 * Returns the length of the shortest encoarsement path from fineNode to coarseNode
	 * @param coarseNode
	 * @param fineNode
	 * @return length of path, 0 if coarseNode is not encoarsement of fineNode, -1 if error
	 */
	public int getMinimumEncoarseDepthDiff(Object coarseNode, Object fineNode)
	{		
		//check cache
		Type typeA = Reflection.getType(coarseNode);
		Type typeB = Reflection.getType(fineNode);
		Integer cacheResult = cacheGetMinEncoarseDiff(typeA, typeB);
		if(cacheResult!=null)
			return cacheResult.intValue();
		
		Object nodeCType = getTypeNode(coarseNode);
		Object nodeFType = getTypeNode(fineNode);
		
		if((nodeCType==null)||(nodeFType==null))
		{
			cacheSetMinEncoarseDiff(typeA,typeB,-1);
			return -1;
		}
		
		if(isSameScale(nodeCType,nodeFType))
		{
			cacheSetMinEncoarseDiff(typeA,typeB,0);
			return 0;
		}
		
		int diff = getMinimumEncoarseDepthDiffInternal(nodeCType, nodeFType, 1);
		cacheSetMinEncoarseDiff(typeA,typeB,diff);
		return diff;
	}
	
	private int getMinimumEncoarseDepthDiffInternal(Object coarseNode, Object fineNode, int depthDiff)
	{
		int minDepthDiff = depthDiff;
		for (EdgeIterator j = model.createEdgeIterator (fineNode, EdgeDirection.BACKWARD); j.hasEdge (); j.moveToNext ())
		{
			if(!RuntimeModel.testEdgeBits(j.edgeBits, RuntimeModel.REFINEMENT_EDGE))
				continue;
			
			Object nodeNext=j.source;
			
			if(nodeNext == fineNode) //ignore self-loop
				continue;
			
			if(isSameScale(nodeNext, coarseNode))
				return depthDiff;
			
			int nextDepth = getMinimumEncoarseDepthDiffInternal(coarseNode, nodeNext, depthDiff+1);
			if(minDepthDiff == depthDiff)
			{
				if(nextDepth!=0)
					minDepthDiff=nextDepth;
			}
			else
			{
				if((nextDepth!=0)&&(nextDepth<minDepthDiff))
					minDepthDiff=nextDepth;
			}
			
			if(minDepthDiff>depthDiff)
				return minDepthDiff;
		}
		return 0;
	}
	
	//multiscale end
}
