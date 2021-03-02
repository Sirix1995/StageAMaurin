/**
 * 
 */
package de.grogra.ext.sunshine.output;

/**
 * @author Mankmil
 *
 */
public class LogarithmicOutput extends LinearOutput
{

	public void applyValues(float[] values, float scale) 
	{		
		values[0] = (float)(Math.log10(1.0+values[0]) / Math.log10(1.0+tempMax)); 
		values[1] = (float)(Math.log10(1.0+values[1]) / Math.log10(1.0+tempMax)); 
		values[2] = (float)(Math.log10(1.0+values[2]) / Math.log10(1.0+tempMax));
		
//		values[0] = (float)Math.pow(values[0], 1/scale);
//		values[1] = (float)Math.pow(values[1], 1/scale);
//		values[2] = (float)Math.pow(values[2], 1/scale);
		
		// Result of clamping the scaled values looks much better than the old method..
		values[0] = Math.min(values[0] * scale, max); 
		values[1] = Math.min(values[1] * scale, max); 
		values[2] = Math.min(values[2] * scale, max);
		
	}
}

