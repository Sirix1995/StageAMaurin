
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

import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.pf.registry.*;
import de.grogra.pf.registry.expr.ObjectExpr;
import de.grogra.pf.ui.edit.PropertyEditorTree.*;

public abstract class PropertyEditor extends Item
{
	public static final String NODE_VALUE = "propertyEditor";


	private static final ItemComparator SPECIFICITY = new ItemComparator ()
		{
			private Type check (Item item, Object o, Object[] a)
			{
				Type type;
				if (item instanceof PropertyEditor)
				{
					type = ((PropertyEditor) item).getPropertyType ();
				}
				else if (item instanceof ObjectExpr)
				{
					type = ((ObjectExpr) item).getObjectType ();
				}
				else
				{
					return null;
				}
				if (a.length == 4)
				{
					if (!((ItemCriterion) a[2]).isFulfilled (item, a[3]))
					{
						return null;
					}
				}
				if (o instanceof Type)
				{
					if (Reflection.isSupertypeOrSame (type, (Type) o))
					{
						return type;
					}
				}
				else if ((o != null) && !(item instanceof PolyEditor))
				{
					if (type.isInstance (o))
					{
						return type;
					}
				}
				return null;
			}


			public int compare (Item item1, Item item2, Object info)
			{
				Object[] a = (Object[]) info;
				info = a[0];
				boolean nullPossible = Boolean.TRUE.equals (a[1]);
				Type t1 = check (item1, info, a);
				Type t2 = check (item2, info, a);
				if (t1 == null)
				{
					return (t2 == null) ? 0 : -1;
				}
				else
				{
					if (t2 == null)
					{
						return 1;
					}
					if (Reflection.isSupertype (t1, t2))
					{
						return -1;
					}
					if (Reflection.isSupertype (t2, t1))
					{
						return 1;
					}
					if ((item1 instanceof PropertyEditor)
						&& (item2 instanceof PropertyEditor))
					{
						boolean na1 = ((PropertyEditor) item1).isNullAllowed (),
							na2 = ((PropertyEditor) item2).isNullAllowed ();
						if (na1 && !na2)
						{
							return nullPossible ? 1 : -1;
						}
						if (!na1 && na2)
						{
							return nullPossible ? -1 : 1;
						}
					}
					if ((item1 instanceof ObjectExpr)
						&& (item2 instanceof ObjectExpr))
					{
						boolean a1 = ((ObjectExpr) item1).isAlias ();
						boolean a2 = ((ObjectExpr) item2).isAlias ();
						if (a2 && !a1)
						{
							return 1;
						}
						if (a1 && !a2)
						{
							return -1;
						}
					}
					return 0;
				}
			}
		};


	public static PropertyEditor findEditor (RegistryContext c, Type t,
											 boolean nullPossible)
	{
		return (PropertyEditor) findMax
			(c, "/ui/editors", SPECIFICITY,
			 new Object[] {t, Boolean.valueOf (nullPossible)}, true);
	}


	public static PropertyEditor findNonpolyEditor
		(RegistryContext c, Object value, boolean nullPossible)
	{
		return (PropertyEditor) findMax
			(c, "/ui/editors", SPECIFICITY,
			 new Object[] {value, Boolean.valueOf (nullPossible)}, true);
	}


	public static PropertyEditor findEditor
		(RegistryContext c, Type t, boolean nullPossible, ItemCriterion crit, Object info)
	{
		return (PropertyEditor) findMax
			(c, "/ui/editors", SPECIFICITY,
			 new Object[] {t, Boolean.valueOf (nullPossible), crit, info}, true);
	}


	String type;
	//enh:field

	private Type propertyType;

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field type$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (PropertyEditor.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((PropertyEditor) o).type = (String) value;
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
					return ((PropertyEditor) o).type;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (PropertyEditor.class);
		$TYPE.addManagedField (type$FIELD = new _Field ("type", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
		$TYPE.validate ();
	}

//enh:end


	public PropertyEditor (String key)
	{
		super (key);
	}


	public abstract boolean isNullAllowed ();

	
	public abstract Node createNodes (PropertyEditorTree tree, Property p, String labelPrefix);


	public Type getPropertyType ()
	{
		if (propertyType == null)
		{
			propertyType = Reflection.getType (type, getClassLoader ());
		}
		return propertyType;
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws org.xml.sax.SAXException
	{
		if ("".equals (uri) && "type".equals (name))
		{
			type = value;
			setNameIfNull (this, value);
			return true;
		}
		return super.readAttribute (uri, name, value);
	}

	
	static Item findMostSpecificItem (RegistryContext ctx, Type type)
	{
		return findMax (ctx, "/objects", SPECIFICITY,
			  new Object[] {type, Boolean.FALSE},
			  false);
	}


	@Override
	protected Object getDefaultDescription (String type)
	{
		Item i = findMostSpecificItem (this, getPropertyType ());
		return (i != null) ? i.getDescription (type)
			: super.getDefaultDescription (type);
	}

}
