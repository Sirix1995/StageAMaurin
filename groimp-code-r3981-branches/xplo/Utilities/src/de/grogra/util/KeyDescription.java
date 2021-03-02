
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

import de.grogra.reflect.Type;

/**
 * A description of the properties of a key. This interface inherits
 * the semantics of its superinterface {@link de.grogra.util.Described}.
 * In addition, it provides information about some key being described,
 * and about the type and quantity of values associated with this key.
 * 
 * @author Ole Kniemeyer
 */
public interface KeyDescription extends Described
{
	/**
	 * Returns the key which is described by this instance.
	 * 
	 * @return the key
	 */
	String getKey ();

	/**
	 * Returns the type of values which are associated with the key.
	 * 
	 * @return the type of values
	 */
	Type getType ();

	/**
	 * Returns the quantity of values which are associated with the key.
	 * 
	 * @return the quantity of values
	 */
	Quantity getQuantity ();
}
