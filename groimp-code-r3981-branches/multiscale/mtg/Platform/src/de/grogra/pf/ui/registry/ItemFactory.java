
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

import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;

public class ItemFactory extends de.grogra.pf.registry.expr.ObjectExpr
	implements UIItem
{

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new ItemFactory ());
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
		return new ItemFactory ();
	}

//enh:end

	public final Item createItem (Context ctx)
	{
		Item i = createItemImpl (ctx);
		if ((i != null) && (i.getName () == null))
		{
			setNameIfNull (i, (String) getDescription (NAME));
		}
		return i;
	}


	protected Item createItemImpl (Context ctx)
	{
		return (Item) evaluate (ctx.getWorkbench (),
								UI.getArgs (ctx, this));
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


	public int getUINodeType ()
	{
		return de.grogra.pf.ui.tree.UINodeHandler.NT_ITEM;
	}

}
