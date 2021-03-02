
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

import java.util.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;

public class DockTabbedPane extends JTabbedPane
	implements DockContainer, DragGestureListener, PropertyChangeListener 
{
	private final DockManager manager;
	private final Stack listeners = new Stack ();


	private final class TabShape implements DockShape
	{
		private final int index;

		private int lx, ly;
		private Graphics g;


		TabShape (int index)
		{
			this.index = index;
		}


		public boolean equals (DockShape s)
		{
			return (s instanceof TabShape) && (index == ((TabShape) s).index)
				&& (getPane () == ((TabShape) s).getPane ());
		}


		public void paintDockShape (DockPosition pos, Graphics g)
		{
			g.translate (pos.getAbsX (), pos.getAbsY ());
			Rectangle r = getBoundsAt (index);
			this.g = g;
			lx = r.x;
			ly = r.y;
			switch (getTabPlacement ())
			{
				case TOP:
					drawH (r.x + r.width - 1);
					drawV (r.y + r.height - 1);
					drawH (getWidth () - 1);
					drawV (getHeight () - 1);
					drawH (0);
					drawV (r.y + r.height - 1);
					break;
				case BOTTOM:
					drawH (0);
					drawV (0);
					drawH (getWidth () - 1);
					drawV (r.y);
					drawH (r.x + r.width - 1);
					drawV (getHeight () - 1);
					break;
				case LEFT:
					drawH (r.x + r.width - 1);
					drawV (0);
					drawH (getWidth () - 1);
					drawV (getHeight () - 1);
					drawH (r.x + r.width - 1);
					drawV (r.y + r.height - 1);
					break;
				case RIGHT:
					drawH (getWidth () - 1);
					drawV (r.y + r.height - 1);
					drawH (r.x);
					drawV (getHeight () - 1);
					drawH (0);
					drawV (0);
					break;
			}
			drawH (r.x);
			drawV (r.y);
			this.g = null;
			g.translate (-pos.getAbsX (), -pos.getAbsY ());
		}


		private void drawH (int x)
		{
			if (x >= lx)
			{
				g.fillRect (lx - 2, ly - 2, x - lx + 4, 5);
			}
			else
			{
				g.fillRect (x - 2, ly - 2, lx - x + 4, 5);
			}
			lx = x;
		}


		private void drawV (int y)
		{
			if (y >= ly)
			{
				g.fillRect (lx - 2, ly - 2, 5, y - ly + 4);
			}
			else
			{
				g.fillRect (lx - 2, y - 2, 5, ly - y + 4);
			}
			ly = y;
		}


		private DockTabbedPane getPane ()
		{
			return DockTabbedPane.this;
		}
	}


	public DockTabbedPane (final DockManager manager)
	{
		super (TOP, SCROLL_TAB_LAYOUT);
		this.manager = manager;
		updateRecognizers (this);
		getModel ().addChangeListener (new ChangeListener ()
			{
				public void stateChanged (ChangeEvent e)
				{
					Component c = getSelectedComponent ();
					if (c instanceof Dockable)
					{
						manager.select ((Dockable) c, false, null);
					}
				}
			});
	}


	private Object[] installListeners (Component c)
	{
		return manager.installListeners
			(c, this,
			 new DockMenuListener (manager, null)
				{
					@Override
					protected Dockable getDockable (MouseEvent e)
					{
						return DockTabbedPane.this.getDockable
							(e.getComponent (), e.getPoint ());
					}
				});
	}


	@Override
	protected void firePropertyChange (String propertyName,
									   Object oldValue, Object newValue)
	{
		super.firePropertyChange (propertyName, oldValue, newValue);
		if ((manager != null) && "UI".equals (propertyName))
		{
			updateRecognizers (this);
		}
	}


	private void updateRecognizers (Component c)
	{
		if ((c == this) || (c instanceof javax.swing.plaf.UIResource))
		{
			if (c == this)
			{
				while (!listeners.empty ())
				{
					DockManager.uninstallListeners
						((Object[]) listeners.pop ());
				}
			}
			if ((c instanceof Container)
				&& (c.getMouseListeners ().length == 0))
			{
				for (int i = ((Container) c).getComponentCount () - 1; i >= 0;
					 i--)
				{
					updateRecognizers (((Container) c).getComponent (i));
				}
			}
			listeners.push (installListeners (c));
		}
	}


	private int myIndexAtLocation (Point p)
	{
		for (int i = 0; i < getTabCount (); i++)
		{
			if (getBoundsAt (i).contains (p))
			{
				return i;
			}
		}
		return -1;
	}


	public DockContainer getDockParent ()
	{
		return DockManager.getDockParent (this);
	}


	public DockComponent remove (DockComponent dc)
	{
		remove ((Component) dc);
		((Dockable) dc).removePropertyChangeListener (this);
		if (getTabCount () == 1)
		{
			dc = (Dockable) getComponentAt (0);
			DockContainer c = getDockParent ();
			return (c != null) ? c.replace (this, dc) : dc;
		}
		return this;
	}


	public int getDockComponentCount ()
	{
		return getTabCount ();
	}


	public DockComponent getDockComponent (int index)
	{
		return (DockComponent) getComponentAt (index);
	}


	public void findDockPositions (DockPositionList list, Point relDrag)
	{
		int i = myIndexAtLocation (relDrag);
		if (i >= 0)
		{
			list.addDockPosition (getDockComponent (i), DockPosition.TAB,
								  relDrag, new TabShape (i));
		}
	}


	public void add (int position, DockComponent c)
	{
		c = manager.wrap (c, false);
		if (!(c instanceof Dockable))
		{
			throw new IllegalArgumentException ();
		}
		if (position < 0)
		{
			position = getTabCount ();
		}
		Dockable dockable = (Dockable) c;
		insertTab (dockable.getTabTitle (), dockable.getIcon (),
				   (Component) c, dockable.getToolTipText (), position);
		dockable.addPropertyChangeListener (this);
		setSelectedIndex (position);
	}


	public DockComponent replace (DockContainer dc, DockComponent d)
	{
		throw new UnsupportedOperationException ();
	}


	protected Dockable getDockable (Component c, Point p)
	{
		while (c != this)
		{
			p.translate (c.getX (), c.getY ());
			c = c.getParent ();
		}
		int i = myIndexAtLocation (p);
		return (i >= 0) ? (Dockable) getComponentAt (i) : null;
	}


	public void dragGestureRecognized (DragGestureEvent e)
	{
		Dockable d = getDockable (e.getComponent (), e.getDragOrigin ());
		if (d != null)
		{
			manager.dragGestureRecognized
				(e, new DragDockableContext (manager, this, d));
		}
	}


	public void propertyChange (PropertyChangeEvent e)
	{
		int i = indexOfComponent ((Component) e.getSource ());
		if (i >= 0)
		{
			String n = e.getPropertyName ();
			Object v = e.getNewValue ();
			if (Dockable.TAB_TITLE.equals (n))
			{
				setTitleAt (i, String.valueOf (v));
			}
			else if (Dockable.TOOL_TIP.equals (n))
			{
				setToolTipTextAt (i, String.valueOf (v));
			}
			else if (Dockable.ICON.equals (n))
			{
				setIconAt (i, (Icon) v);
			}
		}
	}


	public void toFront (DockComponent c)
	{
		setSelectedComponent ((Component) c);
	}

}
