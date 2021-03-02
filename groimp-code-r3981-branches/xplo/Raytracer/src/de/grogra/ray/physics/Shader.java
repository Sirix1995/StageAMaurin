
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

import javax.vecmath.*;

import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;

/**
 * A <code>Shader</code> instance represents a surface shader which
 * calculates the color of an outgoing light ray at a given point
 * as a function of geometrical and optical properties,
 * namely local and global point coordinates, canonical uv-coordinates, surface
 * tangent vectors, outgoing ray direction, index of refraction,
 * and a list of incident light rays, consisting of color and direction.
 *
 * @author Ole Kniemeyer
 */
public interface Shader extends Scattering
{


	/**
	 * Computes color of outgoing light ray for given input.
	 * The computed value is, for each color component j = R, G, B, the
	 * following sum over all incident rays k:
	 * <center>
	 * &sum;<sub>k</sub> |cos &theta;<sub>k</sub>|
	 * BSDF<sub>j</sub>(&omega;<sub>k</sub>, <code>out</code>) c<sub>k,j</sub>
	 * </center>
	 * where BSDF<sub>j</sub> is the bidirectional scattering distribution
	 * function (= BRDF + BTDF) at the point <code>env.point</code>,
	 * &omega;<sub>k</sub> and c<sub>k</sub> the direction and color of ray k,
	 * and &theta;<sub>k</sub> the angle between the surface normal
	 * and &omega;<sub>k</sub>.
	 * <p>
	 * The computation may include physically invalid contributions,
	 * which may not fit into the formula above, e.g., ambient or emissive
	 * light contributions.
	 * 
	 * @param env the environment for scattering
	 * @param in the incoming rays 
	 * @param out the direction unit vector of the outgoing ray
	 * (i.e., pointing away from the surface)
	 * @param specOut spectrum of outgoing ray
	 * @param color the output color will be placed in here
	 */
	void shade (Environment env, RayList in, Vector3f out, Spectrum specOut, Tuple3d color);

	
	/**
	 * The angular variance of a lambertian reflector,
	 * (&pi;<sup>2</sup> - 4) / 8.
	 * 
	 * @see #computeMaxRays
	 */
	float LAMBERTIAN_VARIANCE = (float) ((Math.PI * Math.PI - 4) / 8);


	/**
	 * Computes, for the given input, the reflected
	 * and transmitted importance rays for which the reflection/transmission
	 * probability densities (integrated over the spectrum) attain a maximum.
	 * The reflection probability
	 * density (measured with respect to solid angle)
	 * for the outgoing importance direction (i.e., incoming light direction)
	 * &omega;, given a fixed incident direction <code>in</code>, is
	 * <center>
	 * p<sub>r</sub>(&omega;) = cos &theta; BRDF(&omega;, <code>in</code>) / R 
	 * </center>
	 * where BRDF is the bidirectional reflectivity distribution function,
	 * &theta; the angle between the surface normal and &omega;, and R
	 * the total reflectivity for the incident direction, i.e., the integral
	 * over cos &theta; BRDF(&omega;, <code>in</code>).
	 * The transmission probability density is defined correspondingly.
	 * <p>
	 * The <code>color</code>-fields are set to the total
	 * reflectivity/transparency for the incident direction
	 * for each color component R, G, B. Thus, for physically plausible
	 * BRDF/BTDF, the component-wise sum of <code>reflected.color</code> and
	 * <code>transmitted.color</code> lies in the interval [0, 1],
	 * and the difference to 1 is the amount absorbed.
	 * <p>
	 * The <code>color</code> may be zero if there is no reflected or transmitted ray,
	 * respectively, i.e., if the surface is fully transparent, opaque,
	 * or absorbing. The origin-fields of the rays will never be set.
	 * <p>
	 * The computed variances are defined to be, for each color component,
	 * (approximations for) the angular mean quadratic
	 * deviations of the densities from the returned maximal ray directions.
	 * E.g., for perfect reflection/transmission, these variances are zero,
	 * whereas for a perfect lambertian reflector, the variance of reflection
	 * is &int; cos &theta; (1 / &pi;) &theta;<sup>2</sup> d&omega;
	 * = (&pi;<sup>2</sup> - 4) / 8.
	 * This is the value of {@link #LAMBERTIAN_VARIANCE}.  
	 * <p>
	 * The ray properties which are not mentioned are
	 * neither used nor modified. These are the origin and its density, and
	 * the direction density. 
	 *
	 * @param env the environment for scattering
	 * @param in the (negated) direction unit vector of the incoming ray
	 * (i.e., pointing away from the surface)
	 * @param specIn spectrum of incoming ray
	 * @param reflected the reflected ray with maximal probability
	 * @param refVariance the angular mean quadratic deviation from <code>reflected</code>
	 * @param transmitted the transmitted ray with maximal probability
	 * @param transVariance the angular mean quadratic deviation from <code>transmitted</code>
	 */
	void computeMaxRays
		(Environment env, Vector3f in, Spectrum specIn, Ray reflected, Tuple3f refVariance, Ray transmitted, Tuple3f transVariance);
	
	
	
	
	boolean isTransparent();

}
