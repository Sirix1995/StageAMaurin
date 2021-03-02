
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
 * Instances of <code>IntToFloat</code> represent functions
 * which take <code>int</code>s as input and return values of
 * type <code>float</code>. Such instances are created by
 * lambda expressions of the XL programming language, e.g.,
 * <code>IntToFloat f = int x => float</code>
 * <i>some expression containing x</i><code>;</code>.
 *
 * @author Ole Kniemeyer
 */
public interface IntToFloat
{
	/**
	 * Computes the value of this function at <code>x</code>.
	 * 
	 * @param x where the function is to be evaluated
	 * @return function value at <code>x</code>
	 */
	float evaluateFloat (int x);
}
