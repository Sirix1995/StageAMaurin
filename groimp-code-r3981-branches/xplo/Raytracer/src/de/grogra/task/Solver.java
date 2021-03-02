
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

/**
 * A <code>Solver</code> solves a {@link de.grogra.task.PartialTask}
 * of a {@link de.grogra.task.Task}. The process of solving may
 * be implemented asynchronously, so that multiple processors
 * or even remote processors can be used.
 * 
 * @author Ole Kniemeyer
 */
public abstract class Solver
{
	/**
	 * The complete task for which this solver is used.
	 */
	private Task task;

	/**
	 * The current partial task to solve.
	 */
	private PartialTask currentPartialTask;

	/**
	 * Initializes this solver to be used for the given task.
	 * 
	 * @param task the complete task for which this solver is used 
	 */
	public void initialize (Task task)
	{
		this.task = task;
		currentPartialTask = null;
	}

	/**
	 * Returns the complete task for which this solver is used.
	 * 
	 * @return complete task of this solver
	 */
	public Task getTask ()
	{
		return task;
	}

	final void solve (PartialTask task)
	{
		if (currentPartialTask != null)
		{
			throw new IllegalStateException ();
		}
		currentPartialTask = task;
		solve ();
	}

	final PartialTask clearCurrentPartialTask ()
	{
		PartialTask t = currentPartialTask;
		currentPartialTask = null;
		return t;
	}
	
	/**
	 * Returns the partial task which is currently solved by this
	 * solver. Returns <code>null</code> if the solver has nothing
	 * to do.
	 * 
	 * @return current partial task
	 */
	public final PartialTask getCurrentPartialTask ()
	{
		return currentPartialTask;
	}

	/**
	 * This method has to be implemented by subclasses in order to
	 * solve the current partial task synchronously or asynchronously.
	 * After the partial task has been solved, the method
	 * {@link Task#partialTaskDone(Solver)} has to be invoked.
	 */
	protected abstract void solve ();

	
	/**
	 * This method is invoked by the {@link Task} when this solver
	 * is removed from the task. Subclasses have to implement this method
	 * in order to free resources (e.g., created threads).
	 */
	public abstract void dispose ();
}
