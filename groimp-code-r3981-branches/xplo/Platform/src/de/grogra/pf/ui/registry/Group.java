
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

import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.tree.UINodeHandler;

public class Group extends Item implements UIItem
{
	private static final int ITEM_GROUP_MASK = 1 << Item.USED_BITS;
	public static final int USED_BITS = Item.USED_BITS + 1;

	// boolean itemGroup
	//enh:field type=bits(ITEM_GROUP_MASK)

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field itemGroup$FIELD;

	static
	{
		$TYPE = new NType (new Group ());
		$TYPE.addManagedField (itemGroup$FIELD = new NType.BitField ($TYPE, "itemGroup", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, ITEM_GROUP_MASK));
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
		return new Group ();
	}

//enh:end


	private Group ()
	{
		this (null, false);
	}


	public Group (String key, boolean itemGroup)
	{
		super (key);
		setDirectory ();
		if (itemGroup)
		{
			bits |= ITEM_GROUP_MASK;
		}
	}


	public int getUINodeType ()
	{
		return ((bits & ITEM_GROUP_MASK) != 0) ? UINodeHandler.NT_ITEM_GROUP
			: UINodeHandler.NT_GROUP;
	}


	public Object invoke (Context ctx, String method, Object arg)
	{
		return null;
	}


	public boolean isAvailable (Context ctx)
	{
		return true;
	}

	
	public boolean isEnabled (Context ctx)
	{
		return true;
	}

}
