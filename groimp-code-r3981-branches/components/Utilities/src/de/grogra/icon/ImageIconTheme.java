
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

package de.grogra.icon;

import java.awt.*;
import java.net.*;
import de.grogra.util.*;

public class ImageIconTheme extends ResourceConverterBase implements IconTheme
{
	protected Image image;
	protected Image disabledImage;
	protected URL url;
	protected Dimension size;
	protected int x0, y0, dx, dy;
	protected StringMap positions;


	private class Source extends IconSourceBase
	{
		private final int x, y;


		Source (Point position)
		{
			x = x0 + position.x * dx;
			y = y0 + position.y * dy; 
		}


		@Override
		protected Icon getIconImpl (Dimension s, int state)
		{
			return new IconImpl (this, url,
								 (state == Icon.DISABLED) ? disabledImage : image,
								 new Rectangle (x, y, size.width, size.height));
		}
	}


	public ImageIconTheme (String name, URL image,
						   int x0, int y0, int dx, int dy,
						   Dimension size, StringMap positions)
	{
		this (name, image, new javax.swing.ImageIcon (image).getImage (),
			  x0, y0, dx, dy, size, positions);
	}


	public ImageIconTheme (String name, URL url, Image image,
						   int x0, int y0, int dx, int dy, Dimension size,
						   StringMap positions)
	{
		super (name, new StringMap ());
		this.url = url;
		this.image = image;
		this.disabledImage = javax.swing.GrayFilter.createDisabledImage (image);
		this.x0 = x0;
		this.y0 = y0;
		this.dx = dx;
		this.dy = dy;
		this.size = size;
		this.positions = positions;
	}


	public Dimension getSize ()
	{
		return size;
	}


	@Override
	protected Object convertImpl (String key, I18NBundle bundle)
	{
		Object o = positions.get (key);
		return (o != null) ? new Source ((Point) o) : null;
	}


	public IconSource getSource (String key)
	{
		return (IconSource) convert (null, key, null);
	}
}
