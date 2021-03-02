
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

package de.grogra.xl.compiler.scope;

import antlr.collections.AST;
import java.util.*;
import java.util.Map;
import de.grogra.reflect.*;
import de.grogra.xl.expr.Expression;
import de.grogra.xl.expr.Block;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.compiler.Compiler;

/**
 * A <code>BlockScope</code> represents a scope for local variables.
 *
 * @author Ole Kniemeyer
 */
public class BlockScope extends Scope
{
	private final HashMap<String,Local> locals = new HashMap<String,Local> (20);
	private Expression block;
	private boolean newScopeForQueries;
	private BlockScope scopeForLocals;
	private final ObjectList localClassScopes = new ObjectList ();
	private final ProduceScope produceScope;


	public BlockScope (Scope enclosing)
	{
		this (enclosing, new Block ());
	}


	public BlockScope (Scope enclosing, boolean seq)
	{
		this (enclosing, seq ? Block.createSequentialBlock () : new Block ());
	}


	public BlockScope (Scope enclosing, Expression block)
	{
		super (enclosing);
		setBlock (block);
		produceScope = ProduceScope.get (this);
	}

	
	public ProduceScope getProduceScope ()
	{
		return produceScope;
	}

	public void setUseNewScopeForQueries (boolean newScope)
	{
		newScopeForQueries = newScope;
	}
	
	
	public boolean useNewScopeForQueries ()
	{
		return newScopeForQueries;
	}

	
	public void setScopeForLocals (BlockScope scope)
	{
		this.scopeForLocals = scope;
	}


	@Override
	public void findMembers (String name, int flags, Members list)
	{
		if ((flags & Members.LOCAL) != 0)
		{
			Local l = locals.get (name);
			if (l != null)
			{
				list.add (l, this, flags);
				flags &= ~Members.LOCAL;
			}
		}
		if ((flags & Members.TYPE) != 0)
		{
			for (int i = localClassScopes.size () - 1; i >= 0; i--)
			{
				if (name.equals (((TypeScope) localClassScopes.get (i)).getDeclaredType ().getSimpleName ()))
				{
					list.add (((TypeScope) localClassScopes.get (i)).getDeclaredType (), this, flags);
					flags &= ~(Members.TYPE | Members.PREDICATE);
					break;
				}
			}
		}
		if ((flags & Members.MEMBER_MASK) != 0)
		{
			super.findMembers (name, flags, list);
		}
	}


	public Local findLocal (String name, boolean includeNonlocal)
	{
		BlockScope s = this;
		while (true)
		{
			Local l = s.locals.get (name);
			if (l != null)
			{
				return l;
			}
			Scope e = s.getEnclosingScope ();
			while (!(e instanceof BlockScope))
			{
				if (!includeNonlocal)
				{
					return null;
				}
				e = e.getEnclosingScope ();
				if (e == null)
				{
					return null;
				}
			}
			s = (BlockScope) e;
		}
	}


	public XClass findClass (String name)
	{
		BlockScope s = this;
		while (true)
		{
			for (int i = s.localClassScopes.size () - 1; i >= 0; i--)
			{
				TypeScope ts = (TypeScope) localClassScopes.get (i);
				if (name.equals (ts.getDeclaredType ().getSimpleName ()))
				{
					return ts.getDeclaredType ();
				}
			}
			Scope e = s.getEnclosingScope ();
			if (!(e instanceof BlockScope))
			{
				return null;
			}
			s = (BlockScope) e;
		}
	}


	public TypeScope getTypeScope (Type localClass)
	{
		BlockScope s = this;
		while (true)
		{
			for (int i = s.localClassScopes.size () - 1; i >= 0; i--)
			{
				TypeScope ts = (TypeScope) s.localClassScopes.get (i);
				if (ts.getDeclaredType ().getBinaryName ().equals (localClass.getBinaryName ()))
				{
					return ts;
				}
			}
			Scope e = s.getEnclosingScope ();
			while (!(e instanceof BlockScope))
			{
				e = e.getEnclosingScope ();
			}
			s = (BlockScope) e;
		}
	}


	public void declareLocalClass (TypeScope cls)
	{
		cls.getClass ();
		localClassScopes.add (cls);
	}
	
	
	public int getDeclaredClassCount ()
	{
		return localClassScopes.size ();
	}

	
	public void setDeclaredClassCount (int c)
	{
		if (c > localClassScopes.size ())
		{
			throw new IllegalArgumentException ();
		}
		localClassScopes.setSize (c);
	}

	
	public void addExpression (Expression e)
	{
		block.add (e);
	}

	
	public void prependExpression (Expression e)
	{
		block.insertBranchNode (0, e);
	}


	public Expression getBlock ()
	{
		return block;
	}


	public Expression setBlock (Expression block)
	{
		if (block != null)
		{
			if (this.block != null)
			{
				block.appendBranchNode (this.block.getFirstExpression ());
				this.block.removeAll (null);
			}
			this.block = block;
			Compiler.setBlockScope (block, this);
		}
		return block;
	}


	public String nextUniqueId ()
	{
		MethodScope ms = MethodScope.get (this);
		return ms.getMethod ().getRoutineId () + '@' + ms.nextUniqueInt ();
	}


	public final String getUniqueName (String name)
	{
		return (name.charAt (name.length () - 1) == '.')
			? name + getMethodScope ().nextUniqueInt ()
			: name + '.' + getMethodScope ().nextUniqueInt ();
	}


	public final Local declareLocal (String name, long modifiers, Type type,
									 AST pos)
	{
		if (scopeForLocals != null)
		{
			return scopeForLocals.declareLocal (name, modifiers, type, pos);
		}
		if (name.charAt (name.length () - 1) == '.')
		{
			name = getUniqueName (name);
			modifiers |= Member.SYNTHETIC;
		}
		Local l = new Local (name, modifiers, type, this, pos);
		locals.put (name, l);
		return l;	
	}


	void printLocals ()
	{
		System.out.println (locals.values ());
	}

/*
	public final int getLocalCount ()
	{
		return locals.size ();
	}


	public final Local getLocal (int index)
	{
		return locals.getValue (index);
	}
*/


	public MethodScope getMethodScope ()
	{
		return MethodScope.get (this);
	}


	public Expression createThis ()
	{
		return getMethodScope ().createThis ();
	}

	
	public void receiveLocals (BlockScope src, AST behind)
	{
		if (src == this)
		{
			return;
		}
		for (Iterator i = src.locals.entrySet ().iterator (); i.hasNext (); )
		{
			Map.Entry e = (Map.Entry) i.next ();
			Local l = (Local) e.getValue ();
			if ((behind == null) || l.isDeclaredBehind (behind))
			{
				i.remove ();
				l.scope = this;
				assert !locals.containsKey (l.getSimpleName ());
				locals.put (l.getSimpleName (), l);
			}
		}
	}

	
	public Collection<Local> getLocals ()
	{
		return locals.values ();
	}

	void dispose ()
	{
		locals.clear ();
		block.removeAll (null);
		block = null;
		scopeForLocals = null;
		localClassScopes.clear ();
		insert (null);
	}
}
