
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

/**
 * An <code>EdgeIterator</code> is used to iterate over the edges
 * of a node. It is obtained by {@link Graph#createEdgeIterator}
 * and used in the following way:
 * <pre>
 *     for (EdgeIterator i = graph.createEdgeIterator (node); i.hasEdge ();
 *          i.moveToNext ())
 *     {
 *         // i.source contains the source node, i.target the target node,
 *         // i.edgeBits the edge bits of the current edge
 *         ...
 *     }
 * </pre>
 * 
 * @author Ole Kniemeyer
 */
public abstract class EdgeIterator
{
	/**
	 * Contains the source node of the current edge.
	 */
	public Object source;


	/**
	 * Contains the target node of the current edge.
	 */
	public Object target;


	/**
	 * Contains the edge bits of the current edge.
	 */
	public int edgeBits;

	
	/**
	 * Returns <code>true</code> if the fields {@link #source}, {@link #target}
	 * and {@link #edgeBits} contain a valid edge. Otherwise, it has been
	 * iterated over all edges of the node, and this method returns
	 * <code>false</code>. Then, further use of this
	 * edge iterator is not allowed; the iterator may be re-used by
	 * implementations of {@link Graph}.
	 * 
	 * @return <code>true</code> iff this iterator contains a valid edge
	 */
	public abstract boolean hasEdge ();
	

	/**
	 * Moves the iterator to the next edge. This operation is allowed only
	 * when {@link #hasEdge} returns <code>true</code>. If there is no next
	 * edge left to iterate, {@link #hasEdge} will return <code>false</code>
	 * afterwards.
	 */
	public abstract void moveToNext ();
	
	
	public abstract void dispose ();
}
