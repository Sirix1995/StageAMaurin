
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

public final class InstanceOf extends EvalExpression
{
	private Expression expr;
	private final Type type;


	public InstanceOf (Type type)
	{
		super (Type.BOOLEAN);
		this.type = type;
	}


	@Override
	protected boolean evaluateBooleanImpl (VMXState vm)
	{
		return type.isInstance (expr.evaluateObject (vm));
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (1);
		expr = getExpression (0, OBJECT, checkTypes);
	}


	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		writer.visitTypeInsn (Opcodes.INSTANCEOF, type);
	}

}