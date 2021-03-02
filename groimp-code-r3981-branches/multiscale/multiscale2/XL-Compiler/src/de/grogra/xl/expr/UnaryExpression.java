
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

package de.grogra.xl.expr;

import de.grogra.reflect.*;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Scope;

public abstract class UnaryExpression extends ConstExpression
{
	protected Expression expr;


	public UnaryExpression (Type type)
	{
		super (type);
	}


	public UnaryExpression ()
	{
		super ();
	}


	public abstract int getSupportedTypes ();


	@Override
	public final int getSupportedTypes (int arg)
	{
		return (arg == 0) ? getSupportedTypes () : 0;
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (1);
		expr = getExpression (0, etype, checkTypes);
	}


	@Override
	public Expression compile (Scope scope, Expression e1)
	{
		e1 = e1.promote (scope, getSupportedTypes ());
		return setType (e1.getType ()).add (e1);
	}


	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		writer.visitInsn (opcode (getOpcodes ()));
	}

	
	protected int[] getOpcodes ()
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}

}
