
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
 * <code>RVMul(x)</code>
 * represents a rotation which implements gravitropism. Its strength is
 * the <code>localTropism</code>
 * of the current {@link de.grogra.turtle;TurtleState}
 * multiplied by the specified {@link #argument argument}.
 * <br>
 * This corresponds to the turtle command <code>RV*(x)</code>
 * of the GROGRA software.
 *
 * @author Ole Kniemeyer
 */
public class RVMul extends
	GRotation
	
	implements TurtleModifier
{




	public float argument;
	//enh:field attr=Attributes.ARGUMENT

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TURTLE_MODIFIER);
		$TYPE.addDependency (TurtleStateAttribute.ATTRIBUTE, Attributes.TRANSFORMATION);
		$TYPE.addDependency (Attributes.ARGUMENT, Attributes.TURTLE_MODIFIER);
	}

	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field argument$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (RVMul.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((RVMul) o).argument = (float) value;
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
					return ((RVMul) o).argument;
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new RVMul ());
		$TYPE.addManagedField (argument$FIELD = new _Field ("argument", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.declareFieldAttribute (argument$FIELD, Attributes.ARGUMENT);
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
		return new RVMul ();
	}

//enh:end


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (RVMul.$TYPE,
				   argument$FIELD
			);
		}

		public static void signature (@In @Out RVMul n, float a)
		{
		}
	}


	public RVMul ()
	{
		this (1);
	}


	public RVMul (float argument)
	{
		super ();
		this.argument = argument;
	}

	private float getArgument (Object node, GraphState gs)
	{
		if (node == this)
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				return argument;
			}
			else
			{
				return gs.checkFloat (this, true, Attributes.ARGUMENT, argument);
			}
		}
		else
		{
			return gs.getFloat (node, true, Attributes.ARGUMENT);
		}
	}






	@Override
	public float getTropismStrength (Object node, GraphState gs)
	{
		TurtleState state = TurtleState.getBefore (node, gs);
		return state.localTropism * getArgument (node, gs);
	}

	public void execute (Object node, TurtleState state, GraphState gs)
	{
		state.localTropism = state.tropism;
	}

}
