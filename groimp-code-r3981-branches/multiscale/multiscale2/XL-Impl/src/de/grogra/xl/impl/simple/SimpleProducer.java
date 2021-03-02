
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

import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.QueryState;

public class SimpleProducer extends de.grogra.xl.impl.base.Producer
{

	private final Graph graph;

	public SimpleProducer (QueryState match)
	{
		super (match);
		graph = (Graph) match.getGraph ();
	}

	public Node producer$getRoot ()
	{
		return (Node) graph.getRoot ();
	}

	public SimpleProducer operator$space (Node node)
	{
		addNodeImpl (node, true);
		return this;
	}

	public SimpleProducer producer$push ()
	{
		pushImpl ();
		return this;
	}

	public SimpleProducer producer$separate ()
	{
		separateImpl ();
		return this;
	}

	public SimpleProducer producer$begin ()
	{
		return this;
	}

	public void producer$end ()
	{
	}

	public SimpleProducer producer$pop (Object oldProducer)
	{
		popImpl ();
		return this;
	}

	public SimpleProducer operator$arrow (Node node, int edge)
	{
		addEdgeImpl (getPreviousNode (), node, edge, EdgeDirection.FORWARD);
		addNodeImpl (node, false);
		return this;
	}

	private boolean deriveOnEnd;

	public void applyDerivation ()
	{
		deriveOnEnd = true;
	}

	public boolean producer$beginExecution (int arrow)
	{
		deriveOnEnd = false;
		return super.producer$beginExecution (arrow);
	}

	public void producer$endExecution (boolean applied)
	{
		super.producer$endExecution (applied);
		if (!applied)
		{
			return;
		}
		if (deriveOnEnd)
		{
			graph.derive ();
		}
	}

}
