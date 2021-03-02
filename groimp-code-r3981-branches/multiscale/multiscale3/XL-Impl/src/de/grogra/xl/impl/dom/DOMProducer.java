
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

import org.w3c.dom.Node;

import de.grogra.xl.impl.base.Producer;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.QueryState;

public class DOMProducer extends Producer
{

	private final Graph graph;

	public DOMProducer (QueryState match)
	{
		super (match);
		graph = (Graph) match.getGraph ();
	}


	public Node producer$getRoot ()
	{
		return (Node) graph.getRoot ();
	}

	public DOMProducer operator$space (Node node)
	{
		addNodeImpl (node, true);
		return this;
	}

	public DOMProducer producer$push ()
	{
		pushImpl ();
		return this;
	}

	public DOMProducer producer$separate ()
	{
		separateImpl ();
		return this;
	}

	public DOMProducer producer$begin ()
	{
		return this;
	}

	public void producer$end ()
	{
	}

	public DOMProducer producer$pop (Object oldProducer)
	{
		popImpl ();
		return this;
	}

	public DOMProducer operator$arrow (Node node, int edge)
	{
		addEdgeImpl (getPreviousNode (), node, edge, EdgeDirection.FORWARD);
		addNodeImpl (node, false);
		return this;
	}

	public DOMProducer operator$leftArrow (Node node, int edge)
	{
		addEdgeImpl (getPreviousNode (), node, edge, EdgeDirection.BACKWARD);
		addNodeImpl (node, false);
		return this;
	}

	public DOMProducer operator$sub (Node node, int edge)
	{
		addEdgeImpl (getPreviousNode (), node, edge, EdgeDirection.UNDIRECTED);
		addNodeImpl (node, false);
		return this;
	}


	public DOMProducer operator$gt (Node node, int edge)
	{
		addEdgeImpl (getPreviousNode (), node, RuntimeModel.SUCCESSOR_EDGE, EdgeDirection.FORWARD);
		addNodeImpl (node, false);
		return this;
	}

	public DOMProducer operator$lt (Node node, int edge)
	{
		addEdgeImpl (getPreviousNode (), node, RuntimeModel.SUCCESSOR_EDGE, EdgeDirection.BACKWARD);
		addNodeImpl (node, false);
		return this;
	}

	public DOMProducer operator$plusArrow (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, RuntimeModel.BRANCH_EDGE, EdgeDirection.FORWARD);
		addNodeImpl (node, false);
		return this;
	}


	public DOMProducer operator$plusLeftArrow (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, RuntimeModel.BRANCH_EDGE, EdgeDirection.BACKWARD);
		addNodeImpl (node, false);
		return this;
	}


	public DOMProducer operator$and (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, RuntimeModel.SIBLING, EdgeDirection.FORWARD);
		addNodeImpl (node, false);
		return this;
	}


	public Node toNode (String value)
	{
		return graph.doc.createTextNode (value);
	}

}
