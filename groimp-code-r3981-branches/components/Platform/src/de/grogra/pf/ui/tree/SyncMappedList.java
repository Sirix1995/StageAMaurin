
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

package de.grogra.pf.ui.tree;

import javax.swing.*;
import javax.swing.event.*;

import de.grogra.pf.ui.*;
import de.grogra.xl.util.ObjectList;

public class SyncMappedList
	implements Synchronizer.Callback, ListDataListener, ListModel
{
	protected final Synchronizer sync;
	protected final ListModel source;

	private ObjectList listeners = new ObjectList (), syncData = new ObjectList ();


	public SyncMappedList (ListModel source, Synchronizer sync)
	{
		this.sync = sync;
		this.source = source;
		sync.initCallback (this);
		copy ();
	}

	
	private void copy ()
	{
		syncData.clear ();
		for (int i = 0; i < source.getSize (); i++)
		{
			syncData.add (source.getElementAt (i));
		}
	}

	
	protected synchronized void fireContentsChanged (int a, int b)
	{
		if (listeners.isEmpty ())
		{
			return;
		}
		ListDataEvent e = new ListDataEvent
			(this, ListDataEvent.CONTENTS_CHANGED, a, b);
		for (int i = listeners.size - 1; i >= 0; i--)
		{
			((ListDataListener) listeners.get (i)).contentsChanged (e);
		}
	}


	public Object run (int action, int iarg, Object oarg1, Object oarg2)
	{
		copy ();
		fireContentsChanged ((iarg >= 0) ? 0 : -1,
							 (iarg >= 0) ? syncData.size - 1 : -1);
		return null;
	}


	public synchronized void addListDataListener (ListDataListener l)
	{
		boolean b = listeners.size == 0;
		listeners.add (l);
		if (b)
		{
			source.addListDataListener (this);
		}
	}


	public synchronized void removeListDataListener (ListDataListener l)
	{
		listeners.remove (l);
		if (listeners.size == 0)
		{
			source.removeListDataListener (this);
		}
	}


	public int getSize ()
	{
		return syncData.size;
	}
	
	
	public Object getElementAt (int index)
	{
		return syncData.get (index);
	}


	public void intervalAdded (ListDataEvent e)
	{
		sync.invokeAndWait (0, e);
	}


	public void intervalRemoved (ListDataEvent e)
	{
		sync.invokeAndWait (0, e);
	}


	public void contentsChanged (ListDataEvent e)
	{
		sync.invokeAndWait (0, e);
	}

}
