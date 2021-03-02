
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

public final class Range extends BinaryExpression
	implements Generator, LocalAccess
{
	private Expression statement;
	private Local max, counter;
	private VMXState.Local vmxMax, vmxCounter;

	
	public void setLocals (Local counter, Local max)
	{
		this.counter = counter;
		this.max = max;
	}


	@Override
	public int getSupportedTypes ()
	{
		return INT_MASK | LONG_MASK;
	}


	@Override
	protected int evaluateIntImpl (VMXState t)
	{
		int i = expr1.evaluateInt (t), max = expr2.evaluateInt (t);
		while (i <= max)
		{
			t.ipush (i++);
			statement.evaluateAsVoid (t);
		}
		return 0;
	}


	@Override
	protected long evaluateLongImpl (VMXState t)
	{
		long i = expr1.evaluateLong (t), max = expr2.evaluateLong (t);
		while (i <= max)
		{
			t.lpush (i++);
			statement.evaluateAsVoid (t);
		}
		return 0;
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (3);
		expr1 = getExpression (0, etype, checkTypes);
		expr2 = getExpression (1, etype, checkTypes);
		statement = expr2.getNextExpression ();
	}


	public int getGeneratorType ()
	{
		return LOCAL;
	}


	public void setBreakTarget (BreakTarget target)
	{
	}

	
	@Override
	public Expression toConst ()
	{
		return this;
	}


	public int getLocalCount ()
	{
		return 2;
	}

	
	public int getAccessType (int index)
	{
		return PRE_2_ASSIGNMENT | POST_USE;
	}

	
	public Local getLocal (int index)
	{
		return (index == 0) ? counter : max;
	}

	
	public void setLocal (int index, Local local)
	{
		if (index == 0)
		{
			counter = local;
		}
		else
		{
			max = local;
		}
	}
	

	public void complete (MethodScope scope)
	{
		vmxCounter = counter.createVMXLocal ();
		vmxMax = max.createVMXLocal ();
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		assert discard && vmxCounter.isJavaLocal () && vmxMax.isJavaLocal ();

		Label loop = new Label ();
		Label cond = new Label ();
		
		expr1.write (writer, false);
		expr2.write (writer, false);
		writer.visitStore (vmxMax, getType ());
		writer.visitDup (etype);
		writer.visitStore (vmxCounter, getType ());
		writer.visitJumpInsn (Opcodes.GOTO, cond);

		writer.visitLabel (loop);
		writer.visitLoad (vmxCounter, getType ());
		statement.write (writer, true);
		
		if (etype == INT)
		{
			writer.visitIincInsn (vmxCounter.getIndex (), 1);
			writer.visitLoad (vmxCounter, Type.INT);
		}
		else
		{
			writer.visitLoad (vmxCounter, Type.LONG);
			writer.visitInsn (Opcodes.LCONST_1);
			writer.visitInsn (Opcodes.LADD);
			writer.visitInsn (Opcodes.DUP2);
			writer.visitStore (vmxCounter, Type.LONG);
		}

		writer.visitLabel (cond);
		writer.visitLoad (vmxMax, getType ());
		if (etype == INT)
		{
			writer.visitJumpInsn (Opcodes.IF_ICMPLE, loop);
		}
		else
		{
			writer.visitInsn (Opcodes.LCMP);
			writer.visitJumpInsn (Opcodes.IFLE, loop);
		}
	}

}
