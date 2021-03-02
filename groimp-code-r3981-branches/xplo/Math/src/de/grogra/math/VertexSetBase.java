
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
import de.grogra.graph.impl.ContextDependentBase;
import de.grogra.persistence.SCOType;

public abstract class VertexSetBase extends ContextDependentBase implements VertexSet
{
	//enh:sco SCOType

	protected float[] data;
	//enh:field getter setter

	protected int dimension;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field data$FIELD;
	public static final Type.Field dimension$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (VertexSetBase representative, de.grogra.persistence.SCOType supertype)
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
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((VertexSetBase) o).dimension = (int) value;
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
					return ((VertexSetBase) o).getDimension ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((VertexSetBase) o).data = (float[]) value;
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
					return ((VertexSetBase) o).getData ();
			}
			return super.getObject (o, id);
		}
	}

	static
	{
		$TYPE = new Type (VertexSetBase.class);
		data$FIELD = Type._addManagedField ($TYPE, "data", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, Type.SUPER_FIELD_COUNT + 0);
		dimension$FIELD = Type._addManagedField ($TYPE, "dimension", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

	public int getDimension ()
	{
		return dimension;
	}

	public void setDimension (int value)
	{
		this.dimension = (int) value;
	}

	public float[] getData ()
	{
		return data;
	}

	public void setData (float[] value)
	{
		data$FIELD.setObject (this, value);
	}

//enh:end


	protected VertexSetBase ()
	{
		super ();
	}


	public boolean dependsOnContext ()
	{
		return false;
	}


	public int getVertex (float[] out, int index, GraphState gs)
	{
		index = getVertexIndex (index);
		int d = dimension;
		if (d > out.length)
		{
			d = out.length;
		}
		for (int i = 0; i < d; i++)
		{
			out[i] = data[index++];
		}
		return d;
	}


	protected int getVertexIndex (int index)
	{
		return index * dimension;
	}


	public int getDimension (GraphState gs)
	{
		return dimension;
	}


	public boolean isRational (GraphState gs)
	{
		return false;
	}


/*
	public void setVertex (int index, Tuple3d vertex)
	{
		if (index >= length ())
		{
			return;
		}
		data[index * dimen] = (float) vertex.x;
		data[index * perVertexSize + 1] = (float) vertex.y;
		data[index * perVertexSize + 2] = (float) vertex.z;
	}


	public void setVertex (int index, float x, float y, float z)
	{
		if (index >= length ())
		{
			return;
		}
		data[index * perVertexSize] = x;
		data[index * perVertexSize + 1] = y;
		data[index * perVertexSize + 2] = z;
	}


	public void setLength (int length)
	{
		float[] a = new float[length * perVertexSize];
		if (data != null)
		{
			System.arraycopy (data, 0, a, 0, Math.min (data.length, a.length));
		}
		data = a;
	}


	/**
	 * Adds a vertex to the list. If the index is negative the point gets appended to the list.
	 *
	 * @param index index to insert
	 * @param vertex coords of the vertex
	 * /
	public void addVertex (int index, Tuple3d vertex)
	{
		addVertex (index, (float) vertex.x, (float) vertex.y, (float) vertex.z);
	}


	public void addVertex (int index, float x, float y, float z)
	{
		float[] a = new float[data.length + perVertexSize];
		if ((index < 0) || (index > length ()))
		{
			index = length () * perVertexSize;
		}
		else
		{
			index *= perVertexSize;
		}
		if (index > 0)
		{
			System.arraycopy (data, 0, a, 0, index);
		}
		if (index < data.length)
		{
			System.arraycopy (data, index, a, index + perVertexSize,
							  data.length - index);
		}
		a[index] = x;
		a[index + 1] = y;
		a[index + 2] = z;
		data = a;
	}


	/**
	 * Removes a vertex at given index
	 *
	 * /
	public void removeVertex (int index)
	{
		index *= perVertexSize;
		if ((index < 0) || (index >= data.length))
		{
			return;
		}

		float[] a = new float[data.length - perVertexSize];
		if (index > 0)
		{
			System.arraycopy (data, 0, a, 0, index);
		}
		if (index < a.length)
		{
			System.arraycopy (data, index + perVertexSize, a, index,
							  a.length - index);
		}
		data = a;
	}
*/
}
