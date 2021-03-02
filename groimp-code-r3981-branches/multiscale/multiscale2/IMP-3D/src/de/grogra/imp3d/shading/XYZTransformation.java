
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

package de.grogra.imp3d.shading;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;

import de.grogra.graph.impl.Node.NType;
import de.grogra.math.Channel;
import de.grogra.math.ChannelData;
import de.grogra.math.Transform3D;

public class XYZTransformation extends ChannelMapNode
{
	Transform3D transform;
	//enh:field getter setter

	boolean useGlobal;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field transform$FIELD;
	public static final NType.Field useGlobal$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (XYZTransformation.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 1:
					((XYZTransformation) o).useGlobal = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 1:
					return ((XYZTransformation) o).isUseGlobal ();
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((XYZTransformation) o).transform = (Transform3D) value;
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
					return ((XYZTransformation) o).getTransform ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new XYZTransformation ());
		$TYPE.addManagedField (transform$FIELD = new _Field ("transform", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Transform3D.class), null, 0));
		$TYPE.addManagedField (useGlobal$FIELD = new _Field ("useGlobal", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 1));
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
		return new XYZTransformation ();
	}

	public boolean isUseGlobal ()
	{
		return useGlobal;
	}

	public void setUseGlobal (boolean value)
	{
		this.useGlobal = (boolean) value;
	}

	public Transform3D getTransform ()
	{
		return transform;
	}

	public void setTransform (Transform3D value)
	{
		transform$FIELD.setObject (this, value);
	}

//enh:end



	@Override
	public float getFloatValue (ChannelData data, int channel)
	{
		ChannelData in = data.getData (input);
		switch (channel)
		{
			case Channel.X:
			case Channel.Y:
			case Channel.Z:
				Point3f p = data.p3f0;
				if (transform != null)
				{
					Matrix4d m = data.m4d0;
					m.setIdentity ();
					transform.transform (m, m = data.m4d1);
					in.getTuple3f (p, data, useGlobal ? Channel.PX : Channel.X);
					m.transform (p);
				}
				else if (useGlobal)
				{
					in.getTuple3f (p, data, Channel.PX);
				}
				else
				{
					break;
				}
				data.setTuple3f (Channel.X, p);
				return data.getValidFloatValue (channel);
		}
		return data.forwardGetFloatValue (in);
	}

	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}
	
	
}
