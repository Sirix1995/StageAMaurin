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

package de.grogra.vecmath;

import java.io.Serializable;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class Matrix34d implements Serializable, Cloneable
{
	/**
	 * The first element of the first row.
	 */
	public double m00;

	/**
	 * The second element of the first row.
	 */
	public double m01;

	/**
	 * third element of the first row.
	 */
	public double m02;

	/**
	 * The fourth element of the first row.
	 */
	public double m03;

	/**
	 * The first element of the second row.
	 */
	public double m10;

	/**
	 * The second element of the second row.
	 */
	public double m11;

	/**
	 * The third element of the second row.
	 */
	public double m12;

	/**
	 * The fourth element of the second row.
	 */
	public double m13;

	/**
	 * The first element of the third row.
	 */
	public double m20;

	/**
	 * The second element of the third row.
	 */
	public double m21;

	/**
	 * The third element of the third row.
	 */
	public double m22;

	/**
	 * The fourth element of the third row.
	 */
	public double m23;

	public Matrix34d ()
	{
	}

	public Matrix34d (Matrix34d m)
	{
		set (m);
	}

	public final void set (Matrix34d m)
	{
		m00 = m.m00;
		m01 = m.m01;
		m02 = m.m02;
		m03 = m.m03;
		m10 = m.m10;
		m11 = m.m11;
		m12 = m.m12;
		m13 = m.m13;
		m20 = m.m20;
		m21 = m.m21;
		m22 = m.m22;
		m23 = m.m23;
	}

	public final void set (Matrix4d m)
	{
		m00 = m.m00;
		m01 = m.m01;
		m02 = m.m02;
		m03 = m.m03;
		m10 = m.m10;
		m11 = m.m11;
		m12 = m.m12;
		m13 = m.m13;
		m20 = m.m20;
		m21 = m.m21;
		m22 = m.m22;
		m23 = m.m23;
	}

	public final void setIdentity ()
	{
		m00 = 1.0;
		m01 = 0.0;
		m02 = 0.0;
		m03 = 0.0;
		m10 = 0.0;
		m11 = 1.0;
		m12 = 0.0;
		m13 = 0.0;
		m20 = 0.0;
		m21 = 0.0;
		m22 = 1.0;
		m23 = 0.0;
	}

	public final void get (Matrix4d m)
	{
		m.m00 = m00;
		m.m01 = m01;
		m.m02 = m02;
		m.m03 = m03;
		m.m10 = m10;
		m.m11 = m11;
		m.m12 = m12;
		m.m13 = m13;
		m.m20 = m20;
		m.m21 = m21;
		m.m22 = m22;
		m.m23 = m23;
		m.m30 = 0;
		m.m31 = 0;
		m.m32 = 0;
		m.m33 = 1;
	}

	public final void getRotationScale (Matrix3d m)
	{
		m.m00 = m00;
		m.m01 = m01;
		m.m02 = m02;
		m.m10 = m10;
		m.m11 = m11;
		m.m12 = m12;
		m.m20 = m20;
		m.m21 = m21;
		m.m22 = m22;
	}

	public final void get (Vector3d trans)
	{
		trans.x = m03;
		trans.y = m13;
		trans.z = m23;
	}

	public Matrix4d toMatrix4d ()
	{
		return new Matrix4d (m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, 0, 0, 0, 1);
	}

	public Matrix34d clone ()
	{
		return new Matrix34d (this);
	}
}
