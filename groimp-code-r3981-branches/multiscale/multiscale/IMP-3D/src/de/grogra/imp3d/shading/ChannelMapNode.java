
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
import de.grogra.math.ChannelData;
import de.grogra.math.ChannelMap;
import de.grogra.math.ChannelMapVisitor;

public abstract class ChannelMapNode extends Node
	implements ChannelMap
{
	public static final int INPUT = Node.MIN_UNUSED_SPECIAL_OF_TARGET;
	public static final int COLOR = INPUT + 1;
	public static final int TRANSPARENCY = COLOR + 1;
	public static final int TRANSPARENCY_SHININESS = TRANSPARENCY + 1;
	public static final int AMBIENT = TRANSPARENCY_SHININESS + 1;
	public static final int EMISSIVE = AMBIENT + 1;
	public static final int SPECULAR = EMISSIVE + 1;
	public static final int SHININESS = SPECULAR + 1;
	public static final int DIFFUSE_TRANSPARENCY = SHININESS + 1;
	public static final int COLOR_2 = DIFFUSE_TRANSPARENCY + 1;
	public static final int DISPLACEMENT = COLOR_2 + 1;
	public static final int FIRST_OP = DISPLACEMENT + 1;
	public static final int SECOND_OP = FIRST_OP + 1;
	public static final int MIN_UNUSED_SPECIAL_OF_TARGET = SECOND_OP + 1;

	ChannelMap input = null;
	//enh:field edge=INPUT getter setter

	private static void initType ()
	{
		ChannelMap[] type = new ChannelMap[0];
		$TYPE.declareSpecialEdge (INPUT, "channel.input", type);
		$TYPE.declareSpecialEdge (COLOR, "channel.color", type);
		$TYPE.declareSpecialEdge (TRANSPARENCY, "channel.transparency", type);
		$TYPE.declareSpecialEdge (TRANSPARENCY_SHININESS, "channel.transparencyShininess", type);
		$TYPE.declareSpecialEdge (AMBIENT, "channel.ambient", type);
		$TYPE.declareSpecialEdge (EMISSIVE, "channel.emissive", type);
		$TYPE.declareSpecialEdge (SPECULAR, "channel.specular", type);
		$TYPE.declareSpecialEdge (SHININESS, "channel.shininess", type);
		$TYPE.declareSpecialEdge (DIFFUSE_TRANSPARENCY, "channel.diffuseTransparency", type);
		$TYPE.declareSpecialEdge (COLOR_2, "channel.color2", type);
		$TYPE.declareSpecialEdge (DISPLACEMENT, "channel.displacement", type);
		$TYPE.declareSpecialEdge (FIRST_OP, "channel.firstOperand", type);
		$TYPE.declareSpecialEdge (SECOND_OP, "channel.secondOperand", type);
	}

	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field input$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (ChannelMapNode.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((ChannelMapNode) o).input = (ChannelMap) value;
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
					return ((ChannelMapNode) o).getInput ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (ChannelMapNode.class);
		$TYPE.addManagedField (input$FIELD = new _Field ("input", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 0));
		$TYPE.setSpecialEdgeField (input$FIELD, INPUT);
		initType ();
		$TYPE.validate ();
	}

	public ChannelMap getInput ()
	{
		return input;
	}

	public void setInput (ChannelMap value)
	{
		input$FIELD.setObject (this, value);
	}

//enh:end


	public float getFloatValue (ChannelData data, int channel)
	{
		return data.forwardGetFloatValue (data.getData (input));
	}


	public Object getObjectValue (ChannelData data, int channel)
	{
		return data.forwardGetObjectValue (data.getData (input));
	}
	
	public void accept( ChannelMapNodeVisitor visitor )
	{
		visitor.visit(this);
	}

	public void accept(ChannelMapVisitor visitor) {
		if( visitor instanceof ChannelMapNodeVisitor )
			accept( (ChannelMapNodeVisitor) visitor );
		else
			visitor.visit(this);
	}
	
}
