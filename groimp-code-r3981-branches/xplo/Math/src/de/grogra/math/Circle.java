
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

public class Circle extends Circular
{
	//enh:sco

	float radius = 1;
	//enh:field quantity=LENGTH getter setter

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field radius$FIELD;

	public static class Type extends Circular.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Circle representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Circular.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Circular.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Circular.Type.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((Circle) o).radius = (float) value;
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
					return ((Circle) o).getRadius ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new Circle ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (Circle.class);
		radius$FIELD = Type._addManagedField ($TYPE, "radius", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		radius$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		$TYPE.validate ();
	}

	public float getRadius ()
	{
		return radius;
	}

	public void setRadius (float value)
	{
		this.radius = (float) value;
	}

//enh:end

	public Circle ()
	{
	}


	public Circle (float radius)
	{
		this.radius = radius;
	}


	public int getDimension (GraphState gs)
	{
		return (plane == 0) ? 3 : 4;
	}


	@Override
	protected int getArcCount ()
	{
		return (int) (4 * Math.max (intermediateArcs, 1));
	}


	@Override
	protected float[] calculateCache (GraphState gs)
	{
		return calculateCache (getArcCount (), 0, 0, 2 * (float) Math.PI,
							   radius, radius, radius, radius, gs);
	}

}
