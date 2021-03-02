
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

import javax.swing.*;

public class DockableRootPane extends JRootPane implements Dockable
{
	protected DockManager manager;

	private String panelTitle, tabTitle;
	private Icon icon;
	private DockableWrapper wrapper;


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


	public void setManager (DockManager manager)
	{
		this.manager = manager;
	}


	public boolean isClosable ()
	{
		return false;
	}


	public void dockableClosed ()
	{
		setManager (null);
	}


	public Dockable getDockable ()
	{
		return this;
	}


	public DockableComponent getComponent ()
	{
		return (wrapper != null) ? wrapper : (DockableComponent) this;
	}


	public void setWrapper (DockableWrapper component)
	{
		wrapper = component;
	}


	public DockContainer getDockParent ()
	{
		return (wrapper != null) ? wrapper.getDockParent ()
			: DockManager.getDockParent (this);
	}


	public boolean needsWrapper ()
	{
		return true;
	}


	public boolean isSelectable ()
	{
		return true;
	}


	public void setSelected (boolean selected)
	{
		if (wrapper != null)
		{
			wrapper.setSelected (selected);
		}
	}


	public void checkClose (Runnable ok)
	{
		ok.run ();
	}
}
