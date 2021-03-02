
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

import de.grogra.reflect.*;
import de.grogra.xl.vmx.*;

public final class InvokeVirtual extends Invoke
{
	private final boolean iface;


	public InvokeVirtual (Method method)
	{
		super (method);
		iface = (method.getDeclaringType ().getModifiers () & Member.INTERFACE) != 0;
	}


	@Override
	protected Method getMethod (VMXState vm)
	{
		Object target = vm.apeek (size);
		if (target == null)
		{
			throw new NullPointerException ();
		}
		if (!targetType.isInstance (target))
		{
			throw new AssertionError ((Object) (descriptor + " " + target));
		}
		Type t = Reflection.getType (target);
		while (t != null)
		{
			if (t instanceof ClassAdapter)
			{
				return Reflection.getDeclaredMethod (targetType, descriptor);
			}
			else
			{
				de.grogra.reflect.Method m = Reflection.getDeclaredMethod (t, descriptor);
				if (m != null)
				{
					if ((m.getModifiers () & Member.ABSTRACT) != 0)
					{
						throw new AbstractMethodError ("Abstract Method " + m);
					}
					return m;
				}
			}
			t = t.getSupertype ();
		}
		throw new NoSuchMethodError ("Virtual method " + descriptor
									 + " not found.");
	}


	@Override
	protected int getOpcode ()
	{
		return iface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL;
	}

}
