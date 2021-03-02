
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

package de.grogra.xl.compiler.pattern;

import de.grogra.reflect.Type;
import de.grogra.xl.expr.*;
import de.grogra.xl.query.*;
import de.grogra.xl.compiler.*;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.compiler.scope.Local;
import antlr.collections.AST;

public final class SimplePatternData extends PatternData
{
	private final ArgumentDescription[] args;
	private final Place[] places;
	private final int in;
	private final int out;
	
	private final Pattern predicate;

	
	SimplePatternData (AST label, Pattern p, ArgumentDescription[] args, int in, int out,
						 int inKind, int outKind, AST pos, int textualPosition,
						 int level, PatternBuilder builder)
	{
		super (label, inKind, outKind, pos, textualPosition, level, builder);
		if ((args == null) || (args.length < p.getParameterCount ()))
		{
			ArgumentDescription[] d = new ArgumentDescription[p.getParameterCount ()];
			if (args != null)
			{
				System.arraycopy (args, 0, d, 0, args.length);
			}
			args = d;
		}
		this.predicate = p;
		this.args = args;
		this.places = new Place[p.getParameterCount ()];
		this.in = in;
		this.out = out;
	}

	
	@Override
	boolean hasInPlace ()
	{
		return getInArgument () >= 0;
	}

	
	@Override
	boolean hasOutPlace ()
	{
		return getOutArgument () >= 0;
	}


	@Override
	Place getInPlace (boolean force)
	{
		int t = getInArgument ();
		if (t < 0)
		{
			return null;
		}
		Place p = places[t];
		if ((p == null) && force)
		{
			p = builder.getPlace (this, t);
		}
		return p;
	}
	
	
	@Override
	Place getOutPlace (boolean force)
	{
		int t = getOutArgument ();
		if (t < 0)
		{
			return null;
		}
		Place p = places[t];
		if ((p == null) && force)
		{
			p = builder.getPlace (this, t);
		}
		return p;
	}
	

	@Override
	Type getLastNodeType ()
	{
		int t = getOutArgument ();
		return (t < 0) ? builder.nodeType : predicate.getParameterType (t);
	}


	@Override
	int getLabelArgument ()
	{
		if (!isInClosed () || !isOutClosed ()
			|| (inKind == PatternBuilder.NULL_ARGUMENT)
			|| (outKind == PatternBuilder.NULL_ARGUMENT)
			|| (in != out))
		{
			return -1;
		}
		return in;
	}


	@Override
	Place[] getPlaces ()
	{
		return places;
	}

	
	@Override
	public Pattern getPattern ()
	{
		return predicate;
	}


	private int getOutArgument ()
	{
		return (outKind == PatternBuilder.NULL_ARGUMENT) ? -1 : out;
	}


	private int getInArgument ()
	{
		return (inKind == PatternBuilder.NULL_ARGUMENT) ? -1 : in;
	}


	@Override
	public String toString ()
	{
		return "Simple " + dataId + " [" + predicate.getClass ().getName () + ']';
	}


	@Override
	void createSubPatterns ()
	{
		if (args == null)
		{
			return;
		}
		for (int j = 0; j < args.length; j++)
		{
			ArgumentDescription a = args[j];
			if (a != null)
			{
				Expression e = a.expr;
				if (e == null)
				{
					e = a.local.createGet ();
				}
				addExpression (e, predicate.getParameterType (j)).getPlaces ()[0]
					.map (this, j, pos);
			}
		}
	}
	

	@Override
	void mapLabeledArgs ()
	{
		if (args == null)
		{
			return;
		}
		for (int i = 0; i < args.length; i++)
		{
			ArgumentDescription a = args[i];
			if (a != null)
			{
				if (a.place != null)
				{
					a.place.map (this, i, a.ast);
					args[i] = null;
				}
				else if ((a.local != null) && a.local.isVariable (builder))
				{
					builder.getPlace (a.local).map (this, i, a.ast);
					args[i] = null;
				}
				else if (a.expr instanceof OpenArgument)
				{
					AST lbl = Compiler.getAST (a.expr);
					if (lbl != null)
					{
						Local loc = builder.getScope ().findLocal (lbl.getText (), true);
						if ((loc == null) || !loc.isVariable (builder))
						{
							loc = builder.declareVariable (lbl, predicate.getParameterType (i));
						}
						builder.getPlace (loc).map (this, i, lbl);
					}
					args[i] = null;
				}
			}
		}
	}

	
	@Override
	void mapUnlabeledArgs ()
	{
		if (args == null)
		{
			return;
		}
		for (int i = 0; i < args.length; i++)
		{
			builder.getPlace (this, i);
		}
	}

}
