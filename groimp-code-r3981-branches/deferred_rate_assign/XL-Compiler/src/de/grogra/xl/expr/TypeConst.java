
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

import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;
import de.grogra.reflect.*;

public class TypeConst extends EvalExpression implements Constant
{
	public Type value;
	
	private final boolean nullInBytecode;


	public TypeConst (Type value)
	{
		this (value, false);
	}


	public TypeConst (Type value, boolean nullInBytecode)
	{
		super (Type.TYPE);
		this.value = value;
		this.nullInBytecode = nullInBytecode;
	}

	
	@Override
	protected Object evaluateObjectImpl (VMXState t)
	{
		return value;
	}


	@Override
	protected void writeImpl (BytecodeWriter out, boolean discard)
	{
		if (!discard)
		{
			if (nullInBytecode)
			{
				out.visitaconst (null);
			}
			else
			{
				out.visitaconst (value);
				out.visitClass2Type ();
			}
		}
	}

}

