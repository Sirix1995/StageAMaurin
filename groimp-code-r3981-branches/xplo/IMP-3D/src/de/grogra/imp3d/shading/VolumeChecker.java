
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
import de.grogra.math.ColorMap;
import de.grogra.math.Graytone;
import de.grogra.vecmath.Math2;

public class VolumeChecker extends ChannelMapNode implements ColorMap
{
	protected static final ColorMap DEFAULT_COLOR_1 = new Graytone (1);
	protected static final ColorMap DEFAULT_COLOR_2 = new Graytone (0);

	ChannelMap color1 = null;
	//enh:field edge=COLOR

	ChannelMap color2 = null;
	//enh:field edge=COLOR_2

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field color1$FIELD;
	public static final NType.Field color2$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (VolumeChecker.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((VolumeChecker) o).color1 = (ChannelMap) value;
					return;
				case 1:
					((VolumeChecker) o).color2 = (ChannelMap) value;
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
					return ((VolumeChecker) o).color1;
				case 1:
					return ((VolumeChecker) o).color2;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new VolumeChecker ());
		$TYPE.addManagedField (color1$FIELD = new _Field ("color1", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 0));
		$TYPE.addManagedField (color2$FIELD = new _Field ("color2", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 1));
		$TYPE.setSpecialEdgeField (color1$FIELD, COLOR);
		$TYPE.setSpecialEdgeField (color2$FIELD, COLOR_2);
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
		return new VolumeChecker ();
	}

//enh:end


	public int getAverageColor ()
	{
		int c1 = ((color1 instanceof ColorMap) ? (ColorMap) color1 : DEFAULT_COLOR_1).getAverageColor ();
		int c2 = ((color2 instanceof ColorMap) ? (ColorMap) color2 : DEFAULT_COLOR_2).getAverageColor ();
		return ((c1 & 0xfffefefe) + (c2 & 0xfffefefe)) >> 1;
	}


	@Override
	public float getFloatValue (ChannelData data, int channel)
	{
		ChannelData in = data.getData (input);
		boolean b = ((Math2.floor (in.getFloatValue (data, Channel.X))
					  ^ Math2.floor (in.getFloatValue (data, Channel.Y))
					  ^ Math2.floor (in.getFloatValue (data, Channel.Z))) & 1)
			== 0;
		return data.getData
			(b ? ((color2 != null) ? color2 : DEFAULT_COLOR_2)
			 : ((color1 != null) ? color1 : DEFAULT_COLOR_1))
			.getFloatValue (data, channel);
	}


	public ChannelMap getColor1 ()
	{
		return color1;
	}


	public ChannelMap getColor2 ()
	{
		return color2;
	}

	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}
	
	
}
