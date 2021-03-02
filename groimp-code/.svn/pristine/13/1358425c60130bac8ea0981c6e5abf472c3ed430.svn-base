
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
 * <code>N(x)</code>
 * sets {@link de.grogra.turtle.TurtleState#parameter} to
 * the specified {@link de.grogra.turtle.Assignment#argument argument}
 * <code>x</code>.
 * Then the value of <code>parameter</code> is copied to
 * its local counterpart {@link de.grogra.turtle.TurtleState#localParameter}.
 * <br>
 * This corresponds to the turtle command <code>N(x)</code>
 * of the GROGRA software.
 *
 * @author Ole Kniemeyer
 */
public class N extends
	Assignment
{



	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new N ());
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
		return new N ();
	}

//enh:end
	

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (N.$TYPE, argument$FIELD);
		}

		public static void signature (@In @Out N n, float a)
		{
		}
	}


	public N ()
	{
		this (0);
	}


	public N (float argument)
	{
		super (argument);
	}


	public void execute (Object node, TurtleState state, GraphState gs)
	{
		state.localParameter = state.parameter =  (getArgument (node, gs));
	}
}
