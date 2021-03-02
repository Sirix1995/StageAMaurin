
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
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import de.grogra.icon.Icon;
import de.grogra.icon.IconSource;
import de.grogra.persistence.*;

abstract public class ImageAdapter extends ShareableBase implements IconSource,
		Icon
{
	private HashMap scaledImages = new HashMap ();

	protected ImageAdapter ()
	{
	}

	abstract public BufferedImage getBufferedImage ();

	abstract public BufferedImage getNativeImage ();

	private transient int scaledStamp = -1;


	public Image getImage ()
	{
		return getNativeImage ();
	}

	public Icon getIcon (Dimension size, int state)
	{
		return this;
	}

	public Dimension getPreferredIconSize (boolean small)
	{
		BufferedImage img = getNativeImage ();
		if (img == null)
		{
			return new Dimension (16, 16);
		}
		int w = img.getWidth (), h = img.getHeight ();
		float f = w * h / (small ? 2000f : 8000f);
		if (f > 1)
		{
			f = 1 / (float) Math.sqrt (f);
			w *= f;
			h *= f;
		}
		return new Dimension (w, h);
	}

	public void paintIcon (Component c, Graphics2D g, int x, int y, int w,
			int h, int state)
	{
		Image img = getImage (w, h);
		if (img == null)
		{
			g.setColor (Color.DARK_GRAY);
			g.fillRect (x, y, w, h);
		}
		else
		{
			g.drawImage (img, x, y, w, h, null);
		}
	}

	public Image getImage (int w, int h)
	{
		BufferedImage img = getNativeImage ();
		if ((img == null)
				|| ((img.getWidth () == w) && (img.getHeight () == h)))
		{
			return img;
		}
		Dimension d = new Dimension (w, h);
		Image i;
		int s = getStamp ();
		synchronized (scaledImages)
		{
			if (scaledStamp != s)
			{
				scaledImages.clear ();
				scaledStamp = s;
			}
			i = (Image) scaledImages.get (d);
			if (i == null)
			{
				i = img.getScaledInstance (w, h, Image.SCALE_FAST);
				scaledImages.put (d, i);
			}
		}
		return i;
	}

	public void prepareIcon ()
	{
	}

	public IconSource getIconSource ()
	{
		return this;
	}

	public URL getImageSource ()
	{
		return null;
	}

	public Rectangle getIconBounds ()
	{
		return null;
	}

	// enh:sco SCOType
	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ImageAdapter representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}
	}

	static
	{
		$TYPE = new Type (ImageAdapter.class);
		$TYPE.validate ();
	}

//enh:end

}
