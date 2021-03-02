
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
import de.grogra.vecmath.Math2;

public class WaveMap extends ChannelMapNode
{
	float amplitude = 1;
	//enh:field quantity=LENGTH getter setter

	float uCount = 10;
	//enh:field getter setter

	float vCount = 10;
	//enh:field getter setter

	boolean boxTop = false;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field amplitude$FIELD;
	public static final NType.Field uCount$FIELD;
	public static final NType.Field vCount$FIELD;
	public static final NType.Field boxTop$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (WaveMap.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 3:
					((WaveMap) o).boxTop = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 3:
					return ((WaveMap) o).isBoxTop ();
			}
			return super.getBoolean (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((WaveMap) o).amplitude = (float) value;
					return;
				case 1:
					((WaveMap) o).uCount = (float) value;
					return;
				case 2:
					((WaveMap) o).vCount = (float) value;
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
					return ((WaveMap) o).getAmplitude ();
				case 1:
					return ((WaveMap) o).getUCount ();
				case 2:
					return ((WaveMap) o).getVCount ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new WaveMap ());
		$TYPE.addManagedField (amplitude$FIELD = new _Field ("amplitude", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (uCount$FIELD = new _Field ("uCount", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (vCount$FIELD = new _Field ("vCount", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (boxTop$FIELD = new _Field ("boxTop", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 3));
		amplitude$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
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
		return new WaveMap ();
	}

	public boolean isBoxTop ()
	{
		return boxTop;
	}

	public void setBoxTop (boolean value)
	{
		this.boxTop = (boolean) value;
	}

	public float getAmplitude ()
	{
		return amplitude;
	}

	public void setAmplitude (float value)
	{
		this.amplitude = (float) value;
	}

	public float getUCount ()
	{
		return uCount;
	}

	public void setUCount (float value)
	{
		this.uCount = (float) value;
	}

	public float getVCount ()
	{
		return vCount;
	}

	public void setVCount (float value)
	{
		this.vCount = (float) value;
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
			float u = in.getFloatValue (data, Channel.U);
			float v = in.getFloatValue (data, Channel.V);
			if (boxTop)
			{
				u -= 0.25f;
				v -= 1f/3;
			}
			if (!boxTop || ((u >= 0) && (u <= 0.25) && (v >= 0) && (v <= 1f/3)))
			{
				Vector3f n = data.v3f0, dpdu = data.v3f1, dpdv = data.v3f2;
				in.getTuple3f (n, data, Channel.NX);
				in.getTuple3f (dpdu, data, Channel.DPXDU);
				in.getTuple3f (dpdv, data, Channel.DPXDV);
	
				// the formula for the amplitude is h = cos(2 pi u uCount) * cos(2 pi v vCount)
				float su = Math2.M_2PI * uCount;
				float sv = Math2.M_2PI * vCount;
				if (boxTop)
				{
					su *= 4;
					sv *= 3;
				}
				u *= su;
				v *= sv;
				float dhdu = -su * (float) (Math.sin(u) * Math.cos(v)); 
				float dhdv = -sv * (float) (Math.cos(u) * Math.sin(v));
				// the tangents are modified as if the vector amplitude*h*normalized(n) is added to the surface
				float f = amplitude / n.length();
				dpdu.scaleAdd (f * dhdu, n, dpdu);
				dpdv.scaleAdd (f * dhdv, n, dpdv);
				data.setTuple3f (Channel.DPXDU, dpdu);
				data.setTuple3f (Channel.DPXDV, dpdv);
				n.cross (dpdu, dpdv);
				data.setTuple3f (Channel.NX, n);
				return data.getValidFloatValue (channel);
			}
		}
		return data.forwardGetFloatValue (in);
	}

	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}
	
}
