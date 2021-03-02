
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

package de.grogra.pf.ui.registry;

import java.util.EventObject;

import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Option;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.UIProperty;
import de.grogra.pf.ui.event.ActionEditEvent;
import de.grogra.pf.ui.tree.UINodeHandler;
import de.grogra.util.EventListener;

public final class CheckBoxItem extends Item implements UIItem, EventListener
{
	String declaring;
	//enh:field
	
	boolean option;
	//enh:field

	private transient UIProperty property;


	public CheckBoxItem ()
	{
		super (null, false);
	}


	public int getUINodeType ()
	{
		return UINodeHandler.NT_CHECKBOX_ITEM;
	}

	public Object invoke (Context ctx, String method, Object arg)
	{
		return "getCheckBoxItem".equals (method) ? this : null;
	}


	public static CheckBoxItem get (UINodeHandler handler, Object node)
	{
		return (CheckBoxItem) handler.invoke (node, "getCheckBoxItem", null);
	}


	public void setValue (Context ctx, boolean value)
	{
		getProperty ().setValue (ctx, value);
	}

	public void eventOccured (EventObject event)
	{
		if (!(event instanceof ActionEditEvent))
		{
			return;
		}
		setValue ((ActionEditEvent) event, Boolean.TRUE.equals (((ActionEditEvent) event).getParameter()));
	}

	public UIProperty getProperty ()
	{
		if (property == null)
		{
			property = UIProperty.get
				(getAbsoluteName (), declaring, getClassLoader ());
		}
		return property;
	}


	public boolean getValue (Context ctx)
	{
		Object v = getProperty ().getValue (ctx);
		if (v != null)
		{
			//System.out.println(this + ", wert: " + v);
			return Boolean.TRUE.equals (v);
		}
		return option && "true".equals (Option.getPreference (this));
	}


	public boolean isAvailable (Context ctx)
	{
		return true;
	}

	public boolean isEnabled (Context ctx)
	{
		return true;
	}


	//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field declaring$FIELD;
	public static final NType.Field option$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (CheckBoxItem.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 1:
					((CheckBoxItem) o).option = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 1:
					return ((CheckBoxItem) o).option;
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((CheckBoxItem) o).declaring = (String) value;
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
					return ((CheckBoxItem) o).declaring;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new CheckBoxItem ());
		$TYPE.addManagedField (declaring$FIELD = new _Field ("declaring", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
		$TYPE.addManagedField (option$FIELD = new _Field ("option", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 1));
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
		return new CheckBoxItem ();
	}

//enh:end

}
