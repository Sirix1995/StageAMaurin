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

import java.util.ArrayList;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * This abstract class can be used as base class for compound volumes
 * whose geometry is defined in terms of a set of contained
 * volumes.
 * 
 * @author Ole Kniemeyer
 */
public abstract class CompoundVolume extends VolumeBase
{
	/**
	 * The list of volumes from which this compound volume is computed.
	 */
	public ArrayList<Volume> volumes = new ArrayList<Volume> ();

	/**
	 * This implementation of <code>boxContainsBoundary</code> returns
	 * <code>true</code> iff at least one of the invocations of this method
	 * on the {@link #volumes} returns <code>true</code>. 
	 */
	public boolean boxContainsBoundary (BoundingBox box, Tuple3d center,
			double radius, Variables temp)
	{
		for (int i = volumes.size () - 1; i >= 0; i--)
		{
			if (volumes.get (i).boxContainsBoundary (box, center,
				radius, temp))
			{
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * 
	 * A compound volume typically does not compute an intersection on its own,
	 * so this method is never invoked.
	 */
	public void computeNormal (Intersection is, Vector3d normal)
	{
		throw new UnsupportedOperationException ();
	}

	/* (non-Javadoc)
	 * 
	 * A compound volume typically does not compute an intersection on its own,
	 * so this method is never invoked.
	 */
	public void computeUV (Intersection is, Vector2d uv)
	{
		throw new UnsupportedOperationException ();
	}

	/* (non-Javadoc)
	 * 
	 * A compound volume typically does not compute an intersection on its own,
	 * so this method is never invoked.
	 */
	public void computeTangents (Intersection is, Vector3d dpdu, Vector3d dpdv)
	{
		throw new UnsupportedOperationException ();
	}

	/**
	 * This method implements a CSG volume which consists of all
	 * points which lie in at least <code>minInside</code>
	 * components (see {@link #volumes}). This can be used to
	 * implement the CSG operations union or intersection.
	 * 
	 * @param line a line
	 * @param all <code>true</code> for all intersections,
	 * <code>false</code> for only the first (closest)
	 * intersection has to be added to <code>list</code>
	 * @param list the intersections are added to this list
	 * @param excludeStart intersection at start point which shall be excluded
	 * @param excludeEnd intersection at end point which shall be excluded
	 * @param minInside a point has to be in at least this number of components 
	 * 
	 * @return <code>true</code> iff the beginning of the line lies
	 * within the volume (i.e., if the line starts within the volume or
	 * enters the volume at the starting point)
	 * 
	 * @see Volume#computeIntersections
	 */
	boolean computeIntersections (Line line, boolean all,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd, int minInside)
	{
		int s = volumes.size ();

		// initial size of list
		int rs = list.size;

		int index = list.ienter (s);
		int end = list.ienter (s);

		// list.iarray[index + i] : index of next unprocessed intersection of volume i
		// when this index reaches list.iarray{end + i], volume i has been processed completely

		// counts the number of volumes which have been entered
		int inside = 0;

		for (int i = 0; i < s; i++)
		{
			list.istack[index + i] = list.size;
			if (volumes.get (i).computeIntersections (line,
				Intersection.ALL, list, excludeStart, excludeEnd))
			{
				inside++;
			}
			list.istack[end + i] = list.size;
		}

		// size of list after intersections with volumes have been added
		int re = list.size;

		// in indicates if the resulting csg volume has been entered
		boolean in = inside >= minInside;

		boolean startInside = in;

		// loop over all intersections, ordered by their parameter
		while (true)
		{
			// find volume with closest unprocessed intersection
			int vol = findClosestVolume (list, s, index, end);
			if (vol < 0)
			{
				// all intersections have been processed
				break;
			}

			// index of closest unprocessed intersection
			int idx = list.istack[index + vol]++;
			Intersection is = list.elements[idx];

			if (is.type == Intersection.PASSING)
			{
				continue;
			}

			if (is.type == Intersection.ENTERING)
			{
				inside++;
			}
			else
			{
				inside--;
			}

			if ((inside >= minInside) != in)
			{
				// csg volume entered or left
				list.addSwap (idx);
				is.solid = this;
				in = !in;

				if (!all)
				{
					break;
				}
			}
		}

		// remove intersections with volumes
		list.remove (rs, re);

		// restore previous size of int-stack
		list.setISize (index);

		return startInside;
	}

	/**
	 * Find the index of closest unprocessed volume in an intersection
	 * list. The data structure is as follows:
	 * <p>
	 * Starting at <code>index</code>, the <code>int</code>-stack
	 * of the <code>list</code> (see {@link Variables#istack}) contains
	 * <code>volumeCount</code> entries which are interpreted as current
	 * indices into <code>list.elements</code>: For each volume, this
	 * indicates the first intersection in <code>list</code> which
	 * has not yet been processed.
	 * <p>
	 * Starting at <code>end</code>,
	 * <code>list.istack</code> contains <code>volumeCount</code> entries
	 * which indicate the final indices: For each volume, if its
	 * current index reaches the final index, the intersections of this
	 * volume have been processed completely.
	 * <p>
	 * Now this method finds the volume number (from 0 to
	 * <code>volumeCount - 1</code>) for which the current intersection is
	 * closest (minimal value of <code>parameter</code>),
	 * and increments the current index for this
	 * volume. If all current indices have reached their final values,
	 * -1 is returned. 
	 * 
	 * @param list an intersection list
	 * @param volumeCount number of volumes
	 * @param index start of current indices in int-stack
	 * @param end start of end indices in int-stack
	 * @return index of closest unprocessed volume, or -1
	 */
	static int findClosestVolume (IntersectionList list, int volumeCount,
			int index, int end)
	{
		double p = Double.POSITIVE_INFINITY;
		Intersection is = null;

		// find volume with closest unprocessed intersection
		int vol = -1;
		for (int i = 0; i < volumeCount; i++)
		{
			int idx;
			if (((idx = list.istack[index + i]) < list.istack[end + i])
				&& ((is = list.elements[idx]).parameter < p))
			{
				vol = i;
				p = is.parameter;
			}
		}
		return vol;
	}

	/**
	 * Sorts the intersections in <code>list</code> according to
	 * their <code>parameter</code> value. The data structure is the same
	 * as for {@link #findClosestVolume(IntersectionList, int, int, int)},
	 * <code>begin</code> points to the first intersection in
	 * <code>list</code>. If <code>all</code> is <code>false</code>,
	 * only the first intersection is extracted, all other intersections
	 * are removed.
	 * 
	 * @param all sort all intersections or extract only the first
	 * @param list an intersection list
	 * @param begin index of first intersection
	 * @param volumeCount number of volumes
	 * @param index start of current indices in int-stack
	 * @param end start of end indices in int-stack
	 * @return index of closest unprocessed volume, or -1
	 */
	static void sortIntersections (boolean all, IntersectionList list,
			int begin, int volumeCount, int index, int end)
	{

		// size of list after intersections with volumes have been added
		int ls = list.size;

		// loop over all intersections, ordered by their parameter
		while (true)
		{
			// find volume with closest unprocessed intersection
			int vol = findClosestVolume (list, volumeCount, index, end);
			if (vol < 0)
			{
				// all intersections have been processed
				break;
			}

			// index of closest unprocessed intersection
			int idx = list.istack[index + vol]++;

			list.addSwap (idx);
			if (!all)
			{
				break;
			}
		}

		// remove intersections with volumes
		list.remove (begin, ls);
		list.setISize (index);
	}

}
