package de.grogra.ext.sunshine.objects;

import javax.vecmath.*;




public class SunshineFrustum extends SunshineCFC
{
	private float topRadius;
	
	/**
	 * 
	 * @param radius
	 * @param topRadius
	 * @param length
	 * @param top
	 * @param base
	 */
	public SunshineFrustum(float radius, float topRadius, float length, boolean top, boolean base)
	{
		super(radius, length + (topRadius/(radius-topRadius))*length, top, base);
		
		this.topRadius = topRadius;
		if(radius < topRadius) this.length = -this.length;
		
		
		max = radius > topRadius ? -topRadius/radius : topRadius/radius;
//		max = radius > topRadius ? -0.5f : 0.5f;
		typ = 2;

	} //Constructor
	
			
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
		sm.m22 = length;
		sm.m23 = radius > topRadius ? length : -length;
		transformMatrix.mul(sm);
		
	}
}
