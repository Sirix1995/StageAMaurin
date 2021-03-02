
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

package de.grogra.rgg.model;

import de.grogra.graph.GraphState;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Workbench;
import de.grogra.rgg.ConcurrentTask;
import de.grogra.task.PartialTask;
import de.grogra.task.SolverInOwnThread;
import de.grogra.util.ThreadContext;
import de.grogra.xl.impl.queues.Queue;
import de.grogra.xl.impl.queues.QueueCollection;
import de.grogra.xl.util.ObjectList;

public class LocalSolver extends SolverInOwnThread implements QueueSolver
{
	private final Workbench workbench;
	private GraphState state;
	private RGGGraph extent;

	private final Object initLock = new Object ();

	public LocalSolver (Workbench wb)
	{
		workbench = wb;
	}

	@Override
	protected void solveImpl (PartialTask task)
	{
		ConcurrentTask t = (ConcurrentTask) task;
		t.setGraphState (state);
		t.run ();
		t.markProcessed ();
	}

	@Override
	protected Thread createThread ()
	{
		Thread t = new Thread (this, toString ());
		t.setPriority (Thread.MIN_PRIORITY);
		return t;
	}

	public void addQueuesTo (QueueCollection qc)
	{
		synchronized (initLock)
		{
			if (extent != null)
			{
				ObjectList<Queue> list = new ObjectList<Queue> ();
				extent.getQueues ().getQueues (list);
				RGGGraph.addQueues (qc, list);
			}
		}
	}

	public void clearQueues ()
	{
		synchronized (initLock)
		{
			if (extent != null)
			{
				extent.getQueues ().clear ();
			}
		}
	}

	@Override
	public void run ()
	{
		synchronized (initLock)
		{
			Registry.setCurrent (workbench);
			Workbench.setCurrent (workbench);
			ThreadContext c = ThreadContext.current ();
			ThreadContext wc = workbench.getJobManager ().getThreadContext (); 
			c.setPriority (ThreadContext.MAX_PRIORITY);
			state = GraphState.get (workbench.getRegistry ().getProjectGraph (), wc).forContext (c);
			extent = Runtime.INSTANCE.currentGraph ();
			extent.derive ();
		}
		try
		{
			super.run ();
		}
		finally
		{
			Registry.setCurrent (null);
			Workbench.setCurrent (null);
			state.dispose ();
			state = null;
		}
	}

}
