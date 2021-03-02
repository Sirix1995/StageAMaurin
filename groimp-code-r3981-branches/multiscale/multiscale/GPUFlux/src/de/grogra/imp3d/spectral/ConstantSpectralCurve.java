package de.grogra.imp3d.spectral;

/**
 * Very simple class equivalent to a constant spectral curve. Note that this is
 * most likely physically impossible for amplitudes > 0, however this class can
 * be handy since in practice spectral curves end up being integrated against
 * the finite width color matching functions.
 */
public class ConstantSpectralCurve extends SpectralCurve {
   
	//enh:sco de.grogra.persistence.SCOType
	
	private float amp;
	//enh:field

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field amp$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ConstantSpectralCurve representative, de.grogra.persistence.SCOType supertype)
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
					((ConstantSpectralCurve) o).amp = (float) value;
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
					return ((ConstantSpectralCurve) o).amp;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new ConstantSpectralCurve ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (ConstantSpectralCurve.class);
		amp$FIELD = Type._addManagedField ($TYPE, "amp", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

//enh:end
	
	public ConstantSpectralCurve()
	{
		
	}
	
	
    public ConstantSpectralCurve(float amp) {
        this.amp = amp;
    }

    public float sample(float lambda) {
        return amp;
    }
}
