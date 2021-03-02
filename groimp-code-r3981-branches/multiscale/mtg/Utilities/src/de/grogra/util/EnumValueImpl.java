
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

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class EnumValueImpl implements EnumValue, Described, ListModel
{
	private final I18NBundle i18n;
	private final String key;
	private final Object value;
	private final ListModel list;

	
	public EnumValueImpl (I18NBundle i18n, String key, ListModel list)
	{
		this.i18n = i18n;
		this.key = key;
		this.value = null;
		this.list = list;
	}

	
	public EnumValueImpl (Object value, ListModel list)
	{
		this.i18n = null;
		this.key = null;
		this.value = value;
		this.list = list;
	}

	
	public Object getDescription (String type)
	{
		return (value == null) ? Utils.get (i18n, key, type, null)
			: (value instanceof Described) ? ((Described) value).getDescription (type)
			: Utils.isStringDescription (type) ? value.toString () : null;
	}
	
	
	@Override
	public String toString ()
	{
		return (String) getDescription (NAME);
	}


	public int getSize ()
	{
		return list.getSize ();
	}


	public Object getElementAt (int index)
	{
		return list.getElementAt (index);
	}


	public void addListDataListener (ListDataListener l)
	{
	}


	public void removeListDataListener (ListDataListener l)
	{
	}

	
	public ListModel getList ()
	{
		return this;
	}


	public Object getValue ()
	{
		return value;
	}

	
	@Override
	public boolean equals (Object o)
	{
		if (!(o instanceof EnumValueImpl))
		{
			return false;
		}
		EnumValueImpl e = (EnumValueImpl) o;
		if (value != null)
		{
			return (e.value != null) && value.equals (e.value);
		}
		return (e.value == null) && (i18n == e.i18n) && key.equals (e.key);
	}
	
}
