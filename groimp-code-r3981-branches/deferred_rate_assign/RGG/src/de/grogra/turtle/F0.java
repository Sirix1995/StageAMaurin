
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
 * <code>F0</code>
 * represents a cylinder along the local z-direction.
 * In addition, this command translates the local coordinate system
 * along the axis of the cylinder such that the origin of the
 * children's coordinate system coincides with the center of the cylinder's top.
 * <br>
 * The diameter of the cylinder
 * is taken from the field
 * <code>localDiameter</code> of the current {@link de.grogra.turtle;TurtleState}.
 * The shader of the cylinder
 * is taken from the the current {@link de.grogra.turtle;TurtleState}.
 * The length of the axis is
 * the <code>localLength</code>
 * of the current {@link de.grogra.turtle;TurtleState}
 * <br>
 * This corresponds to the turtle command <code>F</code>
 * of the GROGRA software.
 *
 * @author Ole Kniemeyer
 */
public class F0 extends
	Shoot

{




	private static void initType ()
	{
		$TYPE.addDependency (TurtleStateAttribute.ATTRIBUTE, Attributes.LENGTH);
	}

	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new F0 ());
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
		return new F0 ();
	}

//enh:end



	@Override
	public float getLength (Object node, GraphState gs)
	{

		TurtleState state = TurtleState.getBefore (node, gs);
		return state.localLength;

	}



}
