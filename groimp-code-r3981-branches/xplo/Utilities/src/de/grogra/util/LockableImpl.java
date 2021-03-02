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

import de.grogra.xl.util.BooleanList;
import de.grogra.xl.util.LongList;
import de.grogra.xl.util.ObjectList;

public class LockableImpl implements Lockable
{
	private final Object mutex = new Object ();

	private final ObjectList<LockProtectedRunnable> tasks = new ObjectList<LockProtectedRunnable> ();
	private final BooleanList isWriterTask = new BooleanList ();
	private final LongList times = new LongList ();

	private Thread writeLockOwner = null;
	private int activeWriteLocks = 0;
	private int retainedWriteLocks = 0;
	private int activeReadLocks = 0;
	private int retainedReadLocks = 0;
	private int deadLockRiskCounter = 0;
	private final ThreadLocal<int[]> lockCounts = new ThreadLocal<int[]> ();

	private int waitingWriters = 0;

	private final class LockImpl implements Lock
	{
		boolean write;
		boolean retained = false;
		boolean used = false;

		private AssertionError trace;

		LockImpl (boolean write)
		{
			this.write = write;
		}

		public Lockable getLockable ()
		{
			return LockableImpl.this;
		}

		private boolean getStackTrace ()
		{
			trace = new AssertionError ();
			return true;
		}

		public void retain ()
		{
			synchronized (mutex)
			{
				if (retained)
				{
					throw new IllegalStateException (
						"Lock has already been retained");
				}
				retained = true;
				if (write)
				{
					retainedWriteLocks++;
				}
				else
				{
					retainedReadLocks++;
				}
			}
			assert getStackTrace ();
		}

		public void executeWithoutWriteLock (Runnable callback) throws InterruptedException
		{
			Thread t = Thread.currentThread ();
			int[] a = getLockCounts ();
			if (!write || (t != writeLockOwner) || (a[WRITE] != 1))
			{
				throw new IllegalStateException ("The current thread has no write lock, or there exists more than one write lock");
			}
			synchronized (mutex)
			{
				assert (a[WRITE] == 1) && (activeWriteLocks == 1);
				// convert this lock into a read lock
				a[READ]++;
				activeReadLocks++;
				a[WRITE] = 0;
				activeWriteLocks = 0;
				writeLockOwner = null;
				write = false;
				mutex.notifyAll ();
			}

			// now we have temporarily released the write lock and can execute the callback
			callback.run ();

			synchronized (mutex)
			{
				assert a[WRITE] == 0;
				assert activeWriteLocks == 0;

				try
				{
					deadLockRiskCounter++;
					waitingWriters++;
					mutex.notifyAll ();
					// wait until other readers are done
					while (a[READ] != activeReadLocks)
					{
						mutex.wait ();
					}
					// convert this lock into a write lock
					a[READ]--;
					activeReadLocks--;
					a[WRITE] = 1;
					activeWriteLocks = 1;
					writeLockOwner = t;
					write = true;
				}
				finally
				{
					// NOTE: in case of an InterruptedException from mutex.wait(), we stay a read lock
					deadLockRiskCounter--;
					waitingWriters--;
					mutex.notifyAll ();
				}
			}
		}

		public boolean isWriteLock ()
		{
			return write;
		}

		@Override
		protected void finalize ()
		{
			if (retained && !used)
			{
				System.err.println ("Lock " + this
					+ " has not been used as argument to Lockable.execute");
				if (trace != null)
				{
					trace.printStackTrace ();
				}
			}
		}

		void dispose ()
		{
			retained = true;
			used = true;
		}

		void use ()
		{
			if (!retained)
			{
				throw new IllegalStateException (
					"Only retained locks may be used later on");
			}
			if (used)
			{
				throw new IllegalStateException ("Lock has already been used");
			}
			used = true;
			if (write)
			{
				retainedWriteLocks--;
			}
			else
			{
				retainedReadLocks--;
			}
			mutex.notifyAll ();
		}
	}

	private static final int READ = 0;
	private static final int WRITE = 1;

	private int[] getLockCounts ()
	{
		int[] a = lockCounts.get ();
		if (a == null)
		{
			lockCounts.set (a = new int[2]);
		}
		return a;
	}

