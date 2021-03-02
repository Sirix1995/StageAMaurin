
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

import java.beans.PropertyVetoException;

import de.grogra.util.Utils;

public abstract class WidgetBase extends WidgetSupport
{
	protected WidgetConversion conversion;
	protected volatile Object lastValue = this;


	public void setConversion (WidgetConversion conversion)
	{
		this.conversion = conversion;
	}


	protected abstract void setComponentValue (Object value);


	protected void checkForChange (Object widgetValue)
	{
		Object lv = lastValue;
		Exception e = null;
		try
		{
			Object v = (conversion == null) ? widgetValue
				: conversion.fromWidget (widgetValue);
			if (Utils.equal (lv, v))
			{
				return;
			}
			fireVetoableChange (lv, v);
			lastValue = v;
			firePropertyChange (lv, v);
		}
		catch (RuntimeException re)
		{
			e = re;
		}
		catch (PropertyVetoException pe)
		{
			e = pe;
			e.printStackTrace ();
		}
		if (e != null)
		{
			lastValue = lv;
			if (lv != this)
			{
				setComponentValue ((conversion == null) ? lv
								   : conversion.toWidget (lv));
			}
		}
	}

}
