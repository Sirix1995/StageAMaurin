// This file has been automatically generated
// from /home/nmi/groimp/Math/src/de/grogra/math/Matrix3dType.sco.

package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;

public class Matrix3dType extends SCOType
{
	public static final Matrix3dType $TYPE;

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
		$TYPE = new Matrix3dType (Matrix3d.class, SCOType.$TYPE);
		m00$FIELD = $TYPE.addManagedField ("m00",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 0);
		m01$FIELD = $TYPE.addManagedField ("m01",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 1);
		m02$FIELD = $TYPE.addManagedField ("m02",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 2);
		m10$FIELD = $TYPE.addManagedField ("m10",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 3);
		m11$FIELD = $TYPE.addManagedField ("m11",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 4);
		m12$FIELD = $TYPE.addManagedField ("m12",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 5);
		m20$FIELD = $TYPE.addManagedField ("m20",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 6);
		m21$FIELD = $TYPE.addManagedField ("m21",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 7);
		m22$FIELD = $TYPE.addManagedField ("m22",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 8);
		$TYPE.validate ();
	}

	public Matrix3dType (Class c, de.grogra.persistence.SCOType supertype)
	{
		super (c, supertype);
	}

	public Matrix3dType (Matrix3d representative, de.grogra.persistence.SCOType supertype)
	{
		super (representative, supertype);
	}

	protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 9;

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					((Matrix3d) o).m00 = (double) value;
					return;
				case SCOType.FIELD_COUNT + 1:
					((Matrix3d) o).m01 = (double) value;
					return;
				case SCOType.FIELD_COUNT + 2:
					((Matrix3d) o).m02 = (double) value;
					return;
				case SCOType.FIELD_COUNT + 3:
					((Matrix3d) o).m10 = (double) value;
					return;
				case SCOType.FIELD_COUNT + 4:
					((Matrix3d) o).m11 = (double) value;
					return;
				case SCOType.FIELD_COUNT + 5:
					((Matrix3d) o).m12 = (double) value;
					return;
				case SCOType.FIELD_COUNT + 6:
					((Matrix3d) o).m20 = (double) value;
					return;
				case SCOType.FIELD_COUNT + 7:
					((Matrix3d) o).m21 = (double) value;
					return;
				case SCOType.FIELD_COUNT + 8:
					((Matrix3d) o).m22 = (double) value;
					return;
			}
			super.setDouble (o, id, value);
		}

		@Override
		protected double getDouble (Object o, int id)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					return ((Matrix3d) o).m00;
				case SCOType.FIELD_COUNT + 1:
					return ((Matrix3d) o).m01;
				case SCOType.FIELD_COUNT + 2:
					return ((Matrix3d) o).m02;
				case SCOType.FIELD_COUNT + 3:
					return ((Matrix3d) o).m10;
				case SCOType.FIELD_COUNT + 4:
					return ((Matrix3d) o).m11;
				case SCOType.FIELD_COUNT + 5:
					return ((Matrix3d) o).m12;
				case SCOType.FIELD_COUNT + 6:
					return ((Matrix3d) o).m20;
				case SCOType.FIELD_COUNT + 7:
					return ((Matrix3d) o).m21;
				case SCOType.FIELD_COUNT + 8:
					return ((Matrix3d) o).m22;
			}
			return super.getDouble (o, id);
		}


@Override
protected Object newInstance (Object repr)
{
	return ((Matrix3d) repr).clone ();
}

@Override
protected Object cloneNonsharedObject (Object o, boolean deep)
{
	return ((Matrix3d) o).clone ();
}


static
{
	$TYPE.setSerializationMethod (ManageableType.LIST_SERIALIZATION);
}
}
