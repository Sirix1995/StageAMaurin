
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

import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Scope;
import de.grogra.xl.vmx.VMXState;

public strictfp class Add extends BinaryExpression
{

	@Override
	public int getSupportedTypes ()
	{
		return INT_MASK | LONG_MASK | FLOAT_MASK | DOUBLE_MASK | OBJECT_MASK;
	}

/*!!
#foreach ($type in $vmnumeric)
$pp.setType($type)

	@Override
	protected $type evaluate${pp.Type}Impl (VMXState vm)
	{
		return expr1.evaluate$pp.Type (vm) + expr2.evaluate$pp.Type (vm);
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override
	protected int evaluateIntImpl (VMXState vm)
	{
		return expr1.evaluateInt (vm) + expr2.evaluateInt (vm);
	}
// generated
// generated
// generated
	@Override
	protected long evaluateLongImpl (VMXState vm)
	{
		return expr1.evaluateLong (vm) + expr2.evaluateLong (vm);
	}
// generated
// generated
// generated
	@Override
	protected float evaluateFloatImpl (VMXState vm)
	{
		return expr1.evaluateFloat (vm) + expr2.evaluateFloat (vm);
	}
// generated
// generated
// generated
	@Override
	protected double evaluateDoubleImpl (VMXState vm)
	{
		return expr1.evaluateDouble (vm) + expr2.evaluateDouble (vm);
	}
// generated
//!! *# End of generated code

	@Override
	protected Object evaluateObjectImpl (VMXState vm)
	{
		return new StringBuffer ().append (expr1.evaluateObject (vm))
			.append (expr2.evaluateObject (vm)).toString ().intern ();
	}


	@Override
	public Expression compile (Scope scope, Expression expr1, Expression expr2)
	{
		boolean s1, s2, b;
		if ((s1 = Reflection.equal (expr1.getType (), Type.STRING))
			| (s2 = Reflection.equal (expr2.getType (), Type.STRING)))
		{
			if (!s1)
			{
				b = expr1.isPrimitiveOrStringConstant ();
				expr1 = new StringConversion ().add (expr1);
				if (b)
				{
					expr1 = expr1.toConst ();
				}
			}
			if (!s2)
			{
				b = expr2.isPrimitiveOrStringConstant ();
				expr2 = new StringConversion ().add (expr2);
				if (b)
				{
					expr2 = expr2.toConst ();
				}
			}
			return setType (Type.STRING).add (expr1).add (expr2);
		}
		else
		{
			return super.compile (scope, expr1, expr2);
		}
	}


	private static final int[] OPCODES
		= {Opcodes.IADD, Opcodes.LADD, Opcodes.FADD, Opcodes.DADD};

	@Override
	protected int[] getOpcodes ()
	{
		return OPCODES;
	}

	private static String stringBuilder (BytecodeWriter writer)
	{
		return writer.supportsVersion (Opcodes.V1_5) ? "java/lang/StringBuilder" : "java/lang/StringBuffer";
	}

	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		if (discard)
		{
			expr1.write (writer, true);
			expr2.write (writer, true);
		}
		else if (etype != OBJECT)
		{
			super.writeImpl (writer, false);
		}
		else
		{
			String sb = stringBuilder (writer);
			writer.visitTypeInsn (Opcodes.NEW, sb);
			writer.visitInsn (Opcodes.DUP);
			writer.visitMethodInsn (Opcodes.INVOKESPECIAL, sb, "<init>", "()V");
			writeAppend (writer, expr1);
			writeAppend (writer, expr2);
			writer.visitMethodInsn (Opcodes.INVOKEVIRTUAL, sb, "toString", "()Ljava/lang/String;");
		}
	}

	
	private static void writeAppend (BytecodeWriter w, Expression e)
	{
		if (e instanceof Add)
		{
			writeAppend (w, ((Add) e).expr1);
			writeAppend (w, ((Add) e).expr2);
		}
		else
		{
			if (e instanceof StringConversion)
			{
				e = e.getFirstExpression ();
			}
			if (e instanceof Pop)
			{
				w.visitInsn (Opcodes.SWAP);
			}
			else
			{
				e.write (w, false);
			}
			String sb = stringBuilder (w);
			w.visitMethodInsn
				(Opcodes.INVOKEVIRTUAL, sb, "append",
				 e.hasType (Type.STRING) ? "(Ljava/lang/String;)L" + sb + ';'
				 : '(' + getDescriptorNoBS (e.etype) + ")L" + sb + ';');
		}
	}

}


