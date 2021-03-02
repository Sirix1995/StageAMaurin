
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
public final class Filter
{
	/**
	 * An invocation of a filter method has to set this field to
	 * indicate whether the invocation has passed the filter or not.
	 * If this field is set to <code>true</true>, the filter method
	 * also has to set the <code>val</code>-field
	 * corresponding to the result type to the filtered value.
	 * Otherwise, the invocation has not passed the filter.
	 */
	public boolean accept;

	/**
	 * This field contains the result if the type is
	 * <code>boolean</code>, <code>byte</code>, <code>short</code>,
	 * <code>char</code>, or <code>int</code>.
	 */
	public int ival;
	
	/**
	 * This field contains the result if the type is <code>long</code>.
	 */
	public long lval;
	
	/**
	 * This field contains the result if the type is <code>float</code>.
	 */
	public float fval;
	
	/**
	 * This field contains the result if the type is <code>double</code>.
	 */
	public double dval;
	
	/**
	 * This field contains the result if the type is a reference type.
	 * It has to be of the type returned by {@link #getType()}. 
	 */
	public Object aval;

	
	/**
	 * This field may be used freely by filter methods.
	 */
	public int ival1;
	
	/**
	 * This field may be used freely by filter methods.
	 */
	public int ival2;
	
	/**
	 * This field may be used freely by filter methods.
	 */
	public int ival3;

	
	/**
	 * This field may be used freely by filter methods.
	 */
	public long lval1;
	
	/**
	 * This field may be used freely by filter methods.
	 */
	public long lval2;
	
	/**
	 * This field may be used freely by filter methods.
	 */
	public long lval3;

	
	/**
	 * This field may be used freely by filter methods.
	 */
	public float fval1;
	
	/**
	 * This field may be used freely by filter methods.
	 */
	public float fval2;
	
	/**
	 * This field may be used freely by filter methods.
	 */
	public float fval3;

	
	/**
	 * This field may be used freely by filter methods.
	 */
	public double dval1;
	
	/**
	 * This field may be used freely by filter methods.
	 */
	public double dval2;
	
	/**
	 * This field may be used freely by filter methods.
	 */
	public double dval3;

	
	/**
	 * This field may be used freely by filter methods.
	 */
	public Object aval1;
	
	/**
	 * This field may be used freely by filter methods.
	 */
	public Object aval2;
	
	/**
	 * This field may be used freely by filter methods.
	 */
	public Object aval3;


	private Class type;
	private boolean initialized = false;
	private boolean finished = false;

	
	private Filter (Class type)
	{
		this.type = type;
	}


	public static Filter allocate (Class type)
	{
		return new Filter (type);
	}

	
	/**
	 * Returns <code>true</code> the first time it is invoked.
	 * This has to be queried in implementations of filter methods
	 * to initialize their state.
	 * 
	 * @return <code>true</code> iff this method is invoked the first
	 * time for a specific invocation of a filter method
	 */
	public boolean initialize ()
	{
		if (initialized)
		{
			return false;
		}
		initialized = true;
		return true;
	}
	
	
	/**
	 * Sets the <code>finished</code>-flags to <code>true</code>. This
	 * may only be invoked by filter methods.
	 * 
	 * @see #isFinished()
	 */
	public void setFinished ()
	{
		finished = true;
	}
	
	
	/**
	 * Returns <code>true</code> iff {@link #setFinished()} has been called
	 * previously. This is used to indicate the invoker of the
	 * filter method that it must not invoke the filter method any more,
	 * even if there are values left for filtering.
	 * </ol>
	 * 
	 * @return <code>true</code> iff {@link #setFinished()} has been called
	 * previously
	 */
	public boolean isFinished ()
	{
		return finished;
	}


	/**
	 * Returns the result type of the filter. This has to be
	 * respected by filter method implementations which compute
	 * resulting values of reference type.
	 * 
	 * @return the result type
	 * @see #aval
	 */
	public Class getType ()
	{
		return type;
	}

}
