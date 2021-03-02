
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

public final class WidgetAdapter extends WidgetSupport
	implements PropertyChangeListener, Command
{
	private final Widget widget;
	private final Context context;

	
	public WidgetAdapter (Widget widget, Context context)
	{
		this.widget = widget;
		this.context = context;
	}


	public void setEnabled (boolean enabled)
	{
		widget.setEnabled (enabled);
	}

	
	private Object currentValue;

	public void updateValue (Object value)
	{
		currentValue = value;
		widget.updateValue (value);
	}
	

	private boolean installed;

	@Override
	protected void installListener (boolean hasListeners)
	{
		if (hasListeners != installed)
		{
			installed = hasListeners;
			if (hasListeners)
			{
				widget.addPropertyChangeListener (this);
			}
			else
			{
				widget.removePropertyChangeListener (this);
			}
		}
	}


	public Object getComponent ()
	{
		return widget.getComponent ();
	}


	private PropertyChangeEvent pending;

	public void propertyChange (PropertyChangeEvent event)
	{
		boolean hasPending;
		synchronized (this)
		{
			hasPending = pending != null;
			pending = event;
		}
		if (!hasPending)
		{
			UI.getJobManager (context)
				.execute (this, null, context, JobManager.ACTION_FLAGS);
		}
	}


	public String getCommandName ()
	{
		return null;
	}


	public void run (Object info, Context ctx)
	{
		PropertyChangeEvent e;
		synchronized (this)
		{
			e = pending;
			pending = null;
		}
		try
		{
			fireVetoableChange (e);
			firePropertyChange (e);
		}
		catch (PropertyVetoException v)
		{
			widget.updateValue (currentValue);
		}
	}

}
