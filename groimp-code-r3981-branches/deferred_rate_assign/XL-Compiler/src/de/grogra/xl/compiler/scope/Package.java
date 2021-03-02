
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

import de.grogra.reflect.*;

import java.util.HashMap;

public class Package extends Scope implements Member
{
	private final HashMap types = new HashMap (32);
	private final HashMap declaredTypes = new HashMap (32);
	private final String name, descriptor;


	public Package (ClassPath cpath, String name)
	{
		super (cpath);
		this.name = name;
		descriptor = 'P' + name + ';';
	}

	@Override
	public Member getDeclaredEntity ()
	{
		return this;
	}

	@Override
	public final Package getPackage ()
	{
		return this;
	}


	@Override
	public void findMembers (String name, int flags, Members list)
	{
		if ((flags & Members.EXCLUDE_TYPES_IN_PACKAGES) == 0)
		{
			findMembers (name, flags, list, this);
		}
		super.findMembers (name, flags, list);
	}

	
	void findMembers (String name, int flags, Members list, Scope scope)
	{
		Member m;
		if (!this.name.equals (list.getPackage ()))
		{
			flags |= Members.DIFFERENT_PACKAGE;
		}
		if ((flags & (Members.TYPE | Members.PREDICATE)) != 0)
		{
			if ((m = findType (name)) != null)
			{
				TypeScope.addTypeOrPatterns ((Type) m, list, scope, flags);
				return;
			}
		}
		if ((flags & Members.SUB_PACKAGE) != 0)
		{
			list.add (((ClassPath) getEnclosingScope ())
					  .getPackage (getCanonicalName (name), false), this, flags);
		}
	}


	public void declareType (Type type)
	{
		types.put (type.getSimpleName (), type);
		declaredTypes.put (type.getSimpleName (), type);
		ClassPath.get (this).declareType (type);
	}
	
	
	public Type findDeclaredType (String name)
	{
		return (Type) declaredTypes.get (name);
	}

	
	public Type findType (String name)
	{
		Type t = (Type) types.get (name);
		if ((t == null) && ((t = loadType (name)) != null))
		{
			types.put (t.getSimpleName (), t);
		}
		return t;
	}


	protected Type loadType (String name)
	{
		return ((ClassPath) getEnclosingScope ())
			.typeForNameOrNull (getCanonicalName (name));
	}


	public final String getCanonicalName (String member)
	{
		return (name.length () == 0) ? member : name + '.' + member;
	}


	public final boolean contains (Member m)
	{
		return (m instanceof Package) ? equals (m)
			: name.equals (((m instanceof Type) ? (Type) m
							: m.getDeclaringType ()).getPackage ());
	}


	public final Type getDeclaringType ()
	{
		return null;
	}


	public final int getModifiers ()
	{
		return PUBLIC;
	}


	public final String getName ()
	{
		return name;
	}


	public final String getSimpleName ()
	{
		return name;
	}


	public final String getDescriptor ()
	{
		return descriptor;
	}


	@Override
	public final String toString ()
	{
		return "package '" + name + '\'';
	}


	@Override
	public final boolean equals (Object o)
	{
		return (o instanceof Package)
			&& descriptor.equals (((Package) o).descriptor);
	}


	public int getDeclaredAnnotationCount ()
	{
		return 0;
	}


	public Annotation getDeclaredAnnotation (int index)
	{
		throw new IndexOutOfBoundsException (String.valueOf (index));
	}

}
