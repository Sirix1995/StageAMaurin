
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
import de.grogra.graph.Attribute;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.edit.PropertyEditorTree.*;
import de.grogra.pf.ui.registry.AttributeItem;
import de.grogra.pf.ui.tree.*;
import de.grogra.reflect.Field;
import de.grogra.reflect.Type;

public class MapSource extends SelectionBase
{
	private final ModifiableMap values;
	private final KeyDescription[] keys;
	private final String name;


	class MapProperty extends Property
	{
		final KeyDescription key;
		final PropertyEditorTree tree;
		

		MapProperty (PropertyEditorTree tree, KeyDescription key)
		{
			super (tree.getContext (), key.getType ());
			this.tree = tree;
			this.key = key;
			setQuantity (key.getQuantity ());
		}
		
		
		@Override
		public void setValue (Object value)
		{
			values.put (key.getKey (), value);
			tree.fireChanged (key.getKey ());
		}


		@Override
		public Object getValue ()
		{
			return values.get (key.getKey (), null);
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


	public MapSource (Context ctx, ModifiableMap values,
					  KeyDescription[] keys, String name)
	{
		super (ctx);
		this.values = values;
		this.keys = keys;
		this.name = name;
	}


	public Object getDescription (String type)
	{ 
		return Utils.isStringDescription (type) ? name : null;
	}


	@Override
	protected PropertyEditor getEditorFor (Property p, Item item)
	{
		if (((MapProperty) p).key instanceof Option)
		{
			PropertyEditor e = (PropertyEditor)
				((Option) ((MapProperty) p).key).findFirst
				(ItemCriterion.INSTANCE_OF, PropertyEditor.class, true);
			if (e != null)
			{
				return e;
			}
		}
		return super.getEditorFor (p, item);
	}


	@Override
	protected String getLabelFor (Property p)
	{
		return (String) ((MapProperty) p).key.getDescription (NAME);
	}


	@Override
	protected Node createPropertyNodes
		(PropertyEditorTree t, Property p, UITree sourceTree, Object sourceNode)
	{
		return ((sourceNode instanceof AttributeItem)
				&& (((MapProperty) p).key instanceof Attribute)
				&& ((AttributeItem) sourceNode).correspondsTo
					((Attribute) ((MapProperty) p).key))
			? createPropertyNodes (t, p, (Item) sourceNode)
			: null;
	}


	@Override
	protected Node createPropertyNodesInGroup
		(PropertyEditorTree t, Property p, UITree sourceTree, Object sourceGroup)
	{
		return (((Item) sourceGroup).hasName ("attributes")
				|| ((((MapProperty) p).key instanceof Option)
					&& ((Option) ((MapProperty) p).key).belongsToGroup ((Item) sourceGroup)))
			? createPropertyNodes (t, p, null) : null;
	}


	@Override
	protected UITree getHierarchySource ()
	{
		return new RegistryAdapter (context);
	}


	@Override
	protected Object getHierarchySourceRoot (UITree source)
	{
		return Item.resolveItem (UI.getRegistry (context), "/attributes");
	}


	@Override
	protected PropertyEditorTree createTree ()
	{
		return new PropertyEditorTree (context)
		{
			@Override
			protected boolean isNodeAffectedBy (PropertyNode node, Object changeEvent)
			{
				return ((MapProperty) node.getProperty ()).key.getKey ().equals (changeEvent);
			}
		};
	}


	@Override
	protected List getProperties (PropertyEditorTree tree)
	{
		List list = new ArrayList ();
		for (int i = 0; i < keys.length; i++)
		{
			list.add (new MapProperty (tree, keys[i]));
		}
		return list;
	}

}
