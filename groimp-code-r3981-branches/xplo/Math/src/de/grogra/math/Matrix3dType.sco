package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;
begin
Matrix3d
SCOType
double	m00
double	m01
double	m02
double	m10
double	m11
double	m12
double	m20
double	m21
double	m22
end

@Override
protected Object newInstance (Object repr)
{
	return ((Matrix3d) repr).clone ();
}

@Override
protected Object cloneNonsharedObject (Object o, boolean deep)
{
	return ((Matrix3d) o).clone ();
}


static
{
	$TYPE.setSerializationMethod (ManageableType.LIST_SERIALIZATION);
}
