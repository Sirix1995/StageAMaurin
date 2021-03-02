
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

import java.lang.reflect.*;

import de.grogra.persistence.PersistenceBindings;

public final class Application extends Item implements Runnable
{
	private String run;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field run$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Application.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Application) o).run = (String) value;
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
					return ((Application) o).run;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Application ());
		$TYPE.addManagedField (run$FIELD = new _Field ("run", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
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
		return new Application ();
	}

//enh:end


	Application ()
	{
		super (null);
	}


	public void run ()
	{
		try
		{
			de.grogra.util.Utils.invoke (run, new Object[] {this},
										 getClassLoader ());
			de.grogra.pf.boot.Main.exit ();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace ();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace ();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace ();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace ();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace ();
		}
	}


	@Override
	protected Item createItem (PersistenceBindings pb,
							   String name)
		throws InstantiationException, IllegalAccessException,
		java.lang.reflect.InvocationTargetException, ClassNotFoundException
	{
		if ("arg".equals (name))
		{
			return new Argument ();
		}
		return super.createItem (pb, name);
	}

}
