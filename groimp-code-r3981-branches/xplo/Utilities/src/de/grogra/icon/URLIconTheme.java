
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

public class URLIconTheme extends ResourceConverterBase implements IconTheme
{
	protected URL root;
	protected String suffix;
	protected Dimension size;


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
			try
			{
				IconImpl i = new IconImpl (this, new URL (root, key + suffix));
				if (i.getImage () == null)
				{
					return null;
				}
				if (state == Icon.DISABLED)
				{
					i = new IconImpl
						(this, i.getImageSource (), javax.swing.GrayFilter
						 .createDisabledImage (i.getImage ()),
						 i.getIconBounds());
				}
				i.setTolerance (2);
				return i;
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace ();
				return null;
			}
		}
	}


	public URLIconTheme (String name, URL root, String suffix, Dimension size)
	{
		super (name, new StringMap ());
		this.root = root;
		this.suffix = suffix;
		this.size = size;
	}


	public Dimension getSize ()
	{
		return size;
	}


	@Override
	protected Object convertImpl (String key, I18NBundle bundle)
	{
		Source s = new Source (key);
		return (s.getIcon (size, Icon.DEFAULT) != null) ? s : null;
	}


	public IconSource getSource (String key)
	{
		return (IconSource) convert (null, key, null);
	}
}
