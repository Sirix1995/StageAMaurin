
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

package de.grogra.xl.impl.queues;

import de.grogra.xl.query.RuntimeModelException;

/**
 * A <code>Queue</code> represents a queue of tasks which is filled
 * during the execution of an XL program. For the base implementation
 * of the XL interfaces, when a derivation shall be performed by invocation
 * of {@link de.grogra.xl.impl.base.Graph#derive()},
 * the execution of the queued tasks is induced by the invocation
 * of {@link #process}.
 * 
 * Instances of <code>Queue</code> are obtained at run-time by the method
 * {@link de.grogra.xl.impl.queues.QueueCollection#getQueue(QueueDescriptor)}.
 * The returned queue is registered automatically with the
 * <code>QueueCollection</code>, this ensures that its
 * <code>process</code> method is invoked when all queues shall be processes.
 * The order in which the <code>process</code> method is invoked
 * for the registered queues of a <code>QueueCollection</code> is defined
 * by the {@link de.grogra.xl.impl.queues.QueueDescriptor}s which were used
 * to obtain the queues, see
 * {@link de.grogra.xl.impl.queues.QueueCollection#process}.
 *
 * @author Ole Kniemeyer
 */
public interface Queue
{
	/**
	 * Returns the descriptor which was used to create this queue.
	 * 
	 * @return descriptor of this queue
	 * 
	 * @see QueueCollection#getQueue(QueueDescriptor)
	 * @see QueueDescriptor#createQueue(QueueCollection)
	 */
	QueueDescriptor<?> getDescriptor ();

	/**
	 * Processes all queued tasks. This method is invoked by
	 * {@link QueueCollection#process}.
	 * 
	 * @return <code>true</code> iff the execution of tasks resulted
	 * in modifications to the <code>Graph</code>
	 * @throws RuntimeModelException if some error occurs during execution
	 */
	boolean process (int[] segments) throws RuntimeModelException;

	void clearSegmentsToExclude (int[] segments);

	/**
	 * Clears the queue. This is invoked by the {@link QueueCollection}
	 * in order to reuse the queue for a new transformation step.
	 */
	void clear ();

	void markSegment (int n);

	void resetToSegment (int n);
}
