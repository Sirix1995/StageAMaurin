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

import de.grogra.vecmath.Math2;

/**
 * This class represents the base class for volumes whose
 * geometry is specified
 * in local coordinates. The transformation from global
 * world coordinates to local object coordinates is stored in the
 * fields {@link #m00} to {@link #m22} and {@link #t0} to {@link #t2},
 * the transformation is computed by
 * <center>
 * o<sub>i</sub>
 * = &sum;<sub>i</sub> m<sub>i,j</sub> (w<sub>j</sub> - t<sub>j</sub>)
 * </center>
 * I.e., the vector <code>t</code> represents the location of the
 * object origin in world coordinates.
 * This representation has been chosen instead of 
 * an instance of <code>Matrix4d</code> for memory and speed
 * (faster instantiation) reasons. After modification of
 * matrix components and other geometric parameters declared
 * by subclasses, {@link #invalidate()} has to be invoked. 
 * 
 * @author Ole Kniemeyer
 */
public abstract class TransformableVolume extends VolumeBase
{

	/**
	 * 00-component of the rotation matrix from
	 * world coordinates to object coordinates.
	 */
	protected double m00;

	/**
	 * 01-component of the rotation matrix from
	 * world coordinates to object coordinates.
	 */
	protected double m01;

	/**
	 * 02-component of the rotation matrix from
	 * world coordinates to object coordinates.
	 */
	protected double m02;

	/**
	 * 10-component of the rotation matrix from
	 * world coordinates to object coordinates.
	 */
	protected double m10;

	/**
	 * 11-component of the rotation matrix from
	 * world coordinates to object coordinates.
	 */
	protected double m11;

	/**
	 * 12-component of the rotation matrix from
	 * world coordinates to object coordinates.
	 */
	protected double m12;

	/**
	 * 20-component of the rotation matrix from
	 * world coordinates to object coordinates.
	 */
	protected double m20;

	/**
	 * 21-component of the rotation matrix from
	 * world coordinates to object coordinates.
	 */
	protected double m21;

	/**
	 * 22-component of the rotation matrix from
	 * world coordinates to object coordinates.
	 */
	protected double m22;

	/**
	 * 0-component of the translation
	 * from object coordinates to world coordinates.
	 */
	protected double t0;

	/**
	 * 1-component of the translation
	 * from object coordinates to world coordinates.
	 */
	protected double t1;

	/**
	 * 2-component of the translation
	 * from object coordinates to world coordinates.
	 */
	protected double t2;

	/**
	 * Sets the transformation from world to
	 * object coordinates to the specified matrix
	 * and vector: <code>o = rot * (w - trans)</code>
	 * 
	 * @param rot rotational component of new transformation
	 * @param trans translational component of new transformation
	 */
	public void setTransformation (Matrix3d rot, Tuple3d trans)
	{
		m00 = rot.m00;
		m01 = rot.m01;
		m02 = rot.m02;
		m10 = rot.m10;
		m11 = rot.m11;
		m12 = rot.m12;
		m20 = rot.m20;
		m21 = rot.m21;
		m22 = rot.m22;
		t0 = trans.x;
		t1 = trans.y;
		t2 = trans.z;
		invalidate ();
	}

	/**
	 * gets the transformation from world to
	 * object coordinates to the specified matrix
	 * and vector: <code>o = rot * (w - trans)</code>
	 * 
	 * @param rot rotational component is stored in rot
	 * @param trans translational component is stored in trans
	 */
	public void getTransformation (Matrix3d rot, Tuple3d trans)
	{
		rot.m00 = m00;
		rot.m01 = m01;
		rot.m02 = m02;
		rot.m10 = m10;
		rot.m11 = m11;
		rot.m12 = m12;
		rot.m20 = m20;
		rot.m21 = m21;
		rot.m22 = m22;
		trans.x = t0;
		trans.y = t1;
		trans.z = t2;
	}

