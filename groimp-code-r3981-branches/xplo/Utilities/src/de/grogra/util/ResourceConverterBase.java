
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

package de.grogra.util;

public abstract class ResourceConverterBase implements ResourceConverter
{
	protected final String name;
	protected StringMap map;


	public ResourceConverterBase (String name, StringMap map)
	{
		this.name = name;
		this.map = map;
	}


	public boolean canHandleConversion (String name)
	{
		return this.name.equals (name);
	}


	public Object convert (String name, String resource, I18NBundle bundle)
	{
		if (resource == null)
		{
			return resource;
		}
		Object o;
		if ((o = map.get (resource)) == null)
		{
			o = convertImpl (resource, bundle);
			map.put (resource, (o == null) ? this : o);
		}
		else if (o == this)
		{
			o = null;
		}
		return o;
	}


	protected abstract Object convertImpl (String key, I18NBundle bundle);

}
