
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

package de.grogra.imp;

import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.logging.Level;

import de.grogra.persistence.Transaction;
import de.grogra.persistence.XAListener;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.Workbench;
import de.grogra.util.Disposable;
import de.grogra.util.ThreadContext;
import de.grogra.util.Utils;
import de.grogra.xl.util.ObjectList;

public final class IMPJobManager implements JobManager, Runnable, XAListener, Executor
{
	private IMPWorkbench workbench;
	private boolean running = true;
	private volatile boolean executing = false;
	private volatile String nameOfExecuting;
	private int waitingMessageIndex = -1;
	private TimerTask waitingMessageTask;
	private static final int ENTRY_SIZE = 5;
	private Object[] queue = new Object[512];
	private int head = 0, tail = 0;
	private final ObjectList timedCallbacks = new ObjectList ();
	private Command afterDispose;
	private Runnable blocking = null;
	private boolean modified;
	private Thread mainThread;
	private ThreadContext tcontext;
	private ObjectList listeners = new ObjectList ();
	private Window initFeedback;


	IMPJobManager ()
	{
	}


	public ThreadContext getThreadContext ()
	{
		return tcontext;
	}


	boolean isCurrent ()
	{
		return tcontext.isCurrent ();
	}


	public Thread getMainThread ()
	{
		return mainThread;
	}


	void initialize (IMPWorkbench workbench)
	{
		this.workbench = workbench;
		workbench.getRegistry ().getRegistryGraph ().addXAListener (this);
		workbench.getRegistry ().getProjectGraph ().addXAListener (this);
	}


	public Workbench getWorkbench ()
	{
		return workbench;
	}


	public Window getWindow ()
	{
		return workbench.getWindow ();
	}


	public Panel getPanel ()
	{
		return workbench.getPanel ();
	}


	public Object getComponent ()
	{
		return workbench.getComponent ();
	}


	private void checkWaitingMessage ()
	{
		if (executing && (waitingMessageIndex < 0) && !isCurrent ())
		{
			waitingMessageIndex = tail;

			waitingMessageTask = new TimerTask ()
			{
				private boolean cancel = false;
				private Disposable message = null;

				@Override
				public synchronized void run ()
				{
					if (cancel)
					{
						return;
					}
					Window w = getWindow ();
					if (w != null)
					{
						message = w.showWaitMessage (nameOfExecuting);
					}
				}


				@Override
				public boolean cancel ()
				{
					cancel = true;
					boolean b = super.cancel ();
					synchronized (this)
					{
						if (message != null)
						{
							message.dispose ();
						}
					}
					return b;
				}
			};
			Workbench.TIMER.schedule (waitingMessageTask, 800);
		}
	}


	private static final Command INVOKE_LATER = new Command ()
	{
		public String getCommandName ()
		{
			return null;
		}

		public void run (Object info, Context context)
		{
			((Runnable) info).run ();
		}
	};

	public void execute (Runnable r)
	{
		runLater (INVOKE_LATER, r, this, ACTION_FLAGS); 
	}


	public synchronized void runLater (Command command, Object info,
									   Context ctx, int flags)
	{
		if (blocking != null)
		{
			execute (command, info, ctx, flags);
		}
		else
		{
			if ((flags & QUIET) == 0)
			{
				checkWaitingMessage ();
			}
			enqueue (command);
			enqueue (info);
			enqueue (ctx);
			enqueue (Short.valueOf ((short) flags));
			enqueue (new Long (System.currentTimeMillis ()));
			notifyAll ();
		}
	}


	private void enqueue (Object object)
	{
		if (!running)
		{
			return;
		}
		queue[tail] = object;
		if (++tail == queue.length)
		{
			tail = 0;
		}
		if (tail == head)
		{
			Object[] q = new Object[queue.length * 2];
			head += q.length - queue.length;
			System.arraycopy (queue, 0, q, 0, tail);
			System.arraycopy (queue, tail, q, head, queue.length - tail);
			queue = q;
		}
	}


	private Object dequeue ()
	{
		Object object = queue[head];
		queue[head] = null;
		if (++head == queue.length)
		{
			head = 0;
		}
		return object;
	}
	
	
	private Object peek (int n)
	{
		return queue[(n + head) % queue.length];
	}


