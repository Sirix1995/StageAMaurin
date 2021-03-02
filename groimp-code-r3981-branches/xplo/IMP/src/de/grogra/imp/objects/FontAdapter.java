
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

package de.grogra.imp.objects;

import java.awt.*;
import de.grogra.persistence.*;
import de.grogra.util.EnumerationType;

public final class FontAdapter extends ShareableBase
{
	private static final EnumerationType FONT_NAME_TYPE;
	private static final Font[] FONTS;
	private static final Font DEFAULT = new Font (null, Font.PLAIN, 12);

	static
	{
		FONTS = GraphicsEnvironment.getLocalGraphicsEnvironment ()
			.getAllFonts ();
		String[] names = new String[FONTS.length];
		for (int i = names.length - 1; i >= 0; i--)
		{
			names[i] = FONTS[i].getFontName ();
		}
		FONT_NAME_TYPE = new EnumerationType
			("fontnames", names, EnumerationType.OBJECT_ENUMERATION,
			 Type.STRING);
	}

	//enh:sco SCOType

	String name;
	//enh:field type=FONT_NAME_TYPE setmethod=setName	

	float size = 12;
	//enh:field setmethod=setSize

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field name$FIELD;
	public static final Type.Field size$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (FontAdapter representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((FontAdapter) o).setSize ((float) value);
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((FontAdapter) o).size;
			}
			return super.getFloat (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((FontAdapter) o).setName ((String) value);
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((FontAdapter) o).name;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new FontAdapter ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (FontAdapter.class);
		name$FIELD = Type._addManagedField ($TYPE, "name", 0 | Type.Field.SCO, FONT_NAME_TYPE, null, Type.SUPER_FIELD_COUNT + 0);
		size$FIELD = Type._addManagedField ($TYPE, "size", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

//enh:end


	private transient Font font;

	public FontAdapter ()
	{
	}


	void setName (String name)
	{
		this.name = name;
		font = null;
	}


	void setSize (float size)
	{
		this.size = size;
		font = null;
	}


	private Font getFont ()
	{
		Font f = font;
		if (f == null)
		{
			String n = name;
			Font base = DEFAULT;
			for (int i = FONTS.length - 1; i >= 0; i--)
			{
				if (FONTS[i].getFontName ().equals (n))
				{
					base = FONTS[i];
					break;
				}
			}
			font = f = base.deriveFont (size);
		}
		return f;
	}


	public static Font getFont (FontAdapter fa)
	{
		return (fa == null) ? DEFAULT : fa.getFont ();
	}

	
	public static FontAdapter getInstance (float size, String name)
	{
		FontAdapter f = new FontAdapter ();
		f.setSize (size);
		f.setName (name);
		return f;
	}
}
