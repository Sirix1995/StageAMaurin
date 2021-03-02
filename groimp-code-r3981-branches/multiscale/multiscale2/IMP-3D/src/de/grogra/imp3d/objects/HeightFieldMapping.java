
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

package de.grogra.imp3d.objects;

import javax.vecmath.*;
import de.grogra.persistence.*;

public abstract class HeightFieldMapping extends ShareableBase
{
	//enh:sco SCOType

	float scale = 1;
	//enh:field quantity=LENGTH getter setter
	
	float zeroLevel = 0;
	//enh:field getter setter

	boolean water = false;
	//enh:field getter setter

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field scale$FIELD;
	public static final Type.Field zeroLevel$FIELD;
	public static final Type.Field water$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (HeightFieldMapping representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 3;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					((HeightFieldMapping) o).water = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					return ((HeightFieldMapping) o).isWater ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((HeightFieldMapping) o).scale = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((HeightFieldMapping) o).zeroLevel = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((HeightFieldMapping) o).getScale ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((HeightFieldMapping) o).getZeroLevel ();
			}
			return super.getFloat (o, id);
		}
	}

	static
	{
		$TYPE = new Type (HeightFieldMapping.class);
		scale$FIELD = Type._addManagedField ($TYPE, "scale", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		zeroLevel$FIELD = Type._addManagedField ($TYPE, "zeroLevel", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		water$FIELD = Type._addManagedField ($TYPE, "water", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 2);
		scale$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		$TYPE.validate ();
	}

	public boolean isWater ()
	{
		return water;
	}

	public void setWater (boolean value)
	{
		this.water = (boolean) value;
	}

	public float getScale ()
	{
		return scale;
	}

	public void setScale (float value)
	{
		this.scale = (float) value;
	}

	public float getZeroLevel ()
	{
		return zeroLevel;
	}

	public void setZeroLevel (float value)
	{
		this.zeroLevel = (float) value;
	}

//enh:end

	public abstract void map (int x, int y, int sx, int sy, float height,
							  Tuple3f out);
}
