
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

import javax.swing.event.*;
import javax.swing.tree.TreePath;
import de.grogra.util.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.registry.*;

public class RegistryAdapter implements UITree, RegistryContext
{
	private final Context context;
	private final Registry registry;


	public RegistryAdapter (Context context, Registry registry)
	{
		this.context = context;
		this.registry = registry;
	}


	public RegistryAdapter (Context context)
	{
		this (context, (context == null) ? null
			  : context.getWorkbench ().getRegistry ());
	}

	
	public Registry getRegistry ()
	{
		return registry;
	}


	public void dispose ()
	{
	}


	public Context getContext()
	{
		return context;
	}


	public boolean nodesEqual (Object a, Object b)
	{
		return a == b;
	}


	public Object getDescription (Object node, String type)
	{
		return ((Item) node).getDescription (type);
	}


	public int getType (Object node)
	{
		return (node instanceof UIItem) ? ((UIItem) node).getUINodeType ()
			: (node instanceof Link) ? NT_LINK
			: ((Item) node).isDirectory () ? NT_DIRECTORY
			: ((node instanceof Command) || (node instanceof ObjectItem)
			   || (node instanceof de.grogra.pf.registry.expr.Expression))
			  ? NT_ITEM : NT_UNDEFINED;
	}


	public String getName (Object node)
	{
		return ((Item) node).getName (); 
	}


	public Object resolveLink (Object node)
	{
		return ((Item) node).resolveLink (registry);
	}


	public boolean isAvailable (Object node)
	{
		return !((Item) node).getName ().startsWith (".")
			&& (!(node instanceof UIItem)
				|| ((UIItem) node).isAvailable (context));
	}


	public boolean isEnabled (Object node)
	{
		return (node instanceof UIItem) ? ((UIItem) node).isEnabled (context)
			: UI.isEnabled ((Item) node, context);
	}


	public void eventOccured (Object node, java.util.EventObject event)
	{
		if (node instanceof EventListener)
		{
			((EventListener) node).eventOccured (event);
		}
		if (!getName (node).startsWith ("."))
		{
			UITreePipeline.dispatchEvent (this, node, event, false);
		}
	}


	public Object invoke (Object node, String method, Object arg)
	{
		return (node instanceof UIItem)
			? ((UIItem) node).invoke (context, method, arg)
			: "getValue".equals (method)
			? ((Item) node).getOrNull ((String) arg)
			: null;
	}


	public boolean isLeaf (Object node)
	{
		return (getType (node) & NT_DIRECTORY_MASK) == 0;
	}


	public Object getChild (Object parent, int index)
	{
		return registry.getChild (parent, index);
	}


	public int getChildCount (Object parent)
	{
		return registry.getChildCount (parent);
	}


	public int getIndexOfChild (Object parent, Object child)
	{
		return registry.getIndexOfChild (parent, child);
	}


	public Object getParent (Object child)
	{
		return ((Item) child).getAxisParent ();
	}


	public Object getRoot()
	{
		return registry.getRoot ();
	}


	public void valueForPathChanged (TreePath path, Object newValue)
	{
		throw new UnsupportedOperationException ();
	}


	public void addTreeModelListener (TreeModelListener listener)
	{
		registry.addTreeModelListener (listener);
	}


	public void removeTreeModelListener (TreeModelListener listener)
	{
		registry.removeTreeModelListener (listener);
	}


	public void update ()
	{
	}

}
