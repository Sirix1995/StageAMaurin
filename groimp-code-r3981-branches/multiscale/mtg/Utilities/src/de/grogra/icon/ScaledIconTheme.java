
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

import java.io.*;
import java.awt.*;
import java.net.*;
import de.grogra.util.*;

public class ScaledIconTheme extends ResourceConverterBase implements IconTheme
{
	protected IconTheme[] themes;
	protected Dimension[] sizes;


	private class Source extends IconSourceBase
	{
		private final String key;


		Source (String key)
		{
			this.key = key;
		}


		@Override
		protected Icon getIconImpl (Dimension size, int state)
		{
			int optIndex = 0;
			float opt = Float.MAX_VALUE;
			for (int i = 0; i < sizes.length; i++)
			{
				Dimension d = sizes[i];
				if (Math.abs ((float) (d.height * size.width)
							  / (d.width * size.height) - 1) < 0.2f)
				{
					float t = Math.abs ((float) size.width / d.width - 1);
					if (t < opt)
					{
						opt = t;
						optIndex = i;
					}
				}
			}
			if (sizes[optIndex].equals (size))
			{
				IconSource s = themes[optIndex].getSource (key);
				Icon i = (s != null) ? s.getIcon (size, state) : null;
				return (i != null) ? new IconImpl (this, i) : null;
			}
			else
			{
				return getIcon (sizes[optIndex], state);
			}
		}

	}


	public ScaledIconTheme (String name, IconTheme[] themes,
							Dimension[] dimensions)
	{
		super (name, new StringMap ());
		this.themes = themes;
		this.sizes = dimensions;
	}


	public ScaledIconTheme (String name, IconTheme[] themes)
	{
		this (name, themes, getSizes (themes));
	}


	private static Dimension[] getSizes (IconTheme[] themes)
	{
		Dimension[] a = new Dimension[themes.length];
		for (int i = 0; i < a.length; i++)
		{
			a[i] = themes[i].getSize ();	
		}
		return a;
	}


	@Override
	protected Object convertImpl (String key, I18NBundle bundle)
	{
		for (int i = 0; i < themes.length; i++)
		{
			if (themes[i].getSource (key) != null)
			{
				return new Source (key);
			}
		}
		return null;
	}


	public IconSource getSource (String key)
	{
		return (IconSource) convert (null, key, null);
	}


	public Dimension getSize ()
	{
		return null;
	}


	public static ScaledIconTheme readFromDirectory
		(String name, URL root, String suffix)
	{
		if (root == null)
		{
			return create (name, new URL[0], new Dimension[0], suffix);
		}
		return readFromDirectory (name, Utils.urlToFile (root), suffix);
	}


	public static ScaledIconTheme readFromDirectory
		(String name, File root, String suffix)
	{
		URL[] roots = null;
		Dimension[] dimensions = null;
		File[] dirs = root.listFiles ();
		for (int r = 0; r < 2; r++)
		{
			int n = 0;
			for (int j = 0; j < dirs.length; j++)
			{
				int p = dirs[j].getName ().indexOf ('x');
				if (p >= 0)
				{
					try
					{
						Dimension d
							= Utils.parseDimension (dirs[j].getName ());
						try
						{
							URL u = dirs[j].toURI ().toURL ();
							if (r == 1)
							{
								roots[n] = u;
								dimensions[n] = d;
							}
							n++;
						}
						catch (MalformedURLException e)
						{
						}
					}
					catch (IllegalArgumentException e)
					{
					}
				}
			}
			if (r == 0)
			{
				roots = new URL[n];
				dimensions = new Dimension[n];
			}
		}
		return create (name, roots, dimensions, suffix);
	}


	public static ScaledIconTheme create
		(String name, URL[] roots, Dimension[] sizes, String suffix)
	{
		URLIconTheme[] themes = new URLIconTheme[roots.length];
		for (int i = 0; i < themes.length; i++)
		{
			themes[i]
				= new URLIconTheme (name, roots[i], suffix, sizes[i]);
		}
		return new ScaledIconTheme (name, themes, sizes);
	}

}
