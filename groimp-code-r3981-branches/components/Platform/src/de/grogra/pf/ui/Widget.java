
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

import java.beans.*;

public interface Widget extends ComponentWrapper
{
	String WIDGET_VALUE_PROPERTY = "widgetValue";

	void setEnabled (boolean enabled);

	void addPropertyChangeListener (PropertyChangeListener listener);

	void removePropertyChangeListener (PropertyChangeListener listener);

	void addVetoableChangeListener (VetoableChangeListener listener);

	void removeVetoableChangeListener (VetoableChangeListener listener);

	void updateValue (Object value);
}
