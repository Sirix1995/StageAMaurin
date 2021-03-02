
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

package de.grogra.xl.query;

import java.util.Arrays;

import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.xl.util.EHashMap;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.util.XBitSet;

public final class CompoundPattern extends BuiltInPattern
{
	public static final int SIMPLE = 0;
	public static final int ATOMIC = 1;
	public static final int BREAKING = 2;
	
	private final int inParameter;
	private final int outParameter;

	private final Pattern[] predicates;
	private final boolean[] predicateIsContext;
	private final short[][] paramMappings;
	private final short[][] dependencies;
	private final short[][] foldings;
	private final int direction;
	private final byte[] paramKinds;
	private final int predType;
	private final boolean optional;
	private final String continueLabel;
	private boolean paramKindsInitialized = false;
	
	private final boolean[] initParamIsNode;
	private final boolean[] forceParamIsContext;

	//multiscale begin debug
	//public static double prodTime;	
	//multiscale end debug
	
	public CompoundPattern (Type[] types, boolean[] paramIsNode, boolean[] forceParamIsContext,
							  Pattern[] predicates, boolean[] predIsContext,
							  short[][] paramMappings, short[][] dependencies,
							  short[][] foldings,
							  int in, int out, int direction,
							  int predType, boolean optional, String continueLabel)
	{
		super (types, types.length);
		this.inParameter = in;
		this.outParameter = out;
		this.predicates = predicates;
		this.dependencies = dependencies;
		this.foldings = foldings;
		this.paramMappings = paramMappings;
		this.predicateIsContext = predIsContext;
		this.forceParamIsContext = forceParamIsContext;
		this.paramKinds = new byte[types.length];
		for (int i = 0; i < types.length; i++)
		{
			paramKinds[i] = paramIsNode[i]
				? CONTEXT_MASK | INPUT_MASK | NODE_MASK
				: (byte) (CONTEXT_MASK | INPUT_MASK);
		}
		this.direction = direction;
		this.predType = predType;
		this.optional = optional;
		this.continueLabel = (continueLabel != null) ? continueLabel.intern () : null;
		this.initParamIsNode = paramIsNode;
	}


	public void write (BytecodeSerialization out) throws java.io.IOException
	{
		out.beginMethod (Utils.getConstructor (this));
		out.beginArray (getParameterCount (), Type.TYPE);
		for (int i = 0; i < getParameterCount (); i++)
		{
			out.beginArrayComponent (i);
			out.visitType (getParameterType (i));
			out.endArrayComponent ();
		}
		out.endArray ();
		Utils.writeArray (out, initParamIsNode);
		Utils.writeArray (out, forceParamIsContext);
		out.beginArray (predicates.length, ClassAdapter.wrap (Pattern.class));
		for (int i = 0; i < predicates.length; i++)
		{
			out.beginArrayComponent (i);
			predicates[i].write (out);
			out.endArrayComponent ();
		}
		out.endArray ();
		Utils.writeArray (out, predicateIsContext);
		Utils.writeArray (out, paramMappings);
		Utils.writeArray (out, dependencies);
		Utils.writeArray (out, foldings);
		out.visitInt (inParameter);
		out.visitInt (outParameter);
		out.visitInt (direction);
		out.visitInt (predType);
		out.visitBoolean (optional);
		out.visitObject (continueLabel);
		out.endMethod ();
	}


	public int getInParameter ()
	{
		return inParameter;
	}

	public int getOutParameter ()
	{
		return outParameter;
	}

	@Override
	public int getParameterKind (int index)
	{
		synchronized (paramKinds)
		{
			if (!paramKindsInitialized)
			{
				for (int i = 0; i < predicates.length; i++)
				{
					for (int j = 0; j < paramMappings[i].length; j++)
					{
						int t = paramMappings[i][j];
						int kind = predicates[i].getParameterKind (j);
						if ((kind & NODE_MASK) != 0)
						{
							paramKinds[t] |= NODE_MASK;
						}
						if ((kind & INPUT_MASK) == 0)
						{
							paramKinds[t] &= ~INPUT_MASK;
						}
						if ((kind & OUTPUT_MASK) != 0)
						{
							paramKinds[t] |= OUTPUT_MASK;
						}
						if (!predicateIsContext[i] && ((kind & CONTEXT_MASK) == 0)
							&& !forceParamIsContext[t])
						{
							paramKinds[t] &= ~CONTEXT_MASK;
						}
					}
				}
				switch (direction)
				{
					case EdgeDirection.FORWARD_INT:
						if (inParameter >= 0)
						{
							paramKinds[inParameter] |= INPUT_MASK;
						}
						break;
					case EdgeDirection.BACKWARD_INT:
						if (outParameter >= 0)
						{
							paramKinds[outParameter] |= INPUT_MASK;
						}
						break;
				}
				paramKindsInitialized = true;
			}
		}
		return paramKinds[index];
	}
	
	
	static final class ParamData
	{
		Type type;
		int kind;
		int index;
		ObjectList<ParamData> folded;
		
