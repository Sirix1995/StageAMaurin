
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
 * An instance of <code>FilterState</code> is used in filter method
 * invocations as specified by the XL programming language.
 * Filter methods use such an instance to store state information
 * which they need to perform the filtering, and to tell the invoker
 * about the result of their application to the current input value.
 * <p>
 * This interface is abstract in the sense that only its direct
 * subinterfaces {@link BooleanFilterState}, ...,
 * {@link ObjectFilterState} are used by the specification of
 * the XL programming language and only those should be implemented.
 * 
 * @author Ole Kniemeyer
 */
public interface FilterState
{
	/**
	 * Returns <code>true</code> iff the value which has been  
	 * passed to the filter method is accepted by the filter,
	 * i.e., it it passes the filter. If this is the case,
	 * the result of the filter is given by the invocation of the
	 * methods {@link BooleanFilterState#getBooleanResult()}, ...,
	 * {@link ObjectFilterState#getObjectResult()} of the
	 * corresponding subinterfaces. Otherwise, no result is yielded.
	 */
	public boolean isAccepted ();

	/**
	 * Returns <code>true</code> iff all following input values of the
	 * filter method will not pass the filter.
	 * This is used to indicate the invoker of the
	 * filter method that it must not invoke the filter method any more,
	 * even if there are values left for filtering.
	 * 
	 * @return <code>true</code> iff following values won't pass the filter
	 */
	public boolean isFinished ();
}
