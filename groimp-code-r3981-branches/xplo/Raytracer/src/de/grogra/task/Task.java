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

package de.grogra.task;

import java.util.ArrayList;

/**
 * A <code>Task</code> is used for a complex problem which can be decomposed
 * in several independent {@link de.grogra.task.PartialTask}s. These
 * partial tasks are solved synchronously or asynchronously by a set of
 * {@link de.grogra.task.Solver}s which may work in different threads or even
 * on remote computers.
 * 
 * @author Ole Kniemeyer
 */
public abstract class Task
{
	/**
	 * Set by {@link #stop()} to indicate that the task should be stopped.
	 */
	private boolean stopped = false;

	private boolean solving = false;

	/**
	 * The list of registered solvers.
	 */
	private final ArrayList<Solver> solvers = new ArrayList<Solver> ();

	/**
	 * The list of currently idle solvers.
	 */
	private final ArrayList<Solver> idleSolvers = new ArrayList<Solver> ();

	/**
	 * This method is invoked in order to stop the computation
	 * of this task.
	 */
	public synchronized void stop ()
	{
		stopped = true;
		notifyAll ();
	}

	/**
	 * This method returns <code>true</code> iff the method
	 * {@link #stop()} has been invoked on this task.
	 * 
	 * @return has this task been stopped?
	 */
	public synchronized boolean isStopped ()
	{
		return stopped;
	}

	/**
	 * Adds a solver to this task.
	 * 
	 * @param s a solver
	 */
	public synchronized void addSolver (Solver s)
	{
		s.initialize (this);
		solvers.add (s);
		idleSolvers.add (s);
		notifyAll ();
	}

	/**
	 * Removes a solver from this task. The method {@link Solver#dispose()}
	 * is invoked on <code>s</code>.
	 * 
	 * @param s a solver
	 */
	public void removeSolver (Solver s)
	{
		synchronized (this)
		{
			solvers.remove (s);
			idleSolvers.remove (s);
			PartialTask t = s.clearCurrentPartialTask ();
			if (t != null)
			{
				dispose (t);
			}
			notifyAll ();
		}
		s.dispose ();
	}

	/**
	 * Returns the number of solvers for this task.
	 * 
	 * @return number of solvers
	 */
	public synchronized int getSolverCount ()
	{
		return solvers.size ();
	}

	/**
	 * This method returns the next partial task for this task.
	 * If all parts of the task have been solved or currently are being
	 * solved, <code>null</code> is returned. However, a later invocation
	 * may return a partial task if a currently active solver
	 * does not completely solve its partial task. 
	 * 
	 * @param solverIndex index of solver which will be used for next task
	 * @return next partial task, or <code>null</code>
	 */
	protected abstract PartialTask nextPartialTask (int solverIndex);

	/**
	 * This method is invoked when an active solver is removed or
	 * invokes {@link #partialTaskDone(Solver)} in order to tell this
	 * task that the partial task of the solver is no longer processed.
	 * Note that the solver may not have completely processed
	 * its partial task.
	 * 
	 * @param task partial task which is no longer processed 
	 */
	protected abstract void dispose (PartialTask task);

	/**
	 * This method has to be invoked by a solver if it has
	 * completed processing its current partial task. Note that
	 * (depending on the specific implementation of <code>Task</code>)
	 * a solver may complete processing its partial task without
	 * completely solving it. In this case the unsolved part of the
	 * partial task is returned as part of later invocations
	 * of {@link #nextPartialTask(int)}.
	 * 
	 * @param s the solver which has completed processing its partial task
	 */
	public synchronized void partialTaskDone (Solver s)
	{
		if (solvers.contains (s))
		{
			idleSolvers.add (s);
		}
		dispose (s.clearCurrentPartialTask ());
		notifyAll ();
	}

	/**
	 * Returns <code>true</code> iff the complete task has been solved. 
	 * 
	 * @return has the task been solved?
	 */
	protected abstract boolean done ();

	public synchronized boolean isSolving ()
	{
		return solving;
	}

	public void solve ()
	{
		synchronized (this)
		{
			solving = true;
			prepareSolve ();
		}
		try
		{
			while (true)
			{
				Solver idle = null;
				PartialTask task = null;
				synchronized (this)
				{
					if (stopped || done ())
					{
						break;
					}
					if (!idleSolvers.isEmpty ())
					{
						idle = idleSolvers.get (0);
						int index = solvers.indexOf (idle);
						if (index >= 0)
						{
							task = nextPartialTask (index);
							if (task != null)
							{
								idleSolvers.remove (0);
							}
						}
					}
					if (task == null)
					{
						wait ();
					}
				}
				if (task != null)
				{
					idle.solve (task);
				}
			}
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException (e);
		}
		finally
		{
			synchronized (this)
			{
				solving = false;
				finishSolve ();
			}
		}
	}

	protected void prepareSolve ()
	{
	}

	protected void finishSolve ()
	{
	}

	public synchronized Solver[] getSolvers ()
	{
		Solver[] a = new Solver[solvers.size ()];
		solvers.toArray (a);
		return a;
	}

	public void removeSolvers ()
	{
		// solvers may change during the following, so save
		// the initial list of solvers in a
		Solver[] a = getSolvers ();
		for (int i = 0; i < a.length; i++)
		{
			a[i].dispose ();
		}
		synchronized (this)
		{
			idleSolvers.clear  ();
			solvers.clear ();
		}
	}
}
