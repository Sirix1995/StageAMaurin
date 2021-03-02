
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
import javax.swing.ImageIcon;
import java.net.URL;

public class IconImpl implements Icon
{
	private final IconSource source;
	private final URL url;
	private final Image image;
	private final Rectangle bounds;
	private final boolean boundsCoverAll;

	private Icon icon;
	private int tolerance = 0;


	public IconImpl (IconSource source, URL url, Image image,
					 Rectangle bounds)
	{
		this.source = source;
		this.url = url;
		this.image = image;
		this.bounds = bounds;
		this.boundsCoverAll = (bounds.x == 0) && (bounds.y == 0)
			&& (bounds.width == image.getWidth (null))
			&& (bounds.height == image.getHeight (null));
	}


	public IconImpl (IconSource source, Icon icon)
	{
		this (source, icon.getImageSource (), icon.getImage(),
			  icon.getIconBounds ());
		this.icon = icon;
	}


	public IconImpl (IconSource source, URL url)
	{
		this.source = source;
		this.url = url;
		ImageIcon i = new ImageIcon (url);
		if (i.getImageLoadStatus () == MediaTracker.COMPLETE)
		{
			this.image = i.getImage ();
			this.bounds = new Rectangle
				(0, 0, i.getIconWidth (), i.getIconHeight ());			
		}
		else
		{
			this.image = null;
			this.bounds = null;
		}
		boundsCoverAll = true;
	}


	public void paintIcon (Component c, Graphics2D g,
						   int x, int y, int w, int h, int state)
	{
		if (icon != null)
		{
			icon.paintIcon (c, g, x, y, w, h, state);
			return;
		}
		if (image == null)
		{
			return;
		}
		int dx = w - bounds.width, dy = h - bounds.height;
		if ((Math.abs (dx) <= tolerance) && (Math.abs (dy) <= tolerance))
		{
			x += dx >> 1;
			y += dy >> 1;
			w = bounds.width;
			h = bounds.height;
		}
		if (boundsCoverAll)
		{
			if ((w == bounds.width) && (h == bounds.height))
			{
				g.drawImage (image, x, y, null);
			}
			else
			{
				g.drawImage (image, x, y, w, h, null);
			}
		}
		else
		{
			g.drawImage (image, x, y, x + w, y + h, bounds.x, bounds.y,
						 bounds.x + bounds.width, bounds.y + bounds.height,
						 null);
		}
	}


	public IconSource getIconSource ()
	{
		return source;
	}


	public void prepareIcon ()	
	{
	}


	public boolean isMutable ()
	{
		return url == null;
	}


	public Image getImage ()
	{
		return image;
	}


	public Image getImage (int w, int h)
	{
		return null;
	}


	public URL getImageSource ()
	{
		return url;
	}


	public Rectangle getIconBounds ()
	{
		return bounds;
	}


	public void setTolerance (int tolerance)
	{
		this.tolerance = tolerance;
	}

}
