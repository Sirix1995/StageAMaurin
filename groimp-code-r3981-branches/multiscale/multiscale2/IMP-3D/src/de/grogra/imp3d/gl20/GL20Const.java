package de.grogra.imp3d.gl20;

import javax.vecmath.Matrix4d;

public class GL20Const {
	/**
	 * a identity matrix 4x4
	 */
	final public static Matrix4d identityMatrix4d = new Matrix4d(1.0, 0.0, 0.0, 0.0,
																 0.0, 1.0, 0.0, 0.0,
																 0.0, 0.0, 1.0, 0.0,
																 0.0, 0.0, 0.0, 1.0);

	/**
	 * a mask that indicates that ALL has changed
	 */
	final public static int ALL_CHANGED = 0xFFFFFFFF;

	/**
	 * an invalid id
	 */
	final public static int INVALID_ID = -1;
	
	/**
	 * check if a given value is a number in Power-of-2 format
	 * 
	 * @param value number that should checked
	 * @return <code>true</code> given number is in Power-of-2 format
	 * <code>false</code> given number is not Power-of-2 format
	 */
	final static public boolean isPowerOf2(int value) {
		if (value <= 0)
			return false;
		
		while ((value & 0x1) == 0) {
			value >>= 1;
		}
		
		return ((value >> 1) == 0) ? true : false;
	}

	/**
	 * return the first appearance of a set bit starting at the LSB
	 * 
	 * @param value the value that should checked
	 * @return <code>-1</code> no bit is set
	 * otherwise position of the first set bit
	 */
	final static public int getFirstBitPosition(int value) {
		int returnValue = -1;
		
		if (value != 0) {
			returnValue = 0;
			while ((value & 0x1) == 0) {
				value >>= 1;
				returnValue++;
			}
		}
		
		return returnValue;
	}
}