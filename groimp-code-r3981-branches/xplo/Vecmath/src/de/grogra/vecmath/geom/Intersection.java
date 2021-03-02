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
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * Instances of <code>Intersection</code> specify the properties
 * of an intersection point of a
 * {@link de.grogra.vecmath.geom.Line}
 * with the surface of a {@link de.grogra.vecmath.geom.Volume}.
 * They are part of an
 * {@link de.grogra.vecmath.geom.IntersectionList}.
 * 
 * @author Ole Kniemeyer
 */
public class Intersection
{
	/**
	 * Value for {@link #type} indicating that the line is leaving
	 * the solid volume at this intersection point.
	 */
	public static final int LEAVING = -1;

	/**
	 * Value for {@link #type} indicating that the line is entering
	 * the solid volume at this intersection poing.
	 */
	public static final int ENTERING = 1;

	/**
	 * Value for {@link #type} indicating that the line is passing
	 * the non-solid (infinitely thin) "volume" (in this case it is
	 * not a true volume, rather a surface) at this intersection point.
	 */
	public static final int PASSING = 0;

	/**
	 * Parameter for {@link Volume#computeIntersections} indicating
	 * that all intersections have to be found.
	 */
	public static final int ALL = 0;

	/**
	 * Parameter for {@link Volume#computeIntersections} indicating
	 * that only the closest intersection (minimal value of
	 * {@link #parameter}) has to be found.
	 */
	public static final int CLOSEST = 1;

	/**
	 * Parameter for {@link Volume#computeIntersections} indicating
	 * that some arbitrary intersection of the set of all
	 * intersections has to be found.
	 */
	public static final int ANY = 2;

	public Intersection (IntersectionList list)
	{
		tmpPoint0 = list.tmpPoint0;
		tmpVector0 = list.tmpVector0;
	}

	/**
	 * Defines the volume which computed this intersection point.
	 */
	public Volume volume;

	/**
	 * Defines the solid volume on whose surface this intersection point
	 * lies. Normally, it equals {@link #volume}, but for CSG operations,
	 * it is set to the resulting CSG volume.
	 */
	public Volume solid;

	/**
	 * Defines the line which was used for this intersection point.
	 */
	public Line line;

	/**
	 * The intersection point is located at
	 * <code>line.origin + parameter * line.direction</code>. Within an
	 * {@link IntersectionList}, intersections are sorted in ascending
	 * order of <code>parameter</code>.
	 */
	public double parameter;

	/**
	 * Indicates the type of intersection, one of {@link #ENTERING},
	 * {@link #LEAVING}, or {@link #PASSING}.
	 */
	public int type;

	/**
	 * This field can be used by the {@link #volume} to store
	 * additional information, e.g., a face index.
	 */
	public int face;

	/**
	 * This field can be used by the {@link #volume} to store
	 * additional information.
	 */
	public final Vector3d volumeVector = new Vector3d ();

	/**
	 * This field can be used by the {@link #volume} to store
	 * additional information.
	 */
	public Object volumeData;

	/**
	 * This point may be used freely in methods which perform
	 * computations based on this intersection.
	 */
	public Point3d tmpPoint0;

	/**
	 * This vector may be used freely in methods which perform
	 * computations based on this intersection.
	 */
	public Vector3d tmpVector0;

	/**
	 * This method can be invoked by CSG operations to indicate
	 * that the true normal vector is the negation of the vector which
	 * would be computed otherwise.
	 */
	public void negateNormal ()
	{
		if (normalValid)
		{
			throw new IllegalStateException ();
		}
		// flip the flag
		negateNormal = !negateNormal;
	}

	/**
	 * This method is invoked by {@link IntersectionList#add()} to reset
	 * the state of this intersection.
	 */
	void reset ()
	{
		volumeData = null;
		pointValid = false;
		normalValid = false;
		uvValid = false;
		tangentsValid = false;
		negateNormal = false;
	}

	/**
	 * <code>true</code> iff {@link #point} is valid.
	 */
	private boolean pointValid;

	/**
	 * The intersection point.
	 */
	private Point3d point;

	/**
	 * <code>true</code> iff the normal, computed by
	 * {@link Volume#computeNormal(Intersection, Vector3d)}, has to be
	 * negated.
	 */
	private boolean negateNormal;

	/**
	 * <code>true</code> iff {@link #normal} is valid.
	 */
	private boolean normalValid;

