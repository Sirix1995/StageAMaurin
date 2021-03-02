package de.grogra.math;

import javax.vecmath.*;
import de.grogra.persistence.*;
begin
Matrix4f
SCOType
float	m00
float	m01
float	m02
float	m03
float	m10
float	m11
float	m12
float	m13
float	m20
float	m21
float	m22
float	m23
float	m30
float	m31
float	m32
float	m33
end

@Override
protected Object newInstance (Object repr)
{
	return ((Matrix4f) repr).clone ();
}

@Override
protected Object cloneNonsharedObject (Object o, boolean deep)
{
	return ((Matrix4f) o).clone ();
}


static
{
	$TYPE.setSerializationMethod (ManageableType.LIST_SERIALIZATION);
}
