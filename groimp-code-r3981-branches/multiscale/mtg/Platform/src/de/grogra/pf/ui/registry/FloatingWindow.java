
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

public class FloatingWindow extends Item
{
	int width;
	//enh:field getter;

	int height;
	//enh:field getter;

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field width$FIELD;
	public static final NType.Field height$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (FloatingWindow.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 0:
					((FloatingWindow) o).width = (int) value;
					return;
				case 1:
					((FloatingWindow) o).height = (int) value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 0:
					return ((FloatingWindow) o).getWidth ();
				case 1:
					return ((FloatingWindow) o).getHeight ();
			}
			return super.getInt (o);
		}
	}

	static
	{
		$TYPE = new NType (new FloatingWindow ());
		$TYPE.addManagedField (width$FIELD = new _Field ("width", 0 | _Field.SCO, de.grogra.reflect.Type.INT, null, 0));
		$TYPE.addManagedField (height$FIELD = new _Field ("height", 0 | _Field.SCO, de.grogra.reflect.Type.INT, null, 1));
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
		return new FloatingWindow ();
	}

	public int getWidth ()
	{
		return width;
	}

	public int getHeight ()
	{
		return height;
	}

//enh:end

	private FloatingWindow ()
	{
		this (null, 0, 0);
	}


	public FloatingWindow (String key, int width, int height)
	{
		super (key);
		this.width = width;
		this.height = height;
	}

}
