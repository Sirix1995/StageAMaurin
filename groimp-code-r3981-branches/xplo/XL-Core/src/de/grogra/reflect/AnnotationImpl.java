
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

import java.util.HashMap;

public class AnnotationImpl<T extends java.lang.annotation.Annotation> implements Annotation<T>
{
	private final Type<T> type;
	private final HashMap<String,Object> values = new HashMap<String, Object> ();
	

	public AnnotationImpl (Type<T> type)
	{
		this.type = type;
	}

	public AnnotationImpl (Class<T> type)
	{
		this (ClassAdapter.wrap (type));
	}

	public AnnotationImpl setValue (String element, Object value)
	{
		values.put (element, value);
		return this;
	}

	public AnnotationImpl setValue (String element, Class<?> value)
	{
		return setValue (element, ClassAdapter.wrap (value));
	}

	public Type<T> annotationType ()
	{
		return type;
	}

	public Object value (String element)
	{
		Object v = values.get (element);
		if (v == null)
		{
			v = type.getDefaultElementValue (element);
		}
		return v;
	}

	public String[] elements ()
	{
		return values.keySet ().toArray (new String[0]);
	}
}
