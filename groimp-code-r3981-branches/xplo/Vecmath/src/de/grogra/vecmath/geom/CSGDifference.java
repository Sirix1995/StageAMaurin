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

import javax.vecmath.Tuple3d;

/**
 * An instance of this class represents the difference of a list of volumes.
 * The difference consists of all points which are contained in the first
 * volume of {@link #volumes}, but not in one or more of
 * the remaining volumes.
 * 
 * @author Ole Kniemeyer
 */
public class CSGDifference extends CompoundVolume
{

	/**
	 * Creates a new <code>CSGDifference</code> whose list of volumes
	 * is empty.
	 */
	public CSGDifference ()
	{
	}

	/**
	 * Creates a new <code>CSGDifference</code> whose list of volumes
	 * is set to <code>[a, b]</code>.
	 */
	public CSGDifference (Volume a, Volume b)
	{
		volumes.add (a);
		volumes.add (b);
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		if (!volumes.get (0).contains (point, open))
		{
			return false;
		}
		for (int i = 1; i < volumes.size (); i++)
		{
			if (volumes.get (i).contains (point, !open))
			{
				return false;
			}
		}
		return true;
	}

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		int s = volumes.size ();

		// initial size of list
		int rs = list.size;

		int index = list.ienter (s);
		int end = list.ienter (s);

		// list.iarray[index + i] : index of next unprocessed intersection of volume i
		// when this index reaches list.iarray{end + i], volume i has been processed completely

		// counts the number of volumes (excluding first) which have been entered
		int inside = 0;

		// indicates if the first volume has been entered
		boolean firstInside = false;

		for (int i = 0; i < s; i++)
		{
			list.istack[index + i] = list.size;
			if (volumes.get (i).computeIntersections (line,
				Intersection.ALL, list, excludeStart, excludeEnd))
			{
				if (i == 0)
				{
					firstInside = true;
				}
				else
				{
					inside++;
				}
			}
			list.istack[end + i] = list.size;
		}

		// size of list after intersections with volumes have been added
		int re = list.size;

		// in indicates if the resulting csg volume has been entered
		boolean in = firstInside && (inside == 0);

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

			if (vol == 0)
			{
				firstInside = is.type == Intersection.ENTERING;
			}
			else
			{
				if (is.type == Intersection.ENTERING)
				{
					inside++;
				}
				else
				{
					inside--;
				}
			}

			if ((firstInside && (inside == 0)) != in)
			{
				// csg volume entered or left
				list.addSwap (idx);
				in = !in;
				is.solid = this;
				is.type = in ? Intersection.ENTERING : Intersection.LEAVING;
				if (vol > 0)
				{
					is.negateNormal ();
				}

				if (which != Intersection.ALL)
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

	@Override
	public Volume operator$sub (Volume v)
	{
		CSGDifference c = new CSGDifference ();
		c.volumes.addAll (volumes);
		c.volumes.add (v);
		return c;
	}

	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		volumes.get (0).getExtent (min, max, temp);
	}

}
