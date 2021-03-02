
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

package de.grogra.pf.ui.swing;

import java.awt.Component;
import java.awt.Container;

import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import de.grogra.docking.DockableToolBar;
import de.grogra.icon.IconAdapter;
import de.grogra.icon.IconSource;
import de.grogra.pf.ui.UIProperty;

class ToolBar extends DockableToolBar implements SwingDockable
{
	PanelSupport support;

	ToolBar ()
	{
		super ();
		setRollover (true);
		setClosable (true);
		InputMap im = getInputMap (WHEN_IN_FOCUSED_WINDOW);
		ComponentInputMap c = new ComponentInputMap (this);
		c.setParent (im);
		setInputMap (WHEN_IN_FOCUSED_WINDOW, c);
	}

	public PanelSupport getSupport ()
	{
		return support;
	}


	public void initialize (PanelSupport support, de.grogra.util.Map p)
	{
		this.support = support;
		support.installUpdater (UIProperty.PANEL_TITLE, true, "setTitles", p);
		support.installUpdater (UIProperty.ICON, true, "setIconSource", p);
	}


	public void dispose ()
	{
	}


	public void setIconSource (IconSource s)
	{
		setIcon (IconAdapter.create (s, SwingToolkit.WINDOW_ICON_SIZE));
	}


	public void setMenu (Component mb)
	{
		if (mb == null)
		{
			removeAll ();
		}
		else if (mb != this)
		{
			throw new IllegalArgumentException ();
		}
	}


	public Container getContentPane ()
	{
		return this;
	}


	@Override
	public void dockableClosed ()
	{
		support.dockableClosed ();
	}


	@Override
	public void checkClose (Runnable ok)
	{
		support.checkClose (ok);
	}

}
