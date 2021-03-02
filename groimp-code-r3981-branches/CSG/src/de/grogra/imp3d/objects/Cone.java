
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

import javax.vecmath.*;
import de.grogra.graph.*;
import de.grogra.imp3d.*;
import de.grogra.imp3d.ray.Raytraceable;
import de.grogra.imp3d.ray.RaytracerLeaf;
import de.grogra.reflect.XClass;
import de.grogra.reflect.XObject;

public class Cone extends FrustumBase implements Raytraceable, CSGable
{
	protected float radius;
	//enh:field attr=Attributes.RADIUS getter setter

	// boolean open
	//enh:field type=bits(BASE_OPEN_MASK) attr=Attributes.OPEN getter setter

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, Cone.$TYPE, new NType.Field[] {length$FIELD, radius$FIELD});
		}

		public static void signature (@In @Out Cone c, float l, float r)
		{
		}
	}


	public Cone ()
	{
		this (1, 1);
	}


	public Cone (float length, float radius)
	{
		super ();
		setLength (length);
		this.radius = radius;
	}


	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d t, de.grogra.imp.PickList list)
	{
		GraphState gs = list.getGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				pick (length, radius, 0,
					  (bits & BASE_OPEN_MASK) == 0, false,
					  origin, direction, list);
			}
			else
			{
				pick ((float) gs.checkDouble (this, true, Attributes.LENGTH, length),
					  gs.checkFloat (this, true, Attributes.RADIUS, radius), 0,
					  !gs.checkBoolean (this, true, Attributes.OPEN, (bits & BASE_OPEN_MASK) != 0), false,
					  origin, direction, list);
			}
		}
		else
		{
			pick ((float) gs.getDouble (object, asNode, Attributes.LENGTH),
				  gs.getFloat (object, asNode, Attributes.RADIUS), 0,
				  !gs.getBoolean (object, asNode, Attributes.OPEN), false,
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
				rs.drawFrustum (length, radius, 0, (bits & BASE_OPEN_MASK) == 0, false,
								isScaleV () ? length : 1, null, RenderState.CURRENT_HIGHLIGHT, null);
			}
			else
			{
				float len = (float) gs.checkDouble (this, true, Attributes.LENGTH, length);
				rs.drawFrustum (len,
								gs.checkFloat (this, true, Attributes.RADIUS, radius), 0,
								!gs.checkBoolean (this, true, Attributes.OPEN, (bits & BASE_OPEN_MASK) != 0), false,
								gs.checkBoolean (this, true, Attributes.SCALE_V, isScaleV ()) ? len : 1, null, RenderState.CURRENT_HIGHLIGHT, null);
			}
		}
		else
		{
			float len = (float) gs.getDouble (object, asNode, Attributes.LENGTH);
			rs.drawFrustum (len,
							gs.getFloat (object, asNode, Attributes.RADIUS), 0,
							!gs.getBoolean (object, asNode, Attributes.OPEN), false,
							gs.getBoolean (object, asNode, Attributes.SCALE_V) ? len : 1, null, RenderState.CURRENT_HIGHLIGHT, null);
		}
	}

	public RaytracerLeaf createRaytracerLeaf(Object object, boolean asNode, long pathId, GraphState gs) {
		
		float cur_radius = 1.0f;
		Vector3f cur_axis = new Vector3f ();
		boolean open_bottom = false;
		
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				cur_radius  = this.radius;
				cur_axis.z  = this.length;
				open_bottom = this.isOpen();
			}
			else
			{
				cur_radius  = gs.checkFloat(this,true,Attributes.RADIUS,this.radius);
				cur_axis.z  = (float)gs.checkDouble(this,true,Attributes.LENGTH,this.length);
				open_bottom = gs.checkBoolean(this,true,Attributes.OPEN,this.isOpen());
			}
		}
		else
		{
			cur_radius  = gs.getFloat (object, asNode, Attributes.RADIUS);
			cur_axis.z  = (float)gs.getDouble(object,asNode,Attributes.LENGTH);
			open_bottom = gs.getBoolean(object,asNode,Attributes.OPEN);
		}
		
		return new de.grogra.imp3d.ray.RTCone(object,asNode,pathId,
				cur_radius,cur_axis,open_bottom);
	}

//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field radius$FIELD;
	public static final NType.Field open$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Cone.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Cone) o).radius = (float) value;
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
					return ((Cone) o).getRadius ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Cone ());
		$TYPE.addManagedField (radius$FIELD = new _Field ("radius", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (open$FIELD = new NType.BitField ($TYPE, "open", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, BASE_OPEN_MASK));
		$TYPE.declareFieldAttribute (radius$FIELD, Attributes.RADIUS);
		$TYPE.declareFieldAttribute (open$FIELD, Attributes.OPEN);
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
		return new Cone ();
	}

	public float getRadius ()
	{
		return radius;
	}

	public void setRadius (float value)
	{
		this.radius = (float) value;
	}

	public boolean isOpen ()
	{
		return (bits & BASE_OPEN_MASK) != 0;
	}

	public void setOpen (boolean v)
	{
		if (v) bits |= BASE_OPEN_MASK; else bits &= ~BASE_OPEN_MASK;
	}


	@Override
	public boolean usedInCSG() {
		// TODO Auto-generated method stub
		return usedInCSG;
	}

	boolean usedInCSG=false;

	@Override
	public HalfEdgeStructCSG getMesh() {
//		System.out.println("insert Cone");
		// TODO Auto-generated method stub
		HalfEdgeStructCSG	meshed = new HalfEdgeStructCSG();
		de.grogra.imp3d.HalfEdgeUtil.insertCone(16, this.radius, meshed);
//		meshed.insertCone(8, true, true, this.radius);
		meshed.setShaderPrimitive(	this.getShader());
		
		usedInCSG=true;
		
		return meshed;
	}


	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

//enh:end


}
