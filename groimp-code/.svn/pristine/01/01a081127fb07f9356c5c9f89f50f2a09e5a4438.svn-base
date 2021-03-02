
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

public class InvokeMethod extends de.grogra.graph.impl.Node
{
	public float argument;
	//enh:field attr=Attributes.ARGUMENT


	private InvokeMethod ()
	{
		this (0);
	}


	public InvokeMethod (float argument)
	{
		super ();
		this.argument = argument;
	}

	
	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (InvokeMethod.$TYPE, argument$FIELD);
		}

		private static void signature (@In @Out InvokeMethod n, float a)
		{
		}
	}

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field argument$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (InvokeMethod.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((InvokeMethod) o).argument = (float) value;
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
					return ((InvokeMethod) o).argument;
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new InvokeMethod ());
		$TYPE.addManagedField (argument$FIELD = new _Field ("argument", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.declareFieldAttribute (argument$FIELD, Attributes.ARGUMENT);
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
		return new InvokeMethod ();
	}

//enh:end

}
