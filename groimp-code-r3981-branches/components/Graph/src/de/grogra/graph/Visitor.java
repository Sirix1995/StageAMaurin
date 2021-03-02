
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
 * A <code>Visitor</code> is a callback-instance which is informed
 * about the structure of a graph via invocation of
 * {@link de.grogra.graph.Graph#accept(Object, Visitor, ArrayPath)}.
 * It follows the design pattern of hierarchical visitors: For every node
 * and edge of the graph, the corresponding methods <code>visitEnter</code>
 * and <code>visitLeave</code> are invoked. Invocations nested within
 * such an invocation pair indicate the hierarchy. In any case,
 * the invocation pairs are nested correctly. The <code>path</code>
 * argument to the methods of this interface contain the path
 * from the root to the current location, this path corresponds to the
 * invocations of <code>visitEnter</code>-methods which have not yet
 * been closed by invocations of <code>visitLeave</code>.
 * 
 * @author Ole Kniemeyer
 */
public interface Visitor
{
	/**
	 * Return value for {@link #visitEnter} and
	 * {@link #visitInstanceEnter} to indicate that the hierarchical
	 * traversal of the graph must not go more deeply in the hierarchy
	 * at the current location.
	 */
	Object STOP = new Object ();

	/**
	 * Returns the graph state within which the visitor is operating. 
	 * 
	 * @return graph state of visitor
	 */
	GraphState getGraphState ();

	/**
	 * Informs this visitor about a node or edge in the graph.
	 * <code>path</code> contains the path from the root up to and including
	 * the node or, in case an edge is visited, the edge and its terminal node. Later on,
	 * {@link #visitLeave} will be invoked with
	 * the returned value of this invocation, <code>path</code>
	 * and <code>node</code> as arguments. If this method returns
	 * {@link #STOP}, the corresponding <code>visitLeave</code>-method
	 * will be invoked immediately after this method, i.e., the visitor
	 * does not dive more deeply in the hierarchy. Otherwise, if a
	 * node is visited and there
	 * are instantiations associated with or edges connected with
	 * it (regardless of their  direction), their corresponding visitor methods will be invoked
	 * inbetween. Likewise, if an edge is visited, its indicent terminal node
	 * will be visited next.
	 * 
	 * @param path current path
	 * @param node do we enter a node or an edge?
	 * @return value to pass to <code>visitLeave</code>, may be {@link #STOP}
	 */
	Object visitEnter (Path path, boolean node);

	/**
	 * Informs this visitor that a node or an edge has been processed
	 * completely. The value <code>o</code> is the return value
	 * of the corresponding invocation of {@link #visitEnter}.
	 * In case of a node and if this invocation returns <code>false</code>, no further
	 * edges of the current level of hierarchy will be passed to this
	 * visitor, i.e., the <code>visitLeave</code> methods for the enclosing
	 * edge and node will be invoked immediately.
	 * Likewise, in case of an edge and a return value <code>false</code>, no further
	 * instantiations or edges of the current level of hierarchy
	 * will be passed to this
	 * visitor, i.e., the <code>visitLeave</code> method for the enclosing
	 * node will be invoked immediately.
	 * 
	 * @param o returned value of <code>visitEnter</code>
	 * @param path current path to <code>node</code>
	 * @param node do we leave a node or an edge?
	 * @return <code>true</code> iff processing of current level shall be continued
	 */
	boolean visitLeave (Object o, Path path, boolean node);


	/**
	 * Informs this visitor about the beginning of an instantiation.
	 * This method invokation is nested immediately within
	 * <code>visitEnter</code>/<code>visitLeave</code> of a node. Later on,
	 * {@link #visitInstanceLeave(Object)} will be invoked with
	 * the returned value of this invocation as argument. If this method returns
	 * {@link #STOP}, the <code>visitInstanceLeave</code>-method
	 * will be invoked immediately after this method, i.e., the visitor
	 * does not dive into the instantiation. Otherwise, the instantiation
	 * is performed, starting with an edge.
	 * 
	 * @return value to pass to <code>visitInstanceLeave</code>, may be {@link #STOP}
	 */
	Object visitInstanceEnter ();

	/** 
	 * Informs this visitor that an instantiation has been processed
	 * completely. The value <code>o</code> is the return value
	 * of the corresponding invocation of {@link #visitInstanceEnter()}.
	 * If this invocation returns <code>false</code>, no further
	 * instantiations or edges of the current level of hierarchy will
	 * be passed to this
	 * visitor, i.e., the <code>visitLeave</code> method for the enclosing
	 * node will be invoked immediately.
	 * 
	 * @param o returned value of <code>visitInstanceEnter</code>
	 * @return <code>true</code> iff processing of current level shall be continued
	 */
	boolean visitInstanceLeave (Object o);
}
