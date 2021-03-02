package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;
begin
Matrix4d
SCOType
double	m00
double	m01
double	m02
double	m03
double	m10
double	m11
double	m12
double	m13
double	m20
double	m21
double	m22
double	m23
double	m30
double	m31
double	m32
double	m33
end

@Override
protected Object newInstance (Object repr)
{
	return ((Matrix4d) repr).clone ();
}

@Override
protected Object cloneNonsharedObject (Object o, boolean deep)
{
	return ((Matrix4d) o).clone ();
}


static
{
	$TYPE.setSerializationMethod (ManageableType.LIST_SERIALIZATION);
}
