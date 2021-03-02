
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

import java.awt.event.MouseEvent;

public class MouseEditEvent extends InputEditEvent
{
	int eventType;
	private boolean isSet = false;
	private int x, y, modifiers, clickCount;


	public MouseEditEvent ()
	{
		super ();
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ",(" + x + ',' + y
			+ "),modifiers=" + Integer.toHexString (modifiers)
			+ ",clickCount=" + clickCount;
	}


	public final MouseEditEvent set (Object component, int eventType,
									 int modifiers,
									 int x, int y, int clickCount)
	{
		if (isSet)
		{
			throw new IllegalStateException
				("MouseEditEvent has already been set.");
		}
		isSet = true;
		this.component = component;
		this.eventType = eventType;
		this.modifiers = modifiers;
		this.x = x;
		this.y = y;
		this.clickCount = clickCount;
		return this;
	}


	public final MouseEditEvent set (MouseEvent event)
	{
		return set (event.getComponent (), event.getID (),
					event.getModifiers () | event.getModifiersEx (),
					event.getX (), event.getY (), event.getClickCount ());
	}


	public int getEventType ()
	{
		return eventType;
	}


	public final int getModifiers ()
	{
		return modifiers;
	}


	public final int getX ()
	{
		return x;
	}


	public final int getY ()
	{
		return y;
	}


	public final int getClickCount ()
	{
		return clickCount;
	}


	public final boolean isAltDown ()
	{
		return (modifiers & MouseEvent.ALT_MASK) != 0;
	}


	public final boolean isControlDown ()
	{
		return (modifiers & MouseEvent.CTRL_MASK) != 0;
	}


	public final boolean isMetaDown ()
	{
		return (modifiers & MouseEvent.META_MASK) != 0;
	}


	public final boolean isShiftDown ()
	{
		return (modifiers & MouseEvent.SHIFT_MASK) != 0;
	}
}
