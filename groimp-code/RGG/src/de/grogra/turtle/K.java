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

package de.grogra.turtle;

import de.grogra.graph.impl.Node;

public class K extends Node {

	private int argument;
	//enh:field getter
	
	public K(int argument) {
		this.argument = argument;
	}
	
	private K() {
		
	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field argument$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (K.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 0:
					((K) o).argument = (int) value;
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
					return ((K) o).getArgument ();
			}
			return super.getInt (o);
		}
	}

	static
	{
		$TYPE = new NType (new K ());
		$TYPE.addManagedField (argument$FIELD = new _Field ("argument", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.Type.INT, null, 0));
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
		return new K ();
	}

	public int getArgument ()
	{
		return argument;
	}

//enh:end

	
}
