package de.grogra.ext.sunshine.objects;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;


public class SunshineCone extends SunshineCFC
{
	private boolean isOpen;
	private float radius, length;
	
	
	public SunshineCone(boolean isOpen, float radius, float length)
	{
		this.radius = radius;
		this.length = length / radius;
		baseOpen = isOpen;
		topOpen = false;
		
		
		max = 0f;
		typ = 1;
	}
	

	@Override
	public void setTransformMatrix(Matrix4d m)
	{
		transformMatrix = new Matrix4f();
		//convert double matrix to float matrix
		transformMatrix.set(m);

		Matrix4f sm = new Matrix4f();
		
		
		//Skalierungsmatrix mit Radius in trafo integrieren 
		sm.set(radius);
		transformMatrix.mul(sm);
		sm.setIdentity();
		sm.m22 *= length;
		sm.m23 = length;
		transformMatrix.mul(sm);
	}
	
	
	public boolean isOpen()
	{
		return isOpen;
	}

}
