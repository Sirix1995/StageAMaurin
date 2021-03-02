
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

public class BSplineSurfaceImpl extends VertexGridImpl implements BSplineSurface
{
	//enh:sco

	int uDegree = 3;
	//enh:field getter setter

	boolean uPeriodic;
	//enh:field getter setter

	int vDegree = 3;
	//enh:field getter setter

	boolean vPeriodic;
	//enh:field getter setter

	boolean rational;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field uDegree$FIELD;
	public static final Type.Field uPeriodic$FIELD;
	public static final Type.Field vDegree$FIELD;
	public static final Type.Field vPeriodic$FIELD;
	public static final Type.Field rational$FIELD;

	public static class Type extends VertexGridImpl.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (BSplineSurfaceImpl representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, VertexGridImpl.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = VertexGridImpl.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = VertexGridImpl.Type.FIELD_COUNT + 5;

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
					((BSplineSurfaceImpl) o).uPeriodic = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((BSplineSurfaceImpl) o).vPeriodic = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((BSplineSurfaceImpl) o).rational = (boolean) value;
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
					return ((BSplineSurfaceImpl) o).isUPeriodic ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((BSplineSurfaceImpl) o).isVPeriodic ();
				case Type.SUPER_FIELD_COUNT + 4:
					return ((BSplineSurfaceImpl) o).isRational ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((BSplineSurfaceImpl) o).uDegree = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((BSplineSurfaceImpl) o).vDegree = (int) value;
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
					return ((BSplineSurfaceImpl) o).getUDegree ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((BSplineSurfaceImpl) o).getVDegree ();
			}
			return super.getInt (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new BSplineSurfaceImpl ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (BSplineSurfaceImpl.class);
		uDegree$FIELD = Type._addManagedField ($TYPE, "uDegree", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 0);
		uPeriodic$FIELD = Type._addManagedField ($TYPE, "uPeriodic", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 1);
		vDegree$FIELD = Type._addManagedField ($TYPE, "vDegree", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 2);
		vPeriodic$FIELD = Type._addManagedField ($TYPE, "vPeriodic", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 3);
		rational$FIELD = Type._addManagedField ($TYPE, "rational", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 4);
		$TYPE.validate ();
	}

	public boolean isUPeriodic ()
	{
		return uPeriodic;
	}

	public void setUPeriodic (boolean value)
	{
		this.uPeriodic = (boolean) value;
	}

	public boolean isVPeriodic ()
	{
		return vPeriodic;
	}

	public void setVPeriodic (boolean value)
	{
		this.vPeriodic = (boolean) value;
	}

	public boolean isRational ()
	{
		return rational;
	}

	public void setRational (boolean value)
	{
		this.rational = (boolean) value;
	}

	public int getUDegree ()
	{
		return uDegree;
	}

	public void setUDegree (int value)
	{
		this.uDegree = (int) value;
	}

	public int getVDegree ()
	{
		return vDegree;
	}

	public void setVDegree (int value)
	{
		this.vDegree = (int) value;
	}

//enh:end


	public BSplineSurfaceImpl ()
	{
		super ();
	}


	public BSplineSurfaceImpl (float[] data, int dimension, int uCount,
							   int uDegree, boolean uPeriodic,
							   int vDegree, boolean vPeriodic)
	{
		super (data, dimension, uCount);
		this.uDegree = uDegree;
		this.uPeriodic = uPeriodic;
		this.vDegree = vDegree;
		this.vPeriodic = vPeriodic;
	}


	public static BSplineSurfaceImpl create
		(float[] controlPoints, int uCount, int dimension,
		 int uDegree, boolean uClamp, boolean uPeriodic,
		 int vDegree, boolean vClamp, boolean vPeriodic)
	{
		BSplineSurfaceImpl c = new BSplineSurfaceImpl
			(null, dimension, uCount, uDegree, uPeriodic, vDegree, vPeriodic);
		int k = controlPoints.length;
		int mu = uCount + (uPeriodic ? 2 : 1) * uDegree;
		int mv = k / (dimension * uCount) + (vPeriodic ? 2 : 1) * vDegree;
		c.data = new float[k + mu + mv + 2];
		System.arraycopy (controlPoints, 0, c.data, 0, k);
		BSpline.makeDefaultKnotVector (c.data, k, mu - uDegree, uDegree, !uClamp);
		BSpline.makeDefaultKnotVector (c.data, k + mu + 1, mv - vDegree, vDegree, !vClamp);
		return c;
	}


	protected int getVCount ()
	{
		return (data.length - (uCount + (uPeriodic ? 2 * uDegree : uDegree)
							   + (vPeriodic ? 2 * vDegree : vDegree) + 2))
			/ (dimension * uCount + 1); 
	}


	@Override
	public int getUSize (GraphState gs)
	{
		return uCount + (uPeriodic ? uDegree : 0);
	}


	@Override
	public int getVSize (GraphState gs)
	{
		return getVCount () + (vPeriodic ? vDegree : 0);
	}


	@Override
	public int getVertexIndex (int u, int v, GraphState gs)
	{
		if (uPeriodic)
		{
			int n = uCount;
			if (u >= n)
			{
				u -= n;
			}
		}
		if (vPeriodic)
		{
			int n = getVCount ();
			if (v >= n)
			{
				v -= n;
			}
		}
		return v * uCount + u;
	}


	public int getUDegree (GraphState gs)
	{
		return uDegree;
	}


	public int getVDegree (GraphState gs)
	{
		return vDegree;
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		return data[(dim == 0) ? uCount * getVCount () * dimension + index
					: uCount * (getVCount () * dimension + 1)
					  + (uPeriodic ? 2 * uDegree : uDegree) + 1 + index];
	}


	@Override
	public boolean isRational (GraphState gs)
	{
		return rational;
	}

}
