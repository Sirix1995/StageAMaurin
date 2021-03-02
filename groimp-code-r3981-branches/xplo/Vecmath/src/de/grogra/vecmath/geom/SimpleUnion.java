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
 * An instance of this class represents a simple union of a list of volumes.
 * In constrast to {@link de.grogra.vecmath.geom.CSGUnion}, all intersections
 * of volume components are reported so that inner surfaces are not removed. 
 * 
 * @author Ole Kniemeyer
 */
public class SimpleUnion extends UnionBase
{

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

		// counts the number of volumes which have been entered
		boolean inside = false;

		double lineEnd = line.end;

		for (int i = 0; i < s; i++)
		{
			int begin = list.size;
			list.istack[index + i] = begin;
			inside |= volumes.get (i).computeIntersections (line,
				which, list, excludeStart, excludeEnd);
			list.istack[end + i] = list.size;
			if ((which != Intersection.ALL) && (list.size != begin))
			{
				if (which == Intersection.ANY)
				{
					return inside;
				}
				line.end = list.elements[begin].parameter;
			}
		}

		line.end = lineEnd;

		sortIntersections (which == Intersection.ALL, list, rs, s, index, end);

		return inside;
	}

}
