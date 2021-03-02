
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

package de.grogra.imp3d.objects;

import javax.vecmath.Point3f;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.ContextDependentBase;
import de.grogra.math.Pool;
import de.grogra.math.VertexGrid;
import de.grogra.persistence.SCOType;

public abstract class HeightField extends ContextDependentBase
	implements VertexGrid
{
	//enh:sco SCOType

	HeightFieldMapping mapping = new RectangularHeightFieldMapping ();
	//enh:field getter setter
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field mapping$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (HeightField representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((HeightField) o).mapping = (HeightFieldMapping) value;
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
					return ((HeightField) o).getMapping ();
			}
			return super.getObject (o, id);
		}
	}

	static
	{
		$TYPE = new Type (HeightField.class);
		mapping$FIELD = Type._addManagedField ($TYPE, "mapping", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (HeightFieldMapping.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public HeightFieldMapping getMapping ()
	{
		return mapping;
	}

	public void setMapping (HeightFieldMapping value)
	{
		mapping$FIELD.setObject (this, value);
	}

//enh:end


	public HeightField ()
	{
		super ();
	}


	public boolean dependsOnContext ()
	{
		return false;
	}


	public abstract float getHeight (int x, int y, GraphState gs);


	public int getVertex (float[] out, int index, GraphState gs)
	{
		int sx = getUSize (gs);
		int y = index / sx, x = index - y * sx;
		Pool pool = Pool.push (gs);
		Point3f p = pool.p3f0;
		mapping.map (x, y, sx, getVSize (gs), getHeight (x, y, gs), p);
		pool.pop (gs);
		switch (out.length)
		{
			case 1:
				out[0] = p.x;
				return 1;
			case 2:
				out[0] = p.x;
				out[1] = p.y;
				return 2;
			default:
				out[0] = p.x;
				out[1] = p.y;
				out[2] = p.z;
				return 3;
		}
	}


	public int getDimension (GraphState gs)
	{
		return 3;
	}


	public boolean isRational (GraphState gs)
	{
		return false;
	}


	public int getVertexIndex (int u, int v, GraphState gs)
	{
		return v * getUSize (gs) + u;
	}

}
