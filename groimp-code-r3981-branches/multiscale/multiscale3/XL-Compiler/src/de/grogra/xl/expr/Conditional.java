
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
import org.objectweb.asm.Label;

import de.grogra.reflect.*;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.CompilerBase;
import de.grogra.xl.compiler.scope.Scope;
import de.grogra.xl.vmx.*;

public final class Conditional extends ConstExpression
{
	private Expression cond;
	private Expression expr1, expr2;


	public Conditional (Type type)
	{
		super (type);
	}


	public Conditional ()
	{
		super ();
	}


	@Override
	public boolean allowsIteration (int index)
	{
		return index == 0;
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override				
	protected $pp.jtype evaluate${pp.Type}Impl (VMXState t)
	{
		return cond.evaluateBoolean (t) ? expr1.evaluate$pp.Type (t)
			: expr2.evaluate$pp.Type (t);
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override				
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		return cond.evaluateBoolean (t) ? expr1.evaluateBoolean (t)
			: expr2.evaluateBoolean (t);
	}
// generated
// generated
// generated
	@Override				
	protected byte evaluateByteImpl (VMXState t)
	{
		return cond.evaluateBoolean (t) ? expr1.evaluateByte (t)
			: expr2.evaluateByte (t);
	}
// generated
// generated
// generated
	@Override				
	protected short evaluateShortImpl (VMXState t)
	{
		return cond.evaluateBoolean (t) ? expr1.evaluateShort (t)
			: expr2.evaluateShort (t);
	}
// generated
// generated
// generated
	@Override				
	protected char evaluateCharImpl (VMXState t)
	{
		return cond.evaluateBoolean (t) ? expr1.evaluateChar (t)
			: expr2.evaluateChar (t);
	}
// generated
// generated
// generated
	@Override				
	protected int evaluateIntImpl (VMXState t)
	{
		return cond.evaluateBoolean (t) ? expr1.evaluateInt (t)
			: expr2.evaluateInt (t);
	}
// generated
// generated
// generated
	@Override				
	protected long evaluateLongImpl (VMXState t)
	{
		return cond.evaluateBoolean (t) ? expr1.evaluateLong (t)
			: expr2.evaluateLong (t);
	}
// generated
// generated
// generated
	@Override				
	protected float evaluateFloatImpl (VMXState t)
	{
		return cond.evaluateBoolean (t) ? expr1.evaluateFloat (t)
			: expr2.evaluateFloat (t);
	}
// generated
// generated
// generated
	@Override				
	protected double evaluateDoubleImpl (VMXState t)
	{
		return cond.evaluateBoolean (t) ? expr1.evaluateDouble (t)
			: expr2.evaluateDouble (t);
	}
// generated
// generated
// generated
	@Override				
	protected Object evaluateObjectImpl (VMXState t)
	{
		return cond.evaluateBoolean (t) ? expr1.evaluateObject (t)
			: expr2.evaluateObject (t);
	}
// generated
//!! *# End of generated code

	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (3);
		cond = getExpression (0, BOOLEAN, checkTypes);
		expr1 = cond.getNextExpression ();
		expr2 = expr1.getNextExpression ();
	}


