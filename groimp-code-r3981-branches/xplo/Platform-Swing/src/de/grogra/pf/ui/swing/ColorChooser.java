
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
import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;

final class ColorChooser extends JColorChooser implements ChangeListener
{
	private final JTabbedPane pane;
	private final Component[] panels, dummies;

	ColorChooser ()
	{
		setLayout (new java.awt.GridLayout (1, 1));
		AbstractColorChooserPanel[] p
			= ColorChooserComponentFactory.getDefaultChooserPanels ();
		System.arraycopy
			(p, 0,
			 p = new AbstractColorChooserPanel[p.length + 1], 1,
			 p.length - 1);
		p[0] = new SimpleColorChooserPanel ();
		pane = new JTabbedPane (JTabbedPane.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
		add (pane);
		panels = p;
		dummies = new Component[p.length];
		for (int i = 0; i < p.length; i++)
		{
			pane.addTab (p[i].getDisplayName (), dummies[i] = new Container ());
			int m = p[i].getMnemonic ();
			if (m > 0)
			{
				pane.setMnemonicAt (i, m);
				pane.setDisplayedMnemonicIndexAt
					(i, p[i].getDisplayedMnemonicIndex ());
			}
		}
		for (int i = 0; i < p.length; i++)
		{
			p[i].installChooserPanel (this);
		}
		pane.addChangeListener (this);
		stateChanged (null);
	}


	@Override
	public void updateUI ()
    {
    }
	
	
	public void stateChanged (ChangeEvent e)
	{
		int s = pane.getSelectedIndex ();
		for (int i = pane.getTabCount () - 1; i >= 0; i--)
		{
			Component n = ((i == s) ? panels : dummies)[i];
			if (n != pane.getComponentAt (i))
			{
				pane.setComponentAt (i, n);
			}
		}
	}
}
