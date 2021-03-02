
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

/**
 * An instance of <code>Lockable</code> is a resource on which
 * <em>read- or write-locked</em> tasks can be performed.
 * Several read-locked tasks may be executed concurrently, while
 * only a single write-locked task may be executed at the same
 * time. A write-locked task excludes the concurrent
 * execution of both read- and write-locked tasks, so a write lock
 * is an exclusive lock.
 * <p>
 * Locks are re-entrant: A task which is executed using a lock
 * may execute further nested, locked tasks in the same thread.
 * <p>
 * Tasks which should be executed using a lock are specified by
 * implementations of {@link de.grogra.util.LockProtectedRunnable}.
 * The lock/unlock operations are performed implicitly before
 * and after invocation of the method <code>run</code> in
 * <code>LockProtectedRunnable</code>. However,
 * a task may invoke {@link de.grogra.util.Lock#retain()} on its
 * lock in order to suppress the unlock operation after the task
 * has completed: In this case, the lock may be kept for a while and
 * even passed to another thread until it is eventually passed
 * to one of the <code>execute</code> methods of <code>Lockable</code>.
 * 
 * @author Ole Kniemeyer
 */
public interface Lockable
{
	/**
	 * An instance of <code>DeadLockException</code> is thrown
	 * by {@link de.grogra.util.Lockable#executeForcedly(LockProtectedRunnable, boolean)}
	 * if the operation would result in a dead-lock condition.
	 * This happens if two threads which already have a read-lock try to synchronously obtain a write-lock
	 * at the same time.
	 */
	class DeadLockException extends Exception
	{
		public DeadLockException()
		{
		}

		public DeadLockException(String msg)
		{
			super(msg);
		}
	}

	/**
	 * Asynchronously executes a <code>task</code> such that it possesses
	 * a lock on this <code>Lockable</code>. Note that the execution
	 * is asynchronous, so that the thread in which
	 * the task will actually be executed may differ from the current thread.
	 * 
	 * @param task the task to execute
	 * @param write shall a write lock be obtained?
	 */
	void execute (LockProtectedRunnable task, boolean write);

	/**
	 * Asynchronously executes a <code>task</code> using a lock which
	 * has been retained before within another task.
	 * Note that the execution
	 * is asynchronous, so that the thread in which
	 * the task will actually be executed may differ from the current thread.
	 * 
	 * @param task the task to execute
	 * @param retained the previously retained lock
	 * 
	 * @see Lock#retain()
	 */
	void execute (LockProtectedRunnable task, Lock retained);

	/**
	 * Synchronously executes a <code>task</code> such that it possesses
	 * a lock on this <code>Lockable</code>. The execution
	 * is performed in the current thread.
	 * 
	 * @param task the task to execute
	 * @param write shall a write lock be obtained?
	 */
	void executeForcedly (LockProtectedRunnable task, boolean write)
		throws InterruptedException, DeadLockException;

	/**
	 * Synchronously executes a <code>task</code> using a lock which
	 * has been retained before within another task. The execution
	 * is performed in the current thread.
	 * 
	 * @param task the task to execute
	 * @param retained the previously retained lock
	 * 
	 * @see Lock#retain()
	 */
	void executeForcedly (LockProtectedRunnable task, Lock retained)
		throws InterruptedException;

	/**
	 * Determines if the current thread has a lock for this
	 * <code>Lockable</code>.
	 * 
	 * @param write check for write locks only (<code>true</code>)
	 * or for both read and write locks (<code>false</code>)
	 * @return <code>true</code> if the current thread has a lock
	 */
	boolean isLocked (boolean write);
	
	/**
	 * Returns the current number of tasks which are waiting for
	 * locked execution.
	 * 
	 * @return current number of waiting tasks
	 */
	int getQueueLength ();
	
	/**
	 * Returns the waiting time of the pending task which is waiting longest,
	 * or <code>-1</code> if there is no pending task. 
	 * 
	 * @return current maximum waiting time of pending tasks
	 */
	long getMaxWaitingTime ();
}
