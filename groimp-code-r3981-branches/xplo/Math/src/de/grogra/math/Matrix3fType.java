// This file has been automatically generated
// from /home/nmi/groimp/Math/src/de/grogra/math/Matrix3fType.sco.

package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;

public class Matrix3fType extends SCOType
{
	public static final Matrix3fType $TYPE;

	public static final Field m00$FIELD;
	public static final Field m01$FIELD;
	public static final Field m02$FIELD;
	public static final Field m10$FIELD;
	public static final Field m11$FIELD;
	public static final Field m12$FIELD;
	public static final Field m20$FIELD;
	public static final Field m21$FIELD;
	public static final Field m22$FIELD;

	static
	{
		$TYPE = new Matrix3fType (Matrix3f.class, SCOType.$TYPE);
		m00$FIELD = $TYPE.addManagedField ("m00",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 0);
		m01$FIELD = $TYPE.addManagedField ("m01",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 1);
		m02$FIELD = $TYPE.addManagedField ("m02",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 2);
		m10$FIELD = $TYPE.addManagedField ("m10",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 3);
		m11$FIELD = $TYPE.addManagedField ("m11",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 4);
		m12$FIELD = $TYPE.addManagedField ("m12",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 5);
		m20$FIELD = $TYPE.addManagedField ("m20",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 6);
		m21$FIELD = $TYPE.addManagedField ("m21",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 7);
		m22$FIELD = $TYPE.addManagedField ("m22",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 8);
		$TYPE.validate ();
	}

	public Matrix3fType (Class c, de.grogra.persistence.SCOType supertype)
	{
		super (c, supertype);
	}

	public Matrix3fType (Matrix3f representative, de.grogra.persistence.SCOType supertype)
	{
		super (representative, supertype);
	}

	protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 9;

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					((Matrix3f) o).m00 = (float) value;
					return;
				case SCOType.FIELD_COUNT + 1:
					((Matrix3f) o).m01 = (float) value;
					return;
				case SCOType.FIELD_COUNT + 2:
					((Matrix3f) o).m02 = (float) value;
					return;
				case SCOType.FIELD_COUNT + 3:
					((Matrix3f) o).m10 = (float) value;
					return;
				case SCOType.FIELD_COUNT + 4:
					((Matrix3f) o).m11 = (float) value;
					return;
				case SCOType.FIELD_COUNT + 5:
					((Matrix3f) o).m12 = (float) value;
					return;
				case SCOType.FIELD_COUNT + 6:
					((Matrix3f) o).m20 = (float) value;
					return;
				case SCOType.FIELD_COUNT + 7:
					((Matrix3f) o).m21 = (float) value;
					return;
				case SCOType.FIELD_COUNT + 8:
					((Matrix3f) o).m22 = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					return ((Matrix3f) o).m00;
				case SCOType.FIELD_COUNT + 1:
					return ((Matrix3f) o).m01;
				case SCOType.FIELD_COUNT + 2:
					return ((Matrix3f) o).m02;
				case SCOType.FIELD_COUNT + 3:
					return ((Matrix3f) o).m10;
				case SCOType.FIELD_COUNT + 4:
					return ((Matrix3f) o).m11;
				case SCOType.FIELD_COUNT + 5:
					return ((Matrix3f) o).m12;
				case SCOType.FIELD_COUNT + 6:
					return ((Matrix3f) o).m20;
				case SCOType.FIELD_COUNT + 7:
					return ((Matrix3f) o).m21;
				case SCOType.FIELD_COUNT + 8:
					return ((Matrix3f) o).m22;
			}
			return super.getFloat (o, id);
		}


@Override
protected Object newInstance (Object repr)
{
	return ((Matrix3f) repr).clone ();
}

@Override
protected Object cloneNonsharedObject (Object o, boolean deep)
{
	return ((Matrix3f) o).clone ();
}


static
{
	$TYPE.setSerializationMethod (ManageableType.LIST_SERIALIZATION);
}
}
