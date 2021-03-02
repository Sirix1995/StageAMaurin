
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

package de.grogra.pf.registry;

import java.net.*;
import java.util.HashMap;
import de.grogra.reflect.*;

public abstract class PluginClassLoader extends URLClassLoader
	implements TypeLoader
{
	final PluginDescriptor descriptor;
	private final HashMap classes = new HashMap (32);


	PluginClassLoader (URL[] urls, ClassLoader parent,
					   PluginDescriptor descriptor)
	{
		super (urls, parent);
		this.descriptor = descriptor;
	}

	
	public ClassLoader getClassLoader ()
	{
		return this;
	}
	
	
	public Type typeForName (String name) throws ClassNotFoundException
	{
		return ClassAdapter.wrap (Class.forName (name, false, this));
	}

	
	public Class classForName (String name) throws ClassNotFoundException
	{
		return Class.forName (name, false, this);
	}


	@Override
	protected final Class loadClass (String name, boolean resolve)
		throws ClassNotFoundException
	{
		Class c = loadClassNull (name, resolve);
		if (c == null)
		{
			throw new ClassNotFoundException (name);
		}
		return c;
	}


	@Override
	protected final Class findClass (String name) throws ClassNotFoundException
	{
		Class c = findClassNull (name);
		if (c != null)
		{
			return c;
		}
		throw new ClassNotFoundException (name);
	}


	final Class superFindClass (String name) throws ClassNotFoundException
	{
		return super.findClass (name);
	}


	final Class superFindClassNull (String name)
	{
		try
		{
			return super.findClass (name);
		}
		catch (ClassNotFoundException e)
		{
			return null;
		}
	}


	synchronized Class loadClassNull (String name, boolean resolve)
	{
		Object o = classes.get (name);
		if (o == this)
		{
			return null;
		}
		Class c = (Class) o;
		if (c == null)
		{
			c = findLoadedClass (name);
			if (c == null)
			{
				ClassLoader p = getParent ();
				if (p != null)
				{
					if (p instanceof PluginClassLoader)
					{
						c = ((PluginClassLoader) p).loadClassNull (name, false);
					}
					else
					{
						try
						{
							c = p.loadClass (name);
						}
						catch (ClassNotFoundException e)
						{
						}
					}
				}
				if (c == null)
				{
					c = findLoadedClass (name);
					if (c == null)
					{
						c = findClassNull (name);
					}
				}
			}
		}
		if (c != null)
		{
			classes.put (name, c);
			if (resolve)
			{
				resolveClass (c);
			}
		}
		else
		{
			classes.put (name, this);
		}	
		return c;
	}


	abstract Class findClassNull (String name);


	abstract Class loadClassParentsSelf (String name);


	@Override
	public URL getResource (String name)
	{
		URL url = findResource (name);
		if (url != null)
		{
			return url;
		}
		ClassLoader p = getParent ();
		return (p != null) ? p.getResource (name) : null;
	}

	
	public URL getPluginResource (String name)
	{
		return null;
	}


	public PluginDescriptor getPluginDescriptor ()
	{
		return descriptor;
	}

}
