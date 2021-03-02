
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

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;

public class FixedImageAdapter extends ImageAdapter
{
	private BufferedImage image;
	private BufferedImage nativeImage;

	protected FixedImageAdapter ()
	{
	}

	public FixedImageAdapter (BufferedImage image)
	{
		this.image = image;
		ColorSpace cs = image.getColorModel ().getColorSpace ();
		if ((cs == ColorSpace.getInstance (ColorSpace.CS_LINEAR_RGB))
				|| (cs == ColorSpace.getInstance (ColorSpace.CS_sRGB))
				|| (cs == ColorSpace.getInstance (ColorSpace.CS_GRAY)))
		{
			nativeImage = image;
		}
		else
		{
			nativeImage = new BufferedImage (image.getWidth (), image
					.getHeight (), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = nativeImage.createGraphics ();
			g.drawImage (image, 0, 0, null);
			g.dispose ();
		}
	}

	@Override
	public BufferedImage getBufferedImage ()
	{
		return image;
	}

	@Override
	public BufferedImage getNativeImage ()
	{
		return nativeImage;
	}

	public boolean isMutable ()
	{
		return false;
	}

	// enh:sco
	// enh:insert $TYPE.setSerializable(false);
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends ImageAdapter.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (FixedImageAdapter representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, ImageAdapter.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new FixedImageAdapter ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (FixedImageAdapter.class);
		$TYPE.setSerializable(false);
		$TYPE.validate ();
	}

//enh:end

}
