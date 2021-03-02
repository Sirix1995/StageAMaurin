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

package de.grogra.xl.impl.base;

import de.grogra.xl.lang.ObjectConsumer;
import de.grogra.xl.query.EdgeDirection;

/**
 * 
 * @author Ole Kniemeyer
 *
 * @param <N> node type
 */
public class Neighbors<N> implements Operator<N>
{
	protected final RuntimeModel model;
	protected final int mask;
	protected final int copy;
	protected final int add;
	protected final int addWhenMatching;
	protected final int addOrCopy;
	protected final boolean outgoing;
	//multiscale begin
	protected boolean excludeRefinement;
	//multiscale end
	
	public Neighbors (RuntimeModel model, int mask, int copy, int add, int addWhenMatching, boolean outgoing)
	{
		this.model = model;
		this.mask = mask;
		this.copy = copy;
		this.add = add;
		this.addWhenMatching = addWhenMatching;
		this.addOrCopy = RuntimeModel.edgeBitsUnion (copy, add);
		this.outgoing = outgoing;
	}
	
	//multiscale begin
	public Neighbors (RuntimeModel model, int mask, int copy, int add, int addWhenMatching, boolean outgoing, boolean excludeRefinement)
	{
		this.model = model;
		this.mask = mask;
		this.copy = copy;
		this.add = add;
		this.addWhenMatching = addWhenMatching;
		this.addOrCopy = RuntimeModel.edgeBitsUnion (copy, add);
		this.outgoing = outgoing;
		this.excludeRefinement = excludeRefinement;
	}
	//multiscale end

	public int match (N node, Operator<N> op, NodeEdgePair<N> opResult)
	{
		if (!RuntimeModel.testEdgeBits (opResult.edgeBits, addOrCopy))
		{
			return 0;
		}
		int b = outgoing ? model.getEdgeBits (opResult.node, node) : model.getEdgeBits (node, opResult.node);
		if (!RuntimeModel.testEdgeBits (b, mask))
		{
			return ONLY_CT_EDGES_MATCH;
		}
		b = RuntimeModel.edgeBitsUnion (RuntimeModel.edgeBitsIntersection (b, copy), add);
		int c = opResult.edgeBits;
		if (op instanceof Neighbors)
		{
			b = RuntimeModel.edgeBitsUnion (b, addWhenMatching);
			c = RuntimeModel.edgeBitsUnion (c, ((Neighbors) op).addWhenMatching);
		}
		b = RuntimeModel.edgeBitsIntersection (b, c);
		return (b != 0) ? b : ONLY_CT_EDGES_MATCH;
	}

	public int getUnilateralEdgeBits (N node, NodeEdgePair<N> opResult)
	{
		return opResult.edgeBits;
	}

	public void evaluateObject (ObjectConsumer<? super NodeEdgePair<N>> cons, N x)
	{
		NodeEdgePair<N> p = new NodeEdgePair<N> ();
		p.node = x;
		evaluate (cons, p);
	}

	public void evaluate (ObjectConsumer<? super NodeEdgePair<N>> cons, NodeEdgePair<N> p)
	{
		N node = p.node;
		EdgeIterator i = model.createEdgeIterator (node, EdgeDirection.UNDIRECTED);
		while (i.hasEdge ())
		{
			if (((i.source == node) == outgoing) && RuntimeModel.testEdgeBits (i.edgeBits, mask))
			{
				p.node = (N) (outgoing ? i.target : i.source);
				p.edgeBits = RuntimeModel.edgeBitsUnion (RuntimeModel.edgeBitsIntersection (i.edgeBits, copy), add);
				
				//multiscale begin
				if(excludeRefinement)
					p.edgeBits = RuntimeModel.edgeBitsRemove(p.edgeBits, model.REFINEMENT_EDGE);
				//multiscale end
				
				cons.consume (p);
			}
			i.moveToNext ();
		}
	}

}
