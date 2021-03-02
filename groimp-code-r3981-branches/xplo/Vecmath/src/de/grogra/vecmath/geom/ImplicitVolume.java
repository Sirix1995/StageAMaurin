/*
 * Copyright (C) 2011 GroIMP Developer Team
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

import static de.grogra.vecmath.Range.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.LinkedList;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import de.grogra.vecmath.Math2;
import de.grogra.vecmath.Range;

/**
 * This abstract base class represents the geometry of an implicit function. 
 * In local object coordinates, its center is the origin, and its size is infinite.
 * 
 * @author Reinhard Hemmerling
 */
public abstract class ImplicitVolume extends TransformableVolume
{
	// define a class containing all variables that should be accessible
	// once per render thread
	protected static final class Locals
	{
		// temporary ranges (use within one expression to calculate result range)
		final Range tr0 = new Range();
		final Range tr1 = new Range();
		final Range tr2 = new Range();
		final Range tr3 = new Range();
		final Range tr4 = new Range();
		final Range tr5 = new Range();
		final Range tr6 = new Range();
		final Range tr7 = new Range();

		// result ranges
		final Range r0 = new Range();
		final Range r1 = new Range();
		final Range r2 = new Range();
		final Range r3 = new Range();
		final Range r4 = new Range();
		final Range r5 = new Range();
		final Range r6 = new Range();
		final Range r7 = new Range();
		
		// some other ranges
		final Range x = new Range();
		final Range y = new Range();
		final Range z = new Range();
		
		// for calculation of tangents
		final Matrix3d m = new Matrix3d();
	}
	
	// define a thread-local variable to access helper variables per thread
	protected final ThreadLocal<Locals> locals = new ThreadLocal<Locals>() {
		protected Locals initialValue() { return new Locals(); }
	};
	
	// implicit function of the shape
	// >0 outside, =0 surface, <0 inside
	protected abstract Range f(Range x, Range y, Range z);
	// example for unit sphere:
	//   Locals l = locals.get();
	//   l.r0.set(1);
    //   return sub(l.r1, add(l.tr0, mul(l.tr1, x, x), add(l.tr2, mul(l.tr3, y, y), mul(l.tr4, z, z))), l.r0);
	
	// implement the same implicit function as above, but for values instead of ranges
	protected abstract double f(double x, double y, double z);
	// example for unit sphere:
	//   return x*x + y*y + z*z - 1;

	// compute box with the segment of the line parameterised by t as diagonal
	private static void computeLineInterval(Line line, Range t, Range x, Range y, Range z)
	{
		double xa = line.origin.x + line.direction.x * t.a;
		double xb = line.origin.x + line.direction.x * t.b;
		double ya = line.origin.y + line.direction.y * t.a;
		double yb = line.origin.y + line.direction.y * t.b;
		double za = line.origin.z + line.direction.z * t.a;
		double zb = line.origin.z + line.direction.z * t.b;
		x.set(min(xa, xb), max(xa, xb));
		y.set(min(ya, yb), max(ya, yb));
		z.set(min(za, zb), max(za, zb));
	}
	
	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		// TODO use excludeStart and excludeEnd
		
		// obtain per-thread instances of helper variables
		Locals l = locals.get();

		final Range x = l.x;
		final Range y = l.y;
		final Range z = l.z;
		
		final LinkedList<Range> ranges = new LinkedList<Range>();
		ranges.add(new Range(line.start + 1e-4, line.start + 1e8));
		// TODO instead of 1e-4 use excludeStart instead
		// TODO instead of 1e8 calculate end of line somehow
		while (!ranges.isEmpty())
		{
			// get next range for t-parameter
			Range t = ranges.poll();
			// compute space interval for point on line
			computeLineInterval(line, t, x, y, z);
			// compute interval for implicit formula
			Range r = f(x, y, z);
			// if resulting interval does not contain the iso-value then there
			// is no intersection for this interval, otherwise one or more
			// intersections are in this interval
			if (r.a*r.b <= 0) {
				// calculate value of implicit function for t.a and t.b
				final Point3d p = list.tmpPoint0;
				p.scaleAdd(t.a, line.direction, line.origin);
				double fa = f(p.x, p.y, p.z);
				p.scaleAdd(t.b, line.direction, line.origin);
				double fb = f(p.x, p.y, p.z);
				// check if necessary accuracy is reached
				// and if t.a and t.b lie on different sides of the surface
				if (t.b - t.a < 1e-8) {
					// TODO instead of 1e-8 calculate some threshold
					// found intersection, so record it
					list.add(this, line, (t.a + t.b) / 2, fa < fb ? Intersection.LEAVING : Intersection.ENTERING);
					// stop checking the other ranges, if only first or any intersection is searched for
					if (which == Intersection.CLOSEST || which == Intersection.ANY)
						break;
				} else {
					// otherwise split interval into halves and add both into the ranges to check
					double m = (t.a + t.b) / 2;
//					ranges.addFirst(new Range(Math.nextUp(m), t.b));	// not <= jdk1.5
					ranges.addFirst(new Range(m + Math.ulp(m), t.b));	// alternative for jdk1.5 and above
					ranges.addFirst(new Range(t.a, m));
				}
			}
		}

		// is the starting point of the line in the volume ?
		final Point3d p = list.tmpPoint0;
		p.scaleAdd(line.start, line.direction, line.origin);
		return f(p.x, p.y, p.z) <= 0;
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		// transform point
		double px = point.x - t0;
		double py = point.y - t1;
		double pz = point.z - t2;
		double x = m00 * px + m01 * py + m02 * pz;
		double y = m10 * px + m11 * py + m12 * pz;
		double z = m20 * px + m21 * py + m22 * pz;

		// check if point is inside
		if (open)
			// open is true, so exclude surface
			return f(x, y, z) < 0;
		else
			// open is false, so include surface
			return f(x, y, z) <= 0;
	}

	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		// implicit surface is infinitely big
		min.set(Double.NEGATIVE_INFINITY, 
				Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY);
		max.set(Double.POSITIVE_INFINITY, 
				Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY);
	}

	public boolean boxContainsBoundary (BoundingBox box, Tuple3d center,
			double radius, Variables temp)
	{
		// always intersect with implicit surface
		return true;
	}

	public void computeNormal (Intersection is, Vector3d normal)
	{
		// approximate normal numerically
		
		// obtain intersection point coordinates
		Point3d p = is.getPoint();
		double x0 = p.x;
		double y0 = p.y;
		double z0 = p.z;
		
		// normal is the same as gradient of f(x,y,z)
		// centered difference approximation is:
		//     F'(x) = (F(x+h) - F(x-h)) / 2h + O(h^2)
		final double h = 1e-8;
		double dx = f(x0+h,y0,z0) - f(x0-h,y0,z0);
		double dy = f(x0,y0+h,z0) - f(x0,y0-h,z0);
		double dz = f(x0,y0,z0+h) - f(x0,y0,z0-h);

		normal.set(dx, dy, dz);
		normal.normalize ();
	}

	public void computeUV (Intersection is, Vector2d uv)
	{
		// TODO use spherical mapping for uv-coordinates ?
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
		// obtain per-thread instances of helper variables
		Locals l = locals.get();
		final Matrix3d m = l.m;
		
		final Vector3d n = is.tmpVector0;
		computeNormal(is, n);
		Math2.getOrthogonalBasis(n, m, false);
		Matrix3d xform = getObjectToWorldRotationScale();
		m.getColumn(0, dpdu);
		xform.transform(dpdu);
		m.getColumn(1, dpdv);
		xform.transform(dpdv);
	}

}
