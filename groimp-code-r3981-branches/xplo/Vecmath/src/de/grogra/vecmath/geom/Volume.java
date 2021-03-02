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

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * This interface represents three dimensional geometric
 * objects having a volume. 
 * 
 * @author Ole Kniemeyer
 */
public interface Volume
{

	/**
	 * Determines if the given <code>point</code> lies within this object.
	 * If <code>open</code> is <code>true</code>, the interior of the volume
	 * is considered (the largest open set contained in the volume,
	 * i.e., excluding the boundary), otherwise the closure of the volume.
	 * 
	 * @param point a point in global world coordinates
	 * @param open consider open or closed set
	 * @return <code>true</code> iff <code>point</code> is an element of the set
	 */
	boolean contains (Tuple3d point, boolean open);

	/**
	 * Computes intersections between the boundary surface of this object
	 * and the specified <code>line</code>. The intersections are added
	 * to <code>list</code> in ascending order of distance
	 * (i.e., of {@link Intersection#parameter}), where the
	 * <code>parameter</code> has to lie between <code>line.start</code>
	 * and <code>line.end</code>.
	 * Implementations of this method must not
	 * clear or modify the existing intersections in <code>list</code>.
	 * <p>
	 * The parameter <code>which</code> has to be one of
	 * {@link Intersection#ALL}, {@link Intersection#CLOSEST},
	 * {@link Intersection#ANY}. It determines if all intersections
	 * have to be added to the list, only the closest (minimal
	 * value of {@link Intersection#parameter}), or an arbitrary
	 * of the set of all intersections. Only in case of <code>ALL</code>,
	 * the return value of this method is precise.
	 * <p>
	 * If specific intersection points should be excluded from the list
	 * of computed intersections, they have to be specified in
	 * <code>excludeStart</code> and <code>excludeEnd</code>.
	 * The intersection point of <code>excludeStart</code> has to be the
	 * starting point of <code>line</code>, the intersection point of
	 * <code>excludeEnd</code> has to be the end point of <code>line</code>.
	 * The exclusion of intersections is a useful feature for
	 * ray-tracing, e.g., when a ray is re-emitted at an intersection point
	 * in another direction. 
	 *
	 * @param line a line
	 * @param which one of {@link Intersection#ALL},
	 * {@link Intersection#CLOSEST}, {@link Intersection#ANY}, this
	 * determines which intersections have to be added to <code>list</code>
	 * @param list the intersections are added to this list
	 * @param excludeStart intersection at start point which shall be excluded, or <code>null</code>
	 * @param excludeEnd intersection at end point which shall be excluded, or <code>null</code>
	 * @return <code>true</code> iff the beginning of the line lies
	 * within the volume (i.e., if the line starts within the volume or
	 * enters the volume at the starting point); however note that the returned
	 * value is valid only if <code>which == Intersection.ALL</code>
	 */
	boolean computeIntersections (Line line, int which, IntersectionList list,
			Intersection excludeStart, Intersection excludeEnd);

	/**
	 * This method computes the unit normal vector of an intersection
	 * <code>is</code> which has been computed previously by the invocation
	 * of {@link #computeIntersections} on this volume.
	 * 
	 * @param is a previously computed intersection
	 * @param normal resulting unit vector is placed in here
	 */
	void computeNormal (Intersection is, Vector3d normal);

	/**
	 * This method computes the uv-coordinates of an intersection point
	 * <code>is</code> which has been computed previously by the invocation
	 * of {@link #computeIntersections} on this volume.
	 * 
	 * @param is a previously computed intersection
	 * @param uv resulting uv-coordinates are placed in here
	 */
	void computeUV (Intersection is, Vector2d uv);

	/**
	 * This method computes the derivatives of the surface point
	 * (as function of the uv-coordinates, see {@link #computeUV})
	 * with respect to u and v at the intersection point.
	 * 
	 * @param is a previously computed intersection
	 * @param dpdu resulting derivative with respect to u
	 * @param dpdv resulting derivative with respect to v
	 */
	void computeTangents (Intersection is, Vector3d dpdu, Vector3d dpdv);

	/**
	 * Computes the extent of this volume, i.e., an axis-aligned
	 * bounding box between <code>min</code> and <code>max</code>.
	 * 
	 * @param min minimum coordinates of bounding box are placed in here
	 * @param max maximum coordinates of bounding box are placed in here
	 * @param temp has to be provided by the invoker, may be used in implementations
	 */
	void getExtent (Tuple3d min, Tuple3d max, Variables temp);

	/**
	 * Returns <code>true</code> if the specified <code>box</code> contains
	 * (part of) the boundary surface of this volume. Otherwise, if box and
	 * boundary do not overlap, this method should return <code>false</code>,
	 * but may also return <code>true</code> if an exact computation
	 * would be too expensive or complicated.
	 * <p>
	 * Note that a box contains the boundary of a closed set S iff
	 * both have a non-empty intersection and the box is not contained
	 * in the open set of S.
	 * 
	 * @param box bounding box
	 * @param center center coordinates of box
	 * @param radius radius of enclosing sphere
	 * @param temp has to be provided by the invoker, may be used in implementations
	 * @return <code>true</code> if box contains (part of) the boundary of this volume
	 */
	boolean boxContainsBoundary (BoundingBox box, Tuple3d center,
			double radius, Variables temp);

	/**
	 * Sets a unique identifier for this volume. <code>id</code> has to be
	 * non-negative, the <code>id</code>s of all coexisting volumes should
	 * be consecutive numbers starting at zero. 
	 * 
	 * @param id id for this volume
	 * 
	 * @see #getId()
	 */
	void setId (int id);

	/**
	 * Returns the id which has been set by {@link #setId(int)}. Identifiers
	 * are non-negative and should be consecutive starting at zero, so that
	 * they can be used as indices into arrays which associate additional
	 * information with the volumes. Small gaps in the set of used identifiers
	 * are tolerable.
	 * 
	 * @return id of this volume
	 */
	int getId ();

	/**
	 * This operator method creates the complement of
	 * this volume.
	 * 
	 * @return the complement of this volume
	 * 
	 * @see CSGComplement
	 */
	Volume operator$com ();

	/**
	 * This operator method creates the union of this volume
	 * and <code>v</code>.
	 * 
	 * @param v a volume
	 * @return the union of this volume and <code>v</code>
	 * 
	 * @see CSGUnion
	 */
	Volume operator$or (Volume v);

	/**
	 * This operator method creates the intersection of this volume
	 * and <code>v</code>.
	 * 
	 * @param v a volume
	 * @return the intersection of <code>a</code> and <code>b</code>
	 * 
	 * @see CSGIntersection
	 */
	Volume operator$and (Volume v);

	/**
	 * This operator method creates the difference between
	 * <code>a</code> and <code>b</code>.
	 * 
	 * @param v the volume to be subtracted
	 * @return the difference between this volume and <code>b</code>
	 * 
	 * @see CSGDifference
	 */
	Volume operator$sub (Volume v);

}
