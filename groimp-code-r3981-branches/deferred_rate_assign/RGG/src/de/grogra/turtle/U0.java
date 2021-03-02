
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
 * <code>U0</code>
 * resets {@link de.grogra.turtle;TurtleState#internodeCount} to its initial value
 * (as defined in {@link de.grogra.turtle;TurtleState#lsystem}).
 * Then the value of <code>internodeCount</code> is copied to
 * its local counterpart {@link de.grogra.turtle;TurtleState#localInternodeCount}.
 * <br>
 * This corresponds to the turtle command <code>U</code>
 * of the GROGRA software.
 *
 * @author Ole Kniemeyer
 */
public class U0 extends
	de.grogra.graph.impl.Node implements TurtleModifier
{


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TURTLE_MODIFIER);
	}

	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new U0 ());
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
		return new U0 ();
	}

//enh:end



	public void execute (Object node, TurtleState state, GraphState gs)
	{
		state.localInternodeCount = state.internodeCount = state.initialState.internodeCount;
	}
}