	// obtain a lock for reading (forWrite==false) or writing (forWrite==true)
	// if no lock could be obtained, then null is returned
	// either multiple readers or one writer may be active at the same time
	// if all readers belong to the same thread, a write lock may be obtained also by this thread
	// if the current thread already owns a write lock, additional ones may be obtained
	private LockImpl lock (boolean hadLock, boolean forWrite)
	{
		assert Thread.holdsLock (mutex);
		Thread t = Thread.currentThread ();
		int[] a = getLockCounts ();
		if (t == writeLockOwner)
		{
			// is this correct ? what if current thread owns a write lock but wants
			// to obtain another read lock ? it gets a write lock instead !
			// other threads that just want to read then have to wait for no reason
			activeWriteLocks++;
			a[WRITE]++;
			return new LockImpl (true);
		}
		else if ((activeWriteLocks == 0)
			&& (hadLock || (retainedWriteLocks == 0)))
		{
			if (forWrite)
			{
				if ((activeReadLocks == a[READ])
					&& (hadLock || (a[READ] > 0) || (retainedReadLocks == 0)))
				{
					assert writeLockOwner == null;
					writeLockOwner = t;
					activeWriteLocks++;
					a[WRITE]++;
					return new LockImpl (true);
				}
			}
			else
			{
				if (hadLock || (a[READ] > 0)
					|| ((retainedReadLocks == 0) && (waitingWriters == 0)))
				{
					activeReadLocks++;
					a[READ]++;
					return new LockImpl (false);
				}
			}
		}
		return null;
	}

	// execute task non-blocking
	// if lock can be obtained, task is executed immediately (in the current thread),
	// otherwise task execution is deferred until a running task is finished
	public void execute (LockProtectedRunnable task, boolean write)
	{
		task.getClass ();
		LockImpl lock;
		synchronized (mutex)
		{
			if ((lock = lock (false, write)) == null)
			{
				enqueue (task, write);
			}
		}
		if (lock != null)
		{
			executeImpl (task, lock);
		}
	}

	private void enqueue (LockProtectedRunnable task, boolean write)
	{
		tasks.add (task);
		times.add (System.currentTimeMillis ());
		isWriterTask.add (write);
		if (write)
		{
			waitingWriters++;
		}
	}

	private void executeImpl (LockProtectedRunnable task, LockImpl lock)
	{
		boolean sync = true;
		Throwable exception = null;
		int[] counts = getLockCounts ();
		while (task != null)
		{
			boolean writeLockEntered = lock.write && (counts[WRITE] == 1);
			if (writeLockEntered)
			{
				try
				{
					enterWriteLock ();
				}
				catch (Throwable t)
				{
					t.printStackTrace ();
				}
			}
			try
			{
				invokeRun0 (task, sync, lock);
			}
			catch (Throwable t)
			{
				exception = t;
			}
			sync = false;
			task = null;
			if (writeLockEntered)
			{
				try
				{
					leaveWriteLock ();
				}
				catch (Throwable t)
				{
					t.printStackTrace ();
				}
			}
			synchronized (mutex)
			{
				if (!lock.retained)
				{
					lock.dispose ();
				}
				if (lock.write)
				{
					activeWriteLocks--;
					if (--counts[WRITE] == 0)
					{
						writeLockOwner = null;
					}
					assert (activeWriteLocks == 0) == (writeLockOwner == null);
				}
				else
				{
					activeReadLocks--;
					counts[READ]--;
				}
				mutex.notifyAll ();
				if (!tasks.isEmpty ())
				{
					if ((lock = lock (true, isWriterTask.get (0))) != null)
					{
						task = tasks.removeAt (0);
						if (isWriterTask.removeAt (0))
						{
							waitingWriters--;
						}
						times.removeAt (0);
					}
				}
			}
		}
		Utils.rethrow (exception);
	}

	// execute task non-blocking
	// if lock can be obtained, task is executed immediately (in the current thread),
	// otherwise task execution is deferred until a running task is finished
	// the retained lock is used to obtain the access type (read/write)
	public void execute (LockProtectedRunnable task, Lock retained)
	{
		task.getClass ();
		LockImpl lock = null;
		boolean write = ((LockImpl) retained).write;
		synchronized (mutex)
		{
			((LockImpl) retained).use ();
			if ((lock = lock (true, write)) == null)
			{
				enqueue (task, write);
			}
		}
		if (lock != null)
		{
			executeImpl (task, lock);
		}
	}

