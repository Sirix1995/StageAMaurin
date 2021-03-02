
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

import de.grogra.reflect.Type;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Scope;

public abstract class Shift extends ConstExpression
{
	protected Expression expr1, expr2;


	@Override
	public int getSupportedTypes (int arg)
	{
		return INT_MASK | LONG_MASK;
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (2);
		expr1 = getExpression (0);
		expr2 = getExpression (1, INT, checkTypes);
	}


	@Override
	public Expression compile (Scope scope, Expression e1, Expression e2)
	{
		e1 = e1.promote (scope, getSupportedTypes (0));
		e2 = e2.promote (scope, getSupportedTypes (1)).cast (Type.INT);
		return setType (e1.getType ()).add (e1).add (e2);
	}


	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		writer.visitInsn (getOpcode ());
	}
	
	
	protected abstract int getOpcode ();

}
