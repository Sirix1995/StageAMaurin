
// NOTE: This file was generated automatically.

// ********************************************
// *               DO NOT EDIT!               *
// ********************************************


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

package de.grogra.xl.lang;

/**
 * <code>ShortConsumer</code> is a callback interface. Its instances
 * receive values of type <code>short</code> by invocation of their
 * {@link #consume} method.
 * <p>
 * This interface is primarily intended for
 * the declaration of generator methods of the XL programming language
 * with return type <code>short</code>. Such methods have an additional
 * parameter of type <code>ShortConsumer</code> to which they send the
 * generated values. 
 * <p>
 * However, this interface may also be used for similar purposes where
 * a callback instance is needed as a receiver of values of type
 * <code>short</code>.
 *
 * @author Ole Kniemeyer
 */
public interface ShortConsumer
{
	/**
	 * Receives a value of type <code>short</code>.
	 * 
	 */
	void consume (
		short value
		);
}
