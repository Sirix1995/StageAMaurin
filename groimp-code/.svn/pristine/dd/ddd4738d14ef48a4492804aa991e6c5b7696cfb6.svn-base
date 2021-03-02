
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
import de.grogra.pf.registry.expr.*;

public class LazyObjectItem extends ObjectItem
{
	private boolean objectSet;
	private Object baseObject;
	private final boolean soProvider;
	
	private boolean createInstance;

	private boolean fetch = false;
	//enh:field

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field fetch$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (LazyObjectItem.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 0:
					((LazyObjectItem) o).fetch = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 0:
					return ((LazyObjectItem) o).fetch;
			}
			return super.getBoolean (o);
		}
	}

	static
	{
		$TYPE = new NType (new LazyObjectItem ());
		$TYPE.addManagedField (fetch$FIELD = new _Field ("fetch", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 0));
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
		return new LazyObjectItem ();
	}

//enh:end


	public LazyObjectItem ()
	{
		this (null, true);
	}


	public LazyObjectItem (String key, boolean soProvider)
	{
		super (key);
		this.soProvider = soProvider;
	}


	@Override
	public boolean isObjectFetched ()
	{
		if (!objectSet && (fetch || hasNullValue ()))
		{
			getObject ();
		}
		return objectSet;
	}


	@Override
	public Object getObject ()
	{
		return getBaseObjectImpl ();
	}


	protected final Object getBaseObjectImpl ()
	{
		if (!objectSet)
		{
			Object o;
			try
			{
				o = fetchBaseObject ();
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				o = null;
			}
			setBaseObject (o);
			return baseObject;
		}
		return baseObject;
	}


	protected final void setBaseObject (Object object)
	{
		this.baseObject = object;
		if (soProvider && (object instanceof Shareable))
		{
			((Shareable) object).initProvider (this);
		}
		objectSet = true;
	}

	
	protected boolean hasNullValue ()
	{
		return !createInstance && (getBranchLength () == 0);
	}


	protected Object fetchBaseObject () throws InstantiationException,
		IllegalAccessException, java.lang.reflect.InvocationTargetException,
		ClassNotFoundException
	{
		if (createInstance)
		{
			return getObjectType ().newInstance ();
		}
		Object[] a = Expression.getArgs
			((Item) getBranch (), getRegistry (),
			 new de.grogra.util.StringMap (), this);
		return (a.length == 0) ? null : a[0];
	}


	@Override
	protected boolean getTypeFromObject ()
	{
		return objectSet && (getObject () != null);
	}


	@Override
	public void addPluginPrerequisites (java.util.Collection list)
	{
		super.addPluginPrerequisites (list);
		if (getTypeFromObject ())
		{
			Object o = getObject ();
			if (o != null)
			{
				addPluginPrerequisite (list, o.getClass ());
			}
		}
		else if (type != null)
		{
			addPluginPrerequisite
				(list, getObjectTypeImpl ().getImplementationClass ());
		}
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws org.xml.sax.SAXException
	{
		if ("".equals (uri))
		{
			if ("create".equals (name))
			{
				setType (value);
				createInstance = true;
				return true;
			}
		}
		return super.readAttribute (uri, name, value);
	}

}
