/**
 * 
 */
package de.grogra.ext.sunshine;

import javax.vecmath.Color4f;
import de.grogra.vecmath.Math2;


/**
 * @author Thomas
 *
 */
public class SunshineLightHandler
{
	private int lightCount;
	
	public SunshineLightHandler(int lights)
	{
		lightCount = lights;
	}

	private String getLights()
	{
		String lights = new String();
		
		lights += "	float p = 0.0;\n";
		
		for (int i = 0; i < lightCount; i++)
		{
			lights += "	Light light"+i+" = getLight("+i+"*sizes[LIGHT]);\n	";
		}
		
		lights += "	Light light = light0;\n";
		
		return lights;
	} //getVaiables
	
	private String calcProbalbilities()
	{
		String result = new String();
		
		for(int i = 0; i < lightCount; i++)
		{
			result += "float p"+i+" = light"+i+".power;\n	";
			result += "p += p"+i+";\n	";
		}
		
		result +=
		"if(p < 1e-7)\n" +
		"	{\n" +
		"		p = 0.0;\n" +
		"	}\n" +
		"	else\n" +
		"	{\n" +
		"		p = 1.0 / p;\n" +
		"	}\n	";
		
		for(int i = 0; i < lightCount; i++)
		{
			result += "p"+i+" *= p;\n	";
		}
		
		return result;
	}
	
	private String condition()
	{
		String result = new String("float z = rand();\n");
		
		result +=
			"	if(p == 0.0)\n" +
			"	{\n" +
			"	//take standard light\n" +
			"	} else \n" +
			"	{\n	p = 0.0;\n" + 
			"	if(z <= (p = p + p0))\n" +
			"	{\n" +
			"		light = light0;\n" +
			"	}\n";
		
		for(int i = 0; i < lightCount - 1; i++)
		{
			result +=
				"	else if(z <= (p = p + p"+(i+1)+") )\n" +
				"	{\n" +
				"	 	light = light" + (i+1) + ";// take light "+(i+1)+"\n" +
				"		pos = " + (i+1) + ";\n" +
				"	}\n";
		}
		
		return result + "}\n";
	} //condition
	
	private String randomOrigin()
	{
		return
		"	if(light.type == AREA_LIGHT)\n" +
		"	{\n " +
		"		light.origin = randomOrigin(pos);\n" +
		"	}\n";
	}

	
	public String retrieveLight()
	{		
		return "Light retrieveLight(inout int pos)\n{\n" 
		+ getLights() + calcProbalbilities() + condition() + randomOrigin() +
		"return light;\n}";
	} //retrieveLight
	
	
	
	public static void main(String[] argv)
	{
		SunshineLightHandler lh = new SunshineLightHandler(3);

		System.out.println(Math2.M_1_PI);

	}
}
