
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

package de.grogra.util;

import javax.swing.event.*;
import javax.swing.tree.*;

public class SubTree extends TreeModelSupport
	implements TreeModel, TreeModelListener, Disposable
{
	protected TreeModel tree;
	protected TreePath pathToRoot;

	private boolean disposed;
	private TreePath rootAsPath;


	public SubTree (TreeModel tree, TreePath pathToRoot)
	{
		super (false);
		this.tree = tree;
		this.pathToRoot = pathToRoot;
		this.rootAsPath = new TreePath (pathToRoot.getLastPathComponent ());
	}


	public Object getRoot ()
	{
		return pathToRoot.getLastPathComponent ();
	}


	public int getChildCount (Object parent)
	{
		return tree.getChildCount (parent);
	}


	public boolean isLeaf (Object node)
	{
		return tree.isLeaf (node);
	}


	public Object getChild (Object parent, int index)
	{
		return tree.getChild (parent, index);
	}


	public int getIndexOfChild (Object parent, Object child)
	{
		return tree.getIndexOfChild (parent, child);
	}


	public void valueForPathChanged (TreePath path, Object newValue)
	{
		if (path.getPathComponent (0) == pathToRoot.getLastPathComponent ())
		{
			TreePath p = pathToRoot;
			for (int i = 1; i < path.getPathCount (); i++)
			{
				p = p.pathByAddingChild (path.getPathComponent (i));
			}
			tree.valueForPathChanged (p, newValue);
		}
	}


	@Override
	protected void firstListenerAdded ()
	{
		tree.addTreeModelListener (this);
	}


	@Override
	protected void allListenersRemoved ()
	{
		tree.removeTreeModelListener (this);
	}


	public void treeNodesChanged (TreeModelEvent e)
	{
		if (pathToRoot.isDescendant (e.getTreePath ()))
		{
			fireTreeModelEvent (NODES_CHANGED, convert (e));
		}
	}


	public void treeNodesInserted (TreeModelEvent e)
	{
		if (pathToRoot.isDescendant (e.getTreePath ()))
		{
			fireTreeModelEvent (NODES_INSERTED, convert (e));
		}
	}


	public void treeNodesRemoved (TreeModelEvent e)
	{
		if (pathToRoot.isDescendant (e.getTreePath ()))
		{
			fireTreeModelEvent (NODES_REMOVED, convert (e));
		}
	}


	public void treeStructureChanged (TreeModelEvent e)
	{
		if (pathToRoot.isDescendant (e.getTreePath ()))
		{
			fireTreeModelEvent (STRUCTURE_CHANGED, convert (e));
		}
		else if (e.getTreePath ().isDescendant (pathToRoot))
		{
			fireTreeModelEvent (STRUCTURE_CHANGED, new TreeModelEvent
								(e.getSource (), rootAsPath));
		}
	}


	private TreeModelEvent convert (TreeModelEvent e)
	{
		Object[] p = e.getPath ();
		Object[] c = new Object[p.length - pathToRoot.getPathCount () + 1];
		System.arraycopy (p, pathToRoot.getPathCount () - 1, c, 0, c.length);
		return new TreeModelEvent (e.getSource (), p, e.getChildIndices (),
								   e.getChildren ());
	}


	public final void dispose ()
	{
		if (disposed)
		{
			return;
		}
		disposed = true;
		clearListeners ();
		tree.removeTreeModelListener (this);
		disposeImpl ();
		tree = null;
		pathToRoot = null;
	}


	protected void disposeImpl ()
	{
	}

}
