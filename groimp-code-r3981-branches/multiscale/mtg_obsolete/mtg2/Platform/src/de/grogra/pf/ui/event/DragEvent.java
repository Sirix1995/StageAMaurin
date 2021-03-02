
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
 * A <code>DragEvent</code> represents a mouse-drag event.
 * It contains state information about the dragging process.
 * 
 * @author Ole Kniemeyer
 */
public class DragEvent extends MouseEditEvent
{
	public static final int DRAGGING_STARTED = 0;
	public static final int DRAGGING_CONTINUED = 1;
	public static final int DRAGGING_FINISHED = 2;

	private int dragState, deltaX, deltaY;


	public DragEvent setDragData (int dragState, int deltaX, int deltaY)
	{
		this.dragState = dragState;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		return this;
	}


	/**
	 * Returns <code>true</code> iff this event is the first drag event
	 * in a sequence of drag events.
	 * 
	 * @return <code>true</code> iff draggging has just been started
	 */
	public final boolean draggingStarted ()
	{
		return dragState == DRAGGING_STARTED;
	}


	/**
	 * Returns <code>true</code> iff this event is an intermediate drag event
	 * in a sequence of drag events.
	 * 
	 * @return <code>true</code> iff dragging has been continued and not yet finished
	 */
	public final boolean draggingContinued ()
	{
		return dragState == DRAGGING_CONTINUED;
	}


	/**
	 * Returns <code>true</code> iff this event is the last drag event
	 * in a sequence of drag events.
	 * 
	 * @return <code>true</code> iff dragging has been finished
	 */
	public final boolean draggingFinished ()
	{
		return dragState == DRAGGING_FINISHED;
	}


	@Override
	public final int getEventType ()
	{
		return dragState;
	}


	/**
	 * Returns the x-difference of the mouse pointer in pixels between
	 * the previous drag event and this drag event.
	 * 
	 * @return the x-movement of the mouse
	 */
	public final int getDeltaX ()
	{
		return deltaX;
	}


	/**
	 * Returns the y-difference of the mouse pointer in pixels between
	 * the previous drag event and this drag event.
	 * 
	 * @return the y-movement of the mouse
	 */
	public final int getDeltaY ()
	{
		return deltaY;
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ",dragState=" + dragState
			+ ",delta=(" + deltaX + ',' + deltaY + ')';
	}

}
