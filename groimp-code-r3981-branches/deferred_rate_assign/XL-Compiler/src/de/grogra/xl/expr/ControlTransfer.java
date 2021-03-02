
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

import de.grogra.xl.vmx.*;
import de.grogra.xl.compiler.BytecodeWriter;

public abstract class ControlTransfer extends VoidExpression
{
	private int nesting = 0;
	

	public void setNesting (int nesting)
	{
		if (nesting < 0)
		{
			throw new IllegalArgumentException ();
		}
		this.nesting = nesting;
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ",nesting=" + nesting;
	}
	
	
	@Override
	protected final void evaluateVoidImpl (VMXState t)
	{
		AbruptCompletion c = evaluate (t);
		if (nesting == 0)
		{
			throw c;
		}
		else
		{
			throw t.newNonlocal (nesting, c, null);
		}
	}
	
	
	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		if (nesting == 0)
		{
			writeChildren (writer);
			writeLocal (writer, this);
		}
		else
		{
			writer.visitVMX ();
			writer.visiticonst (nesting);
			writeAbruptCompletion (writer);
			writer.visitaconst (null);//AUTH
			writer.visitMethodInsn (VMX_TYPE, "newNonlocal",
				"(ILde/grogra/xl/vmx/AbruptCompletion;" + BytecodeWriter.AUTH_DESCR
				+ ")Lde/grogra/xl/vmx/AbruptCompletion$Nonlocal;");
			writer.visitInsn (Opcodes.ATHROW);
		}
	}
	
	
	protected abstract void writeLocal (BytecodeWriter writer, Expression location);

	
	protected abstract void writeTransfer (BytecodeWriter writer, BreakTarget target);

	
	protected abstract void writeAbruptCompletion (BytecodeWriter writer);

	
	protected abstract AbruptCompletion evaluate (VMXState t);

}
