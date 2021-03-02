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

import javax.vecmath.*;

import de.grogra.ray.util.RayList;

/**
 * A <code>Scattering</code> instance represents a scattering entity:
 * Either a surface <code>Shader</code>, a <code>Light</code> source,
 * or a <code>Sensor</code>. Methods are provided that a
 * Monte Carlo-based raytracer needs for lighting calculations. This allows
 * for any desired bidirectional scattering
 * distribution functions (BSDF) in implementations of <code>Scattering</code>.
 *
 * @author Ole Kniemeyer
 */
public interface Scattering
{

	int NEEDS_TRANSFORMATION = 1;
	int NEEDS_POINT = 2;
	int NEEDS_NORMAL = 4;
	int NEEDS_TANGENTS = 8;
	int NEEDS_UV = 16;
	int IS_NON_OPAQUE = 32;
	int RANDOM_RAYS_GENERATE_ORIGINS = 64;

	int MIN_UNUSED_FLAG = 128;

	int getFlags ();

	/**
	 * A large <code>float</code> value (10<sup>10</sup>) used as a
	 * common factor for representing non-zero values of &delta;-distributions.
	 */
	float DELTA_FACTOR = 1e10f;

	/**
	 * Returns an average color for the scattering entity.
	 * This color is used for simplified graphical representations of
	 * the corresponding objects.
	 *
	 * @return an average color in Java's default sRGB color space,
	 * encoded as an int (0xAARRGGBB).
	 */
	int getAverageColor ();

	/**
	 * Pseudorandomly generates, for the given input,
	 * a set of scattered rays. The scattered rays are generated
	 * such that they can be used for a Monte Carlo integration of a function
	 * f(&omega;;&nu;) over
	 * cos &theta; BSDF(&omega;<sub>i</sub>, &nu;<sub>i</sub>;
	 * &omega;<sub>o</sub>, &nu;<sub>o</sub>)
	 * in the following way:
	 * <ul><li>
	 * If <code>adjoint</code> is <code>false</code>,
	 * <code>out</code> = &omega;<sub>o</sub> describes
	 * the direction of an outgoing light ray.
	 * In this case, the integration is with respect to &omega;<sub>i</sub>.
	 * Let g(&omega;, &nu;; <code>out</code>, &mu;)
	 * = BSDF(&omega;, &nu;; <code>out</code>, &mu;)
	 * <li>
	 * Otherwise, <code>adjoint</code> is <code>true</code>. In this case,
	 * <code>out</code> = &omega;<sub>i</sub> describes
	 * the direction of an outgoing importance ray (an inverse light ray).
	 * Now the integration is with respect to &omega;<sub>o</sub>.
	 * Let g(&omega;, &nu;; <code>out</code>, &mu;)
	 * = BSDF(<code>out</code>, &mu;; &omega;, &nu;)
	 * </ul>
	 * Let d<sub>i</sub> and s<sub>i</sub> denote the directions and spectra of
	 * the N generated rays (N = <code>rays.size</code>). Then, for every
	 * frequency &nu; the sum
	 * <center>
	 * 1 / N &sum;<sub>i</sub> s<sub>i</sub>(&nu;) f(d<sub>i</sub>; &nu;)
	 * </center>
	 * is an unbiased estimate for the integral
	 * <center>
	 * &int; cos &theta; f(&omega;; &nu;) g(&omega;, &nu;; <code>out</code>, &mu;)
	 * <code>specOut</code>(&mu;) d&mu; d&omega;
	 * </center>
	 * &theta; is the angle between the surface normal and &omega;.
	 * The domain of integration is the whole sphere, since the bidirectional
	 * scattering distribution includes both reflection and transmission
	 * (BSDF = BRDF + BTDF).
	 * <p>
	 * If this <code>Scattering</code> instance is in fact a
	 * {@link Light} source, <code>adjoint</code> is <code>true</code>,
	 * and the BSDF is defined as BSDF(<code>out</code>, &mu;; &omega;, &nu;)
	 * = L<sup>1</sup>(&omega;, &nu;) &delta;(&mu; - &nu;),
	 * i.e., the directional distribution
	 * of the emitted radiance at <code>env.point</code>, see {@link Emitter}.
	 * In this case, <code>out</code> is not used.
	 * <p>
	 * If this <code>Scattering</code> instance is in fact a
	 * {@link Sensor}, <code>adjoint</code> is <code>false</code>,
	 * and the BSDF is defined as BSDF(&omega;, &nu;; <code>out</code>, &mu;)
	 * = W<sup>1</sup>(&omega;, &nu;) &delta;(&mu; - &nu;),
	 * i.e., the directional distribution
	 * of the emitted importance at <code>env.point</code>, see {@link Emitter}.
	 * In this case, <code>out</code> is not used.
	 * <p>
	 * Let p<sub>&omega;</sub> be the probability density
	 * used for the ray direction (measured with respect to
	 * solid angle &omega;),
	 * then the field <code>directionDensity</code> of the ray i
	 * is set to p<sub>&omega;</sub>(d<sub>i</sub>).
	 * For ideal specular reflection or transmission, or for directional
	 * lights or sensors, p<sub>&omega;</sub> is not
	 * a regular function, the value <code>directionDensity</code> will
	 * be set to a multiple of {@link #DELTA_FACTOR}. 
	 * <p>
	 * The ray properties which are not mentioned in the given formulas are
	 * neither used nor modified. These are the origin and its density. 
	 * 
	 * @param env the environment for scattering
	 * @param out the direction unit vector of the outgoing ray 
	 * (i.e., pointing away from the surface)
	 * @param specOut the spectrum of the outgoing ray
	 * @param rays the rays to be generated
	 * @param adjoint represents <code>out</code> a light ray or an importance ray?
	 * @param random pseudorandom generator 
	 * 
	 * @see #computeBSDF
	 */
	void generateRandomRays (Environment env, Vector3f out, Spectrum specOut,
			RayList rays, boolean adjoint, Random random);

