
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

import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Type;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.vmx.VMXState;
import de.grogra.xl.query.Graph;
import de.grogra.xl.query.Producer;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.query.RuntimeModel;

public final class Production extends Expression implements LocalAccess
{
	static final Type PRODUCER = ClassAdapter.wrap (Producer.class);

	private Local state;
	private VMXState.Local vmxState;

	private final int arrow;


	public Production (Local state, int arrow)
	{
		super (Type.VOID);
		this.state = state;
		this.arrow = arrow;
	}

	
	@Override
	public boolean discards (int index)
	{
		return true;
	}


	public int getLocalCount ()
	{
		return 1;
	}

	
	public int getAccessType (int index)
	{
		return PRE_USE | POST_USE;
	}

	
	public Local getLocal (int index)
	{
		return state;
	}

	
	public void setLocal (int index, Local local)
	{
		state = local;
	}
	

	public void complete (MethodScope scope)
	{
		vmxState = state.createVMXLocal ();
	}


	@Override
	protected final void evaluateVoidImpl (VMXState vmx)
	{
		Producer p = (Producer) vmx.aget (vmxState, null);
		boolean apply = p.producer$beginExecution (arrow);
		if (apply)
		{
			for (Expression e = getFirstExpression (); e != null;
				 e = e.getNextExpression ())
			{
				e.evaluateAsVoid (vmx);
			}
		}
		p.producer$endExecution (apply);
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		writer.visitLoad (vmxState, state.getType ());
		writer.visiticonst (arrow);
		writer.visitMethodInsn (PRODUCER, "producer$beginExecution");
		Label labFalse = new Label ();
		writer.visitJumpInsn (Opcodes.IFEQ, labFalse);
		writeChildren (writer);
		writer.visitLoad (vmxState, state.getType ());
		writer.visiticonst (1);
		Label labTrue = new Label ();
		writer.visitJumpInsn (Opcodes.GOTO, labTrue);
		writer.visitLabel (labFalse);
		writer.visitLoad (vmxState, state.getType ());
		writer.visiticonst (0);
		writer.visitLabel (labTrue);
		writer.visitMethodInsn (PRODUCER, "producer$endExecution");
	}

}
