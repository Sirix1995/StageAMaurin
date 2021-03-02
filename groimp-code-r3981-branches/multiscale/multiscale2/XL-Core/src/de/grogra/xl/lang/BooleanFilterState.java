
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
 * <code>BooleanFilterState</code> is the subinterface of
 * {@link FilterState} that is used for filter methods with return
 * value of type boolean. Its precise usage is specified by the
 * XL programming language.
 *
 * @author Ole Kniemeyer
 */
public interface BooleanFilterState extends FilterState
{
	/**
	 * Returns the current result of the filter. This is only valid
	 * if {@link FilterState#isAccepted()} returns <code>true</code>.
	 * 
	 * @return current result of filter
	 */
	boolean getBooleanResult ();
}
