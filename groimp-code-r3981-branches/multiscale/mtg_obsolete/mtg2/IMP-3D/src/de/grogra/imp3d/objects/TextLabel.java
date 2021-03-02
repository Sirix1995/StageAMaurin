
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

public class TextLabel extends TextLabelBase
{
	protected String caption = "Label";
	//enh:field attr=Attributes.CAPTION getter setter

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, TextLabel.$TYPE, new NType.Field[] {caption$FIELD});
		}

		public static void signature (@In @Out TextLabel l, String c)
		{
		}
	}


	public TextLabel ()
	{
	}


	public TextLabel (String caption)
	{
		this.caption = caption;
	}


//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field caption$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (TextLabel.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((TextLabel) o).caption = (String) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((TextLabel) o).getCaption ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new TextLabel ());
		$TYPE.addManagedField (caption$FIELD = new _Field ("caption", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
		$TYPE.declareFieldAttribute (caption$FIELD, Attributes.CAPTION);
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
		return new TextLabel ();
	}

	public String getCaption ()
	{
		return caption;
	}

	public void setCaption (String value)
	{
		caption$FIELD.setObject (this, value);
	}

//enh:end

}
