
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

package de.grogra.xl.impl.dom;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.grogra.reflect.Type;
import de.grogra.xl.impl.base.GraphImpl;
import de.grogra.xl.impl.base.GraphQueue;
import de.grogra.xl.impl.base.GraphQueueImpl;
import de.grogra.xl.impl.queues.QueueCollection;
import de.grogra.xl.impl.queues.QueueDescriptor;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.MatchConsumer;
import de.grogra.xl.query.Producer;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.util.ObjectList;

public class Graph extends GraphImpl
{
	final Document doc;

	private final ObjectList siblings = new ObjectList ();


	public Graph (RuntimeModel runtime, Document doc)
	{
		super (runtime);
		this.doc = doc;
	}


	public boolean canEnumerateEdges (EdgeDirection dir, boolean constEdge, Serializable edge)
	{
		if (dir == EdgeDirection.BOTH)
		{
			return false;
		}
		return (dir == EdgeDirection.FORWARD)
			|| !constEdge
			|| (((Integer) edge & RuntimeModel.ATTRIBUTE) == 0);
	}

	@Override
	public GraphQueue createQueue (QueueCollection qc, QueueDescriptor descr)
	{
		return new GraphQueueImpl (descr, qc)
		{
			@Override
			protected int maskConnectionEdges (int edges)
			{
				return edges & RuntimeModel.CHILD;
			}
		};
	}


	@Override
	protected void beginModifications ()
	{
		siblings.clear ();
	}


	@Override
	protected void commitModifications ()
	{
		boolean changed;
		do
		{
			changed = false;
			for (int i = siblings.size () - 2; i >= 0; i -= 2)
			{
				if (RuntimeModel.addEdgeBits ((Node) siblings.get (i), (Node) siblings.get (i + 1), RuntimeModel.SIBLING))
				{
					changed = true;
					siblings.remove (i + 1);
					siblings.remove (i);
				}
			}
		} while (changed);
	}


	public void enumerateNodes (Type type, QueryState qs, int index, MatchConsumer consumer, int arg)
	{
		ObjectList stack = qs.userStack0;
		int sp = stack.size;
		Node n = doc.getDocumentElement ();
		try 
		{
			stack.push (n);
			while (stack.size > sp)
			{
				n = (Node) stack.pop ();
				if (type.isInstance (n))
				{
					qs.amatch (index, n, consumer, arg);
				}
				for (Node c = n.getFirstChild (); c != null; c = c.getNextSibling ())
				{
					stack.push (c);
				}
			}
		}
		finally
		{
			stack.setSize (sp);
		}
	}

	public Object getRoot ()
	{
		return doc.getDocumentElement ();
	}


	@Override
	public void addNode (Object node)
	{
	}


	@Override
	public void addEdgeBits (Object source, Object target, int bits)
	{
		if (!RuntimeModel.addEdgeBits ((Node) source, (Node) target, bits))
		{
			siblings.push (source).push (target);
		}
	}


	@Override
	public void removeEdgeBits (Object source, Object target, int bits)
	{
		Node s = (Node) source;
		Node t = (Node) target;
		if ((bits & RuntimeModel.CHILD) != 0)
		{
			if (t.getParentNode () == s)
			{
				s.removeChild (t);
			}
		}
	}

	public Producer createProducer (QueryState match)
	{
		return new DOMProducer (match);
	}
}
