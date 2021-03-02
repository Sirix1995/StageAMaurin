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

import de.grogra.reflect.Type;

public class KeyDescriptionImpl implements KeyDescription
{
	private final Type type;
	private final Quantity quantity;
	private final String key;
	private final I18NBundle bundle;
	private final String bundleKey;
	private final String description;

	public KeyDescriptionImpl (String key, I18NBundle bundle, String bundleKey,
			Type type, Quantity quantity)
	{
		this.key = key;
		this.bundle = bundle;
		this.bundleKey = bundleKey;
		this.type = type;
		this.quantity = quantity;
		this.description = null;
	}

	public KeyDescriptionImpl (String key, String description,
			Type type, Quantity quantity)
	{
		this.key = key;
		this.bundle = null;
		this.bundleKey = null;
		this.type = type;
		this.quantity = quantity;
		this.description = description;
	}

	public Object getDescription (String type)
	{
		return (description != null) ? description : Utils.get (bundle, bundleKey, type, null);
	}

	public String getKey ()
	{
		return key;
	}

	public Type getType ()
	{
		return type;
	}

	public Quantity getQuantity ()
	{
		return quantity;
	}
}
