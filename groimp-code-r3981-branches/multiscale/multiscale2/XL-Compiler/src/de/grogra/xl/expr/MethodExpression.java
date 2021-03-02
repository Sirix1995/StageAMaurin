
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

public abstract class MethodExpression extends VoidExpression implements Routine
{
	private int params, jsize;

	
	@Override
	public boolean discards (int index)
	{
		return true;
	}

	
	@Override
	public void writeFinally (BytecodeWriter writer, int label, ControlTransfer cause)
	{
		cause.writeTransfer (writer, null);
	}


	@Override
	protected final void evaluateVoidImpl (VMXState t)
	{
		t.invoke (this, -1, null);
	}


	public final AbruptCompletion.Return execute (VMXState s)
	{
		linkGraph (true);
		try
		{
			evaluateImpl (s);
		}
		catch (AbruptCompletion.Return e)
		{
			if (e.getTypeId () == VOID)
			{
				e.dispose ();
				return null;
			}
			return e;
		}
		return null;
	}


	protected abstract void evaluateImpl (VMXState t);


	public final void setParameterSize (int params)
	{
		this.params = params;
	}


	public final void setJFrameSize (int jsize)
	{
		this.jsize = jsize;
	}

	
	public boolean hasJavaParameters ()
	{
		return true;
	}

	
	public int getParameterSize ()
	{
		return params;
	}

	
	public int getJavaFrameSize ()
	{
		return jsize;
	}
	
	
	public int getFrameSize ()
	{
		return 0;
	}

}
