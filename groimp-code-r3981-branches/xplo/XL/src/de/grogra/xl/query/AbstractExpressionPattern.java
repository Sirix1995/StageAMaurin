
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

import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.XBitSet;

public abstract class AbstractExpressionPattern extends BuiltInPattern
{
	public static final int EXPRESSION = 0;
	public static final int CONDITION = 1;
	public static final int BLOCK = 2;

	
	protected final int type;
	protected final int pathIn;
	protected final int pathOut;
	

	private final class ExprMatcher extends Matcher
	{
		
		ExprMatcher ()
		{
			super ((type == CONDITION) ? 0.5f : 1);
		}

		
		@Override
		protected boolean isLessThanOverride (Matcher o)
		{
			return type == BLOCK;
		}

		
		@Override
		public void findMatches
			(QueryState qs, MatchConsumer consumer, int arg)
		{
			findMatchesImpl (qs, consumer, arg, qs.getFrame ());
		}

	
		@Override
		public String toString ()
		{
			return "Matcher[" + AbstractExpressionPattern.this + ']';
		}
	}

	
	protected AbstractExpressionPattern (Type[] termTypes, int type, int pathIn, int pathOut)
	{
		super (termTypes, termTypes.length);
		this.type = type;
		this.pathIn = pathIn;
		this.pathOut = pathOut;
	}


	@Override
	public int getParameterKind (int index)
	{
		if ((index == 0) && (type == EXPRESSION))
		{
			return OUTPUT_MASK;
		}
		if (type == CONDITION)
		{
			if ((index == pathIn) || (index == pathOut))
			{
				return INPUT_MASK;
			}
		}
		return INPUT_MASK | CONTEXT_MASK;
	}


	@Override
	public Matcher createMatcher
		(Graph src, XBitSet providedConstants, IntList neededConstantsOut)
	{
		for (int i = (type == EXPRESSION) ? 1 : 0; i < getParameterCount (); i++)
		{
			if (!providedConstants.get (i))
			{
				neededConstantsOut.add (i);
			}
		}
		return new ExprMatcher ();
	}


	protected abstract void findMatchesImpl (QueryState qs, MatchConsumer consumer, int arg, Frame frame);

}
