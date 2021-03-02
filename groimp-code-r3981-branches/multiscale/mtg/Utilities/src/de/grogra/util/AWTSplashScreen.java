
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
import java.io.*;
import java.net.URL;

public class AWTSplashScreen extends Canvas implements SplashScreen, Runnable
{
	private Rectangle barBounds;
	private Image image;
	private Graphics imageGraphics;
	private Image barLeft;
	private Image barRight;
	private Image buffer;
	private Graphics bufferGraphics;
	private Window splash = null;
	private Frame frame;
	
	private Point textLocation;
	private Color textColor;
	private float progress = 0;
	private int lastProgressPos;
	private String text = "";

	private volatile boolean dispose = false;


	public void init (String title, URL image, Rectangle barBounds,
					  URL barLeft, URL barRight,
					  Point textLocation, Font textFont, Color textColor)
	{
		frame = new Frame ();
		frame.setTitle (title);
		this.image = toImage (image);
		this.barBounds = barBounds;
		this.barLeft = toImage (barLeft);
		this.barRight = toImage (barRight);
		this.textLocation = textLocation;
		this.textColor = textColor;
		setFont (textFont);
		if ((this.image != null)
			&& (this.barLeft != null) && (this.barRight != null))
		{
			imageGraphics = this.image.getGraphics ();
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
//					splash.setVisible (true);
//					splash.toFront ();
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
		return new Dimension (image.getWidth (null), image.getHeight (null));
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
			buffer = createImage (getWidth (), getHeight ());
			bufferGraphics = buffer.getGraphics ();
		}
		if (image != null)
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
			bufferGraphics.drawImage (image, 0, 0, null);
		}
		else
		{
			bufferGraphics.setColor (Color.BLACK);
			FontMetrics fm = bufferGraphics.getFontMetrics ();
			bufferGraphics.drawString
				(frame.getTitle (), (getWidth () - fm.stringWidth (text)) / 2,
				 getHeight () / 2);
		}
		bufferGraphics.setColor (textColor);
		bufferGraphics.drawString (text, textLocation.x, textLocation.y);
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
