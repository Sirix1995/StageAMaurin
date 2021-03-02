package de.grogra.ext.sunshine.objects;

import javax.vecmath.*;


import java.awt.Color;

public class SunshineLight extends SunshineObject
{
	protected Vector3f origin = new Vector3f();
	private float power;
	private float att;
	private float exp;
	private boolean shadowless;
	private float innerAngle = 0;;
	private float outerAngle = 0;;
	protected int typ = 0;
	protected int count;
	
	
	
	public SunshineLight(Point3f origin)
	{
		this.origin.add(origin);
		color = new Color4f(Color.WHITE);
		
		power = 100;
		att = 1;
		exp = 0;
		shadowless = false;
	} //constructor;
	
	
	public SunshineLight(float power, float dist, float ex, boolean shadowless)
	{
		color = new Color4f(Color.WHITE);
		this.power = power;
		att = dist;
		exp = ex;
		this.shadowless = shadowless;
		count = -1;
	}
	

	public int getID()
	{
		return LIGHT_ID;
	}
	
	public void setTransformMatrix(Matrix4d m)
	{
		transformMatrix = new Matrix4f();
		transformMatrix.set(m);
	}
	
	
	public void setPower(float p)
	{
		power = p;
	}

	
	public float getPower()
	{
		return power;
	}


	public float getAtt()
	{
		return att;
	}


	public float getExp()
	{
		return exp;
	}


	public boolean isShadowless()
	{
		return shadowless;
	}


	public int getTyp()
	{
		return typ;
	}


	public float getInnerAngle()
	{
		return innerAngle;
	}


	public void setInnerAngle(float angle)
	{
		typ = 1;
//		innerAngle = 180f/(float)Math.PI * angle;
		innerAngle = angle;
	}


	public float getOuterAngle()
	{
		return outerAngle;
	}


	public void setOuterAngle(float angle)
	{
		typ = 1; 
//		outerAngle = 180f/(float)Math.PI * angle;
		outerAngle = angle;
	}
	
	public int getCount()
	{
		return count;
	}
	
	
	public void setIOR(float ior)
	{
		
	}

	
} //class