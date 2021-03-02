
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

import de.grogra.util.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.registry.expr.*;
import de.grogra.pf.ui.*;

public class PanelFactory extends Item implements Command, UIItem
{
	private static final int HIDDEN_MASK = 1 << Item.USED_BITS;
	public static final int USED_BITS = Item.USED_BITS + 1;

	// boolean hidden
	//enh:field type=bits(HIDDEN_MASK)
	
	String source;
	//enh:field

	public String getFactorySource ()
	{
		return source;
	}

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field hidden$FIELD;
	public static final NType.Field source$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (PanelFactory.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((PanelFactory) o).source = (String) value;
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
					return ((PanelFactory) o).source;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new PanelFactory ());
		$TYPE.addManagedField (hidden$FIELD = new NType.BitField ($TYPE, "hidden", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, HIDDEN_MASK));
		$TYPE.addManagedField (source$FIELD = new _Field ("source", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
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
		return new PanelFactory ();
	}

//enh:end

	PanelFactory ()
	{
		super (null);
	}


	public PanelFactory (String name, String source)
	{
		super (name);
		this.source = source;
	}

	
	public String getDefaultTitle ()
	{
		return (String) getDescription (NAME);
	}


	@Override
	public Object get (Object key, Object defaultValue)
	{
		Object o = super.get (key, DEFAULT_VALUE);
		if (o != DEFAULT_VALUE)
		{
			return o;
		}
		if (Panel.PANEL_ID.equals (key))
		{
			return getAbsoluteName ();
		}
		else if (UIProperty.PANEL_TITLE.getName ().equals (key))
		{
			o = getDefaultTitle ();
		}
		else if (UIProperty.ICON.getName ().equals (key))
		{
			o = getDescription (ICON);
		}
		else
		{
			o = null;
		}
		return (o == null) ? defaultValue : o;
	}


	@Override
	protected Object getDefaultDescription (String type)
	{
		Item i;
		return ((source != null)
				&& ((i = Link.resolveLink (this, source, this)) != null))
			? i.getDescription (type)
			: super.getDefaultDescription (type);
	}


	public Panel createPanel (Context ctx, Map params)
	{
		if (source != null)
		{
			Item src = Link.resolveLink (this, source, ctx.getWorkbench ());
			if (!(src instanceof PanelFactory))
			{
				return null;
			}
			return ((PanelFactory) src).createPanel
				(ctx, Map.Chain.add (params, this));
		}
		Panel p = null;
		Item menu = null;
		StringMap sm = UI.getArgs (ctx, Map.Chain.add (params, this));
		for (Item i = (Item) getBranch (); i != null;
			 i = (Item) i.getSuccessor ())
		{
			if (i instanceof Expression)
			{
				if (p == null)
				{
					Object o = ((Expression) i).evaluate (ctx.getWorkbench (),
														  sm);
					if (o instanceof Panel)
					{
						p = (Panel) o;
					}
					else if (Boolean.FALSE.equals (o))
					{
						return null;
					}
				}
			}
			else if (i.hasName ("menu"))
			{
				menu = i.resolveLink (ctx.getWorkbench ());
			}
		}
		return configure (ctx, p, menu);
	}


	protected Panel configure (Context ctx, Panel p, Item menu)
	{
		if ((p != null) && (menu != null))
		{
			UI.setMenu (p, menu, null);
		}
		return p;
	}


	public static Panel createPanel (Context ctx, String id, Map params)
	{
		int p = id.indexOf ('?');
		if (p >= 0)
		{
			params = new StringMap (params).putObject (Panel.PANEL_ID, id);
			id = id.substring (0, p);
		}
		Item i = resolveItem (ctx.getWorkbench (), id);
		return (i instanceof PanelFactory)
			? ((PanelFactory) i).createPanel (ctx, params) : null;
	}


	public static Panel getAndShowPanel (Context ctx, String id, Map params)
	{
		Window w = ctx.getWindow ();
		if (w == null)
		{
			return null;
		}
		Panel p = w.getPanel (id);
		if (p == null)
		{
			p = createPanel (ctx, id, params);
		}
		if (p != null)
		{
			p.show (false, null);
		}
		return p;
	}


	public String getCommandName ()
	{
		return null;
	}


	public void run (Object info, Context ctx)
	{
		if (ctx.getWorkbench ().isHeadless ())
		{
			return;
		}
		Panel p = ctx.getWindow ().getPanel (getAbsoluteName ());
		if (p == null)
		{
			p = createPanel (ctx, null);
		}
		if (p != null)
		{
			p.show (true, null);
		}
	}


	public Object invoke (Context ctx, String method, Object arg)
	{
		return null;
	}


	public boolean isAvailable (Context ctx)
	{
		return ((bits & HIDDEN_MASK) == 0) && UI.isAvailable (this, ctx);
	}


	public boolean isEnabled (Context ctx)
	{
		return true;
	}


	public int getUINodeType ()
	{
		return de.grogra.pf.ui.tree.UINodeHandler.NT_ITEM;
	}

}
