
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

/**
 * This interface is implemented by attribute values and other objects whose
 * semantics may depend on the <em>object context</em> of the
 * current {@link de.grogra.graph.GraphState}. It is used as part of
 * a caching mechanism (see {@link de.grogra.graph.Cache}) in order to
 * determine if the context has changed or not.
 * <p>
 * As an example, consider
 * a shape attribute of a node in a graph: It could be a polyline
 * starting at this node (which constitutes the object context) and
 * connecting all node positions downwards to the root. Thus, the semantics
 * (a polyline connecting node positions) depends on the context
 * (the node to start with and its ancestors in the graph).
 * 
 * @author Ole Kniemeyer
 */
public interface ContextDependent
{
	/**
	 * Determines whether this object actually depends on context.
	 * 
	 * @return <code>true</code> iff this object depends on context
	 */
	boolean dependsOnContext ();


	/**
	 * Writes a stamp of the context into <code>cache</code>. Based on
	 * this stamp, a cache detects whether the context of this
	 * object has changed or not.
	 * 
	 * @param cache a cache entry to write the stamp
	 * @param gs the current graph state
	 */
	void writeStamp (Cache.Entry cache, GraphState gs);
}
