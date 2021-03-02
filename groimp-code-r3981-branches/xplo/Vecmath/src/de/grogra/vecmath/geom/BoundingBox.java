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
 * This class represents an axis-aligned bounding box. It contains
 * all points between the corners <code>min</code> and
 * <code>max</code>.
 * 
 * @author Ole Kniemeyer
 */
public class BoundingBox extends VolumeBase
{
	/**
	 * The minimum coordinates of this box.
	 */
	public Point3d min;

	/**
	 * The maximum coordinates of this box.
	 */
	public Point3d max;

	public BoundingBox (Point3d min, Point3d max)
	{
		this.min = min;
		this.max = max;
	}

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		int startFace = ((excludeStart != null) && (excludeStart.volume == this)) ? excludeStart.face
				: -1;
		int endFace = ((excludeEnd != null) && (excludeEnd.volume == this)) ? excludeEnd.face
				: -1;
		return computeIntersections (this, min, max, line,
			which == Intersection.ALL, list, startFace, endFace);
	}

	/**
	 * Computes the intersections (or the first thereof, if
	 * <code>all</code> is <code>false</code>)
	 * between the faces of the bounding box <code>[min, max]</code>
	 * and the specified <code>line</code>, see also the general
	 * method {@link Volume#computeIntersections}.
	 * <p>
	 * If <code>excludeStartFace</code> is non-negative, it has to specify
	 * one of the faces of the box, see {@link Cube#LEFT}. The
	 * intersection of the line and the specified face is excluded
	 * from the set of computed intersections. In this case the starting
	 * point of the <code>line</code> has to lie on the specified
	 * face. 
	 * <p>
	 * If <code>excludeEndFace</code> is non-negative, the same
	 * treatment as for <code>excludeStartFace</code> applies with the
	 * requirement, that the end point of the <code>line</code> has to lie
	 * on the specified face.
	 * 
	 * @param box the box
	 * @param min minimum coordinates of box
	 * @param max maximum coordinates of box
	 * @param line a line
	 * @param all <code>true</code> for all intersections,
	 * <code>false</code> for only the first (closest)
	 * intersection has to be added to <code>list</code>
	 * @param list the intersections are added to this list
	 * @param excludeStartFace face which shall be excluded at start, or <code>-1</code>
	 * @param excludeEndFace face which shall be excluded at end, or <code>-1</code>
	 * 
	 * @return <code>true</code> iff the beginning of the line lies
	 * within the box (i.e., if the line starts within the box or
	 * enters the box at the starting point)
	 */
	public static boolean computeIntersections (Volume box, Tuple3d min,
			Tuple3d max, Line line, boolean all, IntersectionList list,
			int excludeStartFace, int excludeEndFace)
	{
		if ((excludeStartFace >= 0) && (excludeEndFace >= 0))
		{
			return true;
		}

		double enter = line.start;
		double leave = line.end;
		double t2;
		int leftFace = -1;
		int enteredFace = -1;

		if (line.direction.x > 0)
		{
			if ((excludeStartFace == Cube.RIGHT)
				|| (excludeEndFace == Cube.LEFT))
			{
				return false;
			}
			if (excludeEndFace < 0)
			{
				t2 = (max.x - line.origin.x) / line.direction.x;
				if (t2 < leave)
				{
					if (t2 < enter)
					{
						return false;
					}
					leave = t2;
					leftFace = Cube.RIGHT;
				}
			}
			if (excludeStartFace < 0)
			{
				t2 = (min.x - line.origin.x) / line.direction.x;
				if (t2 > enter)
				{
					if (t2 > leave)
					{
						return false;
					}
					enter = t2;
					enteredFace = Cube.LEFT;
				}
			}
		}
		else if (line.direction.x < 0)
		{
			if ((excludeStartFace == Cube.LEFT)
				|| (excludeEndFace == Cube.RIGHT))
			{
				return false;
			}
			if (excludeEndFace < 0)
			{
				t2 = (min.x - line.origin.x) / line.direction.x;
				if (t2 < leave)
				{
					if (t2 < enter)
					{
						return false;
					}
					leave = t2;
					leftFace = Cube.LEFT;
				}
			}
			if (excludeStartFace < 0)
			{
				t2 = (max.x - line.origin.x) / line.direction.x;
				if (t2 > enter)
				{
					if (t2 > leave)
					{
						return false;
					}
					enter = t2;
					enteredFace = Cube.RIGHT;
				}
			}
		}
		else if ((line.origin.x > max.x) || (line.origin.x < min.x))
		{
			return false;
		}

		if (line.direction.y > 0)
		{
			if ((excludeStartFace == Cube.BACK)
				|| (excludeEndFace == Cube.FRONT))
			{
				return false;
			}
			if (excludeEndFace < 0)
			{
				t2 = (max.y - line.origin.y) / line.direction.y;
				if (t2 < leave)
				{
					if (t2 < enter)
					{
						return false;
					}
					leave = t2;
					leftFace = Cube.BACK;
				}
			}
			if (excludeStartFace < 0)
			{
				t2 = (min.y - line.origin.y) / line.direction.y;
				if (t2 > enter)
				{
					if (t2 > leave)
					{
						return false;
					}
					enter = t2;
					enteredFace = Cube.FRONT;
				}
			}
		}
		else if (line.direction.y < 0)
		{
			if ((excludeStartFace == Cube.FRONT)
				|| (excludeEndFace == Cube.BACK))
			{
				return false;
			}
			if (excludeEndFace < 0)
			{
				t2 = (min.y - line.origin.y) / line.direction.y;
				if (t2 < leave)
				{
					if (t2 < enter)
					{
						return false;
					}
					leave = t2;
					leftFace = Cube.FRONT;
				}
			}
			if (excludeStartFace < 0)
			{
				t2 = (max.y - line.origin.y) / line.direction.y;
				if (t2 > enter)
				{
					if (t2 > leave)
					{
						return false;
					}
					enter = t2;
					enteredFace = Cube.BACK;
				}
			}
		}
		else if ((line.origin.y > max.y) || (line.origin.y < min.y))
		{
			return false;
		}

		if (line.direction.z > 0)
		{
			if ((excludeStartFace == Cube.TOP)
				|| (excludeEndFace == Cube.BOTTOM))
			{
				return false;
			}
			if (excludeEndFace < 0)
			{
				t2 = (max.z - line.origin.z) / line.direction.z;
				if (t2 < leave)
				{
					if (t2 < enter)
					{
						return false;
					}
					leave = t2;
					leftFace = Cube.TOP;
				}
			}
			if (excludeStartFace < 0)
			{
				t2 = (min.z - line.origin.z) / line.direction.z;
				if (t2 > enter)
				{
					if (t2 > leave)
					{
						return false;
					}
					enter = t2;
					enteredFace = Cube.BOTTOM;
				}
			}
		}
		else if (line.direction.z < 0)
		{
			if ((excludeStartFace == Cube.BOTTOM)
				|| (excludeEndFace == Cube.TOP))
			{
				return false;
			}
			if (excludeEndFace < 0)
			{
				t2 = (min.z - line.origin.z) / line.direction.z;
				if (t2 < leave)
				{
					if (t2 < enter)
					{
						return false;
					}
					leave = t2;
					leftFace = Cube.BOTTOM;
				}
			}
			if (excludeStartFace < 0)
			{
				t2 = (max.z - line.origin.z) / line.direction.z;
				if (t2 > enter)
				{
					if (t2 > leave)
					{
						return false;
					}
					enter = t2;
					enteredFace = Cube.TOP;
				}
			}
		}
		else if ((line.origin.z > max.z) || (line.origin.z < min.z))
		{
			return false;
		}

		if (enteredFace >= 0)
		{
			list.add (box, line, enter, Intersection.ENTERING, enteredFace);
		}
		if ((all || (enteredFace < 0)) && (leftFace >= 0))
		{
			list.add (box, line, leave, Intersection.LEAVING, leftFace);
		}
		return enteredFace < 0;
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		if (open)
		{
			return Math2.lessThan (min, point) && Math2.lessThan (point, max);
		}
		else
		{
			return Math2.lessThanOrEqual (min, point)
				&& Math2.lessThanOrEqual (point, max);
		}
	}

	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		min.set (this.min);
		max.set (this.max);
	}

	public boolean boxContainsBoundary (BoundingBox box, Tuple3d center,
			double radius, Variables temp)
	{
		if (!Math2.lessThanOrEqual (box.min, max)
			|| !Math2.lessThanOrEqual (min, box.max))
		{
			// boxes have an empty intersection
			return false;
		}

		if (Math2.lessThan (min, box.min) && Math2.lessThan (box.max, max))
		{
			// box contained in the open set of this bounding box
			return false;
		}

		return true;
	}

	public void computeNormal (Intersection is, Vector3d normal)
	{
		Cube.getNormal (is.face, normal);
	}

	public void computeUV (Intersection is, Vector2d uv)
	{
		Point3d p = is.tmpPoint0;
		p.add (min, max);
		p.scaleAdd (-0.5, is.getPoint ());
		p.x *= 2 / (max.x - min.x);
		p.y *= 2 / (max.y - min.y);
		p.z *= 2 / (max.z - min.z);
		Cube.getUV (is.face, p, uv);
	}

	public void computeTangents (Intersection is, Vector3d dpdu, Vector3d dpdv)
	{
		Cube.getTangents (is.face, dpdu, dpdv);
	}

	/**
	 * Return value for {@link #testParallelogram} indicating an intersection.
	 */
	public static final int INTERSECTION = -1;

	/**
	 * Return value for {@link #testParallelogram} indicating no intersection.
	 */
	public static final int NO_INTERSECTION = 0;

	/**
	 * Return value for {@link #testParallelogram} indicating that this bounding box
	 * lies completely above the plane of the quad.
	 */
	public static final int ABOVE = 1;

	/**
	 * Return value for {@link #testParallelogram} indicating that this bounding box
	 * lies completely below the plane of the quad.
	 */
	public static final int BELOW = 2;

	/**
	 * Determines if the specified parallelogram intersects this bounding box.
	 * The parallelogram has to specified by the oriented list
	 * <code>(a, b, c, d)</code> of its corners.
	 * 
	 * @param a first corner of parallelogram
	 * @param b second corner of parallelogram
	 * @param c third corner of parallelogram
	 * @param d fourth corner of parallelogram
	 * @param inv this matrix will be used internally by the method
	 * @param normal this vector will be used internally by the method
	 * @param v this vector will be used internally by the method
	 * @return one of {@link #INTERSECTION}, {@link #NO_INTERSECTION},
	 * {@link #ABOVE}, {@link #BELOW}
	 */
	public int testParallelogram (Tuple3d a, Tuple3d b, Tuple3d c, Tuple3d d,
			Matrix3d inv, Vector3d normal, Vector3d v)
	{
		if (contains (a, false) || contains (b, false) || contains (c, false)
			|| contains (d, false))
		{
			return INTERSECTION;
		}

		// all parallelogram corners are outside of this box

		normal.sub (b, a);
		v.sub (d, a);
		inv.setColumn (0, normal);
		inv.setColumn (1, v);

		normal.cross (normal, v);
		inv.setColumn (2, normal);

		boolean above = false;
		boolean below = false;

		int sideBits = 0;

		// loop over all eight corners of this box
		testSide: for (int x = 0; x <= 1; x++)
		{
			double sx = (((x == 0) ? min.x : max.x) - a.x) * normal.x;
			for (int y = 0; y <= 1; y++)
			{
				double sy = (((y == 0) ? min.y : max.y) - a.y) * normal.y;
				for (int z = 0; z <= 1; z++)
				{
					double dot = sx + sy + (((z == 0) ? min.z : max.z) - a.z)
						* normal.z;
					if (dot > 0)
					{
						// box corner lies above parallelogram
						above = true;
						sideBits |= 1 << (x + (y << 1) + (z << 2));
					}
					else if (dot < 0)
					{
						// box corner lies below parallelogram
						below = true;
					}
				}
			}
		}

		if (above && !below)
		{
			return ABOVE;
		}
		if (below && !above)
		{
			return BELOW;
		}

		// now check if one of the edges of the parallelogram intersects the box

		// loop over four edges of parallelogram
		for (int i = 0; i < 4; i++)
		{
			double dx = b.x - a.x;
			double dy = b.y - a.y;
			double dz = b.z - a.z;

			for (int s = 0; s <= 1; s++)
			{
				Point3d p = (s == 0) ? min : max;
				double t;
				double w;

				t = (p.x - a.x) / dx;
				if ((0 <= t) && (t <= 1))
				{
					if ((min.y <= (w = a.y + t * dy)) && (w <= max.y)
						&& (min.z <= (w = a.z + t * dz)) && (w <= max.z))
					{
						return INTERSECTION;
					}
				}

				t = (p.y - a.y) / dy;
				if ((0 <= t) && (t <= 1))
				{
					if ((min.x <= (w = a.x + t * dx)) && (w <= max.x)
						&& (min.z <= (w = a.z + t * dz)) && (w <= max.z))
					{
						return INTERSECTION;
					}
				}

				t = (p.z - a.z) / dz;
				if ((0 <= t) && (t <= 1))
				{
					if ((min.x <= (w = a.x + t * dx)) && (w <= max.x)
						&& (min.y <= (w = a.y + t * dy)) && (w <= max.y))
					{
						return INTERSECTION;
					}
				}
			}

			Tuple3d swap = a;
			a = b;
			b = c;
			c = d;
			d = swap;
		}

		// all edges of parallelogram lie outside of this box

		// the remaining task is to check if diagonals of box intersect the parallelogram

		inv.invert ();

		double dx = max.x - min.x;
		double dy = max.y - min.y;
		double dz = max.z - min.z;

		// diagonal (---) to (+++)
		if ((((sideBits >> 0) ^ (sideBits >> 7)) & 1) != 0)
		{
			v.set (min.x, min.y, min.z);
			v.sub (a);

			double x = dx;
			double y = dy;
			double z = dz;

			double t = -normal.dot (v)
				/ (x * normal.x + y * normal.y + z * normal.z);
			v.x += t * x;
			v.y += t * y;
			v.z += t * z;
			inv.transform (v);
			if ((0 <= v.x) && (v.x <= 1) && (0 <= v.y) && (v.y <= 1))
			{
				return INTERSECTION;
			}
		}

		// diagonal (+--) to (-++)
		if ((((sideBits >> 1) ^ (sideBits >> 6)) & 1) != 0)
		{
			v.set (min.x + dx, min.y, min.z);
			v.sub (a);

			double x = -dx;
			double y = dy;
			double z = dz;

			double t = -normal.dot (v)
				/ (x * normal.x + y * normal.y + z * normal.z);
			v.x += t * x;
			v.y += t * y;
			v.z += t * z;
			inv.transform (v);
			if ((0 <= v.x) && (v.x <= 1) && (0 <= v.y) && (v.y <= 1))
			{
				return INTERSECTION;
			}
		}

		// diagonal (-+-) to (+-+)
		if ((((sideBits >> 2) ^ (sideBits >> 5)) & 1) != 0)
		{
			v.set (min.x, min.y + dy, min.z);
			v.sub (a);

			double x = dx;
			double y = -dy;
			double z = dz;

			double t = -normal.dot (v)
				/ (x * normal.x + y * normal.y + z * normal.z);
			v.x += t * x;
			v.y += t * y;
			v.z += t * z;
			inv.transform (v);
			if ((0 <= v.x) && (v.x <= 1) && (0 <= v.y) && (v.y <= 1))
			{
				return INTERSECTION;
			}
		}

		// diagonal (++-) to (--+)
		if ((((sideBits >> 3) ^ (sideBits >> 4)) & 1) != 0)
		{
			v.set (min.x + dx, min.y + dy, min.z);
			v.sub (a);

			double x = -dx;
			double y = -dy;
			double z = dz;

			double t = -normal.dot (v)
				/ (x * normal.x + y * normal.y + z * normal.z);
			v.x += t * x;
			v.y += t * y;
			v.z += t * z;
			inv.transform (v);
			if ((0 <= v.x) && (v.x <= 1) && (0 <= v.y) && (v.y <= 1))
			{
				return INTERSECTION;
			}
		}

		return NO_INTERSECTION;
	}

	public boolean contains (Tuple3d point, int excludedCoordinate)
	{
		switch (excludedCoordinate)
		{
			case 0:
				return (min.y <= point.y) && (point.y <= max.y)
					&& (min.z <= point.z) && (point.z <= max.z);
			case 1:
				return (min.x <= point.x) && (point.x <= max.x)
					&& (min.z <= point.z) && (point.z <= max.z);
			case 2:
				return (min.x <= point.x) && (point.x <= max.x)
					&& (min.y <= point.y) && (point.y <= max.y);
			default:
				throw new IllegalArgumentException ();
		}
	}

	@Override
	public String toString ()
	{
		return "BoundingBox[" + min + ',' + max + ']';
	}
}
