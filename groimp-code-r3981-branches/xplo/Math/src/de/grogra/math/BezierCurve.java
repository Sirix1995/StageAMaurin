
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

public class BezierCurve extends VertexListImpl implements BSplineCurve
{
	//enh:sco

	boolean rational;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field rational$FIELD;

	public static class Type extends VertexListImpl.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (BezierCurve representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, VertexListImpl.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = VertexListImpl.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = VertexListImpl.Type.FIELD_COUNT + 1;

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
					((BezierCurve) o).rational = (boolean) value;
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
					return ((BezierCurve) o).isRational ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new BezierCurve ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (BezierCurve.class);
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


	public BezierCurve ()
	{
		super ();
	}


	public BezierCurve (float[] data, int dimension)
	{
		super (data, dimension);
	}


	@Override
	public int getSize (GraphState gs)
	{
		return data.length / dimension;
	}


	public int getDegree (GraphState gs)
	{
		return data.length / dimension - 1;
	}


	@Override
	public boolean isRational (GraphState gs)
	{
		return rational;
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		return (index * dimension >= data.length) ? 1 : 0;
	}

}
