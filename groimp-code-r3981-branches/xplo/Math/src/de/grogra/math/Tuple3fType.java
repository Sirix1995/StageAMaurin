// This file has been automatically generated
// from /home/nmi/groimp/Math/src/de/grogra/math/Tuple3fType.sco.

package de.grogra.math;

import java.awt.*;
import javax.vecmath.*;
import de.grogra.persistence.*;

public class Tuple3fType extends SCOType
{
	public static final Tuple3fType $TYPE;

	public static final Field x$FIELD;
	public static final Field y$FIELD;
	public static final Field z$FIELD;

	static
	{
		$TYPE = new Tuple3fType (Tuple3f.class, SCOType.$TYPE);
		x$FIELD = $TYPE.addManagedField ("x",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 0);
		y$FIELD = $TYPE.addManagedField ("y",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 1);
		z$FIELD = $TYPE.addManagedField ("z",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 2);
		$TYPE.validate ();
	}

	public Tuple3fType (Class c, de.grogra.persistence.SCOType supertype)
	{
		super (c, supertype);
	}

	public Tuple3fType (Tuple3f representative, de.grogra.persistence.SCOType supertype)
	{
		super (representative, supertype);
	}

	protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 3;

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					((Tuple3f) o).x = (float) value;
					return;
				case SCOType.FIELD_COUNT + 1:
					((Tuple3f) o).y = (float) value;
					return;
				case SCOType.FIELD_COUNT + 2:
					((Tuple3f) o).z = (float) value;
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
					return ((Tuple3f) o).x;
				case SCOType.FIELD_COUNT + 1:
					return ((Tuple3f) o).y;
				case SCOType.FIELD_COUNT + 2:
					return ((Tuple3f) o).z;
			}
			return super.getFloat (o, id);
		}


@Override
protected Object newInstance (Object repr)
{
	return ((Tuple3f) repr).clone ();
}

@Override
protected Object cloneNonsharedObject (Object o, boolean deep)
{
	return ((Tuple3f) o).clone ();
}


private static int f2i (float f)
{
	int i = Math.round (f * 255);
	return (i < 0) ? 0 : (i > 255) ? 255 : i;
}


public static int colorToInt (Tuple3f color)
{
	return (f2i (color.x) << 16) + (f2i (color.y) << 8) + f2i (color.z)
		 + (255 << 24);
}


public static void setColor (Tuple3f color, int rgb)
{
	color.x = (1f / 255) * ((rgb >> 16) & 255);
	color.y = (1f / 255) * ((rgb >> 8) & 255);
	color.z = (1f / 255) * (rgb & 255);
}


static
{
	$TYPE.setSerializationMethod (ManageableType.LIST_SERIALIZATION);
}

public static final SCOType VECTOR
    = (SCOType) new SCOType (new Vector3f (), $TYPE).validate ();
public static final SCOType POINT
    = (SCOType) new SCOType (new Point3f (), $TYPE).validate ();
public static final SCOType COLOR
    = (SCOType) new SCOType (new Color3f (), $TYPE).validate ();
}
