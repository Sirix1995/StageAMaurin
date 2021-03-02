
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

import de.grogra.xl.util.ObjectList;

/**
 * A <code>QueueDescriptor</code> is a handle to the method
 * {@link QueueCollection#getQueue(QueueDescriptor)},
 * there it is used to instantiate a queue by
 * {@link #createQueue(QueueCollection)}. In addition, a
 * <code>QueueDescriptor</code> describes the order processing of
 * queues (see {@link QueueCollection#process}) by its
 * methods {@link #queuesToProcessBefore()} and {@link #queuesToProcessAfter()}.
 * 
 * The methods <code>equals</code> and <code>hashCode</code> are overriden
 * such that two descriptors are equal iff their classes are equal.
 * 
 * @author Ole Kniemeyer
 */
public abstract class QueueDescriptor<Q extends Queue>
{
	/**
	 * Immutable <code>QueueDescriptor</code> array of length 0.
	 */
	public static final QueueDescriptor[] DESCRIPTOR_0 = new QueueDescriptor[0];


	private static final ObjectList<QueueDescriptor<?>> order = new ObjectList<QueueDescriptor<?>> ();
	private static boolean orderValid = false;

	private final ObjectList<QueueDescriptor<?>> before = new ObjectList<QueueDescriptor<?>> ();

	public QueueDescriptor ()
	{
		add (this);
	}

	private static synchronized void add (QueueDescriptor d)
	{
		if (!order.contains (d))
		{
			order.add (d);
			orderValid = false;
		}
	}

	public static synchronized void addOrder (List<? super QueueDescriptor<?>> queueOrder)
	{
		if (!orderValid)
		{
			QueueDescriptor[] a;
			do
			{
				a = order.toArray (new QueueDescriptor[order.size ()]);
				for (QueueDescriptor q : a)
				{
					q.before.clear ();
					QueueDescriptor[] descrs = q.queuesToProcessBefore ();
					q.before.addAll (descrs, 0, descrs.length);
				}
				for (QueueDescriptor q : a)
				{
					QueueDescriptor[] descrs = q.queuesToProcessAfter ();
					for (QueueDescriptor i : descrs)
					{
						i.before.add (q);
					}
				}
			} while (order.size () > a.length);
			order.clear ();
			int n = a.length;
			while (n > 0)
			{
				boolean found = false;
				for (int i = 0; i < a.length; i++)
				{
					QueueDescriptor q = a[i];
					if ((q != null) && q.before.isEmpty ())
					{
						a[i] = null;
						found = true;
						order.add (q);
						n--;
						for (QueueDescriptor r : a)
						{
							if (r != null)
							{
								while (r.before.remove (q))
								{
								}
							}
						}
					}
				}
				if (!found)
				{
					throw new Error ("Circularity");
				}
			}
			orderValid = true;
		}
		// cast to raw type List as a work-around for bug in javac 1.6.0_03
		((List) queueOrder).addAll (order);
	}


	@Override
	public final boolean equals (Object o)
	{
		return (o == this)
			|| ((o != null) && (o.getClass () == getClass ()));
	}
	

	@Override
	public final int hashCode ()
	{
		return getClass ().hashCode ();
	}


	/**
	 * Returns the descriptors of queues which are to be applied
	 * before the queue of this descriptor.
	 * 
	 * @return descriptors of queues to be applied before this descriptor's queue
	 * @see QueueCollection#process
	 */
	protected abstract QueueDescriptor[] queuesToProcessBefore ();


	/**
	 * Returns the descriptors of queues which are to be applied
	 * after the queue of this descriptor.
	 * 
	 * @return descriptors of queues to be applied after this descriptor's queue
	 * @see QueueCollection#process
	 */
	protected abstract QueueDescriptor[] queuesToProcessAfter ();


	/**
	 * Creates a new <code>Queue</code>. This method is invoked by
	 * {@link QueueCollection#getQueue(QueueDescriptor)}.
	 * 
	 * @param qc the invoking <code>QueueCollection</code>
	 * @return a new <code>Queue</code> instance corresponding to this descriptor
	 */
	public abstract Q createQueue (QueueCollection qc);
}
