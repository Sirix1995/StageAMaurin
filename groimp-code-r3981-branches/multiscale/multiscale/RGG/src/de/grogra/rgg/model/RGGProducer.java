
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

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.NURBSCurve;
import de.grogra.imp3d.objects.NURBSSurface;
import de.grogra.math.BSplineCurve;
import de.grogra.math.BSplineSurface;
import de.grogra.reflect.TypeId;
import de.grogra.rgg.AxiomType;
import de.grogra.rgg.BooleanNode;
import de.grogra.rgg.ByteNode;
import de.grogra.rgg.CharNode;
import de.grogra.rgg.DoubleNode;
import de.grogra.rgg.FloatNode;
import de.grogra.rgg.IntNode;
import de.grogra.rgg.LongNode;
import de.grogra.rgg.ObjectNode;
import de.grogra.rgg.RGGRoot;
import de.grogra.rgg.Reference;
import de.grogra.rgg.ShortNode;
import de.grogra.xl.impl.base.Producer;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.EdgePattern;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.util.Operators;

public class RGGProducer extends Producer
{
	private boolean probabilitySet;
	private double probability;

	public RGGProducer (QueryState match)
	{
		super (match);
	}

	RGGProducer (Runtime model)
	{
		super (model);
	}

	public static final class Creator
	{
		public RGGProducer producer$begin ()
		{
			return new RGGProducer (Runtime.INSTANCE);
		}
	}

	private VVProducer vvProd;

	public VVProducer vv ()
	{
		if (vvProd == null)
		{
			vvProd = new VVProducer (this);
		}
		return vvProd;
	}

	void nodeUsed0 (Node node)
	{
		nodeUsed (node);
	}

	QueryState getQueryState0 ()
	{
		return getQueryState ();
	}

	public boolean ruleProbability (double p)
	{
		if (!probabilitySet)
		{
			QueryState qs = getQueryState ();
			probability = Operators.getRandomGenerator ().nextDouble ();
			probabilitySet = true;
		}
		if (p > 0)
		{
			probability -= p;
		}
		return probability <= 0;
	}

	@Override
	public boolean producer$beginExecution (int arrow)
	{
		probabilitySet = false;
		return super.producer$beginExecution (arrow);
	}


	@Override
	public void producer$endExecution (boolean applied)
	{
		super.producer$endExecution (applied);
		QueryState qs = getQueryState ();
		if (applied && (qs != null))
		{
			((RGGGraph) qs.getGraph ()).productionStateEnded (this);
		}
	}

	public RGGProducer producer$getProducer ()
	{
		return this;
	}
	
	public Node producer$getLeftmostMatch ()
	{
		Object o = getQueryState ().getInValue ();
		return (o instanceof Node) ? (Node) o : null;
	}
	
	public Node producer$getRoot ()
	{
		QueryState qs = getQueryState ();
		return (qs != null) ? RGGRoot.getRoot (((RGGGraph) qs.getGraph ()).getGraphManager ()) : null;
	}

	public VVProducer operator$space (VVProducer prod)
	{
		return prod;
	}

	/**
	 * space operator with a succeeding node
	 * 
	 * E.g.
	 * ... ==> x W
	 * 
	 * tmp1.operator$space(x).operator$space(new W());
	 * where tmp1 is a Producer instance.
	 * 
	 * @param node
	 * @return
	 */
	public RGGProducer operator$space (Node node)
	{
		addNodeImpl (node, true, Graph.SUCCESSOR_EDGE, EdgeDirection.BACKWARD);
		return this;
	}

	/**
	 * '[' operator
	 * 
	 * @return
	 */
	public RGGProducer producer$push ()
	{
		pushImpl ();
		return this;
	}

	public RGGProducer producer$begin ()
	{
		return this;
	}

	public void producer$end ()
	{
	}

	/**
	 * ',' operator
	 * 
	 * @return
	 */
	public RGGProducer producer$separate ()
	{
		separateImpl ();
		return this;
	}

	/**
	 * ']' operator
	 * 
	 * @return
	 */
	public RGGProducer producer$pop (Object oldProducer)
	{
		popImpl ();
		return this;
	}

