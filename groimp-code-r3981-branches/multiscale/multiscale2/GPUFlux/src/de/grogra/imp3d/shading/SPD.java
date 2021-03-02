package de.grogra.imp3d.shading;

import de.grogra.imp3d.spectral.SpectralCurve;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;

public abstract class SPD extends ShareableBase
{
	// enh:sco SCOType
	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SPD representative, de.grogra.persistence.SCOType supertype)
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
		$TYPE = new Type (SPD.class);
		$TYPE.validate ();
	}

//enh:end

	 public abstract SpectralCurve getSpectralDistribution();

}
