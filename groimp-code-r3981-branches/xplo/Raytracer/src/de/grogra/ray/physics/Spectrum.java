
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

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

import de.grogra.xl.lang.DoubleToDouble;

/**
 * This interface represents a spectrum, i.e., a function f(&nu;)
 * of frequency &nu; of a wave.
 * 
 * @author Ole Kniemeyer
 */
public interface Spectrum extends DoubleToDouble
{
	/**
	 * Returns a clone of this spectrum.
	 * 
	 * @return clone of this spectrum
	 */
	Spectrum clone ();

	/**
	 * Returns a new instance of the class of this spectrum. It is
	 * initialized to the zero function.
	 * 
	 * @return new instance of same class
	 */
	Spectrum newInstance ();

	/**
	 * Evaluates the spectrum at frequency <code>nu</code>
	 * (measured in Hz).
	 * 
	 * @param nu frequency in Hz
	 * 
	 * @return function value at <code>nu</code>
	 */
	double evaluateDouble (double nu);

	/**
	 * Scales this spectrum by the given <code>factor</code>.
	 * 
	 * @param factor scaling factor
	 */
	void scale (double factor);

	/**
	 * Sets this spectrum to the identity, i.e., its value
	 * is 1 everywhere.
	 */
	void setIdentity ();

	/**
	 * Sets this spectrum to the zero function.
	 */
	void setZero ();

	/**
	 * Returns the integral over all frequencies of this
	 * spectrum.
	 * 
	 * @return integrated spectrum over all frequencies
	 */
	double integrate ();

	/**
	 * Sets this spectrum to <code>spec</code>. <code>spec</code> has to
	 * be compatible with this spectrum (usually this means that it has
	 * to be of the same class).
	 * 
	 * @param spec another spectrum
	 */
	void set (Spectrum spec);

	/**
	 * Adds the function <code>spec</code> to this function.
	 * <code>spec</code> has to
	 * be compatible with this spectrum (usually this means that it has
	 * to be of the same class).
	 * 
	 * @param spec the spectrum to add
	 */
	void add (Spectrum spec);

	/**
	 * Subtracts the function <code>spec</code> from this function.
	 * <code>spec</code> has to
	 * be compatible with this spectrum (usually this means that it has
	 * to be of the same class).
	 * 
	 * @param spec the spectrum to subtract
	 */
	void sub (Spectrum spec);

	void clampMinZero ();

	/**
	 * Computes the scalar product of this spectrum with <code>spec</code>,
	 * i.e., the integral of the pointwise product of both spectra over all
	 * frequencies. The integration is split into three parts: The first part
	 * ranges over the red part of the spectrum, its result is stored in
	 * <code>out.x</code>. The second part ranges over the green part of the
	 * spectrum and is stored in <code>out.y</code>, the third part ranges
	 * over the blue part and is stored in <code>out.z</code>.
	 * 
	 * @param spec another spectrum
	 * @param out result of dot product, split into three integration domains
	 */
	void dot (Spectrum spec, Tuple3d out);

	/**
	 * Performs an componentwise multiplication of this spectrum
	 * by <code>spec</code>. <code>spec</code> has to
	 * be compatible with this spectrum (usually this means that it has
	 * to be of the same class). The result of each component is stored
	 * in this spectrum.
	 * 
	 * @param factor the spectrum to multiply with
	 */
	void mul (Spectrum factor);

	/**
	 * Performs a pointwise multiplication of this spectrum
	 * by the given RGB spectum <code>factor</code>.
	 * 
	 * @param factor the multiplicator
	 */
	void mul (Tuple3d factor);

	/**
	 * Performs a pointwise multiplication of this spectrum
	 * by the given RGB spectum <code>factor</code>.
	 * 
	 * @param factor the multiplicator
	 */
	void mul (Tuple3f factor);

	
	/**
	 * Performs an componentwise division of this spectrum
	 * by <code>spec</code>. <code>spec</code> has to
	 * be compatible with this spectrum (usually this means that it has
	 * to be of the same class). The result of each component is stored
	 * in this spectrum.
	 * Note that a factor with 0 causes a ArithmeticException
	 * 
	 * @param factor the spectrum to divide with
	 */
	void div (Spectrum factor);
	

	/**
	 * Performs a pointwise division of this spectrum
	 * by the given RGB spectum <code>factor</code>.
	 * Note that a factor with 0 causes a ArithmeticException
	 * 
	 * @param factor the divisor
	 */
	void div (Tuple3d factor);

	/**
	 * Performs a pointwise division of this spectrum
	 * by the given RGB spectum <code>factor</code>.
	 * Note that a factor with 0 causes a ArithmeticException
	 * 
	 * @param factor the divisor
	 */
	void div (Tuple3f factor);
	
	
	/**
	 * Sets this spectrum to the spectrum of the specified
	 * RGB <code>color</code>.
	 * 
	 * @param color RGB color
	 */
	void set (Tuple3d color);

	/**
	 * Computes the RGB spectrum <code>color</code> for this
	 * spectrum.
	 * 
	 * @param color RGB spectrum will be placed in here
	 */
	void get (Tuple3d color);

	/**
	 * Sets this spectrum to the spectrum of the specified
	 * RGB <code>color</code>.
	 * 
	 * @param color RGB color
	 */
	void set (Tuple3f color);

	/**
	 * Computes the RGB spectrum <code>color</code> for this
	 * spectrum.
	 * 
	 * @param color RGB spectrum will be placed in here
	 */
	void get (Tuple3f color);
	
	
	/**
	 * Computes the spectrum sum
	 * 
	 */
	double sum();
	
	
	double getMax();
}
