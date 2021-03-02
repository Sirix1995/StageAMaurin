
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

package de.grogra.ext.jedit;

import java.awt.*;
import javax.swing.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.swing.PanelSupport;

public abstract class ViewBase extends de.grogra.pf.ui.swing.SwingPanel
{
	protected static final int DO_NOTHING_ON_CLOSE = JFrame.DO_NOTHING_ON_CLOSE;

	private String title;


	protected ViewBase ()
	{
		super (null);
	}


	@Override
	public void initialize (PanelSupport support, de.grogra.util.Map p)
	{
		super.initialize (support, p);
		if (title != null)
		{
			setTitle (title);
		}
	}

	
	public void toFront ()
	{
		if (manager != null)
		{
			manager.select (this, true, null);
		}
	}

	
	public void pack ()
	{
	}

	
	public Frame getFrame ()
	{
		return (Frame) SwingUtilities.getWindowAncestor (this);
	}

	
	public Component getFocusOwner ()
	{
		Frame f = getFrame ();
		return (f != null) ? f.getFocusOwner () : null;
	}
	
	
	public void setState (int state)
	{		
	}
	
	
	public void setDefaultCloseOperation (int op)
	{
		assert op == DO_NOTHING_ON_CLOSE;
	}
	
	
	public void setIconImage (Image img)
	{
	}
	
	
	public void setTitle (String title)
	{
		this.title = title;
		if (getSupport () != null)
		{
			UIProperty.PANEL_TITLE.setValue (getSupport(), title);
		}
	}
	
	
	public void addWindowListener (java.awt.event.WindowListener l)
	{
	}


	public static Icon loadPlatformIcon (String iconName)
	{
		return de.grogra.icon.IconAdapter.create
			(UI.getIcon (iconName),
			 de.grogra.pf.ui.UIToolkit.MEDIUM_ICON_SIZE);
	}

}
