
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

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;

public strictfp class NotEquals extends Comparison
{

	@Override
	public int getSupportedTypes ()
	{
		return BOOLEAN_MASK | INT_MASK | LONG_MASK | FLOAT_MASK | DOUBLE_MASK | OBJECT_MASK;
	}


	@Override
	protected boolean evaluateBooleanImpl (VMXState vm)
	{
		switch (ctype)
		{
/*!!
#foreach ($type in ["boolean", "int", "long", "float", "double", "Object"])
$pp.setType($type)
			case $pp.TYPE:
				return expr1.evaluate$pp.Type (vm)
					!= expr2.evaluate$pp.Type (vm);
#end
!!*/
//!! #* Start of generated code
// generated
			case BOOLEAN:
				return expr1.evaluateBoolean (vm)
					!= expr2.evaluateBoolean (vm);
// generated
			case INT:
				return expr1.evaluateInt (vm)
					!= expr2.evaluateInt (vm);
// generated
			case LONG:
				return expr1.evaluateLong (vm)
					!= expr2.evaluateLong (vm);
// generated
			case FLOAT:
				return expr1.evaluateFloat (vm)
					!= expr2.evaluateFloat (vm);
// generated
			case DOUBLE:
				return expr1.evaluateDouble (vm)
					!= expr2.evaluateDouble (vm);
// generated
			case OBJECT:
				return expr1.evaluateObject (vm)
					!= expr2.evaluateObject (vm);
//!! *# End of generated code
		}
		throw new AssertionError ();
	}


	@Override
	public int getPromotedType (Expression expr1, Expression expr2)
	{
		return ((expr1.etype == OBJECT) && (expr2.etype == OBJECT)) ? OBJECT
			: super.getPromotedType (expr1, expr2);
	}


	@Override
	public int writeConditional (BytecodeWriter writer, Label falseLabel, Label trueLabel)
	{
		switch (ctype)
		{
			case BOOLEAN:
				return writeBooleanEquals (writer, falseLabel, trueLabel, false);
			case INT:
				return writeIntObjectComparison
					(writer, falseLabel, trueLabel,
					 Opcodes.IFEQ, Opcodes.IFNE, Opcodes.IFEQ, Opcodes.IFNE, Opcodes.IF_ICMPEQ, Opcodes.IF_ICMPNE);
			case OBJECT:
				return writeIntObjectComparison
					(writer, falseLabel, trueLabel,
					 Opcodes.IFNULL, Opcodes.IFNONNULL, Opcodes.IFNULL, Opcodes.IFNONNULL, Opcodes.IF_ACMPEQ, Opcodes.IF_ACMPNE);
			case LONG:
				return writeLFDComparison
					(writer, falseLabel, trueLabel,
					 Opcodes.LCMP, Opcodes.IFEQ, Opcodes.IFNE);
			case FLOAT:
				return writeLFDComparison
					(writer, falseLabel, trueLabel,
					 Opcodes.FCMPG, Opcodes.IFEQ, Opcodes.IFNE);
			case DOUBLE:
				return writeLFDComparison
					(writer, falseLabel, trueLabel,
					 Opcodes.DCMPG, Opcodes.IFEQ, Opcodes.IFNE);
			default:
				throw new AssertionError ();
		}
	}

}