	/**
	 * arrow operator for an edge with a succeeding node
	 * 
	 * E.g.
	 * ... ==> x -e-> W
	 * 
	 * tmp1.operator$space(x).operator$arrow(new W(),e);
	 * where tmp1 is a Producer instance.
	 * 
	 * @param node
	 * @return
	 */
	public RGGProducer operator$arrow (Node node, int edge)
	{
		addEdgeImpl (getPreviousNode (), node, edge, EdgeDirection.FORWARD);
		addNodeImpl (node, false, edge, EdgeDirection.FORWARD);
		return this;
	}

	public RGGProducer operator$arrow (Node node, Node edge)
	{
		return operator$arrow (edge, Graph.EDGENODE_IN_EDGE).operator$arrow (node, Graph.EDGENODE_OUT_EDGE);
	}

	/**
	 * arrow operator for an edge with a succeeding node
	 * 
	 * E.g.
	 * ... ==> x <-e- W
	 * 
	 * tmp1.operator$space(x).operator$arrow(new W(),e);
	 * where tmp1 is a Producer instance.
	 * 
	 * @param node
	 * @return
	 */
	public RGGProducer operator$leftArrow (Node node, int edge)
	{
		addEdgeImpl (getPreviousNode (), node, edge, EdgeDirection.BACKWARD);
		addNodeImpl (node, false, edge, EdgeDirection.BACKWARD);
		return this;
	}

	public RGGProducer operator$leftArrow (Node node, Node edge)
	{
		return operator$leftArrow (edge, Graph.EDGENODE_OUT_EDGE).operator$leftArrow (node, Graph.EDGENODE_IN_EDGE);
	}

	public RGGProducer operator$sub (Node node, int edge)
	{
		addEdgeImpl (getPreviousNode (), node, edge, EdgeDirection.UNDIRECTED);
		addNodeImpl (node, false, edge, EdgeDirection.UNDIRECTED);
		return this;
	}

	public RGGProducer operator$sub (Node node, Node edge)
	{
		return operator$arrow (node, edge);
	}

	public RGGProducer operator$xLeftRightArrow (Node node, int edge)
	{
		addEdgeImpl (getPreviousNode (), node, edge, EdgeDirection.BOTH);
		addNodeImpl (node, false, edge, EdgeDirection.BOTH);
		return this;
	}

	public RGGProducer operator$xLeftRightArrow (Node node, Node edge)
	{
		Node p = (Node) getPreviousNode ();
		operator$arrow (node, edge);
		addEdgeImpl (node, edge, Graph.EDGENODE_IN_EDGE, EdgeDirection.FORWARD);
		addEdgeImpl (edge, p, Graph.EDGENODE_OUT_EDGE, EdgeDirection.FORWARD);
		return this;
	}

