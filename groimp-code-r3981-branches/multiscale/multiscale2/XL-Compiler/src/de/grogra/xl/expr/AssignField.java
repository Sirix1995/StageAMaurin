
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
import de.grogra.xl.compiler.*;
import de.grogra.xl.vmx.*;

public final class AssignField extends Assignment
{
	de.grogra.reflect.Field field;
	Expression container, expr;
	private CClass accessMethodClass;


	public AssignField (de.grogra.reflect.Field field, int assignmentType)
	{
		super (field.getType (), assignmentType);
		this.field = field;
	}


	@Override
	public boolean isRequired (int index)
	{
		return (index > 0) || !Reflection.isStatic (field)
			|| (getExpressionCount () == 1);
	}


	public de.grogra.reflect.Field getField ()
	{
		return field;
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override				
	protected $type evaluate${pp.Type}Impl (VMXState t)
	{
		$type value;
		Object c = (container == null) ? null : container.evaluateObject (t);
		try
		{
			switch (assignmentType)
			{
				case SIMPLE:
					field.set$pp.Type (c, value = expr.evaluate$pp.Type (t));
					break;
				case COMPOUND:
					t.${pp.prefix}push (field.get$pp.Type (c) $pp.type2vm);
					field.set$pp.Type (c, value = expr.evaluate$pp.Type (t));
					break;
				case POSTFIX_COMPOUND:
					t.${pp.prefix}push ((value = field.get$pp.Type (c)) $pp.type2vm);
					field.set$pp.Type (c, expr.evaluate$pp.Type (t));
					break;
				default:
					throw new AssertionError ();
			}
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
		return value;
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override				
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		boolean value;
		Object c = (container == null) ? null : container.evaluateObject (t);
		try
		{
			switch (assignmentType)
			{
				case SIMPLE:
					field.setBoolean (c, value = expr.evaluateBoolean (t));
					break;
				case COMPOUND:
					t.ipush (field.getBoolean (c)  ? 1 : 0);
					field.setBoolean (c, value = expr.evaluateBoolean (t));
					break;
				case POSTFIX_COMPOUND:
					t.ipush ((value = field.getBoolean (c))  ? 1 : 0);
					field.setBoolean (c, expr.evaluateBoolean (t));
					break;
				default:
					throw new AssertionError ();
			}
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected byte evaluateByteImpl (VMXState t)
	{
		byte value;
		Object c = (container == null) ? null : container.evaluateObject (t);
		try
		{
			switch (assignmentType)
			{
				case SIMPLE:
					field.setByte (c, value = expr.evaluateByte (t));
					break;
				case COMPOUND:
					t.ipush (field.getByte (c) );
					field.setByte (c, value = expr.evaluateByte (t));
					break;
				case POSTFIX_COMPOUND:
					t.ipush ((value = field.getByte (c)) );
					field.setByte (c, expr.evaluateByte (t));
					break;
				default:
					throw new AssertionError ();
			}
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected short evaluateShortImpl (VMXState t)
	{
		short value;
		Object c = (container == null) ? null : container.evaluateObject (t);
		try
		{
			switch (assignmentType)
			{
				case SIMPLE:
					field.setShort (c, value = expr.evaluateShort (t));
					break;
				case COMPOUND:
					t.ipush (field.getShort (c) );
					field.setShort (c, value = expr.evaluateShort (t));
					break;
				case POSTFIX_COMPOUND:
					t.ipush ((value = field.getShort (c)) );
					field.setShort (c, expr.evaluateShort (t));
					break;
				default:
					throw new AssertionError ();
			}
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected char evaluateCharImpl (VMXState t)
	{
		char value;
		Object c = (container == null) ? null : container.evaluateObject (t);
		try
		{
			switch (assignmentType)
			{
				case SIMPLE:
					field.setChar (c, value = expr.evaluateChar (t));
					break;
				case COMPOUND:
					t.ipush (field.getChar (c) );
					field.setChar (c, value = expr.evaluateChar (t));
					break;
				case POSTFIX_COMPOUND:
					t.ipush ((value = field.getChar (c)) );
					field.setChar (c, expr.evaluateChar (t));
					break;
				default:
					throw new AssertionError ();
			}
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected int evaluateIntImpl (VMXState t)
	{
		int value;
		Object c = (container == null) ? null : container.evaluateObject (t);
		try
		{
			switch (assignmentType)
			{
				case SIMPLE:
					field.setInt (c, value = expr.evaluateInt (t));
					break;
				case COMPOUND:
					t.ipush (field.getInt (c) );
					field.setInt (c, value = expr.evaluateInt (t));
					break;
				case POSTFIX_COMPOUND:
					t.ipush ((value = field.getInt (c)) );
					field.setInt (c, expr.evaluateInt (t));
					break;
				default:
					throw new AssertionError ();
			}
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected long evaluateLongImpl (VMXState t)
	{
		long value;
		Object c = (container == null) ? null : container.evaluateObject (t);
		try
		{
			switch (assignmentType)
			{
				case SIMPLE:
					field.setLong (c, value = expr.evaluateLong (t));
					break;
				case COMPOUND:
					t.lpush (field.getLong (c) );
					field.setLong (c, value = expr.evaluateLong (t));
					break;
				case POSTFIX_COMPOUND:
					t.lpush ((value = field.getLong (c)) );
					field.setLong (c, expr.evaluateLong (t));
					break;
				default:
					throw new AssertionError ();
			}
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected float evaluateFloatImpl (VMXState t)
	{
		float value;
		Object c = (container == null) ? null : container.evaluateObject (t);
		try
		{
			switch (assignmentType)
			{
				case SIMPLE:
					field.setFloat (c, value = expr.evaluateFloat (t));
					break;
				case COMPOUND:
					t.fpush (field.getFloat (c) );
					field.setFloat (c, value = expr.evaluateFloat (t));
					break;
				case POSTFIX_COMPOUND:
					t.fpush ((value = field.getFloat (c)) );
					field.setFloat (c, expr.evaluateFloat (t));
					break;
				default:
					throw new AssertionError ();
			}
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected double evaluateDoubleImpl (VMXState t)
	{
		double value;
		Object c = (container == null) ? null : container.evaluateObject (t);
		try
		{
			switch (assignmentType)
			{
				case SIMPLE:
					field.setDouble (c, value = expr.evaluateDouble (t));
					break;
				case COMPOUND:
					t.dpush (field.getDouble (c) );
					field.setDouble (c, value = expr.evaluateDouble (t));
					break;
				case POSTFIX_COMPOUND:
					t.dpush ((value = field.getDouble (c)) );
					field.setDouble (c, expr.evaluateDouble (t));
					break;
				default:
					throw new AssertionError ();
			}
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected Object evaluateObjectImpl (VMXState t)
	{
		Object value;
		Object c = (container == null) ? null : container.evaluateObject (t);
		try
		{
			switch (assignmentType)
			{
				case SIMPLE:
					field.setObject (c, value = expr.evaluateObject (t));
					break;
				case COMPOUND:
					t.apush (field.getObject (c) );
					field.setObject (c, value = expr.evaluateObject (t));
					break;
				case POSTFIX_COMPOUND:
					t.apush ((value = field.getObject (c)) );
					field.setObject (c, expr.evaluateObject (t));
					break;
				default:
					throw new AssertionError ();
			}
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
		return value;
	}
// generated
//!! *# End of generated code

	@Override
	public void link (boolean checkTypes)
	{
		if (Reflection.isStatic (field))
		{
			if (getExpressionCount () == 2)
			{
				container = getObjectExpression (0, field.getDeclaringType (), checkTypes);
				expr = getExpression (1, etype, checkTypes);
			}
			else
			{
				checkExpressionCount (1);
				container = null;
				expr = getExpression (0, etype, checkTypes);
			}
		}
		else
		{
			checkExpressionCount (2);
			container = getObjectExpression (0, field.getDeclaringType (), checkTypes);
			expr = getExpression (1, etype, checkTypes);
		}
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ',' + field;
	}

	
	void useAccessMethod (CClass amc)
	{
		amc.getAccessMethodFor (field, true);
		accessMethodClass = amc;
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		boolean stat = Reflection.isStatic (field);
		if (container != null)
		{
			container.write (writer, stat);
		}
		AccessMethod get = (accessMethodClass != null) ? accessMethodClass.getAccessMethodFor (field, false) : null;
		AccessMethod set = (accessMethodClass != null) ? accessMethodClass.getAccessMethodFor (field, true) : null;
		switch (assignmentType)
		{
			case SIMPLE:
				expr.write (writer, false);
				if (!discard)
				{
					if (stat)
					{
						writer.visitDup (etype);
					}
					else
					{
						writer.visitDupX1 (etype);
					}
				}
				writer.visitFieldInsn
					(stat ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD, field, set);
				break;
			case POSTFIX_COMPOUND:
				if (!discard)
				{
					if (stat)
					{
						writer.visitFieldInsn (Opcodes.GETSTATIC, field, get);
						writer.visitDup (etype);
						expr.write (writer, false);
						writer.visitFieldInsn (Opcodes.PUTSTATIC, field, set);
					}
					else
					{
						writer.visitInsn (Opcodes.DUP);
						writer.visitFieldInsn (Opcodes.GETFIELD, field, get);
						writer.visitDupX1 (etype);
						expr.write (writer, false);
						writer.visitFieldInsn (Opcodes.PUTFIELD, field, set);
					}
					break;
				}
				// no break
			case COMPOUND:
				if (stat)
				{
					writer.visitFieldInsn (Opcodes.GETSTATIC, field, get);
					expr.write (writer, false);
					if (!discard)
					{
						writer.visitDup (etype);
					}
					writer.visitFieldInsn (Opcodes.PUTSTATIC, field, set);
				}
				else
				{
					writer.visitInsn (Opcodes.DUP);
					writer.visitFieldInsn (Opcodes.GETFIELD, field, get);
					expr.write (writer, false);
					if (!discard)
					{
						writer.visitDupX1 (etype);
					}
					writer.visitFieldInsn (Opcodes.PUTFIELD, field, set);
				}
				break;
		}
	}

}
