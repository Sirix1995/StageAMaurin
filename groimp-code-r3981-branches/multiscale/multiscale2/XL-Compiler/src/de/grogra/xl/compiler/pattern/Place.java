
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

import de.grogra.reflect.*;
import de.grogra.xl.query.*;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.compiler.*;
import de.grogra.xl.compiler.scope.*;
import antlr.collections.AST;

public final class Place extends Component
{
	public static final class Mapping
	{
		public final PatternData pred;
		public final int index;

		private Argument arg;

		
		Mapping (PatternData pred, int index)
		{
			this.pred = pred;
			this.index = index;
		}

		Place getPlace ()
		{
			return pred.getPlaces ()[index];
		}
		
		void initParam (Argument param)
		{
			assert this.arg == null;
			param.getClass ();
			this.arg = param;
		}


		public Type getType ()
		{
			return pred.getPattern ().getParameterType (index);
		}
		
		
		public Argument getArgument ()
		{
			return arg;
		}
	}


	final ObjectList<Mapping> mappings = new ObjectList<Mapping> ();
	final Local label;
	
	final ObjectList<Local> foldings = new ObjectList<Local> ();
	final ObjectList<Place> placeFoldings = new ObjectList<Place> ();
	
	private final int placeId;
	private PatternBuilder builder;

	private boolean node = false;
	private boolean notContext = false;
	private boolean hasContext = false;

	private Type firstType;
	private Local wrapperLocal;
	private Argument nodeArg;
	private Place replacement;

	
	Place (Local label, PatternBuilder builder)
	{
		super (builder.problems);
		this.builder = builder;
		this.label = label;
		this.placeId = builder.nextPlaceId ();
	}
	
	
	PatternBuilder getBuilder ()
	{
		return builder;
	}

	
	void setParentBuilder ()
	{
		builder = builder.enclosing;
	}

	
	public void setNode (boolean notCtx, boolean ctx, AST pos)
	{
		notContext |= notCtx;
		hasContext |= ctx;
		if (node)
		{
			return;
		}
		node = true;
		if ((label != null) && builder.getModel ().needsWrapperFor (firstType))
		{
			wrapperLocal = label.getPatternBuilder ().declareWrapper
				(label, builder.compiler.getWrapperTypeFor (firstType, builder.getModel (), pos), pos);
		}
	}
	

