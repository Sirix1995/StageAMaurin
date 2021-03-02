
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

public class AttributeSet
{
	private int[] existingIds;
	private boolean readOnly = false;


	public AttributeSet ()
	{
		existingIds = new int[0];
	}


	public AttributeSet (AttributeSet superSet)
	{
		existingIds = superSet.existingIds.clone ();
	}


	public AttributeSet setReadOnly ()
	{
		readOnly = true;
		return this;
	}


	public final AttributeSet add (Attribute attribute)
	{
		if (readOnly)
		{
			throw new IllegalStateException
				("Attempt to add an attribute to the read-only-set " + this);
		}
		int id = attribute.id;
		if (id < 0)
		{
			throw new IllegalArgumentException
				("Can't add derived attribute " + attribute);
		}
		if ((id >> 5) >= existingIds.length)
		{
			int[] a = new int[(id >> 5) + 1];
			System.arraycopy (existingIds, 0, a, 0, existingIds.length);
			existingIds = a;
		}
		existingIds[id >> 5] |= 1 << id;
		return this;
	}


	public final boolean isSubsetOf (AttributeSet set)
	{
		int[] t, s;
		if ((s = set.existingIds).length < (t = existingIds).length)
		{
			return false;
		}
		for (int i = t.length - 1; i >= 0; i--)
		{
			if ((t[i] & ~s[i]) != 0)
			{
				return false;
			}
		}
		return true;
	}

}
