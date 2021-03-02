
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

package de.grogra.imp2d.layout;

/**
 * An <code>Edge</code> represents an edge of a graph which is to be
 * layouted by a {@link de.grogra.imp2d.layout.Layout}. Such a graph
 * is constructed as an image of an actual source {@link de.grogra.graph.Graph}.
 * 
 * @see de.grogra.imp2d.layout.Node
 * @author Ole Kniemeyer
 */
public final class Edge
{
	/**
	 * The original edge in the source <code>Graph</code>. 
	 */
	public final Object object;

	/**
	 * <code>true</code> iff this edge corresponds to a node
	 * in the source <code>Graph</code> which represents an edge.
	 */
	public final boolean edgeNode;

	/**
	 * The edge weight.
	 */
	public final float weight;

	/**
	 * The source node of this edge.
	 */
	public final Node source;

	/**
	 * The target node of this edge.
	 */
	public final Node target;

	/**
	 * The width of the 2D-visualization. May be 0.
	 */
	public float width;

	/**
	 * The height of the 2D-visualization. May be 0.
	 */
	public float height;
	
	public boolean isAccessed;
	
	private Edge sourceNext;
	private Edge targetNext;

	
	Edge (Node source, Node target, Object object, boolean edgeNode, float weight)
	{
		this.object = object;
		this.edgeNode = edgeNode;
		this.source = source;
		this.target = target;
		this.weight = weight;
	}


	/**
	 * Returns the next edge in <code>parent</code>'s list of edges
	 * 
	 * @param parent a node
	 * @return the next edge in <code>parent</code>'s list of edges
	 * @see Node
	 */
	public Edge getNext (Node parent)
	{
		return (parent == source) ? sourceNext : targetNext;
	}
	
	
	/**
	 * Returns the neighbor of <code>n</code>.
	 * 
	 * @param n one of the two incident nodes of this edge
	 * @return the other incident node
	 */
	public Node getNeighbor (Node n)
	{
		return (n == source) ? target : source;
	}


	void setNext (Edge next, Node parent)
	{
		if (parent == source)
		{
			this.sourceNext = next;
		}
		else
		{
			this.targetNext = next;
		}
	}

}
