
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

import de.grogra.reflect.Field;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;

public final class Assert extends VoidExpression
{
	private Field disabled;
	private Expression condition;
	private Expression message;

	
	public Assert (Field disabled)
	{
		this.disabled = disabled;
	}


	@Override
	protected void evaluateVoidImpl (VMXState t)
	{
		if (!condition.evaluateBoolean (t))
		{
			throw (message == null) ? new AssertionError ()
				: new AssertionError (message.evaluateAsObject (t));
		}
	}


	@Override
	public void link (boolean checkTypes)
	{
		condition = getExpression (0, BOOLEAN, checkTypes);
		message = condition.getNextExpression ();
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		Label ok = new Label ();

		writer.visitFieldInsn (Opcodes.GETSTATIC, disabled, null);
		writer.visitJumpInsn (Opcodes.IFNE, ok);
		
		condition.writeConditional (writer, null, ok);

		writer.visitTypeInsn (Opcodes.NEW, "java/lang/AssertionError");
		writer.visitInsn (Opcodes.DUP);
		if (message != null)
		{
			message.write (writer, false);
		}
		writer.visitMethodInsn
			(Opcodes.INVOKESPECIAL, "java/lang/AssertionError", "<init>",
			 (message == null) ? "()V"
			 : '(' + getDescriptorNoBS (message.etype) + ")V");
		writer.visitInsn (Opcodes.ATHROW);

		writer.visitLabel (ok);
	}

}
