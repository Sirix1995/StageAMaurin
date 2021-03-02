
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
 * A single-method interface which maps keys to values.
 * 
 * @author Ole Kniemeyer
 */
public interface Map
{
	class Chain implements Map
	{
		private final Map a, b;


		public Chain (Map a, Map b)
		{
			this.a = a;
			this.b = b;
		}


		public Chain (Map a)
		{
			this (a, null);
		}


		public Object get (Object key, Object defaultValue)
		{
			Object o;
			if ((a != null)
				&& ((o = a.get (key, DEFAULT_VALUE)) != DEFAULT_VALUE))
			{
				return o;
			}
			return (b != null) ? b.get (key, defaultValue)
				: defaultValue;
		}
		
		
		public static Map add (Map a, Map b)
		{
			return (a == null) ? b : (b == null) ? a : new Chain (a, b);
		}
	}


	Map EMPTY_MAP = new Chain (null);

	Object DEFAULT_VALUE = new Object ();


	/**
	 * Returns the value associated with <code>key</code>. If there is
	 * no value associated with <code>key</code>, <code>defaultValue</code>
	 * is returned.
	 * 
	 * @param key a key for the map
	 * @param defaultValue the default value
	 * @return the associated value, or <code>defaultValue</code>
	 */
	Object get (Object key, Object defaultValue);
}
