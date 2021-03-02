
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

public final class TryCatch extends VoidExpression
{

	@Override
	protected void evaluateVoidImpl (VMXState t)
	{
		Expression e = getFirstExpression ();
		try
		{
			e.evaluateAsVoid (t);
		}
		catch (Throwable ex)
		{
			AbruptCompletion.Throw w;
			if (ex instanceof AbruptCompletion.Throw)
			{
				w = (AbruptCompletion.Throw) ex;
				ex = w.getCause ();
			}
			else
			{
				w = null;
			}
			while ((e = e.getNextExpression ()) != null)
			{
				if (((Catch) e).catchType.isInstance (ex))
				{
					if (w != null)
					{
						w.dispose ();
					}
					t.apush (ex);
					e.evaluateAsVoid (t);
					return;
				}
			}
			if (w != null)
			{
				throw w;
			}
			else if (ex instanceof RuntimeException)
			{
				throw (RuntimeException) ex;
			}
			else
			{
				throw (Error) ex;
			}
		}
	}

	
	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		Label start = new Label ();
		Label end = new Label ();

		writer.visitLabel (start);

		Expression e = getFirstExpression ();
		e.write (writer, true);
		
		writer.visitLabel (end);
		
		if (start.getOffset () != end.getOffset ())
		{
			Label next = new Label ();
			writer.visitJumpInsn (Opcodes.GOTO, next);
			while ((e = e.getNextExpression ()) != null)
			{
				Label handler = new Label ();
				writer.visitLabel (handler);
				e.write (writer, true);
				writer.visitJumpInsn (Opcodes.GOTO, next);
				writer.visitTryCatchBlock
					(start, end, handler,
					 ((Catch) e).catchType.getBinaryName ().equals ("java.lang.Throwable") ? null
					 : writer.toName (((Catch) e).catchType));
			}
			writer.visitLabel (next);
		}
	}

}
