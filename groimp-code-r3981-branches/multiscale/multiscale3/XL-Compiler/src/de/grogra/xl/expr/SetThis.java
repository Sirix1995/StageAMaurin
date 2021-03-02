
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

import de.grogra.reflect.XClass;
import de.grogra.reflect.XObject;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.VMXState;

public final class SetThis extends VoidExpression
{
	private InvokeSpecial expr;


	@Override
	protected void evaluateVoidImpl (VMXState t)
	{	
		XObject o = (XObject) expr.evaluateObject (t);
		XClass cls = (XClass) t.agetj (0, null);
		if (o.getXClass () == null)
		{
			o.initXClass (cls);
		}
		else if (o.getXClass () != cls)
		{
			throw new IllegalStateException (o.getXClass () + " " + cls);
		}
		t.asetj (0, o, null);
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (1);
		expr = (InvokeSpecial) getExpression (0, InvokeSpecial.class);
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discards)
	{
		expr.write (writer, false);
	}

}
