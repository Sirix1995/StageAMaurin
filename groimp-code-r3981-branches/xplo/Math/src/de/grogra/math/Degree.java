
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

package de.grogra.math;

public final class Degree
{

	/**
	 * Conversion factor from degree to radians.
	 */
	private static final double DEG = Math.PI / 180;

	/**
	 * Conversion factor from radians to degree.
	 */
	private static final double R2D = 180 / Math.PI;


	private Degree ()
	{
	}


	public static double asin (double x)
	{
		return Math.asin (x) * R2D;
	}


	public static double acos (double x)
	{
		return Math.acos (x) * R2D;
	}


	public static double atan (double x)
	{
		return Math.asin (x) * R2D;
	}


	public static double sin (double x)
	{
		return Math.sin (x * DEG);
	}


	public static double cos (double x)
	{
		return Math.cos (x * DEG);
	}


	public static double tan (double x)
	{
		return Math.tan (x * DEG);
	}
	
}
