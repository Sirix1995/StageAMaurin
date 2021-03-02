// This file has been automatically generated
// from /home/nmi/groimp/Math/src/de/grogra/math/Tuple2fType.sco.

package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;

public class Tuple2fType extends SCOType
{
	public static final Tuple2fType $TYPE;

	public static final Field x$FIELD;
	public static final Field y$FIELD;

	static
	{
		$TYPE = new Tuple2fType (Tuple2f.class, SCOType.$TYPE);
		x$FIELD = $TYPE.addManagedField ("x",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 0);
		y$FIELD = $TYPE.addManagedField ("y",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 1);
		$TYPE.validate ();
	}

	public Tuple2fType (Class c, de.grogra.persistence.SCOType supertype)
	{
		super (c, supertype);
	}

	public Tuple2fType (Tuple2f representative, de.grogra.persistence.SCOType supertype)
	{
		super (representative, supertype);
	}

	protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 2;

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					((Tuple2f) o).x = (float) value;
					return;
				case SCOType.FIELD_COUNT + 1:
					((Tuple2f) o).y = (float) value;
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
					return ((Tuple2f) o).x;
				case SCOType.FIELD_COUNT + 1:
					return ((Tuple2f) o).y;
			}
			return super.getFloat (o, id);
		}


@Override
protected Object newInstance (Object repr)
{
	return ((Tuple2f) repr).clone ();
}


@Override
protected Object cloneNonsharedObject (Object o, boolean deep)
{
	return ((Tuple2f) o).clone ();
}


static
{
	$TYPE.setSerializationMethod (ManageableType.LIST_SERIALIZATION);
}


public static final SCOType VECTOR
    = (SCOType) new SCOType (new Vector2f (), $TYPE).validate ();
public static final SCOType POINT
    = (SCOType) new SCOType (new Point2f (), $TYPE).validate ();
}
