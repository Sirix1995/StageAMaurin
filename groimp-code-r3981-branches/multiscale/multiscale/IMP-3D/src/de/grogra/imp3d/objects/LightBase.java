
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

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3f;

import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.shading.Light;
import de.grogra.math.RGBColor;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.util.Ray;

public abstract class LightBase extends ShareableBase implements Light
{
	//enh:sco SCOType
	
	final RGBColor color = new RGBColor (1, 1, 1);
	//enh:field type=RGBColor.$TYPE set=set getter

	boolean shadowless = false;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field color$FIELD;
	public static final Type.Field shadowless$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (LightBase representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((LightBase) o).shadowless = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((LightBase) o).isShadowless ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((LightBase) o).color.set ((RGBColor) value);
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
					return ((LightBase) o).getColor ();
			}
			return super.getObject (o, id);
		}
	}

	static
	{
		$TYPE = new Type (LightBase.class);
		color$FIELD = Type._addManagedField ($TYPE, "color", Type.Field.FINAL  | Type.Field.SCO, RGBColor.$TYPE, null, Type.SUPER_FIELD_COUNT + 0);
		shadowless$FIELD = Type._addManagedField ($TYPE, "shadowless", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

	public boolean isShadowless ()
	{
		return shadowless;
	}

	public void setShadowless (boolean value)
	{
		this.shadowless = (boolean) value;
	}

	public RGBColor getColor ()
	{
		return color;
	}

//enh:end

	private static int f2i (float f)
	{
		int i = Math.round (f * 255);
		return (i < 0) ? 0 : (i > 255) ? 255 : i;
	}


	public int getAverageColor ()
	{
		return (f2i (color.x) << 16) + (f2i (color.y) << 8)
			+ f2i (color.z) + (255 << 24);
	}


	public int getFlags ()
	{
		return NEEDS_TRANSFORMATION;
	}

	public boolean isIgnoredWhenHit ()
	{
		return true;
	}

	protected void draw (Tuple3f color, RenderState rs)
	{
	}


	public double completeRay (Environment env, Point3d vertex, Ray out)
	{
		throw new UnsupportedOperationException ();
	}

}
