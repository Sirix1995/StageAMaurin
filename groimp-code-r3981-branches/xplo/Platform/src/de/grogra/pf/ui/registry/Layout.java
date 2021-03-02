
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

import de.grogra.persistence.PersistenceBindings;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;

public class Layout extends Item implements Command, UIItem
{

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Layout ());
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
		return new Layout ();
	}

//enh:end

	private Layout ()
	{
		this (null);
	}


	public Layout (String key)
	{
		super (key);
	}


	@Override
	protected Item createItem (PersistenceBindings pb,
							   String name)
		throws InstantiationException, IllegalAccessException,
		java.lang.reflect.InvocationTargetException, ClassNotFoundException
	{
		if ("main".equals (name))
		{
			return new MainWindow (null);
		}
		else if ("split".equals (name))
		{
			return new Split (null, true, -1);
		}
		else if ("tab".equals (name))
		{
			return new Tab (null, -1);
		}
		return super.createItem (pb, name);
	}


	public String getCommandName ()
	{
		return (String) getDescription (SHORT_DESCRIPTION);
	}


	public void run (Object info, Context ctx)
	{
		ctx.getWindow ().setLayout (this, this);
	}


	public Object invoke (Context ctx, String method, Object arg)
	{
		return null;
	}


	public boolean isAvailable (Context ctx)
	{
		return UI.isAvailable (this, ctx);
	}


	public boolean isEnabled (Context ctx)
	{
		return true;
	}

	
	public int getUINodeType ()
	{
		return de.grogra.pf.ui.tree.UINodeHandler.NT_ITEM;
	}


	public static void addLayout (Item item, Object info, Context ctx)
	{
		Layout layout = ctx.getWindow ().getLayout ();
		layout.makeUserItem (true);
		UI.getRegistry (ctx).getDirectory ("/project/layouts", null)
			.addUserItemWithUniqueName (layout, "Layout");
	}

}
