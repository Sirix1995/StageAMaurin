/**
 * 
 */
package de.grogra.ext.sunshine.shader;

/**
 * @author Thomas
 *
 */
public interface SunshineShader
{
	final int CHECKER_ID 	= 0;
	final int IMAGE_ID 		= 1;
	final int UVTRAVO_ID 	= 2;
	final int BLEND_ID 		= 3;
	
	public int getID();
	
	public String getCode();
}
