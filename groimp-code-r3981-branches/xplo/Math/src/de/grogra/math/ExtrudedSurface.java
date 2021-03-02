
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

public class ExtrudedSurface extends ProductSurface
{
	//enh:sco

	boolean useScale;
	//enh:field setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field useScale$FIELD;

	public static class Type extends ProductSurface.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ExtrudedSurface representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, ProductSurface.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = ProductSurface.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = ProductSurface.Type.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((ExtrudedSurface) o).useScale = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((ExtrudedSurface) o).useScale;
			}
			return super.getBoolean (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new ExtrudedSurface ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (ExtrudedSurface.class);
		useScale$FIELD = Type._addManagedField ($TYPE, "useScale", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public void setUseScale (boolean value)
	{
		this.useScale = (boolean) value;
	}

//enh:end


	public ExtrudedSurface ()
	{
		super ();
	}


	public ExtrudedSurface (BSplineCurve profile, BSplineCurve trajectory)
	{
		super (profile, trajectory);
	}


	public boolean usesScale ()
	{
		return useScale;
	}


	public int getVertex (float[] out, int index, GraphState gs)
	{
		int n = profile.getDimension (gs), v = trajectory.getDimension (gs);
		if (v > n)
		{
			n = v;
		}
		Pool pool = Pool.push (gs);
		float[] a = (n > Math.min (4, out.length)) ? pool.getFloatArray (0, n) : out;
		n = profile.getSize (gs);
		v = index / n;
		n = profile.getVertex (a, index - v * n, gs);
		float px, py, pz, pw;
		if (profile.isRational (gs))
		{
			pw = a[n - 1];
			px = a[0];
			py = (n > 2) ? a[1] : 0;
			pz = (n > 3) ? a[2] : 0;
		}
		else
		{
			pw = 1;
			px = a[0];
			py = (n > 1) ? a[1] : 0;
			pz = (n > 2) ? a[2] : 0;
		}
		n = trajectory.getVertex (a, v, gs);
		float tx, ty, tz, tw, ts;
		tw = trajectory.isRational (gs) ? a[--n] : 1;
		ts = useScale ? a[--n] : tw;
		tx = a[0];
		ty = (n > 1) ? a[1] : 0;
		tz = (n > 2) ? a[2] : 0;
		pool.pop (gs);
		return BSpline.set (out, px * ts + tx * pw, py * ts + ty * pw,
							pz * ts + tz * pw, pw * tw);
	}

}
