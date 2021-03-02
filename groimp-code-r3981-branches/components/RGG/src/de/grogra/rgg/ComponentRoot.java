/*
 * Copyright (C) 2012 GroIMP Developer Team
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

 
package de.grogra.rgg;

import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;

public final class ComponentRoot extends Node {
	
	//enh:insert $TYPE.addIdentityAccessor (de.grogra.turtle.Attributes.TURTLE_MODIFIER);
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new ComponentRoot ());
		$TYPE.addIdentityAccessor (de.grogra.turtle.Attributes.TURTLE_MODIFIER);
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
		return new ComponentRoot ();
	}

//enh:end


	@Override
	public int getSymbolColor ()
	{
		return 0x00ff0000;
	}


	public static Node getRoot (GraphManager g)
	{
		Node root = g.getRootComponentGraph ();
		for (Edge edge = root.getFirstEdge (); edge != null;
			 edge = edge.getNext (root))
		{
			Node rgg;
			if (((edge.getEdgeBits () & de.grogra.graph.Graph.BRANCH_EDGE) != 0)
				&& ((rgg = edge.getTarget ()) instanceof ComponentRoot))
			{
				return rgg;
			}
		}
		return root;
	}

}
