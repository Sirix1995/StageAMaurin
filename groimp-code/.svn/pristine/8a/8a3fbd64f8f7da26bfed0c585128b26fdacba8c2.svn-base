
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

import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.Renderable;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class GWedge extends MeshNode
	implements Pickable, Polygonizable, Renderable
{
	private static final long serialVersionUID = 68631919191885103L;


	protected float length = 1f;
	//enh:field attr=Attributes.LENGTH getter setter
	protected float width = 0.5f;
	//enh:field attr=Attributes.WIDTH getter setter
	protected float height = 1f;
	//enh:field attr=Attributes.HEIGHT getter setter
	protected float x1 = 0;
	//enh:field attr=Attributes.X1 getter setter
	protected float x2 = 0;
	//enh:field attr=Attributes.X2 getter setter
	protected float y1 = 0;
	//enh:field attr=Attributes.Y1 getter setter
	protected float y2 = 0.5f;
	//enh:field attr=Attributes.Y2 getter setter

	private float[] data = null;

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, GWedge.$TYPE, new NType.Field[] {length$FIELD, width$FIELD, height$FIELD, x1$FIELD, x2$FIELD, y1$FIELD, y2$FIELD});
		}

		public static void signature (@In @Out GWedge s, float l, float w, float h, float x1, float x2, float y1, float y2)
		{
		}
	}


	public GWedge ()
	{
		this (1, 0.5f, 1, 0, 0, 0, 0.5f);
	}
	
	public GWedge (float length, float width, float height, float x1, float x2, float y1, float y2)
	{
		super ();
		setLength(length);
		setWidth(width);
		setHeight(height);

		if((length/2f +x1)<(-length/2f+x2)) {
			x1 = -length+x2;
		}
		if((width/2f +y1)<(-width/2f +y2)) {
			y1 = -width+y2;
		}

		setX1(x1);
		setX2(x2);
		setY1(y1);
		setY2(y2);
		calcPolygonMesh();
	}

	private void calcPolygonMesh() {
		int k = 0;
		data = new float[12*9];

		final float x11 = -length/2f;
		final float y11 = -width/2f;
		final float x21 =  length/2f;
		final float y21 = -width/2f;
		final float x31 =  length/2f;
		final float y31 =  width/2f;
		final float x41 = -length/2f;
		final float y41 =  width/2f;
		final float x12 = -length/2f +x2;
		final float y12 = -width/2f  +y2;
		final float x22 =  length/2f +x1;
		final float y22 = -width/2f  +y2;
		final float x32 =  length/2f +x1;
		final float y32 =  width/2f  +y1;
		final float x42 = -length/2f +x2;
		final float y42 =  width/2f  +y1;
		
		// draw base
		data[k]   = x21; data[k+1] = y21; data[k+2] = 0;
		data[k+3] = x11; data[k+4] = y11; data[k+5] = 0;
		data[k+6] = x31; data[k+7] = y31; data[k+8] = 0;
		k+=9;
		data[k]   = x31; data[k+1] = y31; data[k+2] = 0;
		data[k+3] = x11; data[k+4] = y11; data[k+5] = 0;
		data[k+6] = x41; data[k+7] = y41; data[k+8] = 0;
		k+=9;

		// draw top
		data[k]   = x12; data[k+1] = y12; data[k+2] = height;
		data[k+3] = x22; data[k+4] = y22; data[k+5] = height;
		data[k+6] = x32; data[k+7] = y32; data[k+8] = height;
		k+=9;
		data[k]   = x32; data[k+1] = y32; data[k+2] = height;
		data[k+3] = x42; data[k+4] = y42; data[k+5] = height;
		data[k+6] = x12; data[k+7] = y12; data[k+8] = height;
		k+=9;

		//front/back
		data[k]   = x11; data[k+1] = y11; data[k+2] = 0;
		data[k+3] = x21; data[k+4] = y21; data[k+5] = 0;
		data[k+6] = x22; data[k+7] = y22; data[k+8] = height;
		k+=9;
		data[k]   = x22; data[k+1] = y22; data[k+2] = height;
		data[k+3] = x12; data[k+4] = y12; data[k+5] = height;
		data[k+6] = x11; data[k+7] = y11; data[k+8] = 0;
		k+=9;
		data[k]   = x31; data[k+1] = y31; data[k+2] = 0;
		data[k+3] = x41; data[k+4] = y41; data[k+5] = 0;
		data[k+6] = x42; data[k+7] = y42; data[k+8] = height;
		k+=9;
		data[k]   = x42; data[k+1] = y42; data[k+2] = height;
		data[k+3] = x32; data[k+4] = y32; data[k+5] = height;
		data[k+6] = x31; data[k+7] = y31; data[k+8] = 0;
		k+=9;
		
		//left/right
		data[k]   = x41; data[k+1] = y41; data[k+2] = 0;
		data[k+3] = x11; data[k+4] = y11; data[k+5] = 0;
		data[k+6] = x42; data[k+7] = y42; data[k+8] = height;
		k+=9;
		data[k]   = x12; data[k+1] = y12; data[k+2] = height;
		data[k+3] = x42; data[k+4] = y42; data[k+5] = height;
		data[k+6] = x11; data[k+7] = y11; data[k+8] = 0;
		k+=9;
		data[k]   = x21; data[k+1] = y21; data[k+2] = 0;
		data[k+3] = x31; data[k+4] = y31; data[k+5] = 0;
		data[k+6] = x32; data[k+7] = y32; data[k+8] = height;
		k+=9;
		data[k]   = x32; data[k+1] = y32; data[k+2] = height;
		data[k+3] = x22; data[k+4] = y22; data[k+5] = height;
		data[k+6] = x21; data[k+7] = y21; data[k+8] = 0;
		k+=9;
		
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

	public static final NType.Field length$FIELD;
	public static final NType.Field width$FIELD;
	public static final NType.Field height$FIELD;
	public static final NType.Field x1$FIELD;
	public static final NType.Field x2$FIELD;
	public static final NType.Field y1$FIELD;
	public static final NType.Field y2$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (GWedge.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((GWedge) o).setLength (value);
					return;
				case 1:
					((GWedge) o).setWidth (value);
					return;
				case 2:
					((GWedge) o).setHeight (value);
					return;
				case 3:
					((GWedge) o).setX1 (value);
					return;
				case 4:
					((GWedge) o).setX2 (value);
					return;
				case 5:
					((GWedge) o).setY1 (value);
					return;
				case 6:
					((GWedge) o).setY2 (value);
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
					return ((GWedge) o).getLength ();
				case 1:
					return ((GWedge) o).getWidth ();
				case 2:
					return ((GWedge) o).getHeight ();
				case 3:
					return ((GWedge) o).getX1 ();
				case 4:
					return ((GWedge) o).getX2 ();
				case 5:
					return ((GWedge) o).getY1 ();
				case 6:
					return ((GWedge) o).getY2 ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new GWedge ());
		$TYPE.addManagedField (length$FIELD = new _Field ("length", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (width$FIELD = new _Field ("width", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (height$FIELD = new _Field ("height", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (x1$FIELD = new _Field ("x1", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 3));
		$TYPE.addManagedField (x2$FIELD = new _Field ("x2", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 4));
		$TYPE.addManagedField (y1$FIELD = new _Field ("y1", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 5));
		$TYPE.addManagedField (y2$FIELD = new _Field ("y2", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 6));

		$TYPE.declareFieldAttribute (length$FIELD, de.grogra.imp3d.objects.Attributes.LENGTH);
		$TYPE.declareFieldAttribute (width$FIELD, de.grogra.imp3d.objects.Attributes.WIDTH);
		$TYPE.declareFieldAttribute (height$FIELD, de.grogra.imp3d.objects.Attributes.HEIGHT);
		$TYPE.declareFieldAttribute (x1$FIELD, de.grogra.imp3d.objects.Attributes.X1);
		$TYPE.declareFieldAttribute (x2$FIELD, de.grogra.imp3d.objects.Attributes.X2);
		$TYPE.declareFieldAttribute (y1$FIELD, de.grogra.imp3d.objects.Attributes.Y1);
		$TYPE.declareFieldAttribute (y2$FIELD, de.grogra.imp3d.objects.Attributes.Y2);
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
		return new GWedge ();
	}

	
	public float getLength ()
	{
		return length;
	}

	public void setLength (float value)
	{
		if(value<0) value=0;
		length = value;
		calcPolygonMesh();
	}
	
	public float getWidth ()
	{
		return width;
	}

	public void setWidth (float value)
	{
		if(value<0) value=0;
		width = value;
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
	
	public float getX1 ()
	{
		return x1;
	}

	public void setX1 (float value)
	{
		if((length/2f +value)<(-length/2f+x2)) {
			value = -length+x2;
		}
		x1 = value;
		calcPolygonMesh();
	}
	
	public float getX2 ()
	{
		return x2;
	}

	public void setX2 (float value)
	{
		if((length/2f +x1)<(-length/2f+value)) {
			x1 = -length+value;
		}
		x2 = value;
		calcPolygonMesh();
	}
	
	public float getY1 ()
	{
		return y1;
	}

	public void setY1 (float value)
	{
		if((width/2f +value)<(-width/2f +y2)) {
			value = -width+y2;
		}
		y1 = value;
		calcPolygonMesh();
	}
	
	public float getY2 ()
	{
		return y2;
	}

	public void setY2 (float value)
	{
		if((width/2f +y1)<(-width/2f +value)) {
			y1 = -width+value;
		}
		y2 = value;
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
