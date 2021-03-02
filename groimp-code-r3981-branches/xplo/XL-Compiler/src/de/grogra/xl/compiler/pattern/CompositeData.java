
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
import de.grogra.xl.compiler.scope.*;
import antlr.collections.AST;

public final class CompositeData extends PatternData
{
	private final Place inPlace, outPlace;
	private EdgeDirection matchDirection;

	private Place[] places;
	private CompoundPattern predicate;
	
	private static final int NO_BREAK = 0;
	private static final int BREAK = 1;
	private static final int BREAK_AND_CONTINUE = 2;
	private static final int OPTIONAL = 3;

	private int predicateType = NO_BREAK;
	private TraversalData brokenPattern;

	
	CompositeData (Place in, int inKind, Place out, int outKind, EdgeDirection matchDirection,
				   AST pos, int textualPosition, int level, PatternBuilder builder)
	{
		super (null, inKind, outKind, pos, textualPosition, level, builder);
		this.inPlace = in;
		this.outPlace = out;
		this.matchDirection = matchDirection;
	}


	public void setBreak (TraversalData traversal)
	{
		predicateType = BREAK_AND_CONTINUE;
		atomic = true;
		matchDirection = EdgeDirection.FORWARD;
		brokenPattern = traversal;
		addDependency (traversal);
	}
	

	public void setBreak ()
	{
		predicateType = BREAK;
		atomic = true;
		matchDirection = EdgeDirection.FORWARD;
	}
	

	public void setOptional ()
	{
		predicateType = OPTIONAL;
		atomic = true;
		matchDirection = EdgeDirection.FORWARD;
	}
	

	@Override
	Type getLastNodeType ()
	{
		return builder.getLastNodeType ();
	}


	@Override
	Block getSubRoutines ()
	{
		Block b = new Block ();
		for (int i = 0; i < builder.predicates.size (); i++)
		{
			b.add (((PatternData) builder.predicates.get (i)).getAllRoutines ());
		}
		return b;
	}


	@Override
	boolean hasInPlace ()
	{
		return inPlace != null;
	}

	
	@Override
	boolean hasOutPlace ()
	{
		return outPlace != null;
	}


	@Override
	Place getInPlace (boolean force)
	{
		return (inPlace != null) ? inPlace.resolve () : null;
	}
	
	
	@Override
	Place getOutPlace (boolean force)
	{
		return (outPlace != null) ? outPlace.resolve () : null;
	}
	
	
	@Override
	int getLabelArgument ()
	{
		return -1;
	}

	
	void complete (CompoundPattern cp, Argument[] mapping)
	{
		this.predicate = cp;
		this.places = new Place[mapping.length];
		for (int i = 0; i < mapping.length; i++)
		{
			mapping[i].place.map (this, i, null).initParam (mapping[i]);
		}
	}


	EdgeDirection getMatchDirection ()
	{
		return matchDirection;
	}
	
	
	int getCompositeType ()
	{
		return ((predicateType == BREAK) || (predicateType == BREAK_AND_CONTINUE))
			? CompoundPattern.BREAKING
			: atomic ? CompoundPattern.ATOMIC : CompoundPattern.SIMPLE;
	}

	
	boolean isOptional ()
	{
		return predicateType == OPTIONAL;
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


	@Override
	public String toString ()
	{
		return "Composite " + dataId;
	}


	public int getIndexOfVariable (Local local)
	{
		for (int i = 0; i < places.length; i++)
		{
			if (places[i].getMapping (this, i).getArgument ().local == local)
			{
				return i;
			}
		}
		return -1;
	}

	
	String getContinueLabel ()
	{
		if (predicateType != BREAK_AND_CONTINUE)
		{
			return null;
		}
		return brokenPattern.getIdentifier ();
	}


	@Override
	void createSubPatterns ()
	{
	}


	@Override
	void mapLabeledArgs ()
	{
	}


	@Override
	void mapUnlabeledArgs ()
	{
	}

}
