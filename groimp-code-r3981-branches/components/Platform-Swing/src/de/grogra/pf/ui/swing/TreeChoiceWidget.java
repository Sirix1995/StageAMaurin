
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
import java.awt.event.*;
import javax.swing.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.awt.*;
import de.grogra.pf.ui.tree.*;
import de.grogra.util.*;
import de.grogra.icon.*;

class TreeChoiceWidget extends AWTWidgetSupport
	implements ActionListener, Command
{
	private UITree tree;


	TreeChoiceWidget (UITree tree)
	{
		super ();
		this.tree = tree;
		setComponent (new JButton ("TreeChoice"));
	}


	@Override
	protected void install ()
	{
		((AbstractButton) component).addActionListener (this);
	}


	@Override
	protected void uninstall ()
	{
		((AbstractButton) component).removeActionListener (this);
	}


	@Override
	protected void setComponentValue (Object value)
	{
		AbstractButton b = (AbstractButton) component;
		if (value != null)
		{
			b.setIcon (IconAdapter.create
					   ((IconSource) tree.getDescription
						(value, Described.ICON),
						SwingToolkit.MENU_ICON_SIZE));
			b.setText ((String) tree.getDescription (value, Described.NAME));
		}
		else
		{
			b.setIcon (null);
			b.setText ("?");
		}
	}


	public void actionPerformed (ActionEvent e)
	{
		if (e.getSource () == component)
		{
			UI.getJobManager (tree.getContext ()).runLater
				(this, EventQueue.getCurrentEvent (), tree.getContext (),
				 JobManager.ACTION_FLAGS);
		}
		else
		{
			Object node = SwingToolkit.getSource (e.getSource ());
			setComponentValue (node);
			checkForChange (node);
		}
	}


	public String getCommandName ()
	{
		return null;
	}


	public void run (Object info, Context ctx)
	{
		int x, y;
		if (info instanceof MouseEvent)
		{
			x = ((MouseEvent) info).getX ();
			y = ((MouseEvent) info).getY ();
		}
		else
		{
			x = component.getWidth () / 2;
			y = component.getHeight () / 2;
		}
		tree.update ();
		((SwingToolkit) UIToolkit.get (ctx))
			.showPopupMenu (tree, component, x, y, this);
	}
}
