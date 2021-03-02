
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

import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.tree.UINodeHandler;

public final class ChoiceGroup extends Group
{
	String declaring;
	//enh:field
	
	boolean option;
	//enh:field

	private transient UIProperty property;

//enh:insert
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
			super (ChoiceGroup.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 1:
					((ChoiceGroup) o).option = (boolean) value;
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
					return ((ChoiceGroup) o).option;
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((ChoiceGroup) o).declaring = (String) value;
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
					return ((ChoiceGroup) o).declaring;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new ChoiceGroup ());
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
		return new ChoiceGroup ();
	}

//enh:end


	private ChoiceGroup ()
	{
		super (null, false);
	}


	@Override
	public int getUINodeType ()
	{
		return UINodeHandler.NT_CHOICE_GROUP;
	}


	@Override
	public Object invoke (Context ctx, String method, Object arg)
	{
		return "getChoiceGroup".equals (method) ? this
			: super.invoke (ctx, method, arg);
	}


	public static ChoiceGroup get (UINodeHandler handler, Object node)
	{
		return (ChoiceGroup) handler.invoke (node, "getChoiceGroup", null);
	}


	public void setSelected (Context ctx, UINodeHandler handler, Object node)
	{
		node = handler.resolveLink (node);
		if (option)
		{
			Option.setPreference (this, ((Item) node).getAbsoluteName ());
		}
		getProperty ().setValue (ctx, node);
	}


	public boolean isSelected (Context ctx, UINodeHandler handler, Object node)
	{	
		return de.grogra.util.Utils.equal (handler.resolveLink (node), getPropertyValue (ctx));
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


	public Item getPropertyValue (Context ctx)
	{
		Item c = (Item) getProperty ().getValue (ctx);
		if (c != null)
		{
			return c;
		}
		if (option)
		{
			String s = Option.getPreference (this);
			if (s != null)
			{
				c = Item.resolveItem (ctx.getWorkbench (), s);
				if (c != null)
				{
					return c;
				}
			}
		}
		return (Item) getBranch ();
	}


	@Override
	public boolean isAvailable (Context ctx)
	{
		return true;
	}

}
