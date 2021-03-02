
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

package de.grogra.xl.util;

import de.grogra.xl.lang.BooleanAggregateState;
import de.grogra.xl.lang.ByteAggregateState;
import de.grogra.xl.lang.CharAggregateState;
import de.grogra.xl.lang.DoubleAggregateState;
import de.grogra.xl.lang.FloatAggregateState;
import de.grogra.xl.lang.IntAggregateState;
import de.grogra.xl.lang.LongAggregateState;
import de.grogra.xl.lang.ObjectAggregateState;
import de.grogra.xl.lang.ShortAggregateState;

/**
 * Utility class which implements all <code>AggregateState</code>
 * interfaces and provides some general fields for storing state.
 * Instances of this class are pooled so that heap traffic is reduced.
 * 
 * @author Ole Kniemeyer
 */
public final class AggregateStateImpl<T> implements
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
	${pp.Type}AggregateState,
#end
!!*/
//!! #* Start of generated code
// generated
	BooleanAggregateState,
// generated
	ByteAggregateState,
// generated
	ShortAggregateState,
// generated
	CharAggregateState,
// generated
	IntAggregateState,
// generated
	LongAggregateState,
// generated
	FloatAggregateState,
// generated
	DoubleAggregateState,
//!! *# End of generated code
	ObjectAggregateState<T>
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
	 */
	public T aval;

	
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


	private static AggregateStateImpl POOL;
	
	private boolean finished;
	private AggregateStateImpl next;

	
	private AggregateStateImpl ()
	{
	}


	public static synchronized AggregateStateImpl<?> allocate ()
	{
		AggregateStateImpl a = POOL;
		if (a != null)
		{
			POOL = a.next;
		}
		else
		{
			a = new AggregateStateImpl ();
		}
		a.finished = false;
		return a;
	}


/*!!
#foreach ($type in $types)
$pp.setType($type)

#if ($type == "Object")
	#set ($type = "T")
#end

	private static synchronized <T> $type get${pp.Type}Result (AggregateStateImpl<T> a)
	{
		a.aval1 = a.aval2 = a.aval3 = a.aval = null;
		a.next = POOL;
		POOL = a;
		return ($type) (a.${pp.prefix}val $pp.vm2type);
	}

	public $type get${pp.Type}Result ()
	{
		return get${pp.Type}Result (this);
	}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
// generated
	private static synchronized <T> boolean getBooleanResult (AggregateStateImpl<T> a)
	{
		a.aval1 = a.aval2 = a.aval3 = a.aval = null;
		a.next = POOL;
		POOL = a;
		return (boolean) (a.ival  != 0);
	}
// generated
	public boolean getBooleanResult ()
	{
		return getBooleanResult (this);
	}
// generated
// generated
// generated
	private static synchronized <T> byte getByteResult (AggregateStateImpl<T> a)
	{
		a.aval1 = a.aval2 = a.aval3 = a.aval = null;
		a.next = POOL;
		POOL = a;
		return (byte) (a.ival );
	}
// generated
	public byte getByteResult ()
	{
		return getByteResult (this);
	}
// generated
// generated
// generated
	private static synchronized <T> short getShortResult (AggregateStateImpl<T> a)
	{
		a.aval1 = a.aval2 = a.aval3 = a.aval = null;
		a.next = POOL;
		POOL = a;
		return (short) (a.ival );
	}
// generated
	public short getShortResult ()
	{
		return getShortResult (this);
	}
// generated
// generated
// generated
	private static synchronized <T> char getCharResult (AggregateStateImpl<T> a)
	{
		a.aval1 = a.aval2 = a.aval3 = a.aval = null;
		a.next = POOL;
		POOL = a;
		return (char) (a.ival );
	}
// generated
	public char getCharResult ()
	{
		return getCharResult (this);
	}
// generated
// generated
// generated
	private static synchronized <T> int getIntResult (AggregateStateImpl<T> a)
	{
		a.aval1 = a.aval2 = a.aval3 = a.aval = null;
		a.next = POOL;
		POOL = a;
		return (int) (a.ival );
	}
// generated
	public int getIntResult ()
	{
		return getIntResult (this);
	}
// generated
// generated
// generated
	private static synchronized <T> long getLongResult (AggregateStateImpl<T> a)
	{
		a.aval1 = a.aval2 = a.aval3 = a.aval = null;
		a.next = POOL;
		POOL = a;
		return (long) (a.lval );
	}
// generated
	public long getLongResult ()
	{
		return getLongResult (this);
	}
// generated
// generated
// generated
	private static synchronized <T> float getFloatResult (AggregateStateImpl<T> a)
	{
		a.aval1 = a.aval2 = a.aval3 = a.aval = null;
		a.next = POOL;
		POOL = a;
		return (float) (a.fval );
	}
// generated
	public float getFloatResult ()
	{
		return getFloatResult (this);
	}
// generated
// generated
// generated
	private static synchronized <T> double getDoubleResult (AggregateStateImpl<T> a)
	{
		a.aval1 = a.aval2 = a.aval3 = a.aval = null;
		a.next = POOL;
		POOL = a;
		return (double) (a.dval );
	}
// generated
	public double getDoubleResult ()
	{
		return getDoubleResult (this);
	}
// generated
// generated
// generated
	private static synchronized <T> T getObjectResult (AggregateStateImpl<T> a)
	{
		a.aval1 = a.aval2 = a.aval3 = a.aval = null;
		a.next = POOL;
		POOL = a;
		return (T) (a.aval );
	}
// generated
	public T getObjectResult ()
	{
		return getObjectResult (this);
	}
//!! *# End of generated code
	
	
	/**
	 * Sets the <code>finished</code>-flag to <code>true</code>.
	 * Subsequent invocations of {@link #isFinished()} will
	 * return <code>true</code>.
	 */
	public void setFinished ()
	{
		finished = true;
	}
	

	public boolean isFinished ()
	{
		return finished;
	}

}
