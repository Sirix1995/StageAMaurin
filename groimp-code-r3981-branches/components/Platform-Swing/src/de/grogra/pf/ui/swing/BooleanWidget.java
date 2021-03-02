
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

class BooleanWidget extends AWTWidgetSupport implements ActionListener
{

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
		((AbstractButton) component).getModel ()
			.setSelected (Boolean.TRUE.equals (value));
	}


	public void actionPerformed (ActionEvent e)
	{
		checkForChange (((AbstractButton) component).getModel ().isSelected ()
						? Boolean.TRUE : Boolean.FALSE);
	}

}
