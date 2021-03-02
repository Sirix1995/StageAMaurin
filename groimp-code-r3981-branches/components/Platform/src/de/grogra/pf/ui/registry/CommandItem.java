
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

import de.grogra.pf.registry.Executable;
import de.grogra.pf.registry.RegistryContext;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.event.ActionEditEvent;
import de.grogra.pf.ui.event.ClickEvent;
import de.grogra.pf.ui.event.DragEvent;
import de.grogra.pf.ui.tree.UINodeHandler;
import de.grogra.util.EventListener;
import de.grogra.util.StringMap;
import de.grogra.util.Utils;

public class CommandItem extends Executable
	implements Command, UIItem, EventListener, Runnable
{
	private static final int MOTION_MASK = 1 << Executable.USED_BITS;
	private static final int PLUGIN_MASK = 1 << Executable.USED_BITS + 1;
	public static final int USED_BITS = Executable.USED_BITS + 2;

	// boolean motion
	//enh:field type=bits(MOTION_MASK)

	// boolean plugin
	//enh:field type=bits(PLUGIN_MASK)

	String run;
	//enh:field

	String enabled;
	//enh:field
	
	String cls;
	//enh:field
	
	boolean immediate = false;
	//enh:field

	private Command command;
	
	private Runnable immediateListener;


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field motion$FIELD;
	public static final NType.Field plugin$FIELD;
	public static final NType.Field run$FIELD;
	public static final NType.Field enabled$FIELD;
	public static final NType.Field cls$FIELD;
	public static final NType.Field immediate$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (CommandItem.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 3:
					((CommandItem) o).immediate = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 3:
					return ((CommandItem) o).immediate;
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((CommandItem) o).run = (String) value;
					return;
				case 1:
					((CommandItem) o).enabled = (String) value;
					return;
				case 2:
					((CommandItem) o).cls = (String) value;
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
					return ((CommandItem) o).run;
				case 1:
					return ((CommandItem) o).enabled;
				case 2:
					return ((CommandItem) o).cls;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new CommandItem ());
		$TYPE.addManagedField (motion$FIELD = new NType.BitField ($TYPE, "motion", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, MOTION_MASK));
		$TYPE.addManagedField (plugin$FIELD = new NType.BitField ($TYPE, "plugin", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, PLUGIN_MASK));
		$TYPE.addManagedField (run$FIELD = new _Field ("run", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
		$TYPE.addManagedField (enabled$FIELD = new _Field ("enabled", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 1));
		$TYPE.addManagedField (cls$FIELD = new _Field ("cls", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 2));
		$TYPE.addManagedField (immediate$FIELD = new _Field ("immediate", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 3));
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
		return new CommandItem ();
	}

//enh:end

	private CommandItem ()
	{
		this (null);
	}


	public CommandItem (String key)
	{
		super (key);
	}


	public CommandItem (String key, Command command)
	{
		super (key);
		this.command = command;
	}


	public CommandItem (String key, Runnable listener)
	{
		super (key);
		this.immediate = true;
		this.immediateListener = listener;
	}


	public int getUINodeType ()
	{
		return ((bits & MOTION_MASK) != 0) ? UINodeHandler.NT_MOUSE_MOTION
			: UINodeHandler.NT_ITEM;
	}


	public String getCommandName ()
	{
		return (String) getDescription (SHORT_DESCRIPTION);
	}

	
	public void run ()
	{
		try
		{
			Utils.invoke (run, new Object[] {this}, getClassLoader ());
		}
		catch (Exception e)
		{
			Utils.rethrow (e);
		}
	}


	public void run (Object info, Context ctx)
	{
		if ((bits & MOTION_MASK) != 0)
		{
			if (!(info instanceof DragEvent))
			{
				if (info instanceof ClickEvent)
				{
					de.grogra.pf.registry.Plugin p = getPluginDescriptor ().getPlugin ();
					if (p instanceof CommandPlugin)
					{
						((CommandPlugin) p).run (info, ctx, this);
					}
				}
				return;
			}
		}
		else if ((info instanceof EventObject)
				 && !(info instanceof ActionEditEvent))
		{
			return;
		}
		if ((cls != null) && (command == null))
		{
			try
			{
				command = (Command) Class.forName (cls, false, getClassLoader ()).newInstance ();
			}
			catch (Exception e)
			{
				Utils.rethrow (e);
			}
		}
		if (command != null)
		{
			command.run (info, ctx);
		}
		else if (run != null)
		{
			Throwable t;
			try
			{
				Utils.invoke (run, new Object[] {this, info, ctx}, getClassLoader ());
				return;
			}
			catch (InstantiationException e)
			{
				t = e;
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
			catch (java.lang.reflect.InvocationTargetException e)
			{
				t = e.getCause ();
			}
			Utils.rethrow (t);
		}
		else if ((bits & PLUGIN_MASK) != 0)
		{
			de.grogra.pf.registry.Plugin p
				= getPluginDescriptor ().getPlugin ();
			if (p instanceof CommandPlugin)
			{
				((CommandPlugin) p).run (info, ctx, this);
			}
		}
		else
		{
			Executable.runExecutables
				(this, ctx.getWorkbench (), UI.getArgs (ctx, null)
				 .putObject ("info", info).putObject ("item", this));
		}
	}


	public Object invoke (Context ctx, String method, Object arg)
	{
		if (UINodeHandler.GET_IMMEDIATE_LISTENER_METHOD.equals (method))
		{
			if (immediate)
			{
				return (immediateListener != null) ? immediateListener : this;
			}
			else
			{
				return null;
			}
		}
		run (arg, ctx);
		return null;
	}


	@Override
	public void run (RegistryContext ctx, StringMap args)
	{
		Context c = (Context) args.get ("context");
		if (c != null)
		{
			run (args.get ("info"), c);
		}
	}


	public boolean isAvailable (Context ctx)
	{
		return UI.isAvailable (this, ctx);
	}


	public boolean isEnabled (Context ctx)
	{
		return UI.isEnabled (this, ctx);
	}


	public void eventOccured (java.util.EventObject e)
	{
		if (e instanceof ActionEditEvent)
		{
			run (e, (ActionEditEvent) e);
			((ActionEditEvent) e).consume ();
		}
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws org.xml.sax.SAXException
	{
		if ("".equals (uri))
		{
			if ("class".equals (name))
			{
				cls = value;
				return true;
			}
		}
		return super.readAttribute (uri, name, value);
	}


}
