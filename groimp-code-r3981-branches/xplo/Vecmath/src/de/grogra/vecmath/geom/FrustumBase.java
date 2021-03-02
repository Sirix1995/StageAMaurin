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
import javax.vecmath.Vector3d;

import de.grogra.vecmath.Math2;

/**
 * This class represents the geometry of a cone. In local object
 * coordinates, the tip is located at the origin, the axis points
 * in the z-direction, and the half opening angle is 45 degrees.
 * 
 * @author Ole Kniemeyer
 */
public abstract class FrustumBase extends TransformableVolume
{
	public static final int BASE = 0;
	public static final int TOP = 1;
	public static final int LATERAL = 2;

	/**
	 * Scaling factor for v coordinate: v coordinates of lateral
	 * face range from 0 to scaleV.
	 */
	public float scaleV = 1;

	abstract double getTop ();

	abstract double getBase ();

	/**
	 * Intersects this object with <code>line</code>. The result
	 * is will be written to line by a modification of
	 * {@link Line#start} and {@link Line#end}.
	 * 
	 * @param line the line to intersect with
	 * @return <code>true</code> iff the computed intersection is valid and not empty
	 */
	public boolean intersect (Line line)
	{
		Vector3d p = new Vector3d ();
		transformVector (line.direction, p);

		double dx = p.x;
		double dy = p.y;
		double dz = p.z;

		transformPoint (line.origin, p);

		return intersectEdge (line, p.x, p.y, p.z, dx, dy, dz) != EMPTY;
	}

	static final int EMPTY = 0;
	static final int CONTAINED = 1;
	static final int INTERSECTED = 2;

	abstract int intersectEdge (Line line, double px, double py, double pz, double dx,
			double dy, double dz);

	abstract boolean containsLocalPoint (Tuple3d point);

	abstract double getBaseRadiusSquared ();

	abstract double getTopRadiusSquared ();

	abstract boolean intersectsBoundingSphere (Vector3d center, double radius);

	public boolean boxContainsBoundary (BoundingBox box, Tuple3d center,
			double radius, Variables temp)
	{
		Tuple3d min = box.min;
		Tuple3d max = box.max;

		Vector3d p = temp.tmpVector0;
		Vector3d q = temp.tmpVector1;
		Vector3d v = temp.tmpVector2;

		Point3d r = temp.tmpPoint0;
 
		transformPoint (center, v);
		if (!intersectsBoundingSphere (v, radius * getFrobeniusNorm ()))
		{
			return false;
		}

		Line line = temp.tmpLine;

		boolean insideFound = false;
		boolean outsideFound = false;

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

					if (containsLocalPoint (q))
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
					else
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
					if (x == 0)
					{
						double d2 = max.x - min.x;
						line.start = 0;
						line.end = 1;
						if (intersectEdge (line, q.x, q.y, q.z, d2 * m00, d2
							* m10, d2 * m20) == INTERSECTED)
						{
							return true;
						}
					}

					if (y == 0)
					{
						double d2 = max.y - min.y;
						line.start = 0;
						line.end = 1;
						if (intersectEdge (line, q.x, q.y, q.z, d2 * m01, d2
							* m11, d2 * m21) == INTERSECTED)
						{
							return true;
						}
					}

					if (z == 0)
					{
						double d2 = max.z - min.z;
						line.start = 0;
						line.end = 1;
						if (intersectEdge (line, q.x, q.y, q.z, d2 * m02, d2
							* m12, d2 * m22) == INTERSECTED)
						{
							return true;
						}
					}
				}
			}
		}

		if (!outsideFound)
		{
			// all corners of box are inside the frustum
			return false;
		}

		// all corners of box are outside the frustum, no edge intersects the frustum
		// intersection may be on a face of the box

		Matrix3d m = getObjectToWorldRotationScale ();

		// center of frustum
		double t = 0.5 * (getBase () + getTop ());

		// convert to global coordinates
		p.set (m.m02 * t + t0, m.m12 * t + t1, m.m22 * t + t2);

		if (Math2.lessThanOrEqual (min, p) && Math2.lessThanOrEqual (p, max))
		{
			// center of frustum lies within box
			return true;
		}

		// center of frustum is outside the box

		// because no edge intersects the frustum, it suffices to test the top
		// and base faces and the center line for intersection against each face
		// of the box

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

				double qp = q.dot (p);
				if (q.z != 0)
				{
					// intersect center line with face
					t = qp / q.z;
					if ((t >= getTop ()) && (t <= getBase ()))
					{
						r.x = m.m02 * t + t0;
						r.y = m.m12 * t + t1;
						r.z = m.m22 * t + t2;
						// now r is a point on plane of face and on center
						// line of frustum, i.e., within the frustum.
						// If it is really on the face, the box contains
						// the boundary
						if (box.contains (r, dir))
						{
							return true;
						}
					}
				}

				// compute direction v of intersection line
				// between face and horizontal plane
				v.x = -q.y;
				v.y =  q.x;
				v.z = 0;

				boolean xDominant = Math.abs (q.x) > Math.abs (q.y);

				// loop over base and top of frustum
				for (int i = 0; i < 2; i++)
				{
					double r2 = (i == 0) ? getBaseRadiusSquared () : getTopRadiusSquared ();
					if (r2 == 0)
					{
						continue;
					}
					double z = (i == 0) ? getBase () : getTop ();

					// compute point r on intersection line
					// between face and plane of base or top
					if (xDominant)
					{
						r.x = (qp - z * q.z) / q.x;
						r.y = 0;
					}
					else
					{
						r.y = (qp - z * q.z) / q.y;
						r.x = 0;
					}
					r.z = z;

					// now the line r + s v is the intersection
					// between face and plane of base or top

					// intersect it with equation of border circle
					// of base or top, i.e., x^2 = radius^2
					// with x = r + s v
					double a = v.x * v.x + v.y * v.y;
					double b = v.x * r.x + v.y * r.y;
					double c = r.x * r.x + r.y * r.y - r2;

					t = b * b - a * c;

					if (t >= 0)
					{
						// we have two intersections at s1, s2
						// only compute mean of both
						r.scaleAdd (-b / a, v, r);
						// this is on the face iff the actual
						// intersections at s1, s2 are on the face
						// (because otherwise, an edge would
						// intersect the base or top, which we have
						// already excluded)
						m.transform (r);
						r.x += t0;
						r.y += t1;
						r.z += t2;
						if (box.contains (r, dir))
						{
							return true;
						}
					}
				}
			}
		}

		return false;
	}

}
