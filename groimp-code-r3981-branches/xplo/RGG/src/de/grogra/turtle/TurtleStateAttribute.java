
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

import de.grogra.graph.Attribute;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.ObjectTreeAttribute;

public final class TurtleStateAttribute extends ObjectTreeAttribute<TurtleState>
{
	public static final TurtleStateAttribute ATTRIBUTE
		= (TurtleStateAttribute) new TurtleStateAttribute ()
		.initializeName ("de.grogra.turtle.turtleState");


	private TurtleStateAttribute ()
	{
		super (TurtleState.class, false, null);
	}


	@Override
	protected TurtleState derive (Object object, boolean asNode, TurtleState parentValue,
			TurtleState placeIn, GraphState gs)
	{
		TurtleModifier tc = (TurtleModifier) gs.getObjectDefault 
			(object, asNode, Attributes.TURTLE_MODIFIER, null);
		boolean branch = !asNode
			&& (object != null) && ((gs.getGraph ().getEdgeBits (object) & Graph.BRANCH_EDGE) != 0);
		if (branch || (tc != null))
		{
			if (placeIn == null)
			{
				placeIn = new TurtleState ();
			}
			placeIn.inherit (parentValue);
			if (tc != null)
			{
				tc.execute (object, placeIn, gs);
			}
			if (branch)
			{
				placeIn.order++;
			}
			return placeIn;
		}
		return parentValue;
	}


	@Override
	public boolean dependsOn (Attribute[] a)
	{
		return Attributes.TURTLE_MODIFIER.isContained (a);
	}


	@Override
	protected TurtleState getInitialValue (GraphState gs)
	{
		return TurtleState.DEFAULT_TURTLE_STATE;
	}

}
