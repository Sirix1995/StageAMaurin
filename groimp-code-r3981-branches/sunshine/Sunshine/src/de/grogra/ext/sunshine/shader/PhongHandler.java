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
	private String phongStr = "";
	
	
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
	
	private void createPhong()
	{
		String caseOf = new String();
		String s = new String();
		if(phongCounter > 0)
		for(int i = 0; i < phongCounter; i++)
		{
			caseOf += "	if(phong == "+i+")\n" +
					" result = calcPhong(getPhong"+i+"(getUV(td)), light, " +
					"normal, td, counter);\n";
		} //for
		
		
		s += "\n" +
			"Phong getPhong(float id, TraceData td)\n" +
			"{\n" +
			"	Phong result = Phong(vec4(0.01), vec4(0.01), vec4(0.01), vec4(0.01), vec4(0.01), vec4(0.01), vec4(0.01));\n";
			for(int i = 0; i < phongCounter; i++)
			{
				s += "	if(id == "+i+".0)\n" +
						"	result = getPhong"+i+"(getUV(td));\n";
			} //for
			
		s += "\n" +
			"	return result;\n" +
			"}\n";
		
		phongStr = ShaderTree + s;
	}
	
	public String getPhong()
	{
		if(phongStr.equals(""))
			createPhong();
		
		return phongStr;
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
