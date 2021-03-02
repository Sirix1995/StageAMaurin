/**
 * 
 */
package de.grogra.ext.sunshine.shader;

/**
 * @author Thomas
 *
 */
public class PhongHandler
{
	private String ShaderTree;
	private int phongCounter = 0;
	private boolean[] functions;
	
	
	public PhongHandler()
	{
		ShaderTree = new String();
		functions = new boolean[4];
	}

	public void setPhong(SunshinePhong sp, int pc)
	{
		ShaderTree += sp.getCode();
		phongCounter = pc;
	} //setPhong
	
	
	public String getPhong()
	{
		String caseOf = new String();
		if(phongCounter > 0)
		for(int i = 0; i < phongCounter; i++)
		{
			caseOf += "	if(phong == "+i+")\n" +
					" result = calcPhong(getPhong"+i+"(getUV(td)), light, " +
					"normal, td, counter);\n";
		} //for
		
		
		ShaderTree += "\n" +
			"Phong getPhong(float id, TraceData td)\n" +
			"{\n" +
			"	td.iPoint = getInverseMatrix(td.id) * td.iPoint;\n" +
			"	Phong result = Phong(vec4(0.0), vec4(0.0), vec4(0.0), vec4(0.0), vec4(0.0), vec4(0.0), vec4(0.0));\n";
			for(int i = 0; i < phongCounter; i++)
			{
				ShaderTree += "	if(id == "+i+".0)\n" +
						"	result = getPhong"+i+"(getUV(td));\n";
			} //for
			
		ShaderTree += "\n" +
			"	return result;\n" +
			"}\n\n" +
			"vec3 calcDirectIllumination(Light light, TraceData td, float angle, vec3 normal, float phong, int counter)\n" +
			"{\n" +
			"	vec3 result;\n" +
			"	if(phong < 0.0)\n" +
			"	{\n" +
			" 		result = calcRGB(getObjectColor(td.id).xyz, light, normal, angle);\n" +
			"	}\n" +
			"	else\n" +
			"	{\n" +
			"		result = calcPhong(getPhong(phong, td), light, normal, td, counter);\n" +
			"	}" +
			" return result;\n" +
			"}\n";
			
			return ShaderTree;
	} //getPhong
	
	
	public void appendFunction(SunshineShader shader)
	{
		if( !functions[shader.getID()] )
		{
			ShaderTree += shader.getCode();
			functions[shader.getID()] = true;
		} //if
		
	} //attachFunctions
}
