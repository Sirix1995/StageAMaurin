package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;
begin
Tuple4f
SCOType
float	x
float	y
float	z
float	w
end

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
