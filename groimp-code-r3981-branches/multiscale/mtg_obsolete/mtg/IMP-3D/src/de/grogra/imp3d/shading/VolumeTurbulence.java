
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

import de.grogra.vecmath.Math2;

public class VolumeTurbulence extends VolumeFunction
{
	float size = 1;
	//enh:field getter setter

	int octaves = 5;
	//enh:field getter setter

	float noiseRatio = 1f;
	//enh:field getter setter

	float frequencyRatio = 2f;
	//enh:field getter setter


	public static VolumeTurbulence createWrinkles ()
	{
		VolumeTurbulence v = new VolumeTurbulence ();
		v.frequency = 1;
		v.size = 1;
		v.octaves = 11;
		v.frequencyRatio = 2;
		v.noiseRatio = 0.5f;
		return v;
	}

	@Override
	protected float getFloatValue (float x, float y, float z)
	{
		int oct = Math.max (1, Math.min (octaves, 11));
		float a = 1;
		if (Math.abs (noiseRatio) > 1)
		{
			a /= Math.pow (Math.abs (noiseRatio), oct - 1);
		}
		return a * de.grogra.vecmath.Math2.turbulence
			(x * size, y * size, z * size, oct, frequencyRatio, noiseRatio);
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field size$FIELD;
	public static final NType.Field octaves$FIELD;
	public static final NType.Field noiseRatio$FIELD;
	public static final NType.Field frequencyRatio$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (VolumeTurbulence.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 1:
					((VolumeTurbulence) o).octaves = (int) value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 1:
					return ((VolumeTurbulence) o).getOctaves ();
			}
			return super.getInt (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((VolumeTurbulence) o).size = (float) value;
					return;
				case 2:
					((VolumeTurbulence) o).noiseRatio = (float) value;
					return;
				case 3:
					((VolumeTurbulence) o).frequencyRatio = (float) value;
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
					return ((VolumeTurbulence) o).getSize ();
				case 2:
					return ((VolumeTurbulence) o).getNoiseRatio ();
				case 3:
					return ((VolumeTurbulence) o).getFrequencyRatio ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new VolumeTurbulence ());
		$TYPE.addManagedField (size$FIELD = new _Field ("size", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (octaves$FIELD = new _Field ("octaves", 0 | _Field.SCO, de.grogra.reflect.Type.INT, null, 1));
		$TYPE.addManagedField (noiseRatio$FIELD = new _Field ("noiseRatio", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (frequencyRatio$FIELD = new _Field ("frequencyRatio", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 3));
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
		return new VolumeTurbulence ();
	}

	public int getOctaves ()
	{
		return octaves;
	}

	public void setOctaves (int value)
	{
		this.octaves = (int) value;
	}

	public float getSize ()
	{
		return size;
	}

	public void setSize (float value)
	{
		this.size = (float) value;
	}

	public float getNoiseRatio ()
	{
		return noiseRatio;
	}

	public void setNoiseRatio (float value)
	{
		this.noiseRatio = (float) value;
	}

	public float getFrequencyRatio ()
	{
		return frequencyRatio;
	}

	public void setFrequencyRatio (float value)
	{
		this.frequencyRatio = (float) value;
	}

	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}
	
	
//enh:end

}
