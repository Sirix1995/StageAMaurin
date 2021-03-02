
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

package de.grogra.rgg;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.turtle.TurtleModifier;
import de.grogra.turtle.TurtleState;
import de.grogra.pf.ui.Workbench;

public final class RGGRoot extends Node implements TurtleModifier
{
	public void execute (Object node, TurtleState state, GraphState gs)
	{
		RGG rgg = RGG.getMainRGG (Workbench.current (gs.getContext ()));
		if (rgg != null)
		{
			rgg.initializeTurtleState (state);
		}
		state.order = 0;
	}

	//enh:insert $TYPE.addIdentityAccessor (de.grogra.turtle.Attributes.TURTLE_MODIFIER);
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new RGGRoot ());
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
		return new RGGRoot ();
	}

//enh:end


	@Override
	public int getSymbolColor ()
	{
		return 0x00ff0000;
	}


	public static Node getRoot (GraphManager g)
	{
		Node root = g.getRoot ();
		
		//check if RGGRoot node is connected to graph root. 
		//If RGGRoot node is present, return it as the graph root node.
		for (Edge edge = root.getFirstEdge (); edge != null;
			 edge = edge.getNext (root))
		{
			Node rgg;
			if (((edge.getEdgeBits () & de.grogra.graph.Graph.BRANCH_EDGE) != 0)
				&& ((rgg = edge.getTarget ()) instanceof RGGRoot))
			{
				return rgg;
			}
		}
		return root;
	}

	//multiscale begin
	/**
	 * Returns the root of the type graph for the graph
	 * @param g GraphManager (i.e. the graph) instance
	 * @return the root of the type graph for the graph
	 */
	public static Node getTypeRoot (GraphManager g)
	{
		Node root = g.getRoot();
		Node typeroot;
		
		//check if RGGRoot node is connected to graph root. 
		for (Edge edge = root.getFirstEdge (); edge != null;
				 edge = edge.getNext (root))
		{
			Node rgg;
			
			//If RGGRoot node is present, try to find type root 
			if (((edge.getEdgeBits () & de.grogra.graph.Graph.BRANCH_EDGE) != 0)
				&& ((rgg = edge.getTarget ()) instanceof RGGRoot))
			{
				//search nodes connected to RGGRoot for TypeRoot
				for (Edge edgergg = rgg.getFirstEdge (); edgergg != null;
						 edgergg = edgergg.getNext (rgg))
				{
					//if TypeRoot connected to RGGRoot is found
					if((typeroot = edgergg.getTarget ()) instanceof TypeRoot)
					{
						return typeroot;
					}
				}
			}
		}
		
		//RGGRoot node is absent, OR no TypeRoot node is connected to RGGRoot
		//search for TypeRoot connected to the root node
		for (Edge edge = root.getFirstEdge (); edge != null;
				edge = edge.getNext (root))
		{
			//If TypeRoot node is present, return it as root of type graph
			if ((typeroot = edge.getTarget ()) instanceof TypeRoot)
			{
				return typeroot;
			}
		}
		
		return null;
	}
	//multiscale end
}
