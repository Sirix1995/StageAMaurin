
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

package de.grogra.xl.compiler.scope;

import java.util.*;
import de.grogra.reflect.*;
import de.grogra.xl.compiler.*;

public final class ClassPath extends Scope implements TypeLoader
{
	private final ASMTypeLoader loader;
	private final HashMap packages = new HashMap ();
	private final HashMap declaredTypes = new HashMap ();


	public ClassPath (ASMTypeLoader loaderForCompiledClasses)
	{
		super (null);
		this.loader = loaderForCompiledClasses;
	}


	public static ClassPath get (Scope scope)
	{
		while (!(scope instanceof ClassPath))
		{
			if (scope == null)
			{
				return null;
			}
			scope = scope.getEnclosingScope ();
		}
		return (ClassPath) scope;
	}


	public Package getPackage (String name, boolean force)
	{
		Package p = (Package) packages.get (name);
		if ((p == null) && (force || loader.hasPackage (name)))
		{
			p = new Package (this, name);
			packages.put (name, p);
			if (force)
			{
				int pos = name.lastIndexOf ('.');
				if (pos > 0)
				{
					getPackage (name.substring (0, pos), force);
				}
				
			}
		}
		return p;
	}


	void declareType (Type type)
	{
		declaredTypes.put (type.getBinaryName (), type);
	}


	public Type typeForNameOrNull (String name)
	{
		try
		{
			return loader.typeForNameOrNull (name);
		}
		catch (ClassNotFoundException e)
		{
			return null;
		}
	}


	public Type typeForClass (Class cls) throws ClassNotFoundException
	{
		int ac = 0;
		while (cls.isArray ())
		{
			ac++;
			cls = cls.getComponentType ();
		}
		Type t = cls.isPrimitive () ? ClassAdapter.wrap (cls) : loader.typeForName (cls.getName ());
		while (--ac >= 0)
		{
			t = t.getArrayType ();
		}
		return t;
	}


	public Type typeForType (Type cls) throws ClassNotFoundException
	{
		int ac = 0;
		while (Reflection.isArray (cls))
		{
			ac++;
			cls = cls.getComponentType ();
		}
		if (!Reflection.isPrimitive (cls))
		{
			cls = loader.typeForName (cls.getName ());
		}
		while (--ac >= 0)
		{
			cls = cls.getArrayType ();
		}
		return cls;
	}

	
	public Type typeForName (String name) throws ClassNotFoundException
	{
		Type t = (Type) declaredTypes.get (name);
		return (t != null) ? t : loader.typeForName (name);
	}

	
	public Class classForName (String name) throws ClassNotFoundException
	{
		return loader.classForName (name);
	}

	
	public ClassLoader getClassLoader ()
	{
		return loader.getClassLoader ();
	}


	@Override
	public void findMembers (String name, int flags, Members list)
	{
		if ((flags & Members.TOP_LEVEL_PACKAGE) != 0)
		{
			list.add (getPackage (name, false), this, flags);
		}
	}


	public Scope createImports (String[] packageImports, Class[] memberTypeImports,
								Class[] singleTypeImports, Class[] staticImports)
		throws ClassNotFoundException
	{
		Scope s = null;

		for (int i = staticImports.length - 1; i >= 0; i--)
		{
			s = new StaticImportOnDemand (s, typeForClass (staticImports[i]));
		}

		for (int i = singleTypeImports.length - 1; i >= 0; i--)
		{
			s = new SingleTypeImport (s, typeForClass (singleTypeImports[i]));
		}

		for (int i = memberTypeImports.length - 1; i >= 0; i--)
		{
			s = new TypeImportOnDemand (s, typeForClass (memberTypeImports[i]));
		}

		for (int i = packageImports.length - 1; i >= 0; i--)
		{
			s = new PackageImportOnDemand (s, getPackage (packageImports[i], false));
		}
		
		return s;
	}

}
