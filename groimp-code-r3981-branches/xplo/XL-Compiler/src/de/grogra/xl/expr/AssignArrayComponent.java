
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
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;

public final class AssignArrayComponent extends Assignment
{
	Expression container, index, expr;


	public AssignArrayComponent (Type type, int assignmentType)
	{
		super (type, assignmentType);
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override
	protected $type evaluate${pp.Type}Impl (VMXState t)
	{
		$type value;
		$type[] c = ($type[]) container.evaluateObject (t);
		int i = index.evaluateInt (t);
		switch (assignmentType)
		{
			case SIMPLE:
				c[i] = value = expr.evaluate$pp.Type (t);
				break;
			case COMPOUND:
				t.${pp.prefix}push (c[i] $pp.type2vm);
				c[i] = value = expr.evaluate$pp.Type (t);
				break;
			case POSTFIX_COMPOUND:
				t.${pp.prefix}push ((value = c[i]) $pp.type2vm);
				c[i] = expr.evaluate$pp.Type (t);
				break;
			default:
				throw new AssertionError ();
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
		boolean[] c = (boolean[]) container.evaluateObject (t);
		int i = index.evaluateInt (t);
		switch (assignmentType)
		{
			case SIMPLE:
				c[i] = value = expr.evaluateBoolean (t);
				break;
			case COMPOUND:
				t.ipush (c[i]  ? 1 : 0);
				c[i] = value = expr.evaluateBoolean (t);
				break;
			case POSTFIX_COMPOUND:
				t.ipush ((value = c[i])  ? 1 : 0);
				c[i] = expr.evaluateBoolean (t);
				break;
			default:
				throw new AssertionError ();
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
		byte[] c = (byte[]) container.evaluateObject (t);
		int i = index.evaluateInt (t);
		switch (assignmentType)
		{
			case SIMPLE:
				c[i] = value = expr.evaluateByte (t);
				break;
			case COMPOUND:
				t.ipush (c[i] );
				c[i] = value = expr.evaluateByte (t);
				break;
			case POSTFIX_COMPOUND:
				t.ipush ((value = c[i]) );
				c[i] = expr.evaluateByte (t);
				break;
			default:
				throw new AssertionError ();
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
		short[] c = (short[]) container.evaluateObject (t);
		int i = index.evaluateInt (t);
		switch (assignmentType)
		{
			case SIMPLE:
				c[i] = value = expr.evaluateShort (t);
				break;
			case COMPOUND:
				t.ipush (c[i] );
				c[i] = value = expr.evaluateShort (t);
				break;
			case POSTFIX_COMPOUND:
				t.ipush ((value = c[i]) );
				c[i] = expr.evaluateShort (t);
				break;
			default:
				throw new AssertionError ();
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
		char[] c = (char[]) container.evaluateObject (t);
		int i = index.evaluateInt (t);
		switch (assignmentType)
		{
			case SIMPLE:
				c[i] = value = expr.evaluateChar (t);
				break;
			case COMPOUND:
				t.ipush (c[i] );
				c[i] = value = expr.evaluateChar (t);
				break;
			case POSTFIX_COMPOUND:
				t.ipush ((value = c[i]) );
				c[i] = expr.evaluateChar (t);
				break;
			default:
				throw new AssertionError ();
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
		int[] c = (int[]) container.evaluateObject (t);
		int i = index.evaluateInt (t);
		switch (assignmentType)
		{
			case SIMPLE:
				c[i] = value = expr.evaluateInt (t);
				break;
			case COMPOUND:
				t.ipush (c[i] );
				c[i] = value = expr.evaluateInt (t);
				break;
			case POSTFIX_COMPOUND:
				t.ipush ((value = c[i]) );
				c[i] = expr.evaluateInt (t);
				break;
			default:
				throw new AssertionError ();
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
		long[] c = (long[]) container.evaluateObject (t);
		int i = index.evaluateInt (t);
		switch (assignmentType)
		{
			case SIMPLE:
				c[i] = value = expr.evaluateLong (t);
				break;
			case COMPOUND:
				t.lpush (c[i] );
				c[i] = value = expr.evaluateLong (t);
				break;
			case POSTFIX_COMPOUND:
				t.lpush ((value = c[i]) );
				c[i] = expr.evaluateLong (t);
				break;
			default:
				throw new AssertionError ();
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
		float[] c = (float[]) container.evaluateObject (t);
		int i = index.evaluateInt (t);
		switch (assignmentType)
		{
			case SIMPLE:
				c[i] = value = expr.evaluateFloat (t);
				break;
			case COMPOUND:
				t.fpush (c[i] );
				c[i] = value = expr.evaluateFloat (t);
				break;
			case POSTFIX_COMPOUND:
				t.fpush ((value = c[i]) );
				c[i] = expr.evaluateFloat (t);
				break;
			default:
				throw new AssertionError ();
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
		double[] c = (double[]) container.evaluateObject (t);
		int i = index.evaluateInt (t);
		switch (assignmentType)
		{
			case SIMPLE:
				c[i] = value = expr.evaluateDouble (t);
				break;
			case COMPOUND:
				t.dpush (c[i] );
				c[i] = value = expr.evaluateDouble (t);
				break;
			case POSTFIX_COMPOUND:
				t.dpush ((value = c[i]) );
				c[i] = expr.evaluateDouble (t);
				break;
			default:
				throw new AssertionError ();
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
		Object[] c = (Object[]) container.evaluateObject (t);
		int i = index.evaluateInt (t);
		switch (assignmentType)
		{
			case SIMPLE:
				c[i] = value = expr.evaluateObject (t);
				break;
			case COMPOUND:
				t.apush (c[i] );
				c[i] = value = expr.evaluateObject (t);
				break;
			case POSTFIX_COMPOUND:
				t.apush ((value = c[i]) );
				c[i] = expr.evaluateObject (t);
				break;
			default:
				throw new AssertionError ();
		}
		return value;
	}
// generated
//!! *# End of generated code

	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (3);
		container = getExpression (0, OBJECT, checkTypes);
		index = getExpression (1, INT, checkTypes);
		expr = getExpression (2, etype, checkTypes);
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		container.write (writer, false);
		index.write (writer, false);
		write (etype, assignmentType, expr, writer, discard);
	}


	static void write (int etype, int assignmentType, Expression expr,
					   BytecodeWriter writer, boolean discard)
	{
		switch (assignmentType)
		{
			case SIMPLE:
				expr.write (writer, false);
				if (!discard)
				{
					writer.visitDupX2 (etype);
				}
				writer.visitAStore (etype);
				break;
			case POSTFIX_COMPOUND:
				if (!discard)
				{
					writer.visitInsn (Opcodes.DUP2);
					writer.visitALoad (etype);
					writer.visitDupX2 (etype);
					expr.write (writer, false);
					writer.visitAStore (etype);
					break;
				}
				// no break
			case COMPOUND:
				writer.visitInsn (Opcodes.DUP2);
				writer.visitALoad (etype);
				expr.write (writer, false);
				if (!discard)
				{
					writer.visitDupX2 (etype);
				}
				writer.visitAStore (etype);
				break;
		}
	}

}
