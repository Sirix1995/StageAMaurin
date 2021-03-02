
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
 * This abstract class implements <code>Solver</code> such that a thread
 * is created in which the actual work is done. 
 * 
 * @author Ole Kniemeyer
 */
public abstract class SolverInOwnThread extends Solver implements Runnable
{
	private boolean running;
	private boolean disposed;
	private boolean useThread;
	private int id;

	/**
	 * Create the thread to use. If this returns <code>null</code>,
	 * the <code>solve</code> methods operates synchronously, i.e., 
	 * it immediately solves the partial task.
	 * 
	 * @return thread to use for operation, or <code>null</code>
	 * in synchronous case
	 */
	protected abstract Thread createThread ();

	
	/**
	 * This method has to be implemented to perform the actual work.
	 * 
	 * @param task task to solve
	 */
	protected abstract void solveImpl (PartialTask task);

	@Override
	protected synchronized void solve ()
	{
		if (!running)
		{
			Thread t = createThread ();
			useThread = t != null;
			if (useThread)
			{
				t.start ();
			}
			running = true;
		}
		if (useThread)
		{
			notifyAll ();
		}
		else
		{
			solveImpl (getCurrentPartialTask ());
			getTask ().partialTaskDone (this);
		}
	}

	public void run ()
	{
		try
		{
			while (!getTask ().isStopped ())
			{
				PartialTask task;
				synchronized (this)
				{
					if (disposed)
					{
						break;
					}
					task = getCurrentPartialTask ();
					if (task == null)
					{
						wait ();
					}
				}
				if (task != null)
				{
					solveImpl (task);
					getTask ().partialTaskDone (this);
				}
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace ();
		}
		finally
		{
			getTask ().removeSolver (this);
		}
	}

	@Override
	public synchronized void dispose ()
	{
		disposed = true;
		notifyAll ();
	}
}
