
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

import de.grogra.util.*;
import de.grogra.persistence.*;
import de.grogra.pf.registry.expr.*;

public final class ConfigureInstance extends Executable
{
	String useOld;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field useOld$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (ConfigureInstance.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((ConfigureInstance) o).useOld = (String) value;
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
					return ((ConfigureInstance) o).useOld;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new ConfigureInstance ());
		$TYPE.addManagedField (useOld$FIELD = new _Field ("useOld", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
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
		return new ConfigureInstance ();
	}

//enh:end

	public ConfigureInstance ()
	{
		super (".config");
	}


	@Override
	public void run (RegistryContext ctx, StringMap args)
	{
		Object instance = args.get ("instance");
		if (instance == null)
		{
			return;
		}
		ManageableType mt = (instance instanceof Manageable)
			? ((Manageable) instance).getManageableType ()
			: ManageableType.forClass (instance.getClass ());
		if (mt != null)
		{
			for (Item i = (Item) getBranch (); i != null;
				 i = (Item) i.getSuccessor ())
			{
				ManageableType.Field f = mt.getManagedField (i.getName ());
				if (f != null)
				{
					Item r = i.resolveLink (ctx);
					if (r instanceof Expression)
					{
						f.set (instance, null, ((Expression) r).evaluate (ctx, args), null);
					}
					else if (r instanceof ObjectItem)
					{
						f.set (instance, null, ((ObjectItem) r).getObject (), null);
					}
				}
			}
		}
		Object oldValue;
		if ((useOld != null)
			&& ((oldValue = args.get ("oldValue")) != null))
		{
			Throwable t = null;
			try
			{
				if (useOld.indexOf ('.') >= 0)
				{
					Utils.invoke (useOld, new Object[] {instance, oldValue},
								  getClassLoader ());
				}
				else
				{
					Utils.invokeVirtual (instance, useOld,
										 new Object[] {oldValue});
				}
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
			if (t != null)
			{
				Utils.rethrow (t);
			}
		}
	}

}
