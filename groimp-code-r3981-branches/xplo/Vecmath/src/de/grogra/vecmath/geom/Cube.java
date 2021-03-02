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
 * This class represents the geometry of a cube. In local object
 * coordinates, it is axis-aligned with coordinates between
 * (-1, -1, -1) and (1, 1, 1).
 * 
 * @author Ole Kniemeyer
 */
public class Cube extends TransformableVolume
{

	/**
	 * Face index indicating the left face (direction -x)
	 */
	public static final int LEFT = 0;

	/**
	 * Face index indicating the right face (direction +x)
	 */
	public static final int RIGHT = 1;

	/**
	 * Face index indicating the front face (direction -y)
	 */
	public static final int FRONT = 2;

	/**
	 * Face index indicating the back face (direction +y)
	 */
	public static final int BACK = 3;

	/**
	 * Face index indicating the bottom face (direction -z)
	 */
	public static final int BOTTOM = 4;

	/**
	 * Face index indicating the top face (direction +z)
	 */
	public static final int TOP = 5;

	private static final Point3d MIN = new Point3d (-1, -1, -1);
	private static final Point3d MAX = new Point3d (1, 1, 1);

	private static final Point3d[] CORNERS = new Point3d[] {
			new Point3d (-1, -1, -1), new Point3d (1, -1, -1),
			new Point3d (1, -1, 1), new Point3d (1, 1, 1),
			new Point3d (-1, 1, 1), new Point3d (-1, 1, -1),
			new Point3d (-1, -1, -1), new Point3d (1, -1, -1)};

	private static final Point3d MIN_XY_MAX_Z = new Point3d (-1, -1, 1);
	private static final Point3d MAX_XY_MIN_Z = new Point3d (1, 1, -1);

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		int startFace = ((excludeStart != null) && (excludeStart.volume == this)) ? excludeStart.face
				: -1;
		int endFace = ((excludeEnd != null) && (excludeEnd.volume == this)) ? excludeEnd.face
				: -1;

		double ox = line.origin.x;
		double oy = line.origin.y;
		double oz = line.origin.z;

		double dx = line.direction.x;
		double dy = line.direction.y;
		double dz = line.direction.z;

		transformPoint (line.origin, line.origin);
		transformVector (line.direction, line.direction);

		boolean result = BoundingBox.computeIntersections (this, MIN, MAX,
			line, which == Intersection.ALL, list, startFace, endFace);

		line.origin.set (ox, oy, oz);
		line.direction.set (dx, dy, dz);

