
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

package de.grogra.pf.ui;

import java.util.Map;
import de.grogra.util.*;
import de.grogra.xl.util.ObjectList;
import de.grogra.pf.ui.event.*;

public class UIProperty
{
	public static final int WORKBENCH = 0;
	public static final int WINDOW = 1;
	public static final int PANEL = 2;

	private static final StringMap PROPERTIES = new StringMap (40);

	public static final UIProperty WORKBENCH_TITLE
		= new UIProperty ("workbenchTitle", WORKBENCH);
	public static final UIProperty PANEL_TITLE
		= new UIProperty ("panelTitle", PANEL);
	public static final UIProperty ICON = new UIProperty ("icon", PANEL);
	public static final UIProperty WINDOW_LAYOUT
		= new UIProperty ("windowLayout", WINDOW);

	public static final UIProperty WORKBENCH_SELECTION
		= new UIProperty ("workbenchSelection", WORKBENCH);

	private final int source;
	private final String name;
	private final boolean postEvent;

	private final Map globalListeners = new java.util.HashMap ();


	protected UIProperty (String name, int source)
	{
		this (name, source, true);
	}


	protected UIProperty (String name, int source, boolean postEvent)
	{
		this.name = name;
		this.source = source;
		this.postEvent = postEvent;
		synchronized (PROPERTIES)
		{
			if (PROPERTIES.put (name, this) != null)
			{
				throw new IllegalStateException ("UIProperty " + name
												 + " already registered");
			}
		}
	}


	public static UIProperty get (String name, String declaringClass,
								  ClassLoader loader)
	{
		synchronized (PROPERTIES)
		{
			UIProperty p = (UIProperty) PROPERTIES.get (name);
			if (p == null)
			{
				try
				{
					Class.forName (declaringClass, true, loader);
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace ();
				}
				p = (UIProperty) PROPERTIES.get (name);
			}
			return p;
		}
	}


	public static UIProperty getOrCreate (String name, int source)
	{
		synchronized (PROPERTIES)
		{
			UIProperty p = (UIProperty) PROPERTIES.get (name);
			if (p == null)
			{
				p = new UIProperty (name, source);
			}
			return p;
		}
	}


	public static int getSource (String source)
	{
		return source.equals ("workbench") ? WORKBENCH
			: source.equals ("window") ? WINDOW
			: source.equals ("panel") ? PANEL
			: -1;
	}


	public Context getSource (Context context)
	{
		switch (source)
		{
			case WORKBENCH:
				return context.getWorkbench ();
			case WINDOW:
				return context.getWindow ();
			case PANEL:
				return context.getPanel ().resolve ();
		}
		throw new IllegalStateException ("source == " + source);
	}


	private Map getMap (Context context)
	{
		switch (source)
		{
			case WORKBENCH:
				return context.getWorkbench ().propertyMap;
			case WINDOW:
				return context.getWindow ().getUIPropertyMap ();
			case PANEL:
				return context.getPanel ().getUIPropertyMap ();
		}
		throw new IllegalStateException ("source == " + source);
	}


	protected Object initialValue (Context context)
	{
		return null;
	}


	private Object[] getArray (Map map)
	{
		Object o;
		synchronized (map)
		{
			o = map.get (this);
		}
		return (o != null) ? (Object[]) o : null; 
	}


	private Object[] getOrCreateArray (Map map)
	{
		Object o;
		synchronized (map)
		{
			if ((o = map.get (this)) == null)
			{
				map.put (this, o = new Object[2]);
			}
		}
		return (Object[]) o;
	}


	private Object getListener (Map map)
	{
		Object[] a = getArray (map);
		return (a != null) ? a[0] : null;
	}


	public synchronized void firePropertyChange
		(Context ctx, Object oldValue, Object newValue, Object info)
	{
		Map m = getMap (ctx);
		Object s = getListener (m);
		UIPropertyEditEvent e = null;
		if (s != null)
		{
			e = new UIPropertyEditEvent (ctx, this, oldValue, newValue,
										 info);
			firePropertyChange (s, e);
		}
		if ((s = getListener (globalListeners)) != null)
		{
			if (e == null)
			{
				e = new UIPropertyEditEvent (ctx, this, oldValue, newValue,
											 info);
			}
			firePropertyChange (s, e);
		}
	}

	
	private static final Command FIRE = new Command ()
	{
		public String getCommandName ()
		{
			return null;
		}

		public void run (Object info, Context ctx)
		{
			fireImpl (info, (UIPropertyEditEvent) ctx);
		}
	};


	private void firePropertyChange (Object l, UIPropertyEditEvent e)
	{
		if (l instanceof ObjectList)
		{
			l = ((ObjectList) l).toArray ();
		}
		if (postEvent || !UI.getThreadContext (e).isCurrent ())
		{
			UI.getJobManager (e).runLater
				(FIRE, l, e, JobManager.UPDATE_FLAGS);
		}
		else
		{
			fireImpl (l, e);
		}
	}


	static void fireImpl (Object l, UIPropertyEditEvent e)
	{
		if (l instanceof Object[])
		{
			Object[] v = (Object[]) l;
			for (int i = 0; i < v.length; i++)
			{
				((EventListener) v[i]).eventOccured (e);
			}
		}
		else if (l != null)
		{
			((EventListener) l).eventOccured (e);
		}
	}


	public String getName ()
	{
		return name;
	}


	public void setValue (Context context, Object value)
	{
		Map m = getMap (context);
		Object[] a = getOrCreateArray (m);
		Object old = a[1];
		a[1] = (value == null) ? PROPERTIES : value;
		if (old == PROPERTIES)
		{
			old = null;
		}
		if ((old != value) && ((old == null) || !old.equals (value)))
		{
			firePropertyChange (context, old, value, null);
		}
	}


	public Object getValue (Context context)
	{
		Map m = getMap (context);
		Object[] a = getOrCreateArray (m);
		Object v = a[1];
		if (v == null)
		{
			v = initialValue (context);
			a[1] = (v == null) ? PROPERTIES : v;
			return v;
		}
		else if (v == PROPERTIES)
		{
			return null;
		}
		else
		{
			return v;
		}
	}


	public synchronized void addPropertyListener (Context context,
												  EventListener listener)
	{
		Map m = (context == null) ? globalListeners : getMap (context);
		Object l = getListener (m);
		if ((l != null) && (l.getClass () == ObjectList.class))
		{
			((ObjectList) l).addIfNotContained (listener);
		}
		else if ((l != null) && (l != listener))
		{
			ObjectList v = new ObjectList (5, false);
			v.add (l);
			v.addIfNotContained (listener);
			getArray (m)[0] = v;
		}
		else
		{
			getOrCreateArray (m)[0] = listener;
		}
	}


	public synchronized void removePropertyListener (Context context,
													 EventListener listener)
	{
		Map m = (context == null) ? globalListeners : getMap (context);
		Object l = getListener (m);
		if (l == listener)
		{
			getArray (m)[0] = null;
		}
		else if ((l != null) && (l.getClass () == ObjectList.class))
		{
			((ObjectList) l).remove (listener);
		}
	}

}
