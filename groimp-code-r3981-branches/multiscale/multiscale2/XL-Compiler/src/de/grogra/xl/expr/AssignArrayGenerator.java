
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

import de.grogra.reflect.*;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.vmx.*;

public final class AssignArrayGenerator extends Assignment
	implements Generator, LocalAccess
{
	Expression container, expr, statement;
	private Local array, counter;
	private VMXState.Local vmxArray, vmxCounter;
	
	
	public AssignArrayGenerator (Type type, Local array, Local counter,
									 int assignmentType)
	{
		super (type, assignmentType);
		this.array = array;
		this.counter = counter;
	}


	public int getLocalCount ()
	{
		return 2;
	}

	
	public int getAccessType (int index)
	{
		return PRE_1_ASSIGNMENT | POST_USE;
	}

	
	public Local getLocal (int index)
	{
		return (index == 0) ? array : counter;
	}

	
	public void setLocal (int index, Local local)
	{
		if (index == 0)
		{
			array = local;
		}
		else
		{
			counter = local;
		}
	}
	

	public void complete (MethodScope scope)
	{
		vmxArray = array.createVMXLocal ();
		vmxCounter = counter.createVMXLocal ();
	}


/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override
	protected $type evaluate${pp.Type}Impl (VMXState t)
	{
		$type[] c = ($type[]) container.evaluateObject (t);
		for (int i = 0; i < c.length; i++)
		{
			$type value;
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
			t.${pp.prefix}push (value $pp.type2vm);
			statement.evaluateAsVoid (t);
		}
		return $pp.null;
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		boolean[] c = (boolean[]) container.evaluateObject (t);
		for (int i = 0; i < c.length; i++)
		{
			boolean value;
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
			t.ipush (value  ? 1 : 0);
			statement.evaluateAsVoid (t);
		}
		return false;
	}
// generated
// generated
// generated
	@Override
	protected byte evaluateByteImpl (VMXState t)
	{
		byte[] c = (byte[]) container.evaluateObject (t);
		for (int i = 0; i < c.length; i++)
		{
			byte value;
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
			t.ipush (value );
			statement.evaluateAsVoid (t);
		}
		return ((byte) 0);
	}
// generated
// generated
// generated
	@Override
	protected short evaluateShortImpl (VMXState t)
	{
		short[] c = (short[]) container.evaluateObject (t);
		for (int i = 0; i < c.length; i++)
		{
			short value;
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
			t.ipush (value );
			statement.evaluateAsVoid (t);
		}
		return ((short) 0);
	}
// generated
// generated
// generated
	@Override
	protected char evaluateCharImpl (VMXState t)
	{
		char[] c = (char[]) container.evaluateObject (t);
		for (int i = 0; i < c.length; i++)
		{
			char value;
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
			t.ipush (value );
			statement.evaluateAsVoid (t);
		}
		return ((char) 0);
	}
// generated
// generated
// generated
	@Override
	protected int evaluateIntImpl (VMXState t)
	{
		int[] c = (int[]) container.evaluateObject (t);
		for (int i = 0; i < c.length; i++)
		{
			int value;
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
			t.ipush (value );
			statement.evaluateAsVoid (t);
		}
		return ((int) 0);
	}
// generated
// generated
// generated
	@Override
	protected long evaluateLongImpl (VMXState t)
	{
		long[] c = (long[]) container.evaluateObject (t);
		for (int i = 0; i < c.length; i++)
		{
			long value;
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
			t.lpush (value );
			statement.evaluateAsVoid (t);
		}
		return ((long) 0);
	}
// generated
// generated
// generated
	@Override
	protected float evaluateFloatImpl (VMXState t)
	{
		float[] c = (float[]) container.evaluateObject (t);
		for (int i = 0; i < c.length; i++)
		{
			float value;
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
			t.fpush (value );
			statement.evaluateAsVoid (t);
		}
		return ((float) 0);
	}
// generated
// generated
// generated
	@Override
	protected double evaluateDoubleImpl (VMXState t)
	{
		double[] c = (double[]) container.evaluateObject (t);
		for (int i = 0; i < c.length; i++)
		{
			double value;
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
			t.dpush (value );
			statement.evaluateAsVoid (t);
		}
		return ((double) 0);
	}
// generated
// generated
// generated
	@Override
	protected Object evaluateObjectImpl (VMXState t)
	{
		Object[] c = (Object[]) container.evaluateObject (t);
		for (int i = 0; i < c.length; i++)
		{
			Object value;
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
			t.apush (value );
			statement.evaluateAsVoid (t);
		}
		return null;
	}
// generated
//!! *# End of generated code


	public int getGeneratorType ()
	{
		return LOCAL;
	}


	public void setBreakTarget (BreakTarget target)
	{
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (3);
		container = getExpression (0, OBJECT, checkTypes);
		expr = getExpression (1, etype, checkTypes);
		statement = expr.getNextExpression ();
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		assert discard && vmxCounter.isJavaLocal ();
		container.write (writer, false);
		writer.visitStore (vmxArray, container.getType ());
		writer.visiticonst (0);
		writer.visitStore (vmxCounter, Type.INT);
		
		Label loop = new Label ();
		Label cond = new Label ();
		
		writer.visitJumpInsn (Opcodes.GOTO, cond);

		writer.visitLabel (loop);
		writer.visitLoad (vmxArray, container.getType ());
		writer.visitLoad (vmxCounter, Type.INT);
		writer.visitIincInsn (vmxCounter.getIndex (), 1);
		
		AssignArrayComponent.write (etype, assignmentType, expr, writer, false);
		statement.write (writer, true);

		writer.visitLabel (cond);
		writer.visitLoad (vmxCounter, Type.INT);
		writer.visitLoad (vmxArray, container.getType ());
		writer.visitInsn (Opcodes.ARRAYLENGTH);
		writer.visitJumpInsn (Opcodes.IF_ICMPLT, loop);
	}

}
