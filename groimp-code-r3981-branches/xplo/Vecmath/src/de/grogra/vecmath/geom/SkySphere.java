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
 * This class represents a sky sphere. The direction vector in local
 * coordinates is used to compute an intersection at infinity.
 * 
 * @author Ole Kniemeyer
 */
public class SkySphere extends TransformableVolume
{

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		if (line.end >= Double.POSITIVE_INFINITY)
		{
			list.add (this, line, Double.POSITIVE_INFINITY, Intersection.PASSING);
		}
		return false;
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		return false;
	}

	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		min.set (Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		max.set (Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	public boolean boxContainsBoundary (BoundingBox box, Tuple3d center,
			double radius, Variables temp)
	{
		return false;
	}

	public void computeNormal (Intersection is, Vector3d normal)
	{
		// store the local direction in normal
		transformVector (is.line.direction, normal);

		// transform normal vector to global coordinates 
		transformTranspose (normal, normal);

		normal.normalize ();
		normal.negate ();
	}

	public void computeUV (Intersection is, Vector2d uv)
	{
		Vector3d n = is.tmpVector0;
		transformVector (is.line.direction, n);
		n.normalize ();
		n.y = -n.y;
		Sphere.getUV (n, uv);
	}

	public void computeTangents (Intersection is, Vector3d dpdu, Vector3d dpdv)
	{
		Vector3d n = is.tmpVector0;
		transformVector (is.line.direction, n);
		n.normalize ();
		n.y = -n.y;
		Sphere.getTangents (n, getObjectToWorldRotationScale (), dpdu, dpdv);
	}

}
