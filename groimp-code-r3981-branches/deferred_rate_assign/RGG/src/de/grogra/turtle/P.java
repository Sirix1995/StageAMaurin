
// NOTE: This file was generated automatically.

// ********************************************
// *               DO NOT EDIT!               *
// ********************************************



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

package de.grogra.turtle;

import de.grogra.graph.*;

import de.grogra.imp3d.shading.*;

/**
 * The turtle command
 * <code>P(x)</code>
 * sets {@link de.grogra.turtle;TurtleState#color} to
 * the specified {@link de.grogra.turtle;Assignment#argument argument}
 * <code>x</code>.
 * Then the value of <code>color</code> is copied to
 * its local counterpart {@link de.grogra.turtle;TurtleState#localColor}.
 * <br>
 * This corresponds to the turtle command <code>P(x)</code>
 * of the GROGRA software.
 * <br>
 * There exists an extended command
 * <code>P(s)</code> where <code>s</code> is a <code>Shader</code>.
 * This sets the field {@link #shader}, which is
 * in turn used to set
 * {@link de.grogra.turtle;TurtleState#shader} and
 * {@link de.grogra.turtle;TurtleState#localShader}.
 *
 * @author Ole Kniemeyer
 */
public class P extends
	Assignment
{
	public Shader shader = null;
	//enh:field attr=Attributes.SHADER getter setter


	public void setShaders (Shader front, Shader back)
	{
		setShader (new SideSwitchShader (front, back));
	}
	
	
	public P (Shader shader)
	{
		super (-1);
		setShader (shader);
	}


	public P (Shader front, Shader back)
	{
		super (-1);
		setShaders (front, back);
	}

	public P (int argument)
	{
		super ((argument == (argument & 15)) ? argument : -1);
		shader = (argument == (argument & 15)) ? RGBAShader.forEGAColor (argument)
			: new RGBAShader (argument | 0xff000000);
	}



	private static void initType ()
	{
		$TYPE.addDependency (Attributes.SHADER, Attributes.TURTLE_MODIFIER);
	}

	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field shader$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (P.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((P) o).shader = (Shader) value;
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
					return ((P) o).getShader ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new P ());
		$TYPE.addManagedField (shader$FIELD = new _Field ("shader", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Shader.class), null, 0));
		$TYPE.declareFieldAttribute (shader$FIELD, Attributes.SHADER);
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
		return new P ();
	}

	public Shader getShader ()
	{
		return shader;
	}

	public void setShader (Shader value)
	{
		shader$FIELD.setObject (this, value);
	}

//enh:end


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (P.$TYPE, argument$FIELD);
		}

		public static void signature (@In @Out P n, float a)
		{
		}
	}


	public P ()
	{
		this (0);
	}


	public P (float argument)
	{
		super (argument);
	}


	public void execute (Object node, TurtleState state, GraphState gs)
	{
		state.localColor = state.color = Math.round (getArgument (node, gs));
		state.localShader = state.shader = shader;
	}
}
