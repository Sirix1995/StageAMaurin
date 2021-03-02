
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
 * An instance of <code>Aggregate</code> is used in aggregate method
 * invocations as specified by the XL programming language.
 * Aggregate methods use such an instance to collect the information
 * about a sequence of values which they need to compute an
 * aggregate value.
 * 
 * @author Ole Kniemeyer
 */
public final class Aggregate
{
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
	 * This field may be used freely by aggregate methods.
	 */
	public int ival1;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public int ival2;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public int ival3;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public int ival4;

	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public long lval1;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public long lval2;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public long lval3;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public long lval4;

	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public float fval1;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public float fval2;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public float fval3;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public float fval4;

	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public double dval1;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public double dval2;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public double dval3;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public double dval4;

	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public Object aval1;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public Object aval2;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public Object aval3;
	
	/**
	 * This field may be used freely by aggregate methods.
	 */
	public Object aval4;


	private static Aggregate POOL;
	
	private Class type;
	private boolean initialized;
	private boolean finished;
	private Aggregate next;

	
	private Aggregate ()
	{
	}


	public static synchronized Aggregate allocate (Class type)
	{
		Aggregate a = POOL;
		if (a != null)
		{
			POOL = a.next;
		}
		else
		{
			a = new Aggregate ();
		}
		a.type = type;
		return a;
	}


	public static synchronized int ival (Aggregate a)
	{
		a.aval = a.aval1 = a.aval2 = a.aval3 = null;
		a.initialized = false;
		a.finished = false;
		a.next = POOL;
		POOL = a;
		return a.ival;
	}


	public static synchronized long lval (Aggregate a)
	{
		a.aval = a.aval1 = a.aval2 = a.aval3 = null;
		a.initialized = false;
		a.finished = false;
		a.next = POOL;
		POOL = a;
		return a.lval;
	}


	public static synchronized float fval (Aggregate a)
	{
		a.aval = a.aval1 = a.aval2 = a.aval3 = null;
		a.initialized = false;
		a.finished = false;
		a.next = POOL;
		POOL = a;
		return a.fval;
	}


	public static synchronized double dval (Aggregate a)
	{
		a.aval = a.aval1 = a.aval2 = a.aval3 = null;
		a.initialized = false;
		a.finished = false;
		a.next = POOL;
		POOL = a;
		return a.dval;
	}


	public static synchronized Object aval (Aggregate a)
	{
		Object o = a.aval;
		a.aval = a.aval1 = a.aval2 = a.aval3 = null;
		a.initialized = false;
		a.finished = false;
		a.next = POOL;
		POOL = a;
		return o;
	}

	
	/**
	 * Returns <code>true</code> the first time it is invoked.
	 * This has to be queried in implementations of aggregate methods
	 * to initialize their computation.
	 * 
	 * @return <code>true</code> iff this method is invoked the first
	 * time for a specific invocation of an aggregate method
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
	 * Sets the <code>finished</code>-flags to <code>true</code>.
	 * 
	 * @see #isFinished()
	 */
	public void setFinished ()
	{
		finished = true;
	}
	
	
	/**
	 * Returns <code>true</code> iff {@link #setFinished()} has been called
	 * previously. This is used for two purposes:
	 * <ol>
	 * <li>To indicate the aggregate method implementation that no more
	 * values are available for aggregation. The aggregate method has to
	 * compute the final result and place it in the appropriate field
	 * ({@link #ival}, {@link #lval}, {@link #fval}, {@link #dval},
	 * or {@link #aval}).
	 * <li>To indicate the invoker of the aggregate method that the
	 * computation of the aggregate value has been completed by the
	 * aggregate method, even if there are values left for aggregation.
	 * E.g., this is the case for a shortcut-implementation of the boolean
	 * or where the result is known when the first <code>true</code>-value
	 * is encountered. The invoker must not invoke the aggregate method
	 * again.
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
	 * Returns the result type of the aggregation. This has to be
	 * respected by aggregate method implementations which compute
	 * aggregate values of reference type.
	 * 
	 * @return the result type
	 * @see #aval
	 */
	public Class getType ()
	{
		return type;
	}

}
