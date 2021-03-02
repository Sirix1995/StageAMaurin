
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

import java.util.HashMap;

public class ClassLoaderAdapter implements TypeLoader
{
	private final ClassLoader loader;
	private final HashMap types = new HashMap (32);


	public ClassLoaderAdapter (ClassLoader loader)
	{
		this.loader = loader;
	}


	public Class classForName (String name) throws ClassNotFoundException
	{
		return Class.forName (name, false, loader);
	}


	public synchronized Type typeForName (String name) throws ClassNotFoundException
	{
		Object o = types.get (name);
		if (o != null)
		{
			if (o != this)
			{
				return (Type) o;
			}
			throw new ClassNotFoundException (name);
		}
		try
		{
			Type t = ClassAdapter.wrap (Class.forName (name, false, loader));
			types.put (name, t);
			return t;
		}
		catch (ClassNotFoundException e)
		{
			types.put (name, this);
			throw e;
		}
	}


	public ClassLoader getClassLoader ()
	{
		return loader;
	}
}
