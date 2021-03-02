
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





/**
 * The turtle command
 * <code>MAdd(x)</code>
 * represents a movement along the local z-direction. The length of the movement is
 * the <code>length</code>
 * of the current {@link de.grogra.turtle.TurtleState}
 * incremented by the specified {@link #argument argument}.
 * <br>
 * This corresponds to the turtle command <code>f+(x)</code>
 * of the GROGRA software.
 *
 * @author Ole Kniemeyer
 */
public class MAdd extends
	Move

{




	private static void initType ()
	{
	}

	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new MAdd ());
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
		return new MAdd ();
	}

//enh:end


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (MAdd.$TYPE,
				   argument$FIELD
			);
		}

		public static void signature (@In @Out MAdd n, float a)
		{
		}
	}


	public MAdd ()
	{
		this (1);
	}


	public MAdd (float argument)
	{
		super (argument);
	}




	@Override
	public float getLength (Object node, GraphState gs)
	{

		TurtleState state = TurtleState.getBefore (node, gs);
		return state.length + getArgument (node, gs);

	}


	public void execute (Object node, TurtleState state, GraphState gs)
	{
		state.relPosition -= (state.length + getArgument (node, gs)) / state.length;
	}

}