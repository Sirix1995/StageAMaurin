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

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * This class represents the geometry of a half space. In local
 * object coordinates, the half space contains all points
 * having a non-positive z-coordinate.
 * 
 * @author Ole Kniemeyer
 */
public class HalfSpace extends TransformableVolume
{

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		if ((excludeStart != null) && (excludeStart.volume == this))
		{
			return m20 * line.direction.x + m21 * line.direction.y + m22
				* line.direction.z <= 0;
		}
		if ((excludeEnd != null) && (excludeEnd.volume == this))
		{
			return m20 * line.direction.x + m21 * line.direction.y + m22
				* line.direction.z >= 0;
		}

		Point3d o;
		Vector3d d;
		transformPoint (line.origin, o = list.tmpPoint0);
		transformVector (line.direction, d = list.tmpVector0);

		boolean in = o.z <= 0;
		if (d.z != 0)
		{
			double t = -o.z / d.z;
			if ((line.start <= t) && (t <= line.end))
			{
				list.add (this, line, t, in ? Intersection.LEAVING
						: Intersection.ENTERING);
			}
		}

		return in;
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		double z = m20 * (point.x - t0) + m21 * (point.y - t1) + m22 * (point.z - t2);
		return open ? (z < 0) : (z <= 0);
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
		Tuple3d p = temp.tmpPoint0;
		Tuple3d q = temp.tmpPoint1;

		boolean aboveFound = false;
		boolean belowFound = false;
		for (int x = 0; x <= 1; x++)
		{
			p.x = (x == 0) ? box.min.x : box.max.x;
			for (int y = 0; y <= 1; y++)
			{
				p.y = (y == 0) ? box.min.y : box.max.y;
				for (int z = 0; z <= 1; z++)
				{
					p.z = (z == 0) ? box.min.z : box.max.z;
					transformPoint (p, q);
					if (q.z >= 0)
					{
						if (belowFound)
						{
							return true;
						}
						else
						{
							aboveFound = true;
						}
					}
					else
					{
						if (aboveFound)
						{
							return true;
						}
						else
						{
							belowFound = true;
						}
					}
				}
			}
		}
		return false;
	}

	public void computeNormal (Intersection is, Vector3d normal)
	{
		// set normal to (0,0,1), transformed by the transpose of matrix 
		normal.x = m20;
		normal.y = m21;
		normal.z = m22;
		normal.normalize ();
	}

	public void computeUV (Intersection is, Vector2d uv)
	{
		Vector3d n = is.tmpVector0;
		transformPoint (is.getPoint (), n);
		uv.x = n.x;
		uv.y = n.y;
	}

	public void computeTangents (Intersection is, Vector3d dpdu, Vector3d dpdv)
	{
		dpdu.set (1, 0, 0);
		dpdv.set (0, 1, 0);
		getObjectToWorldRotationScale ().transform (dpdu);
		getObjectToWorldRotationScale ().transform (dpdv);
	}

}
