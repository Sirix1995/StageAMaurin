
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

import java.util.List;

import de.grogra.xl.query.Graph;
import de.grogra.xl.query.RuntimeModel;
import de.grogra.xl.query.RuntimeModelException;
import de.grogra.xl.util.EHashMap;
import de.grogra.xl.util.ObjectList;

/**
 * A <code>QueueCollection</Code> represents a set of
 * {@link de.grogra.xl.impl.queues.Queue}s in the context of a
 * {@link de.grogra.xl.query.Graph}.
 * For the base implementation of the XL interfaces, it is obtained
 * at run-time by the invocation of
 * {@link de.grogra.xl.impl.base.Graph#getQueues()}.
 * <p>
 * Queues are added to this collection by invocation of
 * {@link #getQueue(QueueDescriptor)}.
 * 
 * @author Ole Kniemeyer
 */
public final class QueueCollection
{
	final Graph graph;
	final RuntimeModel model;

	private static final class QueueEntry extends EHashMap.ObjectEntry<QueueDescriptor,Queue>
	{
		ObjectList<Queue> additionalQueues;
	}

	
	private final QueueEntry queueKey = new QueueEntry ();
	private final EHashMap queues = new EHashMap ();
	private int currentSegment = -1; 

	private final ObjectList<QueueDescriptor<?>> order = new ObjectList<QueueDescriptor<?>> ();


	public QueueCollection (Graph graph)
	{
		this.graph = graph;
		this.model = graph.getModel ();
	}
	
	
	/**
	 * @return the <code>Graph</code> of this queue collection
	 */
	public Graph getGraph ()
	{
		return graph;
	}

	
	/**
	 * @return the <code>RuntimeModel</code> of the graph of this queue collection
	 */
	public RuntimeModel getModel ()
	{
		return model;
	}

	
	/**
	 * Clears all queues of this collection.
	 */
	public void clear ()
	{
		for (QueueEntry e = (QueueEntry) queues.getFirstEntry ();
			 e != null; e = (QueueEntry) e.listNext)
		{
			e.value.clear ();
			if (e.additionalQueues != null)
			{
				e.additionalQueues.clear ();
			}
		}
		currentSegment = -1;
	}


	/**
	 * Processes the queues of this collection by invoking
	 * {@link Queue#process} for every queue.
	 * All queues which have been registered by
	 * {@link #getQueue(QueueDescriptor)} are processed. The order of
	 * processing is defined by the {@link QueueDescriptor}s of the queues:
	 * If <code>q</code> and <code>r</code> are two queues having descriptors
	 * <code>Q</code> and <code>R</code>, then
	 * <code>q.process(segments)</code> may be invoked before <code>r.process(segments)</code>
	 * only if Q is not contained in <code>R.queuesToProcessAfter()</code>
	 * and R is not contained in <code>Q.queuesToProcessBefore()</code>.
	 * 
	 * After all queues have been processed, this method clears the queues.
	 * 
	 * @return <code>true</code> iff at least one invocation of the
	 * <code>process</code> method on a queue has returned <code>true</code>.
	 * @throws RuntimeModelException if the processing of a queue throws
	 * such an exception, or if there is a circularity in the processing order 
	 */
	public boolean process (int[] segments) throws RuntimeModelException
	{
		boolean modified = false;
		try
		{
			order.clear ();
			QueueDescriptor.addOrder (order);
			if (segments != null)
			{
				for (int i = 0; i < order.size (); i++)
				{
					QueueDescriptor d = order.get (i);
					queueKey.setKey (d);
					QueueEntry e = (QueueEntry) queues.get (queueKey);
					if (e != null)
					{
						e.value.clearSegmentsToExclude (segments);
						if (e.additionalQueues != null)
						{
							for (int j = 0; j < e.additionalQueues.size; j++)
							{
								e.additionalQueues.get (j).clearSegmentsToExclude (segments);
							}
						}
					}
				}
			}
			for (int i = 0; i < order.size (); i++)
			{
				QueueDescriptor d = order.get (i);
				queueKey.setKey (d);
				QueueEntry e = (QueueEntry) queues.get (queueKey);
				if (e != null)
				{
					modified |= e.value.process (segments);
					if (e.additionalQueues != null)
					{
						for (int j = 0; j < e.additionalQueues.size; j++)
						{
							Queue q = e.additionalQueues.get (j);
							modified |= q.process (segments);
						}
					}
				}
			}
		}
		finally
		{
			clear ();
		}
		return modified;
	}

	public void getQueues (List<Queue> list)
	{
		for (QueueEntry e = (QueueEntry) queues.getFirstEntry ();
			 e != null; e = (QueueEntry) e.listNext)
		{
			list.add (e.value);
			if (e.additionalQueues != null)
			{
				list.addAll (e.additionalQueues);
			}
		}
	}


	/**
	 * Registers and returns a queue for the given descriptor. If this
	 * method has been invoked before with the same descriptor, the
	 * previously returned queue is returned again. Otherwise, a new queue
	 * is created by the invocation of
	 * {@link QueueDescriptor#createQueue(QueueCollection)}
	 * on <code>descr</code>, registered with this queue collection,
	 * and returned. 
	 * 
	 * @param descr the descriptor of the queue
	 * @return a queue corresponding to the descriptor
	 */
	public <Q extends Queue> Q getQueue (QueueDescriptor<Q> descr)
	{
		queueKey.setKey (descr);
		QueueEntry e = (QueueEntry) queues.get (queueKey);
		if (e != null)
		{
			return (Q) e.value;
		}
		e = (QueueEntry) queues.popEntryFromPool ();
		if (e == null)
		{
			e = new QueueEntry ();
		}
		e.setKey (descr);
		Q q = descr.createQueue (this);
		if (currentSegment >= 0)
		{
			q.markSegment (currentSegment);
		}
		e.value = q;
		queues.put (e);
		return q;
	}

	public void addQueue (Queue queue)
	{
		getQueue (queue.getDescriptor ());
		queueKey.setKey (queue.getDescriptor ());
		QueueEntry e = (QueueEntry) queues.get (queueKey);
		if (e.additionalQueues == null)
		{
			e.additionalQueues = new ObjectList<Queue> ();
		}
		e.additionalQueues.add (queue);
	}

	public int startNewSegment ()
	{
		currentSegment++;
		for (QueueEntry e = (QueueEntry) queues.getFirstEntry ();
			 e != null; e = (QueueEntry) e.listNext)
		{
			e.value.markSegment (currentSegment);
		}
		return currentSegment;
	}

	public void resetToSegment (int n)
	{
		for (QueueEntry e = (QueueEntry) queues.getFirstEntry ();
			 e != null; e = (QueueEntry) e.listNext)
		{
			e.value.resetToSegment (n);
		}
		currentSegment = n;
	}

}