	/**
	 * The unit normal vector.
	 */
	private Vector3d normal;

	/**
	 * <code>true</code> iff {@link #uv} is valid.
	 */
	private boolean uvValid;

	/**
	 * The uv coordinates.
	 */
	private Vector2d uv;

	/**
	 * <code>true</code> iff {@link #uTangent} and {@link #vTangent}
	 * are valid.
	 */
	private boolean tangentsValid;

	/**
	 * The derivative of the surface position with respect to
	 * the u-coordinate.
	 */
	private Vector3d uTangent;

	/**
	 * The derivative of the surface position with respect to
	 * the v-coordinate.
	 */
	private Vector3d vTangent;

	/**
	 * Returns the intersection point in global coordinates. The
	 * returned value may not be modified.
	 * 
	 * @return intersection point
	 */
	public Point3d getPoint ()
	{
		if (!pointValid)
		{
			if (point == null)
			{
				point = new Point3d ();
			}
			point.scaleAdd (parameter, line.direction, line.origin);
			pointValid = true;
		}
		return point;
	}

	/**
	 * Returns the surface geometric normal unit vector in global
	 * coordinates. It is the outer normal vector, pointing from the
	 * surface outwards. The vector is computed once by
	 * {@link Volume#computeNormal(Intersection, Vector3d)}.
	 * The returned value may not be modified.
	 * 
	 * @return outer unit normal vector
	 */
	public Vector3d getNormal ()
	{
		if (!normalValid)
		{
			if (normal == null)
			{
				normal = new Vector3d ();
			}
			volume.computeNormal (this, normal);
			if (negateNormal)
			{
				normal.negate ();
			}
			normalValid = true;
		}
		return normal;
	}

	/**
	 * Returns the uv coordinates of this intersection point. It is
	 * computed once by {@link Volume#computeUV(Intersection, Vector2d)}.
	 * The returned value may not be modified.
	 * 
	 * @return uv coordinates
	 */
	public Vector2d getUV ()
	{
		if (!uvValid)
		{
			if (uv == null)
			{
				uv = new Vector2d ();
			}
			volume.computeUV (this, uv);
			uvValid = true;
		}
		return uv;
	}

	/**
	 * Returns the surface geometric normal unit vector in global
	 * coordinates. It is the outer normal vector, pointing from the
	 * surface outwards. The vector is computed once by
	 * {@link Volume#computeNormal(Intersection, Vector3d)}.
	 * The returned value may not be modified.
	 * 
	 * @return outer unit normal vector
	 */
	public Vector3d getUTangent ()
	{
		validateTangents ();
		return uTangent;
	}

	public Vector3d getVTangent ()
	{
		validateTangents ();
		return vTangent;
	}

	private void validateTangents ()
	{
		if (!tangentsValid)
		{
			if (uTangent == null)
			{
				uTangent = new Vector3d ();
			}
			if (vTangent == null)
			{
				vTangent = new Vector3d ();
			}
			volume.computeTangents (this, uTangent, vTangent);
			tangentsValid = true;
		}
	}
	
	public Intersection deepCopy(){
		Intersection retIns = new Intersection(new IntersectionList());
		
		retIns.face = this.face;
		retIns.line = this.line.deepCopy();
		retIns.negateNormal = this.negateNormal;
		retIns.normal = this.getNormal();
		retIns.normalValid = this.normalValid;
		retIns.parameter = this.parameter;
		retIns.point = this.getPoint();
		retIns.pointValid = this.pointValid;
		retIns.tangentsValid = this.tangentsValid;
		retIns.tmpPoint0.set(this.tmpPoint0);
		retIns.tmpVector0.set(this.tmpVector0);
		retIns.type = this.type;
		retIns.uTangent = this.getUTangent();
		retIns.uv = this.getUV();
		retIns.uvValid = this.uvValid;
		retIns.vTangent = this.getVTangent();
		
		//DeepCopy of this needed?
		retIns.solid = this.solid;
		retIns.volume = this.volume;
		retIns.volumeData = this.volumeData;
		retIns.volumeVector.set(this.volumeVector);
		
		
		return retIns;
	}

	@Override
	public String toString ()
	{
		return "{"
			+ volume
			+ ','
			+ parameter
			+ ((type == LEAVING) ? ",leaving"
					: (type == ENTERING) ? ",entering" : ",passing") + '}';
	}
}
