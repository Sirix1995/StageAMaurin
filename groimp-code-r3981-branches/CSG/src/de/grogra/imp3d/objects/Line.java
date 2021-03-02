
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
import de.grogra.imp.PickList;
import de.grogra.imp3d.*;
import de.grogra.math.Tuple3fType;

public class Line extends ColoredNull implements Renderable, Pickable
{
	final Vector3f axis = new Vector3f (0, 0, 1);
	//enh:field type=Tuple3fType.VECTOR set=set attr=Attributes.AXIS getter

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field axis$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Line.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Line) o).axis.set ((Vector3f) value);
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Line) o).getAxis ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Line ());
		$TYPE.addManagedField (axis$FIELD = new _Field ("axis", _Field.FINAL  | _Field.SCO, Tuple3fType.VECTOR, null, 0));
		$TYPE.declareFieldAttribute (axis$FIELD, Attributes.AXIS);
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
		return new Line ();
	}

	public Vector3f getAxis ()
	{
		return axis;
	}

//enh:end

	public Line ()
	{
		super ();
		setTransforming (false);
	}


	public Line (float dx, float dy, float dz)
	{
		this ();
		axis.set (dx, dy, dz);
	}


	public Line (Vector3f axis)
	{
		this ();
		this.axis.set (axis);
	}


	public Line (float dx, float dy, float dz, boolean transforming)
	{
		this ();
		axis.set (dx, dy, dz);
		setTransforming (transforming);
	}


	public Line (float x, float y, float z, float dx, float dy, float dz)
	{
		this ();
		setTransform (x, y, z);
		axis.set (dx, dy, dz);
	}

	
	public void setAxis (float x, float y, float z)
	{
		axis.set (x, y, z);
	}


	public static void pick (Vector3f axis, Point3d origin, Vector3d direction,
							 Matrix4d t, PickList list)
	{
		list.p3d0.set (0, 0, 0);
		list.v3d0.set (axis);
		PickRayVisitor.pickLine (list.p3d0, list.v3d0, origin, direction, t, list, 4);
	}


	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d t, PickList list)
	{
		GraphState gs = list.getGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				pick (axis, origin, direction, t, list);
			}
			else
			{
				pick ((Vector3f) gs.checkObject (this, true, Attributes.AXIS, axis),
					  origin, direction, t, list);
			}
		}
		else
		{
			pick ((Vector3f) gs.getObject (object, asNode, list.v3f0, Attributes.AXIS),
				  origin, direction, t, list);
		}
	}


	private static Tuple3f ZERO = new Point3f ();

	public void draw (Object object, boolean asNode, RenderState rs)
	{
		GraphState gs = rs.getRenderGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				rs.drawLine (ZERO, axis, color, RenderState.CURRENT_HIGHLIGHT, null);
			}
			else
			{
				rs.drawLine (ZERO, (Vector3f) gs.checkObject (this, true, Attributes.AXIS, axis),
					(Tuple3f) gs.checkObject (this, true, Attributes.COLOR, color), RenderState.CURRENT_HIGHLIGHT, null);
			}
		}
		else
		{
			rs.drawLine (ZERO, (Vector3f) gs.getObject (object, asNode, rs.getPool ().v3f0, Attributes.AXIS),
				(Tuple3f) gs.getObject (object, asNode, Attributes.COLOR), RenderState.CURRENT_HIGHLIGHT, null);
		}
	}


	@Override
	public void postTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, Matrix4d pre,
							   GraphState gs)
	{
	transform:
		{
			Vector3f a;
			if (object == this) 
			{
				if (gs.getInstancingPathIndex () <= 0)
				{
					if ((bits & TRANSFORMING_MASK) == 0)
					{
						break transform;
					}
					a = axis;
				}
				else
				{
					if (!gs.checkBoolean (this, true, Attributes.TRANSFORMING, (bits & TRANSFORMING_MASK) != 0))
					{
						break transform;
					}
					a = (Vector3f) gs.checkObject (this, true, Attributes.AXIS, axis);
				}
			}
			else
			{
				if (!gs.getBoolean (object, asNode, Attributes.TRANSFORMING))
				{
					break transform;
				}
				a = (Vector3f) gs.getObject (object, asNode, null, Attributes.AXIS);
			}
			out.set (in);
			out.m03 += out.m00 * a.x + out.m01 * a.y + out.m02 * a.z;
			out.m13 += out.m10 * a.x + out.m11 * a.y + out.m12 * a.z;
			out.m23 += out.m20 * a.x + out.m21 * a.y + out.m22 * a.z;
			return;
		}
		if (out != pre)
		{
			out.set (pre);
		}
	}

}
