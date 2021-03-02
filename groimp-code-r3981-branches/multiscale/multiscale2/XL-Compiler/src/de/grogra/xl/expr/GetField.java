
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
import de.grogra.xl.compiler.CClass;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.VMXState;

public class GetField extends Variable
{
	final Field field;
	Expression expr;
	private CClass accessMethodClass;


	public GetField (Field field)
	{
		super (field.getType ());
		this.field = field;
	}


	public de.grogra.reflect.Field getField ()
	{
		return field;
	}


	@Override
	public boolean isRequired (int index)
	{
		return !Reflection.isStatic (field);
	}


/*!!
#foreach ($type in $types)
$pp.setType($type)
	@Override
	protected $type evaluate${pp.Type}Impl (VMXState t)
	{
		try
		{
			return field.get$pp.Type ((expr != null) ? expr.evaluateObject (t) : null);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
#end
!!*/
//!! #* Start of generated code
// generated
	@Override
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		try
		{
			return field.getBoolean ((expr != null) ? expr.evaluateObject (t) : null);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
	@Override
	protected byte evaluateByteImpl (VMXState t)
	{
		try
		{
			return field.getByte ((expr != null) ? expr.evaluateObject (t) : null);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
	@Override
	protected short evaluateShortImpl (VMXState t)
	{
		try
		{
			return field.getShort ((expr != null) ? expr.evaluateObject (t) : null);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
	@Override
	protected char evaluateCharImpl (VMXState t)
	{
		try
		{
			return field.getChar ((expr != null) ? expr.evaluateObject (t) : null);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
	@Override
	protected int evaluateIntImpl (VMXState t)
	{
		try
		{
			return field.getInt ((expr != null) ? expr.evaluateObject (t) : null);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
	@Override
	protected long evaluateLongImpl (VMXState t)
	{
		try
		{
			return field.getLong ((expr != null) ? expr.evaluateObject (t) : null);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
	@Override
	protected float evaluateFloatImpl (VMXState t)
	{
		try
		{
			return field.getFloat ((expr != null) ? expr.evaluateObject (t) : null);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
	@Override
	protected double evaluateDoubleImpl (VMXState t)
	{
		try
		{
			return field.getDouble ((expr != null) ? expr.evaluateObject (t) : null);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
	@Override
	protected Object evaluateObjectImpl (VMXState t)
	{
		try
		{
			return field.getObject ((expr != null) ? expr.evaluateObject (t) : null);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
//!! *# End of generated code

	@Override
	public void link (boolean checkTypes)
	{
		if (Reflection.isStatic (field))
		{
			expr = getFirstExpression ();
		}
		else
		{
			checkExpressionCount (1);
			expr = getExpression (0, OBJECT, checkTypes);
		}
	}


	@Override
	public Expression toAssignment (int assignmentType)
	{
		AssignField f = new AssignField (field, assignmentType);
		if (accessMethodClass != null)
		{
			f.useAccessMethod (accessMethodClass);
		}
		f.receiveChildren (this);
		return f;
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ',' + field;
	}

	
	public void useAccessMethod (CClass amc)
	{
		amc.getAccessMethodFor (field, false);
		accessMethodClass = amc;
	}


	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		writer.visitFieldInsn
			(Reflection.isStatic (field) ? Opcodes.GETSTATIC : Opcodes.GETFIELD,
			 field, (accessMethodClass == null) ? null
			 : accessMethodClass.getAccessMethodFor (field, false));
	}

}
