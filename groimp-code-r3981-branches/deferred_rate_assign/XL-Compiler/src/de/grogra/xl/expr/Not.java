
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

import de.grogra.reflect.*;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;

public class Not extends UnaryExpression
{

	public Not ()
	{
		super (Type.BOOLEAN);
	}


	@Override
	public int getSupportedTypes ()
	{
		return BOOLEAN_MASK;
	}


	@Override
	protected boolean evaluateBooleanImpl (VMXState vm)
	{
		return !expr.evaluateBoolean (vm);
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		if (discard)
		{
			expr.write (writer, true);
		}
		else if (expr.isConditional ())
		{
			writeConditional (writer, false);
		}
		else
		{
			expr.write (writer, false);
			writer.visiticonst (1);
			writer.visitInsn (Opcodes.IXOR);
		}
	}

	
	@Override
	public boolean isConditional ()
	{
		return true;
	}


	@Override
	public int writeConditional (BytecodeWriter writer, Label falseLabel, Label trueLabel)
	{
		return -expr.writeConditional (writer, trueLabel, falseLabel);
	}

}
