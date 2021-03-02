
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

import java.util.ArrayList;

public class FieldChainImpl implements FieldChain
{
	private final ArrayList fields;


	public FieldChainImpl (Field field)
	{
		fields = new ArrayList (1);
		fields.add (field);
	}


	public FieldChainImpl (FieldChain list, Field field)
	{
		if (list == null)
		{
			fields = new ArrayList (1);
		}
		else
		{
			fields = new ArrayList (list.length () + 1);
			for (int i = 0; i < list.length (); i++)
			{
				fields.add (list.getField (i));
			}
		}
		fields.add (field);
	}

	
	public int length ()
	{
		return fields.size ();
	}


	public Field getField (int index)
	{
		return (Field) fields.get (index);
	}


	public boolean overlaps (int[] indices, FieldChain list, int[] listIndices)
	{
		int i = Math.min (fields.size (), list.length ());
		int ai = -1;
		Type arrayComponent = null;
		while (--i >= 0)
		{
			if (arrayComponent != null)
			{
				if (indices[++ai] != listIndices[ai])
				{
					return false;
				}
				arrayComponent = arrayComponent.getComponentType ();
			}
			else
			{
				Field f = list.getField (i);
				if ((fields.get (i) != f)
					&& !Reflection.membersEqual ((Field) fields.get (i), f, false))
				{
					return false;
				}
				arrayComponent = f.getType ().getComponentType ();
			}
		}
		return true;
	}
	
	
	@Override
	public String toString ()
	{
		return fields.toString ();
	}

}
