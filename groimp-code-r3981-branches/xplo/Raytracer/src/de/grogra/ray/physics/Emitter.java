
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

import java.util.Random;

import javax.vecmath.Point3d;

import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;

/**
 * The <code>Emitter</code> interface is the superinterface for
 * all entities which emit a quantity of light transport:
 * {@link de.grogra.ray.physics.Light} sources emit
 * radiance, {@link de.grogra.ray.physics.Sensor}s emit
 * importance. Both sources and sensors are located at the extremal
 * vertices of a complete light path, this interface provides the
 * common methods needed for Monte Carlo-based photon tracing.
 * 
 * @author Ole Kniemeyer
 */
public interface Emitter extends Scattering
{


	/**
	 * Pseudorandomly generates, for the given input, a set of origins
	 * for this emitter. They are generated such that they can be used
	 * for Monte Carlo-based photon tracing algorithms in the
	 * following way.
	 * <p>
	 * At first, we consider the case where the emitter is in fact
	 * a light source.
	 * Let L(x, &omega;, &nu;) be the emitted spectral radiance for the
	 * frequency &nu; at the light's surface
	 * point x in direction &omega;. The radiant exitance
	 * (emitted spectral power per area) at x is defined as
	 * <center>
	 * L<sup>0</sup>(x, &nu;) = &int; cos &theta;
	 * L(x, &omega;, &nu;) d&omega;
	 * </center>
	 * where &theta; is the angle between the surface normal and &omega;.
	 * Now the directional distribution of the emitted radiance at x can be
	 * described by the density
	 * <center>
	 * L<sup>1</sup>(x, &omega;, &nu;)
	 * = L(x, &omega;, &nu;) / L<sup>0</sup>(x, &nu;)
	 * </center>
	 * so that the radiance is split into
	 * <center>
	 * L(x, &omega;, &nu;) = L<sup>0</sup>(x, &nu;)
	 * L<sup>1</sup>(x, &omega;, &nu;)
	 * </center>
	 * Let o<sub>i</sub> and s<sub>i</sub> denote the origins and spectra of
	 * the N generated rays (N = <code>rays.size</code>). Then
	 * for a function f(x, &nu;) which is to be
	 * integrated over the light surface, the sum
	 * <center>
	 * 1 / N &sum;<sub>i</sub> s<sub>i</sub>(&nu;) f(o<sub>i</sub>, &nu;)
	 * </center>
	 * is an unbiased estimate for the integral
	 * <center>
	 * &int; L<sup>0</sup>(x, &nu;) f(x, &nu;) dA
	 * </center>
	 * The integral ranges over the whole surface A of the light source.
	 * As a consequence,
	 * the spectrum of a ray is to be considered as the ray's radiant
	 * spectral power.
	 * <p>
	 * Now if the emitter is a sensor,
	 * let W(x, &omega;, &nu;) be the emitted spectral importance
	 * for frequency &nu; at the sensors's surface point x in direction &omega;.
	 * The quantities W<sup>0</sup>(x, &nu;)
	 * and W<sup>1</sup>(x, &omega;, &nu;) are
	 * defined similarly to the case of light sources:
	 * <center>
	 * W<sup>0</sup>(x, &nu;) = &int; cos &theta;
	 * W(x, &omega;, &nu;) d&omega;<br>
	 * W(x, &omega;, &nu;) = W<sup>0</sup>(x)
	 * W<sup>1</sup>(x, &omega;, &nu;)
	 * </center>
	 * The formulas for light sources are valid for sensors if the
	 * L-quantites are replaced by the corresponding W-quantities.
	 * <p>
	 * Let p<sub>x</sub> be the probability density used for the ray
	 * origin, then the field <code>originDensity</code>
	 * is set to p<sub>x</sub>(o<sub>i</sub>) for each ray.
	 * For emitters which are concentrated at a single point
	 * (e.g., point lights) p<sub>x</sub> is not
	 * a regular function, the value <code>originDensity</code> will
	 * be set to a multiple of {@link #DELTA_FACTOR}. 
	 * <p>
	 * The ray properties which are not mentioned in the given formulas are
	 * neither used nor modified. These are the direction and its density. 
	 * 
	 * @param env the environment
	 * @param out the outgoing rays to be generated
	 * @param random pseudorandom generator
	 */
	void generateRandomOrigins (Environment env, RayList out, Random random);


	/**
	 * Evaluates the exitance function for given input.
	 * The computed value is the spectrum of the
	 * radiant exitance (emitted power per area)
	 * L<sup>0</sup><sub>j</sub>(x, &nu;) at the point
	 * <code>env.point</code> in case of light sources, or the
	 * corresponding function W<sup>0</sup><sub>j</sub>(x, &nu;) in case
	 * of sensors.
	 * <p>
	 * The returned value
	 * is the value of the probability density p<sub>x</sub>
	 * that would be calculated by {@link #generateRandomOrigins} if
	 * <code>env.point</code> happened to be one of the randomly generated
	 * origins.
	 * 
	 * @param env the environment for scattering
	 * @param exitance the exitance values will be placed in here
	 * @return the value of the probability density for the ray origin
	 */
	double computeExitance (Environment env, Spectrum exitance);

	double completeRay (Environment env, Point3d vertex, Ray out);

}
