
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

public final class Finally extends VoidExpression implements LocalAccess
{
	private Local exLocal, addrLocal;
	private VMXState.Local exception, address;

	
	public Finally (Local ex, Local addr)
	{
		assert (ex != null) || (addr == null);
		this.exLocal = ex;
		this.addrLocal = addr;
	}

	
	@Override
	public boolean discards (int index)
	{
		return true;
	}


	public int getLocalCount ()
	{
		return (exLocal == null) ? 0 : (addrLocal == null) ? 1 : 2;
	}

	
	public int getAccessType (int index)
	{
		return PRE_ASSIGNMENT | POST_USE;
	}

	
	public Local getLocal (int index)
	{
		return (index > 0) ? addrLocal : exLocal;
	}

	
	public void setLocal (int index, Local local)
	{
		if (index > 0)
		{
			addrLocal = local;
		}
		else
		{
			exLocal = local;
		}
	}
	

	public void complete (MethodScope scope)
	{
		if (exLocal != null)
		{
			exception = exLocal.createVMXLocal ();
		}
		if (addrLocal != null)
		{
			address = addrLocal.createVMXLocal ();
		}
	}


	@Override
	protected void evaluateVoidImpl (VMXState t)
	{
		getFirstExpression ().evaluateAsVoid (t);
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		if (address != null)
		{
			writer.visitVarInsn (Opcodes.ASTORE, address.getIndex ());
			writeChildren (writer);
			writer.visitVarInsn (Opcodes.RET, address.getIndex ());
		}
	}

	
	@Override
	public void writeFinally (BytecodeWriter writer, int label, ControlTransfer transfer)
	{
		TryFinally t = (TryFinally) getAxisParent ();
		((Expression) t.getAxisParent ()).writeFinally (writer, label, transfer);
	}


	void writeHandler (BytecodeWriter writer, Label lbl)
	{
		if (exception != null)
		{
			writer.visitVarInsn (Opcodes.ASTORE, exception.getIndex ());
		}
		writeJSR (writer, lbl);
		if (exception != null)
		{
			writer.visitVarInsn (Opcodes.ALOAD, exception.getIndex ());
		}
		writer.visitInsn (Opcodes.ATHROW);
	}


	void writeJSR (BytecodeWriter writer, Label lbl)
	{
		if (address != null)
		{
			writer.visitJumpInsn (Opcodes.JSR, lbl);
		}
		else
		{
			writeChildren (writer);
		}
	}

}
