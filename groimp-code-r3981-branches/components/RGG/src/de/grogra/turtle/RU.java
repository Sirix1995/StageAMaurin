
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
public class RU extends URotation
{

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new RU ());
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
		return new RU ();
	}

//enh:end

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (RU.$TYPE, angle$FIELD);
		}

		public static void signature (@In @Out RU n, float a)
		{
		}
	}


	/**
	 * Creates a new <code>RU</code> node with an initial
	 * angle of 0 degrees.
	 */
	public RU ()
	{
		this (0);
	}


	/**
	 * Creates a new <code>RU</code> node which performs
	 * a counter-clockwise rotation of <code>angle</code>
	 * degrees.
	 * 
	 * @param angle rotation angle in degrees
	 */
	public RU (float angle)
	{
		super (angle);
	}


}
