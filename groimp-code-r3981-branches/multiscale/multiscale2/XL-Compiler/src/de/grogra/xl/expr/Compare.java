
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

import de.grogra.reflect.Type;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;

public strictfp class Compare extends BinaryExpression
{
	
	public Compare ()
	{
		super (Type.INT);
	}


	@Override
	public int getSupportedTypes ()
	{
		return LONG_MASK | FLOAT_MASK | DOUBLE_MASK;
	}


	@Override
	protected int evaluateIntImpl (VMXState vm)
	{
		switch (expr1.etype)
		{
/*!!
#foreach ($type in ["long", "float", "double"])
$pp.setType($type)

			case $pp.TYPE:
			{
				$type a = expr1.evaluate$pp.Type (vm);
				$type b = expr2.evaluate$pp.Type (vm);
				return (a > b) ? 1 : (a == b) ? 0 : -1;
			}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
			case LONG:
			{
				long a = expr1.evaluateLong (vm);
				long b = expr2.evaluateLong (vm);
				return (a > b) ? 1 : (a == b) ? 0 : -1;
			}
// generated
// generated
			case FLOAT:
			{
				float a = expr1.evaluateFloat (vm);
				float b = expr2.evaluateFloat (vm);
				return (a > b) ? 1 : (a == b) ? 0 : -1;
			}
// generated
// generated
			case DOUBLE:
			{
				double a = expr1.evaluateDouble (vm);
				double b = expr2.evaluateDouble (vm);
				return (a > b) ? 1 : (a == b) ? 0 : -1;
			}
//!! *# End of generated code
			default:
				throw new AssertionError ();
		}
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (2);
		expr1 = getExpression (0);
		expr2 = getExpression (1);
	}


	private static final int[] OPCODES
		= {Opcodes.NOP, Opcodes.LCMP, Opcodes.FCMPL, Opcodes.DCMPL};


	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		writer.visitInsn (opcode (expr1.etype, OPCODES));
	}

}
