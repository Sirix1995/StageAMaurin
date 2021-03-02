
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
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node.NType;
import de.grogra.imp.PickList;
import de.grogra.imp3d.PickRayVisitor;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.shading.Light;
import de.grogra.math.Tuple3fType;

public class LightNode extends Null implements Renderable, Pickable
{
	protected Light light;
	//enh:field attr=Attributes.LIGHT getter setter

	public LightNode ()
	{
		this (1, 1, 1);
	}


	public LightNode (float r, float g, float b)
	{
		super ();
		PointLight l = new PointLight ();
		l.getColor ().set (r, g, b);
		light = l;
	}

	
	private static Tuple3f ZERO = new Point3f ();

	public static void draw (Light light, RenderState rs)
	{
		Tuple3f col = rs.getPool ().p3f0;
		Tuple3fType.setColor (col, light.getAverageColor ());
		rs.drawPoint (ZERO, 10, col, RenderState.CURRENT_HIGHLIGHT, null);
		if (light instanceof LightBase)
		{
			((LightBase) light).draw (col, rs);
		}
	}


	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d t, PickList list)
	{
		PickRayVisitor.pickPoint (origin, direction, t, list, 8);
	}


	public void draw (Object object, boolean asNode, RenderState rs)
	{
		GraphState gs = rs.getRenderGraphState ();
		Light s;
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				s = light;
			}
			else
			{
				s = (Light) gs.checkObject
					(this, true, Attributes.LIGHT, light);
			}
		}
		else
		{
			s = (Light) gs.getObject (object, asNode, Attributes.LIGHT);
		}
		
		if( s != null )
			draw (s, rs);
	}


//	enh:insert $TYPE.addIdentityAccessor (Attributes.SHAPE);
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field light$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (LightNode.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((LightNode) o).light = (Light) value;
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
					return ((LightNode) o).getLight ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new LightNode ());
		$TYPE.addManagedField (light$FIELD = new _Field ("light", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Light.class), null, 0));
		$TYPE.declareFieldAttribute (light$FIELD, Attributes.LIGHT);
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
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
		return new LightNode ();
	}

	public Light getLight ()
	{
		return light;
	}

	public void setLight (Light value)
	{
		light$FIELD.setObject (this, value);
	}

//enh:end


}
