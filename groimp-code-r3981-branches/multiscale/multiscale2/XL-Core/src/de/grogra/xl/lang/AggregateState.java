
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
 * An instance of <code>Filter</code> is used in filter method
 * invocations as specified by the XL programming language.
 * Filter methods use such an instance to store state information
 * which they need to perform the filtering.
 * 
 * @author Ole Kniemeyer
 */
/**
 * An instance of <code>AggregateState</code> is used in aggregate method
 * invocations as specified by the XL programming language.
 * Aggregate methods use such an instance
 * 
 * @author Ole Kniemeyer
 */
/**
 * An instance of <code>AggregateState</code> is used in aggregate method
 * invocations as specified by the XL programming language.
 * Aggregate methods use such an instance to collect the information
 * about a sequence of values which they need to compute an
 * aggregate value, and to tell the invoker if the computation is
 * already finished even if there are values left.
 * <p>
 * This interface is abstract in the sense that only its direct
 * subinterfaces {@link BooleanAggregateState}, ...,
 * {@link ObjectAggregateState} are used by the specification of
 * the XL programming language and only those should be implemented.
 * 
 * @author Ole Kniemeyer
 */
public interface AggregateState
{
	/**
	 * Returns <code>true</code> to indicate the invoker of the aggregate
	 * method that the computation of the aggregate value has been completed
	 * by the aggregate method, even if there are values left for aggregation.
	 * E.g., this is the case for a short circuit implementation of the boolean
	 * or where the result is known when the first <code>true</code>-value
	 * is encountered. The invoker must not invoke the aggregate method
	 * again.
	 * 
	 * @return <code>true</code> iff the final aggregated value is already known
	 */
	public boolean isFinished ();
}
