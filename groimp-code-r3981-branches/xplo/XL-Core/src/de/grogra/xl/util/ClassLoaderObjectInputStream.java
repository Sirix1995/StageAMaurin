
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

package de.grogra.xl.util;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import de.grogra.reflect.TypeLoader;

public class ClassLoaderObjectInputStream extends ObjectInputStream
{
	private final ClassLoader cloader;
	private final TypeLoader tloader;


	public ClassLoaderObjectInputStream (InputStream in, ClassLoader cloader)
		throws IOException
	{
		super (in);
		this.cloader = cloader;
		this.tloader = null;
	}


	public ClassLoaderObjectInputStream (InputStream in, TypeLoader tloader)
		throws IOException
	{
		super (in);
		this.cloader = tloader.getClassLoader ();
		this.tloader = tloader;
	}


	@Override
	protected Class resolveClass (ObjectStreamClass desc)
		throws IOException, ClassNotFoundException
    {
		try
		{
			return super.resolveClass (desc);
		}
		catch (ClassNotFoundException e)
		{
			return resolveClass (desc.getName ());
		}
    }

	
	protected Class resolveClass (String name) throws ClassNotFoundException
	{
		int ac;
		for (ac = 0; name.charAt (ac) == '['; ac++)
			;
		Class cls = null;
		if (ac > 0)
		{
			switch (name.charAt (ac))
			{
				case 'Z':
					cls = boolean.class;
					break;
				case 'B':
					cls = byte.class;
					break;
				case 'S':
					cls = short.class;
					break;
				case 'C':
					cls = char.class;
					break;
				case 'I':
					cls = int.class;
					break;
				case 'J':
					cls = long.class;
					break;
				case 'F':
					cls = float.class;
					break;
				case 'D':
					cls = double.class;
					break;
				case 'L':
					name = name.substring (ac + 1, name.length () - 1).replace ('/', '.');
					break;
				default:
					throw new ClassNotFoundException (name);
			}
		}
		if (cls == null)
		{
			cls = (tloader != null) ? tloader.classForName (name)
				: Class.forName (name, false, cloader);
		}
		while (--ac >= 0)
		{
			cls = Array.newInstance (cls, 0).getClass ();
		}
		return cls;
	}

	
	@Override
	protected Class resolveProxyClass (String[] interfaces)
		throws IOException, ClassNotFoundException
    {
		ClassLoader nonPublicLoader = null;
		boolean hasNonPublicInterface = false;

		Class<?>[] classObjs = new Class[interfaces.length];
		for (int i = 0; i < interfaces.length; i++)
		{
			Class cl = resolveClass (interfaces[i]);
			if ((cl.getModifiers() & Modifier.PUBLIC) == 0)
			{
				if (hasNonPublicInterface)
				{
					if (nonPublicLoader != cl.getClassLoader ())
					{
						throw new IllegalAccessError
							("conflicting non-public interface class loaders");
					}
				}
				else
				{
					nonPublicLoader = cl.getClassLoader ();
					hasNonPublicInterface = true;
				}
			}
			classObjs[i] = cl;
		}
		try
		{
			return Proxy.getProxyClass
				(hasNonPublicInterface ? nonPublicLoader : cloader, classObjs);
		}
		catch (IllegalArgumentException e)
		{
			throw new ClassNotFoundException (null, e);
		}
    }

}
