
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

package de.grogra.util;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class DelegatingClassLoader extends ClassLoader
{
	private final ClassLoader[] parents;


	public DelegatingClassLoader (ClassLoader[] parents)
	{
		super (parents[0]);
		this.parents = parents;
	}


	public int getParentCount ()
	{
		return parents.length;
	}


	public ClassLoader getParent (int index)
	{
		return parents[index];
	}


	@Override
	protected Class findClass (String name) throws ClassNotFoundException
	{
		for (int i = parents.length - 1; i >= 1; i--)
		{
			try
			{
				return Class.forName (name, false, parents[i]);
			}
			catch (ClassNotFoundException e)
			{
			}
		}
		return super.findClass (name);
	}


	@Override
	protected URL findResource (String name)
	{
		for (int i = parents.length - 1; i >= 1; i--)
		{
			URL u;
			if ((u = parents[i].getResource (name)) != null)
			{
				return u;
			}
		}
		return super.findResource (name);
	}


	@Override
	protected Enumeration<URL> findResources (String name) throws IOException
	{
		CompoundEnumeration<URL> ce = new CompoundEnumeration<URL> (true);
		for (int i = parents.length - 1; i >= 0; i--)
		{
			ce.add (parents[i].getResources (name));
		}
		return ce;
	}

}
