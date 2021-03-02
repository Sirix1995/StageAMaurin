
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

package de.grogra.persistence;

import java.util.Hashtable;
import de.grogra.reflect.*;

public final class PersistenceBindings
{
	private final TypeLoader loader;
	private final SharedObjectProvider.Binding soBinding;
	private final Hashtable types = new Hashtable ();


	public PersistenceBindings (TypeLoader loader,
								SharedObjectProvider.Binding soBinding)
	{
		this.loader = loader;
		this.soBinding = soBinding;
	}
	
	
	public TypeLoader getTypeLoader ()
	{
		return loader;
	}

	
	public SharedObjectProvider.Binding getSOBinding ()
	{
		return soBinding;
	}

	
	public Type typeForName (String name, boolean resolveManageable)
		throws ClassNotFoundException
	{
		Type type = loader.typeForName (name);
		if (!resolveManageable)
		{
			return type;
		}
		ManageableType t = resolveType (type);
		return (t != null) ? t : type;
	}

	
	public ManageableType resolveType (String name)
	{
		ManageableType t = (ManageableType) types.get (name);
		if (t != null)
		{
			return t;
		}
		t = ManageableType.forName (name);
		if (t != null)
		{
			types.put (name, t);
			return t;
		}
		try
		{
			return resolveType (loader.typeForName (name));
		}
		catch (ClassNotFoundException e)
		{
			return null;
		}
	}


	public ManageableType resolveType (Type type)
	{
		String name = type.getBinaryName ();
		ManageableType t = (ManageableType) types.get (name);
		if (t == null)
		{
			t = ManageableType.forType (type);
			if (t != null)
			{
				types.put (name, t);
			}
		}
		return t;
	}


	public ManageableType resolveType (Class type)
	{
		String name = type.getName ();
		ManageableType t = (ManageableType) types.get (name);
		if (t == null)
		{
			t = ManageableType.forClass (type);
			if (t != null)
			{
				types.put (name, t);
			}
		}
		return t;
	}

}
