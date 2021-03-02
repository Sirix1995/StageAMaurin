
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
 * x-axis (the turtle's left axis).
 * 
 * @author Ole Kniemeyer
 */
public class RL extends Rotation
{

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new RL ());
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
		return new RL ();
	}

//enh:end

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (RL.$TYPE, angle$FIELD);
		}

		public static void signature (@In @Out RL n, float a)
		{
		}
	}


	/**
	 * Creates a new <code>RL</code> node with an initial
	 * angle of 0 degrees.
	 */
	public RL ()
	{
		this (0);
	}


	/**
	 * Creates a new <code>RL</code> node which performs
	 * a counter-clockwise rotation of <code>angle</code>
	 * degrees.
	 * 
	 * @param angle rotation angle in degrees
	 */
	public RL (float angle)
	{
		super (angle);
	}


	@Override
	protected void transform (double c, double s, Matrix4d in, Matrix4d out)
	{
		double t;
		out.m00 = in.m00;
		out.m01 = c * (t = in.m01) + s * in.m02;
		out.m02 = c * in.m02 - s * t;
		out.m03 = in.m03;
		out.m10 = in.m10;
		out.m11 = c * (t = in.m11) + s * in.m12;
		out.m12 = c * in.m12 - s * t;
		out.m13 = in.m13;
		out.m20 = in.m20;
		out.m21 = c * (t = in.m21) + s * in.m22;
		out.m22 = c * in.m22 - s * t;
		out.m23 = in.m23;
	}


}