		@Override
		public String toString ()
		{
			return "ParamData[" + type + ',' + kind + ',' + index + ']';
		}
	}

	
	/**
	 * This helper class contains a configuration for a
	 * predicate matcher, which is the set of constants which are needed
	 * when the predicate matcher is invoked. This class also
	 * contains some information which is needed in
	 * {@link CompoundPattern#computeOptimalOrder}. 
	 * 
	 * @author Ole Kniemeyer
	 */
	static final class Config implements Comparable
	{
		final Matcher owner;

		/**
		 * The matcher for which this configuration is defined.
		 */
		final Pattern.Matcher matcher;

		/**
		 * The set of constants which {@link #matcher} needs when
		 * it is invoked. The indices are relative to the
		 * compound predicate in which the matcher's predicate is defined.
		 */
		final XBitSet neededConstants = new XBitSet ();

		/**
		 * Current state of algorithm <code>computeOptimalOrder</code>:
		 * Next configuration of current order.
		 */
		Config curNext;

		/**
		 * Current state of algorithm <code>computeOptimalOrder</code>:
		 * Next configuration of optimal order found so far.
		 */
		Config optNext;
		float optTime = Float.MAX_VALUE;
		float optMatchCount;
		XBitSet visited = new XBitSet ();
		

		Config (Matcher owner, Pattern.Matcher matcher, XBitSet consts)
		{
			this.owner = owner;
			this.matcher = matcher;
			neededConstants.set (consts);
		}


		public int compareTo (Object o)
		{
			return matcher.compareTo (((Config) o).matcher);
		}
		
		
		@Override
		public String toString ()
		{
			return "Config[" + matcher + ',' + neededConstants + ']';
		}

	}
	

	public static final class Matcher extends Pattern.Matcher implements MatchConsumer
	{
		Matcher next;
		int indexInOrder2;
		int[] paramMapping;
		//multiscale begin
		public int[] aparamsBound;
		//multiscale end		
		int[] nodesBound;
		Pattern.Matcher matcher;
		boolean deleting;
		boolean breaking;
		String continueLabel;
		//multiscale begin
		public Type[] types;
		//multiscale end
		int[] kinds;
		int[] folding;
		int predicateCount;
		
		Pattern predicate;
		int index = -1;
		XBitSet dependsOn;
		int base;
		
		private final float time;

		
		Matcher ()
		{
			this (0, Float.NaN);
		}

		
		Matcher (float time, float count)
		{
			super (count);
			this.time = time;
		}

		
		@Override
		public float getBaseCosts ()
		{
			return time;
		}

		
		@Override
		public String toString ()
		{
			if (matcher != null)
			{
				return "Matcher" + '[' + predicate + ',' + matcher + ','
					+ Arrays.toString (paramMapping) + ',' 
					+ Arrays.toString (aparamsBound) + ',' + dependsOn
					+ ",(" + getBranchingFactor () + ',' + getBaseCosts () + ")]";
			}
			else
			{
				StringBuffer b = new StringBuffer ();
				toString (b, "");
				return b.toString ();
			}
		}


		private void toString (StringBuffer b, String indent)
		{
			b.append (indent).append ("Matcher for ").append (predicate)
				.append ('\n').append (indent).append ("[\n");
			String newIndent = indent + "   ";
			for (Matcher m = next; m != null; m = m.next)
			{
				if (m.matcher instanceof Matcher)
				{
					((Matcher) m.matcher).toString (b, newIndent);
				}
				else
				{
					int n = b.length ();
					b.append (newIndent).append (m.matcher);
					while ((n = b.indexOf ("\n", n)) >= 0)
					{
						b.insert (n + 1, newIndent);
						n = n + newIndent.length () + 1;
					}
					b.append ('\n');
				}
				b.append (newIndent).append ("  index=").append (m.index)
					.append (", mapping=").append (Arrays.toString (m.paramMapping))
					.append (", nodes=").append (Arrays.toString (m.nodesBound))
					.append (", kinds=").append (Arrays.toString (m.kinds))
					.append (", dependsOn=").append (m.dependsOn)
					.append (", costs=(").append (m.matcher.getBranchingFactor ()).append (',').append (m.matcher.getBaseCosts ()).append (")\n");
			}
			b.append (indent).append ("] costs=(").append (getBranchingFactor ()).append (',').append (getBaseCosts ()).append (")\n");
		}


