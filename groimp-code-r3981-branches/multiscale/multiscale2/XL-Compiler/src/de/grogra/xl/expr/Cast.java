
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

import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.vmx.*;
import de.grogra.reflect.*;

public final strictfp class Cast extends EvalExpression
{
	private Expression expr;


	public Cast (Type type)
	{
		super (type);
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override				
	protected $pp.jtype evaluate${pp.Type}Impl (VMXState t)
	{
#if ($pp.Object)
		Object o = expr.evaluateObject (t);
		if ((o != null) && !getType ().isInstance (o))
		{
			throw new ClassCastException (o + " " + getType ());
		}
		return o;
#else
		switch (expr.etype)
		{
			case BOOLEAN:
				return ($type) ((expr.evaluateBoolean (t) ? 1 : 0) $pp.vm2type);
			case BYTE:
				return ($type) (expr.evaluateByte (t) $pp.vm2type);
			case SHORT:
				return ($type) (expr.evaluateShort (t) $pp.vm2type);
			case CHAR:
				return ($type) (expr.evaluateChar (t) $pp.vm2type);
			case INT:
				return ($type) (expr.evaluateInt (t) $pp.vm2type);
			case LONG:
				return ($type) (expr.evaluateLong (t) $pp.vm2type);
			case FLOAT:
				return ($type) (expr.evaluateFloat (t) $pp.vm2type);
			case DOUBLE:
				return ($type) (expr.evaluateDouble (t) $pp.vm2type);
		}
		throw new AssertionError ((Object) "Unsupported cast");
#end
	}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override				
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		switch (expr.etype)
		{
			case BOOLEAN:
				return (boolean) ((expr.evaluateBoolean (t) ? 1 : 0)  != 0);
			case BYTE:
				return (boolean) (expr.evaluateByte (t)  != 0);
			case SHORT:
				return (boolean) (expr.evaluateShort (t)  != 0);
			case CHAR:
				return (boolean) (expr.evaluateChar (t)  != 0);
			case INT:
				return (boolean) (expr.evaluateInt (t)  != 0);
			case LONG:
				return (boolean) (expr.evaluateLong (t)  != 0);
			case FLOAT:
				return (boolean) (expr.evaluateFloat (t)  != 0);
			case DOUBLE:
				return (boolean) (expr.evaluateDouble (t)  != 0);
		}
		throw new AssertionError ((Object) "Unsupported cast");
	}
// generated
// generated
	@Override				
	protected byte evaluateByteImpl (VMXState t)
	{
		switch (expr.etype)
		{
			case BOOLEAN:
				return (byte) ((expr.evaluateBoolean (t) ? 1 : 0) );
			case BYTE:
				return (byte) (expr.evaluateByte (t) );
			case SHORT:
				return (byte) (expr.evaluateShort (t) );
			case CHAR:
				return (byte) (expr.evaluateChar (t) );
			case INT:
				return (byte) (expr.evaluateInt (t) );
			case LONG:
				return (byte) (expr.evaluateLong (t) );
			case FLOAT:
				return (byte) (expr.evaluateFloat (t) );
			case DOUBLE:
				return (byte) (expr.evaluateDouble (t) );
		}
		throw new AssertionError ((Object) "Unsupported cast");
	}
// generated
// generated
	@Override				
	protected short evaluateShortImpl (VMXState t)
	{
		switch (expr.etype)
		{
			case BOOLEAN:
				return (short) ((expr.evaluateBoolean (t) ? 1 : 0) );
			case BYTE:
				return (short) (expr.evaluateByte (t) );
			case SHORT:
				return (short) (expr.evaluateShort (t) );
			case CHAR:
				return (short) (expr.evaluateChar (t) );
			case INT:
				return (short) (expr.evaluateInt (t) );
			case LONG:
				return (short) (expr.evaluateLong (t) );
			case FLOAT:
				return (short) (expr.evaluateFloat (t) );
			case DOUBLE:
				return (short) (expr.evaluateDouble (t) );
		}
		throw new AssertionError ((Object) "Unsupported cast");
	}
// generated
// generated
	@Override				
	protected char evaluateCharImpl (VMXState t)
	{
		switch (expr.etype)
		{
			case BOOLEAN:
				return (char) ((expr.evaluateBoolean (t) ? 1 : 0) );
			case BYTE:
				return (char) (expr.evaluateByte (t) );
			case SHORT:
				return (char) (expr.evaluateShort (t) );
			case CHAR:
				return (char) (expr.evaluateChar (t) );
			case INT:
				return (char) (expr.evaluateInt (t) );
			case LONG:
				return (char) (expr.evaluateLong (t) );
			case FLOAT:
				return (char) (expr.evaluateFloat (t) );
			case DOUBLE:
				return (char) (expr.evaluateDouble (t) );
		}
		throw new AssertionError ((Object) "Unsupported cast");
	}
// generated
// generated
	@Override				
	protected int evaluateIntImpl (VMXState t)
	{
		switch (expr.etype)
		{
			case BOOLEAN:
				return (int) ((expr.evaluateBoolean (t) ? 1 : 0) );
			case BYTE:
				return (int) (expr.evaluateByte (t) );
			case SHORT:
				return (int) (expr.evaluateShort (t) );
			case CHAR:
				return (int) (expr.evaluateChar (t) );
			case INT:
				return (int) (expr.evaluateInt (t) );
			case LONG:
				return (int) (expr.evaluateLong (t) );
			case FLOAT:
				return (int) (expr.evaluateFloat (t) );
			case DOUBLE:
				return (int) (expr.evaluateDouble (t) );
		}
		throw new AssertionError ((Object) "Unsupported cast");
	}
// generated
// generated
	@Override				
	protected long evaluateLongImpl (VMXState t)
	{
		switch (expr.etype)
		{
			case BOOLEAN:
				return (long) ((expr.evaluateBoolean (t) ? 1 : 0) );
			case BYTE:
				return (long) (expr.evaluateByte (t) );
			case SHORT:
				return (long) (expr.evaluateShort (t) );
			case CHAR:
				return (long) (expr.evaluateChar (t) );
			case INT:
				return (long) (expr.evaluateInt (t) );
			case LONG:
				return (long) (expr.evaluateLong (t) );
			case FLOAT:
				return (long) (expr.evaluateFloat (t) );
			case DOUBLE:
				return (long) (expr.evaluateDouble (t) );
		}
		throw new AssertionError ((Object) "Unsupported cast");
	}
// generated
// generated
	@Override				
	protected float evaluateFloatImpl (VMXState t)
	{
		switch (expr.etype)
		{
			case BOOLEAN:
				return (float) ((expr.evaluateBoolean (t) ? 1 : 0) );
			case BYTE:
				return (float) (expr.evaluateByte (t) );
			case SHORT:
				return (float) (expr.evaluateShort (t) );
			case CHAR:
				return (float) (expr.evaluateChar (t) );
			case INT:
				return (float) (expr.evaluateInt (t) );
			case LONG:
				return (float) (expr.evaluateLong (t) );
			case FLOAT:
				return (float) (expr.evaluateFloat (t) );
			case DOUBLE:
				return (float) (expr.evaluateDouble (t) );
		}
		throw new AssertionError ((Object) "Unsupported cast");
	}
// generated
// generated
	@Override				
	protected double evaluateDoubleImpl (VMXState t)
	{
		switch (expr.etype)
		{
			case BOOLEAN:
				return (double) ((expr.evaluateBoolean (t) ? 1 : 0) );
			case BYTE:
				return (double) (expr.evaluateByte (t) );
			case SHORT:
				return (double) (expr.evaluateShort (t) );
			case CHAR:
				return (double) (expr.evaluateChar (t) );
			case INT:
				return (double) (expr.evaluateInt (t) );
			case LONG:
				return (double) (expr.evaluateLong (t) );
			case FLOAT:
				return (double) (expr.evaluateFloat (t) );
			case DOUBLE:
				return (double) (expr.evaluateDouble (t) );
		}
		throw new AssertionError ((Object) "Unsupported cast");
	}
// generated
// generated
	@Override				
	protected Object evaluateObjectImpl (VMXState t)
	{
		Object o = expr.evaluateObject (t);
		if ((o != null) && !getType ().isInstance (o))
		{
			throw new ClassCastException (o + " " + getType ());
		}
		return o;
	}
//!! *# End of generated code

	@Override
	protected void evaluateVoidImpl (VMXState t)
	{
		expr.evaluateAsVoid (t);
	}

	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (1);
		expr = getExpression (0);
	}

	@Override
	public boolean discards (int index)
	{
		return etype == VOID;
	}

	@Override
	public Expression toConst ()
	{
		Expression e = getFirstExpression ();
		if ((etype == TypeId.OBJECT) && (e instanceof ObjectConst)
			&& (((ObjectConst) e).value == null))
		{
			Expression result = new ObjectConst (null, getType ());
			Compiler.copyInfo (this, result);
			return result;
		}
		else if ((((1 << etype) & TypeId.NUMERIC_MASK) != 0) && (e instanceof Constant))
		{
			return toConstImpl ();
		}
		else
		{
			return this;
		}
	}

	private static final int[] X2L
		= {Opcodes.I2L, Opcodes.NOP, Opcodes.F2L, Opcodes.D2L};

	private static final int[] X2F
		= {Opcodes.I2F, Opcodes.L2F, Opcodes.NOP, Opcodes.D2F};

	private static final int[] X2D
		= {Opcodes.I2D, Opcodes.L2D, Opcodes.F2D, Opcodes.NOP};

	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		int src = expr.etype;
		if (((1 << etype) & I_VALUE) != 0)
		{
			switch (src)
			{
				case LONG:
					writer.visitInsn (Opcodes.L2I);
					src = INT;
					break;
				case FLOAT:
					writer.visitInsn (Opcodes.F2I);
					src = INT;
					break;
				case DOUBLE:
					writer.visitInsn (Opcodes.D2I);
					src = INT;
					break;
			}
			switch (etype)
			{
				case BOOLEAN:
					if (src != BOOLEAN)
					{
						writer.visitVM2T (BOOLEAN);
					}
					break;
				case BYTE:
					if (src > BYTE)
					{
						writer.visitInsn (Opcodes.I2B);
					}
					break;
				case SHORT:
					if (src > SHORT)
					{
						writer.visitInsn (Opcodes.I2S);
					}
					break;
				case CHAR:
					if ((src != CHAR) && (src != BOOLEAN))
					{
						writer.visitInsn (Opcodes.I2C);
					}
					break;
			}
		}
		else
		{
			int opc;
			switch (etype)
			{
				case LONG:
					opc = opcode (src, X2L);
					break;
				case FLOAT:
					opc = opcode (src, X2F);
					break;
				case DOUBLE:
					opc = opcode (src, X2D);
					break;
				case OBJECT:
					if (!Reflection.isAssignableFrom (getType (), expr.getType ()))
					{
						writer.visitCheckCast (getType ());
					}
					return;
				case VOID:
					return;
				default:
					throw new AssertionError ();
			}
			if (opc != Opcodes.NOP)
			{
				writer.visitInsn (opc);
			}
		}
	}

}
