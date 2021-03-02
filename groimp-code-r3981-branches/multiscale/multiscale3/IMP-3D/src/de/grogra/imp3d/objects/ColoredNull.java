
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

import javax.vecmath.*;
import de.grogra.math.*;

public abstract class ColoredNull extends Null
{
	protected Color3f color = RGBColor.BLACK;
	//enh:field type=Tuple3fType.COLOR attr=Attributes.COLOR getter setter


	public void setColor (int rgb)
	{
		Tuple3fType.setColor (color = new Color3f (), rgb);
	}


	public void setColor (float r, float g, float b)
	{
		color = new Color3f (r, g, b);
	}


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field color$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (ColoredNull.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((ColoredNull) o).color = (Color3f) Tuple3fType.COLOR.setObject (((ColoredNull) o).color, value);
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
					return ((ColoredNull) o).getColor ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (ColoredNull.class);
		$TYPE.addManagedField (color$FIELD = new _Field ("color", _Field.PROTECTED  | _Field.SCO, Tuple3fType.COLOR, null, 0));
		$TYPE.declareFieldAttribute (color$FIELD, Attributes.COLOR);
		$TYPE.validate ();
	}

	public Color3f getColor ()
	{
		return color;
	}

	public void setColor (Color3f value)
	{
		color$FIELD.setObject (this, value);
	}

//enh:end

}
