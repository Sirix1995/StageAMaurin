
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
import de.grogra.xl.vmx.*;

public final class Break extends ControlTransfer
{
	private final int label;


	public Break (int label)
	{
		this.label = label;
	}

	
	public int getLabel ()
	{
		return label;
	}


	@Override
	protected AbruptCompletion evaluate (VMXState t)
	{
		return t.newBreak (label);
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ",label=" + label;
	}
	
	
	@Override
	protected void writeLocal (BytecodeWriter writer, Expression location)
	{
		location.writeFinally (writer, label, this);
	}
	
	
	@Override
	protected void writeTransfer (BytecodeWriter writer, BreakTarget e)
	{
		writer.visitJumpInsn (Opcodes.GOTO, e.getBytecodeLabel ());
	}

	
	@Override
	protected void writeAbruptCompletion (BytecodeWriter writer)
	{
		writer.visitVMX ();
		writer.visiticonst (label);
		writer.visitMethodInsn (VMX_TYPE, "newBreak");
	}

}
