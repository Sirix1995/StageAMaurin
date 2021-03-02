
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

public class Arc extends Circular
{
	//enh:sco

	float startAngle = 0;
	//enh:field quantity=ANGLE getter setter

	float endAngle = (float) Math.PI;
	//enh:field quantity=ANGLE getter setter

	float radius = 1;
	//enh:field quantity=LENGTH getter setter

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field startAngle$FIELD;
	public static final Type.Field endAngle$FIELD;
	public static final Type.Field radius$FIELD;

	public static class Type extends Circular.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Arc representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Circular.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Circular.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Circular.Type.FIELD_COUNT + 3;

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
					((Arc) o).startAngle = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((Arc) o).endAngle = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((Arc) o).radius = (float) value;
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
					return ((Arc) o).getStartAngle ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((Arc) o).getEndAngle ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((Arc) o).getRadius ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new Arc ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (Arc.class);
		startAngle$FIELD = Type._addManagedField ($TYPE, "startAngle", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		endAngle$FIELD = Type._addManagedField ($TYPE, "endAngle", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		radius$FIELD = Type._addManagedField ($TYPE, "radius", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		startAngle$FIELD.setQuantity (de.grogra.util.Quantity.ANGLE);
		endAngle$FIELD.setQuantity (de.grogra.util.Quantity.ANGLE);
		radius$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		$TYPE.validate ();
	}

	public float getStartAngle ()
	{
		return startAngle;
	}

	public void setStartAngle (float value)
	{
		this.startAngle = (float) value;
	}

	public float getEndAngle ()
	{
		return endAngle;
	}

	public void setEndAngle (float value)
	{
		this.endAngle = (float) value;
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


	public Arc ()
	{
	}


	public Arc (float startAngle, float endAngle, float radius)
	{
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.radius = radius;
	}


	public int getDimension (GraphState gs)
	{
		return (plane == 0) ? 3 : 4;
	}


	@Override
	protected int getArcCount ()
	{
		return Math.abs ((int) ((endAngle - startAngle)
								* Math.max (intermediateArcs, 1)
								* (2 / (float) Math.PI))) + 1;
	}


	@Override
	protected float[] calculateCache (GraphState gs)
	{
		return calculateCache (getArcCount (), 0, startAngle, endAngle,
							   radius, radius, radius, radius, gs);
	}

}