		private static final int MSP = 0;
		private static final int ARG = 1;
		private static final int MATCH_FOUND = 2;
		private static final int MAP = 0;
		private static final int CONSUMER_SP = 1;
		private static final int COMMON_STACK_SIZE = 3;
		
 
		private static void removeMappings (EHashMap map, int nodeCount, ObjectList stack, int base)
		{
			EHashMap.Entry e = map.getLastEntry ();
			while (--nodeCount >= 0)
			{
				EHashMap.Entry f = e.listPrev;
				map.remove (e);
				e = f;
			}
			while (stack.size > base)
			{
				((NodeData) stack.pop ()).context = true;
			}
		}


		static EHashMap<NodeData> getNodeMap (QueryState qs, int qsp)
		{
			return (EHashMap<NodeData>) qs.astack.get (MAP + qsp);
		}


		@Override
		public void visitMatch (QueryState qs, Producer prod)
		{
			int[] stack = qs.istack.elements;
			int cmp = qs.getVariables ();
			int dsp = qs.deleteSp;
			for (Matcher p = next; p != null; p = p.next)
			{
				if (p.deleting)
				{
					qs.setVariables (stack[COMMON_STACK_SIZE + dsp + p.indexInOrder2]);
					qs.deleteSp = stack[COMMON_STACK_SIZE + dsp + p.indexInOrder2 + 1];
					p.matcher.visitMatch (qs, prod);
				}
			}
			qs.setVariables (cmp);
			qs.deleteSp = dsp;
		}


		@Override
		public void findMatches
			(QueryState qs, MatchConsumer consumer, int arg)
		{
			int qsp = qs.enter (COMMON_STACK_SIZE + 2 * predicateCount);
			IntList istack = qs.istack;
			ObjectList astack = qs.astack;
			int msp = qs.getVariables ();
			istack.set (MSP + qsp,  msp);
			istack.set (ARG + qsp, arg);
			istack.set (MATCH_FOUND + qsp, 0);
			astack.set (MAP + qsp, qs.allocateNodeMap ());
			astack.set (CONSUMER_SP + qsp, consumer);
			Matcher m = next;
			for (int i = 0; i < predicateCount; i++, m = m.next)
			{
				qs.setVariables (msp);
				int a = qs.mapVariables (m.paramMapping);
				istack.set (COMMON_STACK_SIZE + qsp + m.indexInOrder2, a);
			}
			try
			{
				try
				{
					matchFound (qs, qsp);
				}
				catch (QueryState.Break e)
				{
					qs.check (e, null, qsp);
					e.pointer = -1;
					e.predicateId = continueLabel;
					throw e;
				}
				if (((CompoundPattern) predicate).optional
					&& (istack.get (MATCH_FOUND + qsp) == 0))
				{
					qs.setVariables (msp);
					int p = qs.enter (types.length);
					int nextVoid = p;
					for (int i = 0; i < types.length; i++)
					{
						if (!qs.isBound (i))
						{
							istack.set (nextVoid++, i);
							qs.nullbind (i);
						}
					}
					try
					{
						consumer.matchFound (qs, arg);
					}
					finally
					{
						while (--nextVoid >= p)
						{
							qs.unbind (istack.get (nextVoid));
						}
					}
				}
			}
			finally
			{
				qs.disposeNodeMap ((EHashMap<NodeData>) astack.get (MAP + qsp));
				qs.leave (qsp);
				qs.setVariables (msp);
			}
		}


