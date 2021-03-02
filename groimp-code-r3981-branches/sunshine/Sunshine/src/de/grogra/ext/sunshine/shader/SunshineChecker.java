/**
 * 
 */
package de.grogra.ext.sunshine.shader;

/**
 * @author Thomas
 *
 */
public class SunshineChecker extends SunshineShader
{	
	public SunshineChecker()
	{
	}
	
	
	public int getID()
	{
		return CHECKER_ID;
	}
	
	
	public String getCode()
	{
		return 	
		"vec4 getCheckerColor(UV uv, vec4 c1, vec4 c2) {\n" +
		" return mod(floor( 2.0*uv.u ), 2.0) == mod(floor( 2.0*uv.v ), 2.0) ? c1 : c2; \n" +
		"}\n";
				
	} //getCode
	
	public String getMethodCall()
	{
		return "getCheckerColor(UV, COLOR1, COLOR2)";
	}
} //class
