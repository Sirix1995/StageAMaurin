
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.imp3d.objects;

import java.util.Arrays;

import javax.vecmath.Vector3f;

import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.Renderable;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class SphereSegment extends MeshNode
	implements Pickable, Polygonizable, Renderable
{
	private static final long serialVersionUID = 686382261193185103L;

	protected float radius = 1f;
	//enh:field attr=Attributes.RADIUS getter setter
	protected float theta1 = 1;
	//enh:field attr=Attributes.THETA1 getter setter
	protected float theta2 = -1;
	//enh:field attr=Attributes.THETA2 getter setter
	protected float phi = (float) (Math.PI / 4f);
	//enh:field attr=Attributes.PHI getter setter

	private float[] data = null;

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, SphereSegment.$TYPE, new NType.Field[] {radius$FIELD, theta1$FIELD, theta2$FIELD, phi$FIELD});
		}

		public static void signature (@In @Out SphereSegment s, float r, float t1, float t2, float p)
		{
		}
	}


	public SphereSegment ()
	{
		this (1, 1, -1, (float) (Math.PI / 4f));
	}


	public SphereSegment (float radius)
	{
		this (radius, 1, -1, (float) (Math.PI / 4f));
	}
	
	public SphereSegment (float radius, float theta1, float theta2, float phi)
	{
		super ();
		setRadius(radius);
		setTheta1(theta1);
		setTheta2(theta2);
		setPhi(phi);
		calcPolygonMesh();
	}

	private Vector3f genVertexSphereSegment (float radius, float phi, float theta) {
		float x = (float)(radius*Math.cos (phi) * Math.cos (theta));
		float y = (float)(radius*Math.sin (phi) * Math.cos (theta));
		float z = (float)(radius*Math.sin (theta));
		return new Vector3f (x, y, z);
	}
	
	private void calcPolygonMesh() {
		// calculate segment count depending on level of detail
		final int uCount = 8 + (int) ((phi/Math.PI)*32);
		final int vCount = uCount;
		int k = 0;
		
		float delta = (float)Math.PI;
		if(theta1>0 & theta2>=0) {
			delta = theta1-theta2;
		}
		if(theta1>0 & theta2<0) {
			delta = theta1+Math.abs(theta2);
		}
		if(theta1<=0 & theta2<0) {
			delta = Math.abs(theta2)-Math.abs(theta1);
		}


		float topRadius = (float) Math.cos(theta1);
		float baseRadius = (float) Math.cos(theta2);
		float h1 = (float) Math.sin(theta1);
		float h2 = (float) Math.sin(theta2);
		float phiT1, phiT2;
		float theta1T, theta2T;
		
		float[] dataTop = new float[0];
		float[] dataBase = new float[0];
		float[] dataWall = new float[0];
		float[] dataMantel = new float[0];

		// draw top
		if(theta1<Math.PI/2d) {
			dataTop = new float[uCount * 9];
			k = 0;
			for (int u = 0; u < uCount; u++) {
				phiT1 = phi * 2 * u / uCount;
				phiT2 = phi * 2 * (u+1) / uCount;
				
				dataTop[k] = 0; dataTop[k+1] = 0; dataTop[k+2] = radius*h1;
				dataTop[k+3] = (float)(topRadius * radius * Math.cos (phiT1)); dataTop[k+4] = (float)(topRadius * radius * Math.sin (phiT1)); dataTop[k+5] = radius * h1;
				dataTop[k+6] = (float)(topRadius * radius * Math.cos (phiT2)); dataTop[k+7] = (float)(topRadius * radius * Math.sin (phiT2)); dataTop[k+8] = radius * h1;
				k+=9;
			}
		}
		// draw base
		if(theta2>-Math.PI/2d) {
			dataBase = new float[uCount * 9];
			k = 0;
			for (int u = 0; u < uCount; u++) {
				phiT1 = phi * 2 * u / uCount;
				phiT2 = phi * 2 * (u+1) / uCount;
				
				dataBase[k] = 0; dataBase[k+1] = 0; dataBase[k+2] = radius * h2;
				dataBase[k+3] = (float)(baseRadius * radius * Math.cos (phiT2)); dataBase[k+4] = (float)(baseRadius * radius * Math.sin (phiT2)); dataBase[k+5] = radius * h2;
				dataBase[k+6] = (float)(baseRadius * radius * Math.cos (phiT1)); dataBase[k+7] = (float)(baseRadius * radius * Math.sin (phiT1)); dataBase[k+8] = radius * h2;
				k+=9;
			}
		}
		
		
		// draw connection from top to bottom
		if(phi<Math.PI) {
			Vector3f vL1, vL2, vR1, vR2;
			dataWall = new float[4*(vCount * 9)];
			k=0;
			for (int v = 0; v < vCount; v++) {
				theta1T = theta2 + delta * v / vCount;
				theta2T = theta2 + delta * (v+1) / vCount;
				// calculate next vertex
				vL1 = genVertexSphereSegment (radius, 0, theta1T);
				vL2 = genVertexSphereSegment (radius, 0, theta2T);
				vR1 = genVertexSphereSegment (radius, 2*phi, theta1T);
				vR2 = genVertexSphereSegment (radius, 2*phi, theta2T);
	
				//L
				dataWall[k] = 0; dataWall[k+1] = 0; dataWall[k+2] = vL1.z;
				dataWall[k+3] = vL1.x; dataWall[k+4] = vL1.y; dataWall[k+5] = vL1.z;
				dataWall[k+6] = vL2.x; dataWall[k+7] = vL2.y; dataWall[k+8] = vL2.z;
				k+=9;
				dataWall[k] = 0; dataWall[k+1] = 0; dataWall[k+2] = vL2.z;
				dataWall[k+3] = 0; dataWall[k+4] = 0; dataWall[k+5] = vL1.z;
				dataWall[k+6] = vL2.x; dataWall[k+7] = vL2.y; dataWall[k+8] = vL2.z;
				k+=9;

				//R
				dataWall[k] = 0; dataWall[k+1] = 0; dataWall[k+2] = vR1.z;
				dataWall[k+3] = vR2.x; dataWall[k+4] = vR2.y; dataWall[k+5] = vR2.z;
				dataWall[k+6] = vR1.x; dataWall[k+7] = vR1.y; dataWall[k+8] = vR1.z;
				k+=9;
				dataWall[k] = 0; dataWall[k+1] = 0; dataWall[k+2] = vR1.z;
				dataWall[k+3] = 0; dataWall[k+4] = 0; dataWall[k+5] = vR2.z;
				dataWall[k+6] = vR2.x; dataWall[k+7] = vR2.y; dataWall[k+8] = vR2.z;
				k+=9;
			}
		}
		
		// for each strip
		dataMantel = new float[2*vCount*(2*uCount * 9)];
		Vector3f vL1, vL2, vR1, vR2;

		for (int v = 0; v < vCount; v++) {
			theta1T = theta2 + delta * v / vCount;
			theta2T = theta2 + delta * (v+1) / vCount;

			// start quad strip
			// for each vertical slice
			for (int u = 0; u < uCount; u++) {
				// calculate next vertex
				phiT1 = phi * 2 * u / uCount;
				phiT2 = phi * 2 * (u+1) / uCount;

				vL1 = genVertexSphereSegment (radius, phiT1, theta1T);
				vL2 = genVertexSphereSegment (radius, phiT1, theta2T);
				vR1 = genVertexSphereSegment (radius, phiT2, theta1T);
				vR2 = genVertexSphereSegment (radius, phiT2, theta2T);

				dataMantel[k] = vL2.x; dataMantel[k+1] = vL2.y; dataMantel[k+2] = vL2.z;
				dataMantel[k+3] = vL1.x; dataMantel[k+4] = vL1.y; dataMantel[k+5] = vL1.z;
				dataMantel[k+6] = vR2.x; dataMantel[k+7] = vR2.y; dataMantel[k+8] = vR2.z;
				k+=9;
				dataMantel[k] = vR1.x; dataMantel[k+1] = vR1.y; dataMantel[k+2] = vR1.z;
				dataMantel[k+3] = vR2.x; dataMantel[k+4] = vR2.y; dataMantel[k+5] = vR2.z;
				dataMantel[k+6] = vL1.x; dataMantel[k+7] = vL1.y; dataMantel[k+8] = vL1.z;
				k+=9;
			}
		}

		data = Arrays.copyOf(dataTop, dataTop.length + dataBase.length + dataWall.length +  dataMantel.length);
		System.arraycopy(dataBase, 0, data, dataTop.length, dataBase.length);
		System.arraycopy(dataWall, 0, data, dataTop.length + dataBase.length, dataWall.length);
		System.arraycopy(dataMantel, 0, data, dataTop.length + dataBase.length + dataWall.length, dataMantel.length);

		FloatList vertexDataLeaflet = new FloatList(data);
		int[] tmp = new int[vertexDataLeaflet.size()/3];
		for(int i = 0; i<tmp.length; i++) tmp[i]=i;
		// set a list of the indices of the used list of vertices
		// normally = {0,1,2,3,...,n}, where n is the number of used vertices minus one 
		PolygonMesh polygonMesh = new PolygonMesh();
		polygonMesh.setIndexData(new IntList(tmp));
		// set the list of vertices
		polygonMesh.setVertexData(vertexDataLeaflet);
		setPolygons (polygonMesh);
		setVisibleSides(Attributes.VISIBLE_SIDES_FRONT);
	}

