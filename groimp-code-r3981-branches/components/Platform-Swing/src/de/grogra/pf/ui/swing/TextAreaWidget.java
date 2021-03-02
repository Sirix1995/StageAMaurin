
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import de.grogra.pf.ui.awt.AWTWidgetSupport;

class TextAreaWidget extends AWTWidgetSupport implements ActionListener, FocusListener {

	private final Dimension MINIMUM_SIZE = new Dimension(80, 35);
	
	@Override
	protected void install () {
		component.addFocusListener (this);
		if(component instanceof JTextArea) {
			((JTextArea) component).setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 3, 1, 1)));
			((JTextArea) component).setLineWrap(true);
			((JTextArea) component).setWrapStyleWord(true);
			((JTextArea) component).setColumns(20);
			((JTextArea) component).setRows(3);
			((JTextArea) component).setMinimumSize(MINIMUM_SIZE);
		}
	}

	
	@Override
	protected void uninstall () {
		component.removeFocusListener (this);
	}

	
	@Override
	protected void setComponentValue (Object value) {
		((JTextArea) component).setText ((value == null) ? "" : ((JTextArea) value).getText ());
	}

	
	public void actionPerformed (ActionEvent e) {
		checkForChange ((JTextArea) component);
	}


	public void focusGained (FocusEvent e) {}


	public void focusLost (FocusEvent e) {
		if (!e.isTemporary ()) {
			actionPerformed (null);
		}
	}


	@Override
	public void updateValue (Object value) {
		((JTextArea) component).setText ((value == null) ? "" : ((JTextArea) value).getText ());
		super.updateValue (value);
	}

}
