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

public abstract class BinaryExpression extends ConstExpression
{
	protected Expression expr1, expr2;

	public BinaryExpression (Type type)
	{
		super (type);
	}

	public BinaryExpression ()
	{
		super ();
	}

	public abstract int getSupportedTypes ();

	@Override
	public int getSupportedTypes (int arg)
	{
		return ((arg == 0) || (arg == 1)) ? getSupportedTypes () : 0;
	}

	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (2);
		expr1 = getExpression (0, etype, checkTypes);
		expr2 = getExpression (1, etype, checkTypes);
	}


	@Override
	public Expression compile (Scope scope, Expression e1, Expression e2)
	{
		int promotedType = getPromotedType (e1, e2);
		int supported = getSupportedTypes ();
		if ((promotedType >= 0) && (promotedType != OBJECT))
		{
			for (int i = promotedType; Reflection.isWideningConversion (
					promotedType, i); i++)
			{
				if (((1 << i) & supported) != 0)
				{
					Type t = Reflection.getType (i);
					if (getType () == null)
					{
						setType (t);
					}
					add (e1.unboxingConversion ().implicitConversion (scope, t));
					add (e2.unboxingConversion ().implicitConversion (scope, t));
					return this;
				}
			}
		}
		throw new IllegalOperandTypeException (I18N.msg ("expr.illegal-optype",
				getClass ().getName ()));
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
