
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

public class BSplineOfVertices extends ContextDependentBase implements BSplineCurve
{
	//enh:sco SCOType

	VertexList vertices;
	//enh:field getter setter

	int degree = 3;
	//enh:field getter setter

	boolean periodic;
	//enh:field getter setter

	boolean rational;
	//enh:field getter setter

	boolean bezier;
	//enh:field getter setter

	int size = -1;
	//enh:field getter setter

	float[] knots = null;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field vertices$FIELD;
	public static final Type.Field degree$FIELD;
	public static final Type.Field periodic$FIELD;
	public static final Type.Field rational$FIELD;
	public static final Type.Field bezier$FIELD;
	public static final Type.Field size$FIELD;
	public static final Type.Field knots$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (BSplineOfVertices representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 7;

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
					((BSplineOfVertices) o).periodic = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((BSplineOfVertices) o).rational = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((BSplineOfVertices) o).bezier = (boolean) value;
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
					return ((BSplineOfVertices) o).isPeriodic ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((BSplineOfVertices) o).isRational ();
				case Type.SUPER_FIELD_COUNT + 4:
					return ((BSplineOfVertices) o).isBezier ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((BSplineOfVertices) o).degree = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 5:
					((BSplineOfVertices) o).size = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((BSplineOfVertices) o).getDegree ();
				case Type.SUPER_FIELD_COUNT + 5:
					return ((BSplineOfVertices) o).getSize ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((BSplineOfVertices) o).vertices = (VertexList) value;
					return;
				case Type.SUPER_FIELD_COUNT + 6:
					((BSplineOfVertices) o).knots = (float[]) value;
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
					return ((BSplineOfVertices) o).getVertices ();
				case Type.SUPER_FIELD_COUNT + 6:
					return ((BSplineOfVertices) o).getKnots ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new BSplineOfVertices ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (BSplineOfVertices.class);
		vertices$FIELD = Type._addManagedField ($TYPE, "vertices", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (VertexList.class), null, Type.SUPER_FIELD_COUNT + 0);
		degree$FIELD = Type._addManagedField ($TYPE, "degree", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 1);
		periodic$FIELD = Type._addManagedField ($TYPE, "periodic", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 2);
		rational$FIELD = Type._addManagedField ($TYPE, "rational", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 3);
		bezier$FIELD = Type._addManagedField ($TYPE, "bezier", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 4);
		size$FIELD = Type._addManagedField ($TYPE, "size", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 5);
		knots$FIELD = Type._addManagedField ($TYPE, "knots", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, Type.SUPER_FIELD_COUNT + 6);
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

	public boolean isBezier ()
	{
		return bezier;
	}

	public void setBezier (boolean value)
	{
		this.bezier = (boolean) value;
	}

	public int getDegree ()
	{
		return degree;
	}

	public void setDegree (int value)
	{
		this.degree = (int) value;
	}

	public int getSize ()
	{
		return size;
	}

	public void setSize (int value)
	{
		this.size = (int) value;
	}

	public VertexList getVertices ()
	{
		return vertices;
	}

	public void setVertices (VertexList value)
	{
		vertices$FIELD.setObject (this, value);
	}

	public float[] getKnots ()
	{
		return knots;
	}

	public void setKnots (float[] value)
	{
		knots$FIELD.setObject (this, value);
	}

//enh:end


	public BSplineOfVertices ()
	{
		super ();
	}


	public BSplineOfVertices (VertexList vertices, int degree, boolean periodic,
							  boolean bezier)
	{
		this ();
		this.vertices = vertices;
		this.degree = degree;
		this.periodic = periodic;
		this.bezier = bezier;
	}


	public boolean dependsOnContext ()
	{
		return vertices.dependsOnContext ();
	}


	@Override
	public void writeStamp (Cache.Entry cache, GraphState gs)
	{
		super.writeStamp (cache, gs);
		vertices.writeStamp (cache, gs);
	}


	public int getVertex (float[] out, int index, GraphState gs)
	{
		int n = vertices.getSize (gs);
		return vertices.getVertex (out, (index < n) ? index : index - n, gs);
	}


	public int getSize (GraphState gs)
	{
		int s = (size > 0) ? size : vertices.getSize (gs);
		return (s < 2) ? 0 : periodic ? s + degree : s;
	}


	public int getDegree (GraphState gs)
	{
		return Math.min (degree, getSize (gs) - 1);
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		return (knots != null) ? knots[index]
			: BSpline.getDefaultKnot (getSize (gs), getDegree (gs), periodic, bezier, index);
	}


	public boolean isRational (GraphState gs)
	{
		return rational;
	}


	public int getDimension (GraphState gs)
	{
		return vertices.getDimension (gs);
	}

}
