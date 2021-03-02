
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

package de.grogra.graph;

import java.util.*;
import de.grogra.reflect.FieldChain;
import de.grogra.util.*;

public abstract class GraphBase implements Graph
{
	private final Lockable lockable;

	EventSupport support;
	State mainState;
	private final IdentityHashMap stateMap = new IdentityHashMap ();


	public GraphBase (Lockable lockable)
	{
		this.lockable = lockable;
	}
	
	
	protected void init ()
	{
		support = new EventSupport (createObjectMap ());
		this.mainState = createMainState ();
	}
	
	
	protected abstract State createMainState ();
	
	
	public GraphState getMainState ()
	{
		return mainState;
	}
	

	protected abstract class State extends GraphState
	{
		private final EventSupport.Queue queue;

		
		public State (ThreadContext ctx)
		{
			super ();
			initialize (GraphBase.this, ctx);
			queue = support.new Queue ();
		}
		
		
		@Override
		public void fireAttributeChanged
			(Object object, boolean asNode, Attribute a, FieldChain field,
			 int[] indices)
		{
			queue.postAttributeChanged (object, asNode, a, field, indices);
		}

		
		@Override
		protected void fireEdgeChanged (Object source, Object target, Object edge)
		{
			queue.postEdgeChanged (source, target, edge);
		}


		public EventSupport.Queue getQueue ()
		{
			return queue;
		}
	}


	public void addChangeBoundaryListener (ChangeBoundaryListener l)
	{
		support.addChangeBoundaryListener (l);
	}


	public void removeChangeBoundaryListener (ChangeBoundaryListener l)
	{
		support.removeChangeBoundaryListener (l);
	}


	public void addAttributeChangeListener (AttributeChangeListener l)
	{
		support.addAttributeChangeListener (l);
	}


	public void addEdgeChangeListener (EdgeChangeListener l)
	{
		support.addEdgeChangeListener (l);
	}


	public void removeAttributeChangeListener (AttributeChangeListener l)
	{
		support.removeAttributeChangeListener (l);
	}


	public void removeEdgeChangeListener (EdgeChangeListener l)
	{
		support.removeEdgeChangeListener (l);
	}


	public void addAttributeChangeListener
		(Object object, boolean asNode, AttributeChangeListener l)
	{
		support.addAttributeChangeListener (object, asNode, l);
	}


	public void removeAttributeChangeListener
		(Object object, boolean asNode, AttributeChangeListener l)
	{
		support.removeAttributeChangeListener (object, asNode, l);
	}


	public void addEdgeChangeListener
		(Object object, boolean asNode, EdgeChangeListener l)
	{
		support.addEdgeChangeListener (object, asNode, l);

	}


	public void removeEdgeChangeListener
		(Object object, boolean asNode, EdgeChangeListener l)
	{
		support.removeEdgeChangeListener (object, asNode, l);
	}

	
	int writeLockCount;
	
	private final class TaskWrapper implements LockProtectedRunnable
	{
		final LockProtectedRunnable task;
		
		TaskWrapper (LockProtectedRunnable task)
		{
			this.task = task;
		}
		
		public void run (boolean sameThread, Lock lock)
		{
			boolean w = lock.isWriteLock () && mainState.getContext ().isCurrent ();
			try
			{
				if (w && (++writeLockCount == 1))
				{
					mainState.getQueue ().clear ();
				}
				task.run (sameThread, lock);
			}
			finally
			{
				if (w && (--writeLockCount == 0))
				{
					while (!mainState.getQueue ().isEmpty ())
					{
						mainState.getQueue ().fire (mainState, true);
					}
				}
			}
		}
	}

	
	public void execute (LockProtectedRunnable task, boolean write)
	{
		lockable.execute (new TaskWrapper (task), write);
	}
	
	
	public void execute (LockProtectedRunnable task, Lock retained)
	{
		lockable.execute (new TaskWrapper (task), retained);
	}
	
	
	public void executeForcedly (LockProtectedRunnable task, boolean write)
		throws InterruptedException, DeadLockException
	{
		lockable.executeForcedly (new TaskWrapper (task), write);
	}
	
	
	public void executeForcedly (LockProtectedRunnable task, Lock retained)
		throws InterruptedException
	{
		lockable.executeForcedly (new TaskWrapper (task), retained);
	}


	public boolean isLocked (boolean write)
	{
		return lockable.isLocked (write);
	}


	public int getQueueLength ()
	{
		return lockable.getQueueLength ();
	}
	
	
	public long getMaxWaitingTime ()
	{
		return lockable.getMaxWaitingTime ();
	}


	public java.util.Map getStateMap ()
	{
		return stateMap;
	}

}
