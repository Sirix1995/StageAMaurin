
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

package de.grogra.pf.ui;

import de.grogra.pf.ui.tree.UITree;

/**
 * This is a decorator class for {@link de.grogra.pf.ui.Panel}s.
 *
 * @author Ole Kniemeyer
 */
public class PanelDecorator implements Panel
{
	protected Panel panel;
	protected Panel decorator;


	public void initPanel (Panel panel)
	{
		this.panel = panel;
		panel.initDecorator (this);
	}


	public java.util.Map getUIPropertyMap ()
	{
		return panel.getUIPropertyMap ();
	}


	public String getPanelId ()
	{
		return panel.getPanelId ();
	}


	public UITree getMenu ()
	{
		return panel.getMenu ();
	}


	public void setMenu (UITree menu)
	{
		panel.setMenu (menu);
	}


	public void setContent (ComponentWrapper content)
	{
		panel.setContent (content);
	}


	public ComponentWrapper getContent ()
	{
		return panel.getContent ();
	}


	public Workbench getWorkbench ()
	{
		return panel.getWorkbench ();
	}


	public Window getWindow ()
	{
		return panel.getWindow ();
	}


	public Panel getPanel ()
	{
		return this;
	}


	public Object getComponent ()
	{
		return panel.getComponent ();
	}

	
	public void initDecorator (Panel decorator)
	{
		this.decorator = decorator;
	}


	public Panel getDecorator ()
	{
		return decorator;
	}


	public Panel resolve ()
	{
		return panel.resolve ();
	}


	public void dispose ()
	{
		panel.dispose ();
	}


	public void show (boolean moveToFront, Panel keepInFront)
	{
		panel.show (moveToFront, keepInFront);
	}


	public void checkClose (Command ok)
	{
		panel.checkClose (ok);
	}

	
	public void setCursor (int cursor)
	{
		panel.setCursor (cursor);
	}

}
