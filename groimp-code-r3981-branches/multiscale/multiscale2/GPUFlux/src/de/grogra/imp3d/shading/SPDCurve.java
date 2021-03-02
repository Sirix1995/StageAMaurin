package de.grogra.imp3d.shading;

import de.grogra.imp3d.spectral.ConstantSpectralCurve;
import de.grogra.imp3d.spectral.SpectralCurve;
import de.grogra.persistence.SCOType;

public class SPDCurve extends SPD 
{
	// enh:sco SCOType
	
	SpectralCurve spectrum = new ConstantSpectralCurve(0.5f);
	//enh:field getter setter
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field spectrum$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SPDCurve representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((SPDCurve) o).spectrum = (SpectralCurve) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((SPDCurve) o).getSpectrum ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SPDCurve ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SPDCurve.class);
		spectrum$FIELD = Type._addManagedField ($TYPE, "spectrum", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SpectralCurve.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public SpectralCurve getSpectrum ()
	{
		return spectrum;
	}

	public void setSpectrum (SpectralCurve value)
	{
		spectrum$FIELD.setObject (this, value);
	}

//enh:end

	SPDCurve()
	{
		super();
	}
	
	public SPDCurve( SpectralCurve spectrum )
	{
		super();
		setSpectrum(spectrum);
	}
	
	@Override
	public SpectralCurve getSpectralDistribution() {
		return spectrum;
	}

	
}
