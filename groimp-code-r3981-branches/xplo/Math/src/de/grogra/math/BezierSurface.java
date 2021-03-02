
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

public class BezierSurface extends VertexGridImpl implements BSplineSurface
{
	//enh:sco

	boolean rational;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field rational$FIELD;

	public static class Type extends VertexGridImpl.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (BezierSurface representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, VertexGridImpl.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = VertexGridImpl.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = VertexGridImpl.Type.FIELD_COUNT + 1;

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
					((BezierSurface) o).rational = (boolean) value;
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
					return ((BezierSurface) o).isRational ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new BezierSurface ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (BezierSurface.class);
		rational$FIELD = Type._addManagedField ($TYPE, "rational", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public boolean isRational ()
	{
		return rational;
	}

	public void setRational (boolean value)
	{
		this.rational = (boolean) value;
	}

//enh:end


	public BezierSurface ()
	{
		super ();
	}


	public BezierSurface (float[] data, int dimension, int uCount)
	{
		super (data, dimension, uCount);
	}


	@Override
	public int getUSize (GraphState gs)
	{
		return uCount;
	}


	@Override
	public int getVSize (GraphState gs)
	{
		return data.length / (uCount * dimension);
	}


	public int getUDegree (GraphState gs)
	{
		return uCount - 1;
	}


	public int getVDegree (GraphState gs)
	{
		return data.length / (uCount * dimension) - 1;
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		return ((dim == 0) ? (index >= uCount)
				: (index * uCount * dimension >= data.length)) ? 1 : 0;
	}


	@Override
	public boolean isRational (GraphState gs)
	{
		return rational;
	}

}
