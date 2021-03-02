package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;
begin
Tuple2d
SCOType
double	x
double	y
end

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
