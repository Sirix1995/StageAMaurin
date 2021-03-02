
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

public class RectangularHeightFieldMapping extends HeightFieldMapping
{
	//enh:sco

	float xWidth = 1;
	//enh:field quantity=LENGTH getter setter

	float yWidth = 1;
	//enh:field quantity=LENGTH getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field xWidth$FIELD;
	public static final Type.Field yWidth$FIELD;

	public static class Type extends HeightFieldMapping.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (RectangularHeightFieldMapping representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, HeightFieldMapping.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = HeightFieldMapping.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = HeightFieldMapping.Type.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((RectangularHeightFieldMapping) o).xWidth = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((RectangularHeightFieldMapping) o).yWidth = (float) value;
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
					return ((RectangularHeightFieldMapping) o).getXWidth ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((RectangularHeightFieldMapping) o).getYWidth ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new RectangularHeightFieldMapping ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (RectangularHeightFieldMapping.class);
		xWidth$FIELD = Type._addManagedField ($TYPE, "xWidth", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		yWidth$FIELD = Type._addManagedField ($TYPE, "yWidth", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		xWidth$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		yWidth$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		$TYPE.validate ();
	}

	public float getXWidth ()
	{
		return xWidth;
	}

	public void setXWidth (float value)
	{
		this.xWidth = (float) value;
	}

	public float getYWidth ()
	{
		return yWidth;
	}

	public void setYWidth (float value)
	{
		this.yWidth = (float) value;
	}

//enh:end


	@Override
	public void map (int x, int y, int sx, int sy, float height, Tuple3f out)
	{
		out.x = x * xWidth / (sx - 1);
		out.y = y * yWidth / (sy - 1);
		out.z = (water && (height <= zeroLevel)) ? 0 : (height - zeroLevel) * scale;
	}

}
