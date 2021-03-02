
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

public class ASTWithToken extends antlr.CommonAST
{
	public antlr.Token token = null;


	public ASTWithToken ()
	{
		super ();
	}


	public ASTWithToken (int type, String text)
	{
		super ();
		initialize (type, text);
	}


	@Override
	public void initialize (antlr.Token token)
	{
		super.initialize (token);
		this.token = token;
	}


	@Override
	public void initialize (antlr.collections.AST t)
	{
		super.initialize (t);
		if (t instanceof ASTWithToken)
		{
			token = ((ASTWithToken) t).token;
		}
	}

	
	public ASTWithToken add (antlr.collections.AST child)
	{
		addChild (child);
		return this;
	}

	
	public ASTWithToken add (int type, String text)
	{
		return add (new ASTWithToken (type, text));
	}


	public ASTWithToken dup ()
	{
		ASTWithToken t = new ASTWithToken ();
		t.initialize (this);
		return t;
	}
	
	
	@Override
	public int getLine ()
	{
		return (token != null) ? (token.getLine () + 1) : 0;
	}

}
