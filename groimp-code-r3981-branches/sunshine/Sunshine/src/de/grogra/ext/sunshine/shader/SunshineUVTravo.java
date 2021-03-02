package de.grogra.ext.sunshine.shader;


/**
 * @author Thoams
 *
 */
public class SunshineUVTravo extends SunshineShader
{
	public int getID()
	{
		return UVTRAVO_ID;
	}
	

	public String getCode()
	{
		return
		"UV getUvFloat(UV uv, float scaleU, float scaleV, float angle) {\n" +
		" 	mat3 m = mat3(1.0);\n" + //identity matrix
		" 	mat3 i = mat3(1.0);\n" +
		" 	m[0][2] = 0.0;\n" +
		" 	m[1][2] = 0.0;\n" +
		" 	i[0][0] = cos(-angle);\n" + //rotz
		" 	i[1][0] = -sin(-angle);\n" +
		" 	i[0][1] = sin(-angle);\n" +
		" 	i[1][1] = cos(-angle);\n" +
		" 	i[2][2] = 1.0;\n" +
		" 	i = i*m;\n" +
		" 	m[0][0] = scaleU;\n" +
		" 	m[1][1] = scaleV;\n" +
		" 	m[0][2] = 0.0;\n" +
		" 	m[1][2] = 0.0;\n" +
		" 	m = m*i;\n" +
		" 	uv.u = m[0][0] * uv.u + m[0][1] * uv.v + m[0][2];\n" +
		" 	uv.v = m[1][0] * uv.u + m[1][1] * uv.v + m[1][2];\n" +
		"	uv.u = mod(uv.u, 1.0);\n" +
		"	uv.v = mod(uv.v, 1.0);\n" +
		" return uv;\n" +
		"}\n";
	}
	
	public String getMethodCall()
	{
		return "getUvFloat(uv, SCALE_U, SCALE_V, ANGLE)";
	}

} //class
