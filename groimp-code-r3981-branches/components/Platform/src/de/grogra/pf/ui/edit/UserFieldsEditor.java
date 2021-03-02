
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

import de.grogra.reflect.*;
import de.grogra.persistence.PersistenceCapable;
import de.grogra.persistence.Shareable;
import de.grogra.pf.ui.edit.PropertyEditorTree.*;

public class UserFieldsEditor extends PropertyEditor
{

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new UserFieldsEditor ());
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
		return new UserFieldsEditor ();
	}

//enh:end

	private UserFieldsEditor ()
	{
		super (null);
	}


	@Override
	public boolean isNullAllowed ()
	{
		return false;
	}


	@Override
	public Type getPropertyType ()
	{
		return ClassAdapter.wrap (UserFields.class);
	}


	@Override
	public Node createNodes (PropertyEditorTree tree, Property p, String label)
	{
		if (tree.isMenu ())
		{
			return null;
		}
		UserFields f = (UserFields) p.getValue ();
		Node first = null, last = null;
		if (f != null)
		{
			Type t = Reflection.getType (f);
			for (int i = 0; i < f.getUserFieldCount (); i++)
			{
				Field field = f.getUserField (i);
				Property sp = p.createSubProperty (t, field, -1);
				if (sp != null)
				{
					PropertyEditor e = findEditor (p.getWorkbench (), sp.getType (), true);
					if (e != null) 
					{
						if ((e instanceof CompositeEditor) && Reflection.equal (Type.OBJECT, e.getPropertyType ()))
						{
							Object v = sp.getValue ();
							if ((v instanceof PersistenceCapable)
								&& (((PersistenceCapable) v).getPersistenceManager () != null))
							{
								continue;
							}
							if ((v instanceof Shareable)
								&& (((Shareable) v).getProvider () != null))
							{
								continue;
							}
						}
						Node n = e.createNodes (tree, sp, field.getName ());
						if (n != null)
						{
							if (first == null)
							{
								first = n;
							}
							if (last != null)
							{
								last.next = n;
							}
							while (n.next != null)
							{
								n = (Node) n.next;
							}
							last = n;
						}
					}
				}
			}
		}
		return first;
	}

}
