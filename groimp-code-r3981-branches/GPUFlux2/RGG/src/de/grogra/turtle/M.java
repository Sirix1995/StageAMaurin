
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
 * <code>M(x)</code>
 * represents a movement along the local z-direction. The length of the movement is
 * defined by {@link #length}.
 * <br>
 * This corresponds to the turtle command <code>f(x)</code>
 * of the GROGRA software.
 *
 * @author Ole Kniemeyer
 */
public class M extends
	TurtleStep
	
	implements TurtleModifier
{


	public float length;
	//enh:field attr=Attributes.LENGTH



	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TURTLE_MODIFIER);
		$TYPE.addDependency (Attributes.LENGTH, Attributes.TURTLE_MODIFIER);
	}

	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field length$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (M.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((M) o).length = (float) value;
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
					return ((M) o).length;
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new M ());
		$TYPE.addManagedField (length$FIELD = new _Field ("length", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
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
		return new M ();
	}

//enh:end


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (M.$TYPE,
				   length$FIELD
			);
		}

		public static void signature (@In @Out M n, float a)
		{
		}
	}


	public M ()
	{
		this (1);
	}


	public M (float argument)
	{
		super ();
		length = argument;
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


	public void execute (Object node, TurtleState state, GraphState gs)
	{
		state.relPosition -= getLength (node, gs) / state.length;
	}

}
