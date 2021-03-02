
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
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

package de.grogra.xl.impl.simple;

import java.io.Serializable;

import de.grogra.reflect.Type;
import de.grogra.xl.impl.base.GraphImpl;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.HasModel;
import de.grogra.xl.query.MatchConsumer;
import de.grogra.xl.query.Producer;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.util.ObjectList;

@HasModel(CompiletimeModel.class)
public class Graph extends GraphImpl
{
	final boolean canSupplyBackward;

	
	final ObjectList<Node> nodes = new ObjectList<Node> ();


	public Graph (RuntimeModel runtime, boolean canSupplyBackward)
	{
		super (runtime);
		this.canSupplyBackward = canSupplyBackward;
	}

	public int size ()
	{
		return nodes.size ();
	}
	

	public boolean canEnumerateEdges (EdgeDirection dir, boolean constEdge, Serializable edge)
	{
		if (dir == EdgeDirection.BOTH)
		{
			return false;
		}
		return canSupplyBackward || dir.contains (EdgeDirection.FORWARD);
	}


	@Override
	protected void beginModifications ()
	{
	}


	@Override
	protected void commitModifications ()
	{
	}

	public void enumerateNodes (Type type, QueryState qs, int index, MatchConsumer consumer, int arg)
	{
		for (int i = nodes.size () - 1; i >= 0; i--)
		{
			Node n = nodes.get (i);
			if (type.isInstance (n))
			{
				qs.amatch (index, n, consumer, arg);
			}
		}
	}

	public Object getRoot ()
	{
		return nodes.get (0);
	}


	@Override
	public void addNode (Object node)
	{
		if (((Node) node).index >= 0)
		{
			return;
		}
		((Node) node).index = nodes.size;
		nodes.add ((Node) node);
	}

	@Override
	public void removeNode (Object node)
	{
		super.removeNode (node);
		int i = ((Node) node).index;
		if (i >= 0)
		{
			((Node) node).index = -1;
			int s = --nodes.size;
			if (i < s)
			{
				Node n = (Node) nodes.elements[s];
				nodes.elements[i] = n;
				n.index = i;
			}
			nodes.elements[s] = null;
		}
	}

	@Override
	public void addEdgeBits (Object source, Object target, int bits)
	{
		((Node) source).addEdgeBitsTo ((Node) target, bits);
	}


	@Override
	public void removeEdgeBits (Object source, Object target, int bits)
	{
		((Node) source).removeEdgeBitsTo ((Node) target, bits);
	}

	public Producer createProducer (QueryState match)
	{
		return new SimpleProducer (match);
	}
}
