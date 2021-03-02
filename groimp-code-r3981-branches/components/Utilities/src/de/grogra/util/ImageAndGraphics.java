
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

package de.grogra.util;

import java.awt.*;

public final class ImageAndGraphics implements Disposable
{
	private Image image = null;
	private Graphics graphics = null;


	public ImageAndGraphics (Image image, Graphics graphics)
	{
		this.image = image;
		this.graphics = graphics;
	}


	public void setImage (Image image)
	{
		dispose ();
		this.image = image;
		this.graphics = image.getGraphics ();
	}


	public Image getImage ()
	{
		return image;
	}


	public Graphics getGraphics ()
	{
		return graphics;
	}


	public void dispose ()
	{
		if (image != null)
		{
			graphics.dispose ();
			image.flush ();
		}
		graphics = null;
		image = null;
	}
}
