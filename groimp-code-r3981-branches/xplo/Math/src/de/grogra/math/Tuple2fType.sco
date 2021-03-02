package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;
begin
Tuple2f
SCOType
float	x
float	y
end

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
