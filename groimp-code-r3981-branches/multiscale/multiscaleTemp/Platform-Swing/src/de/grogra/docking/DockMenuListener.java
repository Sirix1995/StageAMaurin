
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
import java.lang.ref.WeakReference;

import javax.swing.*;

class DockMenuListener extends MouseAdapter
	implements ActionListener, Runnable
{
	protected JPopupMenu menu;
	protected JMenuItem close, undock;
	protected WeakReference<Dockable> clicked;
	protected DockManager manager;
	private Dockable dockable;


	DockMenuListener (DockManager manager, Dockable dockable)
	{
		this.manager = manager;
		this.dockable = dockable;
	}


	@Override
	public void mousePressed (MouseEvent e)
	{
		mouseReleased (e);
	}


	@Override
	public void mouseReleased (MouseEvent e)
	{
		if (e.isConsumed () || !e.isPopupTrigger ())
		{
			return;
		}
		clicked = null;
		Dockable c = getDockable (e);
		if (c == null)
		{
			return;
		}
		clicked = new WeakReference<Dockable> (c);
		if (menu == null)
		{
			createMenu ();
		}
		configureMenu ();
		menu.show (e.getComponent (), e.getX (), e.getY ());
	}


	protected Dockable getDockable (MouseEvent e)
	{
		return dockable;
	}


	protected void createMenu ()
	{
		menu = new JPopupMenu ();
		undock = createItem ("undock");
		close = createItem ("close");
	}


	private JMenuItem createItem (String name)
	{
		JMenuItem i
			= menu.add (DockManager.RES_BUNDLE.getString (name + ".Name"));
		i.setActionCommand (name);
		i.addActionListener (this);
		return i;
	}


	protected void configureMenu ()
	{
		close.setEnabled (clicked.get ().isClosable ());
	}


	public void actionPerformed (ActionEvent e)
	{
		if (clicked == null)
		{
			return;
		}
		Dockable d = clicked.get ();
		if (d == null)
		{
			return;
		}
		if ("close".equals (e.getActionCommand ()) && d.isClosable ())
		{
			d.checkClose (this);
		}
		else if ("undock".equals (e.getActionCommand ()))
		{
			manager.floatDockable (d);
		}
	}


	public void run ()
	{
		manager.closeDockable (clicked.get ());
	}

}
