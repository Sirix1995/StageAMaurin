
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
import de.grogra.pf.registry.PluginDescriptor;
import de.grogra.pf.ui.edit.PropertyEditorTree.*;

public class AdditionalNodeFieldsEditor extends PropertyEditor
{

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new AdditionalNodeFieldsEditor ());
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
		return new AdditionalNodeFieldsEditor ();
	}

//enh:end

	private AdditionalNodeFieldsEditor ()
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
		return ClassAdapter.wrap (de.grogra.graph.impl.Node.class);
	}


	@Override
	public Node createNodes (PropertyEditorTree tree, Property p, String label)
	{
		if (tree.isMenu ())
		{
			return null;
		}
		de.grogra.graph.impl.Node object = (de.grogra.graph.impl.Node) p.getValue ();
		Node first = null, last = null;
		if (object != null)
		{
			NType t = object.getNType ();
		findAdditionalFields:
			for (int i = 0; i < t.getManagedFieldCount (); i++)
			{
				NType.Field field = (NType.Field) t.getManagedField (i);
				if ((field.getAttribute () != null)
					|| ((field.getModifiers () & NType.Field.HIDDEN) != 0))
				{
					continue findAdditionalFields;
				}
				for (int j = object.getUserFieldCount () - 1; j >= 0; j--)
				{
					if (Reflection.membersEqual (field, object.getUserField (j), false))
					{
						continue findAdditionalFields;
					}
				}
				Property sp = p.createSubProperty (t, field, -1);
				if (sp != null)
				{
					PropertyEditor e = findEditor (p.getWorkbench (), sp.getType (), true);
					if (e != null)
					{
						PluginDescriptor pd = PluginDescriptor.getInstance (field.getDeclaringType ().getImplementationClass ());
						String descr = field.getName ();
						if (pd != null)
						{
							descr = pd.getI18NBundle ().getString (field.getDeclaringType ().getBinaryName () + '.' + descr + ".Name", descr);
						}
						Node n = e.createNodes (tree, sp, descr);
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
			if (object instanceof ExtraProperties)
			{
				for (Property ep : ((ExtraProperties) object).GetExtraProperties(p.getContext()))
				{
					PropertyEditor e = ep.getEditor();
					Node n = e.createNodes (tree, ep, ep.toString());
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
		return first;
	}

}
