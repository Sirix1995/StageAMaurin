
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

/**
 * A <code>Configuration</code> is a <code>ModifiableMap</code> which uses
 * <code>String</code>s as keys and maintains three levels of associations:
 * <ol>
 * <li>The basic level is represented by default values and is not modified
 * by methods of this class.
 * <li>The next level overrides the basic level, it is defined by
 * the <code>override</code> methods.
 * <li>The third level overrides the other levels, it is defined by the
 * <code>put</code> method.
 * </ol>
 * 
 * @author Ole Kniemeyer
 */
public final class Configuration implements ModifiableMap
{
	final KeyDescription[] keys;
	private final Map values;
	private final StringMap override = new StringMap (16, true);
	private final StringMap modified = new StringMap (16, true);


	/**
	 * Creates a new <code>Configuration</code>. Its keys are described
	 * by <code>keys</code>, its default values by the map <code>values</code>.
	 * 
	 * @param keys the keys of this configuration
	 * @param values the default values of this configuration
	 */
	public Configuration (KeyDescription[] keys, Map values)
	{
		this.keys = keys;
		this.values = values;
	}


	public Object put (Object key, Object value)
	{
		Object o = get (key, null);
		modified.put (key, value);
		return o;
	}


	/**
	 * Overrides default values of this configuration.
	 * 
	 * @param key a key
	 * @param value the new default value for <code>key</code>
	 */
	public void override (String key, Object value)
	{
		override.put (key, value);
	}


	/**
	 * Sets a default value if no default value is provided by the
	 * map for default values.
	 * 
	 * @param key a key
	 * @param value the default value for <code>key</code>
	 */
	public void overrideIfUnset (String key, Object value)
	{
		if (values.get (key, DEFAULT_VALUE) == DEFAULT_VALUE)
		{
			override.put (key, value);
		}
	}


	public Object get (Object key, Object defaultValue)
	{
		Object o = modified.get (key, DEFAULT_VALUE);
		if (o != DEFAULT_VALUE)
		{
			return o;
		}
		o = override.get (key, DEFAULT_VALUE);
		if (o != DEFAULT_VALUE)
		{
			return o;
		}
		return values.get (key, defaultValue);
	}


	public void writeBack ()
	{
		/*
		if (writeBack != null)
		{
			for (int i = writeBack.length - 1; i >= 0; i--)
			{
				Object o = modified.get (writeBack[i].getName (), DEFAULT_VALUE);
				if (o != DEFAULT_VALUE)
				{
					((ModifiableMap) values).put (writeBack[i], o);
				}
			}
		}*/
	}


	boolean contains (Object a)
	{
		for (int i = keys.length - 1; i >= 0; i--)
		{
			if (keys[i].getKey ().equals (a))
			{
				return true;
			}
		}
		return false;
	}

}
