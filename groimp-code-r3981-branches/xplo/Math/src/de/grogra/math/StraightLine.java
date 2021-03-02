
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

public class StraightLine extends ContextDependentBase implements BSplineCurve
{
	//enh:sco SCOType

	float length;
	//enh:field quantity=LENGTH getter setter

	int plane;
	//enh:field type=BSpline.SPLINE_PLANE_TYPE getter setter	

	
	public StraightLine (float length)
	{
		this.length = length;
	}
	
	public StraightLine ()
	{
		this (1);
	}

	public boolean dependsOnContext ()
	{
		return false;
	}


	public boolean isRational (GraphState gs)
	{
		return false;
	}


	public int getDimension (GraphState gs)
	{
		return (plane == 0) ? 3 : 2;
	}


	public int getDegree (GraphState gs)
	{
		return 1;
	}


	public int getSize (GraphState gs)
	{
		return 2; 
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		return index;
	}


	public int getVertex (float[] out, int index, GraphState gs)
	{
		float x = (index == 0) ? 0 : length;
		switch (plane)
		{
			case 1:
				return BSpline.set (out, 0, x);
			case 2:
				return BSpline.set (out, x, 0);
			default:
				return BSpline.set (out, 0, 0, x);
		}
	}


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field length$FIELD;
	public static final Type.Field plane$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (StraightLine representative, de.grogra.persistence.SCOType supertype)
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
					((StraightLine) o).plane = (int) value;
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
					return ((StraightLine) o).getPlane ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((StraightLine) o).length = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((StraightLine) o).getLength ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new StraightLine ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (StraightLine.class);
		length$FIELD = Type._addManagedField ($TYPE, "length", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		plane$FIELD = Type._addManagedField ($TYPE, "plane", 0 | Type.Field.SCO, BSpline.SPLINE_PLANE_TYPE, null, Type.SUPER_FIELD_COUNT + 1);
		length$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		$TYPE.validate ();
	}

	public int getPlane ()
	{
		return plane;
	}

	public void setPlane (int value)
	{
		this.plane = (int) value;
	}

	public float getLength ()
	{
		return length;
	}

	public void setLength (float value)
	{
		this.length = (float) value;
	}

//enh:end

}
