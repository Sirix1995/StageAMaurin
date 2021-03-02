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

import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import de.grogra.vecmath.Math2;

/**
 * This class represents the geometry of a square. In local
 * object coordinates, the square consists of all points
 * which fulfill <code>0 <= x <= 1, 0 <= y <= 1, z = 0</code>.
 * 
 * @author Ole Kniemeyer
 */
public class Square extends TransformableVolume
{
	/**
	 * Scaling factor for u coordinate: u coordinates
	 * range from 0 to scaleU.
	 */
	public float scaleU = 1;

	/**
	 * Scaling factor for v coordinate: v coordinates
	 * range from 0 to scaleV.
	 */
	public float scaleV = 1;

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		if ((excludeStart != null) && (excludeStart.volume == this))
		{
			return false;
		}
		if ((excludeEnd != null) && (excludeEnd.volume == this))
		{
			return false;
		}

		Point3d o;
		Vector3d d;
		transformPoint (line.origin, o = list.tmpPoint0);
		transformVector (line.direction, d = list.tmpVector0);

		if (d.z != 0)
		{
			double t = -o.z / d.z;
			if ((line.start <= t) && (t <= line.end))
			{
				d.scaleAdd (t, o);
				if ((d.x >= 0) && (d.x <= 1) && (d.y >= 0) && (d.y <= 1))
				{
					list.add (this, line, t, Intersection.PASSING);
				}
			}
		}

		return false;
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		return false;
	}

	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		min.set (Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
			Double.POSITIVE_INFINITY);
		max.set (Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
			Double.NEGATIVE_INFINITY);
		Point3d p = temp.tmpPoint0;
		Point3d q = temp.tmpPoint1;
		p.z = 0;
		for (int x = 0; x <= 1; x ++)
		{
			p.x = x;
			for (int y = 0; y <= 1; y ++)
			{
				p.y = y;
				invTransformPoint (p, q);
				Math2.min (min, q);
				Math2.max (max, q);
			}
		}
	}

	public boolean boxContainsBoundary (BoundingBox box, Tuple3d center,
			double radius, Variables temp)
	{
		Point3d p = temp.tmpPoint0;
		Point3d q = temp.tmpPoint1;
		Point3d r = temp.tmpPoint2;
		Point3d a = temp.tmpPoint3;

		Vector3d n = temp.tmpVector1;
		Vector3d v = temp.tmpVector2;

		Matrix3d m3 = temp.tmpMatrix3;

		double t = radius * getFrobeniusNorm () + 0.707106781187;
		transformPoint (center, v);
		v.x -= 0.5;
		v.y -= 0.5;
		if (v.lengthSquared () > t * t)
		{
			// bounding sphere of box does not intersect bounding sphere of this square
			return false;
		}

		Matrix3d m = getObjectToWorldRotationScale ();

		a.x = t0;
		a.y = t1;
		a.z = t2;

		p.x = m.m00 + t0;
		p.y = m.m10 + t1;
		p.z = m.m20 + t2;

		q.x = m.m00 + m.m01 + t0;
		q.y = m.m10 + m.m11 + t1;
		q.z = m.m20 + m.m21 + t2;

		r.x = m.m01 + t0;
		r.y = m.m11 + t1;
		r.z = m.m21 + t2;

		return box.testParallelogram (p, q, r, a, m3, n, v) == BoundingBox.INTERSECTION;
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
		uv.x = n.x * scaleU;
		uv.y = n.y * scaleV;
	}

	public void computeTangents (Intersection is, Vector3d dpdu, Vector3d dpdv)
	{
		dpdu.set (1 / scaleU, 0, 0);
		dpdv.set (0, 1 / scaleV, 0);
		getObjectToWorldRotationScale ().transform (dpdu);
		getObjectToWorldRotationScale ().transform (dpdv);
	}

}
