package de.grogra.ext.sunshine.objects;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;

public class SunshineTriangles extends SunshineObject
{
	private boolean	twoSide;
	private float[]	vertexList;
	private float[]	normalList;
	private float[]	textureCoorList;
	private int[]	indexList;

	private float	ior	= 1f;


	public SunshineTriangles(boolean twoSide, float[] vertexList,
			int[] indexList, float[] normalList, float[] textureCoorList)
	{
		this.twoSide = twoSide;
		this.vertexList = vertexList;
		this.indexList = indexList;
		this.normalList = normalList;
		this.textureCoorList = textureCoorList;
	}


	@Override
	public void setTransformMatrix(Matrix4d m)
	{
		transformMatrix = new Matrix4f();
		transformMatrix.set(m);
	}


	public float[] getVertexList()
	{
		return vertexList;
	}


	public int[] getIndexList()
	{
		return indexList;
	}


	public float[] getNormalList()
	{
		return normalList;
	}


	public float[] getTextureCoorList()
	{
		return textureCoorList;
	}


	public boolean hasNormals()
	{
		return normalList != null;
	}


	@Override
	public int getID()
	{
		return TRI_ID;
	}


	public void setIOR(float ior)
	{
		this.ior = ior;
	}

}
