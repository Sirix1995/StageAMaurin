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

/**
 * This class contains a list of intersection points of a line with the
 * surface of a {@link de.grogra.vecmath.geom.Volume}. The list is
 * piecewise sorted in ascending order of distance, i.e., of
 * {@link de.grogra.vecmath.geom.Intersection#parameter}. The congiguous
 * pieces are computed by
 * {@link de.grogra.vecmath.geom.Volume#computeIntersections}.
 * 
 * @author Ole Kniemeyer
 */
public final class IntersectionList extends Variables
{
	/**
	 * Specifies the number of valid intersections in
	 * {@link #elements}.
	 */
	public int size;

	/**
	 * Contains the list of intersections. Only the components
	 * having indices between 0 and {@link #size} - 1 are valid.
	 */
	public Intersection[] elements = new Intersection[16];

	/**
	 * Sets the {@link #size} of this list to the specified
	 * value. The first <code>size</code> {@link #elements}
	 * will contain valid instances when this method returns. 
	 * 
	 * @param size new size of this list
	 */
	public void setSize (int size)
	{
		if (elements.length < size)
		{
			Intersection[] e = new Intersection[Math.max (elements.length * 2,
				size)];
			System.arraycopy (elements, 0, e, 0, elements.length);
			elements = e;
		}
		this.size = size;
		while (--size >= 0)
		{
			if (elements[size] != null)
			{
				break;
			}
			elements[size] = new Intersection (this);
		}
	}

	/**
	 * Clears this list so that the size is 0.
	 */
	public void clear ()
	{
		size = 0;
		setISize (0);
	}

	/**
	 * Appends an intersection element to this list and returns
	 * the appended instance. The parameters are used to initialize
	 * the intersection element.
	 * 
	 * @param v volume of intersection
	 * @param l line of intersection
	 * @param p line parameter of intersection
	 * @param type type of intersection (see {@link Intersection#type})
	 * 
	 * @return newly appended intersection instance
	 */
	public Intersection add (Volume v, Line l, double p, int type)
	{
		Intersection i = add ();
		i.volume = v;
		i.solid = v;
		i.line = l;
		i.parameter = p;
		i.type = type;
		return i;
	}

	/**
	 * Appends an intersection element to this list and returns
	 * the appended instance. The parameters are used to initialize
	 * the intersection element.
	 * 
	 * @param v volume of intersection
	 * @param l line of intersection
	 * @param p line parameter of intersection
	 * @param type type of intersection (see {@link Intersection#type})
	 * @param face <code>face</code> value of intersection
	 * 
	 * @return newly appended intersection instance
	 */
	public Intersection add (Volume v, Line l, double p, int type, int face)
	{
		Intersection i = add ();
		i.volume = v;
		i.solid = v;
		i.line = l;
		i.parameter = p;
		i.type = type;
		i.face = face;
		return i;
	}

	/**
	 * Appends an intersection element to this list and returns
	 * the appended instance.
	 * 
	 * @return newly appended intersection instance
	 */
	private Intersection add ()
	{
		int s = size;
		setSize (s + 1);
		Intersection is = elements[s];
		is.reset ();
		return is;
	}

	/**
	 * Appends a new intersection element to this list and swaps it with
	 * the existing element at <code>index</code>.
	 * 
	 * @param index index of the element to be swapped with the new element
	 */
	public void addSwap (int index)
	{
		Intersection i = add ();
		elements[size - 1] = elements[index];
		elements[index] = i;
	}

	private Intersection[] removeBuffer = new Intersection[16];

	/**
	 * Removes the elements with indices from <code>start</code> to
	 * <code>end - 1</code>.
	 * 
	 * @param start index to start (inclusive)
	 * @param end index to end (exclusive)
	 */
	public void remove (int start, int end)
	{
		int len = end - start;
		if (len == 0)
		{
			return;
		}
		if (removeBuffer.length < len)
		{
			removeBuffer = new Intersection[Math.max (removeBuffer.length * 2,
				len)];
		}
		System.arraycopy (elements, start, removeBuffer, 0, len);
		System.arraycopy (elements, end, elements, start, size - end);
		size -= len;
		System.arraycopy (removeBuffer, 0, elements, size, len);
	}

	/**
	 * Returns the index of the intersection whose parameter is
	 * closest to <code>p</code>. Only intersection elements with
	 * indices from <code>start</code> to <code>end - 1</code>
	 * and with a parameter value whose difference to <code>p</code>
	 * is less than <code>maxDistance</code> are considered.
	 * 
	 * @param p a line parameter
	 * @param maxDiff maximum difference between <code>p</code> and intersections
	 * @param start index to start (inclusive)
	 * @param end index to end (exclusive)
	 * @return index of closest intersection, or -1
	 */
	public int findClosestIntersection (double p, double maxDiff, int start,
			int end)
	{
		int c = -1;
		while (start < end)
		{
			double a = Math.abs (elements[start].parameter - p);
			if (a < maxDiff)
			{
				c = start;
				maxDiff = a;
			}
			start++;
		}
		return c;
	}

	@Override
	public String toString ()
	{
		StringBuffer buf = new StringBuffer ("IntersectionList[");
		for (int i = 0; i < size; i++)
		{
			if (i > 0)
			{
				buf.append (", ");
			}
			buf.append (elements[i]);
		}
		return buf.append (']').toString ();
	}
}
