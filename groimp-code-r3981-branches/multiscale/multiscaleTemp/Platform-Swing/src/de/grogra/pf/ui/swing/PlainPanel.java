
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

import java.awt.*;
import de.grogra.util.Disposable;
import de.grogra.docking.*;
import de.grogra.pf.ui.UIProperty;
import de.grogra.icon.*;

public class PlainPanel extends DockablePanel implements SwingDockable
{
	private PanelSupport support;
	private Disposable toDispose;


	PlainPanel (LayoutManager layout, Disposable toDispose)
	{
		super (layout);
		this.toDispose = toDispose;
		setClosable (true);
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
		if (toDispose != null)
		{
			toDispose.dispose ();
			toDispose = null;
		}
	}


	public void setIconSource (IconSource s)
	{
		setIcon (IconAdapter.create (s, SwingToolkit.WINDOW_ICON_SIZE));
	}


	@Override
	public void dockableClosed ()
	{
		support.dockableClosed ();
	}


	public void setMenu (Component menu)
	{
		if (menu != null)
		{
			throw new UnsupportedOperationException ();
		}
	}


	public Container getContentPane ()
	{
		return this;
	}


	@Override
	public void checkClose (Runnable ok)
	{
		support.checkClose (ok);
	}

}
