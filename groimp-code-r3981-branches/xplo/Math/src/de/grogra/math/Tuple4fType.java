// This file has been automatically generated
// from /home/nmi/groimp/Math/src/de/grogra/math/Tuple4fType.sco.

package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;

public class Tuple4fType extends SCOType
{
	public static final Tuple4fType $TYPE;

	public static final Field x$FIELD;
	public static final Field y$FIELD;
	public static final Field z$FIELD;
	public static final Field w$FIELD;

	static
	{
		$TYPE = new Tuple4fType (Tuple4f.class, SCOType.$TYPE);
		x$FIELD = $TYPE.addManagedField ("x",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 0);
		y$FIELD = $TYPE.addManagedField ("y",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 1);
		z$FIELD = $TYPE.addManagedField ("z",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 2);
		w$FIELD = $TYPE.addManagedField ("w",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 3);
		$TYPE.validate ();
	}

	public Tuple4fType (Class c, de.grogra.persistence.SCOType supertype)
	{
		super (c, supertype);
	}

	public Tuple4fType (Tuple4f representative, de.grogra.persistence.SCOType supertype)
	{
		super (representative, supertype);
	}

	protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 4;

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					((Tuple4f) o).x = (float) value;
					return;
				case SCOType.FIELD_COUNT + 1:
					((Tuple4f) o).y = (float) value;
					return;
				case SCOType.FIELD_COUNT + 2:
					((Tuple4f) o).z = (float) value;
					return;
				case SCOType.FIELD_COUNT + 3:
					((Tuple4f) o).w = (float) value;
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
					return ((Tuple4f) o).x;
				case SCOType.FIELD_COUNT + 1:
					return ((Tuple4f) o).y;
				case SCOType.FIELD_COUNT + 2:
					return ((Tuple4f) o).z;
				case SCOType.FIELD_COUNT + 3:
					return ((Tuple4f) o).w;
			}
			return super.getFloat (o, id);
		}


@Override
protected Object newInstance (Object repr)
{
	return ((Tuple4f) repr).clone ();
}

@Override
protected Object cloneNonsharedObject (Object o, boolean deep)
{
	return ((Tuple4f) o).clone ();
}


static
{
	$TYPE.setSerializationMethod (ManageableType.LIST_SERIALIZATION);
}

public static final SCOType VECTOR
    = (SCOType) new SCOType (new Vector4f (), $TYPE).validate ();
public static final SCOType POINT
    = (SCOType) new SCOType (new Point4f (), $TYPE).validate ();
public static final SCOType COLOR
    = (SCOType) new SCOType (new Color4f (), $TYPE).validate ();
public static final SCOType QUAT
    = (SCOType) new SCOType (new Quat4f (), $TYPE).validate ();
}
