
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

/**
 * An <code>InputEditEvent</code> is a <i>consumable</i> edit event.
 * 
 * @author Ole Kniemeyer
 */
public class InputEditEvent extends EditEvent
{
	private boolean consumed = false;


	/**
	 * Tests whether this event has been consumed. This method returns
	 * <code>true<code> iff {@link #consume()} has been invoked
	 * previously.
	 * 
	 * @return <code>true</code> iff this event has been consumed
	 */
	public final boolean isConsumed ()
	{
		return consumed;
	}


	/**
	 * Marks this event as consumed. Subsequent invocations
	 * of {@link #isConsumed()} will return <code>true</code>.
	 */
	public final void consume ()
	{
		consumed = true;
	}

}
