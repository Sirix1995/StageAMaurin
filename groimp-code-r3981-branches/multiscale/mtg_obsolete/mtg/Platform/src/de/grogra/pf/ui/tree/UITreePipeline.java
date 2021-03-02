
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

package de.grogra.pf.ui.tree;

import java.util.EventObject;
import javax.swing.tree.*;
import javax.swing.event.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.registry.*;
import de.grogra.pf.ui.event.InputEditEvent;
import de.grogra.util.*;
import de.grogra.xl.lang.ObjectToBoolean;
import de.grogra.xl.util.ObjectList;

public class UITreePipeline extends MutableTree
	implements UITree, TreeModelListener, RegistryContext
{
	public final class Node extends MutableTree.Node implements EventListener
	{
		public Object userData;

		UINodeHandler handler;
		TreeDiff.DiffInfo diffInfo;
		ChoiceGroup group;

		private Object node;


		public Node (UINodeHandler handler, Object node)
		{
			this.handler = handler;
			this.node = node;
			setListeners (node, true);
		}

		
		private void setListeners (Object node, boolean add)
		{
			if (node instanceof Item)
			{
				for (Item i = (Item) ((Item) node).getBranch ();
					 i != null; i = (Item) i.getSuccessor ())
				{
					if (i instanceof UIPropertyDependency)
					{
						UIProperty p = ((UIPropertyDependency) i).getProperty ();
						if (add)
						{
							p.addPropertyListener (getContext (), this);
						}
						else
						{
							p.removePropertyListener (getContext (), this);
						}
					}
				}
			}
		}


		public void eventOccured (EventObject event)
		{
			valueForPathChanged (getTreePath (), null);
		}


		TreeDiff.DiffInfo getDiffInfo ()
		{
			if (diffInfo == null)
			{
				diffInfo = new TreeDiff.DiffInfo (this);		
			}
			return diffInfo;
		}


		public void dispose ()
		{
			for (Node n = (Node) children; n != null; n = (Node) n.next)
			{
				n.dispose ();
			}
			children = null;
			parent = null;
			setListeners (node, false);
		}


		@Override
		public boolean equals (Object o)
		{
			return (o == this)
				|| ((o instanceof Node)
					&& handler.nodesEqual (node, ((Node) o).node));
		}


		public int getType ()
		{
			return (group != null) ? NT_CHOICE_ITEM
				: handler.getType (node);		
		}


		public String getName ()
		{
			return handler.getName (node);		
		}


		public UITreePipeline getPipeline ()
		{
			return UITreePipeline.this;
		}


		public UINodeHandler getNodeHandler ()
		{
			return handler;
		}


		public Object getNode ()
		{
			return node;
		}


		public boolean isLeaf ()
		{
			return handler.isLeaf (node);
		}


		public Object invoke (String method, Object arg)
		{
			return (group != null) && "getChoiceGroup".equals (method)
				? group
				: handler.invoke (node, method, arg);
		}


		void handleEvent (EventObject event)
		{
			if (group != null)
			{
				group.setSelected (getContext (), handler, node);
				UI.consume (event);
			}
			else
			{
				handler.eventOccured (node, event);
			}
		}


		public Node getChild (String name)
		{
			for (Node n = (Node) children; n != null; n = (Node) n.next)
			{
				if (name.equals (n.getName ()))
				{
					return n;
				}
			}
			return null;
		}


		@Override
		public String toString ()
		{
			return "Node@" + Integer.toHexString (hashCode ())
				+ '[' + node + ']';
		}
	}


	public interface Transformer extends Disposable
	{
		void initialize (UITreePipeline pipeline);

		void transform (UITreePipeline.Node root);

		boolean isAffectedBy (TreePath path);
	}


	private final class Diff extends TreeDiff
		implements TreeDiff.NodeModel, TreeModelListener
	{
		public boolean equals (Object a, Object b)
		{
			return a.equals (b);
		}


		public TreeDiff.DiffInfo getDiffInfo (Object node)
		{
			return ((Node) node).getDiffInfo ();
		}


		public void treeNodesChanged (TreeModelEvent e)
		{
			fireTreeModelEvent (NODES_CHANGED, e);
		}


		public void treeNodesInserted (TreeModelEvent e)
		{
			fireTreeModelEvent (NODES_INSERTED, e);
		}


		public void treeNodesRemoved (TreeModelEvent e)
		{
			fireTreeModelEvent (NODES_REMOVED, e);
		}


		public void treeStructureChanged (TreeModelEvent e)
		{
			fireTreeModelEvent (STRUCTURE_CHANGED, e);
		}
	}


	private final ObjectList transformers = new ObjectList (10, false);
	private final Diff diff = new Diff ();

	private UITree source;
	private Object sourceRoot;
	private Map params;
	private boolean disposed = false;


	public UITreePipeline ()
	{
		super (null);
	}


	public void initialize (UITree source, Object sourceRoot, Map params)
	{
		sourceRoot.getClass ();
		if (this.source != null)
		{
			throw new IllegalStateException ();
		}
		this.source = source;
		this.sourceRoot = sourceRoot;
		this.params = params;
		for (int i = 0; i < transformers.size (); i++)
		{
			((Transformer) transformers.get (i)).initialize (this);
		}
		update ();
	}


	public UITree getSource ()
	{
		return source;	
	}


	public Object getSourceRoot ()
	{
		return sourceRoot;	
	}


	public Object getParameter (String key, Object defaultValue)
	{
		return (params != null) ? params.get (key, defaultValue) : defaultValue;
	}


	public Registry getRegistry ()
	{
		return source.getContext ().getWorkbench ().getRegistry ();
	}

	
	private static class TMEvent extends TreeModelEvent
	{
		TMEvent (Object source, TreePath path)
		{
			super (source, path, null, null);
		}
		
		void set (TreePath path, int[] indices, Object[] children)
		{
			this.path = path;
			this.childIndices = indices;
			this.children = children;
		}
	}


	public void update ()
	{
		if (source != null)
		{
			Tree old = (root == null) ? null : new Tree (root);
			root = createInitialTree ();
			for (int i = 0; i < transformers.size (); i++)
			{
				((Transformer) transformers.get (i)).transform ((Node) root);
			}
			if (old != null)
			{
				diff.compare (old, this, diff, this, diff);
				TreePath p = new TreePath (root);
				TMEvent e = new TMEvent (this, p);
				fireTreeModelEvent (NODES_CHANGED, e);
				int n = root.getChildCount ();
				if (n > 0)
				{
					fireChanged (root, n, p, e);
				}
				((Node) old.getRoot ()).dispose ();
			}
		}
	}


	private static final Object INDICES_LOCK = new Object ();
	private static int[][] INDICES = new int[32][];

	private void fireChanged (Tree.Node r, int n, TreePath p, TMEvent e)
	{
		int[] a;
		if (n < INDICES.length)
		{
			a = INDICES[n];
		}
		else
		{
			synchronized (INDICES_LOCK)
			{
				if (n >= INDICES.length)
				{
					INDICES = new int[n + 1][];
				}
			}
			a = null;
		}
		if (a == null)
		{
			a = new int[n];
			for (int i = 0; i < n; i++)
			{
				a[i] = i;
			}
			INDICES[n] = a;
		}
		e.set (p, a, r.getChildren ());
		fireTreeModelEvent (NODES_CHANGED, e);
		for (Tree.Node c = r.children; c != null; c = c.next)
		{
			n = c.getChildCount ();
			if (n > 0)
			{
				fireChanged (c, n, p.pathByAddingChild (c), e);
			}
		}
	}


	@Override
	protected void firstListenerAdded ()
	{
		source.addTreeModelListener (this);
	}


	@Override
	protected void allListenersRemoved ()
	{
		dispose ();
	}


	@Override
	public final void dispose ()
	{
		if (disposed)
		{
			return;
		}
		disposed = true;
		clearListeners ();
		source.removeTreeModelListener (this);
		while (!transformers.isEmpty ())
		{
			((Transformer) transformers.pop ()).dispose ();
		}
		disposeImpl ();
	}


	protected void disposeImpl ()
	{
	}


	public void add (Transformer t)
	{
		transformers.add (t);
		update ();
	}


	public void remove (Transformer t)
	{
		transformers.remove (t);
		t.dispose ();
		update ();
	}


	public Node copyTree (UITree s, Object root, ObjectToBoolean filter)
	{
		Node c = new Node (s, root);
		addTree (c, -1, s, root, filter, null);
		return c;
	}


	public void addTree (Node dest, int pos, UITree s, Object root, ObjectToBoolean filter,
						 java.util.Comparator comp)
	{
		if (!s.isLeaf (root))
		{
		processChildren:
			for (int i = 0, n = s.getChildCount (root); i < n; i++)
			{
				Object child = s.getChild (root, i);
				if (((filter == null) ? s.getType (child) != NT_UNDEFINED
					 : filter.evaluateBoolean (child))
					&& isAvailable (s, child))
				{
					if (comp != null)
					{
						for (Node c = (Node) dest.children; c != null;
							 c = (Node) c.next)
						{
							if (comp.compare (child, c.getNode ()) == 0)
							{
								addTree (c, -1, s, child, filter, comp);
								continue processChildren;
							}
						}
					}
					Node c = new Node (s, child);
					addTree (c, -1, s, child, filter, comp);
					dest.insertChild (c, pos);
				}
			}
		}
	}


	protected Node createInitialTree ()
	{
		return copyTree (source, sourceRoot, null);
	}


	protected boolean isAvailable (UITree s, Object node)
	{
		return s.isAvailable (node);
	}


	public void treeNodesChanged (TreeModelEvent e)
	{
		if (e.getChildren () == null)
		{
			if (source.nodesEqual (e.getTreePath ().getLastPathComponent (),
								   ((Node) root).getNode ()))
			{
				fireTreeModelEvent
					(MutableTree.NODES_CHANGED,
					 new TreeModelEvent (this, new TreePath (root), null, null));
			}
		}
		else
		{
			ObjectList path = new ObjectList ();
			treeNodesChanged ((Node) root, path, e.getChildren ());
		}
	}


	private void treeNodesChanged (Node n, ObjectList path, Object[] changed)
	{
		for (int i = changed.length - 1; i >= 0; i--)
		{
			if (source.nodesEqual (changed[i], n.getNode ()))
			{
				fireTreeModelEvent
					(MutableTree.NODES_CHANGED, path.isEmpty ()
					 ? new TreeModelEvent (this, new TreePath (n), null, null)
					 : new TreeModelEvent (this, path.toArray (),
										   new int[] {getIndexOfChild (n)},
										   new Object[] {n}));
				return;
			}
		}
		path.push (n);
		for (n = (Node) n.children; n != null; n = (Node) n.next)
		{
			treeNodesChanged (n, path, changed);
		}
		path.pop ();
	}


	public void treeNodesInserted (TreeModelEvent e)
	{
		treeStructureChanged (e);
	}


	public void treeNodesRemoved (TreeModelEvent e)
	{
		treeStructureChanged (e);
	}


	public void treeStructureChanged (TreeModelEvent e)
	{
		if (e != null) // TODO
		{
			update ();
			return;
		}
		for (int i = 0; i < transformers.size (); i++)
		{
			if (((Transformer) transformers.get (i))
				.isAffectedBy (e.getTreePath ()))
			{
				update ();
				return;
			}
		}
	}


	public Context getContext ()
	{
		if (source == null)
		{
			source.getClass();
		}
		return source.getContext ();
	}


	public boolean nodesEqual (Object a, Object b)
	{
		return ((MutableTree) this).nodesEqual ((Node) a, (Node) b);
	}


	public int getType (Object node)
	{
		return ((Node) node).getType ();
	}


	public String getName (Object node)
	{
		return ((Node) node).getName ();
	}


	public Object resolveLink (Object node)
	{
		Node n = (Node) node;
		return n.handler.resolveLink (n.node);
	}


	public boolean isAvailable (Object node)
	{
		Node n = (Node) node;
		return n.handler.isAvailable (n.node);
	}


	public boolean isEnabled (Object node)
	{
		Node n = (Node) node;
		return n.handler.isEnabled (n.node);
	}


	@Override
	public boolean isLeaf (Object node)
	{
		return ((Node) node).isLeaf ();
	}


	public Object getDescription (Object node, String type)
	{
		Node n = (Node) node;
		return n.handler.getDescription (n.getNode (), type);
	}


	public void eventOccured (Object node, EventObject event)
	{
		dispatchEvent (this, node, event, true);
		((Node) node).handleEvent (event);
	}


	static void dispatchEvent (UITree tree, Object node, EventObject event,
							   boolean isPipeline)
	{
		if ((event instanceof InputEditEvent)
			&& !((InputEditEvent) event).isConsumed ())
		{
			if (node instanceof Command)
			{
				((Command) node).run (event, (InputEditEvent) event);
				((InputEditEvent) event).consume ();
			}
			else
			{
				InputEditEvent e;
				UITree t;
				if (isPipeline)
				{
					e = (InputEditEvent) ((InputEditEvent) event)
						.clone (((Node) node).getNode ());
					t = ((UITreePipeline) tree).source;
				}
				else
				{
					e = (InputEditEvent) ((InputEditEvent) event).clone (node);
					t = tree;
				}
			dispatchEvent:
				while (true)
				{
					node = tree.getParent (node);
					if (node == null)
					{
						break dispatchEvent; 
					}
					Object n;
					if (isPipeline)
					{
						if (((Node) node).handler != t)
						{
							continue;
						}
						n = ((Node) node).getNode ();
					}
					else
					{
						n = node;
					}
					for (int i = 0, k = t.getChildCount (n); i < k; i++)
					{
						Object c = t.getChild (n, i);
						if (e.isConsumed ())
						{
							break dispatchEvent; 
						}
						if (t.getName (c).startsWith ("."))
						{
							t.eventOccured (c, e);
						}
					}
				}
				if (e.isConsumed ())
				{
					((InputEditEvent) event).consume ();
				}
			}
		}
	}


	public Object invoke (Object node, String method, Object arg)
	{
		return ((Node) node).invoke (method, arg);
	}

}
