
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
import de.grogra.icon.IconSource;
import de.grogra.util.*;
import de.grogra.persistence.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.edit.PropertyEditorTree.*;
import de.grogra.pf.ui.tree.*;

public abstract class FieldSelection extends SelectionBase
{
	final Item description;
	final Object object;
	final PropertyEditor[] editors;
	final PersistenceField[] fields;
	final int[][] indices;
	final String[] labels;


	public FieldSelection (Context ctx, Object object,
						  PersistenceField[] fields,
						  int[][] indices,
						  PropertyEditor[] editors,
						  String[] labels, Item description)
	{
		super (ctx);
		this.object = object;
		this.fields = fields;
		if (indices == null)
		{
			indices = new int[fields.length][];
		}
		this.indices = indices;
		this.editors = editors;
		this.labels = labels;
		this.description = description;
	}


	@Override
	public boolean equals (Object o)
	{
		if (!(o instanceof FieldSelection))
		{
			return false;
		}
		FieldSelection fs = (FieldSelection) o;
		if ((object != fs.object) || (fields.length != fs.fields.length))
		{
			return false;
		}
		for (int i = fields.length - 1; i >= 0; i--)
		{
			if (fields[i] != fs.fields[i])
			{
				return false;
			}
			if (indices[i] != fs.indices[i])
			{
				int[] a = indices[i];
				int[] b = fs.indices[i];
				if ((a == null) || (b == null) || (a.length != b.length))
				{
					return false;
				}
				for (int j = 0; j < a.length; j++)
				{
					if (a[j] != b[j])
					{
						return false;
					}
				}
			}
		}
		return true;
	}


	public Object getDescription (String type)
	{
		if (description != null)
		{
			return description.getDescription (type);
		}
		else if (object instanceof Described)
		{
			return ((Described) object).getDescription (type);	
		}
		else if (ICON.equals (type) && (object instanceof IconSource))
		{
			return object;
		}
		else
		{
			return null;
		}
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
	protected Node createPropertyNodes
		(PropertyEditorTree t, Property p, UITree sourceTree, Object sourceNode)
	{
		return null;
	}


	@Override
	protected Node createPropertyNodesInGroup
		(PropertyEditorTree t, Property p, UITree sourceTree, Object sourceGroup)
	{
		return ((Item) sourceGroup).hasName ("attributes")
			? createPropertyNodes (t, p, null) : null;
	}


	@Override
	protected String getLabelFor (Property p)
	{
		int i = ((FieldProperty) p).index;
		return ((i >= 0) && (labels != null)) ? labels[i] : null;
	}


	@Override
	protected PropertyEditor getEditorFor (Property p, Item item)
	{
		int i = ((FieldProperty) p).index;
		return ((i >= 0) && (editors != null)) ? editors[i]
			: super.getEditorFor (p, item);
	}


	@Override
	protected List getProperties (PropertyEditorTree tree)
	{
		ArrayList list = new ArrayList (fields.length);
		for (int i = 0; i < fields.length; i++)
		{
			list.add (createProperty (tree.getContext (), i));
		}
		return list;
	}
	
	
	protected abstract FieldProperty createProperty (Context c, int i);

}
