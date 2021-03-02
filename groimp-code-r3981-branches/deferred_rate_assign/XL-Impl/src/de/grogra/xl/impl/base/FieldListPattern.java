
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

package de.grogra.xl.impl.base;

import java.util.Arrays;

import de.grogra.reflect.Field;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.xl.query.AttributeListPattern;

public class FieldListPattern extends AttributeListPattern
{
	private final Field[] fields;


	public FieldListPattern (Type cls, Type nodeType, Field[] fields)
	{
		super (cls, fields.length);
		if ((nodeType != null) && !Reflection.equal (nodeType, getParameterType (0)))
		{
			throw new IllegalArgumentException ("Illegal node type");
		}
		for (int i = 0; i < fields.length; i++)
		{
			if ((fields[i] != null)
				&& !Reflection.equal (getParameterType (i + 1), fields[i].getType ()))
			{
				throw new IllegalArgumentException ("Illegal field type " + fields[i].getType () + ", expected " + getParameterType (i + 1));
			}
		}
		this.fields = fields;
	}


	public FieldListPattern (Type cls, Type nodeType, Field field)
	{
		this (cls, nodeType, new Field[] {field});
	}


	public FieldListPattern (Type nodeType, Field field)
	{
		this (null, nodeType, new Field[] {field});
	}


/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override
	protected $type get$pp.Type (Object o, int index)
	{
		try
		{
			return fields[index].get$pp.Type (o);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}

#end

!!*/
//!! #* Start of generated code
// generated
// generated
	@Override
	protected boolean getBoolean (Object o, int index)
	{
		try
		{
			return fields[index].getBoolean (o);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
// generated
// generated
	@Override
	protected byte getByte (Object o, int index)
	{
		try
		{
			return fields[index].getByte (o);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
// generated
// generated
	@Override
	protected short getShort (Object o, int index)
	{
		try
		{
			return fields[index].getShort (o);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
// generated
// generated
	@Override
	protected char getChar (Object o, int index)
	{
		try
		{
			return fields[index].getChar (o);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
// generated
// generated
	@Override
	protected int getInt (Object o, int index)
	{
		try
		{
			return fields[index].getInt (o);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
// generated
// generated
	@Override
	protected long getLong (Object o, int index)
	{
		try
		{
			return fields[index].getLong (o);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
// generated
// generated
	@Override
	protected float getFloat (Object o, int index)
	{
		try
		{
			return fields[index].getFloat (o);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
// generated
// generated
	@Override
	protected double getDouble (Object o, int index)
	{
		try
		{
			return fields[index].getDouble (o);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
// generated
// generated
	@Override
	protected Object getObject (Object o, int index)
	{
		try
		{
			return fields[index].getObject (o);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalAccessError (e.getMessage ());
		}
	}
// generated
// generated
//!! *# End of generated code


	@Override
	protected String paramString ()
	{
		return Arrays.toString (fields);
	}

}
