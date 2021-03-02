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
 * An instance of this class represents the intersection of a list of volumes.
 * 
 * @author Ole Kniemeyer
 */
public class CSGIntersection extends CompoundVolume
{

	/**
	 * Creates a new <code>CSGIntersection</code> whose list of volumes
	 * is empty.
	 */
	public CSGIntersection ()
	{
	}

	/**
	 * Creates a new <code>CSGIntersection</code> whose list of volumes
	 * is set to <code>[a, b]</code>.
	 */
	public CSGIntersection (Volume a, Volume b)
	{
		volumes.add (a);
		volumes.add (b);
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		for (int i = 0; i < volumes.size (); i++)
		{
			if (!volumes.get (i).contains (point, open))
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
		return computeIntersections (line, which == Intersection.ALL, list,
			excludeStart, excludeEnd, volumes.size ());
	}

	@Override
	public Volume operator$and (Volume v)
	{
		CSGIntersection c = new CSGIntersection ();
		c.volumes.addAll (volumes);
		c.volumes.add (v);
		return c;
	}

	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		double minX = Double.NEGATIVE_INFINITY;
		double minY = Double.NEGATIVE_INFINITY;
		double minZ = Double.NEGATIVE_INFINITY;
		double maxX = Double.POSITIVE_INFINITY;
		double maxY = Double.POSITIVE_INFINITY;
		double maxZ = Double.POSITIVE_INFINITY;
		for (int i = volumes.size () - 1; i >= 0; i--)
		{
			volumes.get (i).getExtent (min, max, temp);
			if (minX < min.x)
			{
				minX = min.x;
			}
			if (minY < min.y)
			{
				minY = min.y;
			}
			if (minZ < min.z)
			{
				minZ = min.z;
			}
			if (maxX > max.x)
			{
				maxX = max.x;
			}
			if (maxY > max.y)
			{
				maxY = max.y;
			}
			if (maxZ > max.z)
			{
				maxZ = max.z;
			}
		}
		min.set (minX, minY, minZ);
		max.set (maxX, maxY, maxZ);
	}

}
