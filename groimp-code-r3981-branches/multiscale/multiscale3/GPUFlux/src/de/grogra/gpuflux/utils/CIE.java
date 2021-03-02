package de.grogra.gpuflux.utils;

import javax.vecmath.Point3f;

import org.sunflow.image.XYZColor;

public class CIE {

	public static void XYZtoRGB( Point3f out, XYZColor XYZ)
	{
		out.x =   2.5623f  * XYZ.getX() + (-1.1661f) * XYZ.getY() + (-0.3962f) * XYZ.getZ();
		out.y = (-1.0215f) * XYZ.getX() +   1.9778f  * XYZ.getY() +   0.0437f  * XYZ.getZ();
		out.z =   0.0752f  * XYZ.getX() + (-0.2562f) * XYZ.getY() +   1.1810f  * XYZ.getZ();
	}

}
