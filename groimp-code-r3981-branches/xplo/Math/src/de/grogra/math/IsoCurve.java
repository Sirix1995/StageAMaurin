
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

import de.grogra.graph.Cache;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.ContextDependentBase;
import de.grogra.persistence.SCOType;

public class IsoCurve extends ContextDependentBase implements BSplineCurve
{
	//enh:sco SCOType

	BSplineSurface surface;
	//enh:field getter setter

	float parameter = 0.5f;
	//enh:field getter setter min=0 max=1

	boolean direction;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field surface$FIELD;
	public static final Type.Field parameter$FIELD;
	public static final Type.Field direction$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (IsoCurve representative, de.grogra.persistence.SCOType supertype)
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
					((IsoCurve) o).direction = (boolean) value;
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
					return ((IsoCurve) o).isDirection ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((IsoCurve) o).parameter = (float) value;
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
					return ((IsoCurve) o).getParameter ();
			}
			return super.getFloat (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((IsoCurve) o).surface = (BSplineSurface) value;
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
					return ((IsoCurve) o).getSurface ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new IsoCurve ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (IsoCurve.class);
		surface$FIELD = Type._addManagedField ($TYPE, "surface", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (BSplineSurface.class), null, Type.SUPER_FIELD_COUNT + 0);
		parameter$FIELD = Type._addManagedField ($TYPE, "parameter", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		direction$FIELD = Type._addManagedField ($TYPE, "direction", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 2);
		parameter$FIELD.setMinValue (new Float (0));
		parameter$FIELD.setMaxValue (new Float (1));
		$TYPE.validate ();
	}

	public boolean isDirection ()
	{
		return direction;
	}

	public void setDirection (boolean value)
	{
		this.direction = (boolean) value;
	}

	public float getParameter ()
	{
		return parameter;
	}

	public void setParameter (float value)
	{
		this.parameter = (float) value;
	}

	public BSplineSurface getSurface ()
	{
		return surface;
	}

	public void setSurface (BSplineSurface value)
	{
		surface$FIELD.setObject (this, value);
	}

//enh:end


	public IsoCurve ()
	{
		super ();
	}


	public IsoCurve (BSplineSurface surface)
	{
		this ();
		this.surface = surface;
	}


	public boolean dependsOnContext ()
	{
		return surface.dependsOnContext ();
	}


	@Override
	public void writeStamp (Cache.Entry cache, GraphState gs)
	{
		super.writeStamp (cache, gs);
		surface.writeStamp (cache, gs);
	}


	public int getVertex (float[] out, int index, GraphState gs)
	{
		int n = (direction ? surface.getVSize (gs) : surface.getUSize (gs)) - 1;
		int p = direction ? surface.getVDegree (gs) : surface.getUDegree (gs);
		float min = surface.getKnot (direction ? 1 : 0, 0, gs);
		float max = surface.getKnot (direction ? 1 : 0, n + p, gs);
		float param = parameter * (max - min) + min;
		int s = BSpline.findSpan (n, p, param, surface, direction ? 1 : 0, gs);
		n = Math.min (out.length, surface.getDimension (gs));
		for (int k = n - 1; k >= 0; k--)
		{
			out[k] = 0;
		}
		Pool pool = Pool.push (gs);
		float[] bfu = pool.getFloatArray (0, p + 1),
			left = pool.getFloatArray (2, Math.max (n, p + 1)),
			right = pool.getFloatArray (3, p + 1);
		BSpline.calculateBasisFunctions (bfu, p, surface, direction ? 1 : 0, s, param, gs, left, right);
		s -= p;
		while (p >= 0)
		{
			float t = bfu[p];
			surface.getVertex
				(left,
				 surface.getVertexIndex (direction ? index : s + p,
				 						 direction ? s + p : index, gs),
				 gs);
			for (int i = n - 1; i >= 0; i--)
			{
				out[i] += t * left[i];
			}
			--p;
		}
		pool.pop (gs);
		return n;
	}


	public int getSize (GraphState gs)
	{
		return direction ? surface.getUSize (gs) : surface.getVSize (gs);
	}


	public int getDegree (GraphState gs)
	{
		return direction ? surface.getUDegree (gs) : surface.getVDegree (gs);
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		return surface.getKnot (direction ? 0 : 1, index, gs);
	}


	public boolean isRational (GraphState gs)
	{
		return surface.isRational (gs);
	}


	public int getDimension (GraphState gs)
	{
		return surface.getDimension (gs);
	}

}
