
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

import java.awt.image.*;
import de.grogra.graph.*;
import de.grogra.imp.objects.ImageAdapter;

public class ImageHeightField extends HeightField
{
	//enh:sco

	ImageAdapter image;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field image$FIELD;

	public static class Type extends HeightField.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ImageHeightField representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, HeightField.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = HeightField.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = HeightField.Type.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((ImageHeightField) o).image = (ImageAdapter) value;
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
					return ((ImageHeightField) o).getImage ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new ImageHeightField ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (ImageHeightField.class);
		image$FIELD = Type._addManagedField ($TYPE, "image", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (ImageAdapter.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public ImageAdapter getImage ()
	{
		return image;
	}

	public void setImage (ImageAdapter value)
	{
		image$FIELD.setObject (this, value);
	}

//enh:end


	public ImageHeightField ()
	{
		super ();
	}


	@Override
	public float getHeight (int x, int y, GraphState gs)
	{
		if (image == null)
		{
			return 0;
		}
		BufferedImage i = image.getBufferedImage ();
		DataBuffer db = i.getRaster ().getDataBuffer ();
		y = i.getHeight () - 1 - y;
		switch (i.getType ())
		{
			case BufferedImage.TYPE_BYTE_GRAY:
				if (db.getDataType () == DataBuffer.TYPE_BYTE)
				{
					return i.getSampleModel ().getSample (x, y, 0, db);
				}
				break;
			case BufferedImage.TYPE_USHORT_GRAY:
				if (db.getDataType () == DataBuffer.TYPE_USHORT)
				{
					return i.getSampleModel ().getSample (x, y, 0, db);
				}
				break;
		}
		return i.getRGB (x, y) & 0xff;
	}


	public int getUSize (GraphState gs)
	{
		return (image == null) ? 1 : image.getBufferedImage ().getWidth ();
	}


	public int getVSize (GraphState gs)
	{
		return (image == null) ? 1 : image.getBufferedImage ().getHeight ();
	}

}
