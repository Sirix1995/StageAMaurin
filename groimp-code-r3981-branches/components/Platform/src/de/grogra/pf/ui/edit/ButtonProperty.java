
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

import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.registry.CommandItem;
import de.grogra.reflect.Field;
import de.grogra.reflect.Type;

public class ButtonProperty extends Property
{
	private PropertyEditor editor;
	private String name;

	public ButtonProperty (Context context, String name, PropertyEditor e)
	{
		super (context, null);
		this.editor = e;
		this.name = name;
	}

	public ButtonProperty (Context context, String name, Command command)
	{
		super (context, null);
		ButtonEditor e = new ButtonEditor(null);
		e.add(new CommandItem(null, command));
		this.editor = e;
		this.name = name;
	}

	public PropertyEditor getEditor ()
	{
		return editor;
	}
	
	public ButtonProperty createSubProperty (Type actualType, Field name, int index)
	{
		return null;
	}
	
	public boolean isWritable ()
	{
		return true;
	}

	public void setValue (Object value)
	{
	}

	public Object getValue ()
	{
		return null;
	}

	public String toString()
	{
		return name;
	}
}
