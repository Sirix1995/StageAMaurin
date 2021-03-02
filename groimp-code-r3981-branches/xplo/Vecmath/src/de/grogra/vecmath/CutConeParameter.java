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

package de.grogra.vecmath;

/**
 * A special parameter-class for the cutCone-function in the Math2-class.
 * 
 * @author Jan Dérer
 *
 */
public class CutConeParameter extends FunctionParameter {

	/**
	 * Lower limit for the cutCone-function
	 */
	private float lowLim;
	
	/**
	 * Upper limit for the cutCone-Function
	 */
	private float upLim;
	
	/**
	 * Parameter for the cutCone-Function
	 */
	private float a;
	
	/**
	 * Standardconstructor initialized the attributes with 0.0f
	 */
	public CutConeParameter() {
		super();
		lowLim = 0.0f;
		upLim = 0.0f;
		a = 0.0f;
	}

	/**
	 * Specialconstructor to initialized the attributes with custom-values
	 * @param correct The function works correct
	 * @param exists Values exists after running a function
	 * @param lowLim Lower limit
	 * @param upLim Upper limit
	 * @param a Parameter
	 */
	public CutConeParameter(boolean correct, boolean exists, float lowLim, float upLim, float a) {
		super(correct, exists);
		this.lowLim = lowLim;
		this.upLim = upLim;
		this.a = a;
	}

	/**
	 * Getter for the parameter
	 * @return Parameter
	 */
	public float getA() {
		return a;
	}

	/**
	 * Setter for the parameter
	 * @param a Parameter
	 */
	public void setA(float a) {
		this.a = a;
	}

	/**
	 * Getter for the lower limit parameter
	 * @return The lower limit parameter
	 */
	public float getLowLim() {
		return lowLim;
	}

	/**
	 * Setter for the lower limit parameter
	 * @param lowLim The new lower limit
	 */
	public void setLowLim(float lowLim) {
		this.lowLim = lowLim;
	}

	/**
	 * Getter for the upper limit parameter
	 * @return The upper limit parameter
	 */
	public float getUpLim() {
		return upLim;
	}

	/**
	 * Setter for the lower limit parameter
	 * @param upLim The new upper limit
	 */
	public void setUpLim(float upLim) {
		this.upLim = upLim;
	}
	
}
