package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;
begin
Tuple4d
SCOType
double	x
double	y
double	z
double	w
end

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
