
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

import java.io.*;

public interface ResourceConverter
{
	ResourceConverter CAT = new ResourceConverterBase ("cat", new StringMap ())
	{
		@Override
		protected Object convertImpl (String key, I18NBundle bundle)
		{
			ClassLoader c = bundle.getClassLoader ();
			if (c == null)
			{
				return key;
			}
			try
			{
				InputStream in = c.getResourceAsStream (key);
				if (in == null)
				{
					System.err.println ("Resource " + key + " is missing in " + c);
					throw new java.util.MissingResourceException (key, key, key);
				}
				StringBuffer out = new StringBuffer ();
				Utils.read (new InputStreamReader (in, "UTF-8"), out);
				return out.toString ();
			}
			catch (IOException e)
			{
				return key;
			}
		}
	};


	ResourceConverter LINK = new ResourceConverterBase ("link", new StringMap ())
	{
		@Override
		protected Object convertImpl (String key, I18NBundle bundle)
		{
			return bundle.getObject (key, null);
		}
	};


	boolean canHandleConversion (String name);

	Object convert (String name, String argument, I18NBundle bundle);

/*
	class Map implements ResourceConverter
	{
		protected final String prefix;
		protected final StringMap map;
		protected final ResourceBundle bundle;


		public Map (String prefix, StringMap map)
		{
			this.prefix = prefix;
			this.map = map;
			this.bundle = null;
		}


		public Map (String prefix, ResourceBundle bundle)
		{
			this.prefix = prefix;
			this.map = null;
			this.bundle = bundle;
		}


		public Map (String prefix, String bundle)
		{
			this (prefix, ResourceBundle.getBundle (bundle));
		}


		public String getPrefix ()
		{
			return prefix;
		}


		public Object convert (String resource)
		{
			if ((resource == null) || !resource.startsWith (prefix))
			{
				return resource;
			}
			resource = resource.substring (prefix.length ());
			return I18NBundle.convertResource
				((map != null) ? map.get (resource)
				 : bundle.getObject (resource));
		}

	}
*/
}
