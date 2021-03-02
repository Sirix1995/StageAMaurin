
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

package de.grogra.ray2;

/**
 * This simple interface is used to provide configuration options for
 * raytracing algorithms, e.g., a {@link de.grogra.ray2.Renderer} or
 * a light model. 
 * 
 * @author Ole Kniemeyer
 */
public interface Options
{
	/**
	 * Returns the option value for the option identified
	 * by <code>key</code>. If no special value for the option is defined,
	 * <code>defaultValue</code> is returned.
	 * 
	 * @param key identifier for option
	 * @param defaultValue default value of option
	 * @return value of option named <code>key</code>
	 */
	Object get (String key, Object defaultValue);
}
