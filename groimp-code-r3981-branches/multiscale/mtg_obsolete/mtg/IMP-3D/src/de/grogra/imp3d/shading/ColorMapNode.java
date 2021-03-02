
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

package de.grogra.imp3d.shading;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import de.grogra.icon.Icon;
import de.grogra.icon.IconSource;
import de.grogra.math.ChannelData;
import de.grogra.math.ColorMap;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.RenderedIcon;

public abstract class ColorMapNode extends ChannelMapNode
	implements ColorMap, IconSource, RenderedIcon
{
	private static final int DEFAULT_ICON_SIZE = 64;

	private static final Dimension DEFAULT_DIMENSION
		= new Dimension (DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE);

	private final transient BufferedImage iconImage
		= new BufferedImage (DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE, BufferedImage.TYPE_INT_ARGB);

	private transient int imageStamp = -1;

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (ColorMapNode.class);
		$TYPE.validate ();
	}

//enh:end


	public Icon getIcon (Dimension size, int state)
	{
		return this;
	}


	public Dimension getPreferredIconSize (boolean small)
	{
		return DEFAULT_DIMENSION;
	}


	public void paintIcon (Component c, Graphics2D g,
						   int x, int y, int w, int h, int state)
	{
		g.drawImage (getImage (), x, y, w, h, null);
	}


	protected ChannelData getInputData (ChannelData sink)
	{
		return sink.getData
			(sink.getProperty ("ignoreInput") == Boolean.TRUE ? null : input);
	}


	public void drawImage (BufferedImage image, int supersampling, boolean useInput)
	{
		HashMap m = new HashMap ();
		for (int y = 0; y < image.getHeight (); y++)
		{
			renderLine (image, supersampling, y, useInput, m);
		}
	}


	protected abstract void renderLine (BufferedImage image, int supersampling,
										int line, boolean useInput, Map props);


	public IconSource getIconSource ()
	{
		return this;
	}


	public boolean isMutable ()
	{
		return true;
	}


	public void prepareIcon ()	
	{
		getImage ();
	}


	public Image getImage ()
	{
		int s = getStamp ();
		synchronized (iconImage)
		{
			if (s != imageStamp)
			{
				drawImage (iconImage, 2, true);
				imageStamp = s;
			}
		}
		return iconImage;
	}

	
	public float getSizeRatio ()
	{
		return 1;
	}


	public int renderImage (JobManager jm, BufferedImage img, int y)
	{
		HashMap map = new HashMap ();
		long time = System.currentTimeMillis () + 500;
		do
		{
			for (int i = 3; i > 0; i--)
			{
				if (y == img.getHeight ())
				{
					return y;
				}
				renderLine (img, 2, y, true, map);
//				renderLine (img, 2, y, false, map);
				if (++y == img.getHeight ())
				{
					return y;
				}
			}
		} while ((System.currentTimeMillis () < time)
				 && !jm.hasJobQueued (jm.getThreadContext ().getPriority () + 1));
		return y;
	}


	public BufferedImage getRenderedImage ()
	{
		return iconImage;
	}


	public Image getImage (int w, int h)
	{
		return null;
	}


	public java.net.URL getImageSource ()
	{
		return null;
	}


	public Rectangle getIconBounds ()
	{
		return null;
	}

}
