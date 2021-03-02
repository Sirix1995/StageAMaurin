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
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import de.grogra.vecmath.Math2;

/**
 * This class represents the geometry of a sphere. In local object
 * coordinates, its center is the origin, its radius is 1.
 * 
 * @author Ole Kniemeyer
 */
public class Sphere extends TransformableVolume
{

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		if ((excludeStart != null) && (excludeEnd != null)
			&& (excludeStart.volume == this) && (excludeEnd.volume == this))
		{
			// we already have two different intersections with sphere
			return true;
		}

		Vector3d o;
		Vector3d d;
		transformPoint (line.origin, o = list.tmpVector1);
		transformVector (line.direction, d = list.tmpVector0);

		// equation of sphere
		double a = d.lengthSquared ();
		double b = o.dot (d);
		double c = o.lengthSquared () - 1;

		// equation to solve is: a u^2 + 2bu + c = 0

		double t = b * b - a * c;
		if (t <= 0)
		{
			return false;
		}
		t = Math.sqrt (t);

		// find both intersections such that u0 < u1
		double u0 = -(t + b) / a;
		double u1 = (t - b) / a;

		return addConvexIntersections (u0, 0, u1, 0, true, line, which, list,
			excludeStart, excludeEnd);
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		double px = point.x - t0;
		double py = point.y - t1;
		double pz = point.z - t2;
		double x = m00 * px + m01 * py + m02 * pz;
		double y = m10 * px + m11 * py + m12 * pz;
		double z = m20 * px + m21 * py + m22 * pz;
		double r2 = x * x + y * y + z * z;
		return open ? (r2 < 1) : (r2 <= 1);
	}

	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		Matrix3d m = getObjectToWorldRotationScale ();
		max.set (Math.sqrt (m.m00 * m.m00 + m.m01 * m.m01 + m.m02 * m.m02),
			Math.sqrt (m.m10 * m.m10 + m.m11 * m.m11 + m.m12 * m.m12), Math
				.sqrt (m.m20 * m.m20 + m.m21 * m.m21 + m.m22 * m.m22));
		Vector3d v = temp.tmpVector0;
		v.x = t0;
		v.y = t1;
		v.z = t2;
		min.sub (v, max);
		max.add (v);
	}

	public boolean boxContainsBoundary (BoundingBox box, Tuple3d center,
			double radius, Variables temp)
	{
		Vector3d p = temp.tmpVector0;
		Vector3d q = temp.tmpVector1;
		Vector3d d = temp.tmpVector2;

		double t = radius * getFrobeniusNorm () + 1;
		transformPoint (center, p);
		if (p.lengthSquared () > t * t)
		{
			// bounding sphere of box does not intersect this sphere
			return false;
		}

		Tuple3d min = box.min;
		Tuple3d max = box.max;

		boolean insideFound = false;
		boolean outsideFound = false;

		/* if the infinite line of an edge intersects the sphere boundary
		 * and its closest point to the center (which is, as a consequence,
		 * inside the sphere) lies on the edge, edgeInside is set to true
		 */
		boolean edgeInside = false;

		// loop over all 8 corners of box
		for (int x = 0; x <= 1; x++)
		{
			p.x = (x == 0) ? min.x : max.x;
			for (int y = 0; y <= 1; y++)
			{
				p.y = (y == 0) ? min.y : max.y;
				for (int z = 0; z <= 1; z++)
				{
					p.z = (z == 0) ? min.z : max.z;
					transformPoint (p, q);
					double q2 = q.lengthSquared ();
					if (q2 > 1)
					{
						if (insideFound)
						{
							return true;
						}
						else
						{
							outsideFound = true;
						}
					}
					else
					{
						if (outsideFound)
						{
							return true;
						}
						else
						{
							insideFound = true;
						}
					}

					if ((x == 0) && !edgeInside)
					{
						double d2 = max.x - min.x;
						d.set (d2 * m00, d2 * m10, d2 * m20);
						// d: x-edge vector in sphere coordinates
						double qd = q.dot (d);
						d2 = d.lengthSquared ();
						edgeInside = (qd <= 0) && (-qd <= d2)
							&& (q2 * d2 <= qd * qd + d2);
					}

					if ((y == 0) && !edgeInside)
					{
						double d2 = max.y - min.y;
						d.set (d2 * m01, d2 * m11, d2 * m21);
						// d: y-edge vector in sphere coordinates
						double qd = q.dot (d);
						d2 = d.lengthSquared ();
						edgeInside = (qd <= 0) && (-qd <= d2)
							&& (q2 * d2 <= qd * qd + d2);
					}

					if ((z == 0) && !edgeInside)
					{
						double d2 = max.z - min.z;
						d.set (d2 * m02, d2 * m12, d2 * m22);
						// d: z-edge vector in sphere coordinates
						double qd = q.dot (d);
						d2 = d.lengthSquared ();
						edgeInside = (qd <= 0) && (-qd <= d2)
							&& (q2 * d2 <= qd * qd + d2);
					}
				}
			}
		}
		if (!outsideFound)
		{
			// all corners of box are inside the sphere
			return false;
		}

		// all corners of box are outside the sphere

		if (edgeInside)
		{
			// an edge is inside the sphere, its corners are outside
			// => intersection with boundary
			return true;
		}

		// all edges of box are outside the sphere

		if ((min.x <= t0) && (t0 <= max.x) && (min.y <= t1)
			&& (t1 <= max.y) && (min.z <= t2) && (t2 <= max.z))
		{
			// center of sphere lies within box
			return true;
		}

		Matrix3d m = getObjectToWorldRotationScale ();

		// both for loops together iterate over the six faces (s, dir) of box
		for (int s = -1; s <= 1; s += 2)
		{
			transformPoint ((s < 0) ? min : max, p);
			// p: point on face (s, dir)
			for (int dir = 0; dir <= 2; dir++)
			{
				q.set ((dir == 0) ? s : 0, (dir == 1) ? s : 0, (dir == 2) ? s
						: 0);

				Math2.transformTranspose (m, q);
				// q: normal vector of face (s, dir)

				double pq = p.dot (q);
				if (pq < 0)
				{
					// center of sphere lies above face
					double q2 = q.lengthSquared ();
					if (pq * pq > q2)
					{
						// sphere lies completely above face (s, dir);
						return false;
					}
					else
					{
						q.scale (pq / q2);
						m.transform (q);
						q.x += t0;
						q.y += t1;
						q.z += t2;

						// now q is a point on plane of face and within the sphere
						// if it is really on the face, the box contains the boundary
						
						if (box.contains (q, dir))
						{
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public void computeNormal (Intersection is, Vector3d normal)
	{
		// store the local point in normal
		transformPoint (is.getPoint (), normal);
		// for a sphere, the local point equals the normal vector

		// transform normal vector to global coordinates 
		transformTranspose (normal, normal);

		normal.normalize ();
	}

	public void computeUV (Intersection is, Vector2d uv)
	{
		Vector3d n = is.tmpVector0;
		transformPoint (is.getPoint (), n);
		n.normalize ();
		getUV (n, uv);
	}

	static void getUV (Tuple3d point, Vector2d uv)
	{
		double cosv = point.z;
		double t = 1 - point.z * point.z;
		double sinv = (t <= 0) ? 0 : Math.sqrt (t);

		double cosu;
		double sinu;

		if (sinv == 0)
		{
			cosu = 1;
			sinu = 0;
		}
		else
		{
			cosu = point.x / sinv;
			sinu = point.y / sinv;
		}

		double u = Math.atan2 (sinu, cosu) * (1 / (2 * Math.PI));
		if (u < 0)
		{
			u += 1;
		}
		uv.set (u, Math.acos (-cosv) * (1 / Math.PI));
	}

	public void computeTangents (Intersection is, Vector3d dpdu, Vector3d dpdv)
	{
		Vector3d n = is.tmpVector0;
		transformPoint (is.getPoint (), n);
		getTangents (n, getObjectToWorldRotationScale (), dpdu, dpdv);
	}

	static void getTangents (Tuple3d point, Matrix3d xform, Vector3d dpdu,
			Vector3d dpdv)
	{
		dpdu.set ((-2 * Math.PI) * point.y, (2 * Math.PI) * point.x, 0);
		xform.transform (dpdu);
		double sin = Math.sqrt (point.x * point.x + point.y * point.y);
		if (sin > 0)
		{
			double cot = point.z / sin;
			dpdv.set (-Math.PI * point.x * cot, -Math.PI * point.y * cot,
				Math.PI * sin);
		}
		else
		{
			dpdv.set (-Math.PI, 0, 0);
		}
		xform.transform (dpdv);
	}

}