	/**
	 * Sets the transformation from world to object coordinates to an
	 * orthonormal transformation which transforms the
	 * <code>origin</code> point to (0,0,0) and
	 * the normalized <code>axis</code> vector
	 * to (0,0,1).
	 * 
	 * @param origin origin in world coordinates
	 * @param axis axis (local z-direction) in world coordinates
	 */
	public void setTransformation (Tuple3d origin, Tuple3d axis)
	{
		Matrix3d m = new Matrix3d ();
		Math2.getOrthogonalBasis (axis, m, true);
		m00 = m.m00;
		m10 = m.m01;
		m20 = m.m02;
		m01 = m.m10;
		m11 = m.m11;
		m21 = m.m12;
		m02 = m.m20;
		m12 = m.m21;
		m22 = m.m22;
		t0 = origin.x;
		t1 = origin.y;
		t2 = origin.z;
		invalidate ();
	}

	/**
	 * Multiplies the current world-to-object transformation
	 * from left by a scaling matrix. This corresponds to
	 * a scaling of the object by the reciprocal of the
	 * specified values.
	 * 
	 * @param sx scaling factor in x-direction
	 * @param sy scaling factor in y-direction
	 * @param sz scaling factor in z-direction
	 */
	public void scale (double sx, double sy, double sz)
	{
		m00 *= sx;
		m10 *= sy;
		m20 *= sz;

		m01 *= sx;
		m11 *= sy;
		m21 *= sz;

		m02 *= sx;
		m12 *= sy;
		m22 *= sz;

		invalidate ();
	}

	/**
	 * Multiplies the current world-to-object transformation
	 * from right by a translation matrix using the negated
	 * values. This corresponds to a translation of the object in
	 * the world by the specified values.
	 * 
	 * @param dx translation in x-direction
	 * @param dy translation in y-direction
	 * @param dz translation in z-direction
	 */
	public void translate (double dx, double dy, double dz)
	{
		t0 += dx;
		t1 += dy;
		t2 += dz;

		invalidate ();
	}

	/**
	 * Transforms the direction <code>in</code>, specified in global
	 * world coordinates, to <code>out</code> in local object
	 * coordinates. <code>in</code> and <code>out</code> may be the
	 * same instance.
	 * 
	 * @param in direction in world coordinates
	 * @param out direction in object coordinates
	 */
	public void transformVector (Tuple3d in, Tuple3d out)
	{
		double x = in.x, y = in.y, z = in.z;
		out.x = m00 * x + m01 * y + m02 * z;
		out.y = m10 * x + m11 * y + m12 * z;
		out.z = m20 * x + m21 * y + m22 * z;
	}

	/**
	 * Transforms the point <code>in</code>, specified in global
	 * world coordinates, to <code>out</code> in local object
	 * coordinates. <code>in</code> and <code>out</code> may be the
	 * same instance.
	 * 
	 * @param in point in world coordinates
	 * @param out point in object coordinates
	 */
	public void transformPoint (Tuple3d in, Tuple3d out)
	{
		double x = in.x - t0, y = in.y - t1, z = in.z - t2;
		out.x = m00 * x + m01 * y + m02 * z;
		out.y = m10 * x + m11 * y + m12 * z;
		out.z = m20 * x + m21 * y + m22 * z;
	}

	/**
	 * Transforms the point <code>in</code>, specified in local
	 * object coordinates, to <code>out</code> in global world
	 * coordinates. <code>in</code> and <code>out</code> may be the
	 * same instance.
	 * 
	 * @param in point in object coordinates
	 * @param out point in world coordinates
	 */
	public void invTransformPoint (Tuple3d in, Tuple3d out)
	{
		getObjectToWorldRotationScale ().transform (in, out);
		out.x += t0;
		out.y += t1;
		out.z += t2;
	}

	/**
	 * Multiplies the vector <code>in</code> by the transpose of the upper
	 * 3x3 values of the world-to-object transformation and stores
	 * the result in <code>out</code>. This can be used to transform
	 * normal vectors from object coordinates to world coordinates.
	 * <code>in</code> and <code>out</code> may be the same instance.
	 * 
	 * @param in direction in world coordinates
	 * @param out direction in object coordinates
	 */
	public void transformTranspose (Tuple3d in, Tuple3d out)
	{
		double x = in.x, y = in.y, z = in.z;
		out.x = m00 * x + m10 * y + m20 * z;
		out.y = m01 * x + m11 * y + m21 * z;
		out.z = m02 * x + m12 * y + m22 * z;
	}

	/**
	 * Contains the rotational component of the
	 * object-to-world transformation. A NaN
	 * value of <code>m00</code> indicates that the matrix
	 * is invalid and should be recomputed.
	 */
	private Matrix3d objectToWorld;

