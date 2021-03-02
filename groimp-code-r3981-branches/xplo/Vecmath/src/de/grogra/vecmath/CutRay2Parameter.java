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
 * A special parameter-class for the cutRay2-function in the Math2-class.
 * 
 * @author Jan Dérer
 *
 */
public class CutRay2Parameter extends FunctionParameter {

	/**
	 * First parameter for the cutRay2-Function.
	 */
	private float s1;
	
	/**
	 * Second parameter for the cutRay2-Function.
	 */
	private float s2;
	
	/**
	 * Standardconstructor initialized the attributes with 0.0f
	 */
	public CutRay2Parameter() {
		super();
		s1 = 0.0f;
		s2 = 0.0f;
	}

	/**
	 * Specialconstructor to initialized the attributes with custom-values
	 * @param correct correct The function works correct
	 * @param exists Values exists after running a function
	 * @param s1 First Parameter
	 * @param s2 Second Parameter
	 */
	public CutRay2Parameter(boolean correct, boolean exists, float s1, float s2) {
		super(correct, exists);
		this.s1 = s1;
		this.s2 = s2;
	}

	/**
	 * Getter for the first parameter.
	 * @return The first parameter
	 */
	public float getS1() {
		return s1;
	}

	/**
	 * Setter for the first parameter
	 * @param s1 Set the new value for the first parameter
	 */
	public void setS1(float s1) {
		this.s1 = s1;
	}

	/**
	 * Getter for the second parameter.
	 * @return The second parameter
	 */
	public float getS2() {
		return s2;
	}

	/**
	 * Setter for the second parameter
	 * @param s1 Set the new value for the second parameter
	 */
	public void setS2(float s2) {
		this.s2 = s2;
	}
	
}
