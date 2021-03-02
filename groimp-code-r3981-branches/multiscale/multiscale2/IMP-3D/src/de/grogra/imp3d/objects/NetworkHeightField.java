
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

import de.grogra.graph.GraphState;
import de.grogra.math.Channel;
import de.grogra.math.ChannelData;
import de.grogra.math.ChannelMap;
import de.grogra.math.Graytone;

public class NetworkHeightField extends HeightField
{
	private static final int DATA_ID = GraphState.allocatePropertyId ();

	//enh:sco

	ChannelMap height = new Graytone ();
	//enh:field getter setter

	int xSize = 10;
	//enh:field getter setter

	int ySize = 10;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field height$FIELD;
	public static final Type.Field xSize$FIELD;
	public static final Type.Field ySize$FIELD;

	public static class Type extends HeightField.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (NetworkHeightField representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, HeightField.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = HeightField.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = HeightField.Type.FIELD_COUNT + 3;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((NetworkHeightField) o).xSize = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((NetworkHeightField) o).ySize = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((NetworkHeightField) o).getXSize ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((NetworkHeightField) o).getYSize ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((NetworkHeightField) o).height = (ChannelMap) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((NetworkHeightField) o).getHeight ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new NetworkHeightField ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (NetworkHeightField.class);
		height$FIELD = Type._addManagedField ($TYPE, "height", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, Type.SUPER_FIELD_COUNT + 0);
		xSize$FIELD = Type._addManagedField ($TYPE, "xSize", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 1);
		ySize$FIELD = Type._addManagedField ($TYPE, "ySize", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 2);
		$TYPE.validate ();
	}

	public int getXSize ()
	{
		return xSize;
	}

	public void setXSize (int value)
	{
		this.xSize = (int) value;
	}

	public int getYSize ()
	{
		return ySize;
	}

	public void setYSize (int value)
	{
		this.ySize = (int) value;
	}

	public ChannelMap getHeight ()
	{
		return height;
	}

	public void setHeight (ChannelMap value)
	{
		height$FIELD.setObject (this, value);
	}

//enh:end


	public NetworkHeightField ()
	{
		super ();
	}


	@Override
	public float getHeight (int u, int v, GraphState gs)
	{
		ChannelData src = null;
		Object[] a = (Object[]) gs.getUserProperty (DATA_ID);
		if (a == null)
		{
			a = new Object[2];
			a[0] = src = new ChannelData ();
			a[1] = this;
		}
		else
		{
			src = (ChannelData) a[0];
			if (a[1] != this)
			{
				a[1] = this;
				src.clear ();
			}
		}
		ChannelData sink = src.createSink (height);
		float cu = (float) u / xSize, cv = (float) v / ySize;
		src.setFloat (Channel.U, cu);
		src.setFloat (Channel.V, cv);
		src.setFloat (Channel.X, cu);
		src.setFloat (Channel.Y, cv);
		src.setFloat (Channel.Z, 0);
		return sink.getFloatValue (null, Channel.Z);
	}


	public int getUSize (GraphState gs)
	{
		return xSize;
	}


	public int getVSize (GraphState gs)
	{
		return ySize;
	}

}
