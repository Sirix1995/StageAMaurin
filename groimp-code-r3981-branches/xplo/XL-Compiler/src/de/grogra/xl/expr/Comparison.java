
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
import de.grogra.xl.compiler.scope.Scope;

public abstract class Comparison extends EvalExpression
{
	protected Expression expr1, expr2;
	protected int ctype = -1;


	public Comparison ()
	{
		super (Type.BOOLEAN);
	}


	public abstract int getSupportedTypes ();


	@Override
	public final int getSupportedTypes (int arg)
	{
		return ((arg == 0) || (arg == 1)) ? getSupportedTypes () : 0;
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (2);
		expr1 = getExpression (0);
		expr2 = getExpression (1);
		ctype = expr1.etype;
		if (ctype != expr2.etype)
		{
			throw new AssertionError ();
		}
	}


	@Override
	public Expression compile (Scope scope, Expression e1, Expression e2)
	{
		int promotedType = getPromotedType (e1, e2),
			supported = getSupportedTypes ();
		if (promotedType >= 0)
		{
			for (int i = promotedType;
				 Reflection.isWideningConversion (promotedType, i);
				 i++)
			{
				if (((1 << i) & supported) != 0)
				{
					if (i != OBJECT)
					{
						Type t = Reflection.getType (i);
						e1 = e1.implicitConversion (scope, t);
						e2 = e2.implicitConversion (scope, t);
					}
					else
					{
						if (!Reflection.isCastableFrom (e1.getType (), e2.getType ())
							&& !Reflection.isCastableFrom (e2.getType (), e1.getType ()))
						{
							throw new IllegalOperandTypeException
								(I18N.msg ("expr.illegal-optype", getClass ().getName ()));
						}
					}
					return add (e1).add (e2);
				}
			}
		}
		throw new IllegalOperandTypeException
			(I18N.msg ("expr.illegal-optype", getClass ().getName ()));
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		writeConditional (writer, discard);
	}

	
	@Override
	public boolean isConditional ()
	{
		return true;
	}

	
	protected boolean isNullOrBooleanConst (Expression e)
	{
		return ((ctype == INT) && (e instanceof IntConst)) ? ((IntConst) e).value == 0
			: ((ctype == OBJECT) && (e instanceof ObjectConst)) ? ((ObjectConst) e).value == null
			: (ctype == BOOLEAN);
	}


	int writeBooleanEquals
		(BytecodeWriter writer, Label falseLabel, Label trueLabel,
		 boolean equals)
	{
		Expression e1, e2;
		if (isNullOrBooleanConst (expr1))
		{
			e1 = expr2;
			e2 = expr1;
		}
		else
		{
			e1 = expr1;
			e2 = expr2;
		}
		if (e2 instanceof BooleanConst)
		{
			boolean b = ((BooleanConst) e2).value == equals;
			return e1.writeConditional
				(writer, b ? falseLabel : trueLabel,
				 b ? trueLabel : falseLabel);
		}
		else
		{
			e1.write (writer, false);
			Label t = new Label ();
			e2.writeConditional (writer, null, t);
			
			writer.visiticonst (1);
			writer.visitInsn (Opcodes.IXOR);
			
			writer.visitLabel (t);
			if (trueLabel == null)
			{
				if (falseLabel != null)
				{
					writer.visitJumpInsn (equals ? Opcodes.IFEQ : Opcodes.IFNE, falseLabel);
				}
				else
				{
					writer.visitInsn (Opcodes.POP);
				}
			}
			else
			{
				writer.visitJumpInsn (equals ? Opcodes.IFNE : Opcodes.IFEQ, trueLabel);
				if (falseLabel != null)
				{
					writer.visitJumpInsn (Opcodes.GOTO, falseLabel);
				}
			}
			return 0;
		}
	}


	int writeIntObjectComparison
		(BytecodeWriter writer, Label falseLabel, Label trueLabel,
		 int ifNotCmpX0, int ifCmpX0,
		 int ifNotCmp0X, int ifCmp0X,
		 int ifNotCmpXY, int ifCmpXY)
	{
		Expression e1, e2;
		if (isNullOrBooleanConst (expr1))
		{
			e1 = expr2;
			e2 = expr1;
			ifNotCmpX0 = ifNotCmp0X;
			ifCmpX0 = ifCmp0X;
		}
		else
		{
			e1 = expr1;
			e2 = expr2;
		}
		if (isNullOrBooleanConst (e2))
		{
			e1.write (writer, false);
			if (trueLabel == null)
			{
				if (falseLabel != null)
				{
					writer.visitJumpInsn (ifNotCmpX0, falseLabel);
				}
				else
				{
					writer.visitInsn (Opcodes.POP);
				}
			}
			else
			{
				writer.visitJumpInsn (ifCmpX0, trueLabel);
				if (falseLabel != null)
				{
					writer.visitJumpInsn (Opcodes.GOTO, falseLabel);
				}
			}
		}
		else
		{
			e1.write (writer, false);
			e2.write (writer, false);
			if (trueLabel == null)
			{
				if (falseLabel != null)
				{
					writer.visitJumpInsn (ifNotCmpXY, falseLabel);
				}
				else
				{
					writer.visitInsn (Opcodes.POP2);
				}
			}
			else
			{
				writer.visitJumpInsn (ifCmpXY, trueLabel);
				if (falseLabel != null)
				{
					writer.visitJumpInsn (Opcodes.GOTO, falseLabel);
				}
			}
		}
		return 0;
	}


	int writeLFDComparison (BytecodeWriter writer, Label falseLabel, Label trueLabel,
							int comparison, int falseOpcode, int trueOpcode)
	{
		expr1.write (writer, false);
		expr2.write (writer, false);
		writer.visitInsn (comparison);
		if (trueLabel == null)
		{
			if (falseLabel != null)
			{
				writer.visitJumpInsn (falseOpcode, falseLabel);
			}
			else
			{
				writer.visitInsn (Opcodes.POP);
			}
		}
		else
		{
			writer.visitJumpInsn (trueOpcode, trueLabel);
			if (falseLabel != null)
			{
				writer.visitJumpInsn (Opcodes.GOTO, falseLabel);
			}
		}
		return 0;
	}

}
