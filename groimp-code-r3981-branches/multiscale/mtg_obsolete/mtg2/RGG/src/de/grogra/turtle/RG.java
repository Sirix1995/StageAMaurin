
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

import de.grogra.graph.GraphState;

/**
 * The turtle command <code>RG(x)</code>
 * represents a rotation which implements maximal gravitropism.
 * The effect is that the local z-direction after the
 * rotation points strictly downwards, i.e., corresponds the the
 * negative global z-direction. This corresponds to the turtle
 * command <code>RG</code> of the GROGRA software.
 *
 * @author Ole Kniemeyer
 */
public class RG extends GRotation
{

	/**
	 * Creates a new <code>RG</code> node. This leads to
	 * an orientation which is strictly downwards. 
	 */
	public RG ()
	{
	}


	@Override
	public float getTropismStrength (Object node, GraphState gs)
	{
		return 1e10f;
	}


	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new RG ());
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
		return new RG ();
	}

//enh:end

}
