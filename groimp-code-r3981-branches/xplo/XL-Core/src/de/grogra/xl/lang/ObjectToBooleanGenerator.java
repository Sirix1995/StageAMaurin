
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
 * Instances of <code>ObjectToBooleanGenerator</code> represent
 * generator functions which take <code>K</code>s as input
 * and yield sequences of values of
 * type <code>boolean</code>. Such instances are created by
 * lambda expressions of the XL programming language, e.g.,
 * <code>ObjectToBooleanGenerator f = K x => boolean*</code>
 * <i>some generator expression containing x</i><code>;</code>.
 *
 * @author Ole Kniemeyer
 */
public interface ObjectToBooleanGenerator<K>
{
	/**
	 * Generates the sequence of values for parameter <code>x</code>.
	 * 
	 * @param cons each value is yielded to this consumer
	 * @param x where the generator function is to be evaluated
	 */
	void evaluateBoolean (BooleanConsumer cons, K x);
}