	public Expression compile (Scope scope, Expression c, Expression e1, Expression e2)
	{
		Expression e;
		if (!(c.etype == BOOLEAN))
		{
			throw new IllegalOperandTypeException
				(I18N.msg ("expr.unexpected-type", c.getType (), Type.BOOLEAN));
		}
		int t1 = e1.etype, t2 = e2.etype, t;
		if (t1 == t2)
		{
			t = t1;
		}
		else
		{
			int m = (1 << t1) | (1 << t2);
			if ((m & ~NUMERIC_MASK) == 0)
			{
				boolean b = t1 < t2;
				if (b)
				{
					t = t1; t1 = t2; t2 = t;
					e = e1; e1 = e2; e2 = e;
				}
				if (t1 != INT)
				{
					t = (t1 == CHAR) ? INT : t1;
				}
				else if (e1 instanceof Constant)
				{
					e1.linkGraph (true);
					t1 = e1.evaluateInt (null);
					t = INT;
					switch (t2)
					{
/*!!
#foreach ($type in ["byte", "short", "char"])
$pp.setType($type)
						case $pp.TYPE:
							if ((${pp.wrapper}.MIN_VALUE <= t1)
								&& (t1 <= ${pp.wrapper}.MAX_VALUE))
							{
								t = $pp.TYPE;
							}
							break;
#end
!!*/
//!! #* Start of generated code
// generated
						case BYTE:
							if ((Byte.MIN_VALUE <= t1)
								&& (t1 <= Byte.MAX_VALUE))
							{
								t = BYTE;
							}
							break;
// generated
						case SHORT:
							if ((Short.MIN_VALUE <= t1)
								&& (t1 <= Short.MAX_VALUE))
							{
								t = SHORT;
							}
							break;
// generated
						case CHAR:
							if ((Character.MIN_VALUE <= t1)
								&& (t1 <= Character.MAX_VALUE))
							{
								t = CHAR;
							}
							break;
//!! *# End of generated code
					}
				}
				else
				{
					t = INT;
				}
				if (b)
				{
					e = e1; e1 = e2; e2 = e;
				}
			}
			else
			{
				t = -1;
			}
		}
		Type type;
		if (t == OBJECT)
		{
			type = e1.getType ();
			if (!Reflection.isAssignableFrom (type, e2.getType ()))
			{
				if (Reflection.isAssignableFrom (e2.getType (), type))
				{
					type = e2.getType ();
				}
				else
				{
					t = -1;
				}
			}
		}
		else if (t >= 0)
		{
			type = Reflection.getType (t);
		}
		else
		{
			type = null;
		}
		if (t < 0)
		{
			throw new IllegalOperandTypeException
				(I18N.msg ("expr.conditional-incompatible-types",
						   e1.getType (), e2.getType ()));
		}
		if (t != OBJECT)
		{
			e1 = e1.cast (Reflection.getType (t));
			e2 = e2.cast (Reflection.getType (t));
		}
		return setType (type).add (c).add (e1).add (e2);
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		Label condFalseLabel = new Label ();

		int c1 = cond.writeConditional (writer, condFalseLabel, null);
		switch (c1)
		{
			case 1:
				expr1.write (writer, discard);
				return;
			case -1:
				writer.visitLabel (condFalseLabel);
				expr2.write (writer, discard);
				return;
		}
		
		expr1.write (writer, discard);
		
		Label exitLabel = new Label ();
		writer.visitJumpInsn (Opcodes.GOTO, exitLabel);
		
		writer.visitLabel (condFalseLabel);
		expr2.write (writer, discard);
		
		writer.visitLabel (exitLabel);
	}

	
	@Override
	public boolean isConditional ()
	{
		return true;
	}


	@Override
	public int writeConditional (BytecodeWriter writer, Label falseLabel, Label trueLabel)
	{
		Label condFalseLabel = new Label ();

		int c1 = cond.writeConditional (writer, condFalseLabel, null);
		switch (c1)
		{
			case 1:
				return expr1.writeConditional (writer, falseLabel, trueLabel);
			case -1:
				writer.visitLabel (condFalseLabel);
				return expr2.writeConditional (writer, falseLabel, trueLabel);
		}

		boolean f = falseLabel == null;
		boolean t = trueLabel == null;
		if (f)
		{
			falseLabel = new Label ();
		}
		if (t)
		{
			trueLabel = new Label ();
		}
		c1 = expr1.writeConditional (writer, falseLabel, trueLabel);
		
		writer.visitLabel (condFalseLabel);
		int c2 = expr2.writeConditional (writer, f ? null : falseLabel, t ? null : trueLabel);
		
		if (f)
		{
			writer.visitLabel (falseLabel);
		}
		if (t)
		{
			writer.visitLabel (trueLabel);
		}
		
		return (c1 == c2) ? c1 : 0;
	}

}
