
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

import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;

public final class TryFinally extends VoidExpression
{
	private Expression block;
	private Finally fblock;

	@Override
	protected void evaluateVoidImpl (VMXState t)
	{
		try
		{
			block.evaluateAsVoid (t);
		}
		finally
		{
			fblock.evaluateAsVoid (t);
		}
	}

	
	@Override
	public boolean needsEmptyOperandStackForFinally ()
	{
		return true;
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (2);
		block = getFirstExpression ();
		fblock = (Finally) block.getNextExpression ();
	}


	private Label start;
	private Label routine;
	private Label handler;

	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		handler = null;
		start = new Label ();
		routine = new Label ();
		writer.visitLabel (start);

		block.write (writer, true);
		writeHandler (writer);

		Label next = new Label ();
		writer.visitJumpInsn (Opcodes.GOTO, next);
		
		writer.visitLabel (routine);
		fblock.write (writer, true);
		
		if (handler != null)
		{
			writer.visitLabel (handler);
			fblock.writeHandler (writer, routine);
		}

		writer.visitLabel (next);
		fblock.writeJSR (writer, routine);
	}

	
	@Override
	public void writeFinally (BytecodeWriter writer, int label, ControlTransfer transfer)
	{
		writeHandler (writer);

		fblock.writeJSR (writer, routine);
		super.writeFinally (writer, label, transfer);
		start = new Label ();
		writer.visitLabel (start);
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

}
