
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

public class ArrayInit extends EvalExpression
{

	public ArrayInit (Type type)
	{
		super (type);
	}


	@Override
	protected Object evaluateObjectImpl (VMXState t)
	{
		Type type = getType ();
		int length = getExpressionCount();
		Object array = type.createArray (length);
		int i = 0, typeId = type.getComponentType ().getTypeId ();
		for (Expression e = getFirstExpression (); e != null;
			 e = e.getNextExpression (), i++)
		{
			switch (typeId)
			{
				case BOOLEAN:
					((boolean[]) array)[i] = e.evaluateBoolean (t);
					break;
				case BYTE:
					((byte[]) array)[i] = e.evaluateByte (t);
					break;
				case SHORT:
					((short[]) array)[i] = e.evaluateShort (t);
					break;
				case CHAR:
					((char[]) array)[i] = e.evaluateChar (t);
					break;
				case INT:
					((int[]) array)[i] = e.evaluateInt (t);
					break;
				case LONG:
					((long[]) array)[i] = e.evaluateLong (t);
					break;
				case FLOAT:
					((float[]) array)[i] = e.evaluateFloat (t);
					break;
				case DOUBLE:
					((double[]) array)[i] = e.evaluateDouble (t);
					break;
				case OBJECT:
					((Object[]) array)[i] = e.evaluateObject (t);
					break;
				default:
					throw new AssertionError ();
			}
		}
		return array;
	}


	@Override
	public void link (boolean checkTypes)
	{
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		if (discard)
		{
			for (Expression e = getFirstExpression (); e != null;
				 e = e.getNextExpression ())
			{
				e.write (writer, true);
			}
		}
		else
		{
			writer.visiticonst (getExpressionCount());
			writer.visitNewArray (getType ().getComponentType ());
			int i = 0;
			for (Expression e = getFirstExpression (); e != null;
				 e = e.getNextExpression (), i++)
			{
				if (e instanceof Constant)
				{
					Object c = e.evaluateAsObject (null);
					switch (e.etype)
					{
						case BOOLEAN:
							if (Boolean.FALSE.equals (c))
							{
								continue;
							}
							break;
						case CHAR:
							if (((Character) c).charValue () == 0)
							{
								continue;
							}
							break;
						case BYTE:
						case SHORT:
						case INT:
						case LONG:
							if (((Number) c).longValue () == 0)
							{
								continue;
							}
							break;
						case FLOAT:
							if (((Float) c).equals (Float.valueOf (0)))
							{
								continue;
							}
							break;
						case DOUBLE:
							if (((Double) c).equals (Double.valueOf (0)))
							{
								continue;
							}
							break;
						case OBJECT:
							if (c == null)
							{
								continue;
							}
							break;
					}
				}
				writer.visitInsn (Opcodes.DUP);
				writer.visiticonst (i);
				e.write (writer, false);
				writer.visitAStore (e.etype);
			}
		}
	}

}
