
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

package de.grogra.rgg.model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import de.grogra.graph.impl.Node;
import de.grogra.persistence.ModificationQueue;
import de.grogra.persistence.Transaction;
import de.grogra.xl.impl.base.GraphQueue;
import de.grogra.xl.impl.queues.QueueCollection;
import de.grogra.xl.impl.queues.QueueDescriptor;
import de.grogra.xl.query.RuntimeModelException;
import de.grogra.xl.util.ObjectList;

public class PropertyQueue extends ModificationQueue
	implements TransferableQueue
{
	public static final PropertyQueueDescriptor PROPERTY_QUEUE = new PropertyQueueDescriptor ();
	
	public static final class PropertyQueueDescriptor extends QueueDescriptor<PropertyQueue>
	{
		@Override
		protected QueueDescriptor[] queuesToProcessBefore ()
		{
			return DESCRIPTOR_0;
		}

		@Override
		protected QueueDescriptor[] queuesToProcessAfter ()
		{
			return DESCRIPTOR_0;
		}
		
		@Override
		public PropertyQueue createQueue (QueueCollection qc)
		{
			return new PropertyQueue ((RGGGraph) qc.getGraph (), this, qc.getQueue (MAKE_PERSISTENT));
		}
	}


	public static final MakePersistentDescriptor MAKE_PERSISTENT = new MakePersistentDescriptor ();
	
	public static final class MakePersistentDescriptor extends QueueDescriptor<PropertyQueue>
	{
		@Override
		protected QueueDescriptor[] queuesToProcessBefore ()
		{
			return QueueDescriptor.DESCRIPTOR_0;
		}

		@Override
		protected QueueDescriptor[] queuesToProcessAfter ()
		{
			return new QueueDescriptor[] {GraphQueue.FIRST_QUEUE, PROPERTY_QUEUE};
		}
		
		@Override
		public PropertyQueue createQueue (QueueCollection qc)
		{
			return new PropertyQueue ((RGGGraph) qc.getGraph (), this, null);
		}
	}


	private final Runtime model;
	private final QueueDescriptor<?> descriptor;

	private final ObjectList<Node> newNodes = new ObjectList<Node> ();
	
	private final PropertyQueue makePersistentQueue;


	PropertyQueue (RGGGraph extent, QueueDescriptor<?> descr, PropertyQueue mpq)
	{
		super (extent.manager);
		this.model = (Runtime) extent.getModel ();
		this.descriptor = descr;
		this.makePersistentQueue = mpq;
	}

	
	public QueueDescriptor<?> getDescriptor ()
	{
		return descriptor;
	}
	
	void makePersistent (Node node)
	{
		manager.prepareId (node);
		newNodes.add (node);
	}
	

	static PropertyQueue current (Node node)
	{
		if (node.getGraph () == null)
		{
			throw new IllegalStateException (node + " is not yet part of a graph.");
		}
		RGGGraph g = RGGGraph.get (node.getGraph ());
		if (g == null)
		{
			throw new IllegalStateException (node.getGraph () + " is not yet represented as an RGGGraph");
		}
		QueueCollection qc = g.getQueues ();
		if (qc == null)
		{
			throw new IllegalStateException ("No active derivation, so no deferred assignments");
		}
		return qc.getQueue (PROPERTY_QUEUE);
	}


	public boolean process (int[] segments) throws RuntimeModelException
	{
		boolean modified = newNodes.size > 0;
		Transaction xa = manager.getActiveTransaction ();
		try
		{
			for (int i = 0; i < newNodes.size; i++)
			{
				Node n = newNodes.get (i);
				manager.makePersistent (n, n.getId (), xa);
			}
			newNodes.clear ();
			return apply (xa) || modified;
		}
		catch (IOException e)
		{
			throw new RuntimeModelException
				(de.grogra.util.Utils.unwrap (e), model);
		}
	}

	public boolean write (DataOutput out) throws IOException
	{
		for (int i = 0; i < newNodes.size; i++)
		{
			Node n = newNodes.get (i);
			makePersistent (n, n.getId ());
		}
		newNodes.clear ();
		getData ().write (out);
		return hasItems ();
	}

	public void read (DataInput in) throws IOException
	{
		Data d = createData ();
		d.read (in);
		restore (d);
	}

	private ObjectList<Cursor> segments = new ObjectList<Cursor> ();
	private int segmentsSize = 0;

	public void markSegment (int n)
	{
		while (segmentsSize <= n)
		{
			segments.set (segmentsSize, getCursor (segments.get (segmentsSize)));
			segmentsSize++;
		}
	}

	public void resetToSegment (int n)
	{
		moveTo (segments.get (n));
		segmentsSize = n + 1;
	}

	@Override
	public void clear ()
	{
		super.clear ();
		segmentsSize = 0;
	}

	public void clearSegmentsToExclude (int[] segs)
	{
	}

}
