package de.grogra.ext.x3d.objects;

import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.Util;
import de.grogra.ext.x3d.interfaces.Definable;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;

/**
 * This class saves all informations of a x3d material element.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DMaterial extends ShareableBase implements Definable {
	
	//enh:sco SCOType

	protected float ambientIntensity = 0.2f;
	protected Tuple3f diffuseColor = new Color3f(0.8f, 0.8f, 0.8f);
	protected Tuple3f emissiveColor = new Color3f(0.0f, 0.0f, 0.0f);
	protected float shininess = 0.2f;
	protected Tuple3f specularColor = new Color3f(0.0f, 0.0f, 0.0f);
	protected float transparency = 0.0f;
	
	protected String def = null;
	protected String use = null;
	
	/**
	 * Constructor.
	 */
	public X3DMaterial() {
		super();
	}
	
	/**
	 * Creates a new instance of this class. X3D attributes are read and set in
	 * corresponding class attributes.
	 * @param atts
	 * @return
	 */
	public static X3DMaterial createInstance(Attributes atts) {
		X3DMaterial newMaterial = new X3DMaterial();
		
		String valueString;
		
		valueString = atts.getValue("ambientIntensity");
		if (valueString != null)
			newMaterial.ambientIntensity = Float.valueOf(valueString);
		
		valueString = atts.getValue("diffuseColor");
		if (valueString != null)
			newMaterial.diffuseColor = Util.splitStringToTuple3f(new Color3f(), valueString);
		
		valueString = atts.getValue("emissiveColor");
		if (valueString != null)
			newMaterial.emissiveColor = Util.splitStringToTuple3f(new Color3f(), valueString);

		valueString = atts.getValue("shininess");
		if (valueString != null)
			newMaterial.shininess = Float.valueOf(valueString);
		
		valueString = atts.getValue("specularColor");
		if (valueString != null)
			newMaterial.specularColor = Util.splitStringToTuple3f(new Color3f(), valueString);

		valueString = atts.getValue("transparency");
		if (valueString != null)
			newMaterial.transparency = Float.valueOf(valueString);		
		
		valueString = atts.getValue("DEF");
		newMaterial.def = valueString;
		
		valueString = atts.getValue("USE");
		newMaterial.use = valueString;
		
		return newMaterial;
	}

	public float getAmbientIntensity() {
		return ambientIntensity;
	}

	public void setAmbientIntensity(float ambientIntensity) {
		this.ambientIntensity = ambientIntensity;
	}

	public Tuple3f getDiffuseColor() {
		return diffuseColor;
	}

	public void setDiffuseColor(Color3f diffuseColor) {
		this.diffuseColor = diffuseColor;
	}

	public Tuple3f getEmissiveColor() {
		return emissiveColor;
	}

	public void setEmissiveColor(Color3f emissiveColor) {
		this.emissiveColor = emissiveColor;
	}

	public float getShininess() {
		return shininess;
	}

	public void setShininess(float shininess) {
		this.shininess = shininess;
	}

	public Tuple3f getSpecularColor() {
		return specularColor;
	}

	public void setSpecularColor(Color3f specularColor) {
		this.specularColor = specularColor;
	}

	public float getTransparency() {
		return transparency;
	}

	public void setTransparency(float transparency) {
		this.transparency = transparency;
	}

	public String getDef() {
		return def;
	}

	public String getUse() {
		return use;
	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (X3DMaterial representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new X3DMaterial ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (X3DMaterial.class);
		$TYPE.validate ();
	}

//enh:end
}


