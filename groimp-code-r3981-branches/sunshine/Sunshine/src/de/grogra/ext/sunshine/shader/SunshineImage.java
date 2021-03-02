/**
 * 
 */
package de.grogra.ext.sunshine.shader;

/**
 * @author Thomas
 *
 */
public class SunshineImage extends SunshineShader
{
	public int getID()
	{
		return IMAGE_ID;
	}

	public String getCode()
	{
		return 	"vec4 getImage(UV uv, float x, float y)\n" +
				"{\n" +
				"	return texture2DRect(texTexture, vec2(x + uv.u*512.0, y + uv.v*512.0));\n" +
				"}\n";
	} //getCode
	
	
	public String getMethodCall()
	{
		return "getImage(UV, X_COORD, Y_COORD)";
	}

} //class
