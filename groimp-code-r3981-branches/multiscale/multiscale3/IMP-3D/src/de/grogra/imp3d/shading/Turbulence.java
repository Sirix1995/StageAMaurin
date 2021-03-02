
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

import javax.vecmath.*;
import de.grogra.math.*;
import de.grogra.vecmath.*;

public class Turbulence extends ChannelMapNode
{
	final Vector3f amount = new Vector3f (0.2f, 0.2f, 0.2f);
	//enh:field type=Tuple3fType.VECTOR set=set getter setter

	int octaves = 5;
	//enh:field getter setter

	float noiseRatio = 2f;
	//enh:field getter setter

	float frequencyRatio = 2f;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field amount$FIELD;
	public static final NType.Field octaves$FIELD;
	public static final NType.Field noiseRatio$FIELD;
	public static final NType.Field frequencyRatio$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Turbulence.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 1:
					((Turbulence) o).octaves = (int) value;
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
					return ((Turbulence) o).getOctaves ();
			}
			return super.getInt (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 2:
					((Turbulence) o).noiseRatio = (float) value;
					return;
				case 3:
					((Turbulence) o).frequencyRatio = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 2:
					return ((Turbulence) o).getNoiseRatio ();
				case 3:
					return ((Turbulence) o).getFrequencyRatio ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Turbulence) o).amount.set ((Vector3f) value);
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
					return ((Turbulence) o).getAmount ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Turbulence ());
		$TYPE.addManagedField (amount$FIELD = new _Field ("amount", _Field.FINAL  | _Field.SCO, Tuple3fType.VECTOR, null, 0));
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
		return new Turbulence ();
	}

	public int getOctaves ()
	{
		return octaves;
	}

	public void setOctaves (int value)
	{
		this.octaves = (int) value;
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

	public Vector3f getAmount ()
	{
		return amount;
	}

	public void setAmount (Vector3f value)
	{
		amount$FIELD.setObject (this, value);
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
			case Channel.U:
			case Channel.V:
			case Channel.W:
				Point3f p = data.p3f0, q = data.p3f1;
				in.getTuple3f (p, data, channel & ~3);
				Math2.dTurbulence (q, p.x, p.y, p.z, Math.min (10, octaves),
								   frequencyRatio, noiseRatio);
				p.x += q.x * amount.x;
				p.y += q.y * amount.y;
				p.z += q.z * amount.z;
				data.setTuple3f (channel & ~3, p);
				return data.getValidFloatValue (channel);
			default:
				return data.forwardGetFloatValue (in);
		}
	}

	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}
	
	
}
