
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

package de.grogra.pf.ui.awt;

import java.awt.*;
import java.beans.*;
import de.grogra.util.Utils;
import de.grogra.pf.ui.util.*;

public abstract class AWTWidgetSupport extends WidgetBase
	implements AWTSynchronizer.Callback
{
	protected static final int SET_ENABLED = 0;
	protected static final int SET_COMPONENT_VALUE = 1;

	protected static final int ACTION_COUNT = 2;


	protected Component component;
	protected AWTSynchronizer sync = new AWTSynchronizer (this);

	private boolean enabled = true;


	public Object run (int action, int iarg, Object oarg1, Object oarg2)
	{
		switch (action)
		{
			case SET_ENABLED:
			{
				component.setEnabled (enabled);
				break;
			}
			case SET_COMPONENT_VALUE:
			{
				setComponentValue (oarg1);
				break;
			}
			default:
				throw new AssertionError (action);
		}
		return null;
	}


	public void setEnabled (boolean enabled)
	{
		if (this.enabled != enabled)
		{
			this.enabled = enabled;
			if (component != null)
			{
				sync.invokeAndWait (SET_ENABLED);
			}
		}
	}


	public void setComponent (Component component)
	{
		if (this.component != component)
		{
			if (this.component != null)
			{
				uninstall ();
			}
			this.component = component;
			if (component != null)
			{
				install ();
			}
			if (component != null)
			{
				sync.invokeAndWait (SET_ENABLED);
			}
		}
	}


	public Object getComponent ()
	{
		return component;
	}


	protected void install ()
	{
	}


	protected void uninstall ()
	{
	}


	public void updateValue (Object value)
	{
		if (!Utils.equal (value, lastValue))
		{
			lastValue = value;
			sync.invokeAndWait (SET_COMPONENT_VALUE,
								(conversion == null) ? value
								: conversion.toWidget (value));
		}
	}

}
