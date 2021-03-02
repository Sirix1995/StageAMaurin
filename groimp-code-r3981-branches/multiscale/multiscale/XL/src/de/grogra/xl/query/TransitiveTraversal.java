
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

import de.grogra.reflect.Type;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.LongList;
import de.grogra.xl.util.XBitSet;

public final class TransitiveTraversal extends BuiltInPattern
{

	final Pattern relation;
	final short[] paramMapping;
	final String identifier;

	
	private static Type[] getTypes (Pattern relation, short[] paramMapping)
	{
		Type[] t = new Type[paramMapping.length + 2];
		for (int i = paramMapping.length - 1; i >= 0; i--)
		{
			t[i] = relation.getParameterType (paramMapping[i]);
		}
		t[paramMapping.length] = Type.LONG;
		t[paramMapping.length + 1] = Type.LONG;
		return t;
	}


	public TransitiveTraversal (String id, Pattern relation, short[] paramMapping)
	{
		super (getTypes (relation, paramMapping),
			   paramMapping.length + 2);
		this.identifier = id.intern ();
		this.relation = relation;
		this.paramMapping = paramMapping;
	}


	public void write (BytecodeSerialization out) throws java.io.IOException
	{
		out.beginMethod (Utils.getConstructor (this));
		out.visitObject (identifier);
		relation.write (out);
		Utils.writeArray (out, paramMapping);
		out.endMethod ();
	}


