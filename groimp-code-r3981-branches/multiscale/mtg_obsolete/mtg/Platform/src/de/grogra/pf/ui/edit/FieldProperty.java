
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
import de.grogra.xl.util.IntList;
import de.grogra.persistence.IndirectField;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceField;
import de.grogra.pf.ui.*;

public abstract class FieldProperty extends Property
{
	final Object object;
	final PersistenceField field;
	final int[] indices;
	final int index;

	
	FieldProperty (Context context, Object object, PersistenceField field,
				   int[] indices, int index)
	{
		super (context, field.getType ());
		this.object = object;
		this.field = field;
		this.indices = indices;
		this.index = index;
		setQuantity (field.getQuantity ());
	}


	int[] addIndex (int i)
	{
		return (i < 0) ? indices : (indices == null) ? new int[] {i}
			: new IntList (indices).push (i).toArray ();
	}


	@Override
	public String toString ()
	{
		return field.toString ();
	}
	
	
	@Override
	public Object getValue ()
	{
		return field.get (object, indices);
	}


	@Override
	public boolean isWritable ()
	{
		return true;
	}

	
	@Override
	public Property createSubProperty (Type actualType, Field f, int i)
	{
		if (!(f instanceof ManageableType.Field))
		{
			return null;
		}
		return createSubProperty (IndirectField.concat (field, (ManageableType.Field) f), i);
	}
	
	
	protected abstract FieldProperty createSubProperty (PersistenceField f, int i);

}