	public final void execute (final Command cmd, final Object info,
							   final Context ctx, int flags)
	{
		if (isCurrent ())
		{
			short p = tcontext.getPriority ();
			tcontext.setPriority ((short) (flags & PRIORITY_MASK));
			cmd.run (info, ctx);
			tcontext.setPriority (p);
		}
		else if (blocking != null)
		{
			workbench.runAsCurrent (new Runnable ()
			{
				private boolean joined;

				public void run ()
				{
					if (joined)
					{
						cmd.run (info, ctx);
					}
					else
					{
						joined = true;
						getThreadContext ().joinWhileExecuting (this);
					}
				}
			});
		}
		else
		{
			runLater (cmd, info, ctx, flags);
		}
	}


	public void runLater (long delay, Command object, Object info,
						  Context ctx)
	{
		runAt (System.currentTimeMillis () + delay, object, info, ctx);
	}


	public synchronized void runAt (long time, Command object, Object info,
									Context ctx)
	{
		int i;
		for (i = 0; i < timedCallbacks.size; i += 4)
		{
			if (((Long) timedCallbacks.get (i)).longValue () > time)
			{
				break;
			}
		}
		timedCallbacks.add (i, new Long (time));
		timedCallbacks.add (i + 1, object);
		timedCallbacks.add (i + 2, info);
		timedCallbacks.add (i + 3, ctx);
		notifyAll ();
	}


	public synchronized long getNextInvocationTime ()
	{
		return timedCallbacks.size == 0 ? Long.MAX_VALUE
			: ((Long) timedCallbacks.get (0)).longValue ();
	}


	public synchronized void cancelQueuedJob (Command object)
	{
		int i = head;
		while (i != tail)
		{
			if (queue[i] == object)
			{
				queue[i] = null;
				return;
			}
			i = (i + ENTRY_SIZE) % queue.length;
		}
	}


	public synchronized void cancelTimedJob (Command object)
	{
		for (int i = timedCallbacks.size - 4; i >= 0; i -= 4)
		{
			if (timedCallbacks.get (i + 1) == object)
			{
				timedCallbacks.remove (i + 3);
				timedCallbacks.remove (i + 2);
				timedCallbacks.remove (i + 1);
				timedCallbacks.remove (i);
			}
		}
	}


	public synchronized boolean hasJobQueued (int minPriority)
	{
		int i = head;
		while (i != tail)
		{
			if ((queue[i] != null)
				&& ((((Number) queue[(i + 3) % queue.length]).intValue () & PRIORITY_MASK)
					>= minPriority))
			{
				return true;
			}
			i = (i + ENTRY_SIZE) % queue.length;
		}
		return false;
	}


	public synchronized boolean hasTimedJobQueued ()
	{
		return !timedCallbacks.isEmpty ();
	}


	public void runBlocking (Runnable r)
	{
		if (!isCurrent ())
		{
			throw new IllegalStateException ();
		}
		Runnable b = blocking;
		blocking = r;
		try
		{
			r.run ();
		}
		finally
		{
			blocking = b;
		}
	}


	public void transactionApplied (Transaction.Data xa, boolean rollback)
	{
		modified = true;
	}


	void start (Window feedback)
	{
		initFeedback = feedback;
		new Thread (this, "JobManager@" + workbench).start ();
	}


	private static long nextGC = 0;
	private static int executingCount = 0;
	private static final Object gcLock = new Object ();
	
	private static final boolean SYSTEM_GC = false;

