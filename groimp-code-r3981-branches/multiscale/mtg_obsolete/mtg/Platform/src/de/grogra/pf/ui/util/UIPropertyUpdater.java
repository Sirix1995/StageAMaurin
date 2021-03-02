
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

package de.grogra.pf.ui.util;

import de.grogra.util.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.event.*;
import java.lang.reflect.*;
import java.util.concurrent.Executor;

public class UIPropertyUpdater implements EventListener
{
	private final UIProperty property;
	private final String method;
	private final boolean component;
	private final Executor queue;


	public UIPropertyUpdater (UIProperty property, Context ctx,
							  boolean forComponent, String method,
							  Executor queue, boolean updateImmediately)
	{
		this.property = property;
		this.component = forComponent;
		this.method = method;
		this.queue = queue;
		if (updateImmediately)
		{
			update (ctx);
		}
		property.addPropertyListener (ctx, this);
	}


	public static UIPropertyUpdater install (UIProperty property, Context ctx,
											 boolean forComponent, String method,
											 Executor queue, Map init)
	{
		Object o;
		boolean update;
		if ((init != null)
			&& ((o = init.get (property.getName (), Map.DEFAULT_VALUE))
				!= Map.DEFAULT_VALUE))
		{
			property.setValue (ctx, o);
			update = true;
		}
		else
		{
			o = property.getValue (ctx);
			update = o != null;
		}
		UIPropertyUpdater u = new UIPropertyUpdater (property, ctx, forComponent,
													 method, queue, false);
		if (update)
		{
			u.update (ctx, o);
		}
		return u;
	}


	public void dispose (Context ctx)
	{
		property.removePropertyListener (ctx, this); 
	}


	public void eventOccured (java.util.EventObject e)
	{
		if (e instanceof UIPropertyEditEvent)
		{
			update ((UIPropertyEditEvent) e,
					((UIPropertyEditEvent) e).getNewValue ());
		}
	}


	public void update (Context ctx)
	{
		update (ctx, property.getValue (ctx));	
	}


	void update (final Context ctx, final Object newValue)
	{
		if (queue == null)
		{
			updateCallback (ctx, newValue);
		}
		else
		{
			queue.execute (new Runnable ()
				{
					public void run ()
					{
						updateCallback (ctx, newValue);
					}
				});
		}
	}


	void updateCallback (Context ctx, Object newValue)
	{
		Context c = property.getSource (ctx);
		Object t = component ? c.getComponent () : c;
		Method[] m = t.getClass ().getMethods ();
		int i;
		for (i = m.length - 1; i >= 0; i--)
		{
			if (m[i].getName ().equals (method)
				&& ((m[i].getModifiers () & Modifier.STATIC) == 0)
				&& (m[i].getParameterTypes ().length == 1))
			{
				break;
			}
		}
		if (i < 0)
		{
			System.err.println ("Method " + method
								+ " not found for " + t);
			Thread.dumpStack ();
			return;
		}
		try
		{
			m[i].invoke (t, new Object[] {newValue});
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
}