	/**
	 * Computes the rotational component of the transformation
	 * from local object coordinates to global world coordinates.
	 * The matrix components of the returned value must not be modified.
	 * 
	 * @return object-to-world transformation
	 */
	public Matrix3d getObjectToWorldRotationScale ()
	{
		if (objectToWorld == null)
		{
			objectToWorld = new Matrix3d ();
		}
		else if (objectToWorld.m00 == objectToWorld.m00)
		{
			return objectToWorld;
		}
		objectToWorld.m00 = m00;
		objectToWorld.m10 = m10;
		objectToWorld.m20 = m20;
		objectToWorld.m01 = m01;
		objectToWorld.m11 = m11;
		objectToWorld.m21 = m21;
		objectToWorld.m02 = m02;
		objectToWorld.m12 = m12;
		objectToWorld.m22 = m22;
		objectToWorld.invert ();
		return objectToWorld;
	}
	
	/**
	 * Contains the frobenius norm of world-to-object transformation.
	 * A negative value indicates that it is invalid
	 * and should be recomputed.
	 */
	private double frobenius = -1;

	/**
	 * Returns the Frobenius norm of the upper 3x3 values of the
	 * world-to-object transformation, i.e., the square root of
	 * <code>m00 * m00 + ... + m22 * m22</code>. Note that the length
	 * of a vector in object coordinates is less than or equal to the
	 * length of this vector in world coordinates multplied by the
	 * Frobenius norm of the transformation.
	 * 
	 * @return Frobenius norm of world-to-object transformation
	 */
	public double getFrobeniusNorm ()
	{
		if (frobenius < 0)
		{
			frobenius = Math.sqrt (m00 * m00 + m10 * m10 + m20 * m20 + m01
				* m01 + m11 * m11 + m21 * m21 + m02 * m02 + m12 * m12 + m22
				* m22);
		}
		return frobenius;
	}

	/**
	 * Invalidates cached information about this volume. This
	 * method has to be invoked after matrix components
	 * and geometric parameters declared by subclasses have been
	 * changed.
	 */
	public void invalidate ()
	{
		if (objectToWorld != null)
		{
			objectToWorld.m00 = Double.NaN;
		}
		frobenius = -1;
	}


	public void getDiscExtent (double z, double radius, Tuple3d min, Tuple3d max)
	{
		Matrix3d m = getObjectToWorldRotationScale ();
		double d;
		d = radius * Math.sqrt (m.m00 * m.m00 + m.m01 * m.m01);
		min.x = m.m02 * z - d + t0;
		max.x = m.m02 * z + d + t0;
		d = radius * Math.sqrt (m.m10 * m.m10 + m.m11 * m.m11);
		min.y = m.m12 * z - d + t1;
		max.y = m.m12 * z + d + t1;
		d = radius * Math.sqrt (m.m20 * m.m20 + m.m21 * m.m21);
		min.z = m.m22 * z - d + t2;
		max.z = m.m22 * z + d + t2;
	}


	public void getDiscsExtent (double z1, double radius1, double z2, double radius2, Tuple3d min, Tuple3d max)
	{
		Matrix3d m = getObjectToWorldRotationScale ();
		double d;
		d = Math.sqrt (m.m00 * m.m00 + m.m01 * m.m01);
		min.x = Math.min (m.m02 * z1 - d * radius1, m.m02 * z2 - d * radius2) + t0;
		max.x = Math.max (m.m02 * z1 + d * radius1, m.m02 * z2 + d * radius2) + t0;
		d = Math.sqrt (m.m10 * m.m10 + m.m11 * m.m11);
		min.y = Math.min (m.m12 * z1 - d * radius1, m.m12 * z2 - d * radius2) + t1;
		max.y = Math.max (m.m12 * z1 + d * radius1, m.m12 * z2 + d * radius2) + t1;
		d = Math.sqrt (m.m20 * m.m20 + m.m21 * m.m21);
		min.z = Math.min (m.m22 * z1 - d * radius1, m.m22 * z2 - d * radius2) + t2;
		max.z = Math.max (m.m22 * z1 + d * radius1, m.m22 * z2 + d * radius2) + t2;
	}

}
