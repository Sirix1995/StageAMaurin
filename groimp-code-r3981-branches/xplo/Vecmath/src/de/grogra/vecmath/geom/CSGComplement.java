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
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * An instance of this class represents the complement of a volume.
 * 
 * @author Ole Kniemeyer
 */
public class CSGComplement extends VolumeBase
{
	/**
	 * The volume from which the complement is computed.
	 */
	public Volume volume;

	public CSGComplement (Volume volume)
	{
		this.volume = volume;
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		return !volume.contains (point, !open);
	}

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		int s = list.size;
		boolean in = volume.computeIntersections (line, which, list,
			excludeStart, excludeEnd);
		while (s < list.size)
		{
			Intersection i = list.elements[s++];
			switch (i.type)
			{
				case Intersection.ENTERING:
					i.type = Intersection.LEAVING;
					i.negateNormal ();
					break;
				case Intersection.LEAVING:
					i.type = Intersection.ENTERING;
					i.negateNormal ();
					break;
			}
		}
		return !in;
	}

	@Override
	public Volume operator$com ()
	{
		return volume;
	}

	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		min.set (Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
			Double.NEGATIVE_INFINITY);
		max.set (Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
			Double.POSITIVE_INFINITY);
	}

	public boolean boxContainsBoundary (BoundingBox box, Tuple3d center,
			double radius, Variables temp)
	{
		return volume.boxContainsBoundary (box, center, radius, temp);
	}

	public void computeNormal (Intersection is, Vector3d normal)
	{
		throw new UnsupportedOperationException ();
	}

	public void computeUV (Intersection is, Vector2d uv)
	{
		throw new UnsupportedOperationException ();
	}

	public void computeTangents (Intersection is, Vector3d dpdu, Vector3d dpdv)
	{
		throw new UnsupportedOperationException ();
	}

}
