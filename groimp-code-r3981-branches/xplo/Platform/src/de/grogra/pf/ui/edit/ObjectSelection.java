
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

import java.util.Vector;
import de.grogra.persistence.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;

public class ObjectSelection extends FieldSelection
{
	private Vector trees = new Vector ();

	class Tree extends PropertyEditorTree
	{
		Tree (Context c)
		{
			super (c);
		}
		
		@Override
		protected void firstListenerAdded ()
		{
			trees.add (this);
		}

		@Override
		protected void allListenersRemoved ()
		{
			trees.remove (this);
		}
		
		@Override
		protected boolean isNodeAffectedBy (PropertyNode node, Object changeEvent)
		{
			Object[] a = (Object[]) changeEvent;
			return (a[0] instanceof PersistenceField)
				&& ((PersistenceField) a[0]).overlaps
					((int[]) a[1], ((ObjProperty) node.getProperty ()).field,
					 ((ObjProperty) node.getProperty ()).indices);
		}
	}


	public ObjectSelection (Context ctx, Object object,
							PersistenceField[] fields, int[][] indices,
							PropertyEditor[] editors,
							String[] labels, Item description)
	{
		super (ctx, object, fields, indices, editors, labels, description);
	}


	@Override
	protected PropertyEditorTree createTree ()
	{
		return new Tree (context);
	}
	

	class ObjProperty extends FieldProperty implements SharedObjectReference
	{
		ObjProperty (Context context, Object object, PersistenceField field,
					 int[] indices, int index)
		{
			super (context, object, field, indices, index);
		}
		
		@Override
		public void setValue (Object value)
		{
			value = replaceValue (field, value);
			Object old = field.get (object, indices);
			if (old == value)
			{
				return;
			}
			if (old instanceof Shareable)
			{
//				((Shareable) old).removeReference (this);
			}
			field.set (object, indices, value, null);
			if (value instanceof Shareable)
			{
//				((Shareable) value).addReference (this);
			}
			fireChanged (field, indices);
			valueChanged (field, value);
		}
		
		public void sharedObjectModified (Shareable object, Transaction t)
		{
			fireChanged (field, indices);
		}
		
		@Override
		protected FieldProperty createSubProperty (PersistenceField f, int i)
		{
			return new ObjProperty (getContext (), object, f, addIndex (i), -1);
		}
	}

	
	@Override
	protected FieldProperty createProperty (Context c, int i)
	{
		return new ObjProperty (c, object, fields[i], indices[i], i);
	}
	
	
	protected Object replaceValue (PersistenceField field, Object value)
	{
		return value;
	}

	
	void fireChanged (PersistenceField field, int[] indices)
	{
		for (int i = trees.size () - 1; i >= 0; i--)
		{
			((Tree) trees.get (i)).fireChanged (new Object[] {field, indices});
		}
	}


	protected void valueChanged (PersistenceField field, Object value)
	{
	}

}
