package de.grogra.ext.sunshine.objects;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;


public class SunshinePlane extends SunshineObject
{	
	@Override
	public int getID()
	{
		return PLANE_ID;
	}

	@Override
	public void setTransformMatrix(Matrix4d m)
	{
		transformMatrix = new Matrix4f();
		transformMatrix.set(m);
	}

}
