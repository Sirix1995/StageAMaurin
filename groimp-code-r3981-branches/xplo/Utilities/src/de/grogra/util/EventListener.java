
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

package de.grogra.util;

import java.util.EventObject;

import de.grogra.xl.util.ObjectList;

/**
 * This interface is a listener interface for the general event
 * {@link java.util.EventObject}.
 * 
 * @author Ole Kniemeyer
 */
public interface EventListener extends java.util.EventListener
{
	class Bicast implements EventListener
	{
		public EventListener a;
		public EventListener b;


		public Bicast ()
		{
		}


		public Bicast (EventListener a)
		{
			this.a = a;
		}


		public Bicast (EventListener a, EventListener b)
		{
			this.a = a;
			this.b = b;
		}


		public void eventOccured (EventObject event)
		{
			if (a != null)
			{
				a.eventOccured (event);
			}
			if (b != null)
			{
				b.eventOccured (event);
			}
		}
	}


	class Multicaster implements EventListener
	{
		private ObjectList v = new ObjectList (10, false);


		public final void addEventListener (EventListener l)
		{
			synchronized (v)
			{
				v.addIfNotContained (l);
			}
		}


		public final void removeEventListener (EventListener l)
		{
			synchronized (v)
			{
				v.remove (l);
			}
		}


		public final void removeAllListeners ()
		{
			synchronized (v)
			{
				v.clear ();
			}
		}


		public void eventOccured (EventObject event)
		{
			synchronized (v)
			{
				int l = v.size ();
				for (int i = 0; i < l; i++)
				{
					((EventListener) v.get (i)).eventOccured (event);
				}
			}
		}
	}


	/**
	 * This method is invoked on registered event listeners when
	 * <code>event</code> has occured. This general listener interface
	 * does not restrict the possible kinds of events. Concrete sources
	 * of events should specify which events they may fire. 
	 * 
	 * @param event an event
	 */
	void eventOccured (EventObject event);
}
