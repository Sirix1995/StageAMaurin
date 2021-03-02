
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

/**
 * A <code>Routine</code> provides a VMXState with the information
 * that is needed to construct a stack frame in which the
 * routine can be executed. It is a callback interface and used in the
 * methods {@link de.grogra.xl.vmx.VMXState#invoke(Routine, int, Authorization)} and
 * {@link de.grogra.xl.vmx.VMXState#createDescriptor(Routine, int, Authorization)}. 
 * 
 * @author Ole Kniemeyer
 */
public interface Routine
{
	/**
	 * Determines whether this routine expects its parameters on
	 * the Java frame or the normal frame.
	 * 
	 * @return <code>true</code> iff parameters are expected on the Java frame
	 */
	boolean hasJavaParameters ();

	/**
	 * Computes the number of VMXState stack elements for the parameters
	 * of this routine. 
	 * 
	 * @return the number of stack elements for parameters
	 */
	int getParameterSize ();

	/**
	 * Computes the number of VMXState stack elements for the Java frame
	 * of this routine. This includes the parameters, if any.
	 * 
	 * @return the size of the Java frame in terms of stack elements
	 */
	int getJavaFrameSize ();
	
	/**
	 * Computes the number of VMXState stack elements for the frame
	 * of this routine. This includes the parameters, if any.
	 * 
	 * @return the size of the frame in terms of stack elements
	 */
	int getFrameSize ();

	/**
	 * This callback method is invoked by the executing {@link VMXState}.
	 * A return value has to be wrapped in an instance of
	 * {@link AbruptCompletion.Return}.
	 * 
	 * @param s the executing VMXState
	 * @return the wrapped return value
	 */
	AbruptCompletion.Return execute (VMXState s);
}
