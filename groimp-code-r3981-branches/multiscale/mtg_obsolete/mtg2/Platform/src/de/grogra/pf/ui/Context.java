
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

/**
 * A <code>Context</code> represents some object in the context of the graphical
 * user interface. It knows its containing workbench, window, panel,
 * and component. 
 * 
 * @author Ole Kniemeyer
 */
public interface Context
{
	/**
	 * Determines the workbench of this GUI context.
	 * 
	 * @return this context's workbench
	 */
	Workbench getWorkbench ();

	/**
	 * Determines the window of this GUI context.
	 * 
	 * @return this context's window
	 */
	Window getWindow ();

	/**
	 * Determines the panel of this GUI context.
	 * 
	 * @return this context's panel
	 */
	Panel getPanel ();

	/**
	 * Determines the GUI component of this GUI context.
	 * 
	 * @return this context's component
	 */
	Object getComponent ();
}
