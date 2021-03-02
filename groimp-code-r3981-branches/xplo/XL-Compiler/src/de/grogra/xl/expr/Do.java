
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

import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;

public final class Do extends BreakTarget
{
	private Expression condition, statement;


	@Override
	protected void evaluate (VMXState t)
	{
		do
		{
			statement.evaluateAsVoid (t);
		} while (condition.evaluateBoolean (t));
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (2);
		statement = getExpression (0);
		condition = getExpression (1, BOOLEAN, checkTypes);
	}


	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		Label loop = new Label ();

		writer.visitLabel (loop);
		statement.write (writer, true);

		condition.writeConditional (writer, null, loop);
	}

}
