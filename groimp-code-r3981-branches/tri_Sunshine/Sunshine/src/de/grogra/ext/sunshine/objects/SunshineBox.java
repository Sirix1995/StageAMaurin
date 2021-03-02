package de.grogra.ext.sunshine.objects;

import javax.vecmath.*;



public class SunshineBox extends SunshineObject 
{
	private float width;
	private float height;
	private float length;
	
	
	/**
	 * creates a box length 1 around <0,0,0>
	 */
	public SunshineBox(float width, float height, float length)
	{	
		this.width 	= width;
		this.height = height;
		this.length = length;
	}
	
	
	public int getID()
	{
		return BOX_ID;
	}
	
	
	public void setTransformMatrix(Matrix4d m)
	{	
		transformMatrix = new Matrix4f(); 
		transformMatrix.set(m);
		
		Matrix4f sm = new Matrix4f();
		sm.setIdentity();
		sm.m00 *= width;
		sm.m11 *= height;
		sm.m22 *= length;
		
		//Skalierungsmatrix in trafo integrieren 
		transformMatrix.mul(sm);
	} //setTrafo
	
	
	public Vector3f getOrigin()
	{
		return new Vector3f(0,0,0);
	}
	
	
} //class