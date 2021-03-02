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
 * This class represents the geometry of a cone. In local object
 * coordinates, the tip is located at the origin, the axis points
 * in the z-direction, and the half opening angle is 45 degrees.
 * 
 * @author Ole Kniemeyer
 */
public class Cone extends FrustumBase
{

	/**
	 * The distance of the cone's base from the tip in local units.
	 */
	public double base;

	public boolean baseOpen;

	public boolean rotateUV;

	@Override
	public double getTop ()
	{
		return 0;
	}

	@Override
	public double getBase ()
	{
		return base;
	}

	public boolean isUVRotated ()
	{
		return rotateUV;
	}
	
	public boolean isBaseOpen ()
	{
		return baseOpen;
	}
	
	public boolean isTopOpen ()
	{
		return false;
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
			if (pz > base)
			{
				// p below cone's cap
				return EMPTY;
			}
			if (pz < getTop ())
			{
				// p above frustum's top
				return EMPTY;
			}
			// p between planes of cap and top
			u0 = Double.NEGATIVE_INFINITY;
			u1 = Double.POSITIVE_INFINITY;
		}
		else
		{
			u0 = (base - pz) / dz;
			u1 = (getTop () - pz) / dz;
			if (u1 < u0)
			{
				double t = u0;
				u0 = u1;
				u1 = t;
			}
		}
		// now [u0, u1] is the range such that o + ud lies between the planes of cap and top 

		// equation of cone
		double a = dx * dx + dy * dy - dz * dz;
		double b = px * dx + py * dy - pz * dz;
		double c = px * px + py * py - pz * pz;

		// equation to solve is: a u^2 + 2bu + c = 0

		double v0;
		double v1;

