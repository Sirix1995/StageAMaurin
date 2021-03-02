
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

import de.grogra.pf.registry.expr.Expression;
import de.grogra.util.*;

public class Void extends Executable
{
	private String method;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field method$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Void.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Void) o).method = (String) value;
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
					return ((Void) o).method;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Void ());
		$TYPE.addManagedField (method$FIELD = new _Field ("method", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
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
		return new Void ();
	}

//enh:end

	public Void ()
	{
		super (null);
	}


	public void invoke (Object instance, RegistryContext ctx, StringMap args)
	{
		Throwable t;
		try
		{
			if (method.indexOf ('.') >= 0)
			{
				Utils.invoke
					(method,
					 Expression.getArgs ((Item) getBranch (), ctx, args, this),
					 getClassLoader ());				
			}
			else
			{
				Utils.invokeVirtual
					(instance, method,
					 Expression.getArgs ((Item) getBranch (), ctx, args, this));
			}
			return;
		}
		catch (ClassNotFoundException e)
		{
			t = e;
		}
		catch (NoSuchMethodException e)
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
	}


	@Override
	public void run (RegistryContext ctx, StringMap args)
	{
		invoke (null, ctx, args);
	}

}
