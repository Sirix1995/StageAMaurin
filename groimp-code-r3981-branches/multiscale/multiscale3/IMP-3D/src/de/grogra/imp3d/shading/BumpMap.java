
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

import javax.vecmath.Vector3f;

import de.grogra.graph.impl.Node.NType;
import de.grogra.math.Channel;
import de.grogra.math.ChannelData;
import de.grogra.math.ChannelMap;

public class BumpMap extends ChannelMapNode
{
	ChannelMap displacement = null;
	//enh:field edge=DISPLACEMENT getter setter

	float strength = 1;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field displacement$FIELD;
	public static final NType.Field strength$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (BumpMap.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 1:
					((BumpMap) o).strength = (float) value;
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
					return ((BumpMap) o).getStrength ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((BumpMap) o).displacement = (ChannelMap) value;
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
					return ((BumpMap) o).getDisplacement ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new BumpMap ());
		$TYPE.addManagedField (displacement$FIELD = new _Field ("displacement", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 0));
		$TYPE.addManagedField (strength$FIELD = new _Field ("strength", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.setSpecialEdgeField (displacement$FIELD, DISPLACEMENT);
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
		return new BumpMap ();
	}

	public float getStrength ()
	{
		return strength;
	}

	public void setStrength (float value)
	{
		this.strength = (float) value;
	}

	public ChannelMap getDisplacement ()
	{
		return displacement;
	}

	public void setDisplacement (ChannelMap value)
	{
		displacement$FIELD.setObject (this, value);
	}

//enh:end

	@Override
	public float getFloatValue (ChannelData data, int channel)
	{
		ChannelData in = data.getData (input);
		if (((channel >= Channel.NX) && (channel <= Channel.NZ))
			|| ((channel >= Channel.DPXDU) && (channel <= Channel.DPZDU))
			|| ((channel >= Channel.DPXDV) && (channel <= Channel.DPZDV)))
		{
			if (displacement == null)
			{
				return data.forwardGetFloatValue (in);
			}
			Vector3f n = data.v3f0, dpdu = data.v3f1, dpdv = data.v3f2;

			in.getTuple3f (n, data, Channel.NX);
			in.getTuple3f (dpdu, data, Channel.DPXDU);
			in.getTuple3f (dpdv, data, Channel.DPXDV);

			ChannelData d = data.getData (displacement);
			float s = 0.02f * strength / n.length ();
			dpdu.scaleAdd (s * d.getFloatValue (data, Channel.DPXDU), n, dpdu);
			dpdv.scaleAdd (s * d.getFloatValue (data, Channel.DPXDV), n, dpdv);
			data.setTuple3f (Channel.DPXDU, dpdu);
			data.setTuple3f (Channel.DPXDV, dpdv);
			n.cross (dpdu, dpdv);
			data.setTuple3f (Channel.NX, n);
			return data.getValidFloatValue (channel);
		}
		return data.forwardGetFloatValue (in);
	}

	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}
	
}
