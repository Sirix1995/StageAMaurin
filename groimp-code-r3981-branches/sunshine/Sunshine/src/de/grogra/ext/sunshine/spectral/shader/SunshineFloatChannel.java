package de.grogra.ext.sunshine.spectral.shader;

import java.util.Random;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;

public class SunshineFloatChannel extends ShareableBase implements
		SunshineChannel {

	//enh:sco de.grogra.persistence.SCOType
	
	float value = 0f;
	//enh:field
	
	public Float getContent() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	public int getAverageColor() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getFlags() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float computeBSDF(Environment env, Vector3f in, Spectrum specIn,
			Vector3f out, boolean adjoint, Spectrum bsdf) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void generateRandomRays(Environment env, Vector3f out,
			Spectrum specOut, RayList rays, boolean adjoint, Random random) {
		// TODO Auto-generated method stub

	}

	public void computeMaxRays(Environment env, Vector3f in, Spectrum specIn,
			Ray reflected, Tuple3f refVariance, Ray transmitted,
			Tuple3f transVariance) {
		// TODO Auto-generated method stub

	}

	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return false;
	}

	public void shade(Environment env, RayList in, Vector3f out,
			Spectrum specOut, Tuple3d color) {
		// TODO Auto-generated method stub

	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field value$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SunshineFloatChannel representative, de.grogra.persistence.SCOType supertype)
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
					((SunshineFloatChannel) o).value = (float) value;
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
					return ((SunshineFloatChannel) o).value;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SunshineFloatChannel ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SunshineFloatChannel.class);
		value$FIELD = Type._addManagedField ($TYPE, "value", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

//enh:end

}
