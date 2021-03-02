
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

import org.objectweb.asm.Opcodes;

import de.grogra.reflect.ClassAdapter;
import de.grogra.xl.compiler.*;
import de.grogra.xl.vmx.*;

public class GetDescriptor extends EvalExpression
{
	private final Routine routine;
	private final int nesting;


	public GetDescriptor (Routine routine, int nesting)
	{
		super (ClassAdapter.wrap (RoutineDescriptor.class));
		this.routine = routine;
		this.nesting = nesting;
	}


	@Override
	protected Object evaluateObjectImpl (VMXState t)
	{
		return t.createDescriptor (routine, nesting, null);
	}


	@Override
	protected void writeImpl (BytecodeWriter out, boolean discard)
	{
		if (!discard)
		{
			out.visitVMX ();
			if (!(routine instanceof XMethod))
			{
				throw new AssertionError (routine);
			}
			out.visitFieldInsn (Opcodes.GETSTATIC,
								((XMethod) routine).getRoutineField (), null);
			out.visiticonst (nesting);
			out.visitaconst (null);//AUTH
			out.visitMethodInsn (VMX_TYPE, "createDescriptor");
		}
	}

	protected String paramString ()
	{
		return super.paramString () + ",routine=" + routine + ",nesting=" + nesting;
	}
}

