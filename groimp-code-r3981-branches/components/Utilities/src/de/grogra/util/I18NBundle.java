
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

import java.util.*;
import java.text.*;

import de.grogra.xl.util.ObjectList;

public final class I18NBundle extends ResourceBundle
{
/*
	private static final class Cache extends StringMap
	{
		private final ResourceBundle bundle;


		Cache (ResourceBundle bundle)
		{
			super (50);
			this.bundle = bundle;
		}


		Object getResource (String key)
		{
			Object o = get (key);
			if (o == null)
			{
				Object r = bundle.getObject (key);
				o = convertResource (r);
				if (o != r)
				{
					put (key, o);
				}
			}
			return o;
		}
	}
*/

	private static final ObjectList converters = new ObjectList (10, false);

	public static void addResourceConverter (ResourceConverter c)
	{
		synchronized (converters)
		{
			converters.addIfNotContained (c);
		}
	}


	public static void removeResourceConverter (ResourceConverter c)
	{
		synchronized (converters)
		{
			converters.remove (c);
		}
	}


	public static I18NBundle getInstance (Class cls)
	{
		String bundle = cls.getName ();
		int i = bundle.lastIndexOf ('.');
		String base = (i < 0) ? "" : bundle.substring (0, i);
		bundle = (i < 0) ? "Resources" : base + ".Resources";
		return getInstance (bundle, base, cls.getClassLoader ());
	}


	public static I18NBundle getInstance (String bundle, String baseName,
										  ClassLoader loader)
	{
		I18NBundle b = new I18NBundle
			(ResourceBundle.getBundle (bundle, Locale.getDefault (), loader),
			 baseName);
		b.initClassLoader (loader);
		return b;
	}


	private ClassLoader loader;
	private final String baseName;


	public I18NBundle (ResourceBundle parent, String baseName)
	{
		super ();
		this.parent = parent;
		this.baseName = baseName;
	}


	public void initClassLoader (ClassLoader loader)
	{
		if (this.loader != null)
		{
			throw new IllegalStateException ("ClassLoader already set");
		}
		this.loader = loader;
	}


	public ClassLoader getClassLoader ()
	{
		return loader;
	}


	public String getBaseName ()
	{
		return baseName;
	}


	@Override
	protected Object handleGetObject (String key)
	{
		Object o = convertResource (parent.getObject (key));
		if (o == null)
		{
			throw new MissingResourceException (key, parent.toString (), key);
		}
		return o;
	}


	public Object convertResource (Object resource)
	{
		if ((resource instanceof String)
			&& ((String) resource).startsWith ("`"))
		{
			String s = (String) resource;
			if (s.length () < 2)
			{
				return null;
			}
			if (s.charAt (s.length () - 1) != '`')
			{
				return null;
			}
			s = s.substring (1, s.length () - 1).trim ();
			int p = s.indexOf (' ');
			String c;
			if (p < 0)
			{
				c = s;
				s = null;
			}
			else
			{
				c = s.substring (0, p);
				s = s.substring (p + 1).trim ();
			}
			ResourceConverter rc = null;
			synchronized (converters)
			{
				for (int i = converters.size () - 1; i >= 0; i--)
				{
					ResourceConverter r
						= (ResourceConverter) converters.get (i);
					if (r.canHandleConversion (c))
					{
						rc = r;
						break;
					}
				}
			}
			if (rc != null)
			{
				return rc.convert (c, s, this);
			}
			return null;
		}
		return resource;
	}


	@Override
	public Enumeration getKeys ()
	{
		return parent.getKeys ();
	}


	public String getStringOrNull (String key)
	{
		return getString (key, null);
	}


	public String getString (String key, String defaultValue)
	{
		try
		{
			return getString (key);
		}
		catch (MissingResourceException e)
		{
			return defaultValue;
		}
	}


	public Object getObject (String key, Object defaultValue)
	{
		try
		{
			return getObject (key);
		}
		catch (MissingResourceException e)
		{
			return defaultValue;
		}
	}


	public String msg (String key) throws MissingResourceException
	{
		return msg (key, Utils.OBJECT_0);
	}


	public String msg (String key, Object arg1)
		throws MissingResourceException
	{
		return msg (key, new Object[] {arg1});
	}


	public String msg (String key, Object arg1, Object arg2)
		throws MissingResourceException
	{
		return msg (key, new Object[] {arg1, arg2});
	}


	public String msg (String key, Object arg1, Object arg2, Object arg3)
		throws MissingResourceException
	{
		return msg (key, new Object[] {arg1, arg2, arg3});
	}


	public String msg (String key, Object arg1, Object arg2, Object arg3,
					   Object arg4)
		throws MissingResourceException
	{
		return msg (key, new Object[] {arg1, arg2, arg3, arg4});
	}


	public String msg (String key, Object arg1, Object arg2, Object arg3,
					   Object arg4, Object arg5)
		throws MissingResourceException
	{
		return msg (key, new Object[] {arg1, arg2, arg3, arg4, arg5});
	}


	public String msg (String key, Object[] arguments)
		throws MissingResourceException
	{
		try
		{
			return MessageFormat.format (getString (key), arguments);
		}
		catch (RuntimeException e)
		{
			if (e instanceof MissingResourceException)
			{
				return key + ": " + Arrays.toString (arguments);
			}
			for (int i = 0; i < arguments.length; i++)
			{
				Object a = arguments[i];
				if (!((a == null) || (a instanceof Number)
					  || (a instanceof Boolean) || (a instanceof String)
					  || (a instanceof Date)))
				{
					arguments[i] = a.toString ();
				}
			}
			return MessageFormat.format	(getString (key), arguments);
		}
	}


	public Described keyToDescribed (String key)
	{
		return new EnumValueImpl (this, key, null);
	}

}
