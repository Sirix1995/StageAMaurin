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

import java.text.DecimalFormat;

import de.grogra.graph.GraphState;
import de.grogra.graph.ObjectAttribute;

public class NumericLabel extends TextLabelBase
{
	protected float value = 0;
	//enh:field attr=Attributes.VALUE getter setter

	protected String format = null;
	//enh:field getter setmethod=setFormat


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, NumericLabel.$TYPE, new NType.Field[] {value$FIELD});
		}

		public static void signature (@In @Out NumericLabel l, float v)
		{
		}
	}

	private transient DecimalFormat formatter;

	public NumericLabel ()
	{
		super ();
	}

	public NumericLabel (float value)
	{
		super ();
		this.value = value;
	}

	public void setFormat (String format)
	{
		this.format = format;
		formatter = null;
	}

	@Override
	protected String getCaption ()
	{
		if (format == null)
		{
			return Float.toString (value);
		}
		else
		{
			if (formatter == null)
			{
				formatter = new DecimalFormat (format);
			}
			return formatter.format (value);
		}
	}

	@Override
	protected Object getObject (ObjectAttribute a, Object placeIn, GraphState gs)
	{
		if (a == Attributes.CAPTION)
		{
			return getCaption ();
		}
		else
		{
			return super.getObject (a, placeIn, gs);
		}
	}

	//enh:insert $TYPE.addAccessor (new AccessorBridge (Attributes.CAPTION));
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field value$FIELD;
	public static final NType.Field format$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (NumericLabel.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((NumericLabel) o).value = (float) value;
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
					return ((NumericLabel) o).getValue ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 1:
					((NumericLabel) o).setFormat ((String) value);
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 1:
					return ((NumericLabel) o).getFormat ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new NumericLabel ());
		$TYPE.addManagedField (value$FIELD = new _Field ("value", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (format$FIELD = new _Field ("format", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 1));
		$TYPE.declareFieldAttribute (value$FIELD, Attributes.VALUE);
		$TYPE.addAccessor (new AccessorBridge (Attributes.CAPTION));
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
		return new NumericLabel ();
	}

	public float getValue ()
	{
		return value;
	}

	public void setValue (float value)
	{
		this.value = (float) value;
	}

	public String getFormat ()
	{
		return format;
	}

//enh:end

}
