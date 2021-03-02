
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

package de.grogra.graph;

import de.grogra.xl.util.ObjectList;

public class AccessorMap extends AttributeSet
{
	AttributeAccessor[] accessors, accessorsById;


	public AccessorMap ()
	{
		this (true);
	}


	public AccessorMap (boolean useIdTable)
	{
		super ();
		accessors = new AttributeAccessor[0];
		accessorsById = useIdTable ? new AttributeAccessor[0] : null;
	}


	public AccessorMap (AccessorMap superMap)
	{
		super (superMap);
		accessors = superMap.accessors.clone ();
		accessorsById = (superMap.accessorsById != null)
			? superMap.accessorsById.clone () : null;
	}


	public void add (AttributeAccessor accessor)
	{
		int id = accessor.getAttribute ().id;
		if (accessorsById == null)
		{
			int i = findIndex (id);
			if (i >= 0)
			{
				accessors[i] = accessor;
				return;
			}
		}
		else if ((id < accessorsById.length) && (accessorsById[id] != null))
		{
			for (int i = accessors.length - 1; i >= 0; i--)
			{
				if (accessors[i] == accessorsById[id])
				{
					accessors[i] = accessor;
					accessorsById[id] = accessor;
					return;
				}
			}
			throw new AssertionError ();
		}
		int len = accessors.length;
		System.arraycopy (accessors, 0,
						  accessors = new AttributeAccessor[len + 1], 0,
						  len);
		accessors[len] = accessor;
		if (accessorsById != null)
		{
			if (id >= accessorsById.length)
			{
				AttributeAccessor[] a = new AttributeAccessor[id + 1];
				System.arraycopy (accessorsById, 0, a, 0,
								  accessorsById.length);
				accessorsById = a;
			}
			accessorsById[id] = accessor;
		}
		super.add (accessor.getAttribute ());
	}


	public final int size ()
	{
		return accessors.length;
	}


	private int findIndex (int id)
	{
		for (int i = accessors.length - 1; i >= 0; i--)
		{
			if (accessors[i].getAttribute ().id == id)
			{
				return i;
			}
		}
		return -1;
	}


	public final AttributeAccessor getAccessor (int index)
	{
		return accessors[index];
	}


	public final AttributeAccessor getAccessorById (int id)
	{
		if (accessorsById != null)
		{
			return (id < accessorsById.length) ? accessorsById[id] : null;
		}
		id = findIndex (id);
		return (id >= 0) ? accessors[id] : null;
	}


	public AttributeAccessor getAccessor (Attribute attribute)
	{
		int id = attribute.id;
		return (accessorsById == null) ? getAccessorById (id)
			: (id < accessorsById.length) ? accessorsById[id] : null;
	}


	public final Attribute[] getAttributes (Attribute[] additional)
	{
		if (additional != null)
		{
			ObjectList atts = new ObjectList
				(accessors.length + ((additional != null) ? additional.length : 0));
			for (int i = additional.length - 1; i >= 0; i--)
			{
				if (findIndex (additional[i].id) < 0)
				{
					atts.add (additional[i]);
				}
			}
			for (int i = accessors.length - 1; i >= 0; i--)
			{
				atts.add (accessors[i].getAttribute ());
			}
			return (Attribute[]) atts.toArray (new Attribute[atts.size ()]);
		}
		int i = accessors.length;
		Attribute[] a = new Attribute[i];
		while (--i >= 0)
		{
			a[i] = accessors[i].getAttribute ();
		}
		return a;
	}


	public final AttributeAccessor find (String name)
	{
		return Attribute.find (accessors, name);
	}

}
