
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

package de.grogra.graph.impl;

final class EdgeImpl extends Edge
{
	final Node source, target;


	EdgeImpl (Node source, Node target)
	{
		super ();
		this.source = source;
		this.target = target;
	}


	@Override
	public Edge getNext (Node parent)
	{
		return parent == target ? targetNext : next;
	}


	@Override
	public Node getSource ()
	{
		return source;
	}


	@Override
	public Node getTarget ()
	{
		return target;
	}


	@Override
	public boolean isSource (Node node)
	{
		return node == source;
	}


	@Override
	public boolean isTarget (Node node)
	{
		return node == target;
	}


	@Override
	public Node getNeighbor (Node start)
	{
		return (start == target) ? source : target;
	}


	@Override
	public boolean isDirection (Node source, Node target)
	{
		return (source == this.source) || (target == this.target);
	}


	@Override
	GraphManager getGraph ()
	{
		return (source != null) ? source.getGraph () : null;
	}

}
