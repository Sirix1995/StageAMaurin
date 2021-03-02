
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

package de.grogra.ray.physics;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.ray2.Scene;
import de.grogra.vecmath.geom.BoundingBox;
import de.grogra.vecmath.geom.Intersection;

/**
 * This class serves as input to scattering calculations.
 * The fields together
 * constitute the local environment at a surface, light or
 * sensor point, including geometrical and optical properties.
 * 
 * @author Ole Kniemeyer
 */
public class Environment
{
	/**
	 * Constant for {@link #type} indicating a standard (unphysical)
	 * ray tracer algorithm.
	 */
	public static final int STANDARD_RAY_TRACER = 0;

	/**
	 * Constant for {@link #type} indicating a path tracer algorithm.
	 */
	public static final int PATH_TRACER = 1;

	/**
	 * Constant for {@link #type} indicating a radiation model algorithm.
	 */
	public static final int RADIATION_MODEL = 2;

	/**
	 * The type of algorithm for which this environment is used, one of
	 * {@link #STANDARD_RAY_TRACER}, {@link #PATH_TRACER},
	 * {@link #RADIATION_MODEL}.
	 */
	public final int type;

	/**
	 * The bounding box of all finite volumes of the scene in which this
	 * environment is used.
	 * 
	 * @see #boundsCenter
	 * @see #boundsRadius
	 */
	public final BoundingBox bounds;

	/**
	 * The center of {@link #bounds}.
	 */
	public final Vector3d boundsCenter;

	/**
	 * The radius of the bounding sphere whose center is located at
	 * {@link #boundsCenter} and which encloses {@link #bounds}.
	 */
	public final double boundsRadius;

	/**
	 * The transformation from local to global coordinates.
	 * This is only used for the emitter-specific
	 * methods. The local coordinates are those of the object
	 * which defines the emitter (light source or sensor).
	 */
	public Matrix4f localToGlobal = new Matrix4f ();

	/**
	 * The transformation from global to local coordinates.
	 * This has to be the inverse of <code>localToGlobal</code>.
	 */
	public Matrix4f globalToLocal = new Matrix4f ();

	/**
	 * The surface position in local coordinates of the object which
	 * defines the surface, light source, or sensor.
	 */
	public final Point3f localPoint = new Point3f ();

	/**
	 * The surface position in global world coordinates.
	 */
	public final Point3f point = new Point3f ();

	/**
	 * The surface geometric normal unit vector at {@link #point}
	 * in global world coordinates.
	 * This is always the outer normal vector.
	 * <p>
	 * The normal vector is only needed for surface shaders, it is
	 * not used for emitters (light sources or sensors). 
	 */
	public final Vector3f normal = new Vector3f ();

	/**
	 * The canonical uv-coordinates at {@link #point}. Which
	 * coordinates are canonical for a specific surface depends on the
	 * surface: E.g., for a sphere, the canonical uv-coordinates
	 * would be spherical coordinates.
	 */
	public final Point2f uv = new Point2f ();

	/**
	 * The derivative of the global surface position with respect to
	 * the canonical u-coordinate.
	 */
	public final Vector3f dpdu = new Vector3f ();

	/**
	 * The derivative of the global surface position with respect to
	 * the canonical v-coordinate.
	 */
	public final Vector3f dpdv = new Vector3f ();

	/**
	 * Indicates whether this is an intersection at the surface of a
	 * solid object or of an infinitely thin object.  
	 */
	public boolean solid = true;

	/**
	 * The index-of-refraction ratio at the surface. This is the index
	 * of refraction of the surface side where the normal vector points
	 * into, divided by the index of refraction of the opposite side.
	 */
	public float iorRatio = 1.0f;
 
	/**
	 * This matrix may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Matrix3f userMatrix = new Matrix3f ();

	/**
	 * This matrix may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Matrix3f userMatrix2 = new Matrix3f ();
	 
	/**
	 * This matrix may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Matrix3f userMatrix3 = new Matrix3f ();

	/**
	 * This vector may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Vector3f userVector = new Vector3f ();

	/**
	 * This vector may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Vector3f userVector2 = new Vector3f ();

	/**
	 * This vector may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Vector3f userVector3 = new Vector3f ();

	/**
	 * This point may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Point3d tmpPoint0 = new Point3d ();

	/**
	 * This point may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Point3d tmpPoint1 = new Point3d ();

	/**
	 * This vector may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Vector3d tmpVector0 = new Vector3d ();

	/**
	 * This vector may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Vector3d tmpVector1 = new Vector3d ();

	/**
	 * This point may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Point2d tmpPoint2d0 = new Point2d ();

	/**
	 * This matrix may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Matrix3d tmpMatrix30 = new Matrix3d ();

	/**
	 * This spectrum may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Spectrum tmpSpectrum0;

	/**
	 * This spectrum may be used freely in implementations of the
	 * <code>Scattering</code> methods.
	 */
	public final Spectrum tmpSpectrum1;

	/**
	 * The current owner of {@link #userObject}. This should be used
	 * to determine the validity of caching information stored in
	 * {@link #userObject}.
	 */
	public Object userObjectOwner;

	/**
	 * This object may be used freely in implementations of the
	 * <code>Scattering</code> methods in order to cache results
	 * between invocations. Whenever this field is modified,
	 * {@link #userObjectOwner} has to be set, too.
	 */
	public Object userObject;

	public Environment(){
		bounds = null;
		tmpSpectrum0 = null;
		tmpSpectrum1 = null;
		type = 0;
		boundsRadius = 0;
		boundsCenter = null;
	}
	
	/**
	 * Creates a new instance of <code>Environment</code>. The
	 * <code>factory</code> is used to obtain new spectra for
	 * light transport computations.
	 * 
	 * @param bounds bounding box of scene in which environment is used
	 * @param factory use this to create new spectra
	 * @param type value for {@link #type}
	 */
	public Environment (BoundingBox bounds, Spectrum factory, int type)
	{
		this.bounds = bounds;
		this.type = type;
		if (bounds == null)
		{
			boundsCenter = null;
			boundsRadius = 0;
		}
		else
		{
			boundsCenter = new Vector3d ();
			boundsCenter.sub (bounds.max, bounds.min);
			boundsRadius = 0.5 * boundsCenter.length ();
			boundsCenter.add (bounds.max, bounds.min);
			boundsCenter.scale (0.5);
		}
		tmpSpectrum0 = factory.newInstance ();
		tmpSpectrum1 = factory.newInstance ();
	}

	public void set (Intersection desc, int flags, Scene scene)
	{
		if ((flags & Shader.NEEDS_POINT) != 0)
		{
			if (desc.parameter == Double.POSITIVE_INFINITY)
			{
				tmpVector0.normalize (desc.line.direction);
				point.set (tmpVector0);
				tmpVector0.scale (1e100);
				scene.transform (desc.volume, tmpVector0, tmpVector0);
				tmpVector0.normalize ();
				localPoint.set (tmpVector0);
			}
			else
			{
				Point3d p = desc.getPoint ();
				point.set (p);
				scene.transform (desc.volume, p, tmpVector0);
				localPoint.set (tmpVector0);
			}
		}
		if ((flags & Shader.NEEDS_NORMAL) != 0)
		{
			normal.set (desc.getNormal ());
		}
		if ((flags & Shader.NEEDS_UV) != 0)
		{
			uv.set (desc.getUV ());
		}
		if ((flags & Shader.NEEDS_TANGENTS) != 0)
		{
			dpdu.set (desc.getUTangent ());
			dpdv.set (desc.getVTangent ());
		}
		solid = desc.type != Intersection.PASSING;
	}
}