	public Mapping map (PatternData pred, int index, AST pos)
	{
		Place o = pred.getPlaces ()[index];
		if (o == null)
		{
			Mapping m = new Mapping (pred, index);
			pred.getPlaces ()[index] = this;
			mappings.add (m);
			if (firstType == null)
			{
				firstType = m.getType ();
			}
			return m;
		}
		else if (o.label == null)
		{
			receive (o, pos);
			return getMapping (pred, index);
		}
		else
		{
			problems.addSemanticError
				(I18N.msg (ProblemReporter.DUPLICATE_PLACE_LABEL, o.label.getName ()), pos);
			return null;
		}
	}
	
	
	private void receive (Place o, AST pos)
	{
		assert o.label == null;
		for (int i = 0; i < o.mappings.size (); i++)
		{
			Mapping m = o.mappings.get (i);
			m.pred.getPlaces ()[m.index] = null;
			map (m.pred, m.index, null);
		}
		o.mappings.clear ();
		if (o.node)
		{
			setNode (o.notContext, o.hasContext, pos);
		}
		o.replacement = this;
	}

	
	public Place resolve ()
	{
		Place p = this;
		while (p.replacement != null)
		{
			p = p.replacement;
		}
		return p;
	}

	
	public void merge (Place o, AST pos)
	{
		if (label == null)
		{
			o.receive (this, pos);
		}
		else if (o.label == null)
		{
			receive (o, pos);
		}
		else
		{
			problems.addSemanticError
				(I18N, ProblemReporter.CONFLICTING_JOIN_TERMS, pos);
		}
	}
	
	
	private Local getVariable (Type type, boolean hasWrapper)
	{
		if (label != null)
		{
			int i1 = label.getType ().getTypeId (), i2 = type.getTypeId ();
			if (i1 == TypeId.OBJECT)
			{
				if ((i2 == TypeId.OBJECT)
					&& (!hasWrapper || builder.getModel ().needsWrapperFor (type)))
				{
					return label;
				}
			}
			else if (((i1 == i2)
					 || ((((1 << i1) | (1 << i2)) & ~TypeId.INT_ASSIGNABLE) == 0)))
			{
				return label;
			}
		}
		return builder.declareAuxVariable (type);
	}

	
	void mapToArguments ()
	{
		int n = mappings.size ();
		if (n == 0)
		{
			return;
		}
		Argument wrapper = null, wrapped = null;
		SimplePatternData wrapperPred = null;
		boolean context = !notContext && hasContext;
		if (context)
		{
			builder.beginContext (null);
		}
		if (node)
		{
			for (int i = 0; i < n; i++)
			{
				Mapping m = mappings.get (i);
				if (builder.getModel ().needsWrapperFor (m.getType ()))
				{
					if (wrapperLocal == null)
					{
						wrapperLocal = builder.declareAuxVariable
							(builder.compiler.getWrapperTypeFor (m.getType (), builder.getModel (), m.pred.pos));
					}
					wrapper = new Argument (this, wrapperLocal);
					wrapped = new Argument (this, getVariable (m.getType (), true));
					wrapperPred = new SimplePatternData
						(null,
						 new WrappedTypePattern (wrapperLocal.getType (), m.getType ()),
						 null, 0, 0, PatternBuilder.NULL_ARGUMENT, PatternBuilder.NULL_ARGUMENT,
						 m.pred.pos, -1, -1, builder);
					builder.add (wrapperPred, false);
					map (wrapperPred, 0, m.pred.pos).initParam (wrapper);
					map (wrapperPred, 1, m.pred.pos).initParam (wrapped);
					break;
				}
			}
		}
		AST pos = null;
		Argument xterm = null, firstNumeric = null, iterm = null, lterm = null,
			fterm = null, dterm = null;
		for (int i = 0; i < n; i++)
		{
			Mapping m = mappings.get (i);
			pos = m.pred.pos;
			Argument t = null;
			int typeId = m.getType ().getTypeId ();
			switch (typeId)
			{
				case TypeId.BYTE:
				case TypeId.SHORT:
				case TypeId.CHAR:
				case TypeId.INT:
					t = iterm;
					break;
				case TypeId.LONG:
					t = lterm;
					break;
				case TypeId.FLOAT:
					t = fterm;
					break;
				case TypeId.DOUBLE:
					t = dterm;
					break;
			}
			if (t == null)
			{
				if ((firstNumeric != null)
					&& (((1 << typeId) & TypeId.NUMERIC_MASK) != 0))
				{
					t = new Argument (this, getVariable (m.getType (), wrapper != null));
					PatternData pred = new SimplePatternData
						(null,
						 new NumericConversionPattern (firstNumeric.getType (), t.getType ()),
						 null, -1, -1,
						 PatternBuilder.NULL_ARGUMENT, PatternBuilder.NULL_ARGUMENT,
						 pos, -1, -1, builder);
					builder.add (pred, false);
					map (pred, 0, pos).initParam (firstNumeric);
					map (pred, 1, pos).initParam (t);
				}
				else if (wrapper != null)
				{
					if (((1 << typeId) & TypeId.NUMERIC_MASK) != 0)
					{
						intersectType (wrapped, m.getType (), pos);
						t = wrapped;
						firstNumeric = t;
					}
					else if (builder.getModel ().needsWrapperFor (m.getType ()))
					{
						intersectType (wrapped, m.getType (), pos);
						intersectType (wrapper, builder.compiler.getWrapperTypeFor (m.getType (), builder.getModel (), pos), pos);
						t = wrapped;
					}
					else
					{
						intersectType (wrapper, m.getType (), pos);
						t = wrapper;
					}
				}
				else
				{
					if (xterm == null)
					{
						xterm = new Argument (this, getVariable (m.getType (), false));
					}
					else
					{
						intersectType (xterm, m.getType (), pos);
					}
					t = xterm;
					if (((1 << typeId) & TypeId.NUMERIC_MASK) != 0)
					{
						firstNumeric = t;
					}
				}
				switch (m.getType ().getTypeId ())
				{
					case TypeId.BYTE:
					case TypeId.SHORT:
					case TypeId.CHAR:
					case TypeId.INT:
						iterm = t;
						break;
					case TypeId.LONG:
						lterm = t;
						break;
					case TypeId.FLOAT:
						fterm = t;
						break;
					case TypeId.DOUBLE:
						dterm = t;
						break;
				}
			}
			m.initParam (t);
			if ((wrapper != null) && (t != wrapper))
			{
				m.pred.addDependency (wrapperPred);
			}
		}
		nodeArg = (wrapper != null) ? wrapper : xterm;
		if (node)
		{
			nodeArg.node = true;
			nodeArg.context = context;
			intersectType (nodeArg, builder.getModel ().getNodeType (), pos);
		}
		if (context)
		{
			builder.endContext (null);
		}
	}


	private void intersectType (Argument term, Type type, AST pos)
	{
		if (term.getType ().getTypeId () == TypeId.OBJECT)
		{
			builder.compiler.intersect
				((IntersectionType) term.getType (), type, pos);
		}
		else
		{
			int m0 = 1 << term.getType ().getTypeId (),
				m1 = 1 << type.getTypeId ();
			if ((m0 != m1) && (((m0 | m1) & ~TypeId.INT_ASSIGNABLE) != 0))
			{
				problems.addSemanticError
					(I18N.msg (ProblemReporter.INCOMPATIBLE_TYPES, term.getType ().getName (), type.getName ()), pos);
			}
		}
	}


	public Mapping getMapping (PatternData pred, int index)
	{
		for (int i = 0; i < mappings.size (); i++)
		{
			Mapping m = mappings.get (i);
			if ((m.pred == pred) && (m.index == index))
			{
				return m;
			}
		}
		return null;
	}
	
	
	void getArguments (ObjectList set)
	{
		for (int j = 0; j < mappings.size (); j++)
		{
			set.addIfNotContained (mappings.get (j).arg);
		}
	}

	
	Argument getArgument (int typeId)
	{
		for (int j = 0; j < mappings.size (); j++)
		{
			Argument t = mappings.get (j).arg;
			if (t.getType ().getTypeId () == typeId)
			{
				return t;
			}
		}
		return null;
	}


	public Argument getNodeArgument ()
	{
		return nodeArg;
	}


	@Override
	public String toString ()
	{
		return "Place " + placeId;
	}

}
