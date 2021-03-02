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

/**
 * This class represents the geometry of a cylinder. In local object
 * coordinates, the center is located at the origin, the axis points
 * in the z-direction, the radius is 1, and the cylinder's
 * z-coordinates extend from -1 to +1.
 * 
 * @author Ole Kniemeyer
 */
public class Cylinder extends FrustumBase
{
	public boolean baseOpen;

	public boolean topOpen;

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		if ((excludeStart != null) && (excludeEnd != null)
			&& (excludeStart.volume == this) && (excludeEnd.volume == this))
		{
			// we already have two different intersections with cylinder
			return true;
		}

		Vector3d d = list.tmpVector0;
		Point3d p = list.tmpPoint0;
		transformVector (line.direction, d);
		transformPoint (line.origin, p);

		double u0;
		int f0;
		double u1;
		int f1;
		if (d.z == 0)
		{
			// d horizontal
			if (p.z > 1)
			{
				// p above cylinder's top
				return false;
			}
			if (p.z < -1)
			{
				// p below cylinder's base
				return false;
			}
			// p between planes of base and top
			u0 = Double.NEGATIVE_INFINITY;
			u1 = Double.POSITIVE_INFINITY;
			f0 = -1;
			f1 = -1;
		}
		else
		{
			u0 = (-1 - p.z) / d.z;
			u1 = (1 - p.z) / d.z;
			if (u1 < u0)
			{
				double t = u0;
				u0 = u1;
				u1 = t;
				f0 = topOpen ? -1 : TOP;
				f1 = baseOpen ? -1 : BASE;
			}
			else
			{
				f0 = baseOpen ? -1 : BASE;
				f1 = topOpen ? -1 : TOP;
			}
		}
		// now [u0, u1] is the range such that p + ud lies between the planes of base and top 

		double dp = d.x * p.x + d.y * p.y;
		double p2 = p.x * p.x + p.y * p.y;
		double d2 = d.x * d.x + d.y * d.y;
		double t = dp * dp - d2 * (p2 - 1);
		if (t > 0)
		{
			t = Math.sqrt (t);

			double u;

			u = -(t + dp) / d2;
			if (u > u0)
			{
				u0 = u;
				f0 = LATERAL;
			}

			u = (t - dp) / d2;
			if (u < u1)
			{
				u1 = u;
				f1 = LATERAL;
			}
		}
		else if (p2 >= 1)
		{
			return false;
		}

		if (u1 < u0)
		{
			return false;
		}

