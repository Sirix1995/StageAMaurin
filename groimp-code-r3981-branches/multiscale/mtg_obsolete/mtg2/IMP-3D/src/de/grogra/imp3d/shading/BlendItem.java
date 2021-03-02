
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

import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.math.ChannelMap;

public class BlendItem extends Node
{
	public static final int INPUT = Node.MIN_UNUSED_SPECIAL_OF_TARGET;
	public static final int NEXT = INPUT + 1;
	public static final int MIN_UNUSED_SPECIAL_OF_TARGET = NEXT + 1;

	ChannelMap input = null;
	//enh:field edge=INPUT getter setter

	float value;
	//enh:field getter setter

	BlendItem next = null;
	//enh:field edge=NEXT getter setter

	public BlendItem ()
	{
	}

	public BlendItem (ChannelMap input, float value, BlendItem next)
	{
		this.input = input;
		this.value = value;
		this.next = next;
	}

	private static void initType ()
	{
		$TYPE.declareSpecialEdge (INPUT, "channel.input", new ChannelMap[0]);
		$TYPE.declareSpecialEdge (NEXT, "channel.blend", new BlendItem[0]);
	}

	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field input$FIELD;
	public static final NType.Field value$FIELD;
	public static final NType.Field next$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (BlendItem.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 1:
					((BlendItem) o).value = (float) value;
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
					return ((BlendItem) o).getValue ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((BlendItem) o).input = (ChannelMap) value;
					return;
				case 2:
					((BlendItem) o).next = (BlendItem) value;
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
					return ((BlendItem) o).getInput ();
				case 2:
					return ((BlendItem) o).getNext ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new BlendItem ());
		$TYPE.addManagedField (input$FIELD = new _Field ("input", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 0));
		$TYPE.addManagedField (value$FIELD = new _Field ("value", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (next$FIELD = new _Field ("next", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (BlendItem.class), null, 2));
		$TYPE.setSpecialEdgeField (input$FIELD, INPUT);
		$TYPE.setSpecialEdgeField (next$FIELD, NEXT);
		initType ();
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
		return new BlendItem ();
	}

	public float getValue ()
	{
		return value;
	}

	public void setValue (float value)
	{
		this.value = (float) value;
	}

	public ChannelMap getInput ()
	{
		return input;
	}

	public void setInput (ChannelMap value)
	{
		input$FIELD.setObject (this, value);
	}

	public BlendItem getNext ()
	{
		return next;
	}

	public void setNext (BlendItem value)
	{
		next$FIELD.setObject (this, value);
	}

//enh:end

}
