
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

import de.grogra.reflect.Type;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.VMXState;

public final class ConditionalAnd extends BinaryExpression
{

	public ConditionalAnd ()
	{
		super (Type.BOOLEAN);
	}


	@Override
	public int getSupportedTypes ()
	{
		return BOOLEAN_MASK;
	}


	@Override
	public boolean allowsIteration (int index)
	{
		return index == 0;
	}


	@Override
	protected boolean evaluateBooleanImpl (VMXState vm)
	{
		return expr1.evaluateBoolean (vm) && expr2.evaluateBoolean (vm);
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		writeConditional (writer, discard);
	}

	
	@Override
	public boolean isConditional ()
	{
		return true;
	}


	@Override
	public int writeConditional (BytecodeWriter writer, Label falseLabel, Label trueLabel)
	{
		boolean f = falseLabel == null;
		if (f)
		{
			falseLabel = new Label ();
		}
		int c1 = expr1.writeConditional (writer, falseLabel, null);
		if (c1 == -1)
		{
			if (f)
			{
				writer.visitLabel (falseLabel);
			}
			return -1;
		}
		int c2 = expr2.writeConditional (writer, f ? null : falseLabel, trueLabel);
		if (f)
		{
			writer.visitLabel (falseLabel);
		}
		return (c2 == -1) ? -1 : ((c2 == 1) && (c1 == 1)) ? 1 : 0;
	}

}