	private void checkThread (boolean write)
	{
		if (!isAllowedThread (write))
		{
			throw new IllegalStateException (
				"Current thread is not allowed to obtain a "
					+ (write ? "write" : "read") + " lock on " + this);
		}
	}

	// execute task blocking
	// the execute function will try to obtain the lock until successful and
	// then execute the task
	public void executeForcedly (LockProtectedRunnable task, boolean write)
			throws InterruptedException, DeadLockException
	{
		checkThread (write);
		task.getClass ();
		LockImpl lock = null;
		int[] a = getLockCounts ();
		synchronized (mutex)
		{
			boolean risk = write && (a[WRITE] == 0) && (a[READ] > 0);
			if (risk)
			{
				if (deadLockRiskCounter != 0)
				{
					throw new DeadLockException ();
				}
				deadLockRiskCounter = 1;
			}
			try
			{
				while ((lock = lock (false, write)) == null)
				{
					if (risk && (deadLockRiskCounter > 1))
					{
						throw new DeadLockException ();
					}
					mutex.wait ();
				}
			}
			finally
			{
				if (risk)
				{
					deadLockRiskCounter--;
				}
			}
		}
		executeImpl (task, lock);
	}

	// execute task blocking
	// the execute function will try to obtain the lock until successful and
	// then execute the task
	// the retained lock is used to obtain the access type (read/write)
	public void executeForcedly (LockProtectedRunnable task, Lock retained)
			throws InterruptedException
	{
		boolean write = ((LockImpl) retained).write;
		checkThread (write);
		task.getClass ();
		LockImpl lock = null;
		synchronized (mutex)
		{
			while ((lock = lock (true, write)) == null)
			{
				mutex.wait ();
			}
			((LockImpl) retained).use ();
		}
		executeImpl (task, lock);
	}

	public boolean isLocked (boolean write)
	{
		int[] a = getLockCounts ();
		return (a[WRITE] > 0) || (!write && (a[READ] > 0));
	}

	public int getQueueLength ()
	{
		synchronized (mutex)
		{
			return tasks.size ();
		}
	}

	public long getMaxWaitingTime ()
	{
		synchronized (mutex)
		{
			return times.isEmpty () ? -1 : System.currentTimeMillis ()
				- times.get (0);
		}
	}

	protected boolean isAllowedThread (boolean write)
	{
		return true;
	}

	protected void executeInAllowedThread (Runnable r)
	{
		throw new UnsupportedOperationException ("Not implemented in " + getClass ());
	}

	private void invokeRun0 (final LockProtectedRunnable task, boolean sync,
			Lock lock)
	{
		final boolean write = lock.isWriteLock ();

		class Helper implements Runnable, LockProtectedRunnable
		{
			private boolean executed;
			private Lock retainedLock;

			public void run ()
			{
				synchronized (this)
				{
					execute (this, write);
					if (executed)
					{
						return;
					}
					while (retainedLock == null)
					{
						try
						{
							wait ();
						}
						catch (InterruptedException e)
						{
							e.printStackTrace ();
						}
					}
				}
				try
				{
					executeForcedly (task, retainedLock);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace ();
				}
			}

			public void run (boolean sync, Lock lo)
			{
				if (isAllowedThread (lo.isWriteLock ()))
				{
					executed = true;
					invokeRun (task, false, lo);
				}
				else
				{
					lo.retain ();
					retainedLock = lo;
					synchronized (this)
					{
						notifyAll ();
					}
				}
			}
		}

		if (task instanceof Helper)
		{
			task.run (sync, lock);
		}
		else if (isAllowedThread (write))
		{
			invokeRun (task, sync, lock);
		}
		else
		{
			executeInAllowedThread (new Helper ());
		}
	}

	protected void invokeRun (final LockProtectedRunnable task, boolean sync,
			Lock lock)
	{
		task.run (sync, lock);
	}

	protected void enterWriteLock ()
	{
	}

	protected void leaveWriteLock ()
	{
	}
}
