
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

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import de.grogra.persistence.*;

public class Value extends LazyObjectItem
{
	Object value;
	//enh:field getmethod=getValueForXML setmethod=setValue


	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field value$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Value.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Value) o).setValue ((Object) value);
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
					return ((Value) o).getValueForXML ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Value ());
		$TYPE.addManagedField (value$FIELD = new _Field ("value", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Object.class), null, 0));
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
		return new Value ();
	}

//enh:end

	Value (String key, boolean sop)
	{
		super (key, sop);
	}


	Value (String key, boolean sop, Object value)
	{
		this (key, sop);
		setValue (value);
	}


	public Value ()
	{
		this (null, false);
	}


	public Value (String key, Object value)
	{
		this (key, false, value);
	}


	boolean storesAsString ()
	{
		return false;
	}


	public final void setValue (Object value)
	{
		setValue0 (value);
		setBaseObject (value);
	}

	private void setValue0 (Object value)
	{
		if (value != null)
		{
			setType (value.getClass ().getName ());
		}
		this.value = null;
	}

	
	@Override
	protected boolean hasNullValue ()
	{
		return false;
	}


	@Override
	protected Object fetchBaseObject () throws InstantiationException,
		IllegalAccessException, java.lang.reflect.InvocationTargetException,
		ClassNotFoundException
	{
		if (storesAsString () && (value != null))
		{
			Object o = new XMLPersistenceReader
				(getPersistenceManager (),
				 new PersistenceBindings (getRegistry (), getRegistry ()))
				 .valueOf (getObjectType (), (String) value);
			setValue0 (o);
			return o;
		}
		else
		{
			return value;
		}
	}


	String getValueAsString (XMLPersistenceWriter w)
	{
		return w.toString (getObjectType (), getObject ());
	}


	void setValueAsString (String s)
	{
		if ("null".equals (s))
		{
			setValue (null);
		}
		else
		{
			this.value = s;
		}
	}

	
	Object getValueForXML ()
	{
		return storesAsString () ? null : getObject ();
	}


	@Override
	protected void getAttributes (AttributesImpl attr, XMLPersistenceWriter w)
		throws SAXException
	{
		super.getAttributes (attr, w);
		if (!storesAsString ())
		{
			return;
		}
		String v = getValueAsString (w);
		int i = attr.getIndex ("", "value");
		if (i >= 0)
		{
			attr.setValue (i, v);
		}
		else
		{
			attr.addAttribute ("", "value", "value", "CDATA", v);
		}
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws SAXException
	{
		if ("".equals (uri) && "value".equals (name) && storesAsString ())
		{
			setValueAsString (value);
			return true;
		}
		else if ("".equals (uri) && "storeAsString".equals (name))
		{
			return true;
		}
		else
		{
			return super.readAttribute (uri, name, value);
		}
	}

}
