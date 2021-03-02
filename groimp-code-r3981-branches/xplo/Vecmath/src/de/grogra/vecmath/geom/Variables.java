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

package de.grogra.vecmath.geom;

import java.util.HashMap;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * This class contains a set of temporary variables which may be used
 * freely in methods which receive an instance of <code>Variables</code>
 * as argument. The sole requirement is that the size of the
 * <code>int</code>-stack is restored to its value before method invocation.
 * 
 * @author Ole Kniemeyer
 */
public class Variables
{
	/**
	 * This point may be used freely.
	 */
	public final Point3d tmpPoint0 = new Point3d ();

	/**
	 * This point may be used freely.
	 */
	public final Point3d tmpPoint1 = new Point3d ();

	/**
	 * This point may be used freely.
	 */
	public final Point3d tmpPoint2 = new Point3d ();

	/**
	 * This point may be used freely.
	 */
	public final Point3d tmpPoint3 = new Point3d ();

	/**
	 * This vector may be used freely.
	 */
	public final Vector3d tmpVector0 = new Vector3d ();

	/**
	 * This vector may be used freely.
	 */
	public final Vector3d tmpVector1 = new Vector3d ();

	/**
	 * This vector may be used freely.
	 */
	public final Vector3d tmpVector2 = new Vector3d ();

	/**
	 * This vector may be used freely.
	 */
	public final Vector3d tmpVector3 = new Vector3d ();

	/**
	 * This matrix may be used freely.
	 */
	public final Matrix3d tmpMatrix3 = new Matrix3d ();

	/**
	 * This line may be used freely.
	 */
	public final Line tmpLine = new Line ();
	
	/**
	 * This map may be used by methods which have an instance
	 * of <code>Variables</code> as parameter in order to store
	 * some information which should be available in following
	 * invocations of the method.
	 */
	public final HashMap<Object,Object> cache = new HashMap<Object,Object> ();

	/**
	 * This field contains an <code>int</code>-stack. Its size
	 * is defined by {@link #isize}.
	 * 
	 * @see #ipush(int)
	 */
	public int[] istack = new int[16];

	/**
	 * The current size of the <code>int</code>-stack {@link #istack}.
	 * 
	 * @see #setISize(int)
	 */
	private int isize;

	public int getISize ()
	{
		return isize;
	}

	/**
	 * This method sets the size of the <code>int</code>-stack
	 * {@link #istack} to <code>isize</code>. The array is enlarged
	 * if necessary.
	 * 
	 * @param isize new size of stack
	 */
	public void setISize (int isize)
	{
		if (isize > istack.length)
		{
			System.arraycopy (istack, 0, istack = new int[Math.max (isize, istack.length * 2)], 0, this.isize);
		}
		this.isize = isize;
	}

	/**
	 * This method pushs a new <code>value</code>
	 * on the <code>int</code>-stack {@link #istack}.
	 * 
	 * @param value value to push
	 */
	public void ipush (int value)
	{
		setISize (isize + 1);
		istack[isize - 1] = value;
	}
	
	/**
	 * This method pops the topmost element off the
	 * <code>int</code>-stack {@link #istack} and returns
	 * its value.
	 * 
	 * @return popped element
	 */
	public int ipop ()
	{
		return istack[--isize];
	}

	/**
	 * This method increases the size of the
	 * <code>int</code>-stack {@link #istack} by <code>size</code>
	 * and returns the previous size of the stack.
	 * 
	 * @param size size increment
	 * @return previous value of stack size
	 * 
	 * @see #getISize()
	 */
	public int ienter (int size)
	{
		int s = this.isize;
		setISize (this.isize + size);
		return s;
	}

}
