
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

public class BSplineCurveImpl extends VertexListImpl implements BSplineCurve
{
	//enh:sco

	int degree = 3;
	//enh:field getter setter

	boolean periodic;
	//enh:field getter setter

	boolean rational;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field degree$FIELD;
	public static final Type.Field periodic$FIELD;
	public static final Type.Field rational$FIELD;

	public static class Type extends VertexListImpl.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (BSplineCurveImpl representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, VertexListImpl.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = VertexListImpl.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = VertexListImpl.Type.FIELD_COUNT + 3;

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
					((BSplineCurveImpl) o).periodic = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((BSplineCurveImpl) o).rational = (boolean) value;
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
					return ((BSplineCurveImpl) o).isPeriodic ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((BSplineCurveImpl) o).isRational ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((BSplineCurveImpl) o).degree = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((BSplineCurveImpl) o).getDegree ();
			}
			return super.getInt (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new BSplineCurveImpl ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (BSplineCurveImpl.class);
		degree$FIELD = Type._addManagedField ($TYPE, "degree", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 0);
		periodic$FIELD = Type._addManagedField ($TYPE, "periodic", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 1);
		rational$FIELD = Type._addManagedField ($TYPE, "rational", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 2);
		$TYPE.validate ();
	}

	public boolean isPeriodic ()
	{
		return periodic;
	}

	public void setPeriodic (boolean value)
	{
		this.periodic = (boolean) value;
	}

	public boolean isRational ()
	{
		return rational;
	}

	public void setRational (boolean value)
	{
		this.rational = (boolean) value;
	}

	public int getDegree ()
	{
		return degree;
	}

	public void setDegree (int value)
	{
		this.degree = (int) value;
	}

//enh:end


	public BSplineCurveImpl ()
	{
		super ();
	}


	public BSplineCurveImpl (float[] data, int dimension, int degree,
							 boolean periodic, boolean rational)
	{
		super (data, dimension);
		this.degree = degree;
		this.periodic = periodic;
		this.rational = rational;
	}


	public static BSplineCurveImpl create
		(float[] controlPoints, int dimension, int degree,
		 boolean clamp, boolean periodic)
	{
		BSplineCurveImpl c
			= new BSplineCurveImpl (null, dimension, degree, periodic, false);
		int k = controlPoints.length;
		int m = k / dimension + (periodic ? 2 : 1) * degree;
		c.data = new float[k + m + 1];
		System.arraycopy (controlPoints, 0, c.data, 0, k);
		BSpline.makeDefaultKnotVector (c.data, k, m - degree, degree, !clamp);
		return c;
	}


	protected int getCount ()
	{
		return (data.length - ((periodic ? 2 * degree : degree) + 1))
			/ (dimension + 1); 
	}


	@Override
	public int getVertex (float[] out, int index, GraphState gs)
	{
		int n = getCount ();
		return super.getVertex (out, (index < n) ? index : index - n, gs);
	}


	@Override
	public int getSize (GraphState gs)
	{
		return getCount () + (periodic ? degree : 0);
	}


	public int getDegree (GraphState gs)
	{
		return degree;
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		return data[getKnotIndex (index)];
	}


	protected int getKnotIndex (int knot)
	{
		return getCount () * dimension + knot;
	}


	@Override
	public boolean isRational (GraphState gs)
	{
		return rational;
	}

}
