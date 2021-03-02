
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
import javax.swing.text.*;
import de.grogra.pf.ui.awt.*;
import de.grogra.pf.ui.util.*;

class TextWidget extends AWTWidgetSupport
	implements ActionListener, FocusListener
{

	@Override
	protected void install ()
	{
		if (component instanceof JTextField)
		{
			((JTextField) component).addActionListener (this);
		}
		component.addFocusListener (this);
	}


	@Override
	protected void uninstall ()
	{
		component.removeFocusListener (this);
		if (component instanceof JTextField)
		{
			((JTextField) component).removeActionListener (this);
		}
	}


	@Override
	protected void setComponentValue (Object value)
	{
		((JTextComponent) component).setText ((value == null) ? ""
											  : value.toString ());
	}


	public void actionPerformed (ActionEvent e)
	{
		String t = ((JTextComponent) component).getText ();
		checkForChange ((t.length () > 0) ? t : null);
	}


	public void focusGained (FocusEvent e)
	{
	}


	public void focusLost (FocusEvent e)
	{
		if (!e.isTemporary ())
		{
			actionPerformed (null);
		}
	}


	@Override
	public void updateValue (Object value)
	{
		if ((value != null) && (conversion instanceof Numeric2String)
			&& (((Numeric2String) conversion).quantity != null)
			&& (((Numeric2String) conversion).quantity.parseUnit
				(((JTextComponent) component).getText (), null) == null))
		{
			lastValue = null;
		}
		super.updateValue (value);
	}

}
