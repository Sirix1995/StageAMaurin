
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

package de.grogra.pf.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;

public abstract class Synchronizer implements Runnable, Executor
{
	public interface Callback
	{
		Object run (int action, int iarg, Object oarg1, Object oarg2);
	}


	private Callback callback;
	private Thread thread;
	private int action, iarg;
	private Object oarg1, oarg2;


	public Synchronizer ()
	{
	}


	public Synchronizer (Callback callback)
	{
		initCallback (callback);
	}


	public void initCallback (Callback callback)
	{
		if (this.callback != null)
		{
			throw new IllegalStateException ("Callback already initialized");
		}
		this.callback = callback;
	}


	public Object invokeAndWait (int action)
	{
		return invokeAndWait (action, 0, null, null);
	}


	public Object invokeAndWait (int action, Object arg)
	{
		return invokeAndWait (action, 0, arg, null);
	}


	public Object invokeAndWait
		(final int action, final int iarg, final Object oarg1, final Object oarg2)
	{
		if (isDispatchThread ())
		{
			return callback.run (action, iarg, oarg1, oarg2);
		}
		else
		{
			if (thread == null)
			{
				thread = Thread.currentThread ();
			}
			else if (thread != Thread.currentThread ())
			{
				class Action implements Runnable
				{
					Object result;
					
					public void run ()
					{
						result = callback.run (action, iarg, oarg1, oarg2);
					}
				}
				
				try 
				{
					Action a = new Action ();
					invokeAndWait (a);
					return a.result;
				}
				catch (InvocationTargetException e)
				{
					de.grogra.util.Utils.rethrow (e.getCause ());
					throw new AssertionError ();
				}
			}

			this.action = action;
			this.iarg = iarg;
			this.oarg1 = oarg1;
			this.oarg2 = oarg2;
			try 
			{
				invokeAndWait (this);
				return this.oarg1;
			}
			catch (InvocationTargetException e)
			{
				de.grogra.util.Utils.rethrow (e.getCause ());
				throw new AssertionError ();
			}
			finally
			{
				this.oarg1 = null;
				this.oarg2 = null;
			}
		}
	}


	public void run ()
	{
		oarg1 = callback.run (action, iarg, oarg1, oarg2);
	}


	protected abstract boolean isDispatchThread ();


	protected abstract void invokeAndWait (Runnable r)
		throws InvocationTargetException;

}
