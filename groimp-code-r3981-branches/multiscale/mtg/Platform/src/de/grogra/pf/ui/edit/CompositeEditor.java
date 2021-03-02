
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

import java.util.Arrays;

import de.grogra.persistence.PersistenceBindings;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.edit.PropertyEditorTree.*;
import de.grogra.xl.util.ObjectList;
import de.grogra.reflect.*;

public class CompositeEditor extends PropertyEditor implements ItemCriterion
{
	private static final int INHERIT_MASK = 1 << PropertyEditor.USED_BITS;
	private static final int APPEND_MASK = INHERIT_MASK << 1;
	public static final int USED_BITS = PropertyEditor.USED_BITS + 2;

	// boolean inherit
	//enh:field type=bits(INHERIT_MASK)

	// boolean append
	//enh:field type=bits(APPEND_MASK)

	String[] fields = de.grogra.util.Utils.STRING_0;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field inherit$FIELD;
	public static final NType.Field append$FIELD;
	public static final NType.Field fields$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (CompositeEditor.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((CompositeEditor) o).fields = (String[]) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((CompositeEditor) o).fields;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new CompositeEditor ());
		$TYPE.addManagedField (inherit$FIELD = new NType.BitField ($TYPE, "inherit", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, INHERIT_MASK));
		$TYPE.addManagedField (append$FIELD = new NType.BitField ($TYPE, "append", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, APPEND_MASK));
		$TYPE.addManagedField (fields$FIELD = new _Field ("fields", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String[].class), null, 0));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new CompositeEditor ();
	}

//enh:end

	private CompositeEditor ()
	{
		super (null);
		bits |= APPEND_MASK;
	}


	@Override
	public boolean isNullAllowed ()
	{
		return false;
	}


	private void getRootEditors (RegistryContext c, ObjectList v)
	{
		if ((bits & APPEND_MASK) == 0)
		{
			v.add (this);
		}
		if ((bits & INHERIT_MASK) != 0)
		{
			CompositeEditor e = (CompositeEditor) findEditor
				(c, getPropertyType ().getSupertype (), false, this, null);
			if (e != null)
			{
				e.getRootEditors (c, v);
			}
		}
		if ((bits & APPEND_MASK) != 0)
		{
			v.add (this);
		}
	}


	public boolean isFulfilled (Item item, Object info)
	{
		return item instanceof CompositeEditor;
	}


	public String getRootDirectory ()
	{
		return null;
	}

	
	@Override
	public Node createNodes (PropertyEditorTree tree, Property p, String label)
	{
		ObjectList nodes = new ObjectList (20);
		getRootEditors (p.getWorkbench (), nodes);
		Node[] cursor = new Node[2];
		createNodes (cursor, tree, p, label, nodes, 0);
		Object o = p.getValue ();
		if (o != null)
		{
			installSubtypeFields
				(cursor, tree, p, label, ClassAdapter.wrap (o.getClass ()));
		}
		return cursor[0];
	}
	
	
	private String installSubtypeFields
		(Node[] cursor, PropertyEditorTree tree, Property p, String label, Type type)
	{
		if ((type == null) || Reflection.equal (type, getPropertyType ()))
		{
			return label;
		}
		label = installSubtypeFields (cursor, tree, p, label, type.getSupertype ());
		for (int i = 0; i < type.getDeclaredFieldCount (); i++)
		{
			Field f = type.getDeclaredField (i);
			if ((f.getModifiers () & (Member.STATIC | Member.SYNTHETIC)) == 0)
			{
				label = installFieldEditor (cursor, tree, p, type, label, f.getSimpleName (), null);
			}
		}
		return label;
	}

	
	private static void add (Node[] cursor, Node n)
	{
		if (n == null)
		{
			return;
		}
		if (cursor[0] == null)
		{
			cursor[0] = n;
		}
		if (cursor[1] != null)
		{
			cursor[1].next = n;
		}
		while (n.next != null)
		{
			n = (Node) n.next;
		}
		cursor[1] = n;
	}


	private String installFieldEditor
		(Node[] cursor, PropertyEditorTree tree, Property p, Type ptype,
		 String label, String field, PropertyEditor e)
	{
		Property sp = p.createSubProperty (ptype, (field.charAt (0) == '*') ? "" : field, -1);
		if (sp != null)
		{
			if (e == null)
			{
				e = findEditor (p.getWorkbench (), sp.getType (), true);
			}
			if (e != null)
			{
				String s = (String) getFromResource (getAbsoluteName () + '/' + field + '.' + NAME);
				if (s == null)
				{
					s = field;
				}
				if (label != null)
				{
					s = label + ' ' + s;
					label = null;
				}
				add (cursor, e.createNodes (tree, sp, s));
			}
		}
		return label;
	}


	private void createNodes
		(Node[] cursor, PropertyEditorTree tree,
		 Property p, String label, ObjectList nodes, int begin)
	{
		int end = nodes.size ();
		for (int k = begin; k < end; k++)
		{
			Item n = (Item) nodes.get (k);
			String[] a = (n instanceof CompositeEditor)
				? ((CompositeEditor) n).fields : ((FieldGroup) n).fields;
			Item ce = n;
			while (!(ce instanceof CompositeEditor))
			{
				ce = (Item) ce.getAxisParent ();
			}
			for (int i = 0; i < a.length; i++)
			{
				label = ((CompositeEditor) ce).installFieldEditor
					(cursor, tree, p, ((CompositeEditor) ce).getPropertyType (),
					 label, a[i], null);
			}
			for (Item i = (Item) n.getBranch (); i != null;
				 i = (Item) i.getSuccessor ())
			{
				if (i.resolveLink (p.getWorkbench ()) instanceof PropertyEditor)
				{
					label = ((CompositeEditor) ce).installFieldEditor
						(cursor, tree, p, ((CompositeEditor) ce).getPropertyType (),
						 label, i.getName (), (PropertyEditor) i.resolveLink (p.getWorkbench ()));
				}
				else if (i instanceof FieldGroup)
				{
					nodes.add (i);
				}
			}
		}
		int s = nodes.size ();
		while (s > end)
		{
			FieldGroup g = (FieldGroup) nodes.remove (end);
			--s;
			nodes.add (g);
			int i = end;
			while (i < s)
			{
				if (((FieldGroup) nodes.get (i)).hasName (g.getName ()))
				{
					nodes.add (nodes.remove (i));
					--s;
				}
				else
				{
					++i;
				}
			}
			Node gn = new Node ();
			gn.described = g;
			add (cursor, gn);
			Node[] c = new Node[2];
			createNodes (c, tree, p, null, nodes, s);
			gn.addChild (c[0]);
			nodes.setSize (s);
		}
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ',' + getPropertyType () + ','
			+ Arrays.toString (fields);
	}


	@Override
	protected Item createItem (PersistenceBindings pb,
							   String name)
		throws InstantiationException, IllegalAccessException,
		java.lang.reflect.InvocationTargetException, ClassNotFoundException
	{
		if ("fgroup".equals (name))
		{
			return new FieldGroup ();
		}
		return super.createItem (pb, name);
	}

}
