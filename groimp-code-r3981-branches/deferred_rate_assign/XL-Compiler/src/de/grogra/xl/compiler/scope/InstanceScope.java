
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
import de.grogra.reflect.Member;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.xl.expr.Block;
import de.grogra.xl.expr.Expression;
import de.grogra.xl.expr.ExpressionFactory;

public class InstanceScope extends BlockScope implements ExpressionFactory
{
	final boolean shadowsEnclosing;
	private ExpressionFactory instance;
	private final int memberFlags;

	
	public InstanceScope (BlockScope enclosing)
	{
		this (enclosing, new Block (), null,
			  Members.FIELD | Members.METHOD, true);
	}


	InstanceScope (BlockScope enclosing, Expression block, ExpressionFactory instance,
				   int memberFlags, boolean shadowsEnclosing)
	{
		super (enclosing, block);
		this.instance = instance;
		this.shadowsEnclosing = shadowsEnclosing;
		this.memberFlags = memberFlags;
	}

	public ExpressionFactory getInstance ()
	{
		return instance;
	}

	public Type getType ()
	{
		return instance.getType ();
	}

	public Expression createExpression (Scope scope, AST pos)
	{
		return instance.createExpression (scope, pos);
	}

	
	public void setInstance (ExpressionFactory instance)
	{
		this.instance = instance;
	}


	@Override
	public void findMembers (String name, int flags, Members list)
	{
		if (((flags & Members.EXCLUDE_INSTANCE_SCOPES) == 0)
			&& ((flags & memberFlags & Members.MEMBER_MASK) != 0))
		{
			TypeScope.findMembers (instance.getType (), null, name,
								   flags & memberFlags, list, this);
		}
		if ((flags & Members.INCLUDE_FIRST_INSTANCE_SCOPE) != 0)
		{
			flags |= Members.EXCLUDE_INSTANCE_SCOPES;
		}
		super.findMembers (name, flags, list);
	}


	@Override
	public Type getOwnerOf (Member m)
	{
		return Reflection.isMember (m, instance.getType ())
			? instance.getType () : super.getOwnerOf (m);		
	}
	

	@Override
	public String toString ()
	{
		return super.toString () + '[' + instance + ']';
	}

}
