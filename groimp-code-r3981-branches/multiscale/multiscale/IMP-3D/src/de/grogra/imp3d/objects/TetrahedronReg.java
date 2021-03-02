package de.grogra.imp3d.objects;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.GraphState;
import de.grogra.imp.PickList;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.ray.Raytraceable;
import de.grogra.imp3d.ray.RaytracerLeaf;

public class TetrahedronReg extends ShadedNull
	implements Pickable, Renderable, Raytraceable
{

	protected float length;
	//enh:field attr=Attributes.RADIUS getter setter


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (TetrahedronReg.$TYPE, length$FIELD);
		}

		public static void signature (@In @Out TetrahedronReg s, float r)
		{
		}
	}


	public TetrahedronReg ()
	{
		this (1f);
	}


	public TetrahedronReg (float length)
	{
		super ();
		this.length = length;
	}


	public static void pick (float length, Point3d origin, Vector3d direction,
							 PickList list)
	{
//		double bx, by, bz, s, t;
//		bx = -origin.x;
//		by = -origin.y;
//		bz = -origin.z;
//		s = bx * direction.x + by * direction.y + bz * direction.z;
//		t = direction.lengthSquared ();
//		bx = s * s + t * (length * length - bx * bx - by * by - bz * bz);
//		if (bx < 0)
//		{
//			return;
//		}
//		bx = (bx <= 0d) ? 0d : Math.sqrt (bx);
//		if (s >= bx)
//		{
//			list.add ((s - bx) / t);
//		}
//		else if (s + bx >= 0d)
//		{
//			list.add ((s + bx) / t);
//		}
	}


	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d t, PickList list)
	{
//		GraphState gs = list.getGraphState ();
//		if (object == this)
//		{
//			if (gs.getInstancingPathIndex () <= 0)
//			{
//				pick (length, origin, direction, list);
//			}
//			else
//			{
//				pick (gs.checkFloat (this, true, Attributes.RADIUS, length),
//					  origin, direction, list);
//			}
//		}
//		else
//		{
//			pick (gs.getFloat (object, asNode, Attributes.RADIUS),
//				  origin, direction, list);
//		}
	}


	public void draw (Object object, boolean asNode, RenderState rs)
	{
		GraphState gs = rs.getRenderGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				rs.drawTetrahedronReg(length, null, RenderState.CURRENT_HIGHLIGHT, null);
			}
			else
			{
				rs.drawTetrahedronReg (gs.checkFloat (this, true, Attributes.RADIUS, length),
							   null, RenderState.CURRENT_HIGHLIGHT, null);
			}
		}
		else
		{
			rs.drawTetrahedronReg (gs.getFloat (object, asNode, Attributes.RADIUS),
						   null, RenderState.CURRENT_HIGHLIGHT, null);
		}
	}


	public RaytracerLeaf createRaytracerLeaf
		(Object object, boolean asNode, long pathId, GraphState gs)
	{	
//		float cur_radius = 0.0f;
//		
//		if (object == this) 
//		{
//			if (gs.getInstancingPathIndex () <= 0)
//			{
//				cur_radius = length;
//			}
//			else
//			{
//				cur_radius = gs.checkFloat (this, true, Attributes.RADIUS, length);
//			}
//		}
//		else
//		{
//			cur_radius = gs.getFloat (object, asNode, Attributes.RADIUS);
//		}
//		
//		return new de.grogra.imp3d.ray.RTSphere(object,asNode,pathId,cur_radius);
		return null;
	}


//	enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field length$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (TetrahedronReg.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((TetrahedronReg) o).length = (float) value;
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
					return ((TetrahedronReg) o).getRadius ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new TetrahedronReg ());
		$TYPE.addManagedField (length$FIELD = new _Field ("length", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.declareFieldAttribute (length$FIELD, Attributes.RADIUS);
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
		return new TetrahedronReg ();
	}

	public float getRadius ()
	{
		return length;
	}

	public void setRadius (float value)
	{
		this.length = (float) value;
	}

//enh:end

}
