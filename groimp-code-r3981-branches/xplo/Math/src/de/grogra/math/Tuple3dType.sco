package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;
begin
Tuple3d
SCOType
double	x
double	y
double	z
end

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
