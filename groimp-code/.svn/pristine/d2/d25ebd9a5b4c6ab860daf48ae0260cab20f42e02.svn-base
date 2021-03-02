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

package de.grogra.rgg;

import java.io.Serializable;

import de.grogra.graph.GraphState;
import de.grogra.task.PartialTask;
import de.grogra.xl.impl.base.Graph;

/**
 * This abstract class has to be used as base class for concurrent
 * tasks in the context of an RGG. Such a task must not perform any
 * immediate modification to the graph, however, it is allowed to
 * add quasi-parallel actions to one of the queues of the current
 * extent (see {@link Graph#getQueues()}). This
 * includes XL rule application and quasi-parallel assignments to
 * XL properties.
 * <p>
 * The task is implemented by the <code>run</code> method of the
 * superinterface <code>Runnable</code> as in the following example:
 * <pre>
 *     ConcurrentTask task = new ConcurrentTask ()
 *     {
 *         public void run ()
 *         [
 *             X ==> Y;
 *             a:A ::> a[value] :+= 1;
 *         ]
 *     };
 * </pre>
 * <p>
 * A concurrent task is added to an instance of
 * {@link de.grogra.rgg.ConcurrentTasks}, then the invocation
 * of <code>solve</code> on this instance will execute
 * all added concurrent tasks.
 * 
 * @author Ole Kniemeyer
 */
public abstract class ConcurrentTask implements PartialTask, Runnable,
		Serializable
{
	private transient GraphState state;
	transient boolean processed;

	public void markProcessed ()
	{
		processed = true;
	}

	public void setGraphState (GraphState state)
	{
		this.state = state;
	}

	public GraphState getGraphState ()
	{
		return state;
	}

}
