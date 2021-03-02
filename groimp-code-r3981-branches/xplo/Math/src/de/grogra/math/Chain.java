
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

package de.grogra.math;

import de.grogra.persistence.*;
import de.grogra.vecmath.Math2;
import de.grogra.xl.lang.FloatToFloat;

public class Chain extends ShareableBase implements FloatToFloat
{
	//enh:sco SCOType

	FloatToFloat first;
	//enh:field

	float factor = 1;
	//enh:field

	float shift = 0;
	//enh:field

	FloatToFloat second;
	//enh:field

	public float evaluateFloat (float v)
	{
		if (first != null)
		{
			v = first.evaluateFloat (v);
		}
		v = factor * v + shift;
		if (second != null)
		{
			v = second.evaluateFloat (v);
		}
		return v;
	}
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field first$FIELD;
	public static final Type.Field factor$FIELD;
	public static final Type.Field shift$FIELD;
	public static final Type.Field second$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Chain representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 4;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((Chain) o).factor = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((Chain) o).shift = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((Chain) o).factor;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((Chain) o).shift;
			}
			return super.getFloat (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((Chain) o).first = (FloatToFloat) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((Chain) o).second = (FloatToFloat) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((Chain) o).first;
				case Type.SUPER_FIELD_COUNT + 3:
					return ((Chain) o).second;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new Chain ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (Chain.class);
		first$FIELD = Type._addManagedField ($TYPE, "first", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (FloatToFloat.class), null, Type.SUPER_FIELD_COUNT + 0);
		factor$FIELD = Type._addManagedField ($TYPE, "factor", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		shift$FIELD = Type._addManagedField ($TYPE, "shift", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		second$FIELD = Type._addManagedField ($TYPE, "second", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (FloatToFloat.class), null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

//enh:end
	
}
