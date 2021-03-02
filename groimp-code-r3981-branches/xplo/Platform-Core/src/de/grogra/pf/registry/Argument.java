
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

package de.grogra.pf.registry;

public final class Argument extends Item
{
	private static final int OVERRIDE_MASK = 1 << Item.USED_BITS;
	public static final int USED_BITS = Item.USED_BITS + 1;

	// boolean override
	//enh:field type=bits(OVERRIDE_MASK) getter

	String value;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field override$FIELD;
	public static final NType.Field value$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Argument.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Argument) o).value = (String) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Argument) o).value;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Argument ());
		$TYPE.addManagedField (override$FIELD = new NType.BitField ($TYPE, "override", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, OVERRIDE_MASK));
		$TYPE.addManagedField (value$FIELD = new _Field ("value", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
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
		return new Argument ();
	}

	public boolean isOverride ()
	{
		return (bits & OVERRIDE_MASK) != 0;
	}

//enh:end

	Argument ()
	{
		super (null);
	}


	public String getValue ()
	{
		return value;
	}

}
