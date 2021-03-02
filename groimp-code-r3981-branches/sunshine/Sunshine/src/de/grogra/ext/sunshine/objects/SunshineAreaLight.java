package de.grogra.ext.sunshine.objects;

import java.awt.Color;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

/**
 * @author Thomas
 * 
 */
public class SunshineAreaLight extends SunshineLight
{
	float				length;
	private Vector3f	axis;


	public SunshineAreaLight(float power, float exp, boolean shadowless,
			float length, Vector3f axis, int count)
	{
		super(power, 1f, exp, shadowless);
		this.length = length;
		this.axis = axis;
		typ = 2;
		this.count = count;
		color = new Color4f(Color.WHITE);
	}


	@Override
	public void setTransformMatrix(Matrix4d m)
	{
		transformMatrix = new Matrix4f();
		transformMatrix.set(m);

		Matrix4f sm = new Matrix4f();
		sm.setIdentity();
		sm.m00 *= axis.x;
		sm.m22 *= length;

		// integrate the scale matrix in trafo
		transformMatrix.mul(sm);
	}

}
