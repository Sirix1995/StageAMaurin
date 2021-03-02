
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

package de.grogra.pf.ui.event;

import de.grogra.pf.ui.*;

/**
 * An <code>EditEvent</code> is an event within the context of
 * the graphical user interface.
 * 
 * @author Ole Kniemeyer
 */
public class EditEvent extends java.util.EventObject
	implements Context, Cloneable
{
	Object component;

	private boolean isSet = false;
	private Window window;
	private Panel panel;


	public EditEvent ()
	{
		super ("");
		this.source = null;
	}


	public EditEvent clone (Object source)
	{
		try
		{
			EditEvent e = (EditEvent) clone ();
			e.source = source;
			return e;
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError (e);
		}
	}


	private EditEvent set (Window window, Panel panel,
						   Object component, Object source)
	{
		if (isSet)
		{
			throw new IllegalStateException
				("EditEvent has already been set.");
		}
		isSet = true;
		this.window = window;
		this.panel = panel;
		this.component = component;
		this.source = source;
		return this;
	}


	public EditEvent set (Panel panel, Object component, Object source)
	{
		return set (panel.getWindow (), panel, component, source);
	}


	public EditEvent set (Context context, Object source)
	{
		return set (context.getWindow (), context.getPanel (),
					context.getComponent (), source);
	}


	public EditEvent set (Context context)
	{
		return set (context.getWindow (), context.getPanel (),
					context.getComponent (), null);
	}


	public Workbench getWorkbench ()
	{
		return window.getWorkbench ();
	}


	public Window getWindow ()
	{
		return window;
	}


	public Panel getPanel ()
	{
		return panel;
	}


	public Object getComponent ()
	{
		return component;
	}


	@Override
	public String toString ()
	{
		return getClass ().getName () + '[' + paramString () + ']';
	}


	protected String paramString ()
	{
		return "source=" + source + ',' + window;
	}

}
