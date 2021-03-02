
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
import java.lang.reflect.Array;

public final class CreateArray extends EvalExpression
{
	private final Type componentType;
	private final Class componentClass;
	private Type arrayType = null;
	private Expression[] dimensions;


	public CreateArray (Type type)
	{
		super (Type.OBJECT);
		componentType = type;
		componentClass = type.getImplementationClass ();
	}


	@Override
	protected Object evaluateObjectImpl (VMXState t)
	{
		int l = dimensions.length;
		for (int i = 0; i < l; i++)
		{
			t.ipush (dimensions[i].evaluateInt (t));
		}
		return Array.newInstance (componentClass, t.popIntArray (l));
	}


	@Override
	public Type getType ()
	{
		if (arrayType == null)
		{
			arrayType = componentType;
			for (int i = getExpressionCount (); i >= 1; i--)
			{
				arrayType = arrayType.getArrayType ();
			}
		}
		return arrayType;
	}


	@Override
	public void link (boolean checkTypes)
	{
		dimensions = new Expression[getExpressionCount ()];
		for (int i = 0; i < dimensions.length; i++)
		{
			dimensions[i] = getExpression (i, INT, checkTypes);
		}
	}


	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		if (dimensions.length == 1)
		{
			writer.visitNewArray (getType ().getComponentType ());
		}
		else
		{
			writer.visitMultiANewArrayInsn (writer.toName (getType ()), dimensions.length);
		}
	}


}
