/**
 * 
 */
package de.grogra.ext.sunshine.shader;

import java.util.Hashtable;;

/**
 * @author Thomas
 *
 */
public class NormalHandler
{
	private int[] objects;
	private Hashtable<Integer, String> normals = new Hashtable<Integer, String>(); 
	
	public NormalHandler(int[] objects)
	{
		this.objects = objects;
		
		normals.put(0, new String("if(td.typ == SPHERE)\n{\n normal = td.iPoint.xyz;\n}") );
	}
	
	public String getNormal()
	{
		String normal = new String();
		normal += "vec3 calculateNormal(TraceData td)\n" +
				"{\n" + 
				"	vec3 normal = vec3(0.0);\n";
				
		
		for(int i = 0; i < objects.length-1; i++)
		{
			if(objects[i] > 0)
				normal += normals.get(i);
		}
		
		
		normal += "\n	mat4 m4x4 =	getInverseMatrix(td.id);\n" +
				"	m4x4 = transpose(m4x4);\n" +
				"	mat3 m3x3;\n" + 
				"	m3x3[0] = m4x4[0].xyz;\n" + 
				"	m3x3[1] = m4x4[1].xyz;\n" + 
				"	m3x3[2] = m4x4[2].xyz;\n" + 
				"	return normalize(m3x3 * normal);\n" +
				"\n}";
		
		return normal;
	}
}
