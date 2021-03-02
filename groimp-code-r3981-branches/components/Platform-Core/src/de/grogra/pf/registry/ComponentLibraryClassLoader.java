
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

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import de.grogra.vfs.FileSystem;

class ComponentLibraryClassLoader extends ComponentClassLoader
{
	private final String[] prefixes;
	private Object dir;
	
	
	private static URL[] toURL (Object[] file, ComponentDescriptor pd)
	{
		URL[] u = new URL[file.length];
		for (int i = 0; i < file.length; i++)
		{
			u[i] = pd.getFileSystem ().toURL (file[i]);
		}
		return u;
	}


	ComponentLibraryClassLoader (Object[] files, ComponentClassLoader parent, String[] prefixes)
	{
		this (toURL (files, parent.getComponentDescriptor ()), parent, prefixes);
		FileSystem fs = parent.getComponentDescriptor ().getFileSystem ();
		if (files.length > 0)
		{
			dir = fs.isLeaf (files[0]) ? fs.getParent (files[0]) : files[0];
			if (fs.isLeaf (dir))
			{
				dir = null;
			}
		}
	}


	ComponentLibraryClassLoader (URL[] urls, ComponentClassLoader parent, String[] prefixes)
	{
		this (urls, parent, parent.descriptor, prefixes);
	}


	ComponentLibraryClassLoader (Object[] files, Object libDir, ComponentDescriptor descriptor)
	{
		this (toURL (files, descriptor), descriptor.getClass ().getClassLoader (), descriptor, new String[] {""});
		dir = libDir;
	}


	private ComponentLibraryClassLoader
		(URL[] urls, ClassLoader parent,
		 ComponentDescriptor descriptor, String[] prefixes)
	{
		super (urls, parent, descriptor);
		this.prefixes = prefixes;
	}


	@Override
	protected String findLibrary (String libname)
	{
		if (dir instanceof File)
		{
			File f = new File ((File) dir, System.mapLibraryName (libname));
			if (f.exists ())
			{
				return f.getAbsolutePath ();
			}
		}
		return null;
	}


	protected boolean shouldLookForClass (String name)
	{
		for (int i = prefixes.length - 1; i >= 0; i--)
		{
			if (name.startsWith (prefixes[i]))
			{
				return findResource (name.replace ('.', '/') + ".class")
					!= null;
			}
		}
		return false;
	}


	@Override
	protected Class findClassNull (String name)
	{
		Class c = findLoadedClass (name);
		return (c != null) ? c : superFindClassNull (name);
	}


	@Override
	Class loadClassParentsSelf (String name)
	{
		ClassLoader loader = getParent ();
		if (!(loader instanceof ComponentClassLoader))
		{
			return null;
		}
		Class c = ((ComponentClassLoader) loader).loadClassParentsSelf (name);
		return (c != null) ? c : loadClassNull (name, false);
	}

	@Override
	public URL getComponentResource (String name)
	{
		URL u = findResource (name);
		if (u != null)
		{
			return u;
		}
		if (getParent () instanceof ComponentLibraryClassLoader)
		{
			return ((ComponentLibraryClassLoader) getParent ()).getComponentResource (name);
		}
		return null;
	}

	@Override
	public String toString ()
	{
		return "Lib@" + Integer.toHexString (hashCode ())
			+ Arrays.toString (getURLs ())
			+ Arrays.toString (prefixes);
	}

}
