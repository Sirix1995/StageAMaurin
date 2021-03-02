package de.grogra.imp3d.spectral;

public class RegularSpectralCurve extends SpectralCurve {
	//enh:sco de.grogra.persistence.SCOType
	
	private float[] spectrum;
    //enh:field
    private float lambdaMin;
    //enh:field
    private float lambdaMax;
    //enh:field
    private float delta;
    //enh:field
    private float invDelta;
    //enh:field
	    
	  //enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field spectrum$FIELD;
	public static final Type.Field lambdaMin$FIELD;
	public static final Type.Field lambdaMax$FIELD;
	public static final Type.Field delta$FIELD;
	public static final Type.Field invDelta$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (RegularSpectralCurve representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT + 5;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((RegularSpectralCurve) o).lambdaMin = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((RegularSpectralCurve) o).lambdaMax = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((RegularSpectralCurve) o).delta = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((RegularSpectralCurve) o).invDelta = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((RegularSpectralCurve) o).lambdaMin;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((RegularSpectralCurve) o).lambdaMax;
				case Type.SUPER_FIELD_COUNT + 3:
					return ((RegularSpectralCurve) o).delta;
				case Type.SUPER_FIELD_COUNT + 4:
					return ((RegularSpectralCurve) o).invDelta;
			}
			return super.getFloat (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((RegularSpectralCurve) o).spectrum = (float[]) value;
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
					return ((RegularSpectralCurve) o).spectrum;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new RegularSpectralCurve ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (RegularSpectralCurve.class);
		spectrum$FIELD = Type._addManagedField ($TYPE, "spectrum", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, Type.SUPER_FIELD_COUNT + 0);
		lambdaMin$FIELD = Type._addManagedField ($TYPE, "lambdaMin", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		lambdaMax$FIELD = Type._addManagedField ($TYPE, "lambdaMax", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		delta$FIELD = Type._addManagedField ($TYPE, "delta", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 3);
		invDelta$FIELD = Type._addManagedField ($TYPE, "invDelta", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 4);
		$TYPE.validate ();
	}

//enh:end
    
	public RegularSpectralCurve()
	{
		
	}
	
    public RegularSpectralCurve(float[] spectrum, float lambdaMin, float lambdaMax) {
        this.lambdaMin = lambdaMin;
        this.lambdaMax = lambdaMax;
        this.spectrum = spectrum;
        delta = (lambdaMax - lambdaMin) / (spectrum.length - 1);
        invDelta = 1 / delta;
    }
    
    public float sample(float lambda) {
        // reject wavelengths outside the valid range
        if (lambda < lambdaMin || lambda > lambdaMax)
            return 0;
        // interpolate the two closest samples linearly
        float x = (lambda - lambdaMin) * invDelta;
        int b0 = (int) x;
        int b1 = Math.min(b0 + 1, spectrum.length - 1);
        float dx = x - b0;
        return (1 - dx) * spectrum[b0] + dx * spectrum[b1];
    }
}
