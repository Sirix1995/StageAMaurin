
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

package de.grogra.reflect;

import java.lang.reflect.Array;

public final class XArray extends TypeImpl
{
	private Class typeClass;
	private Object array0 = null;
	private final Field length;
	private final Method clone;

	public XArray (Type componentType)
	{
		super (TypeId.OBJECT, '[' + componentType.getDescriptor (),
			   '[' + componentType.getDescriptor (), PUBLIC | FINAL | ARRAY,
			   null, Type.OBJECT, componentType);
		length = new LengthField (this);
		clone = new CloneMethod (this);
	}


	@Override
	public boolean isInstance (Object object)
	{
		return getImplementationClass ().isInstance (object);
	}


	@Override
	public synchronized Class getImplementationClass ()
	{
		if (typeClass == null)
		{
			typeClass = Array.newInstance
				(getComponentType ().getImplementationClass (), 0).getClass ();
		}
		return typeClass;
	}

	
	@Override
	public Type getArrayType ()
	{
		return new XArray (this);
	}


	@Override
	public Object createArray (int length)
	{
		if (length == 0)
		{
			if (array0 == null)
			{
				array0 = Array.newInstance
					(getComponentType ().getImplementationClass (), 0);
			}
			return array0;
		}
		return Array.newInstance (getComponentType ().getImplementationClass (),
								  length);
	}


	@Override
	public int getDeclaredFieldCount ()
	{
		return 1;
	}


	@Override
	public Field getDeclaredField (int index)
	{
		return length;
	}


	@Override
	public int getDeclaredMethodCount ()
	{
		return 1;
	}


	@Override
	public Method getDeclaredMethod (int index)
	{
		return clone;
	}

}
