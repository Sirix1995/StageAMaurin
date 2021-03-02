
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

public class VertexGridImpl extends VertexSetBase implements VertexGrid
{
	//enh:sco

	protected int uCount;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field uCount$FIELD;

	public static class Type extends VertexSetBase.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (VertexGridImpl representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, VertexSetBase.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = VertexSetBase.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = VertexSetBase.Type.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((VertexGridImpl) o).uCount = (int) value;
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
					return ((VertexGridImpl) o).getUCount ();
			}
			return super.getInt (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new VertexGridImpl ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (VertexGridImpl.class);
		uCount$FIELD = Type._addManagedField ($TYPE, "uCount", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public int getUCount ()
	{
		return uCount;
	}

	public void setUCount (int value)
	{
		this.uCount = (int) value;
	}

//enh:end

	public VertexGridImpl ()
	{
		super ();
	}


	public VertexGridImpl (float[] data, int dimension, int uCount)
	{
		super ();
		this.data = data;
		this.dimension = dimension;
		this.uCount = uCount;
	}


	public int getUSize (GraphState gs)
	{
		return uCount;
	}


	public int getVSize (GraphState gs)
	{
		return data.length / (uCount * dimension);
	}


	public int getVertexIndex (int u, int v, GraphState gs)
	{
		return v * uCount + u;
	}
}
