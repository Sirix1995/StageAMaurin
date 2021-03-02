
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

import java.awt.*;

import javax.swing.*;

public class DockContentPane extends JPanel implements DockContainer, Runnable
{
	private DockManager manager;
	private FloatingWindow window;


	public DockContentPane ()
	{
		this (null);
	}


	DockContentPane (FloatingWindow window)
	{
		super (new GridLayout (1, 1));
		this.window = window;
	}


	void setDockManager (DockManager manager)
	{
		this.manager = manager;
	}


	void windowChanged ()
	{
		window = (FloatingWindow) getRootPane ().getParent ();
	}


	public DockManager getDockManager ()
	{
		return manager;
	}


	public int getDockComponentCount ()
	{
		return getComponentCount ();
	}


	public DockComponent getDockComponent (int index)
	{
		return (DockComponent) getComponent (index);
	}


	public void findDockPositions (DockPositionList list, Point relDrag)
	{
		if (getComponentCount () > 0)
		{
			DockPosition.addDockPositions (list, getDockComponent (0),
										   DockPosition.LEFT
										   | DockPosition.RIGHT
										   | DockPosition.TOP
										   | DockPosition.BOTTOM
										   | DockPosition.CENTER,
										   relDrag, this, null);

		}
		else
		{
			DockPosition.addDockPositions (list, this, DockPosition.CENTER,
										   relDrag, this, null);
		}
	}


	public DockComponent remove (DockComponent c)
	{
		remove (0);
		if (window != null)
		{
			window.dispose ();
		}
		else
		{
			revalidate ();
			repaint ();
		}
		return this;
	}


	public void add (int position, DockComponent d)
	{
		if (getComponentCount () == 0)
		{
			d = manager.wrap (d, true);
			add ((Component) d);
			if ((d instanceof DockableComponent) && (window != null))
			{
				window.setTitle (((DockableComponent) d).getDockable ()
								 .getPanelTitle ());
			}
		}
		else
		{
			if (!(d instanceof DockableComponent))
			{
				throw new IllegalArgumentException ();
			}
			DockComponent c = getDockComponent (0);
			manager.addImpl (((DockableComponent) d).getDockable (),
							 (c instanceof DockSplitPane)
							 ? DockPosition.RIGHT : DockPosition.CENTER, c);
		}
	}


	public DockComponent replace (DockContainer dc, DockComponent d)
	{
		d = manager.wrap (d, true);
		DockManager.replace0 ((Component) dc, (Component) d);
		return d;
	}


	public DockContainer getDockParent ()
	{
		return DockManager.getDockParent (this);
	}


	public static DockContentPane get (Component c)
	{
		while (c != null)
		{
			if (c instanceof DockContentPane)
			{
				return (DockContentPane) c;
			}
			c = c.getParent ();
		}
		return null;
	}


	public void toFront (DockComponent c)
	{
	}


	@Override
	protected void validateTree ()
	{
		super.validateTree ();
		EventQueue.invokeLater (this);
	}


	public void run ()
	{
		updateSplitPanes (this);
	}


	private static void updateSplitPanes (Container c)
	{
		if (c instanceof DockSplitPane)
		{
			((DockSplitPane) c).updateDividerLocation ();
		}
		else if (c instanceof DockableComponent)
		{
			return;
		}
		for (int i = c.getComponentCount () - 1; i >= 0; i--)
		{
			Component o = c.getComponent (i);
			if (o instanceof Container)
			{
				updateSplitPanes ((Container) o);
			}
		}
	}

}
