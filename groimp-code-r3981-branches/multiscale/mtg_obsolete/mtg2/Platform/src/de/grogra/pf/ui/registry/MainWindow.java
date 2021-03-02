
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

public class MainWindow extends Item
{
	boolean menuVisible = true;
	//enh:field getter

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field menuVisible$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (MainWindow.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 0:
					((MainWindow) o).menuVisible = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 0:
					return ((MainWindow) o).isMenuVisible ();
			}
			return super.getBoolean (o);
		}
	}

	static
	{
		$TYPE = new NType (new MainWindow ());
		$TYPE.addManagedField (menuVisible$FIELD = new _Field ("menuVisible", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 0));
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
		return new MainWindow ();
	}

	public boolean isMenuVisible ()
	{
		return menuVisible;
	}

//enh:end

	private MainWindow ()
	{
		this (null);
	}


	public MainWindow (String key)
	{
		super (key);
	}
}
