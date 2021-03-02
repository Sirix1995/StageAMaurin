
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

import de.grogra.graph.GraphState;

public class SwungSurface extends ProductSurface
{
	//enh:sco

	float shift = 0;
	//enh:field quantity=LENGTH getter setter

	float scale = 1;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field shift$FIELD;
	public static final Type.Field scale$FIELD;

	public static class Type extends ProductSurface.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SwungSurface representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, ProductSurface.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = ProductSurface.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = ProductSurface.Type.FIELD_COUNT + 2;

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
					((SwungSurface) o).shift = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((SwungSurface) o).scale = (float) value;
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
					return ((SwungSurface) o).getShift ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((SwungSurface) o).getScale ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SwungSurface ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SwungSurface.class);
		shift$FIELD = Type._addManagedField ($TYPE, "shift", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		scale$FIELD = Type._addManagedField ($TYPE, "scale", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		shift$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		$TYPE.validate ();
	}

	public float getShift ()
	{
		return shift;
	}

	public void setShift (float value)
	{
		this.shift = (float) value;
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


	public SwungSurface ()
	{
		super ();
	}


	public SwungSurface (BSplineCurve profile, BSplineCurve trajectory,
						 float shift, float scale)
	{
		super (profile, trajectory);
		this.shift = shift;
		this.scale = scale;
	}


	public int getVertex (float[] out, int index, GraphState gs)
	{
		int n = profile.getDimension (gs);
		int v = trajectory.getDimension (gs);
		Pool pool = Pool.push (gs);
		int s = (n > v) ? n : v;
		float[] a = ((s > out.length) || (s > 4)) ? pool.getFloatArray (0, s) : out;
		n = profile.getSize (gs);
		v = index / n;
		n = profile.getVertex (a, index - v * n, gs);
		float px, py, pw;
		if (profile.isRational (gs))
		{
			pw = a[n - 1];
			px = (a[0] + pw * shift) * scale;
			py = a[(n > 4) ? 2 : n - 2] * scale;
		}
		else
		{
			pw = 1;
			px = (a[0] + shift) * scale;
			py = a[(n > 3) ? 2 : n - 1] * scale;
		}
		n = trajectory.getVertex (a, v, gs);
		float tx, ty, tz, tw;
		if (trajectory.isRational (gs))
		{
			tw = a[n - 1];
			tx = a[0];
			ty = (n > 2) ? a[1] : 0;
			tz = (n > 3) ? a[2] : 0;
		}
		else
		{
			tw = 1;
			tx = a[0];
			ty = (n > 1) ? a[1] : 0;
			tz = (n > 2) ? a[2] : 0;
		}
		pool.pop (gs);
		return BSpline.set (out, px * tx, px * ty, py * tw + pw * tz, pw * tw);
	}

}
