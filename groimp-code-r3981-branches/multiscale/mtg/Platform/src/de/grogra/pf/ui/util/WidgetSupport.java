
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

import java.beans.*;

import de.grogra.xl.util.ObjectList;
import de.grogra.pf.ui.*;

public abstract class WidgetSupport implements Widget
{
	private boolean disposed = false;
	private Object listenerLock = new Object ();
	private ObjectList listeners = new ObjectList (4, false);
	private ObjectList vetoListeners = new ObjectList (4, false);


	public static PropertyChangeEvent convert (WidgetConversion conversion,
											   PropertyChangeEvent e)
		throws PropertyVetoException
	{
		if (conversion == null)
		{
			return e;
		}
		PropertyChangeEvent pe;
		try 
		{
			pe = new PropertyChangeEvent
				(e.getSource (), e.getPropertyName (),
				 conversion.fromWidget (e.getOldValue ()),
				 conversion.fromWidget (e.getNewValue ()));
		}
		catch (RuntimeException r)
		{
			PropertyVetoException ve
				= new PropertyVetoException (r.getMessage (), e);
			ve.initCause (r);
			throw ve;
		}
		pe.setPropagationId (e.getPropagationId ());
		return pe;
	}


	protected void installListener (boolean hasListeners)
	{
	}


	public void addPropertyChangeListener (PropertyChangeListener listener)
	{
		synchronized (listenerLock)
		{
			listeners.addIfNotContained (listener);
			installListener (!listeners.isEmpty () || !vetoListeners.isEmpty ());
		}
	}


	public void removePropertyChangeListener (PropertyChangeListener listener)
	{
		synchronized (listenerLock)
		{
			listeners.remove (listener);
			installListener (!listeners.isEmpty () || !vetoListeners.isEmpty ());
		}
	}


	protected void firePropertyChange (Object oldValue, Object newValue)
	{
		PropertyChangeListener[] l;
		synchronized (listenerLock)
		{
			if (listeners.size () == 0)
			{
				return;
			}
			else
			{
				l = new PropertyChangeListener[listeners.size ()];
				listeners.toArray (l);
			}
		}
		PropertyChangeEvent e = new PropertyChangeEvent
			(this, WIDGET_VALUE_PROPERTY, oldValue, newValue);
		for (int i = l.length - 1; i >= 0; i--)
		{
			l[i].propertyChange (e);
		}
	}


	protected void firePropertyChange (PropertyChangeEvent e)
	{
		PropertyChangeListener[] l;
		synchronized (listenerLock)
		{
			if (listeners.size () == 0)
			{
				return;
			}
			else
			{
				l = new PropertyChangeListener[listeners.size ()];
				listeners.toArray (l);
			}
		}
		for (int i = l.length - 1; i >= 0; i--)
		{
			l[i].propertyChange (e);
		}
	}


	public void addVetoableChangeListener (VetoableChangeListener listener)
	{
		synchronized (listenerLock)
		{
			vetoListeners.addIfNotContained (listener);
			installListener (!listeners.isEmpty () || !vetoListeners.isEmpty ());
		}
	}


	public void removeVetoableChangeListener (VetoableChangeListener listener)
	{
		synchronized (listenerLock)
		{
			vetoListeners.remove (listener);
			installListener (!listeners.isEmpty () || !vetoListeners.isEmpty ());
		}
	}


	protected void fireVetoableChange (Object oldValue, Object newValue)
		throws PropertyVetoException
	{
		VetoableChangeListener[] l;
		synchronized (listenerLock)
		{
			if (vetoListeners.size () == 0)
			{
				return;
			}
			else
			{
				l = new VetoableChangeListener[vetoListeners.size ()];
				vetoListeners.toArray (l);
			}
		}
		PropertyChangeEvent e = new PropertyChangeEvent
			(this, WIDGET_VALUE_PROPERTY, oldValue, newValue);
		for (int i = l.length - 1; i >= 0; i--)
		{
			l[i].vetoableChange (e);
		}
	}


	protected void fireVetoableChange (PropertyChangeEvent e)
		throws PropertyVetoException
	{
		VetoableChangeListener[] l;
		synchronized (listenerLock)
		{
			if (vetoListeners.size () == 0)
			{
				return;
			}
			else
			{
				l = new VetoableChangeListener[vetoListeners.size ()];
				vetoListeners.toArray (l);
			}
		}
		for (int i = l.length - 1; i >= 0; i--)
		{
			l[i].vetoableChange (e);
		}
	}


	public final void dispose ()
	{
		if (disposed)
		{
			return;
		}
		disposed = true;
		synchronized (listeners)
		{
			listeners.clear ();
		}
		synchronized (vetoListeners)
		{
			vetoListeners.clear ();
		}
		disposeImpl ();
	}


	protected void disposeImpl ()
	{
	}
}
