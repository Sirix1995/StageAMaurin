
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

import java.util.HashMap;

public class LookupForClass implements de.grogra.util.Map
{
	private HashMap table = new HashMap ();

	private final Item base;

	
	public LookupForClass (Item base)
	{
		base.getClass ();
		this.base = base;
	}

	
	public Object get (Object key, Object defaultValue)
	{
		key = lookup ((key instanceof Class) ? (Class) key : key.getClass ());
		return (key != null) ? key : defaultValue;
	}

	
	public synchronized Object lookup (Class cls)
	{
		Object o = table.get (cls);
		if (o == null)
		{
			Item f = base.getItem (cls.getName ());
			if (f instanceof ObjectItem)
			{
				o = ((ObjectItem) f).getObject ();
			}
			else
			{
				Class s = cls.getSuperclass ();
				if (s != null)
				{
					o = lookup (s);
				}
				if (o == null)
				{
					Class[] ifaces = cls.getInterfaces ();
					for (int i = 0; (i < ifaces.length) && (o == null); i++)
					{
						o = lookup (ifaces[i]);
					}
				}
			}
			table.put (cls, (o == null) ? this : o);
		}
		return (o == this) ? null : o;
	}
}
