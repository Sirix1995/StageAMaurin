package de.grogra.ext.sunshine.objects;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;

public class SunshinePlane extends SunshineObject
{
	private float	ior	= 1f;


	public int getID()
	{
		return PLANE_ID;
	}


	public void setTransformMatrix(Matrix4d m)
	{
		transformMatrix = new Matrix4f();
		transformMatrix.set(m);
	}


	public void setIOR(float ior)
	{
		this.ior = ior;
	}

}
