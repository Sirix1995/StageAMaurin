
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

package de.grogra.pf.registry;

import de.grogra.persistence.*;

public abstract class ItemReference<V> extends ShareableBase
{
	//enh:sco SCOType

	String name;
	//enh:field getter


	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field name$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ItemReference representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((ItemReference) o).name = (String) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((ItemReference) o).getName ();
			}
			return super.getObject (o, id);
		}
	}

	static
	{
		$TYPE = new Type (ItemReference.class);
		name$FIELD = Type._addManagedField ($TYPE, "name", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public String getName ()
	{
		return name;
	}

//enh:end

	protected transient boolean itemResolved;
	protected transient Item item;

	protected transient boolean objectResolved;
	protected transient V object;


	public ItemReference (String name)
	{
		this.name = name;
	}


	protected Item resolveItem (String baseName, RegistryContext ctx)
	{
		if (itemResolved)
		{
			return item;
		}
		StringBuffer b = new StringBuffer (baseName.length () + 8);
		b.append ("/project").append (baseName);
		String dir = b.toString ();
		Item result = null;
		Item d = Item.resolveItem (ctx, dir);
		if (d != null)
		{
			result = d.getItem (name);
			if (result != null)
			{
				result = result.resolveLink (ctx);
			}
		}
		if (result == null)
		{
			d = Item.resolveItem (ctx, b.delete (0, 8).toString ());
			if (d != null)
			{
				result = d.getItem (name);
				if (result != null)
				{
					result = result.resolveLink (ctx);
				}
			}
		}
		if (result == null)
		{
			result = createItem (ctx, dir, name);
		}
		item = result;
		itemResolved = true;
		return result;
	}
	
	
	protected Item createItem (RegistryContext ctx, String dir, String name)
	{
		return null;
	}


	protected V resolveObject (String baseName, RegistryContext ctx)
	{
		if (objectResolved)
		{
			return object;
		}
		Item i = resolveItem (baseName, ctx);
		if (i instanceof ObjectItem)
		{
			object = (V) ((ObjectItem) i).getObject ();
		}
		objectResolved = true;
		return object;
	}

	public boolean equals (Object o)
	{
		if (o.getClass () != getClass ())
		{
			return false;
		}
		String n = ((ItemReference) o).name;
		return (name == n) || ((n != null) && n.equals (name));
	}

	public int hashCode ()
	{
		return ((name != null) ? name.hashCode () : 0) ^ getClass ().hashCode ();
	}

}
