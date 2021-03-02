/**
 * 
 */
package de.grogra.ext.sunshine.shader;

/**
 * @author Thomas
 *
 */
public class SunshineImage implements SunshineShader
{
	private int x = 0;;
	private int y = 0;;
	
	public int getID()
	{
		return IMAGE_ID;
	}
	
	
	public void setTexture(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	
	public String getCode()
	{
		return 	"vec4 getImage(UV uv, int x, int y)\n" +
				"{\n" +
				"	return texture2DRect(tex, vec2(float(x) + uv.u*512.0, float(y) + uv.v*512.0));\n" +
				"}\n";
	} //getCode

} //class