		public void matchFound (QueryState qs, int qsp)
		{
			int cmp = qs.getVariables ();
			IntList istack = qs.istack;
			qs.setVariables (istack.get (MSP + qsp));
			EHashMap<NodeData> map = (EHashMap<NodeData>) qs.astack.get (MAP + qsp);
			int mappedCount = 0;
			ObjectList restoreContextStack = qs.userStack0;
			int rdsBase = restoreContextStack.size;
			try
			{
				int i;
				int[] m = aparamsBound;
				for (i = m.length - 1; i >= 0; i--)
				{
					Object o;
					if (((o = qs.abound (m[i])) != null)
						&& !types[m[i]].isInstance (o))
					{
						//multiscale begin - type of the matched node is not the same as the required type 
						if(qs.getIsMultiScaleMatcherSize()>0)
						{
							//pop away the last added relation, if the matcher used is a multiscale matcher
							if(qs.getIsMultiScaleMatcher())
							{
								try
								{
									qs.popRelation();
								}
								catch(QueryStateMultiScaleException e)
								{
									throw qs.breakPattern;
								}
							}
															
						}
						//multiscale end
						
						return;
					}
				}
				
				//multiscale begin - query context checking
				
				//multiscale end
				
				m = nodesBound;
				for (i = m.length - 1; i >= 0; i--)
				{
					if (qs.isNull (m[i]))
					{
						continue;
					}
					boolean context = !qs.forProduction || ((kinds[m[i]] & CONTEXT_MASK) != 0);
					assert types[m[i]].getTypeId () == TypeId.OBJECT;
					NodeData d;
					Object v = qs.abound (m[i]);
					if (!qs.model.isNode (v) || qs.excludeFromMatch (v, context))
					{
						return;
					}
					d = map.popEntryFromPool ();
					if (d == null)
					{
						d = new NodeData ();
					}
					d.setNode (v);
					NodeData e = map.getOrPut (d);
					if (e != null)
					{
						if (!qs.allowsNoninjectiveMatches ()
							&& (e.foldingId != folding[m[i]]))
						{
							return;
						}
						if (!context && e.context)
						{
							restoreContextStack.push (e);
							e.context = false;
						}
					}
					else
					{
						mappedCount++;
						if (!context)
						{
							d.context = false;
						}
						d.foldingId = folding[m[i]];
					}
				}
				if (next == null)
				{
					//multiscale begin
					
					//query context matching
					//if false, conditions for multiscale query context matching are not met or invalid.
					//if true, continue to production.
					try
					{
						if(!qs.queryContextMatch()) 
							return;
					}
					catch(QueryStateMultiScaleException e)
					{
						throw qs.breakPattern;
					}
					
					//multiscale end
					
					//multiscale begin
					try
					{	//multiscale begin debug
//						long prodStart = System.nanoTime();
						//multiscale end debug
						qs.updateFirstLastNodes(); //search and collect first and last nodes at multiple scales
					//multiscale end
						
						//invoking the producer
						istack.set (MATCH_FOUND + qsp, 1);
						((MatchConsumer) qs.astack.get (CONSUMER_SP + qsp))
							.matchFound (qs, istack.get (ARG + qsp));
						if (breaking)
						{
							QueryState.Break b = qs.breakPattern;
							b.pointer = qsp;
							b.predicateId = null;
							throw qs.breakPattern;
						}
						//multiscale begin debug
//						long prodEnd = System.nanoTime();
//						long duration = prodEnd-prodStart;
//						double seconds = (double)duration / 1000000000.0;
//						prodTime += seconds;
						//multiscale end debug
					}
					//multiscale begin
					finally 
					{
						qs.removeDynamicConnections(); //must clear dynamically added relations in QueryStateMultiScale instance
					}
					//multiscale end
				}
				else
				{
					qs.setVariables (istack.get (COMMON_STACK_SIZE + qsp + next.indexInOrder2));
					istack.set (COMMON_STACK_SIZE + qsp + next.indexInOrder2 + 1, qs.getSp ());
										
					next.matcher.findMatches (qs, next, qsp);
				}
			}
			catch (QueryState.Break e)
			{
				qs.check (e, null, -1);
			}
			finally
			{
				qs.setVariables (cmp);
				removeMappings (map, mappedCount, restoreContextStack, rdsBase);
			}
		}
	}


