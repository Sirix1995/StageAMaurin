
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

public abstract class Move extends TurtleStep implements TurtleModifier
{
	public float argument;
	//enh:field attr=Attributes.ARGUMENT

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TURTLE_MODIFIER);
		$TYPE.addDependency (TurtleStateAttribute.ATTRIBUTE, Attributes.LENGTH);
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
			super (Move.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Move) o).argument = (float) value;
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
					return ((Move) o).argument;
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (Move.class);
		$TYPE.addManagedField (argument$FIELD = new _Field ("argument", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.declareFieldAttribute (argument$FIELD, Attributes.ARGUMENT);
		initType ();
		$TYPE.validate ();
	}

//enh:end


	public Move (float argument)
	{
		super ();
		this.argument = argument;
	}


	float getArgument (Object node, GraphState gs)
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

}
