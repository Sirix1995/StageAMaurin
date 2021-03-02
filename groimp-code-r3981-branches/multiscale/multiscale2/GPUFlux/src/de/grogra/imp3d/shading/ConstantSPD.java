package de.grogra.imp3d.shading;

import java.io.Serializable;

import de.grogra.imp3d.spectral.ConstantSpectralCurve;
import de.grogra.imp3d.spectral.SpectralCurve;
import de.grogra.persistence.SCOType;

public class ConstantSPD extends SPD
{
	// enh:sco SCOType
	
	float value = 0.5f;
	//enh:field getter setter
	
	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field value$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ConstantSPD representative, de.grogra.persistence.SCOType supertype)
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
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((ConstantSPD) o).value = (float) value;
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
					return ((ConstantSPD) o).getValue ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new ConstantSPD ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (ConstantSPD.class);
		value$FIELD = Type._addManagedField ($TYPE, "value", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public float getValue ()
	{
		return value;
	}

	public void setValue (float value)
	{
		this.value = (float) value;
	}

//enh:end

	public ConstantSPD(float value)
	{
		super();
		setValue(value);
	}
	
	public ConstantSPD()
	{
		super();
	}

	@Override
	public SpectralCurve getSpectralDistribution() {
		return new ConstantSpectralCurve(value);
	}
		
}