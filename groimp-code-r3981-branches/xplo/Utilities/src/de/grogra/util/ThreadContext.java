
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

package de.grogra.util;

import java.util.*;

import de.grogra.xl.util.ObjectList;

public final class ThreadContext
{
	private static final ThreadLocal CONTEXT = new ThreadLocal ();

	public static final short MIN_PRIORITY = 0;
	public static final short NORMAL_PRIORITY = 10000;
	public static final short MAX_PRIORITY = Short.MAX_VALUE;

	private short priority = NORMAL_PRIORITY;

	private Thread thread;

	private final int id;
	private static int nextId = 0;


	private ThreadContext ()
	{
		synchronized (CONTEXT)
		{
			id = nextId++;
		}
	}


	public int getId ()
	{
		return id;
	}


	public static ThreadContext current ()
	{
		ThreadContext t = (ThreadContext) CONTEXT.get ();
		if (t != null)
		{
			return t;
		}
		t = new ThreadContext ();
		t.join ();
		return t;
	}


	public boolean isCurrent ()
	{
		return this == CONTEXT.get ();
	}


	public Thread getThread ()
	{
		return thread;
	}


	public ThreadContext check ()
	{
		if (thread != Thread.currentThread ())
		{
			throw new AssertionError (thread + " " + Thread.currentThread ());
		}
		return this;
	}


	private static int registeredPropertyCount = 0;

	public static synchronized int registerProperty ()
	{
		return registeredPropertyCount++;
	}


	private ObjectList<Object>properties = new ObjectList<Object> ();

	public Object getProperty (int propertyId)
	{
		return properties.get (propertyId);
	}


	public void setProperty (int propertyId, Object value)
	{
		properties.set (propertyId, value);
	}


	public void checkInterruption () throws InterruptedException
	{
		if (thread.isInterrupted ())
		{
			if (Thread.currentThread () != thread)
			{
				throw new IllegalStateException
					(this + " has to be joined with the current thread.");
			}
			Thread.interrupted ();
			throw new InterruptedException ();
		}
	}


	public void join ()
	{
		Thread t = Thread.currentThread ();
		if (thread == t)
		{
			return;
		}
		if (thread != null)
		{
			throw new IllegalStateException (this + " is already joined.");
		}
		if (CONTEXT.get () != null)
		{
			throw new IllegalStateException ("Thread " + thread
											 + " already has a joined"
											 + " ThreadContext");
		}
		thread = t;
		CONTEXT.set (this);
	}


	public void leave ()
	{
		if (thread == null)
		{
			return;
		}
		if (thread != Thread.currentThread ())
		{
			throw new IllegalStateException ("thread differs from current"
											 + " thread.");
		}
		CONTEXT.set (null);
		thread = null;
	}


	public void joinWhileExecuting (Runnable r)
	{
		Thread old = thread;
		ThreadContext oldCtx = (ThreadContext) CONTEXT.get ();
		thread = Thread.currentThread ();
		CONTEXT.set (this);
		try
		{
			r.run ();
		}
		finally
		{
			thread = old;
			CONTEXT.set (oldCtx);
		}
	}
	

	public boolean hasHigherPriorityThan (ThreadContext o)
	{
		return (priority > o.priority)
			|| ((priority == o.priority) && (id > o.id)
				&& (priority != Integer.MIN_VALUE));
	}
	
	
	public short getPriority ()
	{
		return priority;
	}

	
	public void setPriority (short priority)
	{
		this.priority = priority;
	}
}
