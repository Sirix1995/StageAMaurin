
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

package de.grogra.xl.impl.base;

import de.grogra.xl.impl.queues.QueueCollection;
import de.grogra.xl.impl.queues.QueueDescriptor;
import de.grogra.xl.query.EdgeDirection;

public abstract class GraphImpl extends Graph
{

	public GraphImpl (RuntimeModel model)
	{
		super (model);
	}


	@Override
	public GraphQueue createQueue (QueueCollection qc, QueueDescriptor descr)
	{
		return new GraphQueueImpl (descr, qc);
	}


	/**
	 * Adds a node to this graph extent. This method is invoked by the
	 * XL runtime system before invocations of {@link #addEdgeBits}.
	 * Implementations may perform tasks in preparation of the addition
	 * of edges, if necessary.
	 * 
	 * @param node the node for which edges will be added later on 
	 */
	public abstract void addNode (Object node);


	/**
	 * Adds a set of edge bits from a <code>source</code> node
	 * to a <code>target</code> node. This method is invoked
	 * within the right hand sides of XL productions <code>==&gt;&gt;</code>,
	 * <code>==&gt;</code>.
	 * 
	 * @param source the source node of the edge
	 * @param target the target node of the edge
	 * @param bits the edge bits that are to be added 
	 */
	public abstract void addEdgeBits (Object source, Object target, int bits);


	/**
	 * Removes a set of edge bits from a <code>source</code> node
	 * to a <code>target</code> node. This method is invoked
	 * within the right hand sides of XL productions <code>==&gt;&gt;</code>,
	 * <code>==&gt;</code>.
	 * 
	 * @param source the source node of the edge
	 * @param target the target node of the edge
	 * @param bits the edge bits that are to be removed 
	 */
	public abstract void removeEdgeBits (Object source, Object target, int bits);

	
	public void removeNode (Object node)
	{
		EdgeIterator i = model.createEdgeIterator (node, EdgeDirection.UNDIRECTED);
		while (i.hasEdge ())
		{
			Object s = i.source, t = i.target;
			i.moveToNext ();
			removeEdgeBits (s, t, -1);
		}
	}


	public Object createInterpretiveMark ()
	{
		throw new UnsupportedOperationException ();
	}
}
