
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
import de.grogra.persistence.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.edit.PropertyEditorTree.*;
import de.grogra.pf.ui.tree.RegistryAdapter;
import de.grogra.pf.ui.tree.UITree;
import de.grogra.reflect.Field;
import de.grogra.reflect.Type;

public class OptionsSelection extends SelectionBase
{
	final Item item;
	final boolean listen;
	
	
	class Tree extends PropertyEditorTree implements XAListener
	{
		Tree (Context c)
		{
			super (c);
			((Node) root).described = OptionsSelection.this;
		}
		
		@Override
		protected void firstListenerAdded ()
		{
			if (listen)
			{
				item.getPersistenceManager ().addXAListener (this);
			}
		}

		@Override
		protected void allListenersRemoved ()
		{
			if (listen)
			{
				item.getPersistenceManager ().removeXAListener (this);
			}
		}

		public void transactionApplied (Transaction.Data xa, boolean rollback)
		{
			fireChanged (xa);
		}

		@Override
		protected boolean isNodeAffectedBy (PropertyNode node, Object changeEvent)
		{
			return true;
		}
		
	}
	

	class OptProperty extends Property
	{
		final Option option;

		
		OptProperty (Context ctx, Option o)
		{
			super (ctx, o.getType ());
			this.option = o;
			setQuantity (o.getQuantity ());
		}
		
		
		@Override
		public void setValue (Object value)
		{
			option.setOptionValue (value);
			valueChanged (option.getName (), value);
		}


		@Override
		public Object getValue ()
		{
			return option.getObject ();
		}


		@Override
		public boolean isWritable ()
		{
			return true;
		}

		
		@Override
		public Property createSubProperty (Type actualType, Field field, int i)
		{
			return null;
		}

	}


	public OptionsSelection (Context ctx, Item item, boolean listen)
	{
		super (ctx);
		this.item = item;
		this.listen = listen;
	}


	public Object getDescription (String type)
	{ 
		return item.getDescription (type);
	}


	@Override
	protected PropertyEditor getEditorFor (Property p, Item item)
	{
		PropertyEditor e = (PropertyEditor) ((OptProperty) p).option.findFirst
			(ItemCriterion.INSTANCE_OF, PropertyEditor.class, true);
		return (e != null) ? e : super.getEditorFor (p, item);
	}


	@Override
	protected UITree getHierarchySource ()
	{
		return new RegistryAdapter (context);
	}


	@Override
	protected Object getHierarchySourceRoot (UITree source)
	{
		return item;
	}


	@Override
	protected PropertyEditorTree createTree ()
	{
		return new Tree (context);
	}
	

	@Override
	protected Node createPropertyNodes
		(PropertyEditorTree t, Property p, UITree sourceTree, Object sourceNode)
	{
		return null;
	}


	@Override
	protected Node createPropertyNodesInGroup
		(PropertyEditorTree t, Property p, UITree sourceTree, Object sourceGroup)
	{
		return ((sourceGroup == item) || ((OptProperty) p).option.belongsToGroup ((Item) sourceGroup))
			? createPropertyNodes (t, p, null) : null;
	}


	@Override
	protected String getLabelFor (Property p)
	{
		return (String) ((OptProperty) p).option.getDescription (NAME);
	}


	@Override
	protected List getProperties (PropertyEditorTree tree)
	{
		Option[] opts = Option.getEditableOptions (item, false);
		List list = new ArrayList ();
		for (int i = 0; i < opts.length; i++)
		{
			list.add (new OptProperty (tree.getContext (), opts[i]));
		}
		return list;
	}
	
	
	protected void valueChanged (String name, Object value)
	{
	}

}
