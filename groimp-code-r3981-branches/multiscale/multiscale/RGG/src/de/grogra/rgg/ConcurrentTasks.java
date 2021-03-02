
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

import java.util.TimerTask;

import de.grogra.pf.ui.Workbench;
import de.grogra.rgg.model.LocalSolver;
import de.grogra.rgg.model.QueueSolver;
import de.grogra.rgg.model.RGGGraph;
import de.grogra.task.PartialTask;
import de.grogra.task.Solver;
import de.grogra.task.Task;
import de.grogra.xl.impl.base.Graph;
import de.grogra.xl.impl.queues.QueueCollection;
import de.grogra.xl.util.ObjectList;

/**
 * This class implements a list of {@link de.grogra.rgg.ConcurrentTask}s. The
 * individual tasks are added via {@link #add(ConcurrentTask)} and executed
 * via {@link #solve()}.
 * <p>
 * It is assumed that the only effect of the individual concurrent tasks is to
 * fill the queues of their current extent
 * (see {@link Graph#getQueues()}). The queues are
 * collected and, after all tasks have been processed, are applied as last
 * action of the {@link #solve()} method.
 * 
 * @author Ole Kniemeyer
 */
public final class ConcurrentTasks extends Task
{
	private ObjectList<ConcurrentTask> newTasks = new ObjectList<ConcurrentTask> ();

	private ObjectList<ConcurrentTask> tasks = new ObjectList<ConcurrentTask> ();
	private int taskCount;

	private int localSolverCount = Runtime.getRuntime ().availableProcessors ();

	private TimerTask disposer;

	/**
	 * Invoked to remove the solvers after 5 seconds of inactivity.
	 * This in turn frees system resources (e.g., threads) needed by the
	 * solvers.
	 */
	private void rescheduleDisposer ()
	{
		if (disposer != null)
		{
			disposer.cancel ();
		}
		disposer = new TimerTask ()
		{
			@Override
			public void run ()
			{
				synchronized (ConcurrentTasks.this)
				{
					if (!isSolving ())
					{
						removeSolvers ();
					}
				}
			}
		};
		Workbench.TIMER.schedule (disposer, 5000);
	}

	/**
	 * Sets the number of local solvers (same virtual machine) to use.
	 * The default value is the number of processors which are available
	 * to the virtual machine.
	 * 
	 * @param count number of local solvers to use
	 */
	public void setLocalSolverCount (int count)
	{
		localSolverCount = count;
	}

	/**
	 * Adds a task to be solved during {@link #solve()}.
	 * 
	 * @param task single task to solve
	 */
	public void add (ConcurrentTask task)
	{
		newTasks.add (task);
	}

	@Override
	protected PartialTask nextPartialTask (int solverIndex)
	{
		synchronized (tasks)
		{
			return tasks.isEmpty () ? null : tasks.removeAt (0);
		}
	}

	@Override
	protected void dispose (PartialTask task)
	{
		if (((ConcurrentTask) task).processed)
		{
			taskCount--;
		}
		else
		{
			synchronized (tasks)
			{
				tasks.add ((ConcurrentTask) task);
			}
		}
	}

	@Override
	protected boolean done ()
	{
		return taskCount == 0;
	}

	@Override
	protected void prepareSolve ()
	{
		if (getSolverCount () == 0)
		{
			// no solvers there: create them
			for (int i = 0; i < localSolverCount; i++)
			{
				addSolver (new LocalSolver (Workbench.current ()));
			}
		}
	}

	@Override
	public void solve ()
	{
		ObjectList<ConcurrentTask> tmp = newTasks;
		newTasks = tasks;
		tasks = tmp;
		newTasks.clear ();
		taskCount = tasks.size ();
		super.solve ();
	}

	@Override
	protected void finishSolve ()
	{
		rescheduleDisposer ();
		RGGGraph g = de.grogra.rgg.model.Runtime.INSTANCE.currentGraph ();
		QueueCollection qc = g.getQueues ();
		Solver[] s = getSolvers ();
		// collect the queues
		for (int i = 0; i < s.length; i++)
		{
			((QueueSolver) s[i]).addQueuesTo (qc);
		}
		// induce queue application
		g.derive ();
		for (int i = 0; i < s.length; i++)
		{
			((QueueSolver) s[i]).clearQueues ();
		}
	}

}
