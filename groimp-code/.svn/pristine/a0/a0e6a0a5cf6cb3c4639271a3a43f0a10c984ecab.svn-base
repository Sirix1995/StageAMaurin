
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

package de.grogra.graph;

import de.grogra.util.I18NBundle;
import de.grogra.xl.util.ObjectList;

public final class GraphUtils
{
	public static final I18NBundle I18N
		= I18NBundle.getInstance (GraphUtils.class);


	private GraphUtils ()
	{
	}


	public static boolean nodesEqual (Path p, Path q)
	{
		if (p == q)
		{
			return true;
		}
		int i;
		if ((p == null) || (q == null)
			|| ((i = p.getNodeAndEdgeCount ()) != q.getNodeAndEdgeCount ()))
		{
			return false;
		}
		i = (i - 1) & ~1;
		while (i >= 0)
		{
			if (p.getObject (i) != q.getObject (i))
			{
				return false;
			}
			i -= 2;
		}
		return true;
	}


	public static boolean equal (Path p, Path q)
	{
		if (p == q)
		{
			return true;
		}
		int i;
		if ((p == null) || (q == null)
			|| ((i = p.getNodeAndEdgeCount ()) != q.getNodeAndEdgeCount ()))
		{
			return false;
		}
		while (--i >= 0)
		{
			if (p.getObject (i) != q.getObject (i))
			{
				return false;
			}
		}
		return true;
	}


	public static boolean matches (EdgePattern p, Graph g, Object edgeSet, boolean toTarget)
	{
		return p.matches (g.getSourceNode (edgeSet), g.getTargetNode (edgeSet),
						  g.getEdgeBits (edgeSet), toTarget);
	}


	public static boolean matchesTerminalEdge (EdgePattern p, Path path)
	{
		return p.matches (path.getObject (-3),
						  path.getObject (-1),
						  path.getEdgeBits (-2), path.isInEdgeDirection (-2));
	}


	public static Path getTreePath (GraphState gs, Object obj, boolean node)
	{
		ObjectAttribute pa = gs.getGraph ().getParentAttribute ();

		ObjectList list = new ObjectList ();
		
		while (obj != null)
		{
			list.push (obj);
			obj = pa.getDerived (obj, node, null, gs);
			node = !node;
		}
		if (node)
		{
			return null;
		}
		
		ArrayPath p = new ArrayPath (gs.getGraph ());
		node = true;
		while (!list.isEmpty ())
		{
			if (node)
			{
				obj = list.pop ();
				p.pushNode (obj, gs.getGraph ().getId (obj));
			}
			else
			{
				p.pushEdgeSet (list.pop (), -1, false);
			}
			node = !node;
		}
		return p;
	}


	public static final boolean testEdgeBits (int edgeBits, int mask)
	{
		return (mask == -1) ? (edgeBits != 0)
			: ((((mask & edgeBits) & ~Graph.SPECIAL_EDGE_MASK) != 0)
			   || (((mask & Graph.SPECIAL_EDGE_MASK) != 0)
				   && (((mask ^ edgeBits) & Graph.SPECIAL_EDGE_MASK) == 0)));
	}


