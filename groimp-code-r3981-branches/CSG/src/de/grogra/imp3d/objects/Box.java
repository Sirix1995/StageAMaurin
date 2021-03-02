
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

import javax.media.opengl.GL;
import javax.vecmath.*;
import de.grogra.graph.*;
import de.grogra.imp.PickList;
import de.grogra.imp3d.*;
import de.grogra.imp3d.gl.GLDisplay;
import de.grogra.imp3d.ray.Raytraceable;
import de.grogra.imp3d.ray.RaytracerLeaf;
import de.grogra.reflect.XClass;
import de.grogra.reflect.XObject;

public class Box extends Axis implements Pickable, Renderable, Raytraceable, CSGable
{
	HalfEdgeStructCSG mesh = new HalfEdgeStructCSG();
	
	public static final int SHIFT_PIVOT_MASK = 1 << Axis.USED_BITS;

	public static final int USED_BITS = Axis.USED_BITS + 1;
	
	protected float width = 1;
	//enh:field attr=Attributes.WIDTH getter setter

	protected float height = 1;
	//enh:field attr=Attributes.HEIGHT getter setter
	
	// boolean shiftPivot
	//enh:field type=bits(SHIFT_PIVOT_MASK) attr=Attributes.SHIFT_PIVOT getter setter


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
		$TYPE.addDependency (shiftPivot$FIELD.getAttribute (), Attributes.TRANSFORMATION);
	}


	public static class LPattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public LPattern ()
		{
			super (Box.$TYPE, length$FIELD);
		}

		public static void signature (@In @Out Box b, float l)
		{
		}
	}


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, Box.$TYPE, new NType.Field[] {length$FIELD, width$FIELD, height$FIELD});
		}

		public static void signature (@In @Out Box b, float l, float w, float h)
		{
		}
	}

	
	
	public Box ()
	{
		super ();
	}
	
	
	public Box (float length)
	{
		super ();
		setLength (length);
	}

	
	public Box (float length, boolean shiftPivot)
	{
		super ();
		setLength (length);
		setShiftPivot (shiftPivot);
	}

	
	public Box (float length, float width, float height)
	{
		super ();
		setLength (length);
		setWidth (width);
		setHeight (height);
	}


	@Override
	protected float getPivotShift (Object object, boolean asNode, GraphState gs)
	{
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				return isShiftPivot () ? 0.5f * width : 0;
			}
			else
			{
				if (!gs.checkBoolean (this, true, Attributes.SHIFT_PIVOT, isShiftPivot ()))
				{
					return 0;
				}
				return 0.5f * (float) gs.checkDouble (this, true, Attributes.WIDTH, width);
			}
		}
		else
		{
			if (!gs.getBoolean (object, asNode, Attributes.SHIFT_PIVOT))
			{
				return 0;
			}
			return 0.5f * (float) gs.getDouble (this, true, Attributes.WIDTH);
		}
	}


	private static double pick (double lambda, double w, Tuple3d v,
								Vector3d d, int c, float w2, float l2, float h)
	{
		double t = (c == 0) ? d.x : (c == 1) ? d.y : d.z;
		if (Math.abs (t) > 1e-10)
		{
			t = w / t;
			if ((0 < t) && (t < lambda))
			{
				double x = t * d.x + v.x, y = t * d.y + v.y,
					z = t * d.z + v.z;
				if (((c == 0) || (Math.abs (x) <= w2))
					&& ((c == 1) || (Math.abs (y) <= l2))
					&& ((c == 2) || ((z >= 0) && (z <= h))))
				{
					return t;
				}
			}
		}
		return lambda;
	}


	public static void pick (float w2, float l2, float h, Point3d origin, Vector3d direction,
							 PickList list)
	{
		double l = Double.MAX_VALUE;
		l = pick (l, -w2 - origin.x, origin, direction, 0, w2, l2, h);
		l = pick (l, w2 - origin.x, origin, direction, 0, w2, l2, h);
		l = pick (l, -l2 - origin.y, origin, direction, 1, w2, l2, h);
		l = pick (l, l2 - origin.y, origin, direction, 1, w2, l2, h);
		l = pick (l, -origin.z, origin, direction, 2, w2, l2, h);
		l = pick (l, h - origin.z, origin, direction, 2, w2, l2, h);
		if (l < Double.MAX_VALUE)
		{
			list.add (l);
		}
	}


	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d t, PickList list)
	{
		GraphState gs = list.getGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				pick (width * 0.5f, height * 0.5f, length, origin, direction, list);
			}
			else
			{
				pick ((float) gs.checkDouble (this, true, Attributes.WIDTH, width) * 0.5f,
					  (float) gs.checkDouble (this, true, Attributes.HEIGHT, height) * 0.5f,
					  (float) gs.checkDouble (this, true, Attributes.LENGTH, length),
					  origin, direction, list);
			}
		}
		else
		{
			pick ((float) gs.getDouble (object, asNode, Attributes.WIDTH) * 0.5f,
				  (float) gs.getDouble (object, asNode, Attributes.HEIGHT) * 0.5f,
				  (float) gs.getDouble (object, asNode, Attributes.LENGTH),
				  origin, direction, list);
		}
	}

	
	public void draw (Object object, boolean asNode, RenderState rs)
	{
		GraphState gs = rs.getRenderGraphState ();
		if (object == this) 
		{
			
			if (gs.getInstancingPathIndex () <= 0)
			{
				rs.drawBox (width * 0.5f, height * 0.5f, length, null, RenderState.CURRENT_HIGHLIGHT, null);
			}
			else
			{
				rs.drawBox ((float) gs.checkDouble (this, true, Attributes.WIDTH, width) * 0.5f,
						  (float) gs.checkDouble (this, true, Attributes.HEIGHT, height) * 0.5f,
						  (float) gs.checkDouble (this, true, Attributes.LENGTH, length),
							null, RenderState.CURRENT_HIGHLIGHT, null);
			}
		}
		else
		{
			rs.drawBox ((float) gs.getDouble (object, asNode, Attributes.WIDTH) * 0.5f,
					  (float) gs.getDouble (object, asNode, Attributes.HEIGHT) * 0.5f,
					  (float) gs.getDouble (object, asNode, Attributes.LENGTH),
						null, RenderState.CURRENT_HIGHLIGHT, null);
		}
	}
	
	
	public RaytracerLeaf createRaytracerLeaf(Object object, boolean asNode, long pathId, GraphState gs) {
		
		// bugfix: new interpretation of size and fix point of the box 
		// is done at RTBox. 
		
		float w, l, h;
		
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				w = width;
				l = length;
				h = height;
			}
			else
			{
				w = (float) gs.checkDouble (this, true, Attributes.WIDTH, width);
				l = (float) gs.checkDouble (this, true, Attributes.LENGTH, length);
				h = (float) gs.checkDouble (this, true, Attributes.HEIGHT, height);
			}
		}
		else
		{
			w = (float) gs.getDouble (object, asNode, Attributes.WIDTH);
			l = (float) gs.getDouble (object, asNode, Attributes.LENGTH);
			h = (float) gs.getDouble (object, asNode, Attributes.HEIGHT);
		}

		return new de.grogra.imp3d.ray.RTBox(object,asNode,pathId,w,h,l);
	}

	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field width$FIELD;
	public static final NType.Field height$FIELD;
	public static final NType.Field shiftPivot$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Box.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Box) o).width = (float) value;
					return;
				case 1:
					((Box) o).height = (float) value;
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
					return ((Box) o).getWidth ();
				case 1:
					return ((Box) o).getHeight ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Box ());
		$TYPE.addManagedField (width$FIELD = new _Field ("width", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (height$FIELD = new _Field ("height", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (shiftPivot$FIELD = new NType.BitField ($TYPE, "shiftPivot", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, SHIFT_PIVOT_MASK));
		$TYPE.declareFieldAttribute (width$FIELD, Attributes.WIDTH);
		$TYPE.declareFieldAttribute (height$FIELD, Attributes.HEIGHT);
		$TYPE.declareFieldAttribute (shiftPivot$FIELD, Attributes.SHIFT_PIVOT);
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
		return new Box ();
	}

	public float getWidth ()
	{
		return width;
	}

	public void setWidth (float value)
	{
		this.width = (float) value;
	}

	public float getHeight ()
	{
		return height;
	}

	public void setHeight (float value)
	{
		this.height = (float) value;
	}

	public boolean isShiftPivot ()
	{
		return (bits & SHIFT_PIVOT_MASK) != 0;
	}

	public void setShiftPivot (boolean v)
	{
		if (v) bits |= SHIFT_PIVOT_MASK; else bits &= ~SHIFT_PIVOT_MASK;
	}

	//enh:end

	boolean usedInCSG = false;
	
	public boolean usedInCSG(){
		return usedInCSG;
	} 
	
	@Override
	public HalfEdgeStructCSG getMesh() {
//		System.out.println("insert Box");
		// TODO Auto-generated method stub
//		System.out.println(mesh.getFacesCount());
		float halfWidth = width / 2;
		float halfLength = length / 2;
		float halfHeight = height / 2;
		
		HalfEdgeStructCSG	meshed = new HalfEdgeStructCSG();
		de.grogra.imp3d.HalfEdgeUtil.insertBox(-halfWidth, 0, -halfHeight, halfWidth, length,
				halfHeight, meshed);
//		meshed.insertBox(-halfWidth, 0, -halfHeight, halfWidth, length,
//				halfHeight);
		meshed.setShaderPrimitive(	this.getShader());
		
		usedInCSG=true;
		
		return meshed;
	}
	
	@Override
	public boolean isActive() {
		if (mesh.getFacesCount()==0) {
//			System.out.println(mesh.getFacesCount());
			return false;}
		else return true;
	}

}
