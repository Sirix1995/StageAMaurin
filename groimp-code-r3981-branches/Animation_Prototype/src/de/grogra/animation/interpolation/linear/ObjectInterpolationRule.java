package de.grogra.animation.interpolation.linear;

import de.grogra.persistence.*;

public class ObjectInterpolationRule extends ShareableBase implements InterpolationRule {

	//enh:sco SCOType

	public Object getInterpolatedValue(Object value1, Object value2,
			double ratio) {
		if (ratio > 0.999)
			return value2;
		return value1;
	}

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

		public Type (ObjectInterpolationRule representative, de.grogra.persistence.SCOType supertype)
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
			return new ObjectInterpolationRule ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (ObjectInterpolationRule.class);
		$TYPE.validate ();
	}

//enh:end

}
