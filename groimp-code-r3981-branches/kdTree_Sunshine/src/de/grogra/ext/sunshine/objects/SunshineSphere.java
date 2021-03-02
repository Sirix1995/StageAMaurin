package de.grogra.ext.sunshine.objects;

import javax.vecmath.*;


import java.awt.Color;
/**
 * @author Thomas Huwe
 *
 */
public class SunshineSphere extends SunshineObject {
	private Vector3f origin = new Vector3f();
	private float radius;
	
	
	/**
	 * creates a sphere at <0, 0, 0>
	 * with the given radius
	 */
	public SunshineSphere(float radius)
	{
		origin.set(0, 0, 0);		
		this.radius = radius;
	} //Constructor
	
	
	public int getID()
	{
		return SPHERE_ID;
	} //getID
	
	
	public void set(float x, float y, float z, float r)
	{
		origin.set(x,y,z);
				
		radius = r;
	}


	public void setTransformMatrix(Matrix4d m) 
	{
		this.transformMatrix = new Matrix4f();
		//convert double matrix to float matrix
		this.transformMatrix.set(m);
		
		Matrix4f sm = new Matrix4f();
		
		//Skalierungsmatrix mit Radius in trafo integrieren 
		sm.set(radius);
		transformMatrix.mul(sm);
		
	}
}
