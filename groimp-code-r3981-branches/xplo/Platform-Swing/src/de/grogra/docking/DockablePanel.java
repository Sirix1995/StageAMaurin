
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

package de.grogra.docking;

import java.awt.dnd.*;
import javax.swing.*;

public class DockablePanel extends JPanel
	implements Dockable, DragSourceComponent
{
	private String panelTitle, tabTitle;
	private Icon icon;
	private DockManager manager = null;
	private boolean closable;
	private Object[] listeners;


	public DockablePanel (java.awt.LayoutManager layout)
	{
		super (layout);
	}


	public void setManager (DockManager manager)
	{
		if (manager != this.manager)
		{
			this.manager = manager;
			DockManager.uninstallListeners (listeners);
			if (manager != null)
			{
				listeners = manager.installListeners (this, manager, this);
			}
		}
	}


	public DockContainer getDockParent ()
	{
		return DockManager.getDockParent (this);
	}


	public Dockable getDockable ()
	{
		return this;
	}


	public DockableComponent getComponent ()
	{
		return this;
	}


	public void setWrapper (DockableWrapper component)
	{
		throw new UnsupportedOperationException ();
	}


	public void setIcon (Icon icon)
	{
		firePropertyChange (ICON, this.icon, this.icon = icon);
	}


	public Icon getIcon ()
	{
		return icon;
	}


	public void setTabTitle (String title)
	{
		firePropertyChange (TAB_TITLE, tabTitle, tabTitle = title);
	}


	public String getTabTitle ()
	{
		return tabTitle;
	}


	public void setPanelTitle (String title)
	{
		firePropertyChange (PANEL_TITLE, panelTitle, panelTitle = title);
	}


	public String getPanelTitle ()
	{
		return panelTitle;
	}

	
	public void setTitles (String t)
	{
		setPanelTitle (t);
		setTabTitle (t);
	}


	public void setClosable (boolean closable)
	{
		this.closable = closable;
	}


	public boolean isClosable ()
	{
		return closable;
	}


	public void dockableClosed ()
	{
		setManager (null);
	}


	public DragDockableContext createContext (DragGestureEvent e)
	{
		return new DragDockableContext (manager, this);
	}


	public boolean needsWrapper ()
	{
		return false;
	}


	public boolean isSelectable ()
	{
		return true;
	}


	public void setSelected (boolean selected)
	{
	}


	public void checkClose (Runnable ok)
	{
		ok.run ();
	}

}
