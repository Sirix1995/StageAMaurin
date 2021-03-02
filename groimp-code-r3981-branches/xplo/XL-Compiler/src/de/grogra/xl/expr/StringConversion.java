
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

import org.objectweb.asm.Opcodes;

import de.grogra.reflect.*;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;

public final class StringConversion extends ConstExpression
{
	protected Expression expr;


	public StringConversion ()
	{
		super (Type.STRING);
	}


	@Override
	protected Object evaluateObjectImpl (VMXState vm)
	{
		switch (expr.etype)
		{
			case BOOLEAN:
				return String.valueOf (expr.evaluateBoolean (vm));
			case BYTE:
				return String.valueOf (expr.evaluateByte (vm));
			case SHORT:
				return String.valueOf (expr.evaluateShort (vm));
			case CHAR:
				return String.valueOf (expr.evaluateChar (vm));
			case INT:
				return String.valueOf (expr.evaluateInt (vm));
			case LONG:
				return String.valueOf (expr.evaluateLong (vm));
			case FLOAT:
				return String.valueOf (expr.evaluateFloat (vm));
			case DOUBLE:
				return String.valueOf (expr.evaluateDouble (vm));
			case OBJECT:
				return String.valueOf (expr.evaluateObject (vm));
			default:
				throw new AssertionError ();
		}
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (1);
		expr = getExpression (0);
	}


	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		writer.visitMethodInsn
			(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf",
			 '(' + getDescriptorNoBS (expr.etype) + ")Ljava/lang/String;");
	}

}
