
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

import java.util.*;
import de.grogra.reflect.*;
import de.grogra.xl.expr.*;
import de.grogra.xl.query.*;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.compiler.scope.*;
import antlr.collections.AST;

public abstract class PatternData extends Component
{
	final AST label;
	final AST pos;
	final PatternBuilder builder;
	final int dataId;
	final int textualPosition;
	final int level;

	final int inKind;
	final int outKind;

	boolean context;
	
	PatternData prev, next;
	
	int index;
	ObjectConst targetTypeExpr;

	HashSet uses;
	HashSet assigns;

	boolean atomic;

	private final ObjectList<PatternData> followers = new ObjectList<PatternData> ();
	
	private Block block;

	
	PatternData (AST label, int inKind, int outKind, AST pos,
				   int textualPosition, int level, PatternBuilder builder)
	{
		super (builder.problems);
		this.label = label;
		this.pos = pos;
		this.dataId = builder.nextPatternId ();
		this.builder = builder;
		this.inKind = inKind;
		this.outKind = outKind;
		this.textualPosition = textualPosition;
		this.level = level;
	}

	
	abstract boolean hasInPlace ();

	
	abstract boolean hasOutPlace ();


	abstract Place getInPlace (boolean force);
	
	
	abstract Place getOutPlace (boolean force);
	
	
	abstract int getLabelArgument ();
	
	
	abstract Type getLastNodeType ();


	public abstract Pattern getPattern ();


	boolean isOutClosed ()
	{
		return outKind == PatternBuilder.CLOSED_ARGUMENT;
	}


	boolean isInClosed ()
	{
		return inKind == PatternBuilder.CLOSED_ARGUMENT;
	}


	void addFollower (PatternData pred)
	{
		pred.addDependency (this);
		if (!followers.contains (pred))
		{
			if (!followers.isEmpty ())
			{
				pred.addDependency (followers.peek (1));
			}
			followers.add (pred);
		}
		atomic = true;
	}

	
	void computeAllFollowers ()
	{
		int n = followers.size ();
		for (int j = 0; j < n; j++)
		{
			PatternData f = followers.get (j);
			f.computeAllFollowers ();
			for (int i = 0; i < f.followers.size (); i++)
			{
				followers.addIfNotContained (f.followers.get (i));
			}			
		}
	}

	
	void convertFollowersToDependencies ()
	{
		while (!followers.isEmpty ())
		{
			PatternData f = followers.pop ();
			for (int i = 0; i < f.dependsOn.size (); i++)
			{
				PatternData d = f.dependsOn.get (i);
				if ((d != this) && !followers.contains (d))
				{
					addDependency (d);
				}
			}
		}
	}
	
	
	final void addRoutine (MethodScope routine)
	{
		if (block == null)
		{
			block = new Block ();
		}
		block.add (routine.getMethod ());
	}

	
	Block getSubRoutines ()
	{
		return null;
	}


	public final Block getAllRoutines ()
	{
		Block s = getSubRoutines ();
		if (block == null)
		{
			return s;
		}
		if (s == null)
		{
			return block;
		}
		Block b = new Block ();
		b.add (s);
		b.add (block);
		return b;
	}


	abstract Place[] getPlaces ();

	
	short[] completeArguments (ObjectList parentTerms)
	{
		Place[] p = getPlaces ();
		short[] m = (parentTerms != null) ? new short[p.length] : null;
		for (int j = 0; j < p.length; j++)
		{
			Argument t = getArgument (j);
			if (m != null)
			{
				m[j] = (short) parentTerms.indexOf (t);
			}
			if (assigns == null)
			{
				assigns = new HashSet ();
			}
			assigns.add (t.local);
		}
		return m;
	}

	
	void getArguments (ObjectList<? super Argument> set)
	{
		for (int j = getPlaces ().length - 1; j >= 0; j--)
		{
			set.addIfNotContained (getArgument (j));
		}
	}


	void computeDependenciesFromLocalAccess (ObjectList preds)
	{
		int n = preds.size ();
		if (assigns != null)
		{
			for (Iterator it = assigns.iterator (); it.hasNext (); )
			{
				Local a = (Local) it.next ();
				if (!a.isVariable (builder))
				{
					for (int j = 0; j < n; j++)
					{
						PatternData o = (PatternData) preds.get (j);
						if (o.textualPosition < textualPosition)
						{
							if ((o.uses != null) && o.uses.contains (a))
							{
								addDependency (o); 
							}
						}
					}
				}
			}
		}
		if (uses != null)
		{
			for (Iterator it = uses.iterator (); it.hasNext (); )
			{
				Local a = (Local) it.next ();
				if (!a.isVariable (builder))
				{
					for (int j = 0; j < n; j++)
					{
						PatternData o = (PatternData) preds.get (j);
						if (o.textualPosition < textualPosition)
						{
							if ((o.assigns != null) && o.assigns.contains (a))
							{
								addDependency (o); 
							}
						}
					}
				}
			}
		}
	}


	void setLocalAccess (HashSet used, HashSet assigned)
	{
		if (!used.isEmpty ())
		{
			uses = used;
		}
		if (!assigned.isEmpty ())
		{
			assigns = assigned;
		}
	}


	void addLocalAccess (ObjectList predicates)
	{
		for (int i = 0; i < predicates.size (); i++)
		{
			addLocalAccess ((PatternData) predicates.get (i));
		}
	}


	void addLocalAccess (PatternData c)
	{
		if ((c.assigns != null) && !c.assigns.isEmpty ())
		{
			if (assigns == null)
			{
				assigns = new HashSet ();
			}
			assigns.addAll (c.assigns);
		}
		if ((c.uses != null) && !c.uses.isEmpty ())
		{
			if (uses == null)
			{
				uses = new HashSet ();
			}
			uses.addAll (c.uses);
		}
	}

	
	Argument getArgument (int index)
	{
		return getPlaces ()[index].getMapping (this, index).getArgument ();
	}
	

	SimplePatternData addExpression (Expression e, Type type)
	{
		Local t = builder.declareAuxVariable (type);
		SimplePatternData d = builder.addExpression
			(null, e, pos, true, new ArgumentDescription (pos, t), textualPosition);
		addDependency (d);
		return d;
	}


	abstract void createSubPatterns ();
	

	abstract void mapLabeledArgs ();


	abstract void mapUnlabeledArgs ();


}
