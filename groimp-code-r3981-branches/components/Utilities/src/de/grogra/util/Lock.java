
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
 * A <code>Lock</code> on a {@link de.grogra.util.Lockable} resource is either
 * a read lock or an exclusive write lock. It is acquired indirectly by one of
 * the <code>execute</code> methods in {@link de.grogra.util.Lockable}.
 * 
 * @author Ole Kniemeyer
 */
public interface Lock
{
	/**
	 * Returns the resource on which this lock is held.
	 * 
	 * @return resource locked by this lock
	 */
	Lockable getLockable ();

	/**
	 * A lock may be retained by invocation of this method within the
	 * {@link LockProtectedRunnable#run(boolean, Lock)} method of a task. In
	 * this case, the lock is not released after the task has been finished:
	 * The lock remains active and has to be passed to one of the
	 * <code>execute</code> methods in {@link Lockable} which take a lock
	 * as parameter, only then the lock will be released.
	 */
	void retain ();

	/**
	 * Executes <code>callback</code> while temporarily reducing this write lock to a read lock.
	 * This gives threads waiting for a read lock the chance to execute.
	 * 
	 * @throws IllegalStateException if this lock is not a write lock
	 * @throws InterruptedException if this thread is interrupted while reacquiring the write lock. In this case the lock stays a read lock
	 */
	void executeWithoutWriteLock (Runnable callback) throws InterruptedException;

	/**
	 * Returns the type of lock.
	 * 
	 * @return is this lock a read or write lock?
	 */
	boolean isWriteLock ();
}
