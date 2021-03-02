
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

import java.util.List;
import javax.swing.event.*;

import de.grogra.xl.util.ObjectList;

public class TreeModelSupport
{
	public static final int NODES_CHANGED = 0;
	public static final int NODES_INSERTED = 1;
	public static final int NODES_REMOVED = 2;
	public static final int STRUCTURE_CHANGED = 3;


	private TreeModelListener listener = null;
	private ObjectList listeners = null;

	private final boolean copyListeners;
	
	
	public TreeModelSupport (boolean copyListeners)
	{
		this.copyListeners = copyListeners;
	}

	
	public static void fireTreeModelEvent (int type, TreeModelEvent event,
										   TreeModelListener listener)
	{
		switch (type)
		{
			case NODES_CHANGED:
				listener.treeNodesChanged (event);
				break;
			case NODES_INSERTED:
				listener.treeNodesInserted (event);
				break;
			case NODES_REMOVED:
				listener.treeNodesRemoved (event);
				break;
			case STRUCTURE_CHANGED:
				listener.treeStructureChanged (event);
				break;
		}
	}


	public static void fireTreeModelEvent (int type, TreeModelEvent event,
										   List listeners)
	{
		for (int i = 0, n = listeners.size (); i < n; i++)
		{
			fireTreeModelEvent (type, event,
								(TreeModelListener) listeners.get (i));
		}
	}


	public static void fireTreeModelEvent (int type, TreeModelEvent event,
										   TreeModelListener[] listeners)
	{
		for (int i = 0, n = listeners.length; i < n; i++)
		{
			fireTreeModelEvent (type, event, listeners[i]);
		}
	}


	public synchronized void addTreeModelListener (TreeModelListener l)
	{
		if (listeners != null)
		{
			boolean wasEmpty = listeners.isEmpty ();
			listeners.add (l);
			if (wasEmpty)
			{
				firstListenerAdded ();
			}
		}
		else if (listener != null)
		{
			listeners = new ObjectList (5, false);
			listeners.add (listener);
			listeners.add (l);
			listener = null;
		}
		else
		{
			listener = l;
			firstListenerAdded ();
		}
	}


	public synchronized void removeTreeModelListener (TreeModelListener l)
	{
		if (listeners != null)
		{
			if (listeners.remove (l) && listeners.isEmpty ())
			{
				allListenersRemoved ();
			}
		}
		else if (listener == l)
		{
			listener = null;
			allListenersRemoved ();
		}
	}


	protected void firstListenerAdded ()
	{
	}


	protected void allListenersRemoved ()
	{
	}


	public synchronized boolean hasListeners ()
	{
		return (listener != null)
			|| ((listeners != null) && !listeners.isEmpty ());
	}


	public void fireTreeModelEvent (int type, TreeModelEvent event)
	{
		TreeModelListener t;
		List l = null;
		TreeModelListener[] a = null;
		synchronized (this)
		{
			if ((t = listener) == null)
			{
				if (((l = listeners) != null) && copyListeners)
				{
					l.toArray (a = new TreeModelListener[l.size ()]);
				}
			}
		}
		if (t != null)
		{
			fireTreeModelEvent (type, event, t);
		}
		else if (a != null)
		{
			fireTreeModelEvent (type, event, a);
		}
		else if (l != null)
		{
			fireTreeModelEvent (type, event, l);
		}
	}


	protected synchronized void clearListeners ()
	{
		if (listeners != null)
		{
			listeners.clear ();
			listeners = null;
		}
		listener = null;
	}

}
