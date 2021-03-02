
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

import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.vmx.*;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;

public final class Return extends ControlTransfer
{
	private Expression expr;
	private final int iteratedType;
	private final MethodScope scope;


	public Return (MethodScope scope, Type iteratedType)
	{
		this.scope = scope;
		this.iteratedType = (iteratedType == null) ? VOID
			: iteratedType.getTypeId ();
	}

	
	public final MethodScope getScope ()
	{
		return scope;
	}


	@Override
	protected AbruptCompletion evaluate (VMXState t)
	{
		if (expr == null)
		{
			switch (iteratedType)
			{
				case BOOLEAN:
				case BYTE:
				case SHORT:
				case CHAR:
				case INT:
					return t.ireturn (0);
				case LONG:
					return t.lreturn (0);
				case FLOAT:
					return t.freturn (0);
				case DOUBLE:
					return t.dreturn (0);
				case OBJECT:
					return t.areturn (null);
				case VOID:
					return t.vreturn ();
				default:
					throw new AssertionError ();
			}
		}
		return expr.evaluateRet (t);
	}


	@Override
	public void link (boolean checkTypes)
	{
		expr = getFirstExpression ();
		if ((expr != null) && (expr.etype == VOID))
		{
			throw new AssertionError ();
		}
	}

	
	private VMXState.Local result;

	@Override
	protected void writeLocal (BytecodeWriter writer, Expression location)
	{
		if (expr != null)
		{
			result = getScope ().getResultLocal ();
			if (result != null)
			{
				writer.visitStore (result, expr.getType ());
			}
		}
		location.writeFinally (writer, -1, this);
	}


	@Override
	protected void writeTransfer (BytecodeWriter writer, BreakTarget e)
	{
		if (expr == null)
		{
			writer.visitNull (iteratedType);
			writer.visitReturn (iteratedType);
		}
		else
		{
			if (result != null)
			{
				writer.visitLoad (result, expr.getType ());
			}
			writer.visitReturn (expr.etype);
		}
	}

	
	@Override
	protected void writeAbruptCompletion (BytecodeWriter writer)
	{
		writer.visitVMX ();
		int t;
		if (expr == null)
		{
			t = iteratedType;
			writer.visitNull (t);
		}
		else
		{
			t = expr.etype;
			expr.write (writer, false);
		}
		writer.visitMethodInsn
			(VMX_TYPE, Reflection.getJVMPrefix (t) + "return");
	}

}
