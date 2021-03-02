package de.grogra.ext.sunshine.output;

/**
 * The render process returns float values. To display this values in the
 * directly on screen might has some strange results. The render output contains
 * "High Dynamic Range" information which is hard to be displayed.
 * To apply the output to the display value-range we need a strategy to do so.
 * Implementation of this interface handle the problem to map a big range to
 * a smaller one.
 * 
 * @author adge-k
 *
 */
public interface SunshineOutputStrategy {
	
	/**
	 * This method sets the min and max value for the output color.
	 * 
	 * @param min
	 * @param max
	 */
	void setMinMax(float min, float max);
	
	void gatherValues(float[] values);
	
	/**
	 * This function computes a certain color depending on the
	 * class which implement this interface
	 * 
	 */
	void applyValues(float[] values, float scale);
	
	boolean needPrePass();
}
