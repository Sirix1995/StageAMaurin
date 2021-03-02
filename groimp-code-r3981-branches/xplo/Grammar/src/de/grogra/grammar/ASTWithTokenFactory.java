
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

package de.grogra.grammar;

import antlr.collections.AST;

public class ASTWithTokenFactory extends antlr.ASTFactory
{
	@Override
	public AST create ()
	{
		return create (antlr.Token.INVALID_TYPE, "");
	}


	@Override
	public AST create (int type)
	{
		return create (type, "");
	}


	@Override
	public AST create (int type, String text)
	{
		ASTWithToken t = new ASTWithToken ();
		t.initialize (type, text);
		return t;
	}


	@Override
	public AST create (AST t)
	{
		return (t == null) ? null : create (t.getType (), "");
	}


	@Override
	public AST create (antlr.Token token)
	{
		ASTWithToken t = new ASTWithToken ();
		t.initialize (token);
		return t;
	}


	@Override
	public AST dup (AST t)
	{
		return (t == null) ? null : ((ASTWithToken) t).dup ();
	}


	@Override
	public AST create (int type, String text, String className)
	{
		throw new AssertionError ();
	}


	@Override
	public AST create (antlr.Token token, String className)
	{
		throw new AssertionError ();
	}


	@Override
	protected AST createUsingCtor (antlr.Token token, String className)
	{
		throw new AssertionError ();
	}


	@Override
	protected AST create (Class cls)
	{
		throw new AssertionError ();
	}
}