//	enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field radius$FIELD;
	public static final NType.Field theta1$FIELD;
	public static final NType.Field theta2$FIELD;
	public static final NType.Field phi$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (SphereSegment.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((SphereSegment) o).setRadius (value);
					return;
				case 1:
					((SphereSegment) o).setTheta1 (value);
					return;
				case 2:
					((SphereSegment) o).setTheta2 (value);
					return;
				case 3:
					((SphereSegment) o).setPhi (value);
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 0:
					return ((SphereSegment) o).getRadius ();
				case 1:
					return ((SphereSegment) o).getTheta1 ();
				case 2:
					return ((SphereSegment) o).getTheta2 ();
				case 3:
					return ((SphereSegment) o).getPhi ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new SphereSegment ());
		$TYPE.addManagedField (radius$FIELD = new _Field ("radius", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (theta1$FIELD = new _Field ("theta1", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (theta2$FIELD = new _Field ("theta2", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (phi$FIELD = new _Field ("phi", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 3));
		$TYPE.declareFieldAttribute (radius$FIELD, Attributes.RADIUS);
		$TYPE.declareFieldAttribute (theta1$FIELD, Attributes.THETA1);
		$TYPE.declareFieldAttribute (theta2$FIELD, Attributes.THETA2);
		$TYPE.declareFieldAttribute (phi$FIELD, Attributes.PHI);
		initType ();
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new SphereSegment ();
	}

	public float getRadius ()
	{
		return radius;
	}

	public void setRadius (float value)
	{
		if(value<0) value=0;
		radius = value;
		calcPolygonMesh();
	}
	
	public float getTheta1 ()
	{
		return theta1;
	}

	public void setTheta1 (float value)
	{
		if(value<theta2) {
			value = theta2+0.01f;
		}
		if(value>Math.PI/2f) {
			value = (float)Math.PI/2f;
		}
		if(value<-Math.PI/2f) {
			value = (float)-Math.PI/2f;
		}
		theta1 = value;
		if(theta1==theta2 && theta1<Math.PI/2f) {
			theta1 = theta1+0.01f;
		}
		if(theta1==theta2 && theta1==Math.PI/2f) {
			theta2 = theta2-0.01f;
		}	
		if(theta1==theta2 && theta1==-Math.PI/2f) {
			theta1 = theta1+0.01f;
		}

		calcPolygonMesh();
	}
	
	public float getTheta2 ()
	{
		return theta2;
	}

	public void setTheta2 (float value)
	{
		if(value>theta1) {
			value = theta1-0.01f;
		}
		if(value>Math.PI/2f) {
			value = (float)Math.PI/2f;
		}
		if(value<-Math.PI/2f) {
			value = (float)-Math.PI/2f;
		}
		theta2 = value;
		if(theta1==theta2 && theta1<Math.PI/2f) {
			theta1 = theta1+0.01f;
		}
		if(theta1==theta2 && theta1==Math.PI/2f) {
			theta2 = theta2-0.01f;
		}	
		if(theta1==theta2 && theta1==-Math.PI/2f) {
			theta1 = theta1+0.01f;
		}

		calcPolygonMesh();
	}
	
	public float getPhi ()
	{
		return phi;
	}

	public void setPhi (float value)
	{
		if(value>Math.PI) {
			value = (float)Math.PI;
		}
		if(value<0) {
			value = 0;
		}
		phi = value;

		calcPolygonMesh();
	}

//enh:end


	/**
	 * Calculates the area of an object.
	 * Intersection with other object are not considered.The total area will be calculated.
	 * 
	 * @return area
	 */
	@Override
	public double getSurfaceArea() {
		if(data.length<9) return -1;
		return getAreaOfTriangulation(data);
	}

	/**
	 * Calculates the volume.
	 * Intersection with other object are not considered.The total volume will be calculated.
	 * V=tbd
	 * 
	 * @return volume
	 */
	@Override
	public double getVolume() {
		return -1;
	}
	
}
