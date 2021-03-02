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

public class SensorDisc extends TransformableVolume
{

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
		Vector3d o;
		Vector3d d;
		transformPoint (line.origin, o = list.tmpVector1);
		transformVector (line.direction, d = list.tmpVector0);
		double t = -Math2.dot (o, d) / d.lengthSquared ();
		if ((line.start <= t) && (t <= line.end))
		{
			d.scaleAdd (t, o);
			if (d.lengthSquared () <= 1)
			{
				list.add (this, line, t, Intersection.PASSING);
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
		Vector3d min = temp.tmpVector1;
		Vector3d max = temp.tmpVector2;
		getExtent (min, max, temp);
		return Math2.lessThanOrEqual (box.min, max) && Math2.lessThanOrEqual (min, box.max);
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
	}

	public void computeTangents (Intersection is, Vector3d dpdu, Vector3d dpdv)
	{
	}

}
