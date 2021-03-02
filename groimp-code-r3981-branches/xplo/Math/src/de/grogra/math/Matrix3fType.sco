package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;
begin
Matrix3f
SCOType
float	m00
float	m01
float	m02
float	m10
float	m11
float	m12
float	m20
float	m21
float	m22
end

@Override
protected Object newInstance (Object repr)
{
	return ((Matrix3f) repr).clone ();
}

@Override
protected Object cloneNonsharedObject (Object o, boolean deep)
{
	return ((Matrix3f) o).clone ();
}


static
{
	$TYPE.setSerializationMethod (ManageableType.LIST_SERIALIZATION);
}
