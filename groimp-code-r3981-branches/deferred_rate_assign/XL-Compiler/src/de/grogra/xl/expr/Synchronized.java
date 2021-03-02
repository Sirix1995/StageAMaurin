
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

import de.grogra.xl.vmx.*;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.*;

public final class Synchronized extends VoidExpression implements LocalAccess
{
	private Local mutex;
	private VMXState.Local vmxMutex;
	private Expression lock, statement;

	
	public Synchronized (Local mutex)
	{
		this.mutex = mutex;
	}


	public int getLocalCount ()
	{
		return 1;
	}

	
	public int getAccessType (int index)
	{
		return PRE_1_ASSIGNMENT | POST_USE;
	}

	
	public Local getLocal (int index)
	{
		return mutex;
	}

	
	public void setLocal (int index, Local local)
	{
		mutex = local;
	}
	

	public void complete (MethodScope scope)
	{
		vmxMutex = mutex.createVMXLocal ();
	}


	@Override
	protected void evaluateVoidImpl (VMXState t)
	{
		synchronized (lock.evaluateObject (t))
		{
			statement.evaluateAsVoid (t);
		}
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (2);
		lock = getExpression (0, OBJECT, checkTypes);
		statement = lock.getNextExpression ();
	}

	
	private Label start;
	private Label handler;

	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		assert discard && vmxMutex.isJavaLocal ();
		lock.write (writer, false);
		writer.visitInsn (Opcodes.DUP);
		writer.visitVarInsn (Opcodes.ASTORE, vmxMutex.getIndex ());
		writer.visitInsn (Opcodes.MONITORENTER);

		handler = null;
		start = new Label ();
		writer.visitLabel (start);

		statement.write (writer, true);
		writeHandler (writer);

		writer.visitVarInsn (Opcodes.ALOAD, vmxMutex.getIndex ());
		writer.visitInsn (Opcodes.MONITOREXIT);

		if (handler != null)
		{
			Label next = new Label ();
			writer.visitJumpInsn (Opcodes.GOTO, next);
			writer.visitLabel (handler);
			writer.visitVarInsn (Opcodes.ALOAD, vmxMutex.getIndex ());
			writer.visitInsn (Opcodes.MONITOREXIT);
			writer.visitInsn (Opcodes.ATHROW);
			writer.visitLabel (next);
		}
	}

	
	private void writeHandler (BytecodeWriter writer)
	{
		Label end = new Label ();
		writer.visitLabel (end);
		
		if (end.getOffset () == start.getOffset ())
		{
			return;
		}
		if (handler == null)
		{
			handler = new Label ();
		}
		writer.visitTryCatchBlock (start, end, handler, null);
	}

	
	@Override
	public void writeFinally (BytecodeWriter writer, int label, ControlTransfer transfer)
	{
		writeHandler (writer);

		writer.visitVarInsn (Opcodes.ALOAD, vmxMutex.getIndex ());
		writer.visitInsn (Opcodes.MONITOREXIT);
		super.writeFinally (writer, label, transfer);
		start = new Label ();
		writer.visitLabel (start);
	}

}