		return addConvexIntersections (u0, f0, u1, f1, !(baseOpen || topOpen),
			line, which, list, excludeStart, excludeEnd);
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		if (baseOpen || topOpen)
		{
			return false;
		}
		double px = point.x - t0;
		double py = point.y - t1;
		double pz = point.z - t2;
		double z = m20 * px + m21 * py + m22 * pz;
		if (open ? ((z <= -1) || (z >= 1)) : ((z < -1) || (z > 1)))
		{
			return false;
		}
		double x = m00 * px + m01 * py + m02 * pz;
		double y = m10 * px + m11 * py + m12 * pz;
		return open ? (x * x + y * y < 1) : (x * x + y * y <= 1);
	}

	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		getDiscsExtent (-1, 1, 1, 1, min, max);
	}

	public void computeNormal (Intersection is, Vector3d normal)
	{
		switch (is.face)
		{
			case BASE:
				normal.set (0, 0, -1);
				break;
			case TOP:
				normal.set (0, 0, 1);
				break;
			case LATERAL:
				// store the local point in normal
				transformPoint (is.getPoint (), normal);
				normal.z = 0;
				break;
			default:
				throw new IllegalArgumentException ();
		}
		transformTranspose (normal, normal);
		normal.normalize ();
	}

	public void computeUV (Intersection is, Vector2d uv)
	{
		Vector3d n = is.tmpVector0;
		transformPoint (is.getPoint (), n);
		switch (is.face)
		{
			case BASE:
				uv.x = 0.5 * (n.x + 1);
				uv.y = 0.5 * (1 - n.y);
				break;
			case TOP:
				uv.x = 0.5 * (n.x + 1);
				uv.y = 0.5 * (n.y + 1);
				break;
			case LATERAL:
				uv.x = (Math.atan2 (-n.y, -n.x) + Math.PI)
					* (1 / (2 * Math.PI));
				uv.y = 0.5 * (n.z + 1) * scaleV;
				break;
			default:
				throw new IllegalArgumentException ();
		}
	}

	public void computeTangents (Intersection is, Vector3d dpdu, Vector3d dpdv)
	{
		Vector3d n = is.tmpVector0;
		transformPoint (is.getPoint (), n);
		switch (is.face)
		{
			case BASE:
				dpdu.set (2, 0, 0);
				dpdv.set (0, -2, 0);
				break;
			case TOP:
				dpdu.set (2, 0, 0);
				dpdv.set (0, 2, 0);
				break;
			case LATERAL:
				dpdu.set ((-2 * Math.PI) * n.y, (2 * Math.PI) * n.x, 0);
				dpdv.set (0, 0, 2 / scaleV);
				break;
			default:
				throw new IllegalArgumentException ();
		}
		Matrix3d m = getObjectToWorldRotationScale ();
		m.transform (dpdu);
		m.transform (dpdv);
	}

	@Override
	double getTop ()
	{
		return -1;
	}

	@Override
	double getBase ()
	{
		return 1;
	}

	@Override
	int intersectEdge (Line line, double px, double py, double pz, double dx,
			double dy, double dz)
	{
		double u0;
		double u1;
		if (dz == 0)
		{
			// d horizontal
			if ((pz > 1) || (pz < -1))
			{
				return EMPTY;
			}
			// p between planes of base and top
			u0 = Double.NEGATIVE_INFINITY;
			u1 = Double.POSITIVE_INFINITY;
		}
		else
		{
			u0 = (-1 - pz) / dz;
			u1 = (1 - pz) / dz;
			if (u1 < u0)
			{
				double t = u0;
				u0 = u1;
				u1 = t;
			}
		}
		// now [u0, u1] is the range such that o + ud lies between the planes of base and top 

		// equation of cylinder
		double a = dx * dx + dy * dy;
		double b = px * dx + py * dy;
		double c = px * px + py * py - 1;

		// equation to solve is: a u^2 + 2bu + c = 0

		double v0;
		double v1;

		if (a == 0)
		{
			if (c <= 0)
			{
				v0 = Double.NEGATIVE_INFINITY;
				v1 = Double.POSITIVE_INFINITY;
			}
			else
			{
				return EMPTY;
			}
		}
		else
		{
			double t = b * b - a * c;
			if (t < 0)
			{
				return EMPTY;
			}
			t = (t <= 0) ? 0 : Math.sqrt (t);

			v0 = -(t + b) / a;
			v1 = (t - b) / a;
		}

		// intersect [u0, u1] with [v0, v1]
		if (v0 > u0)
		{
			u0 = v0;
		}
		if (v1 < u1)
		{
			u1 = v1;
		}

		int intersected = 2;
		// intersect [u0, u1] with line
		if (line.start > u0)
		{
			u0 = line.start;
			intersected--;
		}
		if (line.end < u1)
		{
			u1 = line.end;
			intersected--;
		}
		if (u0 > u1)
		{
			return EMPTY;
		}

		line.start = u0;
		line.end = u1;
		return (intersected == 0) ? CONTAINED : INTERSECTED;
	}

	@Override
	boolean containsLocalPoint (Tuple3d point)
	{
		if ((point.z < -1) || (point.z > 1))
		{
			return false;
		}
		return point.x * point.x + point.y * point.y <= 1;
		
	}

	@Override
	double getBaseRadiusSquared ()
	{
		return 1;
	}

	@Override
	double getTopRadiusSquared ()
	{
		return 1;
	}

	@Override
	boolean intersectsBoundingSphere (Vector3d center, double radius)
	{
		double t = radius + 1.414213562374;
		return center.lengthSquared () <= t * t;
	}

}
