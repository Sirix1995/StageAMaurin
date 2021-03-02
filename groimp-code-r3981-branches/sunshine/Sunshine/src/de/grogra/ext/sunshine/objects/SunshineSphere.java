package de.grogra.ext.sunshine.objects;

import javax.vecmath.*;

/**
 * @author Thomas Huwe
 * 
 */
public class SunshineSphere extends SunshineObject
{
	private Vector3f	origin	= new Vector3f();
	private float		radius;
	private float		ior = 1f;


	/**
	 * creates a sphere at <0, 0, 0> with the given radius
	 */
	public SunshineSphere(float radius)
	{
		origin.set(0, 0, 0);
		this.radius = radius;
	} // Constructor


	public int getID()
	{
		return SPHERE_ID;
	} // getID


	public void set(float x, float y, float z, float r)
	{
		origin.set(x, y, z);

		radius = r;
	}


	public void setTransformMatrix(Matrix4d m)
	{
		this.transformMatrix = new Matrix4f();
		// convert double matrix to float matrix
		this.transformMatrix.set(m);

		Matrix4f sm = new Matrix4f();

		// Skalierungsmatrix mit Radius in trafo integrieren
		sm.set(radius);
		transformMatrix.mul(sm);
	}


	public void setIOR(float ior)
	{		
		this.ior = ior;
	}
	
	
	public void setShader(Color4f color)
	{
		Color4f c = new Color4f(color);
		float alpha = color.w;
		c.w = 10f*ior + (alpha/10f);		

		super.setShader(c);
	}
}
