
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
 * Instances of <code>VoidToFloatGenerator</code> represent
 * generator functions which take <code>void</code>s as input
 * and yield sequences of values of
 * type <code>float</code>. Such instances are created by
 * lambda expressions of the XL programming language, e.g.,
 * <code>VoidToFloatGenerator f = void => float*</code>
 * <i>some generator expression</i><code>;</code>.
 *
 * @author Ole Kniemeyer
 */
public interface VoidToFloatGenerator
{
	/**
	 * Generates the sequence of values.
	 * 
	 * @param cons each value is yielded to this consumer
	 */
	void evaluateFloat (FloatConsumer cons);
}