	@Override
	public Matcher createMatcher (Graph src, XBitSet providedConstants,
								  IntList neededConstantsOut)
	{
		final int minMatchIndex = paramMapping.length;
		if (!providedConstants.get (minMatchIndex))
		{
			neededConstantsOut.add (minMatchIndex);
		}
		if (!providedConstants.get (minMatchIndex + 1))
		{
			neededConstantsOut.add (minMatchIndex + 1);
		}
		for (int i = paramMapping.length - 1; i >= 2; i--)
		{
			if (!providedConstants.get (i))
			{
				neededConstantsOut.add (i);
			}
		}
		int bits = providedConstants.getBits (0);
		XBitSet c = (relation.getParameterCount () <= 32)
			? providedConstants : new XBitSet ();
		for (int i = relation.getParameterCount () - 1; i >= 0; i--)
		{
			c.set (i, false);
		}
		for (int i = paramMapping.length - 1; i >= 0; i--)
		{
			c.set (paramMapping[i], true);
		}
		int neededSizeOld = neededConstantsOut.size ();
		int needed = -1;
		final Matcher rm;
		final boolean forward;
		switch (bits & 3)
		{
			case 1:
				c.set (paramMapping[1], false);
				forward = true;
				rm = src.createMatcher (relation, c, neededConstantsOut);
				break;
			case 2:
				c.set (paramMapping[0], false);
				forward = false;
				rm = src.createMatcher (relation, c, neededConstantsOut);
				break;
			case 0:
			case 3:
				c.set (paramMapping[0], false);
				Matcher m0 = src.createMatcher (relation, c, neededConstantsOut);
				int n0 = neededConstantsOut.size ();
				int s0 = n0 - neededSizeOld;
				for (int i = n0 - 1; i >= neededSizeOld; i--)
				{
					if (neededConstantsOut.get (i) == paramMapping[0])
					{
						neededConstantsOut.removeAt (i);
						n0--;
						break;
					}
				}
				c.set (paramMapping[0], true);
				c.set (paramMapping[1], false);
				Matcher m1 = src.createMatcher (relation, c, neededConstantsOut);
				int s1 = neededConstantsOut.size () - n0;
				for (int i = neededConstantsOut.size () - 1; i >= n0; i--)
				{
					if (neededConstantsOut.get (i) == paramMapping[1])
					{
						neededConstantsOut.removeAt (i);
						break;
					}
				}
				forward = (m1 != null)
					&& ((m0 == null) || (s1 < s0) || (m1.compareTo (m0) < 0));
				rm = forward ? m1 : m0;
				if (forward)
				{
					while (--n0 >= neededSizeOld)
					{
						neededConstantsOut.removeAt (n0);
					}
					if ((bits & 3) == 0)
					{
						needed = 0;
					}
				}
				else
				{
					neededConstantsOut.setSize (n0);
					if ((bits & 3) == 0)
					{
						needed = 1;
					}
				}
				break;
			default:
				throw new AssertionError ();
		}
		providedConstants.setBits (0, bits);
		if ((rm == null) || (neededSizeOld != neededConstantsOut.size ()))
		{
			return null;
		}
		if (needed >= 0)
		{
			neededConstantsOut.add (needed);
		}
		final int from = forward ? 0 : 1, to = forward ? 1 : 0,
			fromTerm = paramMapping[from], toTerm = paramMapping[to];
		final Type fromType = relation.getParameterType (fromTerm);

		final class MatcherImpl extends Matcher implements MatchConsumer
		{
			private static final int MSP = 0;
			private static final int ARG = 1;
			private static final int MIN = 0;
			private static final int MAX = 1;
			private static final int COUNT = 2;
			private static final int CONSUMER = 0;
			private static final int STACK_SIZE = 3;
		
			private static final int PK_VAR = -1;
			private static final int PK_FROM = -2;

			private final int[] paramKinds;
			private final int varSize, paramCount;


			MatcherImpl ()
			{
				super (5 * rm.getBranchingFactor ());
				int n = relation.getParameterCount ();
				paramCount = n;
				paramKinds = new int[n];
				for (int i = n - 1; i >= 0; i--)
				{
					paramKinds[i] = PK_VAR;
				}
				for (int i = paramMapping.length - 1; i >= 0; i--)
				{
					paramKinds[paramMapping[i]] = i;
				}
				paramKinds[fromTerm] = PK_FROM;
				paramKinds[toTerm] = PK_VAR;
				varSize = n - paramMapping.length + 1;
			}

			
			@Override
			public void findMatches
				(QueryState qs, MatchConsumer consumer, int arg)
			{
				long min = qs.lbound (minMatchIndex),
					max = qs.lbound (minMatchIndex + 1);
				if (min == 0)
				{
					try
					{
						qs.amatch (to, qs.abound (from), consumer, arg);
					}
					catch (QueryState.Break e)
					{
						qs.check (e, identifier, -1);
						return;
					}
				}
				if (((max < 0) || (max > min) || ((max == min) && (min > 0)))
					&& fromType.isInstance (qs.abound (from)))
				{
					int msp = qs.getVariables ();
					int qsp = qs.enter (STACK_SIZE);
					int mapping = qs.newVariables (paramCount);
					IntList istack = qs.istack;
					LongList lstack = qs.lstack;
					istack.set (MSP + qsp, msp);
					lstack.set (MIN + qsp, min);
					lstack.set (MAX + qsp, max);
					lstack.set (COUNT + qsp, 1);
					qs.astack.set (CONSUMER + qsp, consumer);
					istack.set (ARG + qsp, arg);
					for (int i = 0; i < paramCount; i++)
					{
						switch (paramKinds[i])
						{
							case PK_VAR:
								qs.addVariable ();
								break;
							case PK_FROM:
								qs.mapVariable (from);
								break;
							default:
								qs.mapVariable (paramKinds[i]);
								break;
						}
					}
					qs.setVariables (mapping);
					try
					{
						rm.findMatches (qs, this, qsp);
					}
					finally
					{
						qs.setVariables (msp);
						qs.leave (qsp);
					}
				}
			}
			

			public void matchFound (QueryState qs, int qsp)
			{
				long[] lstack = qs.lstack.elements;
				long c = lstack[COUNT + qsp];
				if (c >= lstack[MIN + qsp])
				{
					int p = qs.getVariables ();
					try
					{
						Object o = qs.abound (toTerm);
						qs.setVariables (qs.istack.get (MSP + qsp));
						qs.amatch (to, o, (MatchConsumer) qs.astack.get (CONSUMER + qsp),
								   qs.istack.get (ARG + qsp));
					}
					catch (QueryState.Break e)
					{
						qs.check (e, identifier, -1);
						return;
					}
					finally
					{
						qs.setVariables (p);
					}
					lstack = qs.lstack.elements;
				}
				if (((lstack[MAX + qsp] < 0) || (c < lstack[MAX + qsp]))
					&& fromType.isInstance (qs.abound (toTerm)))
				{
					++lstack[COUNT + qsp];
					int sp = qs.enter (0);
					int mapping = qs.newVariables (paramCount);
					for (int i = 0; i < paramCount; i++)
					{
						switch (paramKinds[i])
						{
							case PK_VAR:
								qs.addVariable ();
								break;
							case PK_FROM:
								qs.mapVariable (toTerm);
								break;
							default:
								qs.mapVariable (i);
								break;
						}
					}
					int p = qs.getVariables ();
					try
					{
						qs.setVariables (mapping);
						rm.findMatches (qs, this, qsp);
					}
					finally
					{
						qs.setVariables (p);
						qs.leave (sp);
					}
					--qs.lstack.elements[COUNT + qsp];
				}
			}
			
			
			@Override
			public String toString ()
			{
				return "Matcher for " + TransitiveTraversal.this
					+ "[\nforward=" + forward + ", matcher=" + rm + "\n]";
			}
		}
		
		return new MatcherImpl ();
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ',' + identifier + ','
			+ Arrays.toString (paramMapping);
	}


	@Override
	public int getParameterKind (int index)
	{
		return (index < 2) ? 0 : INPUT_MASK;
	}

}
