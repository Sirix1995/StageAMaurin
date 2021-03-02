
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
 * <code>NlAdd(x)</code>
 * sets {@link de.grogra.turtle;TurtleState#localParameter} to
 * the sum of {@link de.grogra.turtle;TurtleState#parameter} and
 * the specified {@link de.grogra.turtle;Assignment#argument argument}
 * <code>x</code>.
 * <br>
 * This corresponds to the turtle command <code>Nl+(x)</code>
 * of the GROGRA software.
 *
 * @author Ole Kniemeyer
 */
public class NlAdd extends
	Assignment
{



	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new NlAdd ());
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
		return new NlAdd ();
	}

//enh:end
	

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (NlAdd.$TYPE, argument$FIELD);
		}

		public static void signature (@In @Out NlAdd n, float a)
		{
		}
	}


	public NlAdd ()
	{
		this (0);
	}


	public NlAdd (float argument)
	{
		super (argument);
	}


	public void execute (Object node, TurtleState state, GraphState gs)
	{
		state.localParameter =  (state.parameter + getArgument (node, gs));
	}
}
