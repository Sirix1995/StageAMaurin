
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

import java.awt.event.*;
import javax.swing.*;
import de.grogra.pf.ui.awt.*;
import de.grogra.util.*;

class MenuChoiceWidget extends AWTWidgetSupport implements ItemListener
{
	final ButtonGroup group;
	final ListModel list;


	public MenuChoiceWidget (ButtonGroup group, ListModel list)
	{
		this.group = group;
		this.list = list;
	}


	@Override
	protected void install ()
	{
		JMenu m = (JMenu) component;
		for (int i = 0; i < m.getMenuComponentCount (); i++)
		{
			((JMenuItem) m.getMenuComponent (i)).addItemListener (this);
		}
	}


	@Override
	protected void uninstall ()
	{
		JMenu m = (JMenu) component;
		for (int i = 0; i < m.getMenuComponentCount (); i++)
		{
			((JMenuItem) m.getMenuComponent (i)).removeItemListener (this);
		}
	}


	@Override
	protected void setComponentValue (Object value)
	{
		for (int i = list.getSize () - 1; i >= 0; i--)
		{
			if (Utils.equal (value, list.getElementAt (i)))
			{
				((JMenuItem) ((JMenu) component).getMenuComponent (i)).setSelected (true);
				return;
			}
		}
	}


	public void itemStateChanged (ItemEvent e)
	{
		JMenu m = (JMenu) component;
		for (int i = m.getMenuComponentCount () - 1; i >= 0; i--)
		{
			if (((JMenuItem) m.getMenuComponent (i)).isSelected ())
			{
				checkForChange (list.getElementAt (i));
			}
		}
	}

}
