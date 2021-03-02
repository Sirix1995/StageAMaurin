
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

import de.grogra.xl.util.ObjectList;

public class ParentAttribute extends ObjectAttribute
	implements ChangeBoundaryListener, EdgeChangeListener
{
	public static final ParentAttribute TREE
		= new ParentAttribute (EdgePatternImpl.TREE, "tree");

	private final EdgePattern pattern;


	public ParentAttribute (EdgePattern pattern, String name)
	{
		super (de.grogra.reflect.Type.OBJECT, false, null);
		initializeName (name);
		this.pattern = pattern;
	}


	@Override
	public boolean isDerived ()
	{
		return true;
	}

	
	public boolean contains (Object object, boolean asNode, GraphState gs)
	{
		if (object == null)
		{
			return false;
		}
		getDerived (gs.getGraph ().getRoot (Graph.MAIN_GRAPH), true, null, gs);
		return ((BooleanMap) ((Object[]) getAttributeState (gs))[0])
			.getBoolean (object, asNode);
	}


	@Override
	protected Object getDerived (Object object, boolean asNode, Object placeIn,
								 GraphState gs)
	{
		Object[] a;
		synchronized (this)
		{
			if ((a = (Object[]) getAttributeState (gs)) == null)
			{
				a = new Object[] {gs.getGraph ().createBooleanMap (),
								  gs.getGraph ().createObjectMap (),
								  new ObjectList (32)};
				setAttributeState (gs, a);
				gs.getGraph ().addChangeBoundaryListener (this);
				gs.getGraph ().addEdgeChangeListener (this);
			}
		}
		synchronized (a)
		{
			BooleanMap valid = (BooleanMap) a[0];
			if (valid.getBoolean (object, asNode))
			{
				return ((ObjectMap) a[1]).getObject (object, asNode);
			}
			if (!valid.getBoolean (gs.getGraph ().getRoot (Graph.MAIN_GRAPH), true))
			{
				recalculate (gs.getGraph ().getRoot (Graph.MAIN_GRAPH), null,
							 gs.getGraph (), (BooleanMap) a[0], (ObjectMap) a[1], gs.treeAttributeStack);
			}
			if (!valid.getBoolean (object, asNode))
			{
				return null;
			}
			return ((ObjectMap) a[1]).getObject (object, asNode);
		}
	}

/*
	public void dispose (GraphState gs)
	{
		Object[] a = (Object[]) getAttributeObject (gs);
		if (a != null)
		{
			gs.removeChangeBoundaryListener (this);
			gs.removeEdgeChangeListener (this);
			((BooleanMap) a[0]).dispose ();
			((ObjectMap) a[1]).dispose ();
			setAttributeObject (gs, null);
		}
	}
*/

	private void recalculate (Object node, Object parentEdge, Graph g,
							  BooleanMap valid, ObjectMap attr, ObjectList stack)
	{
		int stackStart = stack.size;
		Object e = null;
		boolean gotoReturn = false;

		try
		{
		loop:
			while (true)
			{
				if (!gotoReturn)
				{
					if (valid.putBoolean (node, true, true))
					{
						throw new GraphException ("Node " + node + " has more than one tree path to root.");
					}
					attr.putObject (node, true, parentEdge);
					e = g.getFirstEdge (node);
				}
				for (; e != null; e = g.getNextEdge (e, node))
				{
					if (!gotoReturn && (e != parentEdge))
					{
						Object t = g.getTargetNode (e);
						boolean toTarget;
						if (!(toTarget = (t != node)))
						{
							t = g.getSourceNode (e);
						}
						if (GraphUtils.matches (pattern, g, e, toTarget))
						{
							valid.putBoolean (e, false, true);
							attr.putObject (e, false, node);
							stack.push (node).push (parentEdge).push (e);
							node = t;
							parentEdge = e;
							continue loop;
	//						recalculate (t, e, g, valid, attr);
						}
					}
					gotoReturn = false;
				}
				if (stack.size == stackStart)
				{
					return;
				}
				e = stack.pop ();
				parentEdge = stack.pop ();
				node = stack.pop ();
				gotoReturn = true;
			}
		}
		finally
		{
			stack.setSize (stackStart);
		}
	}


	public void beginChange (GraphState gs)
	{
		Object[] a = (Object[]) getAttributeState (gs);
		((ObjectList) a[2]).clear ();
	}


	public void endChange (GraphState gs)
	{
		Object[] a = (Object[]) getAttributeState (gs);
		synchronized (a)
		{
			BooleanMap valid = (BooleanMap) a[0];
			ObjectMap parent = (ObjectMap) a[1];
			Graph g = gs.getGraph ();
			ObjectList added = (ObjectList) a[2];
			a = added.elements;
			int i = added.size;
			while (i > 0)
			{
				Object c = a[--i], p = a[--i];
				boolean toTarget = a[--i] == null;
				if (valid.getBoolean (p, true))
				{
					for (Object e = g.getFirstEdge (p); e != null;
						 e = g.getNextEdge (e, p))
					{
						if (toTarget
							? ((g.getTargetNode (e) == c)
							   && pattern.matches (p, c, g.getEdgeBits (e), true))
							: ((g.getSourceNode (e) == c)
							   && pattern.matches (c, p, g.getEdgeBits (e), false)))
						{
							valid.putBoolean (e, false, true);
							parent.putObject (e, false, p);
							if (!valid.getBoolean (c, true))
							{
								recalculate (c, e, g, valid, parent, gs.treeAttributeStack);
							}
							break;
						}
					}
				}
			}
			added.clear ();
		}
	}/*
		check (gs.getRoot (), null, gs.getGraph (), valid, parent);
	}


	private void check (Object node, Object parentEdge, Graph g,
						BooleanMap valid, ObjectMap attr)
	{
		try
		{
			if (!valid.getBoolean (node, true))
			{
				throw new AssertionError ("Node " + node + " has no valid parent");
			}
			if (attr.getObject (node, true) != parentEdge)
			{
				throw new AssertionError ("Parents of node " + node + " differ: "
										  + parentEdge + " " + attr.getObject (node, true)); 
			}
			for (Object e = g.getFirstEdge (node); e != null; 
				 e = g.getNextEdge (e, node))
			{
				if (e != parentEdge)
				{
					Object t = g.getTargetNode (e);
					boolean toTarget;
					if (!(toTarget = (t != node)))
					{
						t = g.getSourceNode (e);
					}
					if (GraphUtils.matches (pattern, g, e, toTarget))
					{
						if (!valid.getBoolean (e, false))
						{
							throw new AssertionError ("Edge " + e + " has no valid parent");
						}
						if (attr.getObject (e, false) != node)
						{
							throw new AssertionError ("Parents of edge " + e + " differ: "
													  + node + " " + attr.getObject (e, false)); 
						}
						check (t, e, g, valid, attr);
					}
				}
			}
		}
		catch (AssertionError e)
		{
			System.err.println (node);
			throw e;
		}
	}
//*/

	public int getPriority ()
	{
		return TOPOLOGY_PRIORITY;
	}


	public void edgeChanged (Object source, Object target, Object edgeSet,
							 GraphState gs)
	{
		Object[] a = (Object[]) getAttributeState (gs);
		BooleanMap valid = (BooleanMap) a[0];
		ObjectMap parent = (ObjectMap) a[1];
		Object e;
		if (((edgeSet == null)
			 || !pattern.matches (source, target, gs.getGraph ().getEdgeBits (edgeSet), true))
			&& valid.getBoolean (target, true)
			&& ((e = parent.getObject (target, true)) != null)
			&& (source == parent.getObject (e, false)))
		{
			invalidateChildren (target, e, valid, parent, gs, gs.treeAttributeStack);
		}
		else if (((edgeSet == null)
				  || !pattern.matches (source, target, gs.getGraph ().getEdgeBits (edgeSet), false))
				 && valid.getBoolean (source, true)
				 && ((e = parent.getObject (source, true)) != null)
				 && (target == parent.getObject (e, false)))
		{
			invalidateChildren (source, e, valid, parent, gs, gs.treeAttributeStack);
		}
		if (edgeSet != null)
		{
			if (GraphUtils.matches (pattern, gs.getGraph (), edgeSet, true)
				&& valid.getBoolean (source, true))
			{
				((ObjectList) a[2]).push (null).push (source).push (target);
			}
			else if (GraphUtils.matches (pattern, gs.getGraph (), edgeSet, false)
					 && valid.getBoolean (target, true))
			{
				((ObjectList) a[2]).push (this).push (target).push (source);
			}
		}
	}


	private void invalidateChildren
		(Object node, Object parentEdge, BooleanMap valid,
		 ObjectMap parent, GraphState gs, ObjectList stack)
	{
		Graph g = gs.getGraph ();
		int stackStart = stack.size;
		Object e = null;
		boolean gotoReturn = false;
		
		try
		{
		loop:
			while (true)
			{
				if (!gotoReturn)
				{
					valid.putBoolean (parentEdge, false, false);
					gs.fireAttributeChanged (parentEdge, false, this, null, null);
					valid.putBoolean (node, true, false);
					gs.fireAttributeChanged (node, true, this, null, null);
					e = g.getFirstEdge (node);
				}
				for (; e != null; e = g.getNextEdge (e, node))
				{
					if (!gotoReturn && (e != parentEdge))
					{
						Object c = g.getTargetNode (e);
						if (c == node)
						{
							c = g.getSourceNode (e);
						}
						if (valid.getBoolean (c, true))
						{
							Object ce = parent.getObject (c, true);
							if (valid.getBoolean (ce, false)
								&& (parent.getObject (ce, false) == node))
							{
								stack.push (node).push (parentEdge).push (e);
								node = c;
								parentEdge = ce;
								continue loop;
	//							invalidateChildren (c, ce, valid, parent, gs);
							}
						}
					}
					gotoReturn = false;
				}
				if (stack.size == stackStart)
				{
					return;
				}
				e = stack.pop ();
				parentEdge = stack.pop ();
				node = stack.pop ();
				gotoReturn = true;
			}
		}
		finally
		{
			stack.setSize (stackStart);
		}
	}

}
