
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
 * A <code>Configurable</code> object defines a set of configurable options.
 * 
 * @author Ole Kniemeyer
 */
public interface Configurable
{
	/**
	 * This method is invoked to obtain the configurable options of this
	 * <code>Configurable</code> object. The method has to add all
	 * {@link Configuration}s of this object to the specified <code>set</code>.
	 * 
	 * @param set a set to which configurations of this object are added
	 */
	void addConfigurations (ConfigurationSet set);
}
