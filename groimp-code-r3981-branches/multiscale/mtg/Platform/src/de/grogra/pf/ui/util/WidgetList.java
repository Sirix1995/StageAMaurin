
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

package de.grogra.pf.ui.util;

import java.beans.*;

import de.grogra.pf.ui.*;

public final class WidgetList implements Widget
{
	private class WidgetHandle implements PropertyChangeListener
	{
		final Widget widget;

		WidgetHandle (Widget w)
		{
			widget = w;
			w.addPropertyChangeListener (this);
		}

		public void propertyChange (PropertyChangeEvent evt)
		{
			updateValue (evt.getNewValue (), this);
		}
		
		void dispose ()
		{
			widget.removePropertyChangeListener (this);
			widget.dispose ();
		}
	}

	private final Object component;
	private final WidgetHandle[] widgets;

	
	public WidgetList (Object component, Widget... widgets)
	{
		this.component = component;
		this.widgets = new WidgetHandle[widgets.length];
		for (int i = 0; i < widgets.length; i++)
		{
			this.widgets[i] = new WidgetHandle (widgets[i]);
		}
	}


	public Object getComponent ()
	{
		return component;
	}


	public void setEnabled (boolean enabled)
	{
		for (int i = 0; i < widgets.length; i++)
		{
			widgets[i].widget.setEnabled (enabled);
		}
	}

	
	public void updateValue (Object value)
	{
		updateValue (value, null);
	}
	
	
	void updateValue (Object value, WidgetHandle exclude)
	{
		for (int i = 0; i < widgets.length; i++)
		{
			if (widgets[i] != exclude)
			{
				widgets[i].widget.updateValue (value);
			}
		}
	}
	
	
	public void dispose ()
	{
		for (int i = 0; i < widgets.length; i++)
		{
			widgets[i].dispose ();
		}
	}
	
	
	public void addPropertyChangeListener (PropertyChangeListener l)
	{
		for (int i = 0; i < widgets.length; i++)
		{
			widgets[i].widget.addPropertyChangeListener (l);
		}
	}
	
	
	public void removePropertyChangeListener (PropertyChangeListener l)
	{
		for (int i = 0; i < widgets.length; i++)
		{
			widgets[i].widget.removePropertyChangeListener (l);
		}
	}
	
	
	public void addVetoableChangeListener (VetoableChangeListener l)
	{
		for (int i = 0; i < widgets.length; i++)
		{
			widgets[i].widget.addVetoableChangeListener (l);
		}
	}
	
	
	public void removeVetoableChangeListener (VetoableChangeListener l)
	{
		for (int i = 0; i < widgets.length; i++)
		{
			widgets[i].widget.removeVetoableChangeListener (l);
		}
	}
	
}
