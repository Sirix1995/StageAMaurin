package de.grogra.ext.sunshine.objects;

import java.awt.Color;

import javax.vecmath.Color4f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;


public class SunshineCFC extends SunshineObject
{
	protected float length;
	protected float radius;
	protected boolean topOpen;
	protected boolean baseOpen = false;
	protected float max;
	protected int typ;
	
	
	public SunshineCFC()
	{
		
	}

	/**
	 * @param radius
	 * @param length
	 * @param base: set true for an open bottom
	 * @param top : set true for an open upside 
	 */
	public SunshineCFC(float radius, float length, boolean top, boolean base)
	{
		this.radius = radius;
		this.length = length / radius;
		topOpen 	= top;
		baseOpen 	= base;
		color 		= new Color4f( Color.gray );
		
		max = 1f;
		typ = 0;
	} //Constructor


	@Override
	public int getID()
	{
		return CFC_ID;
	}

	@Override
	public Matrix4f getTransformMatrix()
	{
		return transformMatrix;
	}

	@Override
	public void setShader(float r, float g, float b, float a)
	{
		color.set(r, g, b, a);
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
		transformMatrix.mul(sm);
	}


	public boolean isTopOpen()
	{
		return topOpen;
	}


	public boolean isBaseOpen()
	{
		return baseOpen;
	}
	
	
	public float getMax()
	{
		return max;
	}
	
	public int getType()
	{
		return typ;
	}

} //class
