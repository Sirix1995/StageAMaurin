
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

import javax.vecmath.*;
import de.grogra.persistence.*;

public final class UniformScale extends ShareableBase
	implements Transform2D, Transform3D
{
	//enh:sco SCOType

	float scale;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field scale$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (UniformScale representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 1;

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
					((UniformScale) o).scale = (float) value;
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
					return ((UniformScale) o).getScale ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new UniformScale ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (UniformScale.class);
		scale$FIELD = Type._addManagedField ($TYPE, "scale", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public float getScale ()
	{
		return scale;
	}

	public void setScale (float value)
	{
		this.scale = (float) value;
	}

//enh:end


	public UniformScale ()
	{
		this (1);
	}


	public UniformScale (float scale)
	{
		this.scale = scale;
	}


	public void transform (Matrix3d in, Matrix3d out)
	{
		out.m00 = in.m00 * scale;
		out.m10 = in.m10 * scale;
		out.m01 = in.m01 * scale;
		out.m11 = in.m11 * scale;
		if (out != in)
		{
			out.m02 = in.m02;
			out.m12 = in.m12;
		}
	}


	public void transform (Matrix4d in, Matrix4d out)
	{
		out.m00 = in.m00 * scale;
		out.m10 = in.m10 * scale;
		out.m20 = in.m20 * scale;
		out.m01 = in.m01 * scale;
		out.m11 = in.m11 * scale;
		out.m21 = in.m21 * scale;
		out.m02 = in.m02 * scale;
		out.m12 = in.m12 * scale;
		out.m22 = in.m22 * scale;
		if (out != in)
		{
			out.m03 = in.m03;
			out.m13 = in.m13;
			out.m23 = in.m23;
		}
	}

}
