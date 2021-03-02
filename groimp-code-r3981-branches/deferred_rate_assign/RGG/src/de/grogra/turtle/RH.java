
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
 * z-axis (the turtle's head axis).
 * 
 * @author Ole Kniemeyer
 */
public class RH extends Rotation
{

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new RH ());
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new RH ();
	}

//enh:end

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (RH.$TYPE, angle$FIELD);
		}

		public static void signature (@In @Out RH n, float a)
		{
		}
	}


	/**
	 * Creates a new <code>RH</code> node with an initial
	 * angle of 0 degrees.
	 */
	public RH ()
	{
		this (0);
	}


	/**
	 * Creates a new <code>RH</code> node which performs
	 * a counter-clockwise rotation of <code>angle</code>
	 * degrees.
	 * 
	 * @param angle rotation angle in degrees
	 */
	public RH (float angle)
	{
		super (angle);
	}


	@Override
	protected void transform (double c, double s, Matrix4d in, Matrix4d out)
	{
		double t;
		out.m00 = c * (t = in.m00) + s * in.m01;
		out.m01 = c * in.m01 - s * t;
		out.m02 = in.m02;
		out.m03 = in.m03;
		out.m10 = c * (t = in.m10) + s * in.m11;
		out.m11 = c * in.m11 - s * t;
		out.m12 = in.m12;
		out.m13 = in.m13;
		out.m20 = c * (t = in.m20) + s * in.m21;
		out.m21 = c * in.m21 - s * t;
		out.m22 = in.m22;
		out.m23 = in.m23;
	}


}
