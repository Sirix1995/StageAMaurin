package de.grogra.math;

import java.awt.*;
import javax.vecmath.*;
import de.grogra.persistence.*;
begin
Tuple3f
SCOType
float	x
float	y
float	z
end

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
