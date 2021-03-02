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

import de.grogra.imp3d.objects.GRSVertex;
import de.grogra.xl.query.EdgePattern;
import de.grogra.xl.query.Graph;
import de.grogra.xl.query.Producer;
import de.grogra.xl.query.QueryState;

/**
 * 
 * @author Ole Kniemeyer
 */
public class VVProducer implements Producer
{
	public static NotGRSVertex operator$com (GRSVertex v)
	{
		return new NotGRSVertex (v);
	}

	public static final class NotGRSVertex
	{
		final GRSVertex vertex;

		NotGRSVertex (GRSVertex v)
		{
			vertex = v;
		}
	}

	GRSVertex firstVertex;
	GRSVertex secondVertex;

	private static final int REPLACE = 0;
	private static final int DELETE = 1;
	private static final int INSERT = 2;

	final End endProducer = new End ();

	public class End
	{
		public VVProducer producer$separate ()
		{
			return VVProducer.this;
		}

		public void producer$end ()
		{
		}
	}

	public InsertReplaceSplitSet operator$space (GRSVertex v)
	{
		firstVertex = v;
		return irssProducer;
	}

	public InsertReplaceDelete operator$space (NotGRSVertex v)
	{
		firstVertex = v.vertex;
		irdProducer.type = DELETE;
		return irdProducer;
	}

	final InsertReplaceSplitSet irssProducer = new InsertReplaceSplitSet ();

	public final class InsertReplaceSplitSet extends End
	{
		public InsertReplaceDelete operator$space (GRSVertex v)
		{
			secondVertex = v;
			irdProducer.type = INSERT;
			return irdProducer;
		}

		public InsertReplaceDelete operator$shr (GRSVertex v)
		{
			secondVertex = v;
			irdProducer.type = REPLACE;
			return irdProducer;
		}

		public Split operator$plusLeftArrow (GRSVertex v)
		{
			secondVertex = v;
			return splitProducer;
		}

		public AddNeighbor producer$push ()
		{
			nodeUsed (firstVertex);
			queue.setNeighbors (firstVertex);
			return addNbProducer;
		}

		public End producer$pop (AddNeighbor sn)
		{
			return endProducer;
		}
	}

	final InsertReplaceDelete irdProducer = new InsertReplaceDelete ();

	public final class InsertReplaceDelete
	{
		int type;

		public End operator$in (GRSVertex v)
		{
			switch (type)
			{
				case REPLACE:
					nodeUsed (firstVertex);
					nodeUsed (secondVertex);
					nodeUsed (v);
					queue.replace (secondVertex, firstVertex, v);
					break;
				case DELETE:
					nodeUsed (firstVertex);
					nodeUsed (v);
					queue.remove (firstVertex, v);
					break;
				default:
					nodeUsed (firstVertex);
					nodeUsed (secondVertex);
					nodeUsed (v);
					queue.makeFollower (firstVertex, secondVertex, v);
					break;
			}
			return endProducer;
		}
	}

	final Split splitProducer = new Split ();

	public final class Split
	{
		public End operator$plusArrow (GRSVertex v)
		{
			nodeUsed (firstVertex);
			nodeUsed (secondVertex);
			nodeUsed (v);
			queue.splitEdge (secondVertex, firstVertex, v);
			return endProducer;
		}
	}


	final AddNeighbor addNbProducer = new AddNeighbor ();

	public final class AddNeighbor
	{
		public AddNeighbor operator$space (GRSVertex v)
		{
			nodeUsed (v);
			queue.addNeighbor (v);
			return this;
		}

		public AddNeighbor producer$begin ()
		{
			return this;
		}

		public void producer$end ()
		{
		}
	}

	public VVProducer producer$begin ()
	{
		return this;
	}

	public void producer$end ()
	{
	}

	public boolean producer$beginExecution (int arrow)
	{
		return true;
	}

	public void producer$visitEdge (EdgePattern pattern)
	{
	}

	public void producer$endExecution (boolean applied)
	{
	}

	public Graph producer$getGraph ()
	{
		return qs.getGraph ();
	}

	void nodeUsed (GRSVertex v)
	{
		rggProducer.nodeUsed0 (v);
	}

	final RGGProducer rggProducer;
	final QueryState qs;
	final VVQueue queue;

	VVProducer (RGGProducer rggProducer)
	{
		this.rggProducer = rggProducer;
		this.qs = rggProducer.getQueryState0 ();
		this.queue = rggProducer.getQueue (VVQueue.DESCRIPTOR);
	}

}
