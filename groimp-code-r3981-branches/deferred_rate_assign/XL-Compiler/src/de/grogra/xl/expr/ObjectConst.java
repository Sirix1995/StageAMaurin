
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


import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;
import de.grogra.reflect.*;

public class ObjectConst extends EvalExpression implements Constant
{
	public Object value;


	public ObjectConst ()
	{
		super (Type.OBJECT);
	}



	private boolean nullInBytecode = false;

	public ObjectConst (Object value, Type type)
	{
		this (value, false, type);
	}


	public ObjectConst (Object value, boolean nullInBytecode, Type type)
	{
		this ();
		setType (type);
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
			out.visitaconst (nullInBytecode ? null : value);
		}
	}


}

