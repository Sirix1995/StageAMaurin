
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

package de.grogra.pf.ui.event;

import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.UIProperty;

public class UIPropertyEditEvent extends EditEvent
{
	private final UIProperty property;
	private final Object oldValue;
	private final Object newValue;
	private final Object info;


	public UIPropertyEditEvent (Context context, UIProperty property,
								Object oldValue, Object newValue, Object info)
	{
		super ();
		set (context);
		this.property = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.info = info;
	}


	public UIProperty getProperty ()
	{
		return property;
	}


	public Object getOldValue ()
	{
		return oldValue;
	}


	public Object getNewValue ()
	{
		return newValue;
	}


	public Object getInfo ()
	{
		return info;
	}

	protected String paramString ()
	{
		return super.paramString() + "old=" + oldValue + ",new=" + newValue
			+ ",info=" + info;
	}


}
