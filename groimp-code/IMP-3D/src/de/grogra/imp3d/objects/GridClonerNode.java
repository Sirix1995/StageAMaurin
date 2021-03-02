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

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.imp.PickList;
import de.grogra.imp3d.PickRayVisitor;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;

public class GridClonerNode extends Null implements Pickable, Renderable
{
	protected int xCount = 10;
	//enh:field getter setter

	protected float xDistance = 10;
	//enh:field getter setter

	protected int yCount = 10;
	//enh:field getter setter

	protected float yDistance = 10;
	//enh:field getter setter

	
	public GridClonerNode ()
	{
	}

	public GridClonerNode (int xc, float xd, int yc, float yd)
	{
		xCount = xc;
		xDistance = xd;
		yCount = yc;
		yDistance = yd;
	}

	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
			  Matrix4d t, PickList list)
	{
		Point3d pt = new Point3d();
		for (int x = 0; x < xCount; ++x)
		{
			for (int y = 0; y < yCount; ++y)
			{
				pt.x = x * xDistance;
				pt.y = y * yDistance;
				PickRayVisitor.pickPoint (origin, direction, pt, t, list, 8);
			}
		}
	}

	public void draw (Object object, boolean asNode, RenderState rs)
	{
		for (int x = 0; x < xCount; ++x)
		{
			for (int y = 0; y < yCount; ++y)
			{
				rs.drawPoint (new Vector3f(x * xDistance, y * yDistance, 0), 10, new Color3f(1, 1, 0), RenderState.CURRENT_HIGHLIGHT, null);
			}
		}
	}

	//enh:insert $TYPE.addIdentityAccessor (Attributes.SHAPE);
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field xCount$FIELD;
	public static final NType.Field xDistance$FIELD;
	public static final NType.Field yCount$FIELD;
	public static final NType.Field yDistance$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (GridClonerNode.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 0:
					((GridClonerNode) o).xCount = (int) value;
					return;
				case 2:
					((GridClonerNode) o).yCount = (int) value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 0:
					return ((GridClonerNode) o).getXCount ();
				case 2:
					return ((GridClonerNode) o).getYCount ();
			}
			return super.getInt (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 1:
					((GridClonerNode) o).xDistance = (float) value;
					return;
				case 3:
					((GridClonerNode) o).yDistance = (float) value;
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
					return ((GridClonerNode) o).getXDistance ();
				case 3:
					return ((GridClonerNode) o).getYDistance ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new GridClonerNode ());
		$TYPE.addManagedField (xCount$FIELD = new _Field ("xCount", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.INT, null, 0));
		$TYPE.addManagedField (xDistance$FIELD = new _Field ("xDistance", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (yCount$FIELD = new _Field ("yCount", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.INT, null, 2));
		$TYPE.addManagedField (yDistance$FIELD = new _Field ("yDistance", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 3));
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
		return new GridClonerNode ();
	}

	public int getXCount ()
	{
		return xCount;
	}

	public void setXCount (int value)
	{
		this.xCount = (int) value;
	}

	public int getYCount ()
	{
		return yCount;
	}

	public void setYCount (int value)
	{
		this.yCount = (int) value;
	}

	public float getXDistance ()
	{
		return xDistance;
	}

	public void setXDistance (float value)
	{
		this.xDistance = (float) value;
	}

	public float getYDistance ()
	{
		return yDistance;
	}

	public void setYDistance (float value)
	{
		this.yDistance = (float) value;
	}

//enh:end
}
