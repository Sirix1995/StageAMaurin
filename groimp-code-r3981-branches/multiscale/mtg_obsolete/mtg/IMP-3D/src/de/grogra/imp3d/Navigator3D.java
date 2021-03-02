
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

package de.grogra.imp3d;

import java.util.EventObject;
import java.awt.event.*;
import de.grogra.imp.*;

public class Navigator3D implements de.grogra.util.DisposableEventListener
{
	protected final ViewEventHandler handler;
	protected int lastX, lastY;
	protected final boolean wheel;


	public Navigator3D (ViewEventHandler h, java.util.EventObject e)
	{
		handler = h;
		lastX = ((MouseEvent) e).getX ();
		lastY = ((MouseEvent) e).getY ();
		wheel = e instanceof MouseWheelEvent;
	}


	public void dispose ()
	{
	}


	public void eventOccured (EventObject e)
	{
		if (!(e instanceof MouseEvent))
		{
			return;
		}
		MouseEvent me = (MouseEvent) e;
		me.consume ();
		if (wheel)
		{
			if (!(me instanceof MouseWheelEvent))
			{
				handler.disposeNavigator (me);
				return;
			}
			((View3D) handler.getView ()).dollyOrZoom (0,
							   ((MouseWheelEvent) me).getWheelRotation () << 5);
			return;
		}
		switch (me.getID ())
		{
			case MouseEvent.MOUSE_RELEASED:
			case MouseEvent.MOUSE_MOVED:
				handler.disposeNavigator (null);
				return;
			case MouseEvent.MOUSE_DRAGGED:
				int dx = me.getX () - lastX, dy = me.getY () - lastY;
				if ((dx == 0) && (dy == 0))
				{
					return;
				}
				lastX += dx;
				lastY += dy;
				if (me.isMetaDown ())
				{
					((View3D) handler.getView ()).dollyOrZoom (dx, dy);
				}
				else if (me.isAltDown ())
				{
					((View3D) handler.getView ()).move (dx, dy);
				}
				else
				{
					((View3D) handler.getView ()).rotate (dx, dy);
				}
				break;
		}
	}

}
