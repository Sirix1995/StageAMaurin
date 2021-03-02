
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

package de.grogra.pf.registry.expr;

import de.grogra.reflect.*;
import de.grogra.persistence.PersistenceBindings;
import de.grogra.pf.registry.*;
import de.grogra.util.*;

public class ObjectExpr extends Expression
{
	private String type;
	//enh:field

	protected String expr;
	//enh:field

	boolean alias = false;
	//enh:field getter

	private Type objectType;

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field type$FIELD;
	public static final NType.Field expr$FIELD;
	public static final NType.Field alias$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (ObjectExpr.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 2:
					((ObjectExpr) o).alias = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 2:
					return ((ObjectExpr) o).isAlias ();
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((ObjectExpr) o).type = (String) value;
					return;
				case 1:
					((ObjectExpr) o).expr = (String) value;
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
					return ((ObjectExpr) o).type;
				case 1:
					return ((ObjectExpr) o).expr;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new ObjectExpr ());
		$TYPE.addManagedField (type$FIELD = new _Field ("type", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
		$TYPE.addManagedField (expr$FIELD = new _Field ("expr", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 1));
		$TYPE.addManagedField (alias$FIELD = new _Field ("alias", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 2));
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
		return new ObjectExpr ();
	}

	public boolean isAlias ()
	{
		return alias;
	}

//enh:end


	public ObjectExpr ()
	{
		super (null);
	}


	@Override
	public Object evaluate (RegistryContext ctx, StringMap args)
	{
		Throwable t;
		try
		{
			Item i = (Item) getBranch ();
			Object instance;
			if (expr.indexOf ('.') >= 0)
			{
				Object[] a = getArgs (i, ctx, args, this);
				instance = Utils.evaluate (expr, a, getClassLoader ());
			}
			else
			{
				while (!(i instanceof Expression))
				{
					if (i instanceof Executable)
					{
						((Executable) i).run (ctx, args);
					}
					i = (Item) i.getSuccessor ();
				}
				instance = ((Expression) i).evaluate (ctx, args);
				Object[] a = getArgs ((Item) i.getSuccessor (), ctx, args, this);
				instance = Utils.invokeVirtual (instance, expr, a);
			}
			return configure (instance, ctx, args);
		}
		catch (ClassNotFoundException e)
		{
			t = e;
		}
		catch (NoSuchMethodException e)
		{
			t = e;
		}
		catch (NoSuchFieldException e)
		{
			t = e;
		}
		catch (IllegalAccessException e)
		{
			t = e;
		}
		catch (InstantiationException e)
		{
			t = e;
		}
		catch (java.lang.reflect.InvocationTargetException e)
		{
			t = e.getCause ();
		}			 
		Utils.rethrow (t);
		return null;
	}


	public Type getObjectType ()
	{
		if (objectType == null)
		{
			objectType = Reflection.getType (type, getClassLoader ());
		}
		return objectType;
	}


	protected String getTypeAsString ()
	{
		return type;
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws org.xml.sax.SAXException
	{
		if ("".equals (uri))
		{
			if ("class".equals (name))
			{
				type = value;
				expr = value;
				return true;
			}
		}
		return super.readAttribute (uri, name, value);
	}


	@Override
	protected Item createItem (PersistenceBindings pb,
							   String name)
		throws InstantiationException, IllegalAccessException,
		java.lang.reflect.InvocationTargetException, ClassNotFoundException
	{
		return "config".equals (name) ? new ConfigureInstance ()
			: super.createItem (pb, name);
	}


	protected Object configure
		(Object instance, RegistryContext ctx, StringMap args)
	{
		Item i = getItem (".config");
		if (i instanceof Executable)
		{
			args.put ("instance", instance);
			((Executable) i).run (ctx, args);
		}
		return instance;
	}

}
