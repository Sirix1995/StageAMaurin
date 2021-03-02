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
 * A <code>Connector</code> is used to specify a connection of a new node
 * with a graph at a position which is given by an existing node in
 * the graph. For example, the connector implementation could just redirect
 * all edges from the existing node to the new node.
 * <p>
 * A connector is invoked as follows: At first, a connecting action has to be
 * recorded in the connection queue by invocation of
 * {@link Producer#connect}. Afterwards, on queue application the connecting
 * action is executed, which leads to the invocation of the connector.
 * 
 * @author Ole Kniemeyer
 *
 * @param <N> node type
 * @param <P> parameter type
 */
public interface Connector<N,P>
{
	/**
	 * Establish the connection. This method is invoked by the connecting
	 * queue on queue application.
	 * 
	 * @param from existing node in the graph
	 * @param to new node which shall be connected with the graph
	 * @param param some parameter for the connector (the same as the one
	 * passed to {@link Producer#connect})
	 * @param queue connectding queue within which the connecting action
	 * has been recorded
	 */
	void connect (N from, N to, P param, GraphQueue queue);
}
