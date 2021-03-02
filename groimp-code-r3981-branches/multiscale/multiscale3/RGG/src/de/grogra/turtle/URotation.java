
// NOTE: This file was generated automatically.

// ********************************************
// *               DO NOT EDIT!               *
// ********************************************


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

package de.grogra.turtle;

import javax.vecmath.Matrix4d;

/**
 * This class implements a rotation about the local
 * y-axis (the turtle's up axis).
 * 
 * @author Ole Kniemeyer
 */
public abstract class URotation extends Rotation
{

	/**
	 * Creates a new <code>URotation</code> node with an initial
	 * angle of 0 degrees.
	 */
	public URotation ()
	{
		this (0);
	}


	/**
	 * Creates a new <code>URotation</code> node which performs
	 * a counter-clockwise rotation of <code>angle</code>
	 * degrees.
	 * 
	 * @param angle rotation angle in degrees
	 */
	public URotation (float angle)
	{
		super (angle);
	}


	@Override
	protected void transform (double c, double s, Matrix4d in, Matrix4d out)
	{
		double t;
		out.m00 = c * (t = in.m00) - s * in.m02;
		out.m01 = in.m01;
		out.m02 = s * t + c * in.m02;
		out.m03 = in.m03;
		out.m10 = c * (t = in.m10) - s * in.m12;
		out.m11 = in.m11;
		out.m12 = s * t + c * in.m12;
		out.m13 = in.m13;
		out.m20 = c * (t = in.m20) - s * in.m22;
		out.m21 = in.m21;
		out.m22 = s * t + c * in.m22;
		out.m23 = in.m23;
	}


}
