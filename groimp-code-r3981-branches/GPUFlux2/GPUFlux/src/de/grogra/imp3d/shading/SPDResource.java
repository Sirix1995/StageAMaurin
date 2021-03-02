package de.grogra.imp3d.shading;

import de.grogra.imp3d.spectral.IrregularSpectralCurve;
import de.grogra.persistence.SCOType;

public class SPDResource extends SPDCurve{

	// enh:sco SCOType
	
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SPDResource representative, de.grogra.persistence.SCOType supertype)
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
			return new SPDResource ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SPDResource.class);
		$TYPE.setSerializable(false);
		$TYPE.validate ();
	}

//enh:end
	
	public SPDResource()
	{
		super();
	}
	
	public SPDResource(
			IrregularSpectralCurve irregularSpectralCurve) {
		super(irregularSpectralCurve);
	}
}
