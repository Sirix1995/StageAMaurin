// This file has been automatically generated
// from /home/nmi/groimp/Math/src/de/grogra/math/Tuple2dType.sco.

package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;

public class Tuple2dType extends SCOType
{
	public static final Tuple2dType $TYPE;

	public static final Field x$FIELD;
	public static final Field y$FIELD;

	static
	{
		$TYPE = new Tuple2dType (Tuple2d.class, SCOType.$TYPE);
		x$FIELD = $TYPE.addManagedField ("x",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 0);
		y$FIELD = $TYPE.addManagedField ("y",  0 | Field.SCO, de.grogra.reflect.Type.DOUBLE, null, SCOType.FIELD_COUNT + 1);
		$TYPE.validate ();
	}

	public Tuple2dType (Class c, de.grogra.persistence.SCOType supertype)
	{
		super (c, supertype);
	}

	public Tuple2dType (Tuple2d representative, de.grogra.persistence.SCOType supertype)
	{
		super (representative, supertype);
	}

	protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 2;

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					((Tuple2d) o).x = (double) value;
					return;
				case SCOType.FIELD_COUNT + 1:
					((Tuple2d) o).y = (double) value;
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
					return ((Tuple2d) o).x;
				case SCOType.FIELD_COUNT + 1:
					return ((Tuple2d) o).y;
			}
			return super.getDouble (o, id);
		}


@Override
protected Object newInstance (Object repr)
{
	return ((Tuple2d) repr).clone ();
}


@Override
protected Object cloneNonsharedObject (Object o, boolean deep)
{
	return ((Tuple2d) o).clone ();
}


static
{
	$TYPE.setSerializationMethod (ManageableType.LIST_SERIALIZATION);
}


public static final SCOType VECTOR
    = (SCOType) new SCOType (new Vector2d (), $TYPE).validate ();
public static final SCOType POINT
    = (SCOType) new SCOType (new Point2d (), $TYPE).validate ();
}