	public void run ()
	{
		mainThread = Thread.currentThread ();
		mainThread.setPriority ((Thread.MIN_PRIORITY + Thread.NORM_PRIORITY) / 2);
		tcontext = ThreadContext.current ();
		tcontext.setPriority (UI_PRIORITY);
		try
		{
			if (initFeedback != null)
			{
				initFeedback.setCursor (Panel.INC_WAIT_CURSOR);
			}
			try
			{
				workbench.initialize ();
			}
			finally
			{
				if (initFeedback != null)
				{
					initFeedback.setCursor (Panel.DEC_WAIT_CURSOR);
					initFeedback = null;
				}
			}
			Utils.flushHandlers (workbench.getLogger ());
			ObjectList listeners2 = new ObjectList ();
			while (running)
			{
				synchronized (gcLock)
				{
					nextGC = System.currentTimeMillis () + 7000;
				}
				Thread.interrupted ();
				Command c = null;
				Object o = null, info = null;
				Context ctx = null;
				Command callback = null;
				TimerTask toCancel = null;
				synchronized (this)
				{
					while (true)
					{
						long nextTime, t, ct;
						while (((t = (nextTime = getNextInvocationTime ())
								 - (ct = System.currentTimeMillis ())) > 0)
							   && (head == tail))
						{
							boolean invokeGC;
							synchronized (gcLock)
							{
								invokeGC = (ct >= nextGC) && (executingCount == 0);
								if (invokeGC)
								{
									nextGC = Long.MAX_VALUE;
								}
							}
							if (invokeGC && SYSTEM_GC)
							{
								System.gc ();
							}
							try
							{
								wait (Math.min (t, 1000));
								Utils.flushHandlers (workbench.getLogger ());
							}
							catch (InterruptedException e)
							{
							}
							if (!running)
							{
								return;
							}
						}
						if ((head != tail)
							&& ((t > 0) || (head == waitingMessageIndex)
								|| (queue[head] == null)
								|| (((Long) peek (ENTRY_SIZE - 1)).longValue () <= nextTime)))
						{
							if (head == waitingMessageIndex)
							{
								waitingMessageIndex = -1;
								toCancel = waitingMessageTask;
								waitingMessageTask = null;
							}
							c = (Command) dequeue ();
							info = dequeue ();
							ctx = (Context) dequeue ();
							short p = ((Number) dequeue ()).shortValue ();
							dequeue ();
							if (c == null)
							{
								continue;
							}
							tcontext.setPriority (p);
							nameOfExecuting = c.getCommandName ();
							if (nameOfExecuting == null)
							{
								nameOfExecuting = c.toString ();
							}
						}
						else if (t <= 0)
						{
							ctx = (Context) timedCallbacks.remove (3);
							info = timedCallbacks.remove (2);
							callback = (Command) timedCallbacks.remove (1);
							o = timedCallbacks.remove (0);
							tcontext.setPriority (UI_PRIORITY);
						}
						break;
					}
					executing = true;
					listeners2.clear ();
					listeners2.addAll (listeners);
				}
				for (int i = 0; i < listeners2.size; i++)
				{
					((ExecutionListener) listeners2.get (i))
						.executionStarted (this);
				}
				if (toCancel != null)
				{
					toCancel.cancel ();
				}
				modified = false;
//				System.err.println ("JM " + c + " " + callback);
				final Window wd = getWindow ();
				TimerTask cursor = new TimerTask ()
				{
					@Override
					public void run ()
					{
						wd.setCursor (Panel.INC_WAIT_CURSOR);
					}
				};
				if (wd != null)
				{
					Workbench.TIMER.schedule (cursor, 500);
				}
				try
				{
					synchronized (gcLock)
					{
						executingCount++;
					}
					if (o == null)
					{
						c.run (info, ctx);
					}
					else if (o instanceof Long)
					{
						callback.run (info, ctx);
					}
					else
					{
						throw new AssertionError (o);
					}
				}
				catch (Throwable e)
				{
					workbench.getLogger ()
						.log (Level.WARNING, "Unexpected Exception", Utils.getMainException (e));
				}
				finally
				{
					synchronized (gcLock)
					{
						executingCount--;
					}
					if ((wd != null) && !cursor.cancel ())
					{
						wd.setCursor (Panel.DEC_WAIT_CURSOR);
					}
					Transaction xa = workbench.getRegistry ()
						.getRegistryGraph ().getTransaction (false);
					if (xa != null)
					{
						xa.close ();
					}
					executing = false;
					if (modified)
					{
						workbench.setModified ();
					}
					listeners2.clear ();
					synchronized (this)
					{
						listeners2.addAll (listeners);
					}
					for (int i = 0; i < listeners2.size; i++)
					{
						((ExecutionListener) listeners2.get (i))
							.executionFinished (this);
					}
				}
				Utils.flushHandlers (workbench.getLogger ());
			}
		}
		catch (Throwable t)
		{
			de.grogra.pf.boot.Main.logSevere (t);
		}
		workbench.getRegistry ().getRegistryGraph ().removeXAListener (this);
		workbench.getRegistry ().getProjectGraph ().removeXAListener (this);
		running = false;
		queue = null;
		workbench.dispose (afterDispose);
	}


	void stop (Command afterDispose)
	{
		tcontext.check ();
		running = false;
		this.afterDispose = afterDispose;
	}


	public synchronized void addExecutionListener (ExecutionListener listener)
	{
		listeners.add (listener);
	}


	public synchronized void removeExecutionListener (ExecutionListener listener)
	{
		listeners.remove (listener);
	}

}
