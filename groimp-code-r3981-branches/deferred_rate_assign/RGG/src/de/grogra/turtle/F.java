
// NOTE: This file was generated automatically.

// ********************************************
// *               DO NOT EDIT!               *
// ********************************************


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

package de.grogra.turtle;

import de.grogra.graph.*;





/**
 * The turtle command
 * <code>F(x)</code>
 * represents a cylinder along the local z-direction.
 * In addition, this command translates the local coordinate system
 * along the axis of the cylinder such that the origin of the
 * children's coordinate system coincides with the center of the cylinder's top.
 * <br>
 * The diameter of the cylinder
 * is {@link #diameter}, if this value
 * is non-negative, otherwise it
 * is taken from the field
 * <code>localDiameter</code> of the current {@link de.grogra.turtle;TurtleState}.
 * The shader of the cylinder
 * is defined by the {@link #color}, if this value
 * is non-negative, otherwise it
 * is taken from the the current {@link de.grogra.turtle;TurtleState}.
 * The length of the axis is
 * defined by {@link #length}.
 * <br>
 * This corresponds to the turtle command <code>F(x)</code>
 * of the GROGRA software.
 *
 * @author Ole Kniemeyer
 */
public class F extends
	Shoot

{


	public float length;
	//enh:field attr=Attributes.LENGTH

	public float diameter = -1;
	//enh:field
	public int color = -1;
	//enh:field


	private static void initType ()
	{
		$TYPE.addDependency (Attributes.LENGTH, Attributes.TURTLE_MODIFIER);
	}

	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field length$FIELD;
	public static final NType.Field diameter$FIELD;
	public static final NType.Field color$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (F.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 2:
					((F) o).color = (int) value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 2:
					return ((F) o).color;
			}
			return super.getInt (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((F) o).length = (float) value;
					return;
				case 1:
					((F) o).diameter = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 0:
					return ((F) o).length;
				case 1:
					return ((F) o).diameter;
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new F ());
		$TYPE.addManagedField (length$FIELD = new _Field ("length", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (diameter$FIELD = new _Field ("diameter", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (color$FIELD = new _Field ("color", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 2));
		$TYPE.declareFieldAttribute (length$FIELD, Attributes.LENGTH);
		initType ();
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new F ();
	}

//enh:end


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (F.$TYPE,
				   length$FIELD
			);
		}

		public static void signature (@In @Out F n, float a)
		{
		}
	}


	public F ()
	{
		this (1);
	}


	public F (float argument)
	{
		super ();
		length = argument;
	}




	public F (float length, float diameter, int color)
	{
		this (length);
		this.diameter = diameter;
		this.color = color;
	}


	public F (float length, float diameter)
	{
		this (length);
		this.diameter = diameter;
	}


	@Override
	protected float getFloat (FloatAttribute a, GraphState gs)
	{
		return ((a == Attributes.RADIUS) && (diameter >= 0))
			? 0.5f * diameter : super.getFloat (a, gs);
	}


	@Override
	protected int getInt (IntAttribute a, GraphState gs)
	{
		return ((a == Attributes.DTG_COLOR) && (color >= 0))
			? color : super.getInt (a, gs);
	}


	@Override
	public float getLength (Object node, GraphState gs)
	{
		if (node == this)
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				return length;
			}
			else
			{
				return (float) gs.checkDouble (this, true, Attributes.LENGTH, length);
			}
		}
		else
		{
			return (float) gs.getDouble (node, true, Attributes.LENGTH);
		}
	}



}
