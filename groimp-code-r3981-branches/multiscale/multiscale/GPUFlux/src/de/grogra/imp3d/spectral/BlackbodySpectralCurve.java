package de.grogra.imp3d.spectral;

public class BlackbodySpectralCurve extends SpectralCurve {
	
	//enh:sco de.grogra.persistence.SCOType
	
    private float temp;
    //enh:field

    //enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field temp$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (BlackbodySpectralCurve representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((BlackbodySpectralCurve) o).temp = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((BlackbodySpectralCurve) o).temp;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new BlackbodySpectralCurve ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (BlackbodySpectralCurve.class);
		temp$FIELD = Type._addManagedField ($TYPE, "temp", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

//enh:end
	
	public BlackbodySpectralCurve()
	{
		
	}
    
    public BlackbodySpectralCurve(float temp) {
        this.temp = temp;
    }

    public float sample(float lambda) {
        double wavelength = lambda * 1e-9;
        return (float) ((3.74183e-16 * Math.pow(wavelength, -5.0)) / (Math.exp(1.4388e-2 / (wavelength * temp)) - 1.0));
    }
}