		return result;
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		double x = point.x - t0;
		double y = point.y - t1;
		double z = point.z - t2;
		double t = m00 * x + m01 * y + m02 * z;
		if (open ? ((t <= -1) || (t >= 1)) : ((t < -1) || (t > 1)))
		{
			return false;
		}
		t = m10 * x + m11 * y + m12 * z;
		if (open ? ((t <= -1) || (t >= 1)) : ((t < -1) || (t > 1)))
		{
			return false;
		}
		t = m20 * x + m21 * y + m22 * z;
		if (open ? ((t <= -1) || (t >= 1)) : ((t < -1) || (t > 1)))
		{
			return false;
		}
		return true;
	}

	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		min.set (Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
			Double.POSITIVE_INFINITY);
		max.set (Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
			Double.NEGATIVE_INFINITY);
		Point3d p = temp.tmpPoint0;
		Point3d q = temp.tmpPoint1;
		for (int x = -1; x <= 1; x += 2)
		{
			p.x = x;
			for (int y = -1; y <= 1; y += 2)
			{
				p.y = y;
				for (int z = -1; z <= 1; z += 2)
				{
					p.z = z;
					invTransformPoint (p, q);
					Math2.min (min, q);
					Math2.max (max, q);
				}
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
		Vector3d b = temp.tmpVector0;

		Vector3d n = temp.tmpVector1;
		Vector3d v = temp.tmpVector2;

		Matrix3d m3 = temp.tmpMatrix3;

		double t = radius * getFrobeniusNorm () + 1.732050807568878;
		transformPoint (center, v);
		if (v.lengthSquared () > t * t)
		{
			// bounding sphere of box does not intersect bounding sphere of this cube
			return false;
		}

		invTransformPoint (CORNERS[0], p);
		invTransformPoint (CORNERS[1], q);
		invTransformPoint (MIN_XY_MAX_Z, a);
		invTransformPoint (MAX_XY_MIN_Z, b);
		for (int f = 2; f < 8; f++)
		{
			invTransformPoint (CORNERS[f], r);
			int test = ((f & 1) == 0) ? box.testParallelogram (p, q, r, a, m3,
				n, v) : box.testParallelogram (q, p, b, r, m3, n, v);
			switch (test)
			{
				case BoundingBox.INTERSECTION:
					return true;
				case BoundingBox.ABOVE:
					return false;
			}
			Point3d swap = p;
			p = q;
			q = r;
			r = swap;
		}

		return false;
	}

	static void getNormal (int face, Vector3d normal)
	{
		switch (face)
		{
			case LEFT:
				normal.set (-1, 0, 0);
				break;
			case RIGHT:
				normal.set (1, 0, 0);
				break;
			case FRONT:
				normal.set (0, -1, 0);
				break;
			case BACK:
				normal.set (0, 1, 0);
				break;
			case BOTTOM:
				normal.set (0, 0, -1);
				break;
			case TOP:
				normal.set (0, 0, 1);
				break;
			default:
				throw new IllegalArgumentException ();
		}
	}

	public void computeNormal (Intersection is, Vector3d normal)
	{
		getNormal (is.face, normal);
		transformTranspose (normal, normal);
		normal.normalize ();
	}

	static void getUV (int face, Tuple3d point, Vector2d uv)
	{
		double u;
		double v;
		switch (face)
		{
			case LEFT:
				u = 0.125 * (point.z + 1);
				v = (1d / 6) * (point.y + 3);
				break;
			case RIGHT:
				u = -0.125 * (point.z - 5);
				v = (1d / 6) * (point.y + 3);
				break;
			case FRONT:
				u = 0.125 * (point.x + 3);
				v = (1d / 6) * (point.z + 1);
				break;
			case BACK:
				u = 0.125 * (point.x + 3);
				v = (-1d / 6) * (point.z - 5);
				break;
			case BOTTOM:
				u = -0.125 * (point.x - 7);
				v = (1d / 6) * (point.y + 3);
				break;
			case TOP:
				u = 0.125 * (point.x + 3);
				v = (1d / 6) * (point.y + 3);
				break;
			default:
				throw new IllegalArgumentException ();
		}
		uv.set ((u < 0) ? 0 : (u > 1) ? 1 : u, (v < 0) ? 0 : (v > 1) ? 1 : v);
	}

	public void computeUV (Intersection is, Vector2d uv)
	{
		Vector3d p = is.tmpVector0;
		transformPoint (is.getPoint (), p);
		getUV (is.face, p, uv);
	}

	static void getTangents (int face, Vector3d dpdu, Vector3d dpdv)
	{
		switch (face)
		{
			case LEFT:
				dpdu.set (0, 0, 8);
				dpdv.set (0, 6, 0);
				break;
			case RIGHT:
				dpdu.set (0, 0, -8);
				dpdv.set (0, 6, 0);
				break;
			case FRONT:
				dpdu.set (8, 0, 0);
				dpdv.set (0, 0, 6);
				break;
			case BACK:
				dpdu.set (8, 0, 0);
				dpdv.set (0, 0, -6);
				break;
			case BOTTOM:
				dpdu.set (-8, 0, 0);
				dpdv.set (0, 6, 0);
				break;
			case TOP:
				dpdu.set (8, 0, 0);
				dpdv.set (0, 6, 0);
				break;
			default:
				throw new IllegalArgumentException ();
		}
	}

	public void computeTangents (Intersection is, Vector3d dpdu, Vector3d dpdv)
	{
		getTangents (is.face, dpdu, dpdv);
		getObjectToWorldRotationScale ().transform (dpdu);
		getObjectToWorldRotationScale ().transform (dpdv);
	}
}
