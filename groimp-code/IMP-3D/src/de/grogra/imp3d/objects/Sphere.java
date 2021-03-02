
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

public class Sphere extends ShadedNull
	implements Pickable, Renderable, Raytraceable
{

	protected float radius;
	//enh:field attr=Attributes.RADIUS getter setter


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (Sphere.$TYPE, radius$FIELD);
		}

		public static void signature (@In @Out Sphere s, float r)
		{
		}
	}


	public Sphere ()
	{
		this (1f);
	}


	public Sphere (float radius)
	{
		super ();
		this.radius = radius;
	}


	public static void pick (float radius, Point3d origin, Vector3d direction,
							 PickList list)
	{
		double bx, by, bz, s, t;
		bx = -origin.x;
		by = -origin.y;
		bz = -origin.z;
		s = bx * direction.x + by * direction.y + bz * direction.z;
		t = direction.lengthSquared ();
		bx = s * s + t * (radius * radius - bx * bx - by * by - bz * bz);
		if (bx < 0)
		{
			return;
		}
		bx = (bx <= 0d) ? 0d : Math.sqrt (bx);
		if (s >= bx)
		{
			list.add ((s - bx) / t);
		}
		else if (s + bx >= 0d)
		{
			list.add ((s + bx) / t);
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
				pick (radius, origin, direction, list);
			}
			else
			{
				pick (gs.checkFloat (this, true, Attributes.RADIUS, radius),
					  origin, direction, list);
			}
		}
		else
		{
			pick (gs.getFloat (object, asNode, Attributes.RADIUS),
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
				rs.drawSphere (radius, null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
			}
			else
			{
				rs.drawSphere (gs.checkFloat (this, true, Attributes.RADIUS, radius),
							   null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
			}
		}
		else
		{
			rs.drawSphere (gs.getFloat (object, asNode, Attributes.RADIUS),
						   null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
		}
	}


	@Override
	public RaytracerLeaf createRaytracerLeaf
		(Object object, boolean asNode, long pathId, GraphState gs)
	{	
		float cur_radius = 0.0f;
		
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				cur_radius = radius;
			}
			else
			{
				cur_radius = gs.checkFloat (this, true, Attributes.RADIUS, radius);
			}
		}
		else
		{
			cur_radius = gs.getFloat (object, asNode, Attributes.RADIUS);
		}
		
		return new de.grogra.imp3d.ray.RTSphere(object,asNode,pathId,cur_radius);
	}


//	enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field radius$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Sphere.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Sphere) o).radius = value;
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
					return ((Sphere) o).getRadius ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Sphere ());
		$TYPE.addManagedField (radius$FIELD = new _Field ("radius", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.declareFieldAttribute (radius$FIELD, Attributes.RADIUS);
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
		return new Sphere ();
	}

	public float getRadius ()
	{
		return radius;
	}

	public void setRadius (float value)
	{
		this.radius = value;
	}

//enh:end


	/**
	 * Calculates the area of an object.
	 * Intersection with other object are not considered.The total area will be calculated.
	 * A=4*Pi*r^2
	 * 
	 * @return area
	 */
	@Override
	public double getSurfaceArea() {
		return 4*Math.PI*Math.pow(radius, 2);
	}

	/**
	 * Calculates the volume.
	 * Intersection with other object are not considered.The total volume will be calculated.
	 * V=4/3*Pi*r^3
	 * 
	 * @return volume
	 */
	@Override
	public double getVolume() {
		return 4/3f*Math.PI*Math.pow(radius, 3);
	}
	
}
