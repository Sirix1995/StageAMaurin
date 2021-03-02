package de.grogra.animation.util;

import java.awt.Color;

public class Util {

	/**
	 * Rounds a double value to an int value.
	 * @param a
	 * @return
	 */
	public static int round(double a) {
		return (int) Math.round(a);
	}
	
	/**
	 * Converts a color object to an int value (only rgb).
	 * @param color
	 * @return
	 */
	public static int rgbToInt(Color color) {
		return color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
	}
	
	/**
	 * Returns true, if value is between rangeStart and rangeEnd. The borders belong
	 * to the range.
	 * @param value
	 * @param rangeStart
	 * @param rangeEnd
	 * @return
	 */
	public static boolean inRange(double value, double rangeStart, double rangeEnd) {
		if ((rangeStart <= value) && (value <= rangeEnd))
			return true;
		return false;
	}
	
}
