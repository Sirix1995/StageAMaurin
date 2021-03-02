
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

import de.grogra.xl.lang.*;
import de.grogra.xl.vmx.*;
import de.grogra.xl.compiler.BytecodeWriter;

public final class Yield extends VoidExpression
{
	private Expression callback, expr;
	private int typeId;


	@Override
	public boolean allowsIteration (int index)
	{
		return true;
	}


	@Override
	protected void evaluateVoidImpl (VMXState t)
	{
		try
		{
			switch (typeId)
			{
/*!!
#foreach ($type in $types_void)
$pp.setType($type)
				case $pp.TYPE:
					((${pp.Type}Consumer) callback.evaluateObject (t)).consume (
#if (!$pp.void)
						expr.evaluate$pp.Type (t)
#end
						);
					return;
#end
!!*/
//!! #* Start of generated code
// generated
				case BOOLEAN:
					((BooleanConsumer) callback.evaluateObject (t)).consume (
						expr.evaluateBoolean (t)
						);
					return;
// generated
				case BYTE:
					((ByteConsumer) callback.evaluateObject (t)).consume (
						expr.evaluateByte (t)
						);
					return;
// generated
				case SHORT:
					((ShortConsumer) callback.evaluateObject (t)).consume (
						expr.evaluateShort (t)
						);
					return;
// generated
				case CHAR:
					((CharConsumer) callback.evaluateObject (t)).consume (
						expr.evaluateChar (t)
						);
					return;
// generated
				case INT:
					((IntConsumer) callback.evaluateObject (t)).consume (
						expr.evaluateInt (t)
						);
					return;
// generated
				case LONG:
					((LongConsumer) callback.evaluateObject (t)).consume (
						expr.evaluateLong (t)
						);
					return;
// generated
				case FLOAT:
					((FloatConsumer) callback.evaluateObject (t)).consume (
						expr.evaluateFloat (t)
						);
					return;
// generated
				case DOUBLE:
					((DoubleConsumer) callback.evaluateObject (t)).consume (
						expr.evaluateDouble (t)
						);
					return;
// generated
				case OBJECT:
					((ObjectConsumer) callback.evaluateObject (t)).consume (
						expr.evaluateObject (t)
						);
					return;
// generated
				case VOID:
					((VoidConsumer) callback.evaluateObject (t)).consume (
						);
					return;
//!! *# End of generated code
			}
		}
		catch (Exception e)
		{
			throw (e instanceof RuntimeException)
				? (RuntimeException) e : t.newThrow (e);
		}
	}


	@Override
	public void link (boolean checkTypes)
	{
		callback = getExpression (0, OBJECT, checkTypes);
		expr = callback.getNextExpression ();
		if (expr == null)
		{
			typeId = VOID;
		}
		else if ((typeId = expr.etype) == VOID)
		{
			throw new AssertionError ();
		}
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		callback.write (writer, false);
		if (expr != null)
		{
			expr.write (writer, false);
		}
		writer.visitMethodInsn
			(de.grogra.xl.compiler.XMethod.getConsumerType (typeId), "consume");
	}

}
