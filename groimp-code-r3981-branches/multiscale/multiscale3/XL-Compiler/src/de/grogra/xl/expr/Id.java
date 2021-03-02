
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
import de.grogra.xl.vmx.VMXState;

public strictfp class Id extends UnaryExpression
{
	public Id ()
	{	
	}

	public Id (Type type)
	{	
		super (type);
	}

	@Override
	public int getSupportedTypes ()
	{
		return PRIMITIVE_MASK | OBJECT_MASK;
	}

	@Override
	public Expression compile (Scope scope, Expression e1)
	{
		return setType (e1.getType ()).add (e1);
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override				
	protected $type evaluate${pp.Type}Impl (VMXState vm)
	{
		return expr.evaluate$pp.Type (vm);
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override				
	protected boolean evaluateBooleanImpl (VMXState vm)
	{
		return expr.evaluateBoolean (vm);
	}
// generated
// generated
// generated
	@Override				
	protected byte evaluateByteImpl (VMXState vm)
	{
		return expr.evaluateByte (vm);
	}
// generated
// generated
// generated
	@Override				
	protected short evaluateShortImpl (VMXState vm)
	{
		return expr.evaluateShort (vm);
	}
// generated
// generated
// generated
	@Override				
	protected char evaluateCharImpl (VMXState vm)
	{
		return expr.evaluateChar (vm);
	}
// generated
// generated
// generated
	@Override				
	protected int evaluateIntImpl (VMXState vm)
	{
		return expr.evaluateInt (vm);
	}
// generated
// generated
// generated
	@Override				
	protected long evaluateLongImpl (VMXState vm)
	{
		return expr.evaluateLong (vm);
	}
// generated
// generated
// generated
	@Override				
	protected float evaluateFloatImpl (VMXState vm)
	{
		return expr.evaluateFloat (vm);
	}
// generated
// generated
// generated
	@Override				
	protected double evaluateDoubleImpl (VMXState vm)
	{
		return expr.evaluateDouble (vm);
	}
// generated
// generated
// generated
	@Override				
	protected Object evaluateObjectImpl (VMXState vm)
	{
		return expr.evaluateObject (vm);
	}
// generated
//!! *# End of generated code
	
	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
	}

}
