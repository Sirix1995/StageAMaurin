
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
import de.grogra.xl.lang.FloatToFloat;

/**
 * A <code>VolumeFunction</code> is a {@link ChannelMap}
 * which assigns a <code>float</code>-value
 * to each point in 3D-space. The input channels are X, Y, Z; the output
 * value is placed in all output channels
 * except w-channels, which are set to 1.
 * 
 * @author Ole Kniemeyer
 */
public abstract class VolumeFunction extends ChannelMapNode
{
	FloatToFloat waveForm;
	//enh:field getter

	float frequency = 1;
	//enh:field getter setter

	float phase = 0;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field waveForm$FIELD;
	public static final NType.Field frequency$FIELD;
	public static final NType.Field phase$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (VolumeFunction.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 1:
					((VolumeFunction) o).frequency = (float) value;
					return;
				case 2:
					((VolumeFunction) o).phase = (float) value;
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
					return ((VolumeFunction) o).getFrequency ();
				case 2:
					return ((VolumeFunction) o).getPhase ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((VolumeFunction) o).waveForm = (FloatToFloat) value;
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
					return ((VolumeFunction) o).getWaveForm ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (VolumeFunction.class);
		$TYPE.addManagedField (waveForm$FIELD = new _Field ("waveForm", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (FloatToFloat.class), null, 0));
		$TYPE.addManagedField (frequency$FIELD = new _Field ("frequency", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (phase$FIELD = new _Field ("phase", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.validate ();
	}

	public float getFrequency ()
	{
		return frequency;
	}

	public void setFrequency (float value)
	{
		this.frequency = (float) value;
	}

	public float getPhase ()
	{
		return phase;
	}

	public void setPhase (float value)
	{
		this.phase = (float) value;
	}

	public FloatToFloat getWaveForm ()
	{
		return waveForm;
	}

//enh:end


	@Override
	public float getFloatValue (ChannelData data, int channel)
	{
		if ((channel & 3) == 3)
		{
			return 1;
		}
		ChannelData in = data.getData (input);
		float v = getFloatValue (in.getFloatValue (data, Channel.X),
							  in.getFloatValue (data, Channel.Y),
							  in.getFloatValue (data, Channel.Z));
		v = v * frequency + phase;
		if (waveForm != null)
		{
			v = waveForm.evaluateFloat (v);
		}
		return v;
	}


	/**
	 * Evaluates the volume function at the given point. 
	 * 
	 * @param x x-coordinate of 3D-point
	 * @param y y-coordinate of 3D-point
	 * @param z z-coordinate of 3D-point
	 * @return the function value at the 3D-point <code>(x, y, z)</code>
	 */
	protected abstract float getFloatValue (float x, float y, float z);


	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}
	
}
