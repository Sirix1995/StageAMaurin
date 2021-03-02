
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

import de.grogra.graph.impl.Node.NType;
import de.grogra.math.Channel;
import de.grogra.math.ChannelData;
import de.grogra.math.ChannelMap;

public class ChannelBlend extends ChannelMapNode
{
	public static final int BLEND = ChannelMapNode.MIN_UNUSED_SPECIAL_OF_TARGET;
	public static final int MIN_UNUSED_SPECIAL_OF_TARGET = BLEND + 1;


	BlendItem blend;
	//enh:field edge=BLEND getter setter

	
	public ChannelBlend ()
	{
	}

	public ChannelBlend (ChannelMap input)
	{
		this.input = input;
	}

	@Override
	public float getFloatValue (ChannelData data, int channel)
	{
		ChannelData in = data.getData (input);
		float t = in.getFloatValue (data, Channel.X);
		BlendItem last = null;
		for (BlendItem b = blend; b != null; b = b.next)
		{
			if (b.value > t)
			{
				if (last != null)
				{
					return (data.getData (last.input).getFloatValue (data, channel)
							* (b.value - t)
							+ data.getData (b.input).getFloatValue (data, channel)
							* (t - last.value)) / (b.value - last.value);
				}
				else
				{
					return data.getData (b.input).getFloatValue (data, channel);
				}
			}
			last = b;
		}
		return ((last != null) ? data.getData (last.input) : in)
			.getFloatValue (data, channel);
	}

	private static void initType ()
	{
		$TYPE.declareSpecialEdge (BLEND, "channel.blend", new BlendItem[0]);
	}

	
	//enh:insert initType();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field blend$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (ChannelBlend.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((ChannelBlend) o).blend = (BlendItem) value;
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
					return ((ChannelBlend) o).getBlend ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new ChannelBlend ());
		$TYPE.addManagedField (blend$FIELD = new _Field ("blend", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (BlendItem.class), null, 0));
		$TYPE.setSpecialEdgeField (blend$FIELD, BLEND);
		initType();
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
		return new ChannelBlend ();
	}

	public BlendItem getBlend ()
	{
		return blend;
	}

	public void setBlend (BlendItem value)
	{
		blend$FIELD.setObject (this, value);
	}
	
	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}

//enh:end


}
