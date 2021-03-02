
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

public final class Block extends BreakTarget
{
	private final boolean sequential;

	
	public Block ()
	{
		this (false);
	}
	
	
	public static Block createSequentialBlock ()
	{
		return new Block (true);
	}


	private Block (boolean iterated)
	{
		this.sequential = iterated;
	}


	@Override
	public boolean isRequired (int index)
	{
		return false;
	}


	public boolean isSequentialBlock ()
	{
		return sequential;
	}

	
	public boolean isEmpty ()
	{
		for (Expression e = getFirstExpression (); e != null;
			 e = e.getNextExpression ())
		{
			if (!((e instanceof Block) && ((Block) e).isEmpty ()))
			{
				return false;
			}
		}
		return true;
	}


	@Override
	protected void evaluate (VMXState t)
	{
		for (Expression e = getFirstExpression (); e != null;
			 e = e.getNextExpression ())
		{
			e.evaluateAsVoid (t);
		}
	}
	
	
	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		writeChildren (writer);
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ",iterated=" + sequential;
	}

}
