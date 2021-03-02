
// NOTE: This file was generated automatically.

// ********************************************
// *               DO NOT EDIT!               *
// ********************************************


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
import de.grogra.reflect.*;

public class BooleanConst extends EvalExpression implements Constant
{
	public boolean value;


	public BooleanConst ()
	{
		super (Type.BOOLEAN);
	}


	public BooleanConst (boolean value)
	{
		this ();
		this.value = value;
	}

	@Override
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		return value;
	}


	@Override
	protected void writeImpl (BytecodeWriter out, boolean discard)
	{
		if (!discard)
		{
			out.visiticonst (value  ? 1 : 0);
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
		if ((trueLabel != null) && value)
		{
			writer.visitJumpInsn (Opcodes.GOTO, trueLabel);
		}
		else if ((falseLabel != null) && !value)
		{
			writer.visitJumpInsn (Opcodes.GOTO, falseLabel);
		}
		return value ? 1 : -1;
	}


}