	/**
	 * Evaluates bidirectional scattering distribution function for given input.
	 * <p>
	 * The computed spectrum is an integral over the spectrum of the following product:
	 * <center>
	 * |cos &theta;| BSDF(&omega;<sub>i</sub>, &nu;<sub>i</sub>;
	 * &omega;<sub>o</sub>, &nu;<sub>o</sub>)
	 * </center>
	 * where BSDF is the bidirectional scattering distribution
	 * function (= BRDF + BTDF) at the point <code>env.point</code>,
	 * &omega;<sub>i</sub> the (negated) direction of the incoming light ray,
	 * &nu;<sub>i</sub> the frequency where the incoming ray is sampled,
	 * &omega;<sub>o</sub> the direction of the outgoing light ray,
	 * &nu;<sub>o</sub> the frequency where the outgoing ray is sampled,
	 * and &theta; the angle between the surface normal and <code>out</code>.
	 * <p>
	 * If <code>adjoint</code> is <code>false</code>, <code>in</code> and
	 * <code>out</code> describe true light rays from light sources to sensors.
	 * In this case, &omega;<sub>i</sub> = <code>in</code>, 
	 * &omega;<sub>o</sub> = <code>out</code>, and the integral is
	 * <center>
	 * <code>bsdf</code>(&nu;) = |cos &theta;| &int; BSDF(<code>in</code>, &nu;<sub>i</sub>;
	 * <code>out</code>, &nu;) <code>specIn</code>(&nu;<sub>i</sub>) d&nu;<sub>i</sub>
	 * </center>
	 * Otherwise, 
	 * <code>adjoint</code> is <code>true</code>. <code>in</code> and
	 * <code>out</code> then describe importance rays (inverse light rays
	 * from sensors to light sources). In this case,
	 * &omega;<sub>i</sub> = <code>out</code>,
	 * &omega;<sub>o</sub> = <code>in</code>, and the integral is
	 * <center>
	 * <code>bsdf</code>(&nu;) = |cos &theta;| &int;
	 * BSDF(<code>out</code>, &nu;;
	 * <code>in</code>, &nu;<sub>o</sub>) <code>specIn</code>(&nu;<sub>o</sub>) d&nu;<sub>o</sub>
	 * </center>
	 * <p>
	 * If this <code>Scattering</code> instance is in fact a
	 * {@link Light} source, <code>adjoint</code> is <code>false</code>,
	 * and the BSDF is defined as BSDF(<code>in</code>, &mu;; &omega;, &nu;)
	 * = L<sup>1</sup>(&omega;, &nu;) &delta;(&mu; - &nu;),
	 * i.e., the directional distribution
	 * of the emitted radiance at <code>env.point</code>, see {@link Emitter}.
	 * In this case, <code>in</code> is not used.
	 * <p>
	 * If this <code>Scattering</code> instance is in fact a
	 * {@link Sensor}, <code>adjoint</code> is <code>true</code>,
	 * and the BSDF is defined as BSDF(&omega;, &nu;; <code>in</code>, &mu;)
	 * = W<sup>1</sup>(&omega;, &nu;) &delta;(&mu; - &nu;),
	 * i.e., the directional distribution
	 * of the emitted importance at <code>env.point</code>, see {@link Emitter}.
	 * In this case, <code>in</code> is not used.
	 * <p>
	 * The computation should be physically valid. This excludes,
	 * e.g., ambient or emissive light contributions.
	 * <p>
	 * The returned value
	 * is the value of the probability density p<sub>&omega;</sub>
	 * that would be calculated by {@link #generateRandomRays} if
	 * the ray happened to be one of the randomly generated rays.
	 * 
	 * @param env the environment for scattering
	 * @param in the (negated) direction unit vector of the incoming ray
	 * (i.e., pointing away from the surface)
	 * @param specIn the spectrum of the incoming ray
	 * @param out the direction unit vector of the outgoing ray
	 * (i.e., pointing away from the surface)
	 * @param adjoint light ray or importance ray?
	 * @param bsdf the computed spectrum of the outgoing ray will be placed in here
	 * @return the value of the probability density for the ray direction
	 */
	float computeBSDF (Environment env, Vector3f in, Spectrum specIn,
			Vector3f out, boolean adjoint, Spectrum bsdf);

}
