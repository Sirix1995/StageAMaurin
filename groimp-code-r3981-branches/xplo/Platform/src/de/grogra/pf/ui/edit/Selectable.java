
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

package de.grogra.pf.ui.edit;

/**
 * A <code>Selectable</code> is an object which knows how to
 * create a {@link de.grogra.pf.ui.edit.Selection} of itself.
 * The selection is then used to create a property editor in the GUI.
 * 
 * @author Ole Kniemeyer
 */
public interface Selectable
{
	/**
	 * Converts this object into a {@link Selection}.
	 * 
	 * @param ctx the UI context
	 * @return a selection, or <code>null</code> if this is not possible
	 */
	Selection toSelection (de.grogra.pf.ui.Context ctx);
}
