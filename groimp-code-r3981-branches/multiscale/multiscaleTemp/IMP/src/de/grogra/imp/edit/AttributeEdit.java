
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

package de.grogra.imp.edit;

import de.grogra.graph.*;
import de.grogra.util.*;

final class AttributeEdit extends Edit
{
	private final Object object;
	private final Attribute attribute;
	private I18NBundle bundle;
	private String key;
	private int spec;
	private final Object[] args;


	AttributeEdit (Object object, String objectName,
				   Attribute attribute, I18NBundle bundle, String key,
				   int spec)
	{
		this.object = object;
		this.attribute = attribute;
		this.bundle = bundle;
		this.key = key;
		this.spec = spec;
		args = new Object[] {objectName, attribute.getDescription (NAME)};
	}


	public Object getDescription (String type)
	{
		return null;//Described.Util.get (bundle, key, type, args);
	}


	@Override
	boolean addEdit (Edit edit)
	{
		if (edit instanceof AttributeEdit)
		{
			AttributeEdit e = (AttributeEdit) edit;
			if ((e.object == object) && (e.attribute == attribute))
			{
				if (e.spec > spec)
				{
					spec = e.spec;
					bundle = e.bundle;
					key = e.key;
				}
				return true;
			}
		}
		return false;
	}

}
