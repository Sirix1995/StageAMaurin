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
 * A basic class for function-parameters. Inherit from this class to extends with special parameters.
 * Mainly used for old GroGra-functions.
 * 
 * @author Jan Dérer
 *
 */
public class FunctionParameter {

	/**
	 * This attribute returns true, when the function works correct.
	 */
	private boolean correct;
	
	/**
	 * This attribute returns true, when defined values exists after running a function.
	 */
	private boolean exists;

	/**
	 * Standardconstructor initialized the attributes with false
	 */
	public FunctionParameter() {
		super();
		correct = false;
		exists = false;
	}

	/**
	 * Specialconstructor to initialized the attributes with custom-values
	 * @param correct The function works correct
	 * @param exists Values exists after running a function
	 */
	public FunctionParameter(boolean correct, boolean exists) {
		super();
		this.correct = correct;
		this.exists = exists;
	}

	/**
	 * Getter for the exists-attribute
	 * @return true, when the function works correct
	 */
	public boolean isExists() {
		return exists;
	}

	/**
	 * Setter for the exists-attribute
	 * @param exists State for the exists-attribute
	 */
	public void setExists(boolean exists) {
		this.exists = exists;
	}

	/**
	 * Getter for the correct-attribute
	 * @return true, when defined values exists after running a function
	 */
	public boolean isCorrect() {
		return correct;
	}

	/**
	 * Setter for the correct-attribute
	 * @param correct State for the correct-attribute
	 */
	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

}
