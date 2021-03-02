
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
import de.grogra.pf.ui.tree.*;

public class Separator extends Item implements UIItem
{
	private static final int FILL_MASK = 1 << Item.USED_BITS;
	public static final int USED_BITS = Item.USED_BITS + 1;

	// boolean fill
	//enh:field type=bits(FILL_MASK)

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field fill$FIELD;

	static
	{
		$TYPE = new NType (new Separator ());
		$TYPE.addManagedField (fill$FIELD = new NType.BitField ($TYPE, "fill", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, FILL_MASK));
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
		return new Separator ();
	}

//enh:end


	private Separator ()
	{
		this (null, false);
	}


	public Separator (String key)
	{
		this (key, false);
	}


	public Separator (String key, boolean fill)
	{
		super (key);
		if (fill)
		{
			bits |= FILL_MASK;
		}
	}


	public int getUINodeType ()
	{
		return ((bits & FILL_MASK) != 0) ? UINodeHandler.NT_FILL
			: UINodeHandler.NT_SEPARATOR;
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
