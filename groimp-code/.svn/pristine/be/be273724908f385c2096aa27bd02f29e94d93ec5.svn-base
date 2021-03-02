
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

import de.grogra.reflect.*;

public abstract class AccessorBase<T> implements AttributeAccessor, ObjectAttributeAccessor<T>
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
	, ${pp.Type}AttributeAccessor
#end
!!*/
//!! #* Start of generated code
// generated
	, BooleanAttributeAccessor
// generated
	, ByteAttributeAccessor
// generated
	, ShortAttributeAccessor
// generated
	, CharAttributeAccessor
// generated
	, IntAttributeAccessor
// generated
	, LongAttributeAccessor
// generated
	, FloatAttributeAccessor
// generated
	, DoubleAttributeAccessor
//!! *# End of generated code
{
	protected final Attribute attribute;


	public AccessorBase (Attribute attribute)
	{
		this.attribute = attribute;
	}


	public Type getType ()
	{
		return attribute.getType ();
	}


	public Attribute getAttribute ()
	{
		return attribute;
	}


	public Field getField ()
	{
		return null;
	}


	protected T clone (T orig)
	{
		return orig; 
	}


	public Object setSubfield (Object object, FieldChain field,
							   int[] indices, Object value, GraphState gs)
	{
		T v = clone (getObject (object, gs));
		Object o = v;
		int i;
		try
		{
			for (i = 0; i < field.length () - 1; i++)
			{
				o = field.getField (i).getObject (o);
			}
			Reflection.set (o, field.getField (i), value);
		}
		catch (IllegalAccessException e)
		{
			throw new de.grogra.util.WrapException (e);
		}
		setObject (object, v, gs);
		return value;
	}
}
