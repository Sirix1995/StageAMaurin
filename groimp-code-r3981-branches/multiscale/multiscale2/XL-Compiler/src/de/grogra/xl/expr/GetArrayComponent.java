
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
import de.grogra.xl.vmx.*;

public final class GetArrayComponent extends Variable
{
	private Expression array, index;


	public GetArrayComponent (Type type)
	{
		super (type);
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)
	@Override
	protected $type evaluate${pp.Type}Impl (VMXState t)
	{
		return (($type[]) array.evaluateObject (t))[index.evaluateInt (t)];
	}
#end
!!*/
//!! #* Start of generated code
// generated
	@Override
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		return ((boolean[]) array.evaluateObject (t))[index.evaluateInt (t)];
	}
// generated
	@Override
	protected byte evaluateByteImpl (VMXState t)
	{
		return ((byte[]) array.evaluateObject (t))[index.evaluateInt (t)];
	}
// generated
	@Override
	protected short evaluateShortImpl (VMXState t)
	{
		return ((short[]) array.evaluateObject (t))[index.evaluateInt (t)];
	}
// generated
	@Override
	protected char evaluateCharImpl (VMXState t)
	{
		return ((char[]) array.evaluateObject (t))[index.evaluateInt (t)];
	}
// generated
	@Override
	protected int evaluateIntImpl (VMXState t)
	{
		return ((int[]) array.evaluateObject (t))[index.evaluateInt (t)];
	}
// generated
	@Override
	protected long evaluateLongImpl (VMXState t)
	{
		return ((long[]) array.evaluateObject (t))[index.evaluateInt (t)];
	}
// generated
	@Override
	protected float evaluateFloatImpl (VMXState t)
	{
		return ((float[]) array.evaluateObject (t))[index.evaluateInt (t)];
	}
// generated
	@Override
	protected double evaluateDoubleImpl (VMXState t)
	{
		return ((double[]) array.evaluateObject (t))[index.evaluateInt (t)];
	}
// generated
	@Override
	protected Object evaluateObjectImpl (VMXState t)
	{
		return ((Object[]) array.evaluateObject (t))[index.evaluateInt (t)];
	}
//!! *# End of generated code

	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (2);
		array = getExpression (0, OBJECT, checkTypes);
		index = getExpression (1, INT, checkTypes);
	}


	@Override
	public Expression toAssignment (int assignmentType)
	{
		return new AssignArrayComponent (getType (), assignmentType)
			.receiveChildren (this);
	}


	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		writer.visitALoad (etype);
	}

}
