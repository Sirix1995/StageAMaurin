
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

import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.Renderable;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class GPP extends MeshNode
	implements Pickable, Polygonizable, Renderable
{
	private static final long serialVersionUID = 8631919191885103L;

	protected int N = 5;
	//enh:field attr=Attributes.N_POLYGONS getter setter
	protected float radius_top = 0.5f;
	//enh:field attr=Attributes.RADIUS_TOP getter setter
	protected float radius_base = 1f;
	//enh:field attr=Attributes.RADIUS_BASE getter setter
	protected float height = 1f;
	//enh:field attr=Attributes.HEIGHT getter setter
	protected float phi = 0;
	//enh:field attr=Attributes.PHI getter setter
	protected float xShift = 0;
	//enh:field attr=Attributes.X_SHIFT getter setter
	protected float yShift = 0;
	//enh:field attr=Attributes.Y_SHIFT getter setter

	private float[] data = null;

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, GPP.$TYPE, new NType.Field[] {N$FIELD, radius_top$FIELD, radius_base$FIELD, height$FIELD, phi$FIELD, xShift$FIELD, yShift$FIELD});
		}

		public static void signature (@In @Out GPP s, int n, float r1, float r2, float h, float p, float xs, float ys)
		{
		}
	}



	public GPP ()
	{
		this (5, 0.5f, 1, 1, 0, 0, 0);
	}


	public GPP (int n)
	{
		this (n, 0.5f, 1, 1, 0, 0, 0);
	}
	
	public GPP (int n, float radius_top, float radius_base, float height, float phi, float xShift, float yShift)
	{
		super ();
		setN(n);
		setRadius_top(radius_top);
		setRadius_base(radius_base);
		setHeight(height);
		setPhi(phi);
		setXShift(xShift);
		setYShift(yShift);
		calcPolygonMesh();
	}

	private void calcPolygonMesh() {
		int k = 0;
		float phiT1, phiT2;
		float phiB1, phiB2;
		
		float[] dataTop = new float[0];
		float[] dataBase = new float[0];
		float[] dataWall = new float[0];

		// draw top
		if(radius_top>0) {
			dataTop = new float[N*9];
			k = 0;
			for (int u = 0; u < N; u++) {
				phiT1 = (float)(phi + Math.PI * 2 * u / N);
				phiT2 = (float)(phi + Math.PI * 2 * (u+1) / N);
				
				dataTop[k] = xShift; dataTop[k+1] = yShift; dataTop[k+2] = height;
				dataTop[k+3] = (float)(xShift + radius_top * Math.cos (phiT1)); dataTop[k+4] = (float)(yShift + radius_top * Math.sin (phiT1)); dataTop[k+5] = height;
				dataTop[k+6] = (float)(xShift + radius_top * Math.cos (phiT2)); dataTop[k+7] = (float)(yShift + radius_top * Math.sin (phiT2)); dataTop[k+8] = height;
				k+=9;
			}
		}
		// draw base
		if(radius_base>0) {
			dataBase = new float[N * 9];
			k = 0;
			for (int u = 0; u < N; u++) {
				phiT1 = (float)(Math.PI * 2 * u / N);
				phiT2 = (float)(Math.PI * 2 * (u+1) / N);
				
				dataBase[k] = 0; dataBase[k+1] = 0; dataBase[k+2] = 0;
				dataBase[k+3] = (float)(radius_base * Math.cos (phiT2)); dataBase[k+4] = (float)(radius_base * Math.sin (phiT2)); dataBase[k+5] = 0;
				dataBase[k+6] = (float)(radius_base * Math.cos (phiT1)); dataBase[k+7] = (float)(radius_base * Math.sin (phiT1)); dataBase[k+8] = 0;
				k+=9;
			}
		}
		
		
		// draw connection from top to bottom
		if(radius_top>0 && radius_base>0) {
			int vCount = 1;
			double hi = height, phii = phi;
			double r1, r2, delta_r = radius_top;
			if(radius_base<radius_top) {
				delta_r = (radius_top - radius_base);
			} else {
				delta_r = -(radius_base - radius_top);
			}
			
			double xShiftii = xShift;
			double yShiftii = yShift;
			if(phi!=0) {
				double p = Math.abs(phi);
				vCount = (int)Math.round(p /0.15);
				hi = height / (double)vCount;
				phii = phi / (double)vCount;
				xShiftii = xShift / (double)vCount;
				yShiftii = yShift / (double)vCount;
				delta_r = delta_r / vCount;
			}
			
			dataWall = new float[vCount*(2*N * 9)];
			k = 0;
			for (int v = 0; v < vCount; v++) {
				for (int u = 0; u < N; u++) {
					phiB1 = (float)(phii*v + Math.PI * 2 * u / N);
					phiB2 = (float)(phii*v + Math.PI * 2 * (u+1) / N);
					phiT1 = (float)(phii*(v+1) + Math.PI * 2 * u / N);
					phiT2 = (float)(phii*(v+1) + Math.PI * 2 * (u+1) / N);
					
					r1 = radius_base + v * delta_r;
					r2 = radius_base + (v+1) * delta_r;
					
					dataWall[k] = (float)(xShiftii*(v+1) + r2 * Math.cos (phiT2)); dataWall[k+1] = (float)(yShiftii*(v+1) + r2 * Math.sin (phiT2)); dataWall[k+2] = (float)(hi*(v+1));
					dataWall[k+3] = (float)(xShiftii*(v+1) + r2 * Math.cos (phiT1)); dataWall[k+4] = (float)(yShiftii*(v+1) + r2 * Math.sin (phiT1)); dataWall[k+5] = (float)(hi*(v+1));
					dataWall[k+6] = (float)(xShiftii*v + r1 * Math.cos (phiB1)); dataWall[k+7] = (float)(yShiftii*v + r1 * Math.sin (phiB1)); dataWall[k+8] = (float)(hi*v);
					k+=9;

					dataWall[k] = (float)(xShiftii*v + r1 * Math.cos (phiB1)); dataWall[k+1] = (float)(yShiftii*v + r1 * Math.sin (phiB1)); dataWall[k+2] = (float)(hi*v);
					dataWall[k+3] = (float)(xShiftii*v + r1 * Math.cos (phiB2)); dataWall[k+4] = (float)(yShiftii*v + r1 * Math.sin (phiB2)); dataWall[k+5] = (float)(hi*v);
					dataWall[k+6] = (float)(xShiftii*(v+1) + r2 * Math.cos (phiT2)); dataWall[k+7] = (float)(yShiftii*(v+1) + r2 * Math.sin (phiT2)); dataWall[k+8] = (float)(hi*(v+1));
					k+=9;
				}
			}
		}
		

		data = Arrays.copyOf(dataTop, dataTop.length + dataBase.length + dataWall.length);
		System.arraycopy(dataBase, 0, data, dataTop.length, dataBase.length);
		System.arraycopy(dataWall, 0, data, dataTop.length + dataBase.length, dataWall.length);
		
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

	public static final NType.Field N$FIELD;
	public static final NType.Field radius_top$FIELD;
	public static final NType.Field radius_base$FIELD;
	public static final NType.Field height$FIELD;
	public static final NType.Field phi$FIELD;
	public static final NType.Field xShift$FIELD;
	public static final NType.Field yShift$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (GPP.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 0:
					((GPP) o).setN (value);
					return;
			}
			super.setFloat (o, value);
		}
		
		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 0:
					return ((GPP) o).getN ();
			}
			return super.getInt (o);
		}
					
					
		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 1:
					((GPP) o).setRadius_top (value);
					return;
				case 2:
					((GPP) o).setRadius_base (value);
					return;
				case 3:
					((GPP) o).setHeight (value);
					return;
				case 4:
					((GPP) o).setPhi (value);
					return;
				case 5:
					((GPP) o).setXShift (value);
					return;
				case 6:
					((GPP) o).setYShift (value);
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 1:
					return ((GPP) o).getRadius_top ();
				case 2:
					return ((GPP) o).getRadius_base ();
				case 3:
					return ((GPP) o).getHeight ();
				case 4:
					return ((GPP) o).getPhi ();
				case 5:
					return ((GPP) o).getXShift ();
				case 6:
					return ((GPP) o).getYShift ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new GPP ());
		$TYPE.addManagedField (N$FIELD = new _Field ("n_polygons", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.INT, null, 0));
		$TYPE.addManagedField (radius_top$FIELD = new _Field ("radius_top", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (radius_base$FIELD = new _Field ("radius_base", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (height$FIELD = new _Field ("height", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 3));
		$TYPE.addManagedField (phi$FIELD = new _Field ("phi", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 4));
		$TYPE.addManagedField (xShift$FIELD = new _Field ("x_shift", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 5));
		$TYPE.addManagedField (yShift$FIELD = new _Field ("y_shift", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 6));

		$TYPE.declareFieldAttribute (N$FIELD, de.grogra.imp3d.objects.Attributes.N_POLYGONS);
		$TYPE.declareFieldAttribute (radius_top$FIELD, de.grogra.imp3d.objects.Attributes.RADIUS_TOP);
		$TYPE.declareFieldAttribute (radius_base$FIELD, de.grogra.imp3d.objects.Attributes.RADIUS_BASE);
		$TYPE.declareFieldAttribute (height$FIELD, de.grogra.imp3d.objects.Attributes.HEIGHT);
		$TYPE.declareFieldAttribute (phi$FIELD, de.grogra.imp3d.objects.Attributes.PHI);
		$TYPE.declareFieldAttribute (xShift$FIELD, de.grogra.imp3d.objects.Attributes.X_SHIFT);
		$TYPE.declareFieldAttribute (yShift$FIELD, de.grogra.imp3d.objects.Attributes.Y_SHIFT);
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
		return new GPP ();
	}

	public int getN ()
	{
		return N;
	}

	public void setN (int value)
	{
		if(value<3) value=3;
		N = value;
		calcPolygonMesh();
	}
	
	public float getRadius_top ()
	{
		return radius_top;
	}

	public void setRadius_top (float value)
	{
		if(value<0) value=0;
		radius_top = value;
		calcPolygonMesh();
	}
	
	public float getRadius_base ()
	{
		return radius_base;
	}

	public void setRadius_base (float value)
	{
		if(value<0) value=0;
		radius_base = value;
		calcPolygonMesh();
	}
	
	public float getHeight ()
	{
		return height;
	}

	public void setHeight (float value)
	{
		if(value<0) value=0;
		height = value;
		calcPolygonMesh();
	}
	
	public float getXShift ()
	{
		return xShift;
	}

	public void setXShift (float value)
	{
		xShift = value;
		calcPolygonMesh();
	}
	
	public float getYShift ()
	{
		return yShift;
	}

	public void setYShift (float value)
	{
		yShift = value;
		calcPolygonMesh();
	}
	
	public float getPhi ()
	{
		return phi;
	}

	public void setPhi (float value)
	{
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
