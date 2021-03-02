
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
 * A <code>LockProtectedRunnable</code> represents a task which is to be
 * executed within the context of a {@link de.grogra.util.Lock} on a
 * [@link de.grogra.util.Lockable} resource. The execution of the task is
 * induced by one of the <code>execute</code> methods in
 * {@link de.grogra.util.Lockable}.
 * 
 * @author Ole Kniemeyer
 */
public interface LockProtectedRunnable
{
	/**
	 * The invocation of this method is induced by one of the
	 * <code>execute</code> methods in {@link Lockable}. The thread which
	 * invokes this method possesses a <code>lock</code> as requested by the
	 * invocation of <code>execute</code>. If <code>sync</code> is
	 * <code>true</code>, the invocation is synchronous, meaning that it
	 * happens directly within the invocation of the <code>execute</code>
	 * method. Otherwise, the invocation is asynchronous, meaning that it
	 * happens later in a thread which may differ from the original thread.
	 * <p>
	 * Normally, the lock is released after execution of <code>run</code> has
	 * finished. However, when the method {@link Lock#retain()} has been
	 * invoked within <code>run</code>, the lock is retained and remains
	 * active in order to be passed to one of the <code>execute</code>
	 * methods of {@link Lockable}.
	 * 
	 * @param sync synchronous invocation?
	 * @param lock the lock which is currently acquired
	 */
	void run (boolean sync, Lock lock);
}
