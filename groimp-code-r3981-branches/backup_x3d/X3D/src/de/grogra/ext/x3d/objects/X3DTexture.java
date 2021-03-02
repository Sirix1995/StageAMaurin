package de.grogra.ext.x3d.objects;

import de.grogra.ext.x3d.interfaces.Definable;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;

/**
 * Used as superclass for all types of textures.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public abstract class X3DTexture extends ShareableBase implements Definable {
	
	//enh:sco SCOType

	protected String def = null;
	protected String use = null;
	
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

		public Type (X3DTexture representative, de.grogra.persistence.SCOType supertype)
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
	}

	static
	{
		$TYPE = new Type (X3DTexture.class);
		$TYPE.validate ();
	}

//enh:end
}
