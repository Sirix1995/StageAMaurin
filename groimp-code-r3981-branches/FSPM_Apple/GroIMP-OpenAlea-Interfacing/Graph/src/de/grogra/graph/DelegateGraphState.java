
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

package de.grogra.graph;

import de.grogra.reflect.*;
import de.grogra.util.ThreadContext;

public abstract class DelegateGraphState extends GraphState
{
	protected GraphState state;


	protected DelegateGraphState (GraphState state, ThreadContext ctx)
	{
		super (state);
		this.state = state;
		initialize (state.getGraph (), ctx);
	}


	@Override
	public void setEdgeBits (Object edge, int bits)
	{
		throw new UnsupportedOperationException ();
	}


	@Override
	public void fireAttributeChanged
		(Object object, boolean asNode, Attribute a, FieldChain field, int[] indices)
	{
		throw new UnsupportedOperationException ();
	}


	@Override
	protected void fireEdgeChanged (Object source, Object target, Object edge)
	{
		throw new UnsupportedOperationException ();
	}


	@Override
	public GraphState createDelegate (ThreadContext tc)
	{
		return state.createDelegate (tc);
	}
	
}
