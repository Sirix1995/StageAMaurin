
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

import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;

public strictfp class Power extends BinaryExpression
{

	@Override
	public int getSupportedTypes ()
	{
		return FLOAT_MASK | DOUBLE_MASK;
	}

/*!!
#foreach ($type in ["float", "double"])
$pp.setType($type)

	@Override
	protected $type evaluate${pp.Type}Impl (VMXState vm)
	{
		return ($type) StrictMath.pow (expr1.evaluate$pp.Type (vm),
									   expr2.evaluate$pp.Type (vm));
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override
	protected float evaluateFloatImpl (VMXState vm)
	{
		return (float) StrictMath.pow (expr1.evaluateFloat (vm),
									   expr2.evaluateFloat (vm));
	}
// generated
// generated
// generated
	@Override
	protected double evaluateDoubleImpl (VMXState vm)
	{
		return (double) StrictMath.pow (expr1.evaluateDouble (vm),
									   expr2.evaluateDouble (vm));
	}
// generated
//!! *# End of generated code

	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		expr1.write (writer, discard);
		if (!discard && (etype == FLOAT))
		{
			writer.visitInsn (Opcodes.F2D);
		}
		expr2.write (writer, discard);
		if (!discard && (etype == FLOAT))
		{
			writer.visitInsn (Opcodes.F2D);
		}
		if (!discard)
		{
			writer.visitMethodInsn
				(writer.isFPStrict () ? StrictMath.class : Math.class,
				 "pow", "(DD)D");
			if (etype == FLOAT)
			{
				writer.visitInsn (Opcodes.D2F);
			}
		}
	}

}