		if (a == 0)
		{
			if (b == 0)
			{
				if (c == 0)
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
				v0 = v1 = c / (-2 * b);
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

			if (a > 0)
			{
				// both intersections on same side of plane z=0
				v0 = -(t + b) / a;
				v1 = (t - b) / a;
			}
			else
			{
				// intersections on different sides of plane z=0
				if (dz > 0)
				{
					v0 = -(t + b) / a;
					v1 = Double.POSITIVE_INFINITY;
				}
				else
				{
					v0 = Double.NEGATIVE_INFINITY;
					v1 = (t - b) / a;
				}
			}
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

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		if ((excludeStart != null) && (excludeEnd != null)
			&& (excludeStart.volume == this) && (excludeEnd.volume == this))
		{
			// we already have two different intersections with cone
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
			if (p.z > base)
			{
				// p below cone's cap
				return false;
			}
			if (p.z < getTop ())
			{
				// p above frustum's top
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
			u0 = (base - p.z) / d.z;
			u1 = (getTop () - p.z) / d.z;
			if (u1 < u0)
			{
				double t = u0;
				u0 = u1;
				u1 = t;
				f0 = isTopOpen () ? -1 : TOP;
				f1 = baseOpen ? -1 : BASE;
			}
			else
			{
				f0 = baseOpen ? -1 : BASE;
				f1 = isTopOpen () ? -1 : TOP;
			}
		}
		// now [u0, u1] is the range such that p + ud lies between the planes of base and top 

		// equation of cone
		double a = d.x * d.x + d.y * d.y - d.z * d.z;
		double b = p.x * d.x + p.y * d.y - p.z * d.z;
		double c = p.x * p.x + p.y * p.y - p.z * p.z;

		// equation to solve is: a u^2 + 2bu + c = 0

		if (a == 0)
		{
			if (b == 0)
			{
				return false;
			}
			double u = c / (-2 * b);
			if (d.z > 0)
			{
				if (u > u0)
				{
					u0 = u;
					f0 = LATERAL;
				}
			}
			else if (u < u1)
			{
				u1 = u;
				f1 = LATERAL;
			}
		}
		else
		{
			double t = b * b - a * c;
			if (t <= 0)
			{
				if (a >= 0)
				{
					return false;
				}
				// line goes through (0, 0, 0)
				t = 0;
			}
			else
			{
				t = Math.sqrt (t);
			}

			if (a > 0)
			{
				// both intersections on same side of plane z=0
				double u = -(t + b) / a;
				if (u > u0)
				{
					u0 = u;
					f0 = LATERAL;
				}

				u = (t - b) / a;
				if (u < u1)
				{
					u1 = u;
					f1 = LATERAL;
				}
			}
			else
			{
				// intersections on different sides of plane z=0
				if (d.z > 0)
				{
					double u = -(t + b) / a;
					if (u > u0)
					{
						u0 = u;
						f0 = LATERAL;
					}
				}
				else
				{
					double u = (t - b) / a;
					if (u < u1)
					{
						u1 = u;
						f1 = LATERAL;
					}
				}
			}
		}

		if (u1 < u0)
		{
			return false;
		}

		return addConvexIntersections (u0, f0, u1, f1,
			!(baseOpen || isTopOpen ()), line, which, list, excludeStart,
			excludeEnd);
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		if (baseOpen || isTopOpen ())
		{
			return false;
		}
		double px = point.x - t0;
		double py = point.y - t1;
		double pz = point.z - t2;
		double z = m20 * px + m21 * py + m22 * pz;
		if (open ? ((z <= getTop ()) || (z >= base))
				: ((z < getTop ()) || (z > base)))
		{
			return false;
		}
		double x = m00 * px + m01 * py + m02 * pz;
		double y = m10 * px + m11 * py + m12 * pz;
		return open ? (x * x + y * y < z * z) : (x * x + y * y <= z * z);
	}

	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		double t = getTop ();
		if (t == 0)
		{
			getDiscExtent (base, base, min, max);
			if (t0 < min.x)
			{
				min.x = t0;
			}
			else if (t0 > max.x)
			{
				max.x = t0;
			}
			if (t1 < min.y)
			{
				min.y = t1;
			}
			else if (t1 > max.y)
			{
				max.y = t1;
			}
			if (t2 < min.z)
			{
				min.z = t2;
			}
			else if (t2 > max.z)
			{
				max.z = t2;
			}
		}
		else
		{
			getDiscsExtent (t, t, base, base, min, max);
		}
	}

	@Override
	boolean containsLocalPoint (Tuple3d point)
	{
		return (point.z >= getTop ()) && (point.z <= base) && (point.x * point.x + point.y * point.y - point.z * point.z <= 0);
	}

	@Override
	double getBaseRadiusSquared ()
	{
		return base * base;
	}

	@Override
	double getTopRadiusSquared ()
	{
		return getTop () * getTop ();
	}

	@Override
	boolean intersectsBoundingSphere (Vector3d center, double radius)
	{
		double t = radius + 1.000001 * base;
		center.z -= base;
		return center.lengthSquared () <= t * t;
	}

	public void computeNormal (Intersection is, Vector3d normal)
	{
		switch (is.face)
		{
			case BASE:
				normal.set (0, 0, 1);
				break;
			case TOP:
				normal.set (0, 0, -1);
				break;
			case LATERAL:
				// store the local point in normal
				transformPoint (is.getPoint (), normal);
				normal.z = -normal.z;
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
				uv.x = 0.5 * (n.x / base + 1);
				uv.y = 0.5 * (n.y / base - 1);
				break;
			case TOP:
				uv.x = 0.5 * (n.x / getTop () + 1);
				uv.y = 0.5 * (1 - n.y / getTop ());
				break;
			case LATERAL:
				uv.x = (Math.atan2 (n.y, -n.x) + Math.PI) * (1 / (2 * Math.PI));
				uv.y = (n.z - getTop ()) / (base - getTop ());
				if (rotateUV)
				{
					uv.x = 1 - uv.x;
				}
				else
				{
					uv.y = 1 - uv.y;
				}
				uv.y *= scaleV;
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
				dpdu.set (2 * base, 0, 0);
				dpdv.set (0, 2 * base, 0);
				break;
			case TOP:
				dpdu.set (2 * getTop (), 0, 0);
				dpdv.set (0, -2 * getTop (), 0);
				break;
			case LATERAL:
				dpdu.set ((-2 * Math.PI) * n.y * n.z,
					(2 * Math.PI) * n.x * n.z, 0);
				dpdv.set (0, 0, (base - getTop ()) / scaleV);
				(rotateUV ? dpdu : dpdv).negate ();
				break;
			default:
				throw new IllegalArgumentException ();
		}
		Matrix3d m = getObjectToWorldRotationScale ();
		m.transform (dpdu);
		m.transform (dpdv);
	}

}
