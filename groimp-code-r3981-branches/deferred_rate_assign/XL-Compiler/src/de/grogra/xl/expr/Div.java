
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

import de.grogra.xl.vmx.*;

public strictfp class Div extends BinaryExpression
{

	@Override
	public int getSupportedTypes ()
	{
		return INT_MASK | LONG_MASK | FLOAT_MASK | DOUBLE_MASK;
	}

/*!!
#foreach ($type in $vmnumeric)
$pp.setType($type)

	@Override				
	protected $type evaluate${pp.Type}Impl (VMXState vm)
	{
		return expr1.evaluate$pp.Type (vm) / expr2.evaluate$pp.Type (vm);
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override				
	protected int evaluateIntImpl (VMXState vm)
	{
		return expr1.evaluateInt (vm) / expr2.evaluateInt (vm);
	}
// generated
// generated
// generated
	@Override				
	protected long evaluateLongImpl (VMXState vm)
	{
		return expr1.evaluateLong (vm) / expr2.evaluateLong (vm);
	}
// generated
// generated
// generated
	@Override				
	protected float evaluateFloatImpl (VMXState vm)
	{
		return expr1.evaluateFloat (vm) / expr2.evaluateFloat (vm);
	}
// generated
// generated
// generated
	@Override				
	protected double evaluateDoubleImpl (VMXState vm)
	{
		return expr1.evaluateDouble (vm) / expr2.evaluateDouble (vm);
	}
// generated
//!! *# End of generated code
	
	private static final int[] OPCODES
		= {Opcodes.IDIV, Opcodes.LDIV, Opcodes.FDIV, Opcodes.DDIV};

	@Override
	protected int[] getOpcodes ()
	{
		return OPCODES;
	}

}
