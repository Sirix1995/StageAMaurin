
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

package de.grogra.pf.ui.awt;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.WeakHashMap;

public class AWTSynchronizer extends de.grogra.pf.ui.Synchronizer
{
	public static final AWTSynchronizer QUEUE = new AWTSynchronizer (null);


	public AWTSynchronizer (Callback callback)
	{
		super (callback);
	}


	@Override
	protected boolean isDispatchThread ()
	{
		return EventQueue.isDispatchThread ();
	}


	public void execute (Runnable r)
	{
		EventQueue.invokeLater (r);
	}


	private static final class Queue implements Runnable
	{
		private static final WeakHashMap queues = new WeakHashMap ();


		static synchronized Queue get (EventQueue eq)
		{
			Queue q = (Queue) queues.get (eq);
			if (q == null)
			{
				q = new Queue ();
				queues.put (eq, q);
			}
			return q;
		}


		private boolean running = false;
		private final Object postLock = new Object (), doneLock = new Object ();
		private Runnable nextRunnable = null;
		private Throwable exception;


		synchronized Throwable invokeAndWait (Runnable r)
		{
			try
			{
				synchronized (doneLock)
				{
					synchronized (postLock)
					{
						nextRunnable = r;
						if (running)
						{
							postLock.notifyAll ();
						}
						else
						{
							EventQueue.invokeLater (this);
						}
					}
					while (nextRunnable != null)
					{
						try
						{
							doneLock.wait ();
						}
						catch (InterruptedException e)
						{
							Thread.interrupted ();
						}
					}
				}
				return exception;
			}
			finally
			{
				exception = null;
			}
		}


		public void run ()
		{
			synchronized (postLock)
			{
				running = true;
				try
				{
					while (nextRunnable != null)
					{
						try
						{
							exception = null;
							nextRunnable.run ();
						}
						catch (Throwable t)
						{
							exception = t;
						}
						synchronized (doneLock)
						{
							nextRunnable = null;
							doneLock.notifyAll ();
						}
						if (running)
						{
							try
							{
								postLock.wait (15);
							}
							catch (InterruptedException e)
							{
								running = false;
							}
						}
						else
						{
							return;
						}
					}
				}
				finally
				{
					running = false;
				}
			}
		}
	}


	@Override
	protected void invokeAndWait (Runnable r)
		throws InvocationTargetException
	{
		Throwable t = Queue.get
			(java.awt.Toolkit.getDefaultToolkit ().getSystemEventQueue ())
			.invokeAndWait (r);
		if (t != null)
		{
			throw new InvocationTargetException (t);
		}
	}


	public static void staticInvokeAndWait (Runnable r)
	{
		try 
		{
			QUEUE.invokeAndWait (r);
		}
		catch (InvocationTargetException e)
		{
			de.grogra.util.Utils.rethrow (e.getTargetException ());
		}
	}


	public static void invokeInEventQueue (Runnable r)
	{
		if (EventQueue.isDispatchThread ())
		{
			r.run ();
		}
		else
		{
			staticInvokeAndWait (r);
		}
	}

}
