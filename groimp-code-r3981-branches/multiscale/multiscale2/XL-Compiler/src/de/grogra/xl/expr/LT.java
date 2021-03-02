
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
import de.grogra.xl.vmx.VMXState;

public strictfp class LT extends Comparison
{

	@Override
	public int getSupportedTypes ()
	{
		return INT_MASK | LONG_MASK | FLOAT_MASK | DOUBLE_MASK;
	}


	@Override
	protected boolean evaluateBooleanImpl (VMXState vm)
	{
		switch (ctype)
		{
/*!!
#foreach ($type in $vmnumeric)
$pp.setType($type)
			case $pp.TYPE:
				return expr1.evaluate$pp.Type (vm) < expr2.evaluate$pp.Type (vm);
#end
!!*/
//!! #* Start of generated code
// generated
			case INT:
				return expr1.evaluateInt (vm) < expr2.evaluateInt (vm);
// generated
			case LONG:
				return expr1.evaluateLong (vm) < expr2.evaluateLong (vm);
// generated
			case FLOAT:
				return expr1.evaluateFloat (vm) < expr2.evaluateFloat (vm);
// generated
			case DOUBLE:
				return expr1.evaluateDouble (vm) < expr2.evaluateDouble (vm);
//!! *# End of generated code
		}
		throw new AssertionError ();
	}


	@Override
	public int writeConditional (BytecodeWriter writer, Label falseLabel, Label trueLabel)
	{
		switch (ctype)
		{
			case INT:
				return writeIntObjectComparison
					(writer, falseLabel, trueLabel,
					 Opcodes.IFGE, Opcodes.IFLT, Opcodes.IFLE, Opcodes.IFGT, Opcodes.IF_ICMPGE, Opcodes.IF_ICMPLT);
			case LONG:
				return writeLFDComparison
					(writer, falseLabel, trueLabel,
					 Opcodes.LCMP, Opcodes.IFGE, Opcodes.IFLT);
			case FLOAT:
				return writeLFDComparison
					(writer, falseLabel, trueLabel,
					 Opcodes.FCMPG, Opcodes.IFGE, Opcodes.IFLT);
			case DOUBLE:
				return writeLFDComparison
					(writer, falseLabel, trueLabel,
					 Opcodes.DCMPG, Opcodes.IFGE, Opcodes.IFLT);
			default:
				throw new AssertionError ();
		}
	}

}
