
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
import javax.swing.*;

public class FloatingWindow extends JFrame
{
	FloatingWindow (Frame owner)
	{
		super ();
		new LAFUpdateListener (this);
	}


	FloatingWindow (Dialog owner)
	{
		super ();
	}


	public DockContentPane getDockRoot ()
	{
		return (DockContentPane) getContentPane ();
	}


	@Override
	protected void frameInit ()
	{
		super.frameInit ();
		setContentPane (new DockContentPane (this));
		setDefaultCloseOperation (DO_NOTHING_ON_CLOSE);
	}


	@Override
	public void dispose ()
	{
		getDockRoot ().getDockManager ().unregisterRoot (getRootPane ());
		super.dispose ();
	}


	final void superDispose ()
	{
		super.dispose ();
	}


	void setRootPane0 (JRootPane root)
	{
		setRootPane (root);
	}
}
