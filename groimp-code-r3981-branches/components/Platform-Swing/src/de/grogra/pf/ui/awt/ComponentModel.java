
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

package de.grogra.pf.ui.awt;

import java.awt.*;
import javax.swing.RootPaneContainer;
import javax.swing.event.*;

public abstract class ComponentModel implements de.grogra.util.MutableTreeModel,
	AWTSynchronizer.Callback, de.grogra.pf.ui.ComponentWrapper
{
	protected Container root;
	protected final AWTSynchronizer sync = new AWTSynchronizer (this);


	protected static final int INSERT = 0;
	protected static final int REMOVE = 1;
	protected static final int TREE_CHANGED = 2;
	protected static final int SET_ENABLED = 3;

	protected static final int ACTION_COUNT = 4;


	public Object run (int action, int iarg, Object oarg1, Object oarg2)
	{
		switch (action)
		{
			case INSERT:
			{
				Container c = getContentPane (oarg1);
				if (iarg >= c.getComponentCount () - getPrefixComponentCount (c))
				{
					iarg = c.getComponentCount ();
				}
				else
				{
					iarg += getPrefixComponentCount (c);
				}
				if (oarg2 instanceof Component)
				{
					c.add ((Component) oarg2, iarg);
				}
				else
				{
					Object[] a = (Object[]) oarg2;
					c.add ((Component) a[0], a[1], iarg);
				}
				break;
			}
			case REMOVE:
			{
				Container c = getContentPane (oarg1);
				c.remove (iarg + getPrefixComponentCount (c));
				c.repaint ();
				break;
			}
			case TREE_CHANGED:
			{
				treeChangedSync ((Container) oarg1);
				break;
			}
			case SET_ENABLED:
			{
				((Component) oarg1).setEnabled (iarg != 0);
				break;
			}
			default:
				throw new AssertionError (action);
		}
		return null;
	}


	protected Container getContentPane (Object container)
	{
		return (container instanceof RootPaneContainer)
			? ((RootPaneContainer) container).getContentPane ()
			: (container instanceof ContentPaneContainer)
			? ((ContentPaneContainer) container).getContentPane ()
			: (Container) container;
	}


	protected int getPrefixComponentCount (Container c)
	{
		return 0;
	}


	public boolean isLeaf (Object node)
	{
		return !(node instanceof Container);
	}


	public Object getChild (Object parent, int index)
	{
		Container c = getContentPane (parent);
		return c.getComponent (getPrefixComponentCount (c) + index);
	}


	public int getChildCount (Object parent)
	{
		if (isLeaf (parent))
		{
			return 0;
		}
		Container c = getContentPane (parent);
		return (c == null) ? 0
			: c.getComponentCount () - getPrefixComponentCount (c);
	}


	public int getIndexOfChild (Object parent, Object child)
	{
		Container p = getContentPane (parent);
		if (p != null)
		{
			for (int i = 0; i < p.getComponentCount (); i++)
			{
				if (child == p.getComponent (i))
				{
					return i - getPrefixComponentCount (p);
				}
			}
		}
		return -1;
	}


	public Object getRoot ()
	{
		return root;
	}


	public Object getComponent ()
	{
		return root;
	}


	public void setRoot (Object root)
	{
		this.root = (Container) root;
	}


	public void insert (Object parent, int index, Object child,
						Object constraints)
	{
		sync.invokeAndWait (INSERT, index, parent, (constraints == null)
							? child : new Object[] {child, constraints});
	}


	public void remove (Object parent, int index)
	{
		sync.invokeAndWait (REMOVE, index, parent, null);
	}


	public void treeChanged (Object parent)
	{
		sync.invokeAndWait (TREE_CHANGED, parent);
	}

	
	protected void setEnabled (Component c, boolean enabled)
	{
		sync.invokeAndWait (SET_ENABLED, enabled ? 1 : 0, c, null);
	}


	protected abstract void treeChangedSync (Container parent);


	public void addTreeModelListener (TreeModelListener l)
	{
		throw new UnsupportedOperationException ();
	}


	public void removeTreeModelListener (TreeModelListener l)
	{
		throw new UnsupportedOperationException ();
	}

}
