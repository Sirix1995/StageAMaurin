// This file has been automatically generated
// from /home/nmi/groimp/Math/src/de/grogra/math/Tuple3dType.sco.

package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;

public class Tuple3dType extends SCOType
{
	public static final Tuple3dType $TYPE;

	public static final Field x$FIELD;
	public static final Field y$FIELD;
	public static final Field z$FIELD;

	static
	{
		$TYPE = new Tuple3dType (Tuple3d.class, SCOType.$TYPE);
		x$FIELD = $TYPE.addManagedField ("x",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 0);
		y$FIELD = $TYPE.addManagedField ("y",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 1);
		z$FIELD = $TYPE.addManagedField ("z",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 2);
		$TYPE.validate ();
	}

	public Tuple3dType (Class c, de.grogra.persistence.SCOType supertype)
	{
		super (c, supertype);
	}

	public Tuple3dType (Tuple3d representative, de.grogra.persistence.SCOType supertype)
	{
		super (representative, supertype);
	}

	protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 3;

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					((Tuple3d) o).x = (double) value;
					return;
				case SCOType.FIELD_COUNT + 1:
					((Tuple3d) o).y = (double) value;
					return;
				case SCOType.FIELD_COUNT + 2:
					((Tuple3d) o).z = (double) value;
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
					return ((Tuple3d) o).x;
				case SCOType.FIELD_COUNT + 1:
					return ((Tuple3d) o).y;
				case SCOType.FIELD_COUNT + 2:
					return ((Tuple3d) o).z;
			}
			return super.getDouble (o, id);
		}


@Override
protected Object newInstance (Object repr)
{
	return ((Tuple3d) repr).clone ();
}

@Override
protected Object cloneNonsharedObject (Object o, boolean deep)
{
	return ((Tuple3d) o).clone ();
}


static
{
	$TYPE.setSerializationMethod (ManageableType.LIST_SERIALIZATION);
}

public static final SCOType VECTOR
    = (SCOType) new SCOType (new Vector3d (), $TYPE).validate ();
public static final SCOType POINT
    = (SCOType) new SCOType (new Point3d (), $TYPE).validate ();
}
