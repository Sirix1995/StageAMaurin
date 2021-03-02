package de.grogra.ext.sunshine.objects;

import java.awt.Color;
import javax.vecmath.*;

/**
 * @author Thomas
 *
 */
public abstract class SunshineObject {
	protected final int SPHERE_ID 	= 0;
	protected final int BOX_ID 		= 1;
	protected final int CFC_ID	 	= 2;
	protected final int PLANE_ID	= 3;
	protected final int PARA_ID		= 4;
	protected final int SPOT_ID 	= 5;
	protected final int LIGHT_ID 	= 10;
	
	protected Color4f color = new Color4f( Color.gray );
	protected Matrix4f transformMatrix;
	

	public abstract int getID();
	
	public abstract void setTransformMatrix(Matrix4d m);
	
	public Matrix4f getTransformMatrix()
	{
		return transformMatrix;
	}
	
	
	public void setShader(float r, float g, float b, float a)
	{
		color.set(r, g, b, a);		
	} //setShader
	
	
	public void setShader(Color4f color)
	{
		this.color = color;
	} //setShader
	
	public Color4f getColor()
	{
		return color;
	} //getColor
} //class
