
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

import de.grogra.graph.ArrayPath;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.Instantiator;
import de.grogra.graph.Visitor;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.NURBSCurve;
import de.grogra.imp3d.objects.NURBSSurface;
import de.grogra.math.BSplineCurve;
import de.grogra.math.BSplineSurface;
import de.grogra.rgg.Reference;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

public class Instantiation
{
	public static final Instantiator INSTANTIATOR = new Instantiator ()
	{
		public boolean instantiate (ArrayPath path, Visitor v)
		{
			Node n = (Node) path.getNode (-1);
			if (n instanceof de.grogra.xl.modules.Instantiator)
			{
				Instantiation i = new Instantiation ();
				i.initialize (path, v);
				((de.grogra.xl.modules.Instantiator) n).instantiate (i);
				i.clear ();
			}
			return true;
		}
	};


	private ArrayPath path;
	private Visitor visitor;
	private GraphState gs;
	private IntList stack = new IntList ();
	private ObjectList retStack = new ObjectList ();
	private int stopCount;

	private int edges;
	private boolean instEdge;
	private Node node;
	private int nodeEdges;
	private long nodeIid;
	private long id;

	private int depth;
	
	private static final long BRANCH_INCREMENT = 0x100000000l;


	public void initialize (ArrayPath path, Visitor visitor)
	{
		this.path = path;
		this.visitor = visitor;
		this.gs = visitor.getGraphState ();
		stopCount = -1;
		id = 0;
		node = null;
		instEdge = true;
		depth = -1;
		pushImpl ();
	}

	public int getDepth ()
	{
		return depth;
	}

	public Instantiation operator$space (Node node)
	{
		instantiate (node);
		return this;
	}

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
		return value.resolveNode ();
	}

	private void pushImpl ()
	{
		instantiate ();
		stack.push (gs.getInstancingPathIndex ());
		stack.push ((int) id);
		id += BRANCH_INCREMENT;
		edges = Graph.BRANCH_EDGE;
	}

	public Instantiation producer$push ()
	{
		pushImpl ();
		depth--;
		return this;
	}

	public Instantiation producer$begin ()
	{
		depth++;
		return this;
	}

	public void producer$end ()
	{
		instantiate ();
		depth--;
	}

	private void popImpl ()
	{
		instantiate ();
		id = ((id - BRANCH_INCREMENT) & 0xffffffff00000000l) + stack.pop ();
		int n = stack.pop ();
		while (gs.getInstancingPathIndex () > n)
		{
			switch (stopCount)
			{
				case -1:
				case 0:
					visitor.visitLeave (retStack.pop (), path, true);
					// no break
				case 1:
					visitor.visitLeave (retStack.pop (), path, false);
					stopCount = -1;
					break;
				default:
					stopCount -= 2;
			}
			path.popNode ();
			path.popEdgeSet ();
			gs.deinstantiate ();
			gs.deinstantiate ();
		}
	}

	public Instantiation producer$pop (Object oldProducer)
	{
		depth++;
		popImpl ();
		return this;
	}

	public void clear ()
	{
		popImpl ();
	}


	public void instantiate (Node node)
	{
		if (node == null)
		{
			return;
		}
		instantiate ();
		this.node = node;
		this.nodeEdges = edges;
		this.nodeIid = id++;
		gs.instantiateEdge (nodeEdges, true, nodeIid);
		gs.instantiate (node, true, nodeIid);
		edges = Graph.SUCCESSOR_EDGE;
	}

	
	private void instantiate ()
	{
		Node n;
		while ((n = node) != null)
		{
			node = null;
			path.pushEdges (nodeEdges, true, nodeIid, instEdge);
			instEdge = false;
			path.pushNode (n, nodeIid);

			if (stopCount >= 0)
			{
				stopCount += 2;
			}
			else
			{
				Object p = visitor.visitEnter (path, false);
				retStack.push (p);
				if (p == Visitor.STOP)
				{
					stopCount = 1;
				}
				else
				{
					p = visitor.visitEnter (path, true);
					retStack.push (p);
					if (p == Visitor.STOP)
					{
						stopCount = 0;
					}
				}
				if (stopCount >= 0)
				{
					continue;
				}
				Instantiator i;
				if (n instanceof de.grogra.xl.modules.Instantiator)
				{
					((de.grogra.xl.modules.Instantiator) n).instantiate (this);
				}
				else if ((i = n.getInstantiator ()) != null)
				{
					Object ie;
					boolean b = true;
					if ((ie = visitor.visitInstanceEnter ()) != Visitor.STOP)
					{
						try
						{
							gs.beginInstancing (n, path.getObjectId (-1));
							b = i.instantiate (path, visitor);
						}
						finally
						{
							gs.endInstancing ();
						}
					}
					if (!(b & visitor.visitInstanceLeave (ie)))
					{
						continue;
					}
				}
				for (Edge e = n.getFirstEdge (); e != null; e = e.getNext (n))
				{
					Node t = e.getTarget ();
					if ((t != n) && e.testEdgeBits (Graph.BRANCH_EDGE
													| Graph.SUCCESSOR_EDGE))
					{
						pushImpl ();
						instantiate (t);
						popImpl ();
					}
				}
			}
		}
	}
	
	
	public GraphState getGraphState ()
	{
		return gs;
	}

}
