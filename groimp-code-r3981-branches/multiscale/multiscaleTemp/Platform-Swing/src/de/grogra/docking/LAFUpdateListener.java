
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

package de.grogra.docking;

import java.awt.*;
import java.beans.*;
import javax.swing.*;
import java.lang.ref.WeakReference;

public class LAFUpdateListener implements PropertyChangeListener
{
	private static ClassLoader uiClassLoader;


	public static void setLookAndFeel (String systemLaf)
		throws UnsupportedLookAndFeelException, ClassNotFoundException,
			IllegalAccessException, InstantiationException
	{
		UIManager.setLookAndFeel (systemLaf);
	}


	public static void setLookAndFeel (LookAndFeel laf)
		throws UnsupportedLookAndFeelException
	{
		uiClassLoader = laf.getClass ().getClassLoader ();
		UIManager.setLookAndFeel (laf);
		setClassLoader ();
	}


	private static void setClassLoader ()
	{
		if (uiClassLoader != null)
		{
			UIDefaults d = UIManager.getLookAndFeelDefaults ();
			if ((d != null) && (d.get ("ClassLoader") == null))
			{
				d.put ("ClassLoader", uiClassLoader);
			}
			uiClassLoader = null;
		}
	}


	private final WeakReference root;


	public LAFUpdateListener (Component root)
	{
		this.root = new WeakReference (root);
		UIManager.addPropertyChangeListener (this);
	}


	public void propertyChange (PropertyChangeEvent evt)
	{
		Component r = (Component) root.get ();
		if (r == null)
		{
			UIManager.removePropertyChangeListener (this);
			return;
		}
		if ("lookAndFeel".equals (evt.getPropertyName ()))
		{
			setClassLoader ();
			SwingUtilities.updateComponentTreeUI (r);
		}
	}
}