	/**
	 * '>' operator
	 * 
	 * @param node
	 * @return
	 */
	public RGGProducer operator$gt (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, Graph.SUCCESSOR_EDGE, EdgeDirection.FORWARD);
		addNodeImpl (node, false, Graph.SUCCESSOR_EDGE, EdgeDirection.FORWARD);
		return this;
	}

	/**
	 * '<' operator
	 * 
	 * @param node
	 * @return
	 */
	public RGGProducer operator$lt (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, Graph.SUCCESSOR_EDGE, EdgeDirection.BACKWARD);
		addNodeImpl (node, false, Graph.SUCCESSOR_EDGE, EdgeDirection.BACKWARD);
		return this;
	}

	public RGGProducer operator$line (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, Graph.SUCCESSOR_EDGE, EdgeDirection.UNDIRECTED);
		addNodeImpl (node, false, Graph.SUCCESSOR_EDGE, EdgeDirection.UNDIRECTED);
		return this;
	}

	public RGGProducer operator$leftRightArrow (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, Graph.SUCCESSOR_EDGE, EdgeDirection.BOTH);
		addNodeImpl (node, false, Graph.SUCCESSOR_EDGE, EdgeDirection.BOTH);
		return this;
	}


	/**
	 * '+>' operator
	 * 
	 * @param node
	 * @return
	 */
	public RGGProducer operator$plusArrow (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, Graph.BRANCH_EDGE, EdgeDirection.FORWARD);
		addNodeImpl (node, false, Graph.BRANCH_EDGE, EdgeDirection.FORWARD);
		return this;
	}

	/**
	 * '<+' operator
	 * 
	 * @param node
	 * @return
	 */
	public RGGProducer operator$plusLeftArrow (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, Graph.BRANCH_EDGE, EdgeDirection.BACKWARD);
		addNodeImpl (node, false, Graph.BRANCH_EDGE, EdgeDirection.BACKWARD);
		return this;
	}

	public RGGProducer operator$plusLine (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, Graph.BRANCH_EDGE, EdgeDirection.UNDIRECTED);
		addNodeImpl (node, false, Graph.BRANCH_EDGE, EdgeDirection.UNDIRECTED);
		return this;
	}

	public RGGProducer operator$plusLeftRightArrow (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, Graph.BRANCH_EDGE, EdgeDirection.BOTH);
		addNodeImpl (node, false, Graph.BRANCH_EDGE, EdgeDirection.BOTH);
		return this;
	}

	/**
	 * '/>' operator
	 * 
	 * @param node
	 * @return
	 */
	public RGGProducer operator$slashArrow (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, Graph.REFINEMENT_EDGE, EdgeDirection.FORWARD);
		addNodeImpl (node, false);
		return this;
	}

	/**
	 * '</' operator
	 * 
	 * @param node
	 * @return
	 */
	public RGGProducer operator$slashLeftArrow (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, Graph.REFINEMENT_EDGE, EdgeDirection.BACKWARD);
		addNodeImpl (node, false);
		return this;
	}

	public RGGProducer operator$slashLine (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, Graph.REFINEMENT_EDGE, EdgeDirection.UNDIRECTED);
		addNodeImpl (node, false);
		return this;
	}

	public RGGProducer operator$slashLeftRightArrow (Node node)
	{
		addEdgeImpl (getPreviousNode (), node, Graph.REFINEMENT_EDGE, EdgeDirection.BOTH);
		addNodeImpl (node, false);
		return this;
	}


	public void producer$visitEdge (EdgePattern pattern)
	{
		int mi = pattern.getMatchIndex ();
		switch (pattern.getParameterType (mi).getTypeId ())
		{
			case TypeId.INT:
				super.producer$visitEdge (pattern);
				break;
			case TypeId.OBJECT:
				Object e = getQueryState ().abound (mi);
				if (e instanceof Node)
				{
					deleteNodeQueue.deleteNode (e);
				}
				break;
		}
	}
	

/*!!
#foreach ($type in $types)
$pp.setType($type)

	public static Node toNode ($type value)
	{
		return new ${pp.Type}Node (value);
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public static Node toNode (boolean value)
	{
		return new BooleanNode (value);
	}
// generated
// generated
// generated
	public static Node toNode (byte value)
	{
		return new ByteNode (value);
	}
// generated
// generated
// generated
	public static Node toNode (short value)
	{
		return new ShortNode (value);
	}
// generated
// generated
// generated
	public static Node toNode (char value)
	{
		return new CharNode (value);
	}
// generated
// generated
// generated
	public static Node toNode (int value)
	{
		return new IntNode (value);
	}
// generated
// generated
// generated
	public static Node toNode (long value)
	{
		return new LongNode (value);
	}
// generated
// generated
// generated
	public static Node toNode (float value)
	{
		return new FloatNode (value);
	}
// generated
// generated
// generated
	public static Node toNode (double value)
	{
		return new DoubleNode (value);
	}
// generated
// generated
// generated
	public static Node toNode (Object value)
	{
		return new ObjectNode (value);
	}
// generated
//!! *# End of generated code

	public static Node toNode (BSplineCurve value)
	{
		return new NURBSCurve (value);
	}

	public static Node toNode (BSplineSurface value)
	{
		return new NURBSSurface (value);
	}

	public static Node toNode (Reference value)
	{
		return value.resolve ();
	}

}
