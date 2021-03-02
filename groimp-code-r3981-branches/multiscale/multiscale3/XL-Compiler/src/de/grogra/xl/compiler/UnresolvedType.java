
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

package de.grogra.xl.compiler;

import antlr.collections.AST;
import de.grogra.reflect.*;
import de.grogra.xl.compiler.scope.*;

class UnresolvedType extends TypeDecorator implements Resolvable
{
	private final Compiler compiler;
	private final AST identifier;
	private final String simpleName;
	private boolean resolving;
	private Scope scope;
	private Type resolved;
	
	
	UnresolvedType (Compiler compiler, AST identifier, Scope scope)
	{
		super (null);
		this.compiler = compiler;
		this.scope = scope;
		this.identifier = identifier;
		AST t = identifier;
		if (t.getType () == CompilerTokenTypes.DOT)
		{
			t = t.getFirstChild ().getNextSibling ();
		}
		simpleName = t.getText ();
	}


	@Override
	public Member getDecoratedMember ()
	{
		if (resolved == null)
		{
			if (resolving)
			{
				throw new Error ("Circularity");
			}
			resolve (new Resolver (compiler));
		}
		return resolved;
	}


	@Override
	public String getSimpleName ()
	{
		return (resolved != null) ? resolved.getSimpleName () : simpleName;
	}

	
	@Override
	public int getTypeId ()
	{
		return TypeId.OBJECT;
	}

	public void resolve ()
	{
		if (resolved == null)
		{
			if (resolving)
			{
				throw new Error ("Circularity");
			}
			resolve (compiler.resolver);
		}
	}


	private void resolve (Resolver r)
	{
		resolving = true;
		resolved = r.resolveTypeName (identifier, scope);
		if (resolved != null)
		{
			scope = null;
		}
	}

}
