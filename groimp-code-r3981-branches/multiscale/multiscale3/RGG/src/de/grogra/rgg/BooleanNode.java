
// NOTE: This file was generated automatically.

// ********************************************
// *               DO NOT EDIT!               *
// ********************************************


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

package de.grogra.rgg;

public class BooleanNode extends de.grogra.graph.impl.Node
{
	boolean value;
	//enh:field setter

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field value$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (BooleanNode.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 0:
					((BooleanNode) o).value = (boolean) value;
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
					return ((BooleanNode) o).value;
			}
			return super.getBoolean (o);
		}
	}

	static
	{
		$TYPE = new NType (new BooleanNode ());
		$TYPE.addManagedField (value$FIELD = new _Field ("value", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 0));
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
		return new BooleanNode ();
	}

	public void setValue (boolean value)
	{
		this.value = (boolean) value;
	}

//enh:end


	public BooleanNode ()
	{
		super ();
	}


	public BooleanNode (boolean value)
	{
		this ();
		this.value = value;
	}


	public boolean getValue ()
	{
		return value;
	}

}

