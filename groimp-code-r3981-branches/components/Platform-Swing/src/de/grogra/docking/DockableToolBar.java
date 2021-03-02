
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

import java.awt.event.*;
import java.awt.dnd.*;
import javax.swing.*;

public class DockableToolBar extends JToolBar
	implements Dockable, DragSourceComponent, FixedSize
{
	private String panelTitle, tabTitle;
	private Icon icon;
	private DockManager manager = null;
	private boolean closable;
	private Object[] listeners;


	public DockableToolBar ()
	{
		super ();
		setFloatable (true);
		setToolTipText ("ToolTIP");
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


	@Override
	protected void processMouseEvent (MouseEvent e)
	{
		if (listeners != null)
		{
			dispatch (e, listeners[0]);
			dispatch (e, listeners[1]);
		}
	}


	@Override
	protected void processMouseMotionEvent (MouseEvent e)
	{
		if (listeners != null)
		{
			dispatch (e, listeners[0]);
			dispatch (e, listeners[1]);
		}
	}


	private static void dispatch (MouseEvent e, Object l)
	{
		if (l instanceof MouseListener)
		{
			MouseListener listener = (MouseListener) l;
			switch (e.getID ())
			{
				case MouseEvent.MOUSE_PRESSED:
					listener.mousePressed (e);
					break;
				case MouseEvent.MOUSE_RELEASED:
					listener.mouseReleased (e);
					break;
				case MouseEvent.MOUSE_CLICKED:
					listener.mouseClicked (e);
					break;
				case MouseEvent.MOUSE_EXITED:
					listener.mouseExited (e);
					break;
				case MouseEvent.MOUSE_ENTERED:
					listener.mouseEntered (e);
					break;
			}
		}
		if (l instanceof MouseMotionListener)
		{
			MouseMotionListener listener = (MouseMotionListener) l;
			switch (e.getID ())
			{
				case MouseEvent.MOUSE_MOVED:
					listener.mouseMoved (e);
					break;
				case MouseEvent.MOUSE_DRAGGED:
					listener.mouseDragged (e);
					break;
			}
		}
	}


	public int getFixedSize ()
	{
		return (getOrientation () == HORIZONTAL) ? FIXED_HEIGHT : FIXED_WIDTH;
	}


	public boolean needsWrapper ()
	{
		return false;
	}


	@Override
	public void setOrientation (int orientation)
	{
		super.setOrientation (orientation);
		for (int i = 0, n = getComponentCount (); i < n; i++)
		{
			java.awt.Component c = getComponent (i);
			if (c instanceof JToolBar.Separator)
			{
				((JToolBar.Separator) c).setOrientation
					((orientation == JToolBar.VERTICAL) ? JSeparator.HORIZONTAL
					 : JSeparator.VERTICAL);
			}
		}
	}


	public boolean isSelectable ()
	{
		return false;
	}


	public void setSelected (boolean selected)
	{
	}


	public void checkClose (Runnable ok)
	{
		ok.run ();
	}
}
