
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

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.vmx.*;

public final class ArrayGenerator extends Variable
	implements Generator, LocalAccess
{
	private Expression expr, statement;
	private Local array, counter;
	private VMXState.Local vmxArray, vmxCounter;
	
	
	public ArrayGenerator (Type type, Local array, Local counter)
	{
		super (type);
		this.array = array;
		this.counter = counter;
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override
	protected $type evaluate${pp.Type}Impl (VMXState t)
	{
		$type[] a = ($type[]) expr.evaluateObject (t);
		for (int i = 0; i < a.length; i++)
		{
			t.${pp.prefix}push (a[i] $pp.type2vm);
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
		boolean[] a = (boolean[]) expr.evaluateObject (t);
		for (int i = 0; i < a.length; i++)
		{
			t.ipush (a[i]  ? 1 : 0);
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
		byte[] a = (byte[]) expr.evaluateObject (t);
		for (int i = 0; i < a.length; i++)
		{
			t.ipush (a[i] );
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
		short[] a = (short[]) expr.evaluateObject (t);
		for (int i = 0; i < a.length; i++)
		{
			t.ipush (a[i] );
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
		char[] a = (char[]) expr.evaluateObject (t);
		for (int i = 0; i < a.length; i++)
		{
			t.ipush (a[i] );
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
		int[] a = (int[]) expr.evaluateObject (t);
		for (int i = 0; i < a.length; i++)
		{
			t.ipush (a[i] );
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
		long[] a = (long[]) expr.evaluateObject (t);
		for (int i = 0; i < a.length; i++)
		{
			t.lpush (a[i] );
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
		float[] a = (float[]) expr.evaluateObject (t);
		for (int i = 0; i < a.length; i++)
		{
			t.fpush (a[i] );
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
		double[] a = (double[]) expr.evaluateObject (t);
		for (int i = 0; i < a.length; i++)
		{
			t.dpush (a[i] );
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
		Object[] a = (Object[]) expr.evaluateObject (t);
		for (int i = 0; i < a.length; i++)
		{
			t.apush (a[i] );
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
		checkExpressionCount (2);
		expr = getObjectExpression (0, getType ().getArrayType (), checkTypes);
		statement = expr.getNextExpression ();
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


	@Override
	public Expression toAssignment (int assignmentType)
	{
		return new AssignArrayGenerator (getType (), array, counter, assignmentType)
			.receiveChildren (this);
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		assert discard && vmxCounter.isJavaLocal ();
		expr.write (writer, false);
		writer.visitStore (vmxArray, expr.getType ());
		writer.visiticonst (0);
		writer.visitStore (vmxCounter, Type.INT);
		
		Label loop = new Label ();
		Label cond = new Label ();
		
		writer.visitJumpInsn (Opcodes.GOTO, cond);

		writer.visitLabel (loop);
		writer.visitLoad (vmxArray, expr.getType ());
		writer.visitLoad (vmxCounter, Type.INT);
		writer.visitALoad (etype);
		writer.visitIincInsn (vmxCounter.getIndex (), 1);

		statement.write (writer, true);

		writer.visitLabel (cond);
		writer.visitLoad (vmxCounter, Type.INT);
		writer.visitLoad (vmxArray, expr.getType ());
		writer.visitInsn (Opcodes.ARRAYLENGTH);
		writer.visitJumpInsn (Opcodes.IF_ICMPLT, loop);
	}

}
