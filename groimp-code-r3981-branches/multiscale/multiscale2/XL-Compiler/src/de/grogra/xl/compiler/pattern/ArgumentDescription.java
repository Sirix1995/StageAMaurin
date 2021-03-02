
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

import de.grogra.xl.expr.*;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.compiler.scope.*;
import antlr.collections.AST;

public final class ArgumentDescription
{
	public final AST ast;
	public final Expression expr;
	public final Local local;
	public final Place place;


	public ArgumentDescription (AST ast, Expression expr)
	{
		expr.getClass ();
		this.ast = (ast != null) ? ast : Compiler.getAST (expr);
		this.expr = expr;
		this.local = (expr instanceof GetLocal) ? ((GetLocal) expr).getLocal (0) : null;
		this.place = null;
	}


	public ArgumentDescription (AST ast, Local local)
	{
		local.getClass ();
		this.ast = ast;
		this.expr = null;
		this.local = local;
		this.place = null;
	}
	

	public ArgumentDescription (AST ast, Place place)
	{
		place.getClass ();
		this.ast = ast;
		this.expr = null;
		this.local = null;
		this.place = place;
	}
	
	
	public static ArgumentDescription[] create (de.grogra.xl.expr.Expression[] args)
	{
		ArgumentDescription[] a = new ArgumentDescription[args.length];
		for (int i = 0; i < args.length; i++)
		{
			a[i] = (args[i] != null) ? new ArgumentDescription (null, args[i]) : null;
		}
		return a;
	}
}
