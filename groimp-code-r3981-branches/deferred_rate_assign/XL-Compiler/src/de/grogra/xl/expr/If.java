
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

public final class If extends VoidExpression
{
	private Expression condition, thenExpr, elseExpr;


	@Override
	protected void evaluateVoidImpl (VMXState vm)
	{
		if (condition.evaluateBoolean (vm))
		{
			thenExpr.evaluateAsVoid (vm);
		}
		else if (elseExpr != null)
		{
			elseExpr.evaluateAsVoid (vm);
		}
	}


	@Override
	public void link (boolean checkTypes)
	{
		if (getExpressionCount () != 2)
		{
			checkExpressionCount (3);
		}
		condition = getExpression (0, BOOLEAN, checkTypes);
		thenExpr = condition.getNextExpression ();
		elseExpr = thenExpr.getNextExpression ();
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		assert discard;
		Label falseLabel = new Label ();
		Label exitLabel = null;
		condition.writeConditional (writer, falseLabel, null);
		thenExpr.write (writer, true);
		if (elseExpr != null)
		{
			exitLabel = new Label ();
			writer.visitJumpInsn (Opcodes.GOTO, exitLabel);
		}
		writer.visitLabel (falseLabel);
		if (elseExpr != null)
		{
			elseExpr.write (writer, true);
			writer.visitLabel (exitLabel);
		}
	}

}
