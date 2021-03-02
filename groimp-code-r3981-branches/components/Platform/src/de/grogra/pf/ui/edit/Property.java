
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

import de.grogra.util.*;
import de.grogra.reflect.*;
import de.grogra.persistence.ManageableType;
import de.grogra.pf.ui.*;

public abstract class Property
{
	private final Context context;
	private final Type type;
	private Quantity quantity;

	
	public Property (Context context, Type type)
	{
		this.context = context;
		this.type = type;
	}

	
	public Context getContext ()
	{
		return context;
	}

	
	public Workbench getWorkbench ()
	{
		return context.getWorkbench ();
	}

	
	public UIToolkit getToolkit ()
	{
		return UIToolkit.get (context);
	}


	public PropertyEditor getEditor ()
	{
		return null;
	}
	
	
	public Type getType ()
	{
		return type;
	}

	
	protected void setQuantity (Quantity q)
	{
		quantity = q;
	}


	public Quantity getQuantity ()
	{
		return quantity;
	}


	public Property createSubProperty (Type actualType, String name, int index)
	{
		if (name.length () == 0)
		{
			return this;
		}
		ManageableType m = ManageableType.forType (actualType);
		Field f = (m != null) ? m.getManagedField (name) : null;
		if (f == null)
		{
			return null;
		}
		return createSubProperty (actualType, f, index);
	}


	public abstract Property createSubProperty (Type actualType, Field name, int index);
	
	public abstract boolean isWritable ();

	public abstract void setValue (Object value) throws InterruptedException;

	public abstract Object getValue ();
}
