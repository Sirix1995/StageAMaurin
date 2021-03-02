
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

package de.grogra.pf.ui.registry;

import de.grogra.graph.*;
import de.grogra.pf.ui.edit.*;
import de.grogra.pf.registry.*;

public class AttributeItem extends Item
{
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new AttributeItem ());
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new AttributeItem ();
	}

//enh:end


	private AttributeItem ()
	{
		this (null);
	}


	public AttributeItem (String key)
	{
		super (key);
	}


	@Override
	public Item initPluginDescriptor (PluginDescriptor plugin)
	{
		String n = getName ();
		super.initPluginDescriptor (plugin);
		setName (n);
		return this;
	}


	@Override
	public void setName (String name)
	{
		if ((name != null) && (name.indexOf ('.') < 0)
			&& (getPluginDescriptor () != null))
		{
			name = getPluginDescriptor ().getName () + '.' + name;
		}
		super.setName (name);
	}


	public boolean correspondsTo (Attribute a)
	{
		return hasName (a.getKey ());
	}


	public PropertyEditor getEditor ()
	{
		Item i = (Item) getBranch ();
		if ((i != null) && (i.resolveLink (this) instanceof PropertyEditor))
		{
			return (PropertyEditor) i.resolveLink (this);
		}
		return null;
	}


	private static final ItemCriterion CRITERION = new ItemCriterion ()
	{
		public boolean isFulfilled (Item item, Object info)
		{
			return (item instanceof AttributeItem)
				&& ((AttributeItem) item).correspondsTo ((Attribute) info);
		}

		public String getRootDirectory ()
		{
			return null;
		}
	};


	public static AttributeItem get (RegistryContext c, Attribute a)
	{
		return (AttributeItem) findFirst (c, "/attributes", CRITERION, a, true);
	}

}
