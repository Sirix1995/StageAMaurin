
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

package de.grogra.util;

import javax.swing.*;

/**
 * An instance of <code>Described</code> provides descriptions
 * for an object. The descriptions are obtained by the method
 * {@link #getDescription(String)}. Which particular description is
 * returned depends on the argument <code>type</code>: It
 * is one of the string constants defined in this interface.
 * 
 * @author Ole Kniemeyer
 */
public interface Described
{
	/**
	 * Constant for {@link #getDescription(String)} specifying the
	 * name. The returned value has to be a <code>String</code>, it is
	 * used, among others, for menus and buttons.
	 */
	String NAME = Action.NAME;

	/**
	 * Constant for {@link #getDescription(String)} specifying a
	 * short description.
	 * The returned value has to be a <code>String</code>, it is
	 * used, among others, for tooltip text.
	 */
	String SHORT_DESCRIPTION = Action.SHORT_DESCRIPTION;

	/**
	 * Constant for {@link #getDescription(String)} specifying a
	 * title.
	 * The returned value has to be a <code>String</code>, it is
	 * used for titles.
	 */
	String TITLE = "Title";

	/**
	 * Constant for {@link #getDescription(String)} specifying an icon.
	 * The returned value has to be an instance of
	 * {@link de.grogra.icon.IconSource}.
	 */
	String ICON = "Icon";

	/**
	 * Constant for {@link #getDescription(String)} specifying an selected icon.
	 * The returned value has to be an instance of
	 * {@link de.grogra.icon.IconSource}.
	 */
	String SELECTED_ICON = "SelectedIcon";
	
	/**
	 * Constant for {@link #getDescription(String)} specifying a
	 * key code to be used as the mnemonic for the object.
	 * The returned value has to be an instance of
	 * {@link Number} or {@link Character}.
	 */
	String MNEMONIC_KEY = Action.MNEMONIC_KEY;

	/**
	 * Constant for {@link #getDescription(String)} specifying an
	 * accelerator key to be used as the accelerator for the object.
	 * The returned value has to be an instance of
	 * {@link KeyStroke} or a <code>String</code> which can be converted
	 * to a {@link KeyStroke} by
	 * {@link KeyStroke#getKeyStroke(String)}.
	 */
	String ACCELERATOR_KEY = Action.ACCELERATOR_KEY;


	/**
	 * Returns the description associated with the given <code>type</code>.
	 * <code>type</code> has to be one of the predefined constants
	 * of this interface, the returned value has to conform to the
	 * specification of the used constant.
	 * 
	 * @param type the type of description
	 * @return the description, or <code>null</code> if no description is available
	 */
	Object getDescription (String type);
}
