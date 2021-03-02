
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

import de.grogra.xl.util.XHashMap;

public final class Lookup
{
	private final Type type;
	
	private final XHashMap<String,Field> fields;
	private final XHashMap<String,Type> types;
	private final XHashMap<String,Method> methods;


	public Lookup (Type type)
	{
		this.type = type;
		fields = new XHashMap<String,Field> (type.getDeclaredFieldCount ());
		types = new XHashMap<String,Type> (type.getDeclaredTypeCount ());
		methods = new XHashMap<String,Method> (type.getDeclaredMethodCount ());
		update ();
	}

	
	public void update ()
	{
		int i, n;

		Field f;
		fields.clear ();
		n = type.getDeclaredFieldCount ();
		for (i = 0; i < n; i++)
		{
			f = type.getDeclaredField (i);
			fields.put (f.getSimpleName (), f);
		}

		Type t;
		types.clear ();
		n = type.getDeclaredTypeCount ();
		for (i = 0; i < n; i++)
		{
			t = type.getDeclaredType (i);
			types.put (t.getSimpleName (), t);
		}

		Method m;
		methods.clear ();
		n = type.getDeclaredMethodCount ();
		for (i = 0; i < n; i++)
		{
			m = type.getDeclaredMethod (i);
			methods.add (m.getSimpleName (), m);
		}
	}
	
	
	public Type getType ()
	{
		return type;
	}
	
	
	public Field getField (String name)
	{
		return fields.get (name);
	}
	

	public Type getType (String name)
	{
		return types.get (name);
	}


	public XHashMap.Entry<String,Method> getMethods (String name)
	{
		return methods.getEntry (name);
	}

}
