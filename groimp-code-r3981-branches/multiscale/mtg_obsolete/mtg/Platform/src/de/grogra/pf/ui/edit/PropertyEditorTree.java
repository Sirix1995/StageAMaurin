
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

import java.beans.*;
import javax.swing.tree.*;
import de.grogra.icon.*;
import de.grogra.util.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.registry.PanelFactory;
import de.grogra.pf.ui.tree.*;
import de.grogra.pf.ui.util.*;

public abstract class PropertyEditorTree extends MutableTree implements UITree
{

	public static class Node extends MutableTree.Node
	{
		String text;
		IconSource icon;
		Described described;
		
		private TreePath path;
		
		
		public void setDecription (Described d)
		{
			described = d;
		}
		
		
		public void setText (String text)
		{
			this.text = text;
		}


		protected boolean isEnabled ()
		{
			return true;
		}


		@Override
		public TreePath getTreePath ()
		{
			if (path == null)
			{
				path = (parent == null) ? new TreePath (this)
					: parent.getTreePath ().pathByAddingChild (this);
			}
			return path;
		}

		
		protected ComponentWrapper createComponent ()
		{
			return null;
		}
		
		
		protected void updateComponent (ComponentWrapper cw)
		{
		}
		

		void fireChanged (Object changeEvent)
		{
			for (Node n = (Node) children; n != null; n = (Node) n.next)
			{
				n.fireChanged (changeEvent);
			}
		}
	}
	
	
	public static class SelectionNode extends Node
	{
		final Selection sel;
		
		public SelectionNode (Selection sel)
		{
			this.sel = sel;
			described = UI.I18N.keyToDescribed ("selection.edit");
		}
	}
	
	
	public class PropertyNode extends Node
		implements PropertyChangeListener
	{
		private final Property property;
		private final Widget widget;
		

		public PropertyNode (Property p, Widget w, String label)
		{
			this.widget = (w != null) ? new WidgetAdapter (w, p.getContext ()) : null;
			this.property = p;
			text = label;
		}

	
		public Property getProperty ()
		{
			return property;
		}
		

		@Override
		protected ComponentWrapper createComponent ()
		{
			widget.addPropertyChangeListener (this);
			updateComponent (widget);
			return widget;
		}
		
		
		@Override
		protected void updateComponent (ComponentWrapper cw)
		{
			((Widget) cw).updateValue (toWidget (property.getValue ()));
		}


		protected Object toWidget (Object propertyValue)
		{
			return propertyValue;
		}
		

		protected Object fromWidget (Object widgetValue)
		{
			return widgetValue;
		}

	 
		public void propertyChange (PropertyChangeEvent event)
		{
			try
			{
				property.setValue (fromWidget (event.getNewValue ()));
			}
			catch (InterruptedException e)
			{
				System.err.println (e);
			}
		}

		
		@Override
		protected boolean isEnabled ()
		{
			return property.isWritable ();
		}


		@Override
		void fireChanged (Object changeEvent)
		{
			if (isNodeAffectedBy (this, changeEvent))
			{
				valueForPathChanged (getTreePath (), changeEvent);
				super.fireChanged (changeEvent);
			}
		}
		
		
		@Override
		public String toString ()
		{
			return "PropertyNode[" + property + ']';
		}
	}


	private final Context context;
	private boolean menu;
	
	
	public PropertyEditorTree (Context context)
	{
		super (new Node ());
		this.context = context;
	}

	
	public void setMenu ()
	{
		menu = true;
	}


	public Context getContext ()
	{
		return context;
	}

	
	public boolean isMenu ()
	{
		return menu;
	}
	

	protected void fireChanged (Object changeEvent)
	{
		((Node) root).fireChanged (changeEvent);
	}
	

	protected abstract boolean isNodeAffectedBy (PropertyNode node, Object chanveEvent);

	
	public Object getDescription (Object node, String type)
	{
		Node n = (Node) node;
		return (n.described != null) ? n.described.getDescription (type)
			: Utils.isStringDescription (type) ? n.text
			: Described.ICON.equals (type) ? (Object) n.icon
			: null;
	}


	public boolean nodesEqual (Object a, Object b)
	{
		return a == b;
	}


	public int getType (Object node)
	{
		return (((Node) node).children == null) ? NT_ITEM : NT_GROUP;
	}


	public String getName (Object node)
	{
		return (String) getDescription (node, Described.NAME);
	}


	public boolean isAvailable (Object node)
	{
		return true;
	}

	
	public boolean isEnabled (Object node)
	{
		return ((Node) node).isEnabled ();
	}


	public Object resolveLink (Object node)
	{
		return node;
	}

	
	public void eventOccured (Object node, java.util.EventObject event)
	{
		if ((node instanceof SelectionNode)
			&& (event instanceof de.grogra.pf.ui.event.ActionEditEvent))
		{
			PanelFactory.getAndShowPanel ((Context) event, "/ui/panels/attributeeditor", null);
			UIProperty.WORKBENCH_SELECTION.setValue (context, ((SelectionNode) node).sel);
		}
	}

	
	public Object invoke (Object node, String method, Object arg)
	{
		if (UIToolkit.CREATE_COMPONENT_WRAPPER_METHOD.equals (method))
		{
			return ((Node) node).createComponent ();
		}
		else if (UIToolkit.UPDATE_COMPONENT_WRAPPER_METHOD.equals (method))
		{
			((Node) node).updateComponent ((ComponentWrapper) arg);
		}
		return null;
	}
	
	
	public void update ()
	{
	}

}
