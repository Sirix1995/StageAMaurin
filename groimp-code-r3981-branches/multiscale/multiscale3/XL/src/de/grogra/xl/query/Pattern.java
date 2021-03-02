
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

import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Method;
import de.grogra.reflect.Type;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.XBitSet;

public abstract class Pattern implements BytecodeSerialization.Serializable
{
	public static final Matcher NULL_MATCHER = new Matcher (0.95f)
	{
		@Override
		public void findMatches
			(QueryState qs, MatchConsumer consumer, int arg)
		{
			consumer.matchFound (qs, arg);
		}
		
		@Override
		protected boolean isLessThanOverride (Matcher o)
		{
			return true;
		}

		@Override
		public float getBaseCosts ()
		{
			return 0;
		}
	};


	public static final int NODE_MASK = 1;
	public static final int CONTEXT_MASK = 2;
	public static final int INPUT_MASK = 4;
	public static final int OUTPUT_MASK = 8;

	private final Type[] parameterTypes;
	private final int declaredParameterCount;
	

	public static abstract class Matcher implements Comparable
	{

		private final float branchingFactor;

		
		public Matcher (float branchingFactor)
		{
			this.branchingFactor = branchingFactor;
		}
		
		
		public final float getBranchingFactor ()
		{
			return branchingFactor;
		}
		
		
		public float getBaseCosts ()
		{
			return 1;
		}

		
		public final int compareTo (Object o)
		{
			Matcher m = (Matcher) o;
			if (isLessThanOverride (m))
			{
				return -1;
			}
			if (m.isLessThanOverride (this))
			{
				return 1;
			}
			float c = getBranchingFactor () - m.getBranchingFactor ();
			return (c < 0) ? -1 : (c > 0) ? 1 : 0;
		}
	
		
		protected boolean isLessThanOverride (Matcher o)
		{
			return false;
		}

		
		public abstract void findMatches (QueryState qs, MatchConsumer consumer, int arg);


		public void visitMatch (QueryState qs, Producer p)
		{
		}

	}


	protected Pattern (Type[] termTypes, int declaredTermCount)
	{
		this.parameterTypes = termTypes;
		this.declaredParameterCount = declaredTermCount;
	}
	
	
	Pattern (Type cls)
	{
		assert this instanceof UserDefinedPattern;
		if (cls == null)
		{
			cls = ClassAdapter.wrap (getClass (), false);
		}
		Method sig = UserDefinedPattern.findSignatureMethod (cls);
		if (sig == null)
		{
			throw new IncompatibleClassChangeError ("No signature method for " + cls);
		}
		this.parameterTypes = UserDefinedPattern.getSignature (sig, null);
		if (this.parameterTypes == null)
		{
			throw new IncompatibleClassChangeError ("Illegal signature method " + sig);
		}
		this.declaredParameterCount = parameterTypes.length;
	}


	public final int getParameterCount ()
	{
		return parameterTypes.length;
	}
	
	
	public final int getDeclaredParameterCount ()
	{
		return declaredParameterCount;
	}


	public final Type getParameterType (int index)
	{
		return parameterTypes[index];
	}


	public abstract int getParameterKind (int index);
	
	
	public boolean isDeleting ()
	{
		return false;
	}


	@Override
	public String toString ()
	{
		String s = getClass ().getName ();
		return s.substring (s.lastIndexOf ('.') + 1)
			+ '[' + paramString () + "]@"
			+ Integer.toHexString (hashCode ());
	}


	protected String paramString ()
	{
		return String.valueOf (parameterTypes.length);
	}

	
	public abstract Matcher createMatcher
		(Graph graph, XBitSet bound, IntList requiredAsBound);

}