	private Matcher collectPatterns (IntList mapStack, int countBase,
									   int mapBase, ObjectList<ParamData> terms,
									   ObjectList stack, short[] mapping, boolean context)
	{
		int msp = mapStack.size;
		mapStack.setSize (msp + getParameterCount ());
		for (int i = 0; i < getParameterCount (); i++)
		{
			if ((mapping != null) && (mapping[i] >= 0))
			{
				mapStack.set (msp + i, mapStack.get (mapBase + mapping[i]));
			}
			else
			{
				mapStack.set (msp + i, terms.size);
				ParamData d = new ParamData ();
				d.type = getParameterType (i);
				d.kind = getParameterKind (i);
				d.index = terms.size;
				terms.add (d);
			}
		}
		for (int i = 0; i < getParameterCount (); i++)
		{
			short[] folding = foldings[i];
			if (folding != null)
			{
				ParamData d = terms.get (mapStack.get (msp + i));
				ObjectList<ParamData> folded = new ObjectList<ParamData> ();
				folded.add (d);
				for (int j = 0; j < folding.length; j++)
				{
					ParamData f = terms.get (mapStack.get (msp + folding[j]));
					folded.addIfNotContained (f);
					ObjectList<ParamData> folded2 = f.folded;
					if (folded2 != null)
					{
						for (int k = 0; k < folded2.size (); k++)
						{
							folded.addIfNotContained (folded2.get (k));
						}
					}
				}
				for (int j = 0; j < folded.size (); j++)
				{
					folded.get (j).folded = folded;
				}
			}
		}
		int sp = stack.size;
		int n = predicates.length;
		for (int i = n - 1; i >= 0; i--)
		{
			Pattern p = predicates[i];
			if (p instanceof UserDefinedCompoundPattern)
			{
				p = ((UserDefinedCompoundPattern) p).getPattern ();
			}
			short[] m = paramMappings[i];
			if ((p instanceof CompoundPattern) //in the case of simple compound patterns
				&& (((CompoundPattern) p).predType == SIMPLE))
			{ //recursive call to next compound pattern. returned Matcher stored in stack.
				stack.set (sp + i,
						   ((CompoundPattern) p).collectPatterns
						   (mapStack, countBase, msp, terms, stack, m,
						   	context || predicateIsContext[i]));
			}
			else
			{
				Matcher info = new Matcher ();
				info.predicate = p;
				info.deleting = !(context || predicateIsContext[i]) && p.isDeleting ();
				info.index = mapStack.elements[countBase]++;
				stack.set (sp + i, info);
				int[] global = new int[m.length];
				for (int j = m.length - 1; j >= 0; j--)
				{
					global[j] = mapStack.get (msp + m[j]);
				}
				info.paramMapping = global;
			}
		}
		for (int i = n - 1; i >= 0; i--)
		{
			Matcher pi = (Matcher) stack.get (sp + i);
			if (pi != null)
			{
				for (int j = dependencies[i].length - 1; j >= 0; j--)
				{
					Matcher pj = (Matcher)
						stack.get (sp + dependencies[i][j]);
					while (pj != null)
					{
						if (pi.dependsOn == null)
						{
							pi.dependsOn = new XBitSet ();
						}
						pi.dependsOn.set (pj.index);
						pj = pj.next;
					}
				}
			}
		}
		Matcher head = null;
		for (int i = n - 1; i >= 0; i--)
		{
			Matcher pi = (Matcher) stack.get (sp + i);
			if (pi != null)
			{
				Matcher q = pi;
				if (head != null)
				{
					while (q.next != null)
					{
						q = q.next;
					}
					q.next = head;
				}
				head = pi;
			}
		}
		stack.setSize (sp);
		mapStack.setSize (msp);
		return head;
	}

	
	/**
	 * Computes the optimal order of predicate matchers. This
	 * algorithm orders all possible
	 * configurations found in <code>configs</code> so that
	 * the estimated time is minimal.
	 * 
	 * @param src extent to use
	 * @param configs set of all possible {@link Config}s
	 * @param added used to store temporary information, content must be restored when invocation completes
	 * @param constants set of query variables which have been matched so far, i.e., which are constant
	 * @param notMatched set of predicates which have not yet been included in the current order
	 * @param prev previous configuration of current order
	 * @param compl
	 * @param complSum
	 * @param root root configuration, used to store optimal orders
	 */
	private static void computeOptimalOrder
		(Graph src, ObjectList configs, IntList added, XBitSet constants,
		 XBitSet notMatched, Config prev, float compl, float complSum,
		 Config root)
	{
		if (complSum >= root.optTime)
		{
			// no chance to compute an optimal order
			return;
		}
		
		// save old size of added
		// added is used to temporarily store the indices of added constants
		int begin = added.size ();

		prev.visited.clear ();
		Config cursor = prev;
		
		boolean matchersLeft;
		boolean matchersAdded;
		int neededCount = 0;
		do
		{
			matchersLeft = false;
			matchersAdded = false;
			boolean orderFound = true;

		findNextConfig:
			for (int i = 0; i < configs.size (); i++)
			{
				Config c = (Config) configs.get (i);
				if (notMatched.get (c.owner.index))
				{
					orderFound = false;
					if (!prev.visited.get (c.owner.index)
						&& ((c.owner.dependsOn == null)
							|| !c.owner.dependsOn.intersects (notMatched)))
					{
						int s = added.size ();
						int[] mapping = c.owner.paramMapping;
						int n = 0;
						float c2 = compl, cs2 = complSum;
						for (int j = 0; j < mapping.length; j++)
						{
							if (c.neededConstants.get (mapping[j])
								&& !constants.get (mapping[j]))
							{
								if (n == neededCount)
								{
									matchersLeft = true;
									n = -1;
									break;
								}
								else if (!src.canEnumerateNodes (c.owner.predicate.getParameterType (j)))
								{
									n = -1;
									break;
								}
								else
								{
									int as = added.size ();
									boolean c0 = constants.remove (0);
									Pattern.Matcher sm = src.createMatcher
										(new EnumerateNodesPattern
										 (c.owner.predicate.getParameterType (j)),
										 constants, added);
									cs2 += c2 * sm.getBaseCosts ();
									c2 *= sm.getBranchingFactor ();
									constants.set (0, c0);
									added.setSize (as);
									if (!constants.add (mapping[j]))
									{
										throw new AssertionError ();
									}
									added.add (mapping[j]);
									n++;
								}
							}
						}
						if (n == neededCount)
						{
							matchersAdded = true;
							notMatched.remove (c.owner.index);
							prev.visited.add (c.owner.index);
							c.curNext = null;
							cursor.curNext = c;
							cs2 += c2 * c.matcher.getBaseCosts ();
							c2 *= c.matcher.getBranchingFactor ();
							for (int j = 0; j < mapping.length; j++)
							{
								if (constants.add (mapping[j]))
								{
									added.add (mapping[j]);
								}
							}
							if (neededCount > 0)
							{
								computeOptimalOrder
									(src, configs, added, constants, notMatched,
									 c, c2, cs2, root);
								notMatched.add (c.owner.index);
							}
							else
							{
								complSum = cs2;
								compl = c2;
								cursor = c;
								break findNextConfig;
							}
						}
						while (added.size () > s)
						{
							constants.remove (added.pop ());
						}
					}
				}
			}
			if (orderFound)
			{
				if (complSum < root.optTime)
				{
					root.optTime = complSum;
					root.optMatchCount = compl;
					for (Config m = root; m != null; m = m.curNext)
					{
						m.optNext = m.curNext;
					}
				}
			}
			if ((neededCount > 0) || !matchersAdded)
			{
				neededCount++;
			}
		} while (matchersLeft || matchersAdded);
		
		// restore old content of added
		while (added.size () > begin)
		{
			constants.remove (added.pop ());
		}

		for (Config m = prev; m != cursor; m = m.curNext)
		{
			notMatched.add (m.curNext.owner.index);
		}
	}

	
	private static final int[] INT_0 = new int[0];

