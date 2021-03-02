
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

import org.objectweb.asm.*;

import de.grogra.reflect.Type;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Scope;
import de.grogra.xl.vmx.*;

public final class Guard extends Expression implements Generator
{
	private Expression expr, condition, statement;
	

/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override
	protected $type evaluate${pp.Type}Impl (VMXState t)
	{
		$type value = expr.evaluate$pp.Type (t);
		if (condition.evaluateBoolean (t))
		{
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
		boolean value = expr.evaluateBoolean (t);
		if (condition.evaluateBoolean (t))
		{
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
		byte value = expr.evaluateByte (t);
		if (condition.evaluateBoolean (t))
		{
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
		short value = expr.evaluateShort (t);
		if (condition.evaluateBoolean (t))
		{
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
		char value = expr.evaluateChar (t);
		if (condition.evaluateBoolean (t))
		{
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
		int value = expr.evaluateInt (t);
		if (condition.evaluateBoolean (t))
		{
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
		long value = expr.evaluateLong (t);
		if (condition.evaluateBoolean (t))
		{
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
		float value = expr.evaluateFloat (t);
		if (condition.evaluateBoolean (t))
		{
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
		double value = expr.evaluateDouble (t);
		if (condition.evaluateBoolean (t))
		{
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
		Object value = expr.evaluateObject (t);
		if (condition.evaluateBoolean (t))
		{
			t.apush (value );
			statement.evaluateAsVoid (t);
		}
		return null;
	}
// generated
//!! *# End of generated code

	@Override
	protected void evaluateVoidImpl (VMXState t)
	{
		expr.evaluateAsVoid (t);
		if (condition.evaluateBoolean (t))
		{
			statement.evaluateAsVoid (t);
		}
	}

	@Override
	public Expression compile (Scope scope, Expression e1, Expression e2)
	{
		e2 = e2.implicitConversion (scope, Type.BOOLEAN);
		return setType (e1.getType ()).add (e1).add (e2);
	}

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
		expr = getExpression (0, etype, checkTypes);
		condition = getExpression (1, BOOLEAN, checkTypes);
		statement = condition.getNextExpression ();
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		expr.write (writer, etype == VOID);
		Label falseLabel = new Label ();
		condition.writeConditional (writer, falseLabel, null);
		statement.write (writer, true);
		Label exit = new Label ();
		writer.visitJumpInsn (Opcodes.GOTO, exit);
		writer.visitLabel (falseLabel);
		if (etype != VOID)
		{
			writer.visitPop (expr.etype);
		}
		writer.visitLabel (exit);
	}

}
