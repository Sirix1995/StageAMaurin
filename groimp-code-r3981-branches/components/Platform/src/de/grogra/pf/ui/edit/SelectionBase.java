
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

package de.grogra.pf.ui.edit;

import java.util.*;
import de.grogra.util.*;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ItemCriterion;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.tree.*;
import de.grogra.pf.ui.edit.PropertyEditorTree.*;

public abstract class SelectionBase implements Selection
{
	protected final Context context;
	

	public SelectionBase (Context context)
	{
		this.context = context;
	}

	
	public void remove ()
	{
		UIProperty.WORKBENCH_SELECTION.setValue (context, null);
	}


	public int getCapabilities ()
	{
		return 0;
	}


	public Context getContext ()
	{
		return context;
	}


	protected PropertyEditor getEditorFor (Property p, Item item)
	{
		PropertyEditor e = p.getEditor ();
		if ((e == null) && (item != null))
		{
			e = (PropertyEditor) item.findFirst (ItemCriterion.INSTANCE_OF,
												 PropertyEditor.class, true);
		}
		if (e == null)
		{
			e = PropertyEditor.findEditor
				(UI.getRegistry (context), p.getType (), true);
		}
		return e;
	}

	
	protected Node createPropertyNodes (PropertyEditorTree tree, Property p, Item item)
	{
		PropertyEditor e = getEditorFor (p, item);
		return (e != null) ? e.createNodes (tree, p, getLabelFor (p)) : null;
	}

	
	protected abstract String getLabelFor (Property p);


	protected abstract List getProperties (PropertyEditorTree tree);


	protected abstract Node createPropertyNodes
		(PropertyEditorTree tree, Property p, UITree sourceTree, Object sourceNode);
	

	protected abstract Node createPropertyNodesInGroup
		(PropertyEditorTree tree, Property p, UITree sourceTree, Object sourceGroup);


	protected abstract UITree getHierarchySource ();


	protected abstract Object getHierarchySourceRoot (UITree source);


	protected abstract PropertyEditorTree createTree ();


	public ComponentWrapper createPropertyEditorComponent ()
	{
		return createPropertyEditorComponent (false);
	}


	public ComponentWrapper createPropertyEditorMenu ()
	{
		return createPropertyEditorComponent (true);
	}


	private ComponentWrapper createPropertyEditorComponent (boolean forMenu)
	{
		final PropertyEditorTree tree = createTree ();
		if (forMenu)
		{
			tree.setMenu ();
		}
		List nodes = getProperties (tree);
		UITree t = getHierarchySource ();
		new TreeBuilder ()
		{
			@Override
			protected Object createNodes (Object node, Object sourceNode)
			{
				return SelectionBase.this.createPropertyNodes
					(tree, (Property) node, (UITree) source, sourceNode);
			}


			@Override
			protected Object createGroup (Object sourceGroup)
			{
				Node g = new Node ();
				g.described = UI.nodeToDescribed ((UITree) source, sourceGroup);
				return g;
			}


			@Override
			protected Object createNodesInGroup (Object node, Object sourceGroup)
			{
				return SelectionBase.this.createPropertyNodesInGroup
					(tree, (Property) node, (UITree) source, sourceGroup);
			}
		}.buildTree (t, getHierarchySourceRoot (t), tree, nodes);
		if (forMenu)
		{
			tree.insert (tree.getRoot (), 0, new SelectionNode (this), null);
			return UIToolkit.get (context).createComponentMenu (tree);
		}
		final ComponentWrapper w = UIToolkit.get (context).createComponentTree (tree);
		final Object s = UIToolkit.get (context).createScrollPane (w.getComponent ());
		return new ComponentWrapper ()
		{
			public void dispose ()
			{
				w.dispose ();
			}
			
			public Object getComponent ()
			{
				return s;
			}
		};
	}


	public java.awt.datatransfer.Transferable toTransferable
		(boolean includeChildren)
	{
		throw new UnsupportedOperationException ();
	}
	
	
	public void delete (boolean includeChildren)
	{
		throw new UnsupportedOperationException ();
	}
}