	/**
	 * Creates a Matcher object for this CompoundPattern instance.
	 * @param src the runtime graph in which the created matcher will search for matches
	 * @param providedConstants specifies which parameters are already bounded
	 * @param neededConstantsOut specifies which parameters need to be bounded
	 * @return Matcher object that can find matches in the graph 
	 */
	@Override
	public Pattern.Matcher createMatcher
		(Graph src, XBitSet providedConstants, IntList neededConstantsOut)
	{
		int addedParam, removedParam;
		switch (direction)
		{
			//edge patterns have different inParameter and outParameter
			case EdgeDirection.FORWARD_INT:	// case where this pattern is a forward edge pattern
				addedParam = inParameter;
				removedParam = outParameter;
				break;
			case EdgeDirection.BACKWARD_INT:// case where this pattern is a backward edge pattern
				addedParam = outParameter;
				removedParam = inParameter;
				break;
			//default case includes - node patterns (inParameter same as outParameter)
			//						- patterns that are not simple (inParameter or outParameter must be provided)
			default:
				addedParam = -1;
				removedParam = -1;
				if ((predType != SIMPLE) && ((inParameter >= 0) || (outParameter >= 0)))
				{
					if ((inParameter >= 0)
						&& providedConstants.get (inParameter))
					{
						break;
					}
					if ((outParameter >= 0)
						&& providedConstants.get (outParameter))
					{
						break;
					}
					return null;
				}
				break;
		}
		if (addedParam == removedParam) //case where edge is a loop, then only addedParam is required
		{
			removedParam = -1;
		}
		
		//case where this compoundpattern is an edge, check if addedParam (source node) is already bounded
		if ((addedParam >= 0) && providedConstants.add (addedParam))
		{
			//if not already bounded, added the addedParam index to the list of parameters that must be bounded
			neededConstantsOut.add (addedParam); 
		}
		else
		{
			addedParam = -1; //if this is an edge pattern, this is the case where addedParam is already bounded.
							 //if this is not an edge pattern, no need for addedParam.
		}
		if ((removedParam >= 0) && !providedConstants.remove (removedParam))
		{
			removedParam = -1;
		}
		ObjectList stack = new ObjectList (), paramList = new ObjectList ();
		int ncoBase = neededConstantsOut.size;
		neededConstantsOut.add (0);
		Matcher list = collectPatterns
			(neededConstantsOut, ncoBase, Integer.MIN_VALUE, paramList, stack, null, false);
		int count = neededConstantsOut.get (ncoBase);
		ParamData[] params = new ParamData[paramList.size];
		paramList.toArray (params);

		paramList.clear ();
		ObjectList orderedConfigs = paramList;
		paramList = null;

		XBitSet notMatched = new XBitSet ();
		XBitSet constants = new XBitSet ();
		XBitSet predConsts = new XBitSet ();
		XBitSet multiplyMapped = new XBitSet ();
		
		//Go through list of compoundpattern.matchers for the patterns in this compoundPattern
		for (Matcher p = list; p != null; p = p.next)
		{
			constants.clear ();
			predConsts.clear ();
			multiplyMapped.clear ();
			int[] tm = p.paramMapping;
			for (int i = 0; i < tm.length; i++)
			{
				if (!predConsts.add (tm[i]))
				{
					multiplyMapped.set (i);
				}
				constants.set (i, providedConstants.get (tm[i]));
			}
			predConsts.clear ();
			int n = p.predicate.getDeclaredParameterCount ();
			for (int i = 0; i < n; i++)
			{
				if ((p.predicate.getParameterKind (i) & INPUT_MASK) != 0)
				{
					predConsts.set (tm[i]);
				}
			}
			for (int i = 0; i < tm.length; i++)
			{
				if (predConsts.get (tm[i]))
				{
					constants.set (i);
				}
			}
			boolean configFound = false;
		computeConfigs:
			while (true)
			{
				neededConstantsOut.setSize (ncoBase);
				Pattern.Matcher m = src.createMatcher
					(p.predicate, constants, neededConstantsOut);
				if ((m != null) && (neededConstantsOut.size () == ncoBase))
				{
					configFound = true;
					orderedConfigs.addInOrder (new Config (p, m, predConsts));
				}
				int i = 0;
				while (i < n)
				{
					if (((p.predicate.getParameterKind (i) & (INPUT_MASK | OUTPUT_MASK)) != 0)
						|| providedConstants.get (tm[i]))
					{
						i++;
					}
					else if (multiplyMapped.get (i))
					{
						constants.set (i, predConsts.get (tm[i]));
						i++;
					}
					else
					{
						predConsts.flip (tm[i]);
						if (constants.flip (i))
						{
							i++;
						}
						else
						{
							continue computeConfigs;
						}
					}
				}
				break;
			}
			
			if (!configFound)
			{
				throw new RuntimeException
					("No configuration for " + p.predicate + " found");
			}
		}
		
		Config root = new Config (null, null, predConsts);
		constants.set (providedConstants);
		notMatched.setRange (0, count, true);
		
		//get optimum order to perform the matching. i.e. matching certain patterns first can significantly reduce time taken to find matches.
		computeOptimalOrder (src, orderedConfigs, neededConstantsOut, constants, notMatched, root, 1, 0, root);
		
		constants.set (providedConstants);
		
		if (addedParam >= 0)
		{
			providedConstants.remove (addedParam);
		}
		if (removedParam >= 0)
		{
			providedConstants.add (removedParam);
		}

		if ((root.optNext == null) && (list != null))
		{
			neededConstantsOut.setSize (ncoBase);
			return null;
		}
		
		predConsts.clear ();
		
		orderedConfigs.clear ();
		ObjectList matchers = orderedConfigs;
		orderedConfigs = null;
		
		for (Config c = root.optNext; c != null; c = c.optNext)
		{
			Matcher p = c.owner;
			assert (p.dependsOn == null)
				|| !p.dependsOn.intersects (notMatched);
			p.matcher = c.matcher;
			for (int j = 0; j < p.paramMapping.length; j++)
			{
				int term = p.paramMapping[j];
				if (c.neededConstants.get (term) && constants.add (term))
				{
					assert src.canEnumerateNodes (p.predicate.getParameterType (j));
					Pattern s = new EnumerateNodesPattern (params[term].type);
					Matcher m = new Matcher ();
					m.predicate = s;
					m.paramMapping = new int[] {term};
					m.aparamsBound = (params[term].type.getTypeId () == TypeId.OBJECT)
						? m.paramMapping : INT_0;
					m.nodesBound = ((params[term].kind & NODE_MASK) != 0)
						? m.paramMapping : INT_0;
					m.matcher = src.createMatcher (s, predConsts, neededConstantsOut);
					matchers.add (m);
				}
			}
			if (!notMatched.remove (p.index))
			{
				throw new AssertionError ();
			}
			if (p.matcher != NULL_MATCHER)
			{
				matchers.add (p);
				int n = p.predicate.getParameterCount ();
				int tc = 0, nc = 0;
				neededConstantsOut.setSize (ncoBase + 2 * n);
				for (int j = n - 1; j >= 0; j--)
				{
					int t = p.paramMapping[j];
					if (constants.add (t))
					{
						if (params[t].type.getTypeId () == TypeId.OBJECT)
						{
							neededConstantsOut.set (ncoBase + tc++, t);
						}
						if ((params[t].kind & NODE_MASK) != 0)
						{
							neededConstantsOut.set (ncoBase + n + nc++, t);
							if (params[t].type.getTypeId () != TypeId.OBJECT)
							{
								throw new AssertionError ("Nodes have to be Objects");
							}
						}
					}
				}
				int[] a;
				if (tc > 0)
				{
					a = new int[tc];
					System.arraycopy (neededConstantsOut.elements, ncoBase,
									  a, 0, tc);
				}
				else
				{
					a = INT_0;
				}
				p.aparamsBound = a;
				if (nc > 0)
				{
					a = new int[nc];
					System.arraycopy (neededConstantsOut.elements, ncoBase + n,
									  a, 0, nc);
				}
				else
				{
					a = INT_0;
				}
				p.nodesBound = a;
			}
		}
		neededConstantsOut.setSize (ncoBase);

		int[] tk = new int[params.length];
		Type[] tt = new Type[params.length];
		int[] folding = new int[params.length];
		for (int i = params.length - 1; i >= 0; i--)
		{
			tk[i] = params[i].kind;
			tt[i] = params[i].type;
			folding[i] = (params[i].folded == null) ? i : params[i].folded.get (0).index;
		}
		
		//make linked-list of matchers from ObjectList. head of linked-list is root
		Matcher next = null;
		for (int i = matchers.size () - 1; i >= 0; i--)
		{
			Matcher p = (Matcher) matchers.get (i);
			if ((next == null) && (predType == BREAKING))
			{
				p.breaking = true;
			}
			p.kinds = tk;
			p.types = tt;
			p.folding = folding;
			p.next = next;
			p.indexInOrder2 = 2 * i;
			next = p;
		}
		Matcher p = new Matcher (root.optTime, root.optMatchCount);
		
		if ((next == null) && (predType == BREAKING))
		{
			p.breaking = true;
		}
		p.predicate = this;
		p.kinds = tk;
		p.types = tt;
		p.folding = folding;
		p.next = next;
		p.predicateCount = matchers.size ();
		p.nodesBound = p.aparamsBound = INT_0;
		p.continueLabel = continueLabel;
		return p;
	}

	@Override
	public boolean isDeleting ()
	{
		for (int i = 0; i < predicates.length; i++)
		{
			if (predicates[i].isDeleting ())
			{
				return true;
			}
		}
		return false;
	}

	@Override
	protected String paramString ()
	{
		if (paramKinds.length > 0)
		{
			getParameterKind (0);
		}
		return super.paramString () + ",kinds=" + Arrays.toString (paramKinds)
			+ ",dir=" + direction + ",type=" + predType;
	}

}
