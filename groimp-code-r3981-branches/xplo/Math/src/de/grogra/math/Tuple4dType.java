// This file has been automatically generated
// from /home/nmi/groimp/Math/src/de/grogra/math/Tuple4dType.sco.

package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;

public class Tuple4dType extends SCOType
{
	public static final Tuple4dType $TYPE;

	public static final Field x$FIELD;
	public static final Field y$FIELD;
	public static final Field z$FIELD;
	public static final Field w$FIELD;

	static
	{
		$TYPE = new Tuple4dType (Tuple4d.class, SCOType.$TYPE);
		x$FIELD = $TYPE.addManagedField ("x",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 0);
		y$FIELD = $TYPE.addManagedField ("y",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 1);
		z$FIELD = $TYPE.addManagedField ("z",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 2);
		w$FIELD = $TYPE.addManagedField ("w",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 3);
		$TYPE.validate ();
	}

	public Tuple4dType (Class c, de.grogra.persistence.SCOType supertype)
	{
		super (c, supertype);
	}

	public Tuple4dType (Tuple4d representative, de.grogra.persistence.SCOType supertype)
	{
		super (representative, supertype);
	}

	protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 4;

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					((Tuple4d) o).x = (double) value;
					return;
				case SCOType.FIELD_COUNT + 1:
					((Tuple4d) o).y = (double) value;
					return;
				case SCOType.FIELD_COUNT + 2:
					((Tuple4d) o).z = (double) value;
					return;
				case SCOType.FIELD_COUNT + 3:
					((Tuple4d) o).w = (double) value;
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
					return ((Tuple4d) o).x;
				case SCOType.FIELD_COUNT + 1:
					return ((Tuple4d) o).y;
				case SCOType.FIELD_COUNT + 2:
					return ((Tuple4d) o).z;
				case SCOType.FIELD_COUNT + 3:
					return ((Tuple4d) o).w;
			}
			return super.getDouble (o, id);
		}


@Override
protected Object newInstance (Object repr)
{
	return ((Tuple4d) repr).clone ();
}

@Override
protected Object cloneNonsharedObject (Object o, boolean deep)
{
	return ((Tuple4d) o).clone ();
}


static
{
	$TYPE.setSerializationMethod (ManageableType.LIST_SERIALIZATION);
}

public static final SCOType VECTOR
    = (SCOType) new SCOType (new Vector4d (), $TYPE).validate ();
public static final SCOType POINT
    = (SCOType) new SCOType (new Point4d (), $TYPE).validate ();
public static final SCOType QUAT
    = (SCOType) new SCOType (new Quat4d (), $TYPE).validate ();
}
