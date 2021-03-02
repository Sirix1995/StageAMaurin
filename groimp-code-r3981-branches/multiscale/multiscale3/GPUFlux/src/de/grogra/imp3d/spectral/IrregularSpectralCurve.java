package de.grogra.imp3d.spectral;

/**
 * This class allows spectral curves to be defined from irregularly sampled
 * data. Note that the wavelength array is assumed to be sorted low to high. Any
 * values beyond the defined range will simply be extended to infinity from the
 * end points. Points inside the valid range will be linearly interpolated
 * between the two nearest samples. No explicit error checking is performed, but
 * this class will run into {@link ArrayIndexOutOfBoundsException}s if the
 * array lengths don't match.
 */
public class IrregularSpectralCurve extends SpectralCurve {
  
	//enh:sco de.grogra.persistence.SCOType
	
	private float[] wavelengths;
    //enh:field
    private float[] amplitudes;
    //enh:field

    //enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field wavelengths$FIELD;
	public static final Type.Field amplitudes$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (IrregularSpectralCurve representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT + 2;

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
					((IrregularSpectralCurve) o).wavelengths = (float[]) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((IrregularSpectralCurve) o).amplitudes = (float[]) value;
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
					return ((IrregularSpectralCurve) o).wavelengths;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((IrregularSpectralCurve) o).amplitudes;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new IrregularSpectralCurve ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (IrregularSpectralCurve.class);
		wavelengths$FIELD = Type._addManagedField ($TYPE, "wavelengths", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, Type.SUPER_FIELD_COUNT + 0);
		amplitudes$FIELD = Type._addManagedField ($TYPE, "amplitudes", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

//enh:end
    
	public IrregularSpectralCurve()
	{
		
	}
	
    /**
     * Define an irregular spectral curve from the provided (sorted) wavelengths
     * and amplitude data. The wavelength array is assumed to contain values in
     * nanometers. Array lengths must match.
     * 
     * @param wavelengths sampled wavelengths in nm
     * @param amplitudes amplitude of the curve at the sampled points
     */
    public IrregularSpectralCurve(float[] wavelengths, float[] amplitudes) {
        this.wavelengths = wavelengths;
        this.amplitudes = amplitudes;
        if (wavelengths.length != amplitudes.length)
            throw new RuntimeException(String.format("Error creating irregular spectral curve: %d wavelengths and %d amplitudes", wavelengths.length, amplitudes.length));
        for (int i = 1; i < wavelengths.length; i++)
            if (wavelengths[i - 1] >= wavelengths[i])
                throw new RuntimeException(String.format("Error creating irregular spectral curve: values are not sorted - error at index %d", i));
    }

    public float sample(float lambda) {
        if (wavelengths.length == 0)
            return 0; // no data
        if (wavelengths.length == 1 || lambda <= wavelengths[0])
            return amplitudes[0];
        if (lambda >= wavelengths[wavelengths.length - 1])
            return amplitudes[wavelengths.length - 1];
        for (int i = 1; i < wavelengths.length; i++) {
            if (lambda < wavelengths[i]) {
                float dx = (lambda - wavelengths[i - 1]) / (wavelengths[i] - wavelengths[i - 1]);
                return (1 - dx) * amplitudes[i - 1] + dx * amplitudes[i];
            }
        }
        return amplitudes[wavelengths.length - 1];
    }
}
