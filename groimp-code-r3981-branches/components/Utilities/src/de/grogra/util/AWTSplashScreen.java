
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

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class AWTSplashScreen extends Canvas implements SplashScreen, Runnable
{
	private Rectangle barBounds;
	private Image backgroundImage;
	private Image foregroundImage;
	private Image logoImage;
	private Graphics imageGraphics;
	private Image barLeft;
	private Image barRight;
	private Image buffer;
	private Graphics bufferGraphics;
	private Window splash = null;
	private Frame frame;
	
	private I18NBundle mainI18n;
	
	private Point textLocation;
	private Color textColor;
	private float progress = 0;
	private int lastProgressPos;
	private String text = "";

	private volatile boolean dispose = false;
	private boolean versionInfo = false;

	public void init (String title, URL background, URL foreground,  URL logo, Rectangle barBounds, URL barLeft, URL barRight, 
			Point textLocation, Font textFont, Color textColor, boolean versionInfo, I18NBundle mainI18n)
	{
		frame = new Frame ();
		frame.setTitle (title);
		this.backgroundImage = toImage (background);
		this.foregroundImage = toImage (foreground);
		this.logoImage = toImage (logo);
		this.barBounds = barBounds;
		this.barLeft = toImage (barLeft);
		this.barRight = toImage (barRight);
		this.textLocation = textLocation;
		this.textColor = textColor;
		this.versionInfo = versionInfo;
		this.mainI18n = mainI18n;
		setFont (textFont);
		if ((this.backgroundImage != null)
			&& (this.barLeft != null) && (this.barRight != null))
		{
			imageGraphics = this.backgroundImage.getGraphics ();
			imageGraphics.drawImage (this.barLeft, barBounds.x, barBounds.y, null);
			lastProgressPos = this.barLeft.getWidth (null) - 1;
		}
		else
		{
			this.barLeft = null;
			this.barRight = null;
		}
	}

	
	private Image toImage (URL url)
	{
		if (url == null)
		{
			return null;
		}
		if ("file".equals (url.getProtocol ())
			&& url.getPath ().endsWith (".ppm"))
		{
			try
			{
				InputStream in = new FileInputStream (Utils.urlToFile (url));
				Image i = new PPMReader (new BufferedInputStream (in), 1000000).read ();
				in.close ();
				return i;
			}
			catch (IOException e)
			{
				return null;
			}
		}
		else
		{
			return new javax.swing.ImageIcon (url).getImage ();
		}
	}


	@Override
	public void show ()
	{
		splash = new Window (frame);
		splash.setBackground (Color.LIGHT_GRAY);
		splash.add (this, BorderLayout.CENTER);
		splash.pack ();
		Rectangle r = frame.getGraphicsConfiguration ().getBounds ();
		splash.setLocation ((r.x + r.width - splash.getWidth ()) / 2,
							(r.y + r.height - splash.getHeight ()) / 2);
		EventQueue.invokeLater (new Runnable ()
			{
				public void run ()
				{
					splash.setVisible (true);
					splash.toFront ();
				}
			});
	}


	@Override
	public Dimension getMinimumSize ()
	{
		return getPreferredSize ();
	}


	@Override
	public Dimension getPreferredSize ()
	{
		return new Dimension (backgroundImage.getWidth (null), backgroundImage.getHeight (null));
	}


	@Override
	public void update (Graphics g)
	{
		paint (g);
	}


	@Override
	public void paint (Graphics g)
	{
		if (buffer == null)
		{
			buffer = new BufferedImage(getWidth (), getHeight (), BufferedImage.TYPE_INT_ARGB);
			bufferGraphics = buffer.getGraphics ();
		}
		if (backgroundImage != null)
		{
			if (barLeft != null)
			{
				int w = barLeft.getWidth (null);
				int pos = Math.round (progress * (barBounds.width - w - barRight.getWidth (null))) + w;
				while (lastProgressPos < pos)
				{
					imageGraphics.drawImage (barRight, barBounds.x + ++lastProgressPos, barBounds.y, null);
				}
			}
			bufferGraphics.drawImage (backgroundImage, 0, 0, null);
			if (foregroundImage != null)
			{
				bufferGraphics.drawImage (foregroundImage, 0, 0, null);
			}
			if (logoImage != null)
			{
				bufferGraphics.drawImage (logoImage, 0, 0, null);
			}
			
			bufferGraphics.setColor (new Color(0.66f,0.66f,0.66f));
			bufferGraphics.drawString (mainI18n.msg ("splash.text1"), textLocation.x-15, textLocation.y-85);
			bufferGraphics.drawString (mainI18n.msg ("splash.text2"), textLocation.x-15, textLocation.y-70);
			bufferGraphics.drawString (mainI18n.msg ("splash.text3"), textLocation.x-15, textLocation.y-55);
			
			bufferGraphics.setColor (textColor);
			bufferGraphics.drawString (text, textLocation.x, textLocation.y);
			// version info
			if(versionInfo) {
				bufferGraphics.setColor (new Color(1f,0.2f,0.2f));
				bufferGraphics.drawString (mainI18n.msg ("new.version.info.message"), textLocation.x, textLocation.y-33);
			}
		}
		else
		{
			bufferGraphics.setColor (Color.BLACK);
			FontMetrics fm = bufferGraphics.getFontMetrics ();
			bufferGraphics.drawString
				(frame.getTitle (), (getWidth () - fm.stringWidth (text)) / 2,
				 getHeight () / 2);
		}
		g.drawImage (buffer, 0, 0, null);
	}


	public void setInitializationProgress (float progress, String text)
	{
		this.text = text;
		this.progress = progress;
		if (splash != null)
		{
			repaint ();
			try
			{
				Thread.sleep (25);
			}
			catch (InterruptedException e)
			{
			}
		}
	}


	public void toFront ()
	{
		EventQueue.invokeLater (this);
	}


	public void close ()
	{
		if (!dispose)
		{
			dispose = true;
			EventQueue.invokeLater (this);
		}
	}


	public void run ()
	{
		if (splash != null)
		{
			if (dispose)
			{
				splash.dispose ();
				splash = null;
				frame.dispose ();
			}
			else
			{
				splash.toFront ();
			}
		}
	}
}
