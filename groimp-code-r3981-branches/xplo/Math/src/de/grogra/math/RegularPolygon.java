
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

public class RegularPolygon extends ContextDependentBase implements BSplineCurve
{
	//enh:sco SCOType

	float circumradius = 1;
	//enh:field quantity=LENGTH getter setter

	int sideCount = 6;
	//enh:field getter setter

	boolean topHorizontal;
	//enh:field getter setter
	
	float starness = 0;
	//enh:field getter setter

	int plane;
	//enh:field type=BSpline.SPLINE_PLANE_TYPE getter setter	

	boolean reverse;
	//enh:field getter setter
	
	/**
	 * Constructor with side count as parameter.
	 * 
	 * @param side count
	 */
	public RegularPolygon(int value) {
		sideCount = value;	
	}

	/**
	 * Constructor with fixed side count
	 * 
	 */
	public RegularPolygon() {
		sideCount = 6;	
	}

	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field circumradius$FIELD;
	public static final Type.Field sideCount$FIELD;
	public static final Type.Field topHorizontal$FIELD;
	public static final Type.Field starness$FIELD;
	public static final Type.Field plane$FIELD;
	public static final Type.Field reverse$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (RegularPolygon representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 6;

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
					((RegularPolygon) o).topHorizontal = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 5:
					((RegularPolygon) o).reverse = (boolean) value;
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
					return ((RegularPolygon) o).isTopHorizontal ();
				case Type.SUPER_FIELD_COUNT + 5:
					return ((RegularPolygon) o).isReverse ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((RegularPolygon) o).sideCount = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((RegularPolygon) o).plane = (int) value;
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
					return ((RegularPolygon) o).getSideCount ();
				case Type.SUPER_FIELD_COUNT + 4:
					return ((RegularPolygon) o).getPlane ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((RegularPolygon) o).circumradius = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((RegularPolygon) o).starness = (float) value;
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
					return ((RegularPolygon) o).getCircumradius ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((RegularPolygon) o).getStarness ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new RegularPolygon ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (RegularPolygon.class);
		circumradius$FIELD = Type._addManagedField ($TYPE, "circumradius", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		sideCount$FIELD = Type._addManagedField ($TYPE, "sideCount", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 1);
		topHorizontal$FIELD = Type._addManagedField ($TYPE, "topHorizontal", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 2);
		starness$FIELD = Type._addManagedField ($TYPE, "starness", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 3);
		plane$FIELD = Type._addManagedField ($TYPE, "plane", 0 | Type.Field.SCO, BSpline.SPLINE_PLANE_TYPE, null, Type.SUPER_FIELD_COUNT + 4);
		reverse$FIELD = Type._addManagedField ($TYPE, "reverse", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 5);
		circumradius$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		$TYPE.validate ();
	}

	public boolean isTopHorizontal ()
	{
		return topHorizontal;
	}

	public void setTopHorizontal (boolean value)
	{
		this.topHorizontal = (boolean) value;
	}

	public boolean isReverse ()
	{
		return reverse;
	}

	public void setReverse (boolean value)
	{
		this.reverse = (boolean) value;
	}

	public int getSideCount ()
	{
		return sideCount;
	}

	public void setSideCount (int value)
	{
		this.sideCount = (int) value;
	}

	public int getPlane ()
	{
		return plane;
	}

	public void setPlane (int value)
	{
		this.plane = (int) value;
	}

	public float getCircumradius ()
	{
		return circumradius;
	}

	public void setCircumradius (float value)
	{
		this.circumradius = (float) value;
	}

	public float getStarness ()
	{
		return starness;
	}

	public void setStarness (float value)
	{
		this.starness = (float) value;
	}

//enh:end


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
		return (plane == 0) ? 2 : 3;
	}


	public int getDegree (GraphState gs)
	{
		return 1;
	}


	public int getSize (GraphState gs)
	{
		return sideCount + 1; 
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		return (float) (index - 1) / sideCount;
	}


	public int getVertex (float[] out, int index, GraphState gs)
	{
		index -= (sideCount + 2) >> 2;
		float x, y;
		x = ((float) Math.PI / sideCount)
			* (topHorizontal ? (index << 1) + 1 : index << 1);
		float r = ((index & 1) == 0) ? circumradius : circumradius * (1 + starness);
		y = r * (float) Math.cos (x);
		x = -r * (float) Math.sin (x);
		if (reverse)
		{
			x = -x;
		}
		switch (plane)
		{
			case 1:
				return BSpline.set (out, x, 0, y);
			case 2:
				return BSpline.set (out, 0, x, y);
			default:
				return BSpline.set (out, x, y);
		}
	}

}
