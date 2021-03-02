
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
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import de.grogra.pf.ui.awt.*;
import de.grogra.util.*;

class ChoiceWidget extends AWTWidgetSupport
	implements ActionListener, ListCellRenderer
{
	private BasicComboBoxRenderer renderer = new BasicComboBoxRenderer ();


	@Override
	protected void install ()
	{
		((JComboBox) component).addActionListener (this);
		((JComboBox) component).setRenderer (this);
	}


	@Override
	protected void uninstall ()
	{
		((JComboBox) component).removeActionListener (this);
	}


	@Override
	protected void setComponentValue (Object value)
	{
		((JComboBox) component).setSelectedItem (value);
	}


	public void actionPerformed (ActionEvent e)
	{
		if (e.getSource () == component)
		{
			checkForChange (((JComboBox) component).getSelectedItem ());
		}
	}


	public Component getListCellRendererComponent
		(JList list, Object value, int index, boolean isSelected, 
		 boolean cellHasFocus)
	{
		renderer.getListCellRendererComponent (list, value, index, isSelected,
											   cellHasFocus);
		if (value instanceof Described)
		{
			renderer.setIcon ((Icon) ((Described) value)
							  .getDescription (Described.ICON));
			String s = (String) ((Described) value)
				.getDescription (Described.SHORT_DESCRIPTION);
			renderer.setText ((s == null) ? "" : s);
		}
		return renderer;
	}

}
