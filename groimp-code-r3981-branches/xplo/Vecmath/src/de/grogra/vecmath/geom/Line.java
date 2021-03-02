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

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.vecmath.Math2;

/**
 * This class represents the geometry of a line.
 * 
 * @author Ole Kniemeyer
 */
public class Line
{
	/**
	 * Origin of the line.
	 */
	public Tuple3d origin;

	/**
	 * Specifies the direction of the line. This vector is not necessarily
	 * normalized.
	 */
	public Vector3d direction;
	
	/**
	 * The line starts at <code>origin + start * direction</code>.
	 */
	public double start;

	/**
	 * The line ends at <code>origin + end * direction</code>.
	 * The condition <code>start <= end</code> has to be satisfied.
	 */
	public double end;
	
	
	/**
	 * The probability density of direction for this ray.
	 * This field is set by
	 * {@link Shader#generateRandomRays Shader.generateRandomRays} and
	 * {@link Light#generateRandomRays Light.generateRandomRays}.
	 * If p<sub>&omega;<sup>+</sup></sub> is the probability density
	 * that has been used for generating the random ray direction
	 * (measured with respect to projected solid angle &omega;<sup>+</sup>, i.e.,
	 * d&omega;<sup>+</sup> = cos &theta; d&omega;),
	 * then this field is set to
	 * p<sub>&omega;<sup>+</sup></sub>(<code>direction</code>). It is not
	 * defined for rays emanating from a directional light source. It
	 * may be {@link Float#POSITIVE_INFINITY} as a result of
	 * {@link Shader#generateRandomRays Shader.generateRandomRays}, namely
	 * for ideal specular reflection or transmission.
	 */
	public float directionDensity = 1.0f;

	/**
	 * The probability density of origin for this ray.
	 * This field is set by
	 * {@link Light#generateRandomRays Light.generateRandomRays}.
	 * If p<sub>x</sub> is the probability density
	 * that has been used for generating the random ray origin,
	 * then this field is set to
	 * p<sub>x</sub>(<code>origin</code>). It is not
	 * defined for rays emanating from a point light source.
	 */
	public float originDensity = 1.0f;
	
	
	/**
	 * The spectrum of the ray. Depending on context, this may represent
	 * the ray's spectral radiant power or other properties.
	 */
	public Color3f spectrum;
	
	public boolean reflected;
	

	public float x,y;
	public boolean valid;

	
	public Line ()
	{
		origin = new Point3d ();
		direction = new Vector3d ();
		spectrum = new Color3f();
		start =0;
		end = Double.POSITIVE_INFINITY;
		reflected = true;
	}

	
	public Line (Tuple3d origin, Vector3d direction, double start, double end)
	{
		this.origin = origin;
		this.direction = direction;
		this.start = start;
		this.end = end;
		reflected = true;
	}

	public void setLineAttributes(double start, double end){
		this.start = start;
		this.end = end;
	}

	/**
	 * Returns the square of the length of this line. 
	 * 
	 * @return squared length of this line
	 */
	public double lengthSquared ()
	{
		double t = end - start;
		return direction.lengthSquared () * t * t; 
	}


	/**
	 * Returns the length of this line. 
	 * 
	 * @return length of this line
	 */
	public double length ()
	{
		double t = end - start;
		return direction.length () * t; 
	}

	/**
	 * Computes the square of the distance between
	 * <code>point</code> and this line. The distance
	 * is defined to be the distance between
	 * <code>point</code> and the closest point of this line.
	 * 
	 * @param point a point
	 * @return squared distance to <code>point</code>
	 */
	public double distanceSquared (Tuple3d point)
	{
		double u = Math2.dot (point, origin, direction) / direction.lengthSquared ();
		if (u > end)
		{
			u = end;
		}
		else if (u < start)
		{
			u = start;
		}
		double t;
		return (t = origin.x + u * direction.x - point.x) * t
			+ (t = origin.y + u * direction.y - point.y) * t
			+ (t = origin.z + u * direction.z - point.z) * t;
	}


	/**
	 * Computes the distance between
	 * <code>point</code> and this line. The distance
	 * is defined to be the distance between
	 * <code>point</code> and the closest point of this line.
	 * 
	 * @param point a point
	 * @return distance to <code>point</code>
	 */
	public double distance (Tuple3d point)
	{
		return Math.sqrt (distanceSquared (point));
	}

	
	public Line deepCopy(){
		Line ret = new Line();
		ret.direction.set(this.direction);
		ret.origin.set(this.origin);
		ret.start = this.start;
		ret.end = this.end;
		return ret;
	}

	@Override
	public String toString ()
	{
		return origin + " " + direction + " " + start + " " + end +"  valid=" +valid +" direcDens=" +directionDensity+" spec=" +spectrum;
	}
}
