/*
 * Copyright (C) 2016 GroIMP Developer Team
 *
 * Department Ecoinformatics, Biometrics and Forest Growth, University of Göttingen, Germany
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package de.grogra.imp3d.objects;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.GraphState;
import de.grogra.imp.PickList;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.ray.Raytraceable;
import de.grogra.imp3d.ray.RaytracerLeaf;


/**
 * A box with a parameterizable (sinus-)waves on the top side.
 * 
 * Function: a*sin(b*x)
 * 
 * @author MH
 *
 */
public class Lamella extends Axis implements Pickable, Renderable, Raytraceable
{
	public static final int SHIFT_PIVOT_MASK = 1 << Axis.USED_BITS;

	public static final int USED_BITS = Axis.USED_BITS + 1;

	
	protected float width = 1;
	//enh:field attr=Attributes.WIDTH getter setter

	protected float height = 1;
	//enh:field attr=Attributes.HEIGHT getter setter
	
	// boolean shiftPivot
	//enh:field type=bits(SHIFT_PIVOT_MASK) attr=Attributes.SHIFT_PIVOT getter setter

	//amplitudes
	protected float a = 0.03f;
	//enh:field attr=Attributes.LAMELLA_A getter setter
	//frequency
	protected float b = 0.1f;
	//enh:field attr=Attributes.LAMELLA_B getter setter
	
	
	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
		$TYPE.addDependency (shiftPivot$FIELD.getAttribute (), Attributes.TRANSFORMATION);
	}


	public static class LPattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public LPattern ()
		{
			super (Lamella.$TYPE, length$FIELD);
		}

		public static void signature (@In @Out Lamella b, float l)
		{
		}
	}


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, Lamella.$TYPE, new NType.Field[] {length$FIELD, width$FIELD, height$FIELD});
		}

		public static void signature (@In @Out Lamella b, float l, float w, float h)
		{
		}
	}

	
	
	public Lamella ()
	{
		super ();
	}
	
	
	public Lamella (float length, float a, float b)
	{
		super ();
		setLength (length);
		setFunctionParameters(a, b);
	}

	
	public Lamella (float length, float a, float b, boolean shiftPivot)
	{
		super ();
		setLength (length);
		setShiftPivot (shiftPivot);
		setFunctionParameters(a, b);
	}

	
	public Lamella (float length, float width, float height, float a, float b)
	{
		super ();
		setLength (length);
		setWidth (width);
		setHeight (height);
		setFunctionParameters(a, b);
	}

	
	public void setFunctionParameters(float a, float b) {
		this.a = a;
		this.b = b;
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


	@Override
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

	
	@Override
	public void draw (Object object, boolean asNode, RenderState rs)
	{
		GraphState gs = rs.getRenderGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				rs.drawLamella (width * 0.5f, height * 0.5f, length, a,b, null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
			}
			else
			{
				rs.drawLamella ((float) gs.checkDouble (this, true, Attributes.WIDTH, width) * 0.5f,
						(float) gs.checkDouble (this, true, Attributes.HEIGHT, height) * 0.5f,
						(float) gs.checkDouble (this, true, Attributes.LENGTH, length),
						gs.checkFloat (this, true, Attributes.LAMELLA_A, a),
						gs.checkFloat (this, true, Attributes.LAMELLA_B, b),
						null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
			}
		}
		else
		{
			rs.drawLamella ((float) gs.getDouble (object, asNode, Attributes.WIDTH) * 0.5f,
						(float) gs.getDouble (object, asNode, Attributes.HEIGHT) * 0.5f,
						(float) gs.getDouble (object, asNode, Attributes.LENGTH),
						gs.checkFloat (this, true, Attributes.LAMELLA_A, a),
						gs.checkFloat (this, true, Attributes.LAMELLA_B, b),
						null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
		}
	}
	
	
	@Override
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
	public static final NType.Field a$FIELD;
	public static final NType.Field b$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Lamella.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Lamella) o).width = value;
					return;
				case 1:
					((Lamella) o).height = value;
					return;
				case 2:
					((Lamella) o).a = value;
					return;
				case 3:
					((Lamella) o).b = value;
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
					return ((Lamella) o).getWidth ();
				case 1:
					return ((Lamella) o).getHeight ();
				case 2:
					return ((Lamella) o).getA ();
				case 3:
					return ((Lamella) o).getB ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Lamella ());
		$TYPE.addManagedField (width$FIELD = new _Field ("width", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (height$FIELD = new _Field ("height", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (shiftPivot$FIELD = new NType.BitField ($TYPE, "shiftPivot", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, SHIFT_PIVOT_MASK));
		$TYPE.addManagedField (a$FIELD = new _Field ("a", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (b$FIELD = new _Field ("b", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 3));
		$TYPE.declareFieldAttribute (width$FIELD, Attributes.WIDTH);
		$TYPE.declareFieldAttribute (height$FIELD, Attributes.HEIGHT);
		$TYPE.declareFieldAttribute (shiftPivot$FIELD, Attributes.SHIFT_PIVOT);
		$TYPE.declareFieldAttribute (a$FIELD, Attributes.LAMELLA_A);
		$TYPE.declareFieldAttribute (b$FIELD, Attributes.LAMELLA_B);
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
		return new Lamella ();
	}

	public float getWidth ()
	{
		return width;
	}

	public void setWidth (float value)
	{
		this.width = value;
	}

	public float getHeight ()
	{
		return height;
	}

	public void setHeight (float value)
	{
		this.height = value;
	}

	public float getA ()
	{
		return a;
	}

	public void setA (float value)
	{
		this.a = value;
	}

	public float getB ()
	{
		return b;
	}

	public void setB (float value)
	{
		this.b = value;
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

	/**
	 * Approximation of the surface area of an object (approximated by a box).
	 * Intersection with other object are not considered.The total area will be calculated.
	 * A=2*(a*b + a*c + b*c)
	 * 
	 * @return area
	 */
	@Override
	public double getSurfaceArea() {
		return 2*(length*width+ length*height + width*height);
	}

	/**
	 * Approximation of the volume (approximated by a box).
	 * Intersection with other object are not considered.The total volume will be calculated.
	 * V=a*b*c
	 * 
	 * @return volume
	 */
	@Override
	public double getVolume() {
		return length*width*height;
	}

}