	public static void acceptPath (final Path pathToFollow,
								   final Visitor visitor,
								   ArrayPath placeInPath)
	{
		final int count = pathToFollow.getNodeAndEdgeCount ();
		if (count <= 0)
		{
			return;
		}
		visitor.getGraphState ().getGraph ().accept
			(pathToFollow.getObject (0), new Visitor ()
			{
				private int index = 0;


				public GraphState getGraphState ()
				{
					return visitor.getGraphState ();
				}


				private boolean stopped;

				public Object visitEnter (Path path, boolean node)
				{
					if (node)
					{
						if ((index >= count)
							|| (path.getObjectId (-1) != pathToFollow.getObjectId (index)))
						{
							stopped = true;
							return STOP;
						}
						index++;
						return visitor.visitEnter (path, node);
					}
					else
					{
						if (index >= count)
						{
							return STOP;
						}
						int e;
						int pe;
						Object o;
						if ((path.isInEdgeDirection (-2) != pathToFollow.isInEdgeDirection (index))
							|| (path.getObjectId (-2) != pathToFollow.getObjectId (index))
							|| (((e = pathToFollow.getEdgeBits (index)) != (pe = path.getEdgeBits (-2)))
								&& (((e & pe) & ~Graph.SPECIAL_EDGE_MASK) == 0)
								&& (((e & Graph.SPECIAL_EDGE_MASK) == 0) || ((e ^ pe) & Graph.SPECIAL_EDGE_MASK) != 0))
							|| ((index + 1 < count)
								? (path.getObjectId (-1)
								   != pathToFollow.getObjectId (index + 1))
								: (((o = path.getObject (-2)) != null)
								   && (o != pathToFollow.getObject (index)))))
						{
							stopped = true;
							return STOP;
						}
						index++;
						return visitor.visitEnter (path, node);
					}
				}


				public boolean visitLeave (Object o, Path path, boolean node)
				{
					if (node)
					{
						if (stopped)
						{
							stopped = false;
							return true;
						}
						else
						{
							index--;
							visitor.visitLeave (o, path, node);
							return false;
						}
					}
					else
					{
						if (index >= count)
						{
							return false;
						}
						if (stopped)
						{
							stopped = false;
						}
						else
						{
							index--;
							visitor.visitLeave (o, path, node);
						}
						return true;
					}
				}


				public Object visitInstanceEnter ()
				{
					if (index >= count)
					{
						return STOP;
					}
					if (!pathToFollow.isInstancingEdge (index))
					{
						stopped = true;
						return STOP;
					}
					return visitor.visitInstanceEnter ();
				}


				public boolean visitInstanceLeave (Object o)
				{
					if (index >= count)
					{
						return false;
					}
					if (stopped)
					{
						stopped = false;
					}
					else
					{
						visitor.visitInstanceLeave (o);
					}
					return true;
				}
			}, placeInPath);
	}

	
	public static int lastIndexOfGraph (Path path, Graph graph)
	{
		if ((path == null) || (path.getGraph () != graph))
		{
			return -1;
		}
		for (int i = path.getNodeAndEdgeCount () - 1; i >= 0; i--)
		{
			if (graph.getLifeCycleState (path.getObject (i), (i & 1) == 0) == Graph.PERSISTENT)
			{
				return i;
			}
		}
		return -1;
	}

	
	public static int lastIndexOfTree (Path path, GraphState gs)
	{
		if ((path == null) || (path.getGraph () != gs.getGraph ()))
		{
			return -1;
		}
		for (int i = path.getNodeAndEdgeCount () - 1; i >= 0; i--)
		{
			if (gs.containsInTree (path.getObject (i), (i & 1) == 0))
			{
				return i;
			}
		}
		return -1;
	}


	public static Path cutToGraph (Path path, Graph graph)
	{
		if ((path == null) || (path.getGraph () != graph))
		{
			return null;
		}
		int n = path.getNodeAndEdgeCount ();
		for (int i = 0; i < n; i++)
		{
			if (graph.getLifeCycleState (path.getObject (i), (i & 1) == 0) != Graph.PERSISTENT)
			{
				ArrayPath p = new ArrayPath (path);
				while (--n >= i)
				{
					if ((n & 1) == 0)
					{
						p.popNode ();
					}
					else
					{
						p.popEdgeSet ();
					}
				}	
				return p;
			}
		}
		return path;
	}


	public static Object getEdge (Graph graph, Object source, Object target,
									 EdgePattern pattern, boolean ignoreDirection)
	{
		for (Object e = graph.getFirstEdge (source); e != null;
			 e = graph.getNextEdge (e, source))
		{
			Object s = graph.getSourceNode (e);
			if (s == source)
			{
				if (graph.getTargetNode (e) == target)
				{
					int bits = graph.getEdgeBits (e);
					return (pattern.matches (source, target, bits, true)
							|| pattern.matches (source, target, bits, false))
						? e : null;
				}
			}
			else if (ignoreDirection && (s == target))
			{
				int bits = graph.getEdgeBits (e);
				return (pattern.matches (target, source, bits, true)
						|| pattern.matches (target, source, bits, false))
					? e : null;
			}
		}
		return null;
	}

	
	public static Object getFirstIncomingNode (Graph graph, Object node)
	{
		for (Object e = graph.getFirstEdge (node); e != null;
			 e = graph.getNextEdge (e, node))
		{
			Object t = graph.getSourceNode (e);
			if (t != node)
			{
				return t;
			}
		}
		return null;
	}
	
	
	public static Object getFirstOutgoingNode (Graph graph, Object node)
	{
		for (Object e = graph.getFirstEdge (node); e != null;
			 e = graph.getNextEdge (e, node))
		{
			Object t = graph.getTargetNode (e);
			if (t != node)
			{
				return t;
			}
		}
		return null;
	}
	
}
