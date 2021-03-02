
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

import java.util.Iterator;
import java.util.IdentityHashMap;

/**
 * A <code>ConfigurationSet</code> is a <code>ModifiableMap</code>
 * and consists of a set of {@link de.grogra.util.Configuration}s.
 * The map associations are the union of the associations of the
 * individual configurations.
 * 
 * @author Ole Kniemeyer
 */
public final class ConfigurationSet implements ModifiableMap
{
	private final IdentityHashMap configs = new IdentityHashMap ();
	private final IdentityHashMap configurables = new IdentityHashMap ();
	private final String name;


	/**
	 * Creates a new configuration set with the given <code>name</code>.
	 * 
	 * @param name the name for this set
	 */
	public ConfigurationSet (String name)
	{
		this.name = name;
	}


	/**
	 * Adds <code>c</code> to this set.
	 * 
	 * @param c the configuration to be added
	 */
	public void add (Configuration c)
	{
		configs.put (c, this);
	}


	/**
	 * Adds the configurations of <code>c</code> to this set by invoking
	 * {@link Configurable#addConfigurations(ConfigurationSet)} on
	 * <code>c</code>. If this method is invoke more than once with the same
	 * <code>c</code>, only the first invocation has an effect.
	 * 
	 * @param c a configurable object
	 */
	public void add (Configurable c)
	{
		if (configurables.put (c, this) == null)
		{
			c.addConfigurations (this);
		}
	}


	/**
	 * Returns the number of configurations.
	 * 
	 * @return number of configurations
	 */
	public int size ()
	{
		return configs.size ();
	}


	public Object put (Object key, Object value)
	{
		for (Iterator i = configs.keySet ().iterator (); i.hasNext (); )
		{
			Configuration c = (Configuration) i.next ();
			if (c.contains (key))
			{
				Object o = c.get (key, null);
				c.put (key, value);
				return o;
			}
		}
		return null;
	}


	public Object get (Object key, Object defaultValue)
	{
		for (Iterator i = configs.keySet ().iterator (); i.hasNext (); )
		{
			Configuration c = (Configuration) i.next ();
			if (c.contains (key))
			{
				Object o = c.get (key, DEFAULT_VALUE);
				if (o != DEFAULT_VALUE)
				{
					return o;
				}
			}
		}
		return defaultValue;
	}


	public void writeBack ()
	{
		for (Iterator i = configs.keySet ().iterator (); i.hasNext (); )
		{
			((Configuration) i.next ()).writeBack ();
		}
	}


	/**
	 * Returns the union of the <code>KeyDescription</code>s of the
	 * contained configurations.
	 * 
	 * @return the descriptions for all keys of this set
	 */
	public KeyDescription[] getKeyDescriptions ()
	{
		int n = 0;
		for (Iterator i = configs.keySet ().iterator (); i.hasNext (); )
		{
			n += ((Configuration) i.next ()).keys.length;
		}
		KeyDescription[] a = new KeyDescription[n];
		n = 0;
		for (Iterator i = configs.keySet ().iterator (); i.hasNext (); )
		{
			Configuration c = (Configuration) i.next ();
			System.arraycopy (c.keys, 0, a, n, c.keys.length);
			n += c.keys.length;
		}
		return a;
	}


	/**
	 * Returns the name of this set.
	 * 
	 * @return the name
	 */
	public String getName ()
	{
		return name;
	}
}
