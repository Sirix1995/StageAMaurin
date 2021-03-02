/**
 * 
 */
package de.grogra.ext.sunshine.shader;

/**
 * @author Thomas
 *
 */
public abstract class SunshineShader
{
	protected final int CHECKER_ID 	= 0;
	protected final int IMAGE_ID 	= 1;
	protected final int UVTRAVO_ID 	= 2;
	protected final int BLEND_ID 	= 3;
	
	public abstract int getID();
	
	public abstract String getCode();
	
	public abstract String getMethodCall();
}
