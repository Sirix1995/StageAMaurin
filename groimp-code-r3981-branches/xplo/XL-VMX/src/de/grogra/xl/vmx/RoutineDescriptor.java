
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

package de.grogra.xl.vmx;

import de.grogra.xl.lang.*;

/**
 * A descriptor for a later invocation of a routine in a given frame context.
 * Instances of <code>RoutineDescriptor</code> are obtained by
 * {@link de.grogra.xl.vmx.VMXState#createDescriptor(Routine, int, Authorization)}.
 * 
 * @author Ole Kniemeyer
 */
public final class RoutineDescriptor implements
/*!!
#foreach ($type in $types)
$pp.setType($type)
	${pp.Type}Consumer,
#end
!!*/
//!! #* Start of generated code
// generated
	BooleanConsumer,
// generated
	ByteConsumer,
// generated
	ShortConsumer,
// generated
	CharConsumer,
// generated
	IntConsumer,
// generated
	LongConsumer,
// generated
	FloatConsumer,
// generated
	DoubleConsumer,
// generated
	ObjectConsumer,
//!! *# End of generated code
	VoidConsumer
{
	final VMXState vmx;
	VMXState.VMXFrame staticLink;
	Authorization auth;
	Routine routine;
	RoutineDescriptor next;


	RoutineDescriptor (VMXState vmx)
	{
		this.vmx = vmx;
	}
	
	
	/**
	 * This method can be used to recycle this instance. If this
	 * descriptor is not needed any more, this method can be invoked
	 * in order to inform the VMXState that it may re-use this instance.
	 */
	public void dispose ()
	{
		next = vmx.descriptorPool;
		vmx.descriptorPool = this;
	}

/*!!
#foreach ($type in $types_void)
$pp.setType($type)

	/**
	 * Invokes the associated routine with
#if ($pp.void)
	 * no parameters.
#else
	 * a single <code>$type</code> parameter.
#end
	 * The routine has to return an instance of
	 * {@link AbruptCompletion.Return} with a <code>void</code> type.
	 $C
	public void consume (
#if (!$pp.void)
		$type value
#end
		)
	{
#if (!$pp.void)
		vmx.${pp.prefix}push (value $pp.type2vm);
#end
		vmx.invoke (this);
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	/**
	 * Invokes the associated routine with
	 * a single <code>boolean</code> parameter.
	 * The routine has to return an instance of
	 * {@link AbruptCompletion.Return} with a <code>void</code> type.
	 */
	public void consume (
		boolean value
		)
	{
		vmx.ipush (value  ? 1 : 0);
		vmx.invoke (this);
	}
// generated
// generated
// generated
	/**
	 * Invokes the associated routine with
	 * a single <code>byte</code> parameter.
	 * The routine has to return an instance of
	 * {@link AbruptCompletion.Return} with a <code>void</code> type.
	 */
	public void consume (
		byte value
		)
	{
		vmx.ipush (value );
		vmx.invoke (this);
	}
// generated
// generated
// generated
	/**
	 * Invokes the associated routine with
	 * a single <code>short</code> parameter.
	 * The routine has to return an instance of
	 * {@link AbruptCompletion.Return} with a <code>void</code> type.
	 */
	public void consume (
		short value
		)
	{
		vmx.ipush (value );
		vmx.invoke (this);
	}
// generated
// generated
// generated
	/**
	 * Invokes the associated routine with
	 * a single <code>char</code> parameter.
	 * The routine has to return an instance of
	 * {@link AbruptCompletion.Return} with a <code>void</code> type.
	 */
	public void consume (
		char value
		)
	{
		vmx.ipush (value );
		vmx.invoke (this);
	}
// generated
// generated
// generated
	/**
	 * Invokes the associated routine with
	 * a single <code>int</code> parameter.
	 * The routine has to return an instance of
	 * {@link AbruptCompletion.Return} with a <code>void</code> type.
	 */
	public void consume (
		int value
		)
	{
		vmx.ipush (value );
		vmx.invoke (this);
	}
// generated
// generated
// generated
	/**
	 * Invokes the associated routine with
	 * a single <code>long</code> parameter.
	 * The routine has to return an instance of
	 * {@link AbruptCompletion.Return} with a <code>void</code> type.
	 */
	public void consume (
		long value
		)
	{
		vmx.lpush (value );
		vmx.invoke (this);
	}
// generated
// generated
// generated
	/**
	 * Invokes the associated routine with
	 * a single <code>float</code> parameter.
	 * The routine has to return an instance of
	 * {@link AbruptCompletion.Return} with a <code>void</code> type.
	 */
	public void consume (
		float value
		)
	{
		vmx.fpush (value );
		vmx.invoke (this);
	}
// generated
// generated
// generated
	/**
	 * Invokes the associated routine with
	 * a single <code>double</code> parameter.
	 * The routine has to return an instance of
	 * {@link AbruptCompletion.Return} with a <code>void</code> type.
	 */
	public void consume (
		double value
		)
	{
		vmx.dpush (value );
		vmx.invoke (this);
	}
// generated
// generated
// generated
	/**
	 * Invokes the associated routine with
	 * a single <code>Object</code> parameter.
	 * The routine has to return an instance of
	 * {@link AbruptCompletion.Return} with a <code>void</code> type.
	 */
	public void consume (
		Object value
		)
	{
		vmx.apush (value );
		vmx.invoke (this);
	}
// generated
// generated
// generated
	/**
	 * Invokes the associated routine with
	 * no parameters.
	 * The routine has to return an instance of
	 * {@link AbruptCompletion.Return} with a <code>void</code> type.
	 */
	public void consume (
		)
	{
		vmx.invoke (this);
	}
// generated
//!! *# End of generated code


	/**
	 * Invokes the associated routine. Parameters have to be
	 * pushed on the stack in advance, the will be popped by
	 * the invocation.
	 * 
	 * @return the return value of the routine
	 */
	public AbruptCompletion.Return invoke ()
	{
		return vmx.invoke (this);
	}

}
