
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
 * This interface adds a <code>put</code>-method to the
 * <code>Map</code> interface.
 * 
 * @author Ole Kniemeyer
 */
public interface ModifiableMap extends Map
{
	interface Producer
	{
		void addMappings (ModifiableMap out);
	}


	/**
	 * Maps <code>key</code> to <code>value</code>. Following invocations
	 * of the <code>get</code>-method with <code>key</code> as key will
	 * return <code>value</code>. 
	 * 
	 * @param key a key
	 * @param value the value to be associated with <code>key</code>
	 * @return the previously associated value, or <code>null</code>
	 */
	Object put (Object key, Object value);
}
