
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

package de.grogra.pf.ui.registry;

import javax.swing.JSplitPane;
import de.grogra.pf.registry.*;

public class Split extends Item
{
	int orientation;
	//enh:field getter

	float location;
	//enh:field getter

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field orientation$FIELD;
	public static final NType.Field location$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Split.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 0:
					((Split) o).orientation = (int) value;
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
					return ((Split) o).getOrientation ();
			}
			return super.getInt (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 1:
					((Split) o).location = (float) value;
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
					return ((Split) o).getLocation ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Split ());
		$TYPE.addManagedField (orientation$FIELD = new _Field ("orientation", 0 | _Field.SCO, de.grogra.reflect.Type.INT, null, 0));
		$TYPE.addManagedField (location$FIELD = new _Field ("location", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
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
		return new Split ();
	}

	public int getOrientation ()
	{
		return orientation;
	}

	public float getLocation ()
	{
		return location;
	}

//enh:end

	private Split ()
	{
		this (null, true, -1);
	}


	public Split (String key, boolean horizontal, float location)
	{
		super (key);
		this.orientation = horizontal ? JSplitPane.HORIZONTAL_SPLIT
			: JSplitPane.VERTICAL_SPLIT;
		this.location = location;
	}


	public Split (String key, int orientation, float location)
	{
		super (key);
		this.orientation = orientation;
		this.location = location;
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws org.xml.sax.SAXException
	{
		if ("".equals (uri) && "orientation".equals (name))
		{
			orientation = ("vertical".equals (value) || "0".equals (value))
				? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT;
			return true;
		}
		return super.readAttribute (uri, name, value);
	}

}
