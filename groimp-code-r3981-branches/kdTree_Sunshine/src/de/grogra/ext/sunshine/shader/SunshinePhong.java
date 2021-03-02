/**
 * 
 */
package de.grogra.ext.sunshine.shader;

/**
 * @author Thomas
 *
 */
public class SunshinePhong
{
	private String code, name, dcm, scm, acm, ecm, trans, diffTrans, srm;

	
	public SunshinePhong(int counter)
	{
		name = "Phong"+counter;
		code = new String();
	} //constructor
	
	
	public void setDiffuse(String diffuse)
	{
		dcm = diffuse;
	} //setDiffuse
	
	
	public void setSpecular(String specular)
	{
		scm = specular;
	}
	
	
	public void setEmissive(String emissive)
	{
		ecm = emissive;
	}
	
	
	public void setAmbient(String ambient)
	{
		acm = ambient;
	}
	
	public void setTransparency(String trans)
	{
		this.trans = trans;
	}
	
	public void setDiffTrans(String diffTrans)
	{
		this.diffTrans = diffTrans;
	}
	
	public void setShininess(String srm)
	{
		this.srm = srm;
	}
	
	
	public String getCode()
	{
		return code +=
			"Phong get"+name+"(UV uv)\n" +
			"{\n" +
			"	return Phong("+ecm+","+acm+","+dcm+","+scm+","+srm+","+trans+","+diffTrans+");\n" +
			"}\n";
		
	} //getCode
	
} //class